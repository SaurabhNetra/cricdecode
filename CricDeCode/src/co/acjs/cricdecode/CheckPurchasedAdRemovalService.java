package co.acjs.cricdecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.google.ads.AdView;
import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

public class CheckPurchasedAdRemovalService extends IntentService {
	public static boolean started = true;
	public static Context who;
	public String orderId, token, sign;

	public CheckPurchasedAdRemovalService() {
		super("CheckPurchasedAdRemovalService");
	}
	
	private void writeToFile(String data){
		try{
			File root = new File(Environment.getExternalStorageDirectory(), "CricDeCode");
			if(!root.exists()){
				root.mkdirs();
			}
			File gpxfile = new File(root, "purchase.txt");
			FileWriter writer = new FileWriter(gpxfile, true);
			writer.write(data + "\n");
			writer.flush();
			writer.close();
		}catch(IOException e){
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		who = this;
		Log.w("CheckPurchasedAdRemovalService", "Started");
		writeToFile("chkAdRemoval service started");
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("CheckPurchasedAdRemovalService", "Ended");
		writeToFile("chkAdRemoval service ended");
	}
	public static String decrypt(String val1,String val2,String val3,String val4, String seq, int ci){
		String val=val2+val4+val1+val3;
		int num = val.length() / 10;
		char h[][] = new char[num+1][10];
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
				String n= new String(""+s[j]);
				int ind = Integer.parseInt(n);
				un[ind][i] = h[i][j];
				
			}
		}
		String dec="";
		for(int i=0;i<10;i++)
		{
			String n = new String(un[i]);
			dec=dec+n;
		}
		String ex= new String(h[num]);
		dec=dec+ex;
		char[] us=dec.toCharArray();
		char[] sh=new char[us.length];
		for(int i=0;i<us.length;i++)
		{
			sh[i]= (char)(us[i]-ci);
		}		
		return new String(sh);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		AccessSharedPrefs.mPrefs = getApplicationContext()
				.getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		StackMobAndroid.init(getApplicationContext(), 0, decrypt("00e65id7", "97:4fdeh","4d3f56i:",":06::h8<d05d", "7295013486", 3));
		try {
			JSONObject jn = new JSONObject(intent.getExtras().getString("json"));
			orderId = jn.getString("orderId");
			token = jn.getString("Token");
			sign = jn.getString("Sign");
			writeToFile("chkAdRemoval service "+jn.toString());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		writeToFile("chkAdRemoval service calling stackmob");
		ServerDBRemoveAds
				.query(ServerDBRemoveAds.class,
						new StackMobQuery()
								.field(new StackMobQueryField("user_id")
										.isEqualTo(AccessSharedPrefs.mPrefs
												.getString("id", "")))
								.field(new StackMobQueryField("order_id")
										.isEqualTo(orderId))
								.field(new StackMobQueryField("token")
										.isEqualTo(token))
								.field(new StackMobQueryField("sign")
										.isEqualTo(sign)),
						new StackMobQueryCallback<ServerDBRemoveAds>() {

							@Override
							public void failure(StackMobException arg0) {
								writeToFile("chkAdRemovalservice failure"+arg0);
							}

							@Override
							public void success(List<ServerDBRemoveAds> arg0) {
								writeToFile("chkAdRemovalservice success "+arg0.size());
								if (arg0.size() == 0) {
									AccessSharedPrefs.setString(who, "ad_free",
											"no");
									writeToFile("chkAdRemovalservice mark no");
									try {
										((MainActivity) MainActivity.main_context)
												.runOnUiThread(new Runnable() {
													public void run() {
														try {
															final AdView adView = (AdView) ((MainActivity) MainActivity.main_context)
																	.findViewById(R.id.adView);
															adView.setVisibility(View.VISIBLE);
															writeToFile("chkAdRemovalservice make visible");
														} catch (Exception e) {
															writeToFile("chkAdRemovalservice make visible catch");
														}
													}
												});
									} catch (Exception e) {
										writeToFile("chkAdRemovalservice outer catch");
									}

								} else {
									AccessSharedPrefs.setString(who, "ad_free",
											"yes");
									writeToFile("chkAdRemovalservice mark yes");
									try {
										((MainActivity) MainActivity.main_context)
												.runOnUiThread(new Runnable() {
													public void run() {
														try {
															final AdView adView = (AdView) ((MainActivity) MainActivity.main_context)
																	.findViewById(R.id.adView);
															adView.setVisibility(View.GONE);
														} catch (Exception e) {
															writeToFile("chkAdRemovalservice make gone");
														}
													}
												});
									} catch (Exception e) {
										writeToFile("chkAdRemovalservice outer catch2");
									}
								}

							}
						});
		writeToFile("chkAdRemoval service towards end");
		/*
		 * final JSONParser jsonParser = new JSONParser(); List<NameValuePair>
		 * params = new ArrayList<NameValuePair>(); params.add(new
		 * BasicNameValuePair("id", AccessSharedPrefs.mPrefs .getString("id",
		 * ""))); params.add(new BasicNameValuePair("json",
		 * intent.getExtras().getString( "json"))); JSONObject jn = null; jn =
		 * jsonParser.makeHttpRequest(
		 * getResources().getString(R.string.check_purchase_remove_ads), "POST",
		 * params, this); try { if (jn.getInt("status") == 1) {
		 * AccessSharedPrefs.setString(this, "ad_free", "no"); ((MainActivity)
		 * MainActivity.main_context) .runOnUiThread(new Runnable() { public
		 * void run() { try { final AdView adView = (AdView) ((MainActivity)
		 * MainActivity.main_context) .findViewById(R.id.adView);
		 * adView.setVisibility(View.INVISIBLE); } catch (Exception e) {
		 * 
		 * } } }); } } catch (Exception e) { }
		 */
	}


}