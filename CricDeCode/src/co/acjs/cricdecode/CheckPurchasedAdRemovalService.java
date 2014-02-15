package co.acjs.cricdecode;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.*;
import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;


public class CheckPurchasedAdRemovalService extends IntentService{
	public static boolean	started	= true;
	public static Context	who;
	public String			orderId, token, sign;

	public CheckPurchasedAdRemovalService(){
		super("CheckPurchasedAdRemovalService");
	}

	@Override
	public void onCreate(){
		super.onCreate();
		who = this;
		Log.w("CheckPurchasedAdRemovalService", "Started");
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.w("CheckPurchasedAdRemovalService", "Ended");
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

	@Override
	protected void onHandleIntent(Intent intent){
		AccessSharedPrefs.mPrefs = getApplicationContext().getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		StackMobAndroid.init(getApplicationContext(), 1, decrypt("5g28><6hi=2", "26j6jff", "29>5h;<=8>", "f8=f=if5", "6103927458", 5));
		try{
			JSONObject jn = new JSONObject(intent.getExtras().getString("json"));
			orderId = jn.getString("orderId");
			token = jn.getString("Token");
			sign = jn.getString("Sign");
		}catch(JSONException e1){
			e1.printStackTrace();
		}
		ServerDBRemoveAds.query(ServerDBRemoveAds.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))).field(new StackMobQueryField("order_id").isEqualTo(orderId)).field(new StackMobQueryField("token").isEqualTo(token)).field(new StackMobQueryField("sign").isEqualTo(sign)), new StackMobQueryCallback<ServerDBRemoveAds>(){
			@Override
			public void failure(StackMobException arg0){}

			@Override
			public void success(List<ServerDBRemoveAds> arg0){
				if(arg0.size() == 0){
					AccessSharedPrefs.setString(who, "ad_free", "no");
					try{
						((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
							public void run(){
								try{
									final AdView adView = (AdView)((MainActivity)MainActivity.main_context).findViewById(R.id.adView);
								    adView.setAdUnitId(R.string.publisher_id+"");
								    adView.setAdSize(AdSize.BANNER);
									adView.setVisibility(View.VISIBLE);
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.rem_ads_pur)).setVisibility(View.GONE);
								}catch(Exception e){}
							}
						});
					}catch(Exception e){}
				}else{
					AccessSharedPrefs.setString(who, "ad_free", "yes");
					try{
						((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
							public void run(){
								try{
									final AdView adView = (AdView)((MainActivity)MainActivity.main_context).findViewById(R.id.adView);
								    adView.setAdUnitId(R.string.publisher_id+"");
								    adView.setAdSize(AdSize.BANNER);
									adView.setVisibility(View.GONE);
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.rem_ads_pur)).setVisibility(View.VISIBLE);
								}catch(Exception e){}
							}
						});
					}catch(Exception e){}
				}
			}
		});
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
