package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;


public class CheckPurchaseSync extends IntentService{
	public static boolean	started	= true;
	public static Context	who;
	public String			orderId, token, sign;

	public CheckPurchaseSync(){
		super("CheckPurchaseSyncService");
	}

	@Override
	public void onCreate(){
		super.onCreate();
		who = this;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
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
		StackMobAndroid.init(getApplicationContext(), 0, decrypt("00e65id7", "97:4fdeh", "4d3f56i:", ":06::h8<d05d", "7295013486", 3));
		try{
			JSONObject jn = new JSONObject(intent.getExtras().getString("json"));
			orderId = jn.getString("orderId");
			token = jn.getString("Token");
			sign = jn.getString("Sign");
		}catch(JSONException e1){
			e1.printStackTrace();
		}
		ServerDBSubSync.query(ServerDBSubSync.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBSubSync>(){
			@Override
			public void failure(StackMobException arg0){}

			@Override
			public void success(List<ServerDBSubSync> arg0){
				long now = new Date().getTime();
				if((arg0.size() > 0)){
					long t = arg0.get(0).getValidUntilTs();
					int m = 0;
					for(int i = 1; i < arg0.size(); i++){
						if(t < arg0.get(i).getValidUntilTs()){
							t = arg0.get(i).getValidUntilTs();
							m = i;
						}
					}
					final ServerDBSubSync max = arg0.get(m);
					if(now < arg0.get(m).getValidUntilTs()){
						AccessSharedPrefs.setString(who, "sync", "yes");
					}else{
						ServerDBAndroidDevices.query(ServerDBAndroidDevices.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBAndroidDevices>(){
							@Override
							public void failure(StackMobException arg0){}

							@Override
							public void success(List<ServerDBAndroidDevices> arg0){
								String regids = "";
								for(int i = 0; i < arg0.size(); i++){
									regids = regids + " " + arg0.get(i).getGcmId();
								}
								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("SendToArrays", regids));
								params.add(new BasicNameValuePair("product_id", "sub_infi_sync"));
								JSONObject jo = new JSONObject();
								try{
									jo.put("orderId", max.getOrderId());
									jo.put("Token", max.getToken());
									jo.put("Sign", max.getSign());
								}catch(JSONException e){}
								params.add(new BasicNameValuePair("json", jo.toString()));
								params.add(new BasicNameValuePair("id", AccessSharedPrefs.mPrefs.getString("id", "")));
								final JSONParser jsonParser = new JSONParser();
								int trial = 1;
								JSONObject jn = null;
								while(jsonParser.isOnline(who)){
									Log.w("JSONParser", "SubinfiSync:: Called");
									jn = jsonParser.makeHttpRequest(who.getResources().getString(R.string.purchase_infi_sync), "POST", params, who);
									Log.w("JSON returned", "SubinfiSync:: " + jn);
									Log.w("trial value", "SubinfiSync:: " + trial);
									if(jn != null) break;
									try{
										Thread.sleep(10 * trial);
									}catch(InterruptedException e){}
									trial++;
									if(trial == 50) break;
								}
								try{
									if(jn.getInt("status") == 1){
										AccessSharedPrefs.setString(who, "sync", "yes");
										try{
											((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
												public void run(){
													try{
														((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.sync_pur)).setVisibility(View.VISIBLE);
													}catch(Exception e){}
												}
											});
										}catch(Exception e){}
									}else{
										AccessSharedPrefs.setString(who, "sync", "no");
										try{
											((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
												public void run(){
													try{
														((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.sync_pur)).setVisibility(View.GONE);
													}catch(Exception e){}
												}
											});
										}catch(Exception e){}
									}
								}catch(NullPointerException e){}catch(JSONException e){
									e.printStackTrace();
								}
							}
						});
					}
					Log.w("LoginIn", "sub_infi_chk success!! 2");
				}else{
					AccessSharedPrefs.setString(who, "sync", "no");
				}
			}
		});
		/* final JSONParser jsonParser = new JSONParser(); List<NameValuePair> params = new ArrayList<NameValuePair>(); params.add(new BasicNameValuePair("id", AccessSharedPrefs.mPrefs .getString("id", ""))); params.add(new BasicNameValuePair("json", intent.getExtras().getString( "json"))); JSONObject jn = null; jn = jsonParser.makeHttpRequest( getResources().getString(R.string.check_purchase_infi_sync), "POST", params, this); try { if (jn.getInt("status") == 1) { AccessSharedPrefs.setString(this, "infi_sync", "no"); } } catch (Exception e) { } */
	}
}
