package co.acjs.cricdecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;


public class PurchasedAdRemovalService extends IntentService{
	public static boolean	started	= true;
	public Context			con;

	public PurchasedAdRemovalService(){
		super("PurchasedAdRemovalService");
	}

	@Override
	public void onCreate(){
		super.onCreate();
		Log.w("PurchasedAdRemovalService", "Started");
		writeToFile("PurchasedAdRemovalService Started");
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.w("PurchasedAdRemovalService", "Ended");
		writeToFile("PurchasedAdRemovalService Ended");
	}

	@Override
	protected void onHandleIntent(Intent intent){
		AccessSharedPrefs.mPrefs = getApplicationContext().getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		if(AccessSharedPrefs.mPrefs.getString("PurchaseAdRemovalServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED)){
			ServerDBAndroidDevices.query(ServerDBAndroidDevices.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBAndroidDevices>(){
				@Override
				public void failure(StackMobException arg0){
					// TODO Auto-generated method stub
				}

				@Override
				public void success(List<ServerDBAndroidDevices> arg0){
					String regids = "";
					for(int i = 0; i < arg0.size(); i++){
						regids = regids + " " + arg0.get(i).getGcmId();
					}
					final JSONParser jsonParser = new JSONParser();
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("id", AccessSharedPrefs.mPrefs.getString("id", "")));
					params.add(new BasicNameValuePair("product_id", "ad_removal"));
					params.add(new BasicNameValuePair("SendToArrays", regids));
					params.add(new BasicNameValuePair("json", AccessSharedPrefs.mPrefs.getString("pur_ad_data", "")));
					Log.w("Sending User Data...", "PurchaseAdRemovalServiceCalled:" + jsonParser.isOnline(con));
					writeToFile("Sending User Data...PurchaseAdRemovalServiceCalled:");
					int trial = 1;
					JSONObject jn = null;
					while(jsonParser.isOnline(con)){
						jn = jsonParser.makeHttpRequest(getResources().getString(R.string.purchase_remove_ads_sync), "POST", params, con);
						Log.w("JSON returned", "PurchasedAdRemovalService: " + jn);
						Log.w("trial value", "PurchasedAdRemovalService: " + trial);
						if(jn != null) break;
						try{
							Thread.sleep(10 * trial);
						}catch(InterruptedException e){}
						trial++;
					}
					try{
						Log.w("PurchaseAdRemovalServiceCalled", "Reply" + jn);
						writeToFile("PurchaseAdRemovalServiceCalled Reply " + jn.toString());
						if(jn.getInt("status") == 1){
							AccessSharedPrefs.setString(con, "PurchaseAdRemovalServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
							AccessSharedPrefs.setString(con, "pur_ad_data", "");
						}
					}catch(Exception e){}
				}
			});
		}
	}

	private void writeToFile(String data){
		try{
			File root = new File(Environment.getExternalStorageDirectory(), "CricDeCode");
			if(!root.exists()){
				root.mkdirs();
			}
			File gpxfile = new File(root, "debug.txt");
			FileWriter writer = new FileWriter(gpxfile, true);
			writer.write(data);
			writer.flush();
			writer.close();
		}catch(IOException e){
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}
}
