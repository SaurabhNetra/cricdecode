package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;


public class PurchasedInfiService extends IntentService{
	public static boolean	started	= true;
	public Context			con;

	public PurchasedInfiService(){
		super("PurchasedInfiService");
	}

	@Override
	public void onCreate(){
		super.onCreate();
		Log.w("PurchasedInfiService", "Started");
		con = this;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.w("PurchasedInfiService", "Ended");
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
		StackMobAndroid.init(getApplicationContext(), 1, decrypt("5g28><6hi=2", "26j6jff", "29>5h;<=8>", "f8=f=if5", "6103927458", 5));
		AccessSharedPrefs.mPrefs = getApplicationContext().getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		if(AccessSharedPrefs.mPrefs.getString("PurchasedInfiServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED)){
			ServerDBAndroidDevices.query(ServerDBAndroidDevices.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBAndroidDevices>(){
				@Override
				public void failure(StackMobException arg0){}

				@Override
				public void success(List<ServerDBAndroidDevices> arg0){
					try{
						final JSONParser jsonParser = new JSONParser();
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("id", AccessSharedPrefs.mPrefs.getString("id", "")));
						String regids = "";
						for(int i = 0; i < arg0.size(); i++){
							regids = regids + " " + arg0.get(i).getGcmId();
						}
						params.add(new BasicNameValuePair("SendToArrays", regids));
						params.add(new BasicNameValuePair("product_id", "sub_infi"));
						params.add(new BasicNameValuePair("json", AccessSharedPrefs.mPrefs.getString("pur_infi_data", "")));
						int trial = 1;
						JSONObject jn = null;
						while(jsonParser.isOnline(con)){
							jn = jsonParser.makeHttpRequest(getResources().getString(R.string.purchase_infi), "POST", params, con);
							Log.w("JSON returned", "PurchasedInfiServiceService: " + jn);
							Log.w("trial value", "PurchasedInfiServiceService: " + trial);
							if(jn != null) break;
							try{
								Thread.sleep(10 * trial);
							}catch(InterruptedException e){}
							trial++;
							if(trial == 50){
								break;
							}
						}
						try{
							if(jn.getInt("status") == 1){
								AccessSharedPrefs.setString(con, "PurchasedInfiServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
								AccessSharedPrefs.setString(con, "pur_infi_data", "");
								AccessSharedPrefs.setString(con, "infi_use", "yes");
								try{
									((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
										public void run(){
											try{
												((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.infi_pur)).setVisibility(View.VISIBLE);
												((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.infi_pur)).setText("Purchased");
												((LinearLayout)((MainActivity)MainActivity.main_context).findViewById(R.id.pur_subscribe_sync)).setVisibility(View.VISIBLE);
												((LinearLayout)((MainActivity)MainActivity.main_context).findViewById(R.id.pur_subscribe_infi_sync)).setVisibility(View.GONE);
											}catch(Exception e){}
										}
									});
								}catch(Exception e){}
							}
							if(jn.getInt("status") == 0){
								AccessSharedPrefs.setString(con, "PurchasedInfiServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
								AccessSharedPrefs.setString(con, "pur_infi_data", "");
								AccessSharedPrefs.setString(con, "infi_use", "no");
								try{
									((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
										public void run(){
											try{
												((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.infi_pur)).setVisibility(View.GONE);
											}catch(Exception e){}
										}
									});
								}catch(Exception e){}
							}
						}catch(Exception e){}
					}catch(Exception e){}
				}
			});
		}
	}
}
