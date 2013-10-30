package co.acjs.cricdecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;


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
		writeToFile("PurchasedInfiService Started");
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.w("PurchasedInfiService", "Ended");
		writeToFile("PurchasedInfiService Ended");
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
	protected void onHandleIntent(Intent intent){
		StackMobAndroid.init(getApplicationContext(), 0, decrypt("00e65id7", "97:4fdeh","4d3f56i:",":06::h8<d05d", "7295013486", 3));
		AccessSharedPrefs.mPrefs = getApplicationContext().getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		if(AccessSharedPrefs.mPrefs.getString("PurchasedInfiServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED)){
			
			writeToFile("PurchasedInfiService android devices calling");
			ServerDBAndroidDevices.query(ServerDBAndroidDevices.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBAndroidDevices>(){
				@Override
				public void failure(StackMobException arg0){
					writeToFile("PurchasedInfiService android devices failure "+arg0);
				}

				@Override
				public void success(List<ServerDBAndroidDevices> arg0){
					writeToFile("PurchasedInfiService android devices success ");
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
					Log.w("Sending User Data...", "ProfileEditService:" + jsonParser.isOnline(con));
					writeToFile("Sending User Data...ProfileEditService:");
					int trial = 1;
					JSONObject jn = null;
					writeToFile("PurchasedInfiService http to "+getResources().getString(R.string.purchase_infi));
					while(jsonParser.isOnline(con)){
						
						jn = jsonParser.makeHttpRequest(getResources().getString(R.string.purchase_infi), "POST", params, con);
						Log.w("JSON returned", "PurchasedInfiServiceService: " + jn);
						Log.w("trial value", "PurchasedInfiServiceService: " + trial);
						writeToFile("PurchasedInfiService "+jn+ " "+trial);
						if(jn != null) break;
						try{
							Thread.sleep(10 * trial);
						}catch(InterruptedException e){}
						trial++;
					}
					try{
						writeToFile("PurchasedInfiService Reply");
						if(jn.getInt("status") == 1){
							writeToFile("PurchasedInfiService mark yes");
							AccessSharedPrefs.setString(con, "PurchasedInfiServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
							AccessSharedPrefs.setString(con, "pur_infi_data", "");
							AccessSharedPrefs.setString(con, "infi_use", "yes");
						}
						if(jn.getInt("status") == 0)
						{
							writeToFile("PurchasedInfiService mark no");
							AccessSharedPrefs.setString(con, "PurchasedInfiServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
							AccessSharedPrefs.setString(con, "pur_infi_data", "");
							AccessSharedPrefs.setString(con, "infi_use", "no");
						}
					}catch(Exception e){}
				}
			});
			writeToFile("PurchasedInfiService towards end");
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
