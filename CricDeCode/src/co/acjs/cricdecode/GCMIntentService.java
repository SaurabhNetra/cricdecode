package co.acjs.cricdecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.ads.AdView;


public class GCMIntentService extends GCMBaseIntentServiceCompat{
	public static final int	UPDATE_PROFILE_DATA			= 1;
	public static final int	MATCH_N_PERFORMANCE_DATA	= 2;
	public static final int	DELETE_MATCH				= 3;
	public static final int	REMOVE_ADS					= 4;
	public static final int	SUB_INFI					= 5;
	public static final int	SUB_INFI_SYNC				= 6;
	public static final int	NO_REMOVE_ADS				= 7;
	public static final int	NO_SUB_INFI					= 8;
	public static final int	NO_SUB_INFI_SYNC			= 9;
	public static final int	SUB_SYNC					= 10;
	public static final int	NO_SUB_SYNC					= 11;
	public static Context	context;

	public GCMIntentService(){
		super("GCMIntentService");
		context = this;
	}
	
	private void writeToFile(String data){
		try{
			File root = new File(Environment.getExternalStorageDirectory(), "CricDeCode");
			if(!root.exists()){
				root.mkdirs();
			}
			File gpxfile = new File(root, "gcm.txt");
			FileWriter writer = new FileWriter(gpxfile, true);
			writer.write(data + "\n");
			writer.flush();
			writer.close();
		}catch(IOException e){
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	@Override
	protected void onMessage(Intent message){
		AccessSharedPrefs.mPrefs = context.getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		final String gcmString = message.getStringExtra("cricdecode");
		try{
			writeToFile(""+gcmString);
			Log.w("GCM Received", "GCMData: " + gcmString.toString());
			String s = gcmString.toString();
			s = s.replace("\\", "");
			JSONObject gcmData = new JSONObject(s);
			switch(gcmData.getInt("gcmid")){
				case UPDATE_PROFILE_DATA:
					AccessSharedPrefs.setString(context, "nickname", gcmData.getString("nickname"));
					AccessSharedPrefs.setString(context, "role", gcmData.getString("role"));
					AccessSharedPrefs.setString(context, "battingStyle", gcmData.getString("battingStyle"));
					AccessSharedPrefs.setString(context, "bowlingStyle", gcmData.getString("bowlingStyle"));
					Log.w("Updating profile data", "with gcm data");
					try{
						((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
							public void run(){
								try{
									((TextView)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.role)).setText(AccessSharedPrefs.mPrefs.getString("role", ""));
									((TextView)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.nickname)).setText(AccessSharedPrefs.mPrefs.getString("nickname", ""));
									((TextView)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.batting_style)).setText(AccessSharedPrefs.mPrefs.getString("battingStyle", ""));
									((TextView)((SherlockFragmentActivity)MainActivity.main_context).findViewById(R.id.bowling_style)).setText(AccessSharedPrefs.mPrefs.getString("bowlingStyle", ""));
								}catch(Exception e){}
							}
						});
					}catch(Exception e){}
					break;
				case MATCH_N_PERFORMANCE_DATA:
					Log.w("Match and Per Sync", "GCM");
					if(AccessSharedPrefs.mPrefs.getString("infi_sync", "no").equals("yes") || AccessSharedPrefs.mPrefs.getString("sync", "no").equals("yes")){
						if(!AccessSharedPrefs.mPrefs.getString("GCMMatchData", "").equals("")){
							JSONArray ja_d = (new JSONObject(AccessSharedPrefs.mPrefs.getString("GCMMatchData", ""))).getJSONArray("matches");
							JSONArray ja_s = gcmData.getJSONArray("matches");
							for(int i = 0; i < ja_s.length(); i++){
								ja_d.put(ja_s.get(i));
							}
							JSONObject jo = new JSONObject();
							jo.put("matches", ja_d);
							AccessSharedPrefs.setString(context, "GCMMatchData", jo.toString());
						}else{
							JSONObject jo = new JSONObject();
							jo.put("matches", gcmData.getJSONArray("matches"));
							AccessSharedPrefs.setString(context, "GCMMatchData", jo.toString());
						}
						Log.w("Match and Per Sync", "starting service: " + AccessSharedPrefs.mPrefs.getString("GCMMatchData", ""));
						AccessSharedPrefs.setString(context, "GCMSyncServiceCalled", CDCAppClass.NEEDS_TO_BE_CALLED);
						startService(new Intent(this, GCMSyncService.class));
					}
					break;
				case DELETE_MATCH:
					JSONArray ja2 = gcmData.getJSONArray("todelete");
					Log.w("GCM delete Match", "with gcm data");
					if(AccessSharedPrefs.mPrefs.getString("infi_sync", "no").equals("yes") || AccessSharedPrefs.mPrefs.getString("sync", "no").equals("yes")){
						for(int i = 0; i < ja2.length(); i++){
							JSONObject jo = ja2.getJSONObject(i);
							String str = jo.getString("mid");
							String d_str = jo.getString("dev");
							Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/" + str + "/" + d_str);
							getApplicationContext().getContentResolver().delete(uri, null, null);
							uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/" + str + "/" + d_str);
							getApplicationContext().getContentResolver().delete(uri, null, null);
						}
						try{
							((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
								public void run(){
									try{
										DiaryMatchesFragment.loader_diary_list.restartLoader(0, null, DiaryMatchesFragment.diary_matches_fragment);
									}catch(Exception e){
										Log.w("GCMSync", "UI update error" + e);
									}
								}
							});
						}catch(Exception e){
							Log.w("GCMSync", "UI update error" + e);
						}
					}
					break;
				case REMOVE_ADS:
					Log.w("GCM: ", "gcm remove ads");
					AccessSharedPrefs.setString(this, "ad_free", "yes");
					try{
						((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
							public void run(){
								try{
									final AdView adView = (AdView)((MainActivity)MainActivity.main_context).findViewById(R.id.adView);
									adView.setVisibility(View.GONE);
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.rem_ads_pur)).setVisibility(View.VISIBLE);
								}catch(Exception e){}
							}
						});
					}catch(Exception e){}
					break;
				case SUB_INFI:
					Log.w("GCM: ", "gcm infi use");
					AccessSharedPrefs.setString(this, "infi_use", "yes");
					try{
						((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
							public void run(){
								try{
									((LinearLayout)((MainActivity)MainActivity.main_context).findViewById(R.id.pur_subscribe_sync)).setVisibility(View.VISIBLE);
									((LinearLayout)((MainActivity)MainActivity.main_context).findViewById(R.id.pur_subscribe_infi_sync)).setVisibility(View.GONE);
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.infi_pur)).setVisibility(View.VISIBLE);
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.infi_pur)).setText("Purchased");
								}catch(Exception e){}
							}
						});
					}catch(Exception e){}
					break;
				case SUB_INFI_SYNC:
					Log.w("GCM: ", "gcm infi sync");
					AccessSharedPrefs.setString(this, "infi_sync", "yes");
					try{
						((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
							public void run(){
								try{
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.infi_sync_pur)).setVisibility(View.VISIBLE);
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.infi_pur)).setText("Not Applicable");
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.infi_pur)).setVisibility(View.VISIBLE);
								}catch(Exception e){}
							}
						});
					}catch(Exception e){}
					break;
				case NO_REMOVE_ADS:
					Log.w("GCM: ", "gcm no remove ads");
					AccessSharedPrefs.setString(this, "ad_free", "no");
					try{
						((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
							public void run(){
								try{
									final AdView adView = (AdView)((MainActivity)MainActivity.main_context).findViewById(R.id.adView);
									adView.setVisibility(View.VISIBLE);
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.rem_ads_pur)).setVisibility(View.GONE);
								}catch(Exception e){}
							}
						});
					}catch(Exception e){}
					break;
				case NO_SUB_INFI:
					Log.w("GCM: ", "gcm no infi use");
					AccessSharedPrefs.setString(this, "infi_use", "no");
					try{
						((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
							public void run(){
								try{
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.infi_pur)).setVisibility(View.GONE);
								}catch(Exception e){}
							}
						});
					}catch(Exception e){}
					break;
				case NO_SUB_INFI_SYNC:
					Log.w("GCM: ", "gcm no infi sync");
					AccessSharedPrefs.setString(this, "infi_sync", "no");
					try{
						((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
							public void run(){
								try{
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.infi_sync_pur)).setVisibility(View.GONE);
								}catch(Exception e){}
							}
						});
					}catch(Exception e){}
					break;
				case NO_SUB_SYNC:
					Log.w("GCM: ", "gcm no sync");
					AccessSharedPrefs.setString(this, "sync", "no");
					try{
						((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
							public void run(){
								try{
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.sync_pur)).setVisibility(View.GONE);
								}catch(Exception e){}
							}
						});
					}catch(Exception e){}
					break;
				case SUB_SYNC:
					Log.w("GCM: ", "gcm sync");
					AccessSharedPrefs.setString(this, "sync", "yes");
					try{
						((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
							public void run(){
								try{
									((TextView)((MainActivity)MainActivity.main_context).findViewById(R.id.sync_pur)).setVisibility(View.VISIBLE);
								}catch(Exception e){}
							}
						});
					}catch(Exception e){}
					break;
			}
		}catch(JSONException e){
			Log.w("Json exception", "ex in gcm" + e);
		}
	}

	@Override
	protected void onError(Intent message){
		dumpEvent("onError", message);
	}

	@Override
	protected void onDeleted(Intent message){
		dumpEvent("onDeleted", message);
	}

	private void dumpEvent(String event, Intent message){
		Bundle extras = message.getExtras();
		for(String key: extras.keySet()){
			Log.d(getClass().getSimpleName(), String.format("%s: %s=%s", event, key, extras.getString(key)));
		}
	}
}
