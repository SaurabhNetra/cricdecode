package co.acjs.cricdecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.analytics.tracking.android.EasyTracker;
import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobCallback;
import com.stackmob.sdk.callback.StackMobModelCallback;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;


public class LogIn extends SherlockActivity{
	static GraphUser		user;
	static Context			login_activity;
	ContentProviderClient	client;
	SQLiteDatabase			dbHandle;
	static TextView			progressText;
	static Boolean			onActivityResult	= false;
	static ContentResolver	cr;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		cr = getContentResolver();
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.action_bar_login, null);
		actionBar.setCustomView(view);
		AccessSharedPrefs.mPrefs = getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		login_activity = this;
		final LoginButton loginButton = (LoginButton)findViewById(R.id.login);
		progressText = (TextView)findViewById(R.id.warning_text);
		List<String> permissions = Arrays.asList("user_birthday");
		loginButton.setReadPermissions(permissions);
		loginButton.setApplicationId(getResources().getString(R.string.fb_app_id));
		loginButton.setSessionStatusCallback(new Session.StatusCallback(){
			@Override
			public void call(Session session, SessionState state, Exception exception){
				if(session.isOpened()){
					loginButton.setVisibility(View.GONE);
					((ProgressBar)findViewById(R.id.progress_bar)).setVisibility(View.VISIBLE);
					// make request to the /me API
					Request.newMeRequest(session, new Request.GraphUserCallback(){
						// callback after Graph API response with user
						// object
						@Override
						public void onCompleted(GraphUser user, Response response){
							if(user != null){
								Log.w("Face Book Login Complete 1", "LogIn: " + user.getBirthday());
								writeToFile("Face Book Login Complete");
								LogIn.user = user;
								progressText.setText("Phase 1...");
								GCMRegistration();
							}
						}
					}).executeAsync();
				}
			}
		});
	}

	@Override
	protected void onStart(){
		super.onStart();
		// Google Analytics Stop
		EasyTracker.getInstance(this).activityStart(this);
		if(!AccessSharedPrefs.mPrefs.getString("isSignedIn", "").equals("")){
			ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
				login_activity.startService(new Intent(login_activity, FBRefreshService.class));
			}
			openMainActivity();
		}else if(!onActivityResult){
			Session session = Session.getActiveSession();
			final LoginButton loginButton = (LoginButton)findViewById(R.id.login);
			try{
				if(loginButton.getVisibility() != View.GONE) if(session.isOpened()){
					loginButton.setVisibility(View.GONE);
					((ProgressBar)findViewById(R.id.progress_bar)).setVisibility(View.VISIBLE);
					// make request to the /me API
					Request.newMeRequest(session, new Request.GraphUserCallback(){
						// callback after Graph API response with
						// user
						// object
						@Override
						public void onCompleted(GraphUser user, Response response){
							if(user != null){
								Log.w("Face Book Login Complete 2", "LogIn: " + user.getBirthday());
								LogIn.user = user;
								progressText.setText("Phase 1...");
								GCMRegistration();
							}
						}
					}).executeAsync();
				}
			}catch(Exception e){}
		}
	}

	@Override
	protected void onStop(){
		super.onStop();
		// Google Analytics Stop
		EasyTracker.getInstance(this).activityStop(this);
		onActivityResult = false;
	}

	void GCMRegistration(){
		writeToFile("GCMRegistration");
		progressText.setText("Phase 2...");
		// TODO encrypt
		StackMobAndroid.init(login_activity, 0, "c52a9f47-baae-41e3-aa63-72177b0c23f7");
		client = getContentResolver().acquireContentProviderClient(CricDeCodeContentProvider.AUTHORITY);
		dbHandle = ((CricDeCodeContentProvider)client.getLocalContentProvider()).getDbHelper().getReadableDatabase();
		AccessSharedPrefs.mPrefs = login_activity.getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		GCMRegistrarCompat.checkDevice(this);
		if(BuildConfig.DEBUG){
			GCMRegistrarCompat.checkManifest(this);
		}
		new RegisterTask(this).execute(getResources().getString(R.string.projno));
	}

	private static class RegisterTask extends GCMRegistrarCompat.BaseRegisterTask{
		RegisterTask(Context context){
			super(context);
		}

		@Override
		public void onPostExecute(String regid){
			Log.d(getClass().getSimpleName(), "registered as: " + regid);
			if(regid == null) ((LogIn)login_activity).GCMRegistration();
			else{
				startApp(regid);
				writeToFile("On Post Execute startApp");
			}
		}
	}

	static void chkAll(){
		writeToFile("chk all called");
		Log.w("INSERT INTO user_android_devices values('$id','$gcmid','$tday')", "success");
		ServerDBUserTable.query(ServerDBUserTable.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(user.getId())), new StackMobQueryCallback<ServerDBUserTable>(){
			@Override
			public void failure(StackMobException arg0){
				Log.w("chk if user existing", arg0);
			}

			@Override
			public void success(final List<ServerDBUserTable> returenedVar){
				Log.w("chk if user existing", "success");
				if(returenedVar.size() == 0) new ServerDBUserTable(user.getId(), 1, user.getFirstName(), user.getLastName(), "", user.getLink(), user.getBirthday(), "", "", "", 1).save(new StackMobCallback(){
					@Override
					public void success(String arg0){
						AccessSharedPrefs.setString(login_activity, "device_id", "1");
						openMainActivity();
					}

					@Override
					public void failure(StackMobException arg0){}
				});
				else{
					// Bigggg else
					returenedVar.get(0).setDeviceNo(returenedVar.get(0).getDeviceNo() + 1);
					returenedVar.get(0).save(new StackMobModelCallback(){
						String	dev_no		= "" + (returenedVar.get(0).getDeviceNo() + 1);
						String	nickname	= returenedVar.get(0).getNickName();
						String	bat_style	= returenedVar.get(0).getBattingStyle();
						String	bowl_style	= returenedVar.get(0).getBowlingStyle();
						String	role		= returenedVar.get(0).getRole();

						@Override
						public void failure(StackMobException arg0){}

						@Override
						public void success(){
							Log.w("LogIn", "DeviceId success");
							AccessSharedPrefs.setString(login_activity, "device_id", "" + dev_no);
							AccessSharedPrefs.setString(login_activity, "role", role);
							AccessSharedPrefs.setString(login_activity, "battingStyle", bat_style);
							AccessSharedPrefs.setString(login_activity, "bowlingStyle", bowl_style);
							AccessSharedPrefs.setString(login_activity, "nickname", nickname);
							ServerDBRemoveAds.query(ServerDBRemoveAds.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(user.getId())), new StackMobQueryCallback<ServerDBRemoveAds>(){
								@Override
								public void failure(StackMobException arg0){
									Log.w("LoginIn", "remove_ads_chk failure!!");
								}

								@Override
								public void success(List<ServerDBRemoveAds> arg0){
									Log.w("LoginIn", "remove_ads_chk success!!");
									if(arg0.size() > 0) AccessSharedPrefs.setString(login_activity, "ad_free", "yes");
									ServerDBSubInfi.query(ServerDBSubInfi.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(user.getId())), new StackMobQueryCallback<ServerDBSubInfi>(){
										@Override
										public void failure(StackMobException arg0){
											Log.w("LoginIn", "sub_infi_chk failure!!");
										}

										@Override
										public void success(List<ServerDBSubInfi> arg0){
											Log.w("LoginIn", "sub_infi_chk success!!" + arg0.size());
											long t = new Date().getTime();
											if((arg0.size() > 0)){
												if((t < arg0.get(0).validUntil_ts_msec)){
													AccessSharedPrefs.setString(login_activity, "infi_use", "yes");
													Log.w("LoginIn", "sub_infi_chk success!! 2");
												}
											}
											Log.w("LoginIn", "sub_infi_chk success!! 3");
											ServerDBSubInfiSync.query(ServerDBSubInfiSync.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(user.getId())), new StackMobQueryCallback<ServerDBSubInfiSync>(){
												@Override
												public void failure(StackMobException arg0){
													Log.w("LoginIn", "sub_sync_chk failure!!");
												}

												@Override
												public void success(List<ServerDBSubInfiSync> arg0){
													Log.w("LoginIn", "sub_sync_chk success!!");
													long t = new Date().getTime();
													if(arg0.size() > 0){
														if(t < arg0.get(0).validUntil_ts_msec) AccessSharedPrefs.setString(login_activity, "infi_sync", "yes");
													}
													ServerDBCricketMatch.query(ServerDBCricketMatch.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(user.getId())).field(new StackMobQueryField("status").isEqualTo(0)), new StackMobQueryCallback<ServerDBCricketMatch>(){
														@Override
														public void failure(StackMobException arg0){
															Log.w("LoginIn", "cricket_match failure!!");
														}

														@Override
														public void success(List<ServerDBCricketMatch> arg0){
															Log.w("LoginIn", "cricket_match success!!" + arg0.size());
															for(int i = 0; i < arg0.size(); i++){
																Log.w("LoginIn", "cricket_match in loop!!" + i);
																ContentValues values = new ContentValues();
																values.put(MatchDb.KEY_ROWID, arg0.get(i).getMatchId());
																values.put(MatchDb.KEY_DEVICE_ID, "" + arg0.get(i).getDeviceId());
																values.put(MatchDb.KEY_MATCH_DATE, arg0.get(i).getMatchDate());
																values.put(MatchDb.KEY_MY_TEAM, arg0.get(i).getMyTeam());
																Log.w("LoginIn", "cricket_match in loop!!" + arg0.get(i).getMyTeam());
																values.put(MatchDb.KEY_OPPONENT_TEAM, arg0.get(i).getOpponentTeam());
																values.put(MatchDb.KEY_VENUE, arg0.get(i).getVenue());
																values.put(MatchDb.KEY_OVERS, arg0.get(i).getOvers());
																values.put(MatchDb.KEY_INNINGS, arg0.get(i).getInnings());
																values.put(MatchDb.KEY_RESULT, arg0.get(i).getResult());
																values.put(MatchDb.KEY_LEVEL, arg0.get(i).getLevel());
																values.put(MatchDb.KEY_FIRST_ACTION, arg0.get(i).getFirstAction());
																values.put(MatchDb.KEY_DURATION, arg0.get(i).getDuration());
																values.put(MatchDb.KEY_REVIEW, arg0.get(i).getReview());
																values.put(MatchDb.KEY_STATUS, MatchDb.MATCH_HISTORY);
																values.put(MatchDb.KEY_SYNCED, 0);
																Uri u = cr.insert(CricDeCodeContentProvider.CONTENT_URI_MATCH, values);
																Log.w("Uri inserted", "" + u);
															}
															ServerDBPerformance.query(ServerDBPerformance.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(user.getId())).field(new StackMobQueryField("status").isEqualTo(0)), new StackMobQueryCallback<ServerDBPerformance>(){
																@Override
																public void failure(StackMobException arg0){
																	Log.w("LoginIn", "performance failure!!");
																}

																@Override
																public void success(List<ServerDBPerformance> arg0){
																	Log.w("LoginIn", "performance success!!" + arg0.size());
																	for(int i = 0; i < arg0.size(); i++){
																		ContentValues values = new ContentValues();
																		values.put(PerformanceDb.KEY_MATCHID, arg0.get(i).getMatchId());
																		values.put(PerformanceDb.KEY_DEVICE_ID, "" + arg0.get(i).getDeviceId());
																		Log.w("LoginIn", "cricket_match in loop!!" + arg0.get(i).getDeviceId());
																		values.put(PerformanceDb.KEY_ROWID, arg0.get(i).getPerId());
																		values.put(PerformanceDb.KEY_INNING, arg0.get(i).getInning());
																		values.put(PerformanceDb.KEY_BAT_NUM, arg0.get(i).getBatNum());
																		values.put(PerformanceDb.KEY_BAT_RUNS, arg0.get(i).getBatRuns());
																		values.put(PerformanceDb.KEY_BAT_BALLS, arg0.get(i).getBatBalls());
																		values.put(PerformanceDb.KEY_BAT_TIME, arg0.get(i).getBatTime());
																		values.put(PerformanceDb.KEY_BAT_FOURS, arg0.get(i).getBatFours());
																		values.put(PerformanceDb.KEY_BAT_SIXES, arg0.get(i).getBatSixes());
																		values.put(PerformanceDb.KEY_BAT_HOW_OUT, arg0.get(i).getBatDismissal());
																		values.put(PerformanceDb.KEY_BAT_BOWLER_TYPE, arg0.get(i).getBatBowlerType());
																		values.put(PerformanceDb.KEY_BAT_FIELDING_POSITION, arg0.get(i).getBatFieldingPosition());
																		values.put(PerformanceDb.KEY_BAT_CHANCES, arg0.get(i).getBatChances());
																		values.put(PerformanceDb.KEY_BOWL_BALLS, arg0.get(i).getBowlBalls());
																		values.put(PerformanceDb.KEY_BOWL_SPELLS, arg0.get(i).getBowlSpells());
																		values.put(PerformanceDb.KEY_BOWL_MAIDENS, arg0.get(i).getBowlMaidens());
																		values.put(PerformanceDb.KEY_BOWL_RUNS, arg0.get(i).getBowlRuns());
																		values.put(PerformanceDb.KEY_BOWL_FOURS, arg0.get(i).getBowlFours());
																		values.put(PerformanceDb.KEY_BOWL_SIXES, arg0.get(i).getBowlSixes());
																		values.put(PerformanceDb.KEY_BOWL_WKTS_LEFT, arg0.get(i).getBowlWktsLeft());
																		values.put(PerformanceDb.KEY_BOWL_WKTS_RIGHT, arg0.get(i).getBowlWktsRight());
																		values.put(PerformanceDb.KEY_BOWL_CATCHES_DROPPED, arg0.get(i).getBowlCatchesDropped());
																		values.put(PerformanceDb.KEY_BOWL_NOBALLS, arg0.get(i).getNoBalls());
																		values.put(PerformanceDb.KEY_BOWL_WIDES, arg0.get(i).getBowlWides());
																		values.put(PerformanceDb.KEY_FIELD_SLIP_CATCH, arg0.get(i).getFieldSlipCatch());
																		values.put(PerformanceDb.KEY_FIELD_CLOSE_CATCH, arg0.get(i).getFieldCloseCatch());
																		values.put(PerformanceDb.KEY_FIELD_CIRCLE_CATCH, arg0.get(i).getFieldCircleCatch());
																		values.put(PerformanceDb.KEY_FIELD_DEEP_CATCH, arg0.get(i).getFieldDeepCatch());
																		values.put(PerformanceDb.KEY_FIELD_RO_CIRCLE, arg0.get(i).getFieldRoCircle());
																		values.put(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE, arg0.get(i).getFieldRoDirectCircle());
																		values.put(PerformanceDb.KEY_FIELD_RO_DEEP, arg0.get(i).getFieldRoDeep());
																		values.put(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP, arg0.get(i).getFieldRoDirectDeep());
																		values.put(PerformanceDb.KEY_FIELD_STUMPINGS, arg0.get(i).getFieldStumping());
																		values.put(PerformanceDb.KEY_FIELD_BYES, arg0.get(i).getFieldByes());
																		values.put(PerformanceDb.KEY_FIELD_MISFIELDS, arg0.get(i).getMisFields());
																		values.put(PerformanceDb.KEY_FIELD_CATCHES_DROPPED, arg0.get(i).getCatchedDropped());
																		values.put(PerformanceDb.KEY_SYNCED, 0);
																		Uri u = cr.insert(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE, values);
																		Log.w("Uri inserted", "" + u);
																	}
																	openMainActivity();
																}
															});
														}
													});
												}
											});
										}
									});
								}
							});
						}
					});
				}
			}
		});
	}

	static void startApp(final String gcm_reg_id){
		Log.w("Start App", "called");
		writeToFile("Inside StartApp");
		progressText.setText("Phase 3...");
		Log.w("Start App", "else ");
		AccessSharedPrefs.setString(login_activity, "id", user.getId());
		AccessSharedPrefs.setString(login_activity, "f_name", user.getFirstName());
		AccessSharedPrefs.setString(login_activity, "l_name", user.getLastName());
		AccessSharedPrefs.setString(login_activity, "dob", user.getBirthday());
		AccessSharedPrefs.setString(login_activity, "gcm_reg_id", gcm_reg_id);
		AccessSharedPrefs.setString(login_activity, "fb_link", user.getLink());
		ServerDBAndroidDevices.query(ServerDBAndroidDevices.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(user.getId())).field(new StackMobQueryField("gcm_id").isEqualTo(gcm_reg_id)), new StackMobQueryCallback<ServerDBAndroidDevices>(){
			@Override
			public void failure(StackMobException arg0){
				Log.w("LogIn gcmid", arg0);
				new AlertDialog.Builder(login_activity).setTitle("Weak Internet Connection").setMessage("Check your internet connection and restart app.").setNeutralButton("Restart", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						dialog.dismiss();
						Intent i = new Intent(login_activity, LogIn.class);
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						login_activity.startActivity(i);
					}
				}).show();
			}

			@Override
			public void success(List<ServerDBAndroidDevices> returenedVar){
				writeToFile("ServerDBAndroidDevices success" + returenedVar.size());
				Log.w("LogIn gcmid", "success1" + returenedVar.size());
				// progressText.setText("Phase 4...");
				Log.w("LogIn gcmid", "success2" + returenedVar.size());
				if(returenedVar.size() == 0){
					Log.w("Login gcmid", "size = 0");
					new ServerDBAndroidDevices(user.getId(), gcm_reg_id).save(new StackMobCallback(){
						@Override
						public void success(String arg0){
							writeToFile("ServerDBAndroidDevices success");
							chkAll();
						}

						@Override
						public void failure(StackMobException arg0){
							Log.w("INSERT INTO user_android_devices values('$id','$gcmid','$tday')", arg0);
							new AlertDialog.Builder(login_activity).setTitle("Weak Internet Connection").setMessage("Check your internet connection and restart app.").setNeutralButton("Ok", new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int which){
									dialog.dismiss();
									Intent i = new Intent(login_activity, LogIn.class);
									i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									login_activity.startActivity(i);
								}
							}).show();
						}
					});
				}else{
					writeToFile("Already Existing user");
					chkAll();
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		onActivityResult = true;
	}

	public static void openMainActivity(){
		Intent intent = new Intent(login_activity, MainActivity.class);
		login_activity.startActivity(intent);
		((LogIn)login_activity).finish();
	}

	private static void writeToFile(String data){
		try{
			File root = new File(Environment.getExternalStorageDirectory(), "CricDeCode");
			if(!root.exists()){
				root.mkdirs();
			}
			File gpxfile = new File(root, "login.txt");
			FileWriter writer = new FileWriter(gpxfile, true);
			writer.write(data + "\n");
			writer.flush();
			writer.close();
		}catch(IOException e){
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}
}
