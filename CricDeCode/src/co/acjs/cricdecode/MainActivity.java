package co.acjs.cricdecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import co.acjs.cricdecode.util.IabHelper;
import co.acjs.cricdecode.util.IabHelper.OnIabPurchaseFinishedListener;
import co.acjs.cricdecode.util.IabResult;
import co.acjs.cricdecode.util.Inventory;
import co.acjs.cricdecode.util.Purchase;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.stackmob.android.sdk.common.StackMobAndroid;


public class MainActivity extends SherlockFragmentActivity{
	// Declare Variables
	DrawerLayout									mDrawerLayout;
	ListView										mDrawerList;
	ActionBarDrawerToggle							mDrawerToggle;
	Spinner											spinner;
	MenuListAdapter									mMenuAdapter;
	String[]										title;
	int												currentFragment, preFragment;
	Menu											current_menu;
	static SQLiteDatabase							dbHandle;
	public static Context							main_context;
	TextView										tx;
	ProgressDialog									progressDialog;
	private static IabHelper						mHelper;
	private static OnIabPurchaseFinishedListener	mPurchaseFinishedListener;
	static String									bat, bowl, field, match_lvl, team_a, team_b, venue, date;
	// Filter Variables
	ArrayList<String>								my_team_list, my_team_list_selected, opponent_list, opponent_list_selected, venue_list, venue_list_selected, overs_list, overs_list_selected, innings_list, innings_list_selected, level_list, level_list_selected, duration_list, duration_list_selected, first_list, first_list_selected, season_list, season_list_selected, result_list, result_list_selected, batting_no_list, batting_no_list_selected, how_out_list, how_out_list_selected;
	String											myteam_whereClause				= "", opponent_whereClause = "", venue_whereClause = "", overs_whereClause = "", innings_whereClause = "", level_whereClause = "", duration_whereClause = "", first_whereClause = "", season_whereClause = "", result_whereClause = "", batting_no_whereClause = "", how_out_whereClause = "";
	// Dialog state
	boolean											filter_showing;
	MultiSelectSpinner								batting_no_spinner, how_out_spinner, season_spinner, my_team_spinner, opponent_spinner, venue_spinner, result_spinner, level_spinner, overs_spinner, innings_spinner, duration_spinner, first_spinner;
	ArrayList<Integer>								batting_no_val, how_out_val, season_val, my_team_val, opponent_val, venue_val, result_val, level_val, overs_val, innings_val, duration_val, first_val;
	FilterDialog									filter_dialog;
	static ContentProviderClient					client;
	// Declare Constants
	static final int								NO_FRAGMENT						= -1, SIGNIN_FRAGMENT = 9, PROFILE_FRAGMENT = 0, CAREER_FRAGMENT = 1, ANALYSIS_FRAGMENT = 2, DIARY_MATCHES_FRAGMENT = 3, ONGOING_MATCHES_FRAGMENT = 4, PURCHASE_FRAGMENT = 5, SUPPORT = 6, MATCH_CREATION_FRAGMENT = 7, PERFORMANCE_FRAGMENT_EDIT = 8, PERFORMANCE_FRAGMENT_VIEW = 9, PROFILE_EDIT = 10;
	static int										root_fragment					= CAREER_FRAGMENT;
	// Request Codes
	static final int								PURCHASE_REMOVE_ADS				= 398457, PURCHASE_INFI = 34809, PURCHASE_INFI_SYNC = 37867, PURCHASE_SYNC = 3561;
	// InAppBillingItems
	static final String								SKU_REMOVE_ADS					= "ad_removal", SKU_SUB_INFI = "sub_infi", SKU_SUB_INFI_SYNC = "sub_infi_sync", SKU_SUB_SYNC = "sub_sync";
	static String									pur_remove_adds_title			= "", pur_remove_adds_descr = "", pur_remove_adds_price = "", pur_infi_price = "", pur_infi_title = "", pur_infi_descr = "", pur_infi_sync_price = "", pur_infi_sync_title = "", pur_infi_sync_descr = "";
	// FB Constants
	private static final List<String>				PERMISSIONS						= Arrays.asList("publish_actions");
	private static final String						PENDING_PUBLISH_KEY				= "pendingPublishReauthorization";
	private boolean									pendingPublishReauthorization	= false;
	private UiLifecycleHelper						uiHelper;
	private Session.StatusCallback					callback;
	protected static String							id;
	static{
	}

	// FB Share function
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset){
		for(String string: subset){
			if(!superset.contains(string)){ return false; }
		}
		return true;
	}

	String getMD5(){
		try{
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(AccessSharedPrefs.mPrefs.getString("id", "").getBytes());
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < array.length; ++i){
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		}catch(java.security.NoSuchAlgorithmException e){}
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		StackMobAndroid.init(getApplicationContext(), 1, decrypt("5g28><6hi=2", "26j6jff", "29>5h;<=8>", "f8=f=if5", "6103927458", 5));
		AccessSharedPrefs.mPrefs = getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		AccessSharedPrefs.setString(this, "isSignedIn", "Yes");
		main_context = this;
		mHelper = new IabHelper(this, decrypt("C,sCZBgBPDBE,p8OF0U[RLcYTHjI:iFsKulbsFD,Gs4Q2L1qh,BfWJRSnY9OBCY1mUI5UQPe0Y:wsNJ4", "uDL4NVye4e[B8oJFm40g2R45Jf3JuehFp4CH8K3lZBkRJBvef9dmJ", "D7FN4KtfNwB4iYV3G0e6Rn98JcPEsC9[qwE6F:BJB{Uw7g9O36NV53heRgz3JL:NlxlKdqJixRhBoVP6CBJlUHhgHtDPEzM7PXlehTBT8EJ:xLL8RHrgBC", "HkE5Pd47RYKBuSCFBHw22OvZdpPmzP8CZsEfS9WKqhbPYgQNk4qlWy0ouq[f{rco2gkuGoxi[pKkGSfDslUjKtfkPRcje2{:Lrd3cHztXv0BN2q:YHxu7MI:gx4O7whSqCs1jOHg[0n4W", "5143079682", 1));
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener(){
			@Override
			public void onIabSetupFinished(IabResult result){
				if(!result.isSuccess()){
					Log.d("Billing", "Problem setting up In-app Billing: " + result);
				}else{
					Log.d("Billing", "In-app Billing set up: " + result);
					ArrayList<String> additionalSkuList = new ArrayList<String>();
					additionalSkuList.add(MainActivity.SKU_REMOVE_ADS);
					additionalSkuList.add(MainActivity.SKU_SUB_INFI);
					additionalSkuList.add(MainActivity.SKU_SUB_INFI_SYNC);
					// additionalSkuList.add(MainActivity.SKU_SUB_SYNC);
					MainActivity.mHelper.queryInventoryAsync(true, additionalSkuList, new IabHelper.QueryInventoryFinishedListener(){
						@Override
						public void onQueryInventoryFinished(IabResult result, Inventory inv){
							Log.w("onQueryInventoryFinished", "Result: " + result + "Inventory: " + inv);
							if(result.isFailure()){
								return;
							}else{
								AccessSharedPrefs.setString(main_context, "pur_remove_adds_title", "Remove Ads");
								AccessSharedPrefs.setString(main_context, "pur_remove_adds_price", inv.getSkuDetails(MainActivity.SKU_REMOVE_ADS).getPrice());
								AccessSharedPrefs.setString(main_context, "pur_remove_adds_descr", inv.getSkuDetails(MainActivity.SKU_REMOVE_ADS).getDescription());
								AccessSharedPrefs.setString(main_context, "pur_infi_title", "Unlimited Matches");
								AccessSharedPrefs.setString(main_context, "pur_infi_price", inv.getSkuDetails(MainActivity.SKU_SUB_INFI).getPrice());
								AccessSharedPrefs.setString(main_context, "pur_infi_descr", inv.getSkuDetails(MainActivity.SKU_SUB_INFI).getDescription());
								AccessSharedPrefs.setString(main_context, "pur_infi_sync_title", "Unlimited Matches & Cloud Backup");
								AccessSharedPrefs.setString(main_context, "pur_infi_sync_price", inv.getSkuDetails(MainActivity.SKU_SUB_INFI_SYNC).getPrice());
								AccessSharedPrefs.setString(main_context, "pur_infi_sync_descr", inv.getSkuDetails(MainActivity.SKU_SUB_INFI_SYNC).getDescription());
								AccessSharedPrefs.setString(main_context, "pur_sync_title", "Cloud Backup");
								AccessSharedPrefs.setString(main_context, "pur_sync_price", "Price");
								AccessSharedPrefs.setString(main_context, "pur_sync_descr", "Keep your data safe with cloud backup.");
								MainActivity.mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener(){
									public void onQueryInventoryFinished(IabResult result, Inventory inventory){
										if(result.isFailure()){}else{
											
											writeToFile("ad_free: "+AccessSharedPrefs.mPrefs.getString("infi_use", "no"));
											if(AccessSharedPrefs.mPrefs.getString("ad_free", "no").equals("yes")){
												if(inventory.hasPurchase(SKU_REMOVE_ADS)){
													Purchase p1 = inventory.getPurchase(SKU_REMOVE_ADS);
													if(p1.getDeveloperPayload().equals(getMD5())){
														if(isOnline(MainActivity.main_context)){
															JSONObject jo = new JSONObject();
															try{
																jo.put("orderId", p1.getOrderId());
																jo.put("Token", p1.getToken());
																jo.put("Sign", p1.getSignature());
																writeToFile("calling chkadremove: ");
																writeToFile("orderid:"+p1.getOrderId());
																writeToFile("token:"+p1.getToken());
																writeToFile("sign:"+p1.getSignature());
																Intent i = new Intent(MainActivity.main_context, CheckPurchasedAdRemovalService.class);
																i.putExtra("json", jo.toString());
																//startService(i);
															}catch(JSONException e){}
														}
													}else{
														AccessSharedPrefs.setString(main_context, "ad_free", "no");
													}
												}else{
													AccessSharedPrefs.setString(main_context, "ad_free", "no");
												}
											}
											writeToFile("infi_use: "+AccessSharedPrefs.mPrefs.getString("infi_use", "no"));
											if(AccessSharedPrefs.mPrefs.getString("infi_use", "no").equals("yes")){
												if(inventory.hasPurchase(SKU_SUB_INFI)){
													Purchase p1 = inventory.getPurchase(SKU_SUB_INFI);
													if(p1.getDeveloperPayload().equals(getMD5())){
														if(isOnline(MainActivity.main_context)){
															JSONObject jo = new JSONObject();
															try{
																jo.put("orderId", p1.getOrderId());
																jo.put("Token", p1.getToken());
																jo.put("Sign", p1.getSignature());
																writeToFile("calling chkinfiuse: ");
																writeToFile("orderid:"+p1.getOrderId());
																writeToFile("token:"+p1.getToken());
																writeToFile("sign:"+p1.getSignature());
																Intent i = new Intent(MainActivity.main_context, CheckPurchaseInfiService.class);
																i.putExtra("json", jo.toString());
																writeToFile("json: "+jo.toString());
																//startService(i);
															}catch(JSONException e){}
														}
													}else{
														AccessSharedPrefs.setString(main_context, "infi_use", "no");
													}
												}else{
													AccessSharedPrefs.setString(main_context, "infi_use", "no");
												}
											}
											writeToFile("infi_sync: "+AccessSharedPrefs.mPrefs.getString("infi_sync", "no"));
											if(AccessSharedPrefs.mPrefs.getString("infi_sync", "no").equals("yes")){
												if(inventory.hasPurchase(SKU_SUB_INFI_SYNC)){
													Purchase p1 = inventory.getPurchase(SKU_SUB_INFI_SYNC);
													if(p1.getDeveloperPayload().equals(getMD5())){
														if(isOnline(MainActivity.main_context)){
															JSONObject jo = new JSONObject();
															try{
																jo.put("orderId", p1.getOrderId());
																jo.put("Token", p1.getToken());
																jo.put("Sign", p1.getSignature());
																writeToFile("calling chkinfisync: ");
																writeToFile("orderid:"+p1.getOrderId());
																writeToFile("token:"+p1.getToken());
																writeToFile("sign:"+p1.getSignature());
																Intent i = new Intent(MainActivity.main_context, CheckPurchaseInfiSync.class);
																i.putExtra("json", jo.toString());
																//startService(i);
															}catch(JSONException e){}
														}
													}else{
														AccessSharedPrefs.setString(main_context, "infi_sync", "no");
													}
												}else{
													AccessSharedPrefs.setString(main_context, "infi_sync", "no");
												}
											}
											if(AccessSharedPrefs.mPrefs.getString("sync", "no").equals("yes")){
												if(inventory.hasPurchase(SKU_SUB_SYNC)){
													Purchase p1 = inventory.getPurchase(SKU_SUB_SYNC);
													if(p1.getDeveloperPayload().equals(getMD5())){
														if(isOnline(MainActivity.main_context)){
															JSONObject jo = new JSONObject();
															try{
																jo.put("orderId", p1.getOrderId());
																jo.put("Token", p1.getToken());
																jo.put("Sign", p1.getSignature());
																Intent i = new Intent(MainActivity.main_context, CheckPurchaseSync.class);
																i.putExtra("json", jo.toString());
																startService(i);
															}catch(JSONException e){}
														}
													}else{
														AccessSharedPrefs.setString(main_context, "sync", "no");
													}
												}else{
													AccessSharedPrefs.setString(main_context, "sync", "no");
												}
											}
										}
									}
								});
							}
						}
					});
				}
			}
		});
		// FB Thing
		//TODO
	writeToFile("Calling Purchase:");
		JSONObject jo = new JSONObject();
		try{
			jo.put("orderId", "12999763169054705758.1398666396207159");
			jo.put("Token", "ivqfvospmfqgjeyarkmnmazj.AO-J1Oyyg1GOIG78Vhk7Q9GfTGdILeGUnRzAGqYLSHbgHQE07HyoCKQAgKr00Q424s6fQHnoh-1Uv93T_aSezBp1cRIVNZ2viyZFeQyqQERuMJM59wWZjbA");
			jo.put("Sign", "R+ngqaVMPriFGo+b5G1g/O8ZayVeLefuTws/Yn+654gF20SHNvMjm8w2oVXyascfnmSRVoD9rh0X/3XvVAPEXuAy4K7Tr+gAnCPoM8u3leqLz+cQzXWRtQRMWUYinvEpdk26EMNK0n4PY7CxvoGFOciOqkIqaC80+RUVYraENFoHGZxLCriGGgO1QFYIY48NlXABLBbyAHgjkA4LKlCCtgswiR4K9jXIirFZDxpDXI9tl5pdjmeUWquUENo2Zh/dnmoV8DHcp6f5jS+mcpPzIvVSQVn09GlyQfNjk27vSkgDunmpG3GhfDLuKTS2f2fcwO72aLotS/VcvnaHpaPwGg==");
		}catch(JSONException e){}
		writeToFile("Calling Purchase:"+jo.toString());
		Intent intent = new Intent(main_context, PurchasedInfiSyncService.class);
		AccessSharedPrefs.setString(main_context, "PurchaseInfiSyncServiceCalled", CDCAppClass.NEEDS_TO_BE_CALLED);
		AccessSharedPrefs.setString(main_context, "pur_infi_sync_data", jo.toString());
		AccessSharedPrefs.setString(main_context, "infi_sync", "yes");
		main_context.startService(intent);
		
		callback = new Session.StatusCallback(){
			@Override
			public void call(Session session, SessionState state, Exception exception){
				onSessionStateChange(session, state, exception);
			}
		};
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_main);
		// InAppBilling Stuff
		mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener(){
			public void onIabPurchaseFinished(IabResult result, Purchase purchase){
				Log.w("MainActivity", "Purchase Test: on purchase listener 1");
				if(result.isFailure()){
					Log.w("onPurchase", "Error purchasing: " + result);
					return;
				}else if((purchase.getPurchaseState() == 0) & (getMD5().equals(purchase.getDeveloperPayload()))){
					JSONObject jo = new JSONObject();
					try{
						jo.put("orderId", purchase.getOrderId());
						jo.put("Token", purchase.getToken());
						jo.put("Sign", purchase.getSignature());
					}catch(JSONException e){}
					Intent intent = null;
					if(purchase.getSku().equals(SKU_REMOVE_ADS)){
						Log.w("MainActivity", "Purchase Test: on purchase remove ads");
						writeToFile("On Purchase Listener remove ads ");
						AccessSharedPrefs.setString(main_context, "PurchaseAdRemovalServiceCalled", CDCAppClass.NEEDS_TO_BE_CALLED);
						AccessSharedPrefs.setString(main_context, "pur_ad_data", jo.toString());
						AccessSharedPrefs.setString(main_context, "ad_free", "yes");
						intent = new Intent(main_context, PurchasedAdRemovalService.class);
						findViewById(R.id.adView).setVisibility(View.GONE);
					}else if(purchase.getSku().equals(SKU_SUB_INFI)){
						writeToFile("On Purchase Listener infi ");
						AccessSharedPrefs.setString(main_context, "PurchaseInfiServiceCalled", CDCAppClass.NEEDS_TO_BE_CALLED);
						AccessSharedPrefs.setString(main_context, "pur_infi_data", jo.toString());
						AccessSharedPrefs.setString(main_context, "infi_use", "yes");
						intent = new Intent(main_context, PurchasedInfiService.class);
					}else if(purchase.getSku().equals(SKU_SUB_INFI_SYNC)){
						writeToFile("On Purchase Listener infi sync");
						AccessSharedPrefs.setString(main_context, "PurchaseInfiSyncServiceCalled", CDCAppClass.NEEDS_TO_BE_CALLED);
						AccessSharedPrefs.setString(main_context, "pur_infi_sync_data", jo.toString());
						AccessSharedPrefs.setString(main_context, "infi_sync", "yes");
						intent = new Intent(main_context, PurchasedInfiSyncService.class);
					}else if(purchase.getSku().equals(SKU_SUB_SYNC)){
						AccessSharedPrefs.setString(main_context, "PurchaseSyncServiceCalled", CDCAppClass.NEEDS_TO_BE_CALLED);
						AccessSharedPrefs.setString(main_context, "pur_sync_data", jo.toString());
						AccessSharedPrefs.setString(main_context, "sync", "yes");
						intent = new Intent(main_context, PurchasedSyncService.class);
					}
					Log.w("MainActivity", "Purchase Test: on purchase listener starting service");
					main_context.startService(intent);
				}
			}
		};
		// Action Bar Customization
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.action_bar, null);
		actionBar.setCustomView(view);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		AccessSharedPrefs.mPrefs.getInt("height", 0);
		AccessSharedPrefs.mPrefs.getInt("width", 0);
		if(AccessSharedPrefs.mPrefs.getInt("height", 0) == 0){
			AccessSharedPrefs.setInt(main_context, "height", AccessSharedPrefs.mPrefs.getInt("height", displaymetrics.heightPixels));
		}
		if(AccessSharedPrefs.mPrefs.getInt("width", 0) == 0){
			AccessSharedPrefs.setInt(main_context, "width", AccessSharedPrefs.mPrefs.getInt("width", displaymetrics.widthPixels));
		}
		Log.w("Width and Height", "Display: " + displaymetrics.heightPixels + " " + displaymetrics.widthPixels);
		client = getContentResolver().acquireContentProviderClient(CricDeCodeContentProvider.AUTHORITY);
		dbHandle = ((CricDeCodeContentProvider)client.getLocalContentProvider()).getDbHelper().getReadableDatabase();
		make_directory();
		// Spinner
		spinner = (Spinner)findViewById(R.id.inning_no);
		// Generate title
		title = getResources().getStringArray(R.array.drawer_list_item);
		// Locate DrawerLayout in drawer_main.xml
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		// Locate ListView in drawer_main.xml
		mDrawerList = (ListView)findViewById(R.id.left_drawer);
		// Set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// Pass results to MenuListAdapter Class
		mMenuAdapter = new MenuListAdapter(this, title);
		// Set the MenuListAdapter to the ListView
		mDrawerList.setAdapter(mMenuAdapter);
		// Capture button clicks on side menu
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){
			ImageView	icon	= (ImageView)findViewById(R.id.icon);

			public void onDrawerClosed(View view){
				super.onDrawerClosed(view);
				icon.setPadding(0, 0, 0, 0);
			}

			public void onDrawerOpened(View drawerView){
				super.onDrawerOpened(drawerView);
				/*
				 * AccessSharedPrefs.mPrefs = getSharedPreferences("CricDeCode",
				 * Context.MODE_PRIVATE); ((TextView)
				 * mDrawerList.getChildAt(ONGOING_MATCHES_FRAGMENT)
				 * .findViewById(R.id.title))
				 * .setText(getResources().getStringArray(
				 * R.array.drawer_list_item)[ONGOING_MATCHES_FRAGMENT] + " (" +
				 * AccessSharedPrefs.mPrefs.getInt( "ongoingMatches", 0) + ")");
				 * ((TextView) mDrawerList.getChildAt(DIARY_MATCHES_FRAGMENT)
				 * .findViewById(R.id.title))
				 * .setText(getResources().getStringArray(
				 * R.array.drawer_list_item)[DIARY_MATCHES_FRAGMENT] + " (" +
				 * AccessSharedPrefs.mPrefs.getInt( "diaryMatches", 0) + ")");
				 */
				icon.setPadding(-5, 0, 0, 0);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		tx = (TextView)findViewById(R.id.page_name);
		if(!AccessSharedPrefs.mPrefs.getBoolean("matches_exist", false)){
			root_fragment = ONGOING_MATCHES_FRAGMENT;
		}else{
			root_fragment = CAREER_FRAGMENT;
		}
		if(savedInstanceState == null){
			Log.d("Debug", "Saved is null");
			currentFragment = root_fragment;
			preFragment = NO_FRAGMENT;
			ProfileFragment.currentProfileFragment = ProfileFragment.PROFILE_VIEW_FRAGMENT;
			selectItem(currentFragment, true);
			setPageName(currentFragment);
		}else{
			filter_showing = savedInstanceState.getBoolean("filter_showing");
			batting_no_val = savedInstanceState.getIntegerArrayList("batting_no_val");
			how_out_val = savedInstanceState.getIntegerArrayList("how_out_val");
			season_val = savedInstanceState.getIntegerArrayList("season_val");
			my_team_val = savedInstanceState.getIntegerArrayList("my_team_val");
			opponent_val = savedInstanceState.getIntegerArrayList("opponent_val");
			venue_val = savedInstanceState.getIntegerArrayList("venue_val");
			result_val = savedInstanceState.getIntegerArrayList("result_val");
			level_val = savedInstanceState.getIntegerArrayList("level_val");
			overs_val = savedInstanceState.getIntegerArrayList("overs_val");
			innings_val = savedInstanceState.getIntegerArrayList("innings_val");
			duration_val = savedInstanceState.getIntegerArrayList("duration_val");
			first_val = savedInstanceState.getIntegerArrayList("first_val");
			// FB Post Thing
			pendingPublishReauthorization = savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
			currentFragment = savedInstanceState.getInt("currentFragment");
			preFragment = savedInstanceState.getInt("preFragment");
			setPageName(currentFragment);
			switch(currentFragment){
				case PROFILE_FRAGMENT:
					spinner.setVisibility(View.GONE);
					ProfileFragment.profileFragment = (ProfileFragment)getSupportFragmentManager().getFragment(savedInstanceState, "currentFragmentInstance");
					tx.setVisibility(View.VISIBLE);
					break;
				case CAREER_FRAGMENT:
					spinner.setVisibility(View.GONE);
					CareerFragment.careerFragment = (CareerFragment)getSupportFragmentManager().getFragment(savedInstanceState, "currentFragmentInstance");
					tx.setVisibility(View.VISIBLE);
					tx.setText(R.string.career);
					break;
				case ANALYSIS_FRAGMENT:
					spinner.setVisibility(View.GONE);
					AnalysisFragment.analysisFragment = (AnalysisFragment)getSupportFragmentManager().getFragment(savedInstanceState, "currentFragmentInstance");
					tx.setVisibility(View.VISIBLE);
					tx.setText(R.string.analysis);
					break;
				case MATCH_CREATION_FRAGMENT:
					spinner.setVisibility(View.GONE);
					MatchCreationFragment.matchCreationFragment = (MatchCreationFragment)getSupportFragmentManager().getFragment(savedInstanceState, "currentFragmentInstance");
					tx.setVisibility(View.VISIBLE);
					tx.setText(R.string.create_new_match);
					break;
				case DIARY_MATCHES_FRAGMENT:
					spinner.setVisibility(View.GONE);
					DiaryMatchesFragment.diaryMatchesFragment = (DiaryMatchesFragment)getSupportFragmentManager().getFragment(savedInstanceState, "currentFragmentInstance");
					tx.setVisibility(View.VISIBLE);
					tx.setText(R.string.match_diary);
					break;
				case ONGOING_MATCHES_FRAGMENT:
					spinner.setVisibility(View.GONE);
					OngoingMatchesFragment.ongoingMatchesFragment = (OngoingMatchesFragment)getSupportFragmentManager().getFragment(savedInstanceState, "currentFragmentInstance");
					tx.setVisibility(View.VISIBLE);
					tx.setText(R.string.ongoing_matches);
					break;
				case PURCHASE_FRAGMENT:
					spinner.setVisibility(View.GONE);
					PurchaseFragment.purchaseFragment = (PurchaseFragment)getSupportFragmentManager().getFragment(savedInstanceState, "currentFragmentInstance");
					tx.setVisibility(View.VISIBLE);
					tx.setText(R.string.buy_schemes);
					break;
				case SUPPORT:
					spinner.setVisibility(View.GONE);
					SupportFragment.supportFragment = (SupportFragment)getSupportFragmentManager().getFragment(savedInstanceState, "currentFragmentInstance");
					tx.setVisibility(View.VISIBLE);
					tx.setText(R.string.support);
					break;
				case PERFORMANCE_FRAGMENT_EDIT:
					spinner.setVisibility(View.VISIBLE);
					tx.setVisibility(View.GONE);
					PerformanceFragmentEdit.performanceFragmentEdit = (PerformanceFragmentEdit)getSupportFragmentManager().getFragment(savedInstanceState, "currentFragmentInstance");
					break;
				case PERFORMANCE_FRAGMENT_VIEW:
					spinner.setVisibility(View.VISIBLE);
					tx.setVisibility(View.GONE);
					PerformanceFragmentView.performanceFragmentView = (PerformanceFragmentView)getSupportFragmentManager().getFragment(savedInstanceState, "currentFragmentInstance");
					break;
				default:
					break;
			}
			Log.d("Debug", "currentFragment " + currentFragment);
		}
		if(AccessSharedPrefs.mPrefs.getInt("FirstTym", 0) == 0){
			mDrawerLayout.openDrawer(mDrawerList);
			AccessSharedPrefs.setInt(this, "FirstTym", 1);
		}
		MainActivity mainActivity = this;
		if(savedInstanceState == null){
			fetchFromDb();
		}else{
			batting_no_list = savedInstanceState.getStringArrayList("batting_no_list");
			batting_no_list_selected = savedInstanceState.getStringArrayList("batting_no_list_selected");
			how_out_list = savedInstanceState.getStringArrayList("how_out_list");
			how_out_list_selected = savedInstanceState.getStringArrayList("how_out_list_selected");
			mainActivity.season_list = savedInstanceState.getStringArrayList("season_list");
			mainActivity.season_list_selected = savedInstanceState.getStringArrayList("season_list_selected");
			mainActivity.my_team_list = savedInstanceState.getStringArrayList("my_team_list");
			mainActivity.my_team_list_selected = savedInstanceState.getStringArrayList("my_team_list_selected");
			mainActivity.opponent_list = savedInstanceState.getStringArrayList("opponent_list");
			mainActivity.opponent_list_selected = savedInstanceState.getStringArrayList("opponent_list_selected");
			mainActivity.venue_list = savedInstanceState.getStringArrayList("venue_list");
			mainActivity.venue_list_selected = savedInstanceState.getStringArrayList("venue_list_selected");
			mainActivity.result_list = savedInstanceState.getStringArrayList("result_list");
			mainActivity.result_list_selected = savedInstanceState.getStringArrayList("result_list_selected");
			mainActivity.level_list = savedInstanceState.getStringArrayList("level_list");
			mainActivity.level_list_selected = savedInstanceState.getStringArrayList("level_list_selected");
			mainActivity.overs_list = savedInstanceState.getStringArrayList("overs_list");
			mainActivity.overs_list_selected = savedInstanceState.getStringArrayList("overs_list_selected");
			mainActivity.innings_list = savedInstanceState.getStringArrayList("innings_list");
			mainActivity.innings_list_selected = savedInstanceState.getStringArrayList("innings_list_selected");
			mainActivity.duration_list = savedInstanceState.getStringArrayList("duration_list");
			mainActivity.duration_list_selected = savedInstanceState.getStringArrayList("duration_list_selected");
			mainActivity.first_list = savedInstanceState.getStringArrayList("first_list");
			mainActivity.first_list_selected = savedInstanceState.getStringArrayList("first_list_selected");
			batting_no_whereClause = savedInstanceState.getString("batting_no_whereClause");
			how_out_whereClause = savedInstanceState.getString("how_out_whereClause");
			mainActivity.myteam_whereClause = savedInstanceState.getString("myteam_whereClause");
			mainActivity.opponent_whereClause = savedInstanceState.getString("opponent_whereClause");
			mainActivity.venue_whereClause = savedInstanceState.getString("venue_whereClause");
			mainActivity.level_whereClause = savedInstanceState.getString("level_whereClause");
			mainActivity.overs_whereClause = savedInstanceState.getString("overs_whereClause");
			mainActivity.innings_whereClause = savedInstanceState.getString("innings_whereClause");
			mainActivity.duration_whereClause = savedInstanceState.getString("duration_whereClause");
			mainActivity.first_whereClause = savedInstanceState.getString("first_whereClause");
			mainActivity.season_whereClause = savedInstanceState.getString("season_whereClause");
			mainActivity.result_whereClause = savedInstanceState.getString("result_whereClause");
			if(filter_showing){
				showFilterDialog(MainActivity.DIARY_MATCHES_FRAGMENT);
			}
		}
		final AdView adView = (AdView)findViewById(R.id.adView);
		(new Thread(){
			public void run(){
				Looper.prepare();
				adView.loadAd(new AdRequest());
			}
		}).start();
		// If AdRemovePerchased do it //ad_free //infi_use //infi_sync
		if(AccessSharedPrefs.mPrefs.getString("ad_free", "no").equals("yes")){
			adView.setVisibility(View.GONE);
		}else{
			adView.setVisibility(View.VISIBLE);
		}
		//TODO 
		//restart_services();
	}

	protected void onSessionStateChange(Session session, SessionState state, Exception exception){
		Log.w("FBShare", "onSessionStateChange");
		if(pendingPublishReauthorization && state.equals(SessionState.OPENED_TOKEN_UPDATED)){
			pendingPublishReauthorization = false;
			// DONE
			publishStory(bat, bowl, field, match_lvl, team_a, team_b, venue, date);
		}
	}

	public void restart_services()
	{
		boolean NotSyncedGCMSync = AccessSharedPrefs.mPrefs.getString("GCMSyncServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED);
		boolean NotSyncedEditProfile = AccessSharedPrefs.mPrefs.getString("ProfileEditServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED);
		boolean NotSyncedMatchHistory = AccessSharedPrefs.mPrefs.getString("MatchHistorySyncServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED);
		boolean NotDeleted = AccessSharedPrefs.mPrefs.getString("DeleteMatchServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED);
		boolean NotAdRemoved = AccessSharedPrefs.mPrefs.getString("PurchaseAdRemovalServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED);
		boolean NotInfied = AccessSharedPrefs.mPrefs.getString("PurchaseInfiServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED);
		boolean NotInfiSynced = AccessSharedPrefs.mPrefs.getString("PurchaseInfiSyncServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED);
		boolean NotSynced = AccessSharedPrefs.mPrefs.getString("PurchaseSyncServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED);
		if(NotSyncedMatchHistory | NotSyncedEditProfile | NotDeleted | NotAdRemoved | NotInfied | NotInfiSynced | NotSyncedGCMSync | NotSynced){
			boolean isConnected = isOnline(main_context);
			if(isConnected){
				Log.w("restart service", "mainactivity");
				writeToFile("restart service mainactivity");
				if(NotSyncedMatchHistory & isConnected){
					Log.w("Starting MatchCreateService", "restart service");
					writeToFile("Starting MatchCreateService restart service");
					startService(new Intent(main_context, MatchHistorySyncService.class));
				}
				if(NotSyncedEditProfile & isConnected){
					Log.w("Starting ProfileEditService", "restart service");
					writeToFile("Starting ProfileEditService restart service");
					startService(new Intent(main_context, ProfileEditService.class));
				}
				if(NotDeleted & isConnected){
					Log.w("Starting DeleteMatch", "restart service");
					writeToFile("Starting DeleteMatch restart service");
					startService(new Intent(main_context, DeleteMatchService.class));
				}
				if(NotAdRemoved & isConnected){
					Log.w("Starting PurchasedAdRemovalService", "restart service");
					writeToFile("Starting PurchasedAdRemovalService restart service");
					startService(new Intent(main_context, PurchasedAdRemovalService.class));
				}
				if(NotInfied & isConnected){
					Log.w("Starting PurchasedInfiService", "restart service");
					writeToFile("Starting PurchasedInfiService restart service");
					startService(new Intent(main_context, PurchasedInfiService.class));
				}
				if(NotInfiSynced & isConnected){
					Log.w("Starting PurchasedInfiSyncService", "restart service");
					writeToFile("Starting PurchasedInfiSyncService restart service");
					startService(new Intent(main_context, PurchasedInfiSyncService.class));
				}
				if(NotSyncedGCMSync & isConnected){
					Log.w("Starting PurchasedInfiSyncService", "restart service");
					writeToFile("Starting PurchasedInfiSyncService restart service");
					startService(new Intent(main_context, GCMSyncService.class));
				}
				if(NotSynced & isConnected){
					Log.w("Starting PurchasedSyncService", "restart service");
					writeToFile("Starting PurchasedSyncService restart service");
					startService(new Intent(main_context, PurchasedSyncService.class));
				}
			}else Log.w("no connection", "restart service");
		}
	}
	@Override
	protected void onStop(){
		super.onStop();
		// Google Analytics Stop
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		uiHelper.onDestroy();
		if(mHelper != null) mHelper.dispose();
		mHelper = null;
	}

	@Override
	protected void onPause(){
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	protected void onResume(){
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		menu.clear();
		setPageName(currentFragment);
		switch(currentFragment){
			case PROFILE_FRAGMENT:
				if(ProfileFragment.currentProfileFragment == ProfileFragment.PROFILE_VIEW_FRAGMENT){
					// menu.add(Menu.NONE, R.string.edit_profile, Menu.NONE,
					// R.string.edit_profile);
					// menu.findItem(R.string.edit_profile).setShowAsAction(
					// MenuItem.SHOW_AS_ACTION_IF_ROOM);
					ImageButton button = (ImageButton)getSupportActionBar().getCustomView().findViewById(R.id.button_menu);
					button.setImageDrawable(getResources().getDrawable(R.drawable.edit));
					button.setContentDescription(getResources().getString(R.string.edit_profile));
					// button.setText(getResources().getString(R.string.edit_profile));
					((RelativeLayout)findViewById(R.id.rl_button)).setVisibility(View.VISIBLE);
				}else{
					/*
					 * menu.add(Menu.NONE, R.string.save_profile, Menu.NONE,
					 * R.string.save_profile);
					 * menu.findItem(R.string.save_profile).setShowAsAction(
					 * MenuItem.SHOW_AS_ACTION_IF_ROOM);
					 */
					ImageButton button = (ImageButton)getSupportActionBar().getCustomView().findViewById(R.id.button_menu);
					button.setImageDrawable(getResources().getDrawable(R.drawable.save));
					button.setContentDescription(getResources().getString(R.string.save_profile));
					// button.setText(getResources().getString(R.string.save_profile));
					((RelativeLayout)findViewById(R.id.rl_button)).setVisibility(View.VISIBLE);
				}
				break;
			case MATCH_CREATION_FRAGMENT:
				/*
				 * menu.add(Menu.NONE, R.string.create_match, Menu.NONE,
				 * R.string.create_match);
				 * menu.findItem(R.string.create_match).setShowAsAction(
				 * MenuItem.SHOW_AS_ACTION_ALWAYS);
				 */
				ImageButton button = (ImageButton)getSupportActionBar().getCustomView().findViewById(R.id.button_menu);
				button.setImageDrawable(getResources().getDrawable(R.drawable.save));
				button.setContentDescription(getResources().getString(R.string.create_match));
				// button.setText(getResources().getString(R.string.create_match));
				((RelativeLayout)findViewById(R.id.rl_button)).setVisibility(View.VISIBLE);
				break;
			case ONGOING_MATCHES_FRAGMENT:
				/*
				 * menu.add(Menu.NONE, R.string.new_match, Menu.NONE,
				 * R.string.new_match);
				 * menu.findItem(R.string.new_match).setShowAsAction(
				 * MenuItem.SHOW_AS_ACTION_ALWAYS);
				 */
				button = (ImageButton)getSupportActionBar().getCustomView().findViewById(R.id.button_menu);
				button.setImageDrawable(getResources().getDrawable(R.drawable.add));
				button.setContentDescription(getResources().getString(R.string.new_match));
				// button.setText(getResources().getString(R.string.new_match));
				((RelativeLayout)findViewById(R.id.rl_button)).setVisibility(View.VISIBLE);
				break;
			case DIARY_MATCHES_FRAGMENT:
			case CAREER_FRAGMENT:
				/*
				 * menu.add(Menu.NONE, R.string.filter, Menu.NONE, R.string.filter);
				 * menu.findItem(R.string.filter).setShowAsAction(
				 * MenuItem.SHOW_AS_ACTION_IF_ROOM);
				 */
				button = (ImageButton)getSupportActionBar().getCustomView().findViewById(R.id.button_menu);
				button.setImageDrawable(getResources().getDrawable(R.drawable.filter));
				button.setContentDescription(getResources().getString(R.string.filter));
				// button.setText(getResources().getString(R.string.filter));
				((RelativeLayout)findViewById(R.id.rl_button)).setVisibility(View.VISIBLE);
				break;
			case ANALYSIS_FRAGMENT:
				button = (ImageButton)getSupportActionBar().getCustomView().findViewById(R.id.button_menu);
				button.setImageDrawable(getResources().getDrawable(R.drawable.gen_bar));
				button.setContentDescription(getResources().getString(R.string.bar_chart));
				// button.setText(getResources().getString(R.string.filter));
				((RelativeLayout)findViewById(R.id.rl_button)).setVisibility(View.VISIBLE);
				break;
			case PERFORMANCE_FRAGMENT_EDIT:
				/*
				 * menu.add(Menu.NONE, R.string.save_performance, Menu.NONE,
				 * R.string.save_performance);
				 * menu.findItem(R.string.save_performance).setShowAsAction(
				 * MenuItem.SHOW_AS_ACTION_ALWAYS);
				 */
				button = (ImageButton)getSupportActionBar().getCustomView().findViewById(R.id.button_menu);
				button.setContentDescription(getResources().getString(R.string.save_performance));
				button.setImageDrawable(getResources().getDrawable(R.drawable.save));
				// button.setText(getResources().getString(R.string.save_performance));
				((RelativeLayout)findViewById(R.id.rl_button)).setVisibility(View.VISIBLE);
				break;
			default:
				((RelativeLayout)findViewById(R.id.rl_button)).setVisibility(View.GONE);
				break;
		}
		current_menu = menu;
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		Log.d("Debug", "On option item selected");
		switch(item.getItemId()){
			case android.R.id.home:
				if(mDrawerLayout.isDrawerOpen(mDrawerList)){
					mDrawerLayout.closeDrawer(mDrawerList);
				}else{
					mDrawerLayout.openDrawer(mDrawerList);
				}
				break;
			/*
			 * case R.string.new_match: preFragment = currentFragment;
			 * currentFragment = MATCH_CREATION_FRAGMENT;
			 * selectItem(MATCH_CREATION_FRAGMENT, true);
			 * onPrepareOptionsMenu(current_menu); break; case
			 * R.string.edit_profile: ProfileFragment.currentProfileFragment =
			 * ProfileFragment.PROFILE_EDIT_FRAGMENT;
			 * onPrepareOptionsMenu(current_menu);
			 * ProfileFragment.profileFragment.viewFragment(); break; case
			 * R.string.save_profile:
			 * ProfileEditFragment.profileEditFragment.saveEditedProfile();
			 * ProfileFragment.currentProfileFragment =
			 * ProfileFragment.PROFILE_VIEW_FRAGMENT;
			 * onPrepareOptionsMenu(current_menu);
			 * ProfileFragment.profileFragment.viewFragment(); break; case
			 * R.string.create_match:
			 * MatchCreationFragment.matchCreationFragment.insertMatch();
			 * onPrepareOptionsMenu(current_menu); break; case
			 * R.string.save_performance:
			 * PerformanceFragmentEdit.performanceFragmentEdit.insertOrUpdate();
			 * onPrepareOptionsMenu(current_menu); break; case R.string.filter:
			 * showFilterDialog(currentFragment); break;
			 */
			default:
				break;
		}
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			invalidateOptionsMenu();
		}
		return super.onOptionsItemSelected(item);
	}

	// The click listener for ListView in the navigation drawer
	private class DrawerItemClickListener implements ListView.OnItemClickListener{
		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			if(currentFragment != position){
				preFragment = currentFragment;
				currentFragment = position;
				onPrepareOptionsMenu(current_menu);
				selectItem(position, true);
				if(android.os.Build.VERSION.SDK_INT >= 16){
					view.setBackground(getResources().getDrawable(R.layout.drawer_list_selected_selector));
				}else{
					view.setBackgroundDrawable(getResources().getDrawable(R.layout.drawer_list_selected_selector));
				}
			}else{
				// Close drawer
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		}
	}

	public void selectItem(int position, boolean newInstance){
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// Locate Position
		switch(position){
			case PROFILE_FRAGMENT:
				spinner.setVisibility(View.GONE);
				Log.d("Debug", "Select Profile");
				if(newInstance){
					ft.replace(R.id.content_frame, new ProfileFragment());
				}else{
					ft.replace(R.id.content_frame, ProfileFragment.profileFragment);
				}
				break;
			case CAREER_FRAGMENT:
				spinner.setVisibility(View.GONE);
				if(newInstance){
					ft.replace(R.id.content_frame, new CareerFragment());
				}else{
					ft.replace(R.id.content_frame, CareerFragment.careerFragment);
				}
				break;
			case SUPPORT:
				spinner.setVisibility(View.GONE);
				if(newInstance){
					ft.replace(R.id.content_frame, new SupportFragment());
				}else{
					ft.replace(R.id.content_frame, SupportFragment.supportFragment);
				}
				break;
			case ANALYSIS_FRAGMENT:
				spinner.setVisibility(View.GONE);
				if(newInstance){
					ft.replace(R.id.content_frame, new AnalysisFragment());
				}else{
					ft.replace(R.id.content_frame, AnalysisFragment.analysisFragment);
				}
				break;
			case ONGOING_MATCHES_FRAGMENT:
				spinner.setVisibility(View.GONE);
				if(newInstance){
					ft.replace(R.id.content_frame, new OngoingMatchesFragment());
				}else{
					ft.replace(R.id.content_frame, OngoingMatchesFragment.ongoingMatchesFragment);
				}
				break;
			case DIARY_MATCHES_FRAGMENT:
				spinner.setVisibility(View.GONE);
				if(newInstance){
					ft.replace(R.id.content_frame, new DiaryMatchesFragment());
				}else{
					ft.replace(R.id.content_frame, DiaryMatchesFragment.diaryMatchesFragment);
				}
				break;
			case PURCHASE_FRAGMENT:
				spinner.setVisibility(View.GONE);
				Log.d("Debug", "Select Purchase");
				if(newInstance){
					ft.replace(R.id.content_frame, new PurchaseFragment());
				}else{
					ft.replace(R.id.content_frame, PurchaseFragment.purchaseFragment);
				}
				break;
			case MATCH_CREATION_FRAGMENT:
				spinner.setVisibility(View.GONE);
				if(newInstance){
					ft.replace(R.id.content_frame, new MatchCreationFragment());
				}else{
					ft.replace(R.id.content_frame, MatchCreationFragment.matchCreationFragment);
				}
				break;
			case PERFORMANCE_FRAGMENT_EDIT:
				spinner.setVisibility(View.VISIBLE);
				if(newInstance){
					ft.replace(R.id.content_frame, new PerformanceFragmentEdit());
				}else{
					ft.replace(R.id.content_frame, PerformanceFragmentEdit.performanceFragmentEdit);
				}
				break;
			case PERFORMANCE_FRAGMENT_VIEW:
				spinner.setVisibility(View.VISIBLE);
				if(newInstance){
					ft.replace(R.id.content_frame, new PerformanceFragmentView());
				}else{
					ft.replace(R.id.content_frame, PerformanceFragmentView.performanceFragmentView);
				}
				break;
			default:
				break;
		}
		ft.commit();
		mDrawerList.setItemChecked(position, true);
		// Close drawer
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
		MainActivity mainActivity = this;
		outState.putStringArrayList("batting_no_list", (ArrayList<String>)batting_no_list);
		outState.putStringArrayList("how_out_list", (ArrayList<String>)how_out_list);
		outState.putStringArrayList("season_list", (ArrayList<String>)mainActivity.season_list);
		outState.putStringArrayList("result_list", (ArrayList<String>)mainActivity.result_list);
		outState.putStringArrayList("my_team_list", (ArrayList<String>)mainActivity.my_team_list);
		outState.putStringArrayList("opponent_list", (ArrayList<String>)mainActivity.opponent_list);
		outState.putStringArrayList("venue_list", (ArrayList<String>)mainActivity.venue_list);
		outState.putStringArrayList("level_list", (ArrayList<String>)mainActivity.level_list);
		outState.putStringArrayList("overs_list", (ArrayList<String>)mainActivity.overs_list);
		outState.putStringArrayList("innings_list", (ArrayList<String>)mainActivity.innings_list);
		outState.putStringArrayList("duration_list", (ArrayList<String>)mainActivity.duration_list);
		outState.putStringArrayList("first_list", (ArrayList<String>)mainActivity.first_list);
		outState.putStringArrayList("batting_no_list_selected", (ArrayList<String>)batting_no_list_selected);
		outState.putStringArrayList("how_out_list_selected", (ArrayList<String>)how_out_list_selected);
		outState.putStringArrayList("season_list_selected", (ArrayList<String>)mainActivity.season_list_selected);
		outState.putStringArrayList("result_list_selected", (ArrayList<String>)mainActivity.result_list_selected);
		outState.putStringArrayList("my_team_list_selected", (ArrayList<String>)mainActivity.my_team_list_selected);
		outState.putStringArrayList("opponent_list_selected", (ArrayList<String>)mainActivity.opponent_list_selected);
		outState.putStringArrayList("venue_list_selected", (ArrayList<String>)mainActivity.venue_list_selected);
		outState.putStringArrayList("level_list_selected", (ArrayList<String>)mainActivity.level_list_selected);
		outState.putStringArrayList("overs_list_selected", (ArrayList<String>)mainActivity.overs_list_selected);
		outState.putStringArrayList("innings_list_selected", (ArrayList<String>)mainActivity.innings_list_selected);
		outState.putStringArrayList("duration_list_selected", (ArrayList<String>)mainActivity.duration_list_selected);
		outState.putStringArrayList("first_list_selected", (ArrayList<String>)mainActivity.first_list_selected);
		outState.putString("batting_no_whereClause", batting_no_whereClause);
		outState.putString("how_out_whereClause", how_out_whereClause);
		outState.putString("myteam_whereClause", mainActivity.myteam_whereClause);
		outState.putString("opponent_whereClause", mainActivity.opponent_whereClause);
		outState.putString("venue_whereClause", mainActivity.venue_whereClause);
		outState.putString("level_whereClause", mainActivity.level_whereClause);
		outState.putString("overs_whereClause", mainActivity.overs_whereClause);
		outState.putString("innings_whereClause", mainActivity.innings_whereClause);
		outState.putString("duration_whereClause", mainActivity.duration_whereClause);
		outState.putString("first_whereClause", mainActivity.first_whereClause);
		outState.putString("season_whereClause", mainActivity.season_whereClause);
		outState.putString("result_whereClause", mainActivity.result_whereClause);
		Log.d("Debug", "Save currentFragment " + currentFragment);
		outState.putInt("currentFragment", currentFragment);
		outState.putInt("preFragment", preFragment);
		outState.putBoolean("filter_showing", filter_showing);
		Log.d("Debug", "Dialog print" + (filter_dialog == null));
		if(filter_showing){
			outState.putIntegerArrayList("batting_no_val", batting_no_spinner.getSelectedIndicies());
			outState.putIntegerArrayList("how_out_val", how_out_spinner.getSelectedIndicies());
			outState.putIntegerArrayList("season_val", season_spinner.getSelectedIndicies());
			outState.putIntegerArrayList("my_team_val", my_team_spinner.getSelectedIndicies());
			outState.putIntegerArrayList("opponent_val", opponent_spinner.getSelectedIndicies());
			outState.putIntegerArrayList("venue_val", venue_spinner.getSelectedIndicies());
			outState.putIntegerArrayList("result_val", result_spinner.getSelectedIndicies());
			outState.putIntegerArrayList("level_val", level_spinner.getSelectedIndicies());
			outState.putIntegerArrayList("overs_val", overs_spinner.getSelectedIndicies());
			outState.putIntegerArrayList("innings_val", innings_spinner.getSelectedIndicies());
			outState.putIntegerArrayList("duration_val", duration_spinner.getSelectedIndicies());
			outState.putIntegerArrayList("first_val", first_spinner.getSelectedIndicies());
		}
		switch(currentFragment){
			case PROFILE_FRAGMENT:
				getSupportFragmentManager().putFragment(outState, "currentFragmentInstance", ProfileFragment.profileFragment);
				break;
			case CAREER_FRAGMENT:
				getSupportFragmentManager().putFragment(outState, "currentFragmentInstance", CareerFragment.careerFragment);
				break;
			case SUPPORT:
				getSupportFragmentManager().putFragment(outState, "currentFragmentInstance", SupportFragment.supportFragment);
				break;
			case ANALYSIS_FRAGMENT:
				getSupportFragmentManager().putFragment(outState, "currentFragmentInstance", AnalysisFragment.analysisFragment);
				break;
			case ONGOING_MATCHES_FRAGMENT:
				getSupportFragmentManager().putFragment(outState, "currentFragmentInstance", OngoingMatchesFragment.ongoingMatchesFragment);
				break;
			case DIARY_MATCHES_FRAGMENT:
				getSupportFragmentManager().putFragment(outState, "currentFragmentInstance", DiaryMatchesFragment.diaryMatchesFragment);
				break;
			case PURCHASE_FRAGMENT:
				getSupportFragmentManager().putFragment(outState, "currentFragmentInstance", PurchaseFragment.purchaseFragment);
				break;
			case MATCH_CREATION_FRAGMENT:
				getSupportFragmentManager().putFragment(outState, "currentFragmentInstance", MatchCreationFragment.matchCreationFragment);
				break;
			case PERFORMANCE_FRAGMENT_EDIT:
				getSupportFragmentManager().putFragment(outState, "currentFragmentInstance", PerformanceFragmentEdit.performanceFragmentEdit);
				break;
			case PERFORMANCE_FRAGMENT_VIEW:
				getSupportFragmentManager().putFragment(outState, "currentFragmentInstance", PerformanceFragmentView.performanceFragmentView);
				break;
			default:
				break;
		}
		// FB Post thing
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
	}

	@Override
	protected void onStart(){
		super.onStart();
		// Google Analytics Start
		EasyTracker.getInstance(this).activityStart(this);
		int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(isAvailable == ConnectionResult.SUCCESS){}else if(isAvailable == ConnectionResult.SERVICE_MISSING || isAvailable == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED){
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog_play_service_chk);
			dialog.setTitle("Missing Vital App");
			((TextView)dialog.findViewById(R.id.dialog_text)).setText("Google Play Services App is not found/updated on your phone.");
			((Button)dialog.findViewById(R.id.yes)).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms")));
					dialog.dismiss();
					finish();
				}
			});
			((Button)dialog.findViewById(R.id.no)).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					dialog.dismiss();
					finish();
				}
			});
			dialog.setCancelable(false);
			dialog.show();
		}else{
			new AlertDialog.Builder(this).setTitle("Google Play Service Error").setMessage("Unable to start app").setNeutralButton("OK", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
					finish();
				}
			}).show();
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void changeTabLayout(int x){
		switch(x){
			case 0:
				if(android.os.Build.VERSION.SDK_INT < 16){
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.gen_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_selected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bat_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bowl_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.field_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
				}else{
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.gen_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_selected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bat_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bowl_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.field_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
				}
				break;
			case 1:
				if(android.os.Build.VERSION.SDK_INT < 16){
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.gen_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bat_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_selected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bowl_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.field_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
				}else{
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.gen_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bat_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_selected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bowl_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.field_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
				}
				break;
			case 2:
				if(android.os.Build.VERSION.SDK_INT < 16){
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.gen_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bat_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bowl_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_selected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.field_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
				}else{
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.gen_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bat_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bowl_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_selected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.field_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
				}
				break;
			case 3:
				if(android.os.Build.VERSION.SDK_INT < 16){
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.gen_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bat_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bowl_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.field_tab)).setBackgroundDrawable(main_context.getResources().getDrawable(R.drawable.tab_selected_selector));
				}else{
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.gen_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bat_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bowl_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_unselected_selector));
					((RelativeLayout)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.field_tab)).setBackground(main_context.getResources().getDrawable(R.drawable.tab_selected_selector));
				}
				break;
		}
	}

	@SuppressLint("NewApi")
	public void onClick(View view){
		final Dialog dialog;
		final View finalview;
		switch(view.getId()){
		// On Off Button Layout Click Handled Here
			case R.id.bat_toggle_layout:
				PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.bat_toggle.setChecked(!PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.bat_toggle.isChecked());
				break;
			case R.id.bowl_toggle_layout:
				PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.bowl_toggle.setChecked(!PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.bowl_toggle.isChecked());
				break;
			case R.id.gen_tab:
				switch(currentFragment){
					case CAREER_FRAGMENT:
						CareerFragment.careerFragment.mViewPager.setCurrentItem(PerformanceFragmentEdit.GENERAL);
						break;
					case PERFORMANCE_FRAGMENT_EDIT:
						PerformanceFragmentEdit.performanceFragmentEdit.mViewPager.setCurrentItem(PerformanceFragmentEdit.GENERAL);
						break;
					case PERFORMANCE_FRAGMENT_VIEW:
						PerformanceFragmentView.performanceFragmentView.mViewPager.setCurrentItem(PerformanceFragmentEdit.GENERAL);
						break;
					default:
						break;
				}
				break;
			case R.id.bat_tab:
				switch(currentFragment){
					case CAREER_FRAGMENT:
						CareerFragment.careerFragment.mViewPager.setCurrentItem(PerformanceFragmentEdit.BATTING);
						break;
					case PERFORMANCE_FRAGMENT_EDIT:
						PerformanceFragmentEdit.performanceFragmentEdit.mViewPager.setCurrentItem(PerformanceFragmentEdit.BATTING);
						break;
					case PERFORMANCE_FRAGMENT_VIEW:
						PerformanceFragmentView.performanceFragmentView.mViewPager.setCurrentItem(PerformanceFragmentEdit.BATTING);
						break;
					default:
						break;
				}
				break;
			case R.id.bowl_tab:
				switch(currentFragment){
					case CAREER_FRAGMENT:
						CareerFragment.careerFragment.mViewPager.setCurrentItem(PerformanceFragmentEdit.BOWLING);
						break;
					case PERFORMANCE_FRAGMENT_EDIT:
						PerformanceFragmentEdit.performanceFragmentEdit.mViewPager.setCurrentItem(PerformanceFragmentEdit.BOWLING);
						break;
					case PERFORMANCE_FRAGMENT_VIEW:
						PerformanceFragmentView.performanceFragmentView.mViewPager.setCurrentItem(PerformanceFragmentEdit.BOWLING);
						break;
					default:
						break;
				}
				break;
			case R.id.field_tab:
				switch(currentFragment){
					case CAREER_FRAGMENT:
						CareerFragment.careerFragment.mViewPager.setCurrentItem(PerformanceFragmentEdit.FIELDING);
						break;
					case PERFORMANCE_FRAGMENT_EDIT:
						PerformanceFragmentEdit.performanceFragmentEdit.mViewPager.setCurrentItem(PerformanceFragmentEdit.FIELDING);
						break;
					case PERFORMANCE_FRAGMENT_VIEW:
						PerformanceFragmentView.performanceFragmentView.mViewPager.setCurrentItem(PerformanceFragmentEdit.FIELDING);
						break;
					default:
						break;
				}
				break;
			case R.id.btn_fb_got_it:
				AccessSharedPrefs.setString(main_context, "fb_got_it", "yes");
				((RelativeLayout)findViewById(R.id.fb_tip_rl)).setVisibility(View.GONE);
				break;
			case R.id.btn_add_to_hist:
				AccessSharedPrefs.setString(main_context, "hist_got_it", "yes");
				((RelativeLayout)findViewById(R.id.add_to_hist_rl)).setVisibility(View.GONE);
				break;
			case R.id.filter:
				showFilterDialog(currentFragment);
				break;
			case R.id.date_of_birth:
				showDatePicker(R.id.date_of_birth);
				break;
			case R.id.date_of_match_row:
			case R.id.date_of_match:
				showDatePicker(R.id.date_of_match);
				break;
			case R.id.add_match_to_career:
				dialog = new Dialog(this);
				finalview = view;
				dialog.setContentView(R.layout.dialog_confirmation);
				dialog.setTitle("Add to History");
				TextView dialogText = (TextView)dialog.findViewById(R.id.dialog_text);
				dialogText.setText("Are you sure you want to Add this Match to History? You will not be able to edit this match again!");
				Button yes = (Button)dialog.findViewById(R.id.yes);
				yes.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						OngoingMatchesFragment.ongoingMatchesFragment.addToCareer(finalview);
						fetchFromDb();
						dialog.dismiss();
					}
				});
				Button no = (Button)dialog.findViewById(R.id.no);
				no.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						dialog.dismiss();
					}
				});
				dialog.show();
				break;
			case R.id.delete_ongoing_match:
				dialog = new Dialog(this);
				finalview = view;
				dialog.setContentView(R.layout.dialog_confirmation);
				dialog.setTitle("Delete Match");
				dialogText = (TextView)dialog.findViewById(R.id.dialog_text);
				dialogText.setText("Are you sure you want to Delete this Match?");
				yes = (Button)dialog.findViewById(R.id.yes);
				yes.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						OngoingMatchesFragment.ongoingMatchesFragment.deleteMatch(finalview);
						dialog.dismiss();
					}
				});
				no = (Button)dialog.findViewById(R.id.no);
				no.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						dialog.dismiss();
					}
				});
				dialog.show();
				break;
			case R.id.delete_match_diary:
				dialog = new Dialog(this);
				finalview = view;
				dialog.setContentView(R.layout.dialog_confirmation);
				dialog.setTitle("Delete Match");
				dialogText = (TextView)dialog.findViewById(R.id.dialog_text);
				dialogText.setText("Are you sure you want to Delete this Match from your Career?");
				yes = (Button)dialog.findViewById(R.id.yes);
				yes.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						DiaryMatchesFragment.diaryMatchesFragment.deleteMatch(finalview);
						fetchFromDb();
						dialog.dismiss();
					}
				});
				no = (Button)dialog.findViewById(R.id.no);
				no.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						dialog.dismiss();
					}
				});
				dialog.show();
				break;
			case R.id.post_to_fb:
				RelativeLayout vwParentRow = (RelativeLayout)view.getParent().getParent().getParent();
				TextView child = (TextView)vwParentRow.getChildAt(0);
				String str = child.getText().toString();
				child = (TextView)vwParentRow.getChildAt(1);
				String d_str = child.getText().toString();
				Cursor c = MainActivity.dbHandle.rawQuery("select " + MatchDb.KEY_LEVEL + "," + MatchDb.KEY_MY_TEAM + "," + MatchDb.KEY_OPPONENT_TEAM + "," + MatchDb.KEY_VENUE + "," + MatchDb.KEY_MATCH_DATE + " from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_DEVICE_ID + " = '" + d_str + "' and " + MatchDb.KEY_ROWID + " = " + str, null);
				c.moveToFirst();
				match_lvl = c.getString(0);
				team_a = c.getString(1);
				team_b = c.getString(2);
				venue = c.getString(3);
				date = c.getString(4);
				c.close();
				c = MainActivity.dbHandle.rawQuery("select " + PerformanceDb.KEY_BAT_RUNS + "," + PerformanceDb.KEY_BAT_BALLS + "," + PerformanceDb.KEY_BAT_HOW_OUT + "," + PerformanceDb.KEY_BOWL_BALLS + "," + PerformanceDb.KEY_BOWL_RUNS + "," + PerformanceDb.KEY_BOWL_WKTS_LEFT + "," + PerformanceDb.KEY_BOWL_WKTS_RIGHT + "," + PerformanceDb.KEY_FIELD_CIRCLE_CATCH + "," + PerformanceDb.KEY_FIELD_CLOSE_CATCH + "," + PerformanceDb.KEY_FIELD_DEEP_CATCH + "," + PerformanceDb.KEY_FIELD_SLIP_CATCH + "," + PerformanceDb.KEY_FIELD_RO_CIRCLE + "," + PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE + "," + PerformanceDb.KEY_FIELD_RO_DEEP + "," + PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP + "," + PerformanceDb.KEY_FIELD_STUMPINGS + " from " + PerformanceDb.SQLITE_TABLE + " where " + PerformanceDb.KEY_DEVICE_ID + " = '" + d_str + "' and " + PerformanceDb.KEY_MATCHID + " = " + str, null);
				int count = c.getCount();
				bat = "";
				bowl = "";
				field = "";
				int catches = 0,
				ro = 0,
				st = 0;
				c.moveToFirst();
				bat = "1st Innings ";
				if(c.getInt(1) == 0 && c.getString(2).equals("Not Out")){
					bat = bat + " DNB";
				}else{
					bat = bat + c.getInt(0);
					if(c.getString(2).equals("Not Out")){
						bat = bat + "*";
					}
				}
				bowl = "1st Innings ";
				if(c.getInt(3) == 0){
					bowl = bowl + "DNB";
				}else{
					bowl = bowl + (c.getInt(5) + c.getInt(6)) + "/" + c.getInt(4);
				}
				catches += c.getInt(7) + c.getInt(8) + c.getInt(9) + c.getInt(10);
				ro += c.getInt(11) + c.getInt(12) + c.getInt(13) + c.getInt(14);
				st += c.getInt(15);
				if(count == 2){
					c.moveToNext();
					bat = bat + ", 2nd Innings ";
					if(c.getInt(1) == 0 && c.getString(2).equals("Not Out")){
						bat = bat + " DNB";
					}else{
						bat = bat + c.getInt(0);
						if(c.getString(2).equals("Not Out")){
							bat = bat + "*";
						}
					}
					bowl = bowl + ", 2nd Innings ";
					if(c.getInt(3) == 0){
						bowl = bowl + "DNB";
					}else{
						bowl = bowl + (c.getInt(5) + c.getInt(6)) + "/" + c.getInt(4);
					}
					catches += c.getInt(7) + c.getInt(8) + c.getInt(9) + c.getInt(10);
					ro += c.getInt(11) + c.getInt(12) + c.getInt(13) + c.getInt(14);
					st += c.getInt(15);
				}
				c.close();
				field = "Catches " + catches + ", Run Outs " + ro + ", Stumpings " + st;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				Date datef = new Date();
				try{
					datef = sdf.parse(date);
					Calendar cal = new GregorianCalendar();
					cal.setTime(datef);
					String day = cal.get(Calendar.DAY_OF_MONTH) + "";
					String month = PerformanceFragmentView.month_str[cal.get(Calendar.MONTH)];
					String year = cal.get(Calendar.YEAR) + "";
					date = month + " " + day + ", " + year;
				}catch(ParseException e){
					e.printStackTrace();
					Log.d("Debug", "Date Exception");
				}
				// DONE
				publishStory(bat, bowl, field, match_lvl, team_a, team_b, venue, date);
				break;
			case R.id.pur_remove_ads:
				try{
					mHelper.launchPurchaseFlow((MainActivity)main_context, SKU_REMOVE_ADS, PURCHASE_REMOVE_ADS, mPurchaseFinishedListener, getMD5());
				}catch(Exception e){
					Toast.makeText(this, "Please retry in a few seconds.", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.pur_subscribe_infi:
				try{
					if(AccessSharedPrefs.mPrefs.getString("infi_sync", "no").equals("yes")){
						try{
							((MainActivity)main_context).runOnUiThread(new Runnable(){
								public void run(){
									try{
										new AlertDialog.Builder(main_context).setTitle("Not Applicable!").setMessage("This item is not applicable for you as you have already purchased a pack which contains this feature").setNeutralButton("OK", new DialogInterface.OnClickListener(){
											public void onClick(DialogInterface dialog, int which){
												dialog.dismiss();
											}
										}).show();
									}catch(Exception e){}
								}
							});
						}catch(Exception e){}
					}else{
						mHelper.launchSubscriptionPurchaseFlow(this, SKU_SUB_INFI, PURCHASE_INFI, mPurchaseFinishedListener, getMD5());
					}
				}catch(Exception e){
					Toast.makeText(this, "Please retry in a few seconds.", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.pur_subscribe_infi_sync:
				try{
					mHelper.launchSubscriptionPurchaseFlow(this, SKU_SUB_INFI_SYNC, PURCHASE_INFI_SYNC, mPurchaseFinishedListener, getMD5());
				}catch(Exception e){
					Toast.makeText(this, "Please retry in a few seconds.", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.pur_subscribe_sync:
				try{
					((MainActivity)main_context).runOnUiThread(new Runnable(){
						public void run(){
							try{
								new AlertDialog.Builder(main_context).setTitle("Coming Soon!").setMessage("This feature is in the making. You can avail it very soon!").setNeutralButton("OK", new DialogInterface.OnClickListener(){
									public void onClick(DialogInterface dialog, int which){
										dialog.dismiss();
									}
								}).show();
							}catch(Exception e){}
						}
					});
				}catch(Exception e){}
				/*try{
					writeToFile("onclick sync");
					mHelper.launchSubscriptionPurchaseFlow(this, SKU_SUB_SYNC, PURCHASE_SYNC, mPurchaseFinishedListener, getMD5());
				}catch(Exception e){
					Toast.makeText(this, "Please retry in a few seconds.", Toast.LENGTH_SHORT).show();
				}*/
				break;
			default:
				break;
		}
	}

	private void publishStory(final String bat, final String bowl, final String field, final String match_lvl, final String team_a, final String team_b, final String venue, final String date){
		Session session = Session.getActiveSession();
		Log.w("FBShare", "Publish Story");
		if(session != null){
			// Check for publish permissions
			Log.w("FBShare", "Publish Story not null");
			List<String> permissions = session.getPermissions();
			if(!isSubsetOf(PERMISSIONS, permissions)){
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				Log.w("FBShare", "has permission");
				return;
			}
			String fname = AccessSharedPrefs.mPrefs.getString("f_name", "");
			String lname = AccessSharedPrefs.mPrefs.getString("l_name", "");
			String title = String.format("Highlights of %s's peformance on %s!", fname, date);
			String caption = String.format("%s match, %s vs %s, at %s", match_lvl, team_a, team_b, venue);
			String description = String.format("Batting: %s<center/> Bowling: %s<center/> Fielding: %s", bat, bowl, field);
			Bundle params = new Bundle();
			params.putString("name", title);
			params.putString("caption", caption);
			params.putString("link", "cdc.acjs.co/sharedHighlights.php?fn=" + fname + "&ln=" + lname + "&lvl=" + match_lvl + "&ta=" + team_a + "&tb=" + team_b + "&v=" + venue + "&bat=" + bat + "&bowl=" + bowl + "&field=" + field);
			params.putString("description", description);
			params.putString("picture", "https://dl.dropboxusercontent.com/u/15057367/Icons/CDC-icon.png");
			WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(MainActivity.main_context, Session.getActiveSession(), params)).setOnCompleteListener(new OnCompleteListener(){
				@Override
				public void onComplete(final Bundle values, final FacebookException error){
					if(error == null){
						final String postId = values.getString("post_id");
						if(postId != null){
							Toast.makeText(MainActivity.main_context, "Shared Successfully", Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(MainActivity.main_context, "Publish cancelled", Toast.LENGTH_SHORT).show();
						}
					}else if(error instanceof FacebookOperationCanceledException){
						Toast.makeText(MainActivity.main_context, "Publish cancelled", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(MainActivity.main_context, "Error posting story", Toast.LENGTH_SHORT).show();
					}
				}
			}).build();
			feedDialog.show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(!mHelper.handleActivityResult(requestCode, resultCode, data)){
			uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback(){
				@Override
				public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data){
					Log.w("Activity", String.format("Error: %s", error.toString()));
				}

				@Override
				public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data){
					Log.w("Activity", "Success!");
				}
			});
			super.onActivityResult(requestCode, resultCode, data);
		}else{}
	}

	public void showDatePicker(int view_callee){
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// Create and show the dialog.
		DatePickerFragment.datePickerFragment = new DatePickerFragment();
		DatePickerFragment.datePickerFragment.setView_callee(view_callee);
		DatePickerFragment.datePickerFragment.setDate_str(((TextView)findViewById(view_callee)).getText().toString());
		DatePickerFragment.datePickerFragment.show(ft, null);
	}

	public void showFilterDialog(int id){
		// custom dialog
		final FilterDialog dialog = new FilterDialog(this);
		Button dialogButton;
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.filter_general);
		final int sel = id;
		dialog.setOnDismissListener(new OnDismissListener(){
			@Override
			public void onDismiss(DialogInterface dialog){
				filter_showing = false;
			}
		});
		dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog){
				filter_showing = true;
			}
		});
		switch(id){
			case CAREER_FRAGMENT:
			case ANALYSIS_FRAGMENT:
			case DIARY_MATCHES_FRAGMENT:
				if(sel == DIARY_MATCHES_FRAGMENT){
					dialog.findViewById(R.id.lbl_batting_no_list).setVisibility(View.GONE);
					dialog.findViewById(R.id.batting_no_list).setVisibility(View.GONE);
					dialog.findViewById(R.id.lbl_how_out_list).setVisibility(View.GONE);
					dialog.findViewById(R.id.how_out_list).setVisibility(View.GONE);
				}
				batting_no_spinner = (MultiSelectSpinner)dialog.findViewById(R.id.batting_no_list);
				batting_no_spinner.setItems(batting_no_list);
				batting_no_spinner.setSelection(batting_no_list_selected);
				batting_no_spinner._proxyAdapter.clear();
				batting_no_spinner._proxyAdapter.add(batting_no_spinner.buildSelectedItemString());
				batting_no_spinner.setSelection(0);
				how_out_spinner = (MultiSelectSpinner)dialog.findViewById(R.id.how_out_list);
				how_out_spinner.setItems(how_out_list);
				how_out_spinner.setSelection(how_out_list_selected);
				how_out_spinner._proxyAdapter.clear();
				how_out_spinner._proxyAdapter.add(how_out_spinner.buildSelectedItemString());
				how_out_spinner.setSelection(0);
				season_spinner = (MultiSelectSpinner)dialog.findViewById(R.id.season_list);
				season_spinner.setItems(season_list);
				season_spinner.setSelection(season_list_selected);
				season_spinner._proxyAdapter.clear();
				season_spinner._proxyAdapter.add(season_spinner.buildSelectedItemString());
				season_spinner.setSelection(0);
				my_team_spinner = (MultiSelectSpinner)dialog.findViewById(R.id.my_team_list);
				my_team_spinner.setItems(my_team_list);
				my_team_spinner.setSelection(my_team_list_selected);
				my_team_spinner._proxyAdapter.clear();
				my_team_spinner._proxyAdapter.add(my_team_spinner.buildSelectedItemString());
				my_team_spinner.setSelection(0);
				opponent_spinner = (MultiSelectSpinner)dialog.findViewById(R.id.opponent_list);
				opponent_spinner.setItems(opponent_list);
				opponent_spinner.setSelection(opponent_list_selected);
				opponent_spinner._proxyAdapter.clear();
				opponent_spinner._proxyAdapter.add(opponent_spinner.buildSelectedItemString());
				opponent_spinner.setSelection(0);
				venue_spinner = (MultiSelectSpinner)dialog.findViewById(R.id.venue_list);
				venue_spinner.setItems(venue_list);
				venue_spinner.setSelection(venue_list_selected);
				venue_spinner._proxyAdapter.clear();
				venue_spinner._proxyAdapter.add(venue_spinner.buildSelectedItemString());
				venue_spinner.setSelection(0);
				result_spinner = (MultiSelectSpinner)dialog.findViewById(R.id.result_list);
				result_spinner.setItems(result_list);
				result_spinner.setSelection(result_list_selected);
				result_spinner._proxyAdapter.clear();
				result_spinner._proxyAdapter.add(result_spinner.buildSelectedItemString());
				result_spinner.setSelection(0);
				level_spinner = (MultiSelectSpinner)dialog.findViewById(R.id.level_list);
				level_spinner.setItems(level_list);
				level_spinner.setSelection(level_list_selected);
				level_spinner._proxyAdapter.clear();
				level_spinner._proxyAdapter.add(level_spinner.buildSelectedItemString());
				level_spinner.setSelection(0);
				overs_spinner = (MultiSelectSpinner)dialog.findViewById(R.id.overs_list);
				overs_spinner.setItems(overs_list);
				overs_spinner.setSelection(overs_list_selected);
				overs_spinner._proxyAdapter.clear();
				overs_spinner._proxyAdapter.add(overs_spinner.buildSelectedItemString());
				overs_spinner.setSelection(0);
				innings_spinner = (MultiSelectSpinner)dialog.findViewById(R.id.innings_list);
				innings_spinner.setItems(innings_list);
				innings_spinner.setSelection(innings_list_selected);
				innings_spinner._proxyAdapter.clear();
				innings_spinner._proxyAdapter.add(innings_spinner.buildSelectedItemString());
				innings_spinner.setSelection(0);
				duration_spinner = (MultiSelectSpinner)dialog.findViewById(R.id.duration_list);
				duration_spinner.setItems(duration_list);
				duration_spinner.setSelection(duration_list_selected);
				duration_spinner._proxyAdapter.clear();
				duration_spinner._proxyAdapter.add(duration_spinner.buildSelectedItemString());
				duration_spinner.setSelection(0);
				first_spinner = (MultiSelectSpinner)dialog.findViewById(R.id.first_list);
				first_spinner.setItems(first_list);
				first_spinner.setSelection(first_list_selected);
				first_spinner._proxyAdapter.clear();
				first_spinner._proxyAdapter.add(first_spinner.buildSelectedItemString());
				first_spinner.setSelection(0);
				Log.d("Debug", "filter_showing " + filter_showing);
				if(my_team_val != null){
					batting_no_spinner.setSelection(batting_no_val.toArray(new Integer[0]));
					how_out_spinner.setSelection(how_out_val.toArray(new Integer[0]));
					my_team_spinner.setSelection(my_team_val.toArray(new Integer[0]));
					opponent_spinner.setSelection(opponent_val.toArray(new Integer[0]));
					venue_spinner.setSelection(venue_val.toArray(new Integer[0]));
					result_spinner.setSelection(result_val.toArray(new Integer[0]));
					level_spinner.setSelection(level_val.toArray(new Integer[0]));
					overs_spinner.setSelection(overs_val.toArray(new Integer[0]));
					innings_spinner.setSelection(innings_val.toArray(new Integer[0]));
					duration_spinner.setSelection(duration_val.toArray(new Integer[0]));
					first_spinner.setSelection(first_val.toArray(new Integer[0]));
					season_spinner.setSelection(season_val.toArray(new Integer[0]));
				}else if(filter_showing){ return; }
				dialogButton = (Button)dialog.findViewById(R.id.okay);
				dialogButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						batting_no_list_selected = batting_no_spinner.getSelectedStrings();
						String str = DiaryMatchesFragment.buildSelectedItemString(batting_no_list_selected, false);
						if(!str.equals("")){
							batting_no_whereClause = " and p." + PerformanceDb.KEY_BAT_NUM + " in(" + str + ")";
						}else{
							batting_no_whereClause = " and p." + PerformanceDb.KEY_BAT_NUM + " in('')";
						}
						how_out_list_selected = how_out_spinner.getSelectedStrings();
						str = DiaryMatchesFragment.buildSelectedItemString(how_out_list_selected, false);
						if(!str.equals("")){
							how_out_whereClause = " and p." + PerformanceDb.KEY_BAT_HOW_OUT + " in(" + str + ")";
						}else{
							how_out_whereClause = " and p." + PerformanceDb.KEY_BAT_HOW_OUT + " in('')";
						}
						season_list_selected = season_spinner.getSelectedStrings();
						str = DiaryMatchesFragment.buildSelectedItemString(season_list_selected, false);
						if(!str.equals("")){
							season_whereClause = " and strftime('%Y'," + MatchDb.KEY_MATCH_DATE + ") in(" + str + ")";
						}else{
							season_whereClause = " and strftime('%Y'," + MatchDb.KEY_MATCH_DATE + ") in('')";
						}
						my_team_list_selected = my_team_spinner.getSelectedStrings();
						str = DiaryMatchesFragment.buildSelectedItemString(my_team_list_selected, false);
						if(!str.equals("")){
							myteam_whereClause = " and " + MatchDb.KEY_MY_TEAM + " in(" + str + ")";
						}else{
							myteam_whereClause = " and " + MatchDb.KEY_MY_TEAM + " in('')";
						}
						opponent_list_selected = opponent_spinner.getSelectedStrings();
						str = DiaryMatchesFragment.buildSelectedItemString(opponent_list_selected, false);
						if(!str.equals("")){
							opponent_whereClause = " and " + MatchDb.KEY_OPPONENT_TEAM + " in(" + str + ")";
						}else{
							opponent_whereClause = " and " + MatchDb.KEY_OPPONENT_TEAM + " in('')";
						}
						venue_list_selected = venue_spinner.getSelectedStrings();
						str = DiaryMatchesFragment.buildSelectedItemString(venue_list_selected, false);
						if(!str.equals("")){
							venue_whereClause = " and " + MatchDb.KEY_VENUE + " in(" + str + ")";
						}else{
							venue_whereClause = " and " + MatchDb.KEY_VENUE + " in('')";
						}
						result_list_selected = result_spinner.getSelectedStrings();
						str = DiaryMatchesFragment.buildSelectedItemString(result_list_selected, false);
						if(!str.equals("")){
							result_whereClause = " and " + MatchDb.KEY_RESULT + " in(" + str + ")";
						}else{
							result_whereClause = " and " + MatchDb.KEY_RESULT + " in('')";
						}
						level_list_selected = level_spinner.getSelectedStrings();
						str = DiaryMatchesFragment.buildSelectedItemString(level_list_selected, false);
						if(!str.equals("")){
							level_whereClause = " and " + MatchDb.KEY_LEVEL + " in(" + str + ")";
						}else{
							level_whereClause = " and " + MatchDb.KEY_LEVEL + " in('')";
						}
						overs_list_selected = overs_spinner.getSelectedStrings();
						str = DiaryMatchesFragment.buildSelectedItemString(overs_list_selected, true);
						if(!str.equals("")){
							overs_whereClause = " and " + MatchDb.KEY_OVERS + " in(" + str + ")";
						}else{
							overs_whereClause = " and " + MatchDb.KEY_OVERS + " in(-2)";
						}
						innings_list_selected = innings_spinner.getSelectedStrings();
						str = DiaryMatchesFragment.buildSelectedItemString(innings_list_selected, true);
						if(!str.equals("")){
							innings_whereClause = " and " + MatchDb.KEY_INNINGS + " in(" + str + ")";
						}else{
							innings_whereClause = " and " + MatchDb.KEY_INNINGS + " in(-2)";
						}
						duration_list_selected = duration_spinner.getSelectedStrings();
						str = DiaryMatchesFragment.buildSelectedItemString(duration_list_selected, false);
						if(!str.equals("")){
							duration_whereClause = " and " + MatchDb.KEY_DURATION + " in(" + str + ")";
						}else{
							duration_whereClause = " and " + MatchDb.KEY_DURATION + " in('')";
						}
						first_list_selected = first_spinner.getSelectedStrings();
						str = DiaryMatchesFragment.buildSelectedItemString(first_list_selected, false);
						if(!str.equals("")){
							first_whereClause = " and " + MatchDb.KEY_FIRST_ACTION + " in(" + str + ")";
						}else{
							first_whereClause = " and " + MatchDb.KEY_FIRST_ACTION + " in('')";
						}
						if(sel == DIARY_MATCHES_FRAGMENT){
							getSupportLoaderManager().restartLoader(0, null, DiaryMatchesFragment.diaryMatchesFragment);
						}else if(sel == CAREER_FRAGMENT){
							CareerFragment.careerFragment.fireQueries();
							CareerFragment.careerFragment.viewInfo(PerformanceFragmentEdit.GENERAL);
							CareerFragment.careerFragment.viewInfo(PerformanceFragmentEdit.BATTING);
							CareerFragment.careerFragment.viewInfo(PerformanceFragmentEdit.BOWLING);
							CareerFragment.careerFragment.viewInfo(PerformanceFragmentEdit.FIELDING);
						}
						Toast.makeText(MainActivity.main_context, "Filter Set", Toast.LENGTH_LONG).show();
						dialog.dismiss();
						v.setBackgroundColor(getResources().getColor(R.color.dark_red));
					}
				});
				dialogButton = (Button)dialog.findViewById(R.id.filter_reset);
				dialogButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						season_spinner.setItems(season_list);
						season_spinner.setSelection(season_list);
						season_spinner._proxyAdapter.clear();
						season_spinner._proxyAdapter.add(season_spinner.buildSelectedItemString());
						season_spinner.setSelection(0);
						my_team_spinner.setItems(my_team_list);
						my_team_spinner.setSelection(my_team_list);
						my_team_spinner._proxyAdapter.clear();
						my_team_spinner._proxyAdapter.add(my_team_spinner.buildSelectedItemString());
						my_team_spinner.setSelection(0);
						opponent_spinner.setItems(opponent_list);
						opponent_spinner.setSelection(opponent_list);
						opponent_spinner._proxyAdapter.clear();
						opponent_spinner._proxyAdapter.add(opponent_spinner.buildSelectedItemString());
						opponent_spinner.setSelection(0);
						venue_spinner.setItems(venue_list);
						venue_spinner.setSelection(venue_list);
						venue_spinner._proxyAdapter.clear();
						venue_spinner._proxyAdapter.add(venue_spinner.buildSelectedItemString());
						venue_spinner.setSelection(0);
						result_spinner.setItems(result_list);
						result_spinner.setSelection(result_list);
						result_spinner._proxyAdapter.clear();
						result_spinner._proxyAdapter.add(result_spinner.buildSelectedItemString());
						result_spinner.setSelection(0);
						level_spinner.setItems(level_list);
						level_spinner.setSelection(level_list);
						level_spinner._proxyAdapter.clear();
						level_spinner._proxyAdapter.add(level_spinner.buildSelectedItemString());
						level_spinner.setSelection(0);
						overs_spinner.setItems(overs_list);
						overs_spinner.setSelection(overs_list);
						overs_spinner._proxyAdapter.clear();
						overs_spinner._proxyAdapter.add(overs_spinner.buildSelectedItemString());
						overs_spinner.setSelection(0);
						innings_spinner.setItems(innings_list);
						innings_spinner.setSelection(innings_list);
						innings_spinner._proxyAdapter.clear();
						innings_spinner._proxyAdapter.add(innings_spinner.buildSelectedItemString());
						innings_spinner.setSelection(0);
						duration_spinner.setItems(duration_list);
						duration_spinner.setSelection(duration_list);
						duration_spinner._proxyAdapter.clear();
						duration_spinner._proxyAdapter.add(duration_spinner.buildSelectedItemString());
						duration_spinner.setSelection(0);
						first_spinner.setItems(first_list);
						first_spinner.setSelection(first_list);
						first_spinner._proxyAdapter.clear();
						first_spinner._proxyAdapter.add(first_spinner.buildSelectedItemString());
						first_spinner.setSelection(0);
					}
				});
				dialogButton = (Button)dialog.findViewById(R.id.dialog_cancel);
				dialogButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						dialog.dismiss();
					}
				});
				break;
			default:
				break;
		}
		filter_dialog = dialog;
		dialog.show();
	}

	public boolean onKeyUp(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(currentFragment == root_fragment){
				super.onBackPressed();
				return true;
			}
			switch(currentFragment){
				case MATCH_CREATION_FRAGMENT:
					currentFragment = ONGOING_MATCHES_FRAGMENT;
					if(root_fragment == CAREER_FRAGMENT){
						preFragment = CAREER_FRAGMENT;
					}else{
						preFragment = NO_FRAGMENT;
					}
					selectItem(ONGOING_MATCHES_FRAGMENT, true);
					onPrepareOptionsMenu(current_menu);
					return true;
				case PERFORMANCE_FRAGMENT_EDIT:
					PerformanceFragmentEdit.performanceFragmentEdit.insertOrUpdate();
					onPrepareOptionsMenu(current_menu);
					return true;
				case PERFORMANCE_FRAGMENT_VIEW:
					currentFragment = DIARY_MATCHES_FRAGMENT;
					preFragment = root_fragment;
					selectItem(currentFragment, true);
					onPrepareOptionsMenu(current_menu);
					return true;
				case PROFILE_FRAGMENT:
					if(ProfileFragment.currentProfileFragment == ProfileFragment.PROFILE_EDIT_FRAGMENT){
						Log.d("Debug", "Profile Edit Hi");
						ProfileEditFragment.profileEditFragment.saveEditedProfile();
						ProfileFragment.currentProfileFragment = ProfileFragment.PROFILE_VIEW_FRAGMENT;
						onPrepareOptionsMenu(current_menu);
						ProfileFragment.profileFragment.viewFragment();
						return true;
					}
				default:
					switch(preFragment){
						case PROFILE_FRAGMENT:
						case ANALYSIS_FRAGMENT:
						case CAREER_FRAGMENT:
						case DIARY_MATCHES_FRAGMENT:
						case PURCHASE_FRAGMENT:
						case ONGOING_MATCHES_FRAGMENT:
							currentFragment = root_fragment;
							preFragment = NO_FRAGMENT;
							selectItem(root_fragment, true);
							onPrepareOptionsMenu(current_menu);
							return true;
					}
					break;
			}
		}else if(keyCode == KeyEvent.KEYCODE_MENU){
			toggleNavigationDrawer();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	public void make_directory(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			try{
				File extStore = Environment.getExternalStorageDirectory();
				File projDir = new File(extStore.getAbsolutePath() + "/" + getResources().getString(R.string.cricdecode_dir));
				if(!projDir.exists()) projDir.mkdirs();
				Log.w("MainActivity", "Creating Directory");
			}catch(Exception e){}
		}
	}

	public void showNavigationDrawer(View view){
		toggleNavigationDrawer();
	}

	private void toggleNavigationDrawer(){
		if(mDrawerLayout.isDrawerOpen(mDrawerList)){
			mDrawerLayout.closeDrawer(mDrawerList);
		}else{
			mDrawerLayout.openDrawer(mDrawerList);
		}
	}

	public void onMenuButtonClick(View v){
		String str = ((ImageButton)v).getContentDescription().toString();
		if(str.equals(getResources().getString(R.string.new_match))){
			boolean matchLimitExceeeded = false;
			Cursor c = dbHandle.rawQuery("select count(" + MatchDb.KEY_ROWID + ") from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_DEVICE_ID + " = '" + AccessSharedPrefs.mPrefs.getString("device_id", "") + "'", null);
			if(c.getCount() != 0){
				c.moveToFirst();
				Log.d("Debug", "Match Count " + c.getInt(0));
				if(c.getInt(0) >= 5){
					matchLimitExceeeded = true;
				}
			}
			c.close();
			// Comment this condition and Uncomment if true for testing
			if(AccessSharedPrefs.mPrefs.getString("infi_use", "no").equals("yes") | AccessSharedPrefs.mPrefs.getString("infi_sync", "no").equals("yes") | !matchLimitExceeeded)
			/* if (true) */{
				preFragment = currentFragment;
				currentFragment = MATCH_CREATION_FRAGMENT;
				selectItem(MATCH_CREATION_FRAGMENT, true);
				onPrepareOptionsMenu(current_menu);
			}else{
				final Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.dialog_confirmation);
				dialog.setTitle("Subscribe");
				TextView dialogText = (TextView)dialog.findViewById(R.id.dialog_text);
				dialogText.setText("Subscribe to one of our schemes to enable creation of Unlimited Matches");
				Button yes = (Button)dialog.findViewById(R.id.yes);
				yes.setText("Buy");
				yes.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						dialog.dismiss();
						currentFragment = PURCHASE_FRAGMENT;
						preFragment = CAREER_FRAGMENT;
						onPrepareOptionsMenu(current_menu);
						selectItem(currentFragment, true);
						setPageName(currentFragment);
					}
				});
				Button no = (Button)dialog.findViewById(R.id.no);
				no.setText("Cancel");
				no.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		}else if(str.equals(getResources().getString(R.string.edit_profile))){
			ProfileFragment.currentProfileFragment = ProfileFragment.PROFILE_EDIT_FRAGMENT;
			onPrepareOptionsMenu(current_menu);
			ProfileFragment.profileFragment.viewFragment();
		}else if(str.equals(getResources().getString(R.string.save_profile))){
			ProfileEditFragment.profileEditFragment.saveEditedProfile();
			ProfileFragment.currentProfileFragment = ProfileFragment.PROFILE_VIEW_FRAGMENT;
			onPrepareOptionsMenu(current_menu);
			ProfileFragment.profileFragment.viewFragment();
		}else if(str.equals(getResources().getString(R.string.create_match))){
			MatchCreationFragment.matchCreationFragment.insertMatch();
			onPrepareOptionsMenu(current_menu);
		}else if(str.equals(getResources().getString(R.string.save_performance))){
			PerformanceFragmentEdit.performanceFragmentEdit.insertOrUpdate();
			onPrepareOptionsMenu(current_menu);
		}else if(str.equals(getResources().getString(R.string.filter))){
			showFilterDialog(currentFragment);
		}else if(str.equals(getResources().getString(R.string.bar_chart))){
			AnalysisFragment.analysisFragment.generateXYGraph();
		}else if(str.equals(getResources().getString(R.string.pie_chart))){
			AnalysisFragment.analysisFragment.generatePieGraph();
		}
	}

	public void setPageName(int curr){
		switch(currentFragment){
			case PROFILE_FRAGMENT:
				spinner.setVisibility(View.GONE);
				tx.setVisibility(View.VISIBLE);
				tx.setText(R.string.profile);
				break;
			case PROFILE_EDIT:
				spinner.setVisibility(View.GONE);
				tx.setVisibility(View.VISIBLE);
				tx.setText(R.string.profile_edit);
				break;
			case CAREER_FRAGMENT:
				spinner.setVisibility(View.GONE);
				tx.setVisibility(View.VISIBLE);
				tx.setText(R.string.career);
				break;
			case ANALYSIS_FRAGMENT:
				spinner.setVisibility(View.GONE);
				tx.setVisibility(View.VISIBLE);
				tx.setText(R.string.analysis);
				break;
			case MATCH_CREATION_FRAGMENT:
				spinner.setVisibility(View.GONE);
				tx.setVisibility(View.VISIBLE);
				tx.setText(R.string.create_new_match);
				break;
			case DIARY_MATCHES_FRAGMENT:
				spinner.setVisibility(View.GONE);
				tx.setVisibility(View.VISIBLE);
				tx.setText(R.string.match_diary);
				break;
			case ONGOING_MATCHES_FRAGMENT:
				spinner.setVisibility(View.GONE);
				tx.setVisibility(View.VISIBLE);
				tx.setText(R.string.ongoing_matches);
				break;
			case PURCHASE_FRAGMENT:
				spinner.setVisibility(View.GONE);
				tx.setVisibility(View.VISIBLE);
				tx.setText(R.string.buy_schemes);
				break;
			case SUPPORT:
				spinner.setVisibility(View.GONE);
				tx.setVisibility(View.VISIBLE);
				tx.setText(R.string.support);
				break;
			case PERFORMANCE_FRAGMENT_EDIT:
				spinner.setVisibility(View.VISIBLE);
				tx.setVisibility(View.GONE);
				tx.setText(R.string.profile_edit);
				break;
			case PERFORMANCE_FRAGMENT_VIEW:
				spinner.setVisibility(View.VISIBLE);
				tx.setVisibility(View.GONE);
				tx.setText(R.string.profile);
				break;
			default:
				break;
		}
	}

	public void fetchFromDb(){
		MainActivity mainActivity = this;
		batting_no_list = new ArrayList<String>();
		batting_no_list_selected = new ArrayList<String>();
		how_out_list = new ArrayList<String>();
		how_out_list_selected = new ArrayList<String>();
		mainActivity.season_list = new ArrayList<String>();
		mainActivity.season_list_selected = new ArrayList<String>();
		mainActivity.my_team_list = new ArrayList<String>();
		mainActivity.my_team_list_selected = new ArrayList<String>();
		mainActivity.opponent_list = new ArrayList<String>();
		mainActivity.opponent_list_selected = new ArrayList<String>();
		mainActivity.venue_list = new ArrayList<String>();
		mainActivity.venue_list_selected = new ArrayList<String>();
		mainActivity.result_list = new ArrayList<String>();
		mainActivity.result_list_selected = new ArrayList<String>();
		mainActivity.level_list = new ArrayList<String>();
		mainActivity.level_list_selected = new ArrayList<String>();
		mainActivity.overs_list = new ArrayList<String>();
		mainActivity.overs_list_selected = new ArrayList<String>();
		mainActivity.innings_list = new ArrayList<String>();
		mainActivity.innings_list_selected = new ArrayList<String>();
		mainActivity.duration_list = new ArrayList<String>();
		mainActivity.duration_list_selected = new ArrayList<String>();
		mainActivity.first_list = new ArrayList<String>();
		mainActivity.first_list_selected = new ArrayList<String>();
		Cursor c = MainActivity.dbHandle.rawQuery("select distinct strftime('%Y'," + MatchDb.KEY_MATCH_DATE + ") as _id from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null);
		int count = c.getCount();
		if(count != 0){
			c.moveToFirst();
			do{
				mainActivity.season_list.add(c.getString(0));
				mainActivity.season_list_selected.add(c.getString(0));
			}while(c.moveToNext());
		}
		c.close();
		c = MainActivity.dbHandle.rawQuery("select distinct " + MatchDb.KEY_MY_TEAM + " as _id from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if(count != 0){
			c.moveToFirst();
			do{
				mainActivity.my_team_list.add(c.getString(0));
				mainActivity.my_team_list_selected.add(c.getString(0));
			}while(c.moveToNext());
		}
		c.close();
		c = MainActivity.dbHandle.rawQuery("select distinct " + MatchDb.KEY_OPPONENT_TEAM + " as _id from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if(count != 0){
			c.moveToFirst();
			do{
				mainActivity.opponent_list.add(c.getString(0));
				mainActivity.opponent_list_selected.add(c.getString(0));
			}while(c.moveToNext());
		}
		c.close();
		c = MainActivity.dbHandle.rawQuery("select distinct " + MatchDb.KEY_VENUE + " as _id from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if(count != 0){
			c.moveToFirst();
			do{
				mainActivity.venue_list.add(c.getString(0));
				mainActivity.venue_list_selected.add(c.getString(0));
			}while(c.moveToNext());
		}
		c.close();
		c = MainActivity.dbHandle.rawQuery("select distinct " + MatchDb.KEY_RESULT + " as _id from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if(count != 0){
			c.moveToFirst();
			do{
				mainActivity.result_list.add(c.getString(0));
				mainActivity.result_list_selected.add(c.getString(0));
			}while(c.moveToNext());
		}
		c.close();
		c = MainActivity.dbHandle.rawQuery("select distinct " + MatchDb.KEY_LEVEL + " as _id from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if(count != 0){
			c.moveToFirst();
			do{
				mainActivity.level_list.add(c.getString(0));
				mainActivity.level_list_selected.add(c.getString(0));
			}while(c.moveToNext());
		}
		c.close();
		c = MainActivity.dbHandle.rawQuery("select distinct " + MatchDb.KEY_OVERS + " as _id from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if(count != 0){
			c.moveToFirst();
			do{
				int temp = c.getInt(0);
				if(temp == -1){
					mainActivity.overs_list.add("Unlimited");
					mainActivity.overs_list_selected.add("Unlimited");
				}else{
					mainActivity.overs_list.add(c.getInt(0) + "");
					mainActivity.overs_list_selected.add(c.getInt(0) + "");
				}
			}while(c.moveToNext());
		}
		c.close();
		c = MainActivity.dbHandle.rawQuery("select distinct " + MatchDb.KEY_INNINGS + " as _id from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if(count != 0){
			c.moveToFirst();
			do{
				mainActivity.innings_list.add(c.getInt(0) + "");
				mainActivity.innings_list_selected.add(c.getInt(0) + "");
			}while(c.moveToNext());
		}
		c.close();
		c = MainActivity.dbHandle.rawQuery("select distinct " + MatchDb.KEY_DURATION + " as _id from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if(count != 0){
			c.moveToFirst();
			do{
				mainActivity.duration_list.add(c.getString(0));
				mainActivity.duration_list_selected.add(c.getString(0));
			}while(c.moveToNext());
		}
		c.close();
		c = MainActivity.dbHandle.rawQuery("select distinct " + MatchDb.KEY_FIRST_ACTION + " as _id from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if(count != 0){
			c.moveToFirst();
			do{
				mainActivity.first_list.add(c.getString(0));
				mainActivity.first_list_selected.add(c.getString(0));
			}while(c.moveToNext());
		}
		c.close();
		c = MainActivity.dbHandle.rawQuery("select distinct " + PerformanceDb.KEY_BAT_NUM + " as _id from " + PerformanceDb.SQLITE_TABLE + " where " + PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "' and (" + PerformanceDb.KEY_BAT_HOW_OUT + "!='Not Out' or " + PerformanceDb.KEY_BAT_BALLS + "!=0)", null);
		count = c.getCount();
		if(count != 0){
			c.moveToFirst();
			do{
				batting_no_list.add(c.getString(0));
				batting_no_list_selected.add(c.getString(0));
			}while(c.moveToNext());
		}
		c.close();
		c = MainActivity.dbHandle.rawQuery("select distinct " + PerformanceDb.KEY_BAT_HOW_OUT + " as _id from " + PerformanceDb.SQLITE_TABLE + " where " + PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if(count != 0){
			c.moveToFirst();
			do{
				how_out_list.add(c.getString(0));
				how_out_list_selected.add(c.getString(0));
			}while(c.moveToNext());
		}
		c.close();
	}

	public static String decrypt(String val1, String val2, String val3, String val4, String seq, int ci){
		String val = val2 + val4 + val1 + val3;
		int num = val.length() / 10;
		char h[][] = new char[num + 1][10];
		int start = 0;
		int end = 10;
		for(int i = 0; i < num; i++){
			String s = val.substring(start, end);
			h[i] = s.toCharArray();
			start = end;
			end = end + 10;
		}
		h[num] = val.substring(start, val.length()).toCharArray();
		char[][] un = new char[10][num];
		char s[] = seq.toCharArray();
		for(int i = 0; i < num; i++){
			for(int j = 0; j < 10; j++){
				String n = new String("" + s[j]);
				int ind = Integer.parseInt(n);
				un[ind][i] = h[i][j];
			}
		}
		String dec = "";
		for(int i = 0; i < 10; i++){
			String n = new String(un[i]);
			dec = dec + n;
		}
		String ex = new String(h[num]);
		dec = dec + ex;
		char[] us = dec.toCharArray();
		char[] sh = new char[us.length];
		for(int i = 0; i < us.length; i++){
			sh[i] = (char)(us[i] - ci);
		}
		return new String(sh);
	}

	private void writeToFile(String data){
		try{
			File root = new File(Environment.getExternalStorageDirectory(), "CricDeCode");
			if(!root.exists()){
				root.mkdirs();
			}
			File gpxfile = new File(root, "main.txt");
			FileWriter writer = new FileWriter(gpxfile, true);
			writer.write(data + "\n");
			writer.flush();
			writer.close();
		}catch(IOException e){
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	public Boolean isOnline(Context cont){
		ConnectivityManager connectivityManager = (ConnectivityManager)cont.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}