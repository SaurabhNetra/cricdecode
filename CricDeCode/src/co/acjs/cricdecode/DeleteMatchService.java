package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobCallback;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;


public class DeleteMatchService extends IntentService{
	public static boolean	started	= true;
	public static Context	who;
	// static List<ServerDBCricketMatch> match_list = new
	// ArrayList<ServerDBCricketMatch>();
	static int				delete_match_count, match_deleted, delete_performance_count, performance_deleted;
	static boolean			all_match_done	= false, all_performance_done = false;

	// static List<ServerDBPerformance> performance_list = new
	// ArrayList<ServerDBPerformance>();
	public DeleteMatchService(){
		super("DeleteMatchService");
	}

	@Override
	public void onCreate(){
		super.onCreate();
		Log.w("DeleteMatchService", "Started");
		who = this;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.w("DeleteMatchService", "Ended");
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

	public void deleteData(){
		final Cursor c = getContentResolver().query(CricDeCodeContentProvider.CONTENT_URI_MATCH, new String[ ]{MatchDb.KEY_ROWID, MatchDb.KEY_DEVICE_ID, MatchDb.KEY_MATCH_DATE, MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM, MatchDb.KEY_VENUE, MatchDb.KEY_OVERS, MatchDb.KEY_INNINGS, MatchDb.KEY_RESULT, MatchDb.KEY_LEVEL, MatchDb.KEY_FIRST_ACTION, MatchDb.KEY_DURATION, MatchDb.KEY_REVIEW, MatchDb.KEY_STATUS, MatchDb.KEY_SYNCED}, MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_DELETED + "'", null, MatchDb.KEY_ROWID);
		final Cursor c2 = getContentResolver().query(CricDeCodeContentProvider.CONTENT_URI_MATCH, new String[ ]{MatchDb.KEY_ROWID, MatchDb.KEY_DEVICE_ID, MatchDb.KEY_MATCH_DATE, MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM, MatchDb.KEY_VENUE, MatchDb.KEY_OVERS, MatchDb.KEY_INNINGS, MatchDb.KEY_RESULT, MatchDb.KEY_LEVEL, MatchDb.KEY_FIRST_ACTION, MatchDb.KEY_DURATION, MatchDb.KEY_REVIEW, MatchDb.KEY_STATUS, MatchDb.KEY_SYNCED}, MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_DELETED + "'", null, MatchDb.KEY_ROWID);
		Log.w("Debug", "Matches to be deleted" + c.getCount());
		if(c.getCount() != 0){
			delete_match_count = c.getCount();
			c.moveToFirst();
			do{
				// Delete By ID
				ServerDBCricketMatch cm = new ServerDBCricketMatch(AccessSharedPrefs.mPrefs.getString("id", ""), Integer.parseInt(c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_ROWID))), Integer.parseInt(c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID))), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_MATCH_DATE)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_MY_TEAM)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_OPPONENT_TEAM)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_VENUE)), Integer.parseInt(c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_OVERS))), Integer.parseInt(c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_INNINGS))), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_RESULT)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_LEVEL)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_FIRST_ACTION)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_DURATION)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_REVIEW)), 1);
				// Set The StackMob Primary Key ID
				cm.setID(c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)) + "A" + c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_ROWID)) + "B" + AccessSharedPrefs.mPrefs.getString("id", ""));
				cm.setStatus(1);
				cm.save(new StackMobCallback(){
					String	match_id	= c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_ROWID));
					String	device_id	= c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID));

					@Override
					public void success(String arg0){
						match_deleted++;
						Log.w("Match Delete", "success");
						// Delete That Match
						Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/" + match_id + "/" + device_id);
						getContentResolver().delete(uri, null, null);
						if(match_deleted == delete_match_count){
							all_match_done = true;
						}
						if(all_match_done && all_performance_done){
							Log.w("Debug", "Deleted Success ALL");
							ServerDBAndroidDevices.query(ServerDBAndroidDevices.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBAndroidDevices>(){
								@Override
								public void failure(StackMobException arg0){}

								@Override
								public void success(List<ServerDBAndroidDevices> arg0){
									Log.w("DeleteMatch", "GCM Ids fetched" + arg0.size());
									String regids = "";
									for(int i = 0; i < arg0.size(); i++){
										regids = regids + " " + arg0.get(i).getGcmId();
									}
									JSONObject j = new JSONObject();
									try{
										if(c2.getCount() != 0){
											j.put("gcmid", "3");
											JSONArray ja = new JSONArray();
											c2.moveToFirst();
											do{
												JSONObject jo = new JSONObject();
												jo.put("mid", "" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_ROWID)));
												jo.put("dev", "" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
												ja.put(jo);
												c2.moveToNext();
											}while(!c2.isAfterLast());
											j.put("todelete", ja);
										}
									}catch(JSONException e){}
									Log.w("Json tosend", "" + j.toString());
									List<NameValuePair> params = new ArrayList<NameValuePair>();
									params.add(new BasicNameValuePair("SendToArrays", regids));
									params.add(new BasicNameValuePair("MsgToSend", j.toString()));
									params.add(new BasicNameValuePair("uid", AccessSharedPrefs.mPrefs.getString("id", "")));
									final JSONParser jsonParser = new JSONParser();
									Log.w("Sending User Data...", "DeleteMatch:" + jsonParser.isOnline(who));
									int trial = 1;
									JSONObject jn = null;
									while(jsonParser.isOnline(who)){
										Log.w("JSONParser", "DeleteMatch:: Called");
										jn = jsonParser.makeHttpRequest(getResources().getString(R.string.edit_profile_sync), "POST", params, who);
										Log.w("JSON returned", "DeleteMatch:: " + jn);
										Log.w("trial value", "DeleteMatch:: " + trial);
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
											c2.moveToFirst();
											do{
												Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_ROWID)) + "/" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
												getApplicationContext().getContentResolver().delete(uri, null, null);
												uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_ROWID)) + "/" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
												getApplicationContext().getContentResolver().delete(uri, null, null);
												c2.moveToNext();
											}while(!c2.isAfterLast());
											c2.close();
											AccessSharedPrefs.setString(who, "DeleteMatchServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
										}
									}catch(NullPointerException e){}catch(JSONException e){
										e.printStackTrace();
									}
								}
							});
						}
					}

					@Override
					public void failure(StackMobException arg0){
						Log.w("Performance Delete", "failure");
					}
				});
				c.moveToNext();
			}while(!c.isAfterLast());
		}
		c.close();
		Log.w("prog", "1");
		final Cursor c1 = getContentResolver().query(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE, new String[ ]{PerformanceDb.KEY_MATCHID, PerformanceDb.KEY_DEVICE_ID, PerformanceDb.KEY_INNING, PerformanceDb.KEY_ROWID, PerformanceDb.KEY_BAT_BALLS, PerformanceDb.KEY_BAT_BOWLER_TYPE, PerformanceDb.KEY_BAT_CHANCES, PerformanceDb.KEY_BAT_FIELDING_POSITION, PerformanceDb.KEY_BAT_FOURS, PerformanceDb.KEY_BAT_HOW_OUT, PerformanceDb.KEY_BAT_NUM, PerformanceDb.KEY_BAT_RUNS, PerformanceDb.KEY_BAT_SIXES, PerformanceDb.KEY_BAT_TIME, PerformanceDb.KEY_BOWL_BALLS, PerformanceDb.KEY_BOWL_CATCHES_DROPPED, PerformanceDb.KEY_BOWL_FOURS, PerformanceDb.KEY_BOWL_MAIDENS, PerformanceDb.KEY_BOWL_NOBALLS, PerformanceDb.KEY_BOWL_RUNS, PerformanceDb.KEY_BOWL_SIXES, PerformanceDb.KEY_BOWL_SPELLS, PerformanceDb.KEY_BOWL_WIDES, PerformanceDb.KEY_BOWL_WKTS_LEFT, PerformanceDb.KEY_BOWL_WKTS_RIGHT, PerformanceDb.KEY_FIELD_BYES, PerformanceDb.KEY_FIELD_CATCHES_DROPPED, PerformanceDb.KEY_FIELD_CIRCLE_CATCH, PerformanceDb.KEY_FIELD_CLOSE_CATCH, PerformanceDb.KEY_FIELD_DEEP_CATCH, PerformanceDb.KEY_FIELD_MISFIELDS, PerformanceDb.KEY_FIELD_RO_CIRCLE, PerformanceDb.KEY_FIELD_RO_DEEP, PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE, PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP, PerformanceDb.KEY_FIELD_SLIP_CATCH, PerformanceDb.KEY_FIELD_STUMPINGS, PerformanceDb.KEY_INNING, PerformanceDb.KEY_MATCHID, PerformanceDb.KEY_STATUS}, PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_DELETED + "'", null, PerformanceDb.KEY_MATCHID);
		if(c1.getCount() != 0){
			Log.w("prog", "2");
			delete_performance_count = c1.getCount();
			c1.moveToFirst();
			do{
				Log.w("prog", "3");
				ServerDBPerformance sp = new ServerDBPerformance(AccessSharedPrefs.mPrefs.getString("id", ""), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID))), Integer.parseInt(AccessSharedPrefs.mPrefs.getString("device_id", "")), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_ROWID))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_INNING))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_NUM))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_RUNS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BALLS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_TIME))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FOURS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_SIXES))), c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_HOW_OUT)), c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BOWLER_TYPE)), c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FIELDING_POSITION)), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_CHANCES))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_BALLS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SPELLS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_MAIDENS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_RUNS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_FOURS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SIXES))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_LEFT))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_RIGHT))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_CATCHES_DROPPED))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_NOBALLS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WIDES))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_SLIP_CATCH))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CLOSE_CATCH))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CIRCLE_CATCH))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_DEEP_CATCH))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_CIRCLE))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DEEP))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_STUMPINGS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_BYES))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_MISFIELDS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CATCHES_DROPPED))), 1);
				// Set The StackMob Primary Key ID
				sp.setID(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_DEVICE_ID)) + "A" + c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID)) + "B" + c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_INNING)) + "C" + AccessSharedPrefs.mPrefs.getString("id", ""));
				sp.setStatus(1);
				sp.save(new StackMobCallback(){
					String	match_id	= c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID));
					String	device_id	= c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_DEVICE_ID));

					@Override
					public void success(String arg0){
						performance_deleted++;
						Log.w("Performance Delete", "success");
						// Delete The Performance
						if(performance_deleted == delete_performance_count){
							all_performance_done = true;
						}
						if(all_match_done && all_performance_done){
							Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/" + match_id + "/" + device_id);
							getContentResolver().delete(uri, null, null);
							Log.w("Debug", "Deleted Success ALL");
							ServerDBAndroidDevices.query(ServerDBAndroidDevices.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBAndroidDevices>(){
								@Override
								public void failure(StackMobException arg0){}

								@Override
								public void success(List<ServerDBAndroidDevices> arg0){
									Log.w("DeleteMatch", "GCM Ids fetched" + arg0.size());
									String regids = "";
									for(int i = 0; i < arg0.size(); i++){
										regids = regids + " " + arg0.get(i).getGcmId();
									}
									JSONObject j = new JSONObject();
									try{
										if(c2.getCount() != 0){
											j.put("gcmid", "3");
											JSONArray ja = new JSONArray();
											c2.moveToFirst();
											do{
												JSONObject jo = new JSONObject();
												jo.put("mid", "" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_ROWID)));
												jo.put("dev", "" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
												ja.put(jo);
												c2.moveToNext();
											}while(!c2.isAfterLast());
											j.put("todelete", ja);
										}
									}catch(JSONException e){}
									Log.w("Json tosend", "" + j.toString());
									List<NameValuePair> params = new ArrayList<NameValuePair>();
									params.add(new BasicNameValuePair("SendToArrays", regids));
									params.add(new BasicNameValuePair("MsgToSend", j.toString()));
									params.add(new BasicNameValuePair("uid", AccessSharedPrefs.mPrefs.getString("id", "")));
									final JSONParser jsonParser = new JSONParser();
									Log.w("Sending User Data...", "DeleteMatch:" + jsonParser.isOnline(who));
									int trial = 1;
									JSONObject jn = null;
									while(jsonParser.isOnline(who)){
										Log.w("JSONParser", "DeleteMatch:: Called");
										jn = jsonParser.makeHttpRequest(getResources().getString(R.string.edit_profile_sync), "POST", params, who);
										Log.w("JSON returned", "DeleteMatch:: " + jn);
										Log.w("trial value", "DeleteMatch:: " + trial);
										if(jn != null) break;
										try{
											Thread.sleep(10 * trial);
										}catch(InterruptedException e){}
										trial++;
									}
									try{
										if(jn.getInt("status") == 1){
											c2.moveToFirst();
											do{
												Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_ROWID)) + "/" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
												getApplicationContext().getContentResolver().delete(uri, null, null);
												uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_ROWID)) + "/" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
												getApplicationContext().getContentResolver().delete(uri, null, null);
												c2.moveToNext();
											}while(!c2.isAfterLast());
											c2.close();
											AccessSharedPrefs.setString(who, "DeleteMatchServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
										}
									}catch(NullPointerException e){}catch(JSONException e){
										e.printStackTrace();
									}
								}
							});
						}
					}

					@Override
					public void failure(StackMobException arg0){
						Log.w("Performance Delete", "fialure");
					}
				});
				c1.moveToNext();
			}while(!c1.isAfterLast());
		}
		c1.close();
		try{
			((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
				public void run(){
					try{
						DiaryMatchesFragment.loader_diary_list.restartLoader(0, null, DiaryMatchesFragment.diary_matches_fragment);
					}catch(Exception e){}
				}
			});
		}catch(Exception e){}
	}

	@Override
	protected void onHandleIntent(Intent intent){
		StackMobAndroid.init(getApplicationContext(), 1, decrypt("5g28><6hi=2", "26j6jff", "29>5h;<=8>", "f8=f=if5", "6103927458", 5));
		Log.w("DeleteMatchService: ", "DeleteMatchService: " + AccessSharedPrefs.mPrefs.getString("DeleteMatchServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED));
		if(AccessSharedPrefs.mPrefs.getString("DeleteMatchServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED)){
			Log.w("DeleteMatchService", "polo");
			if(AccessSharedPrefs.mPrefs.getString("infi_sync", "no").equals("yes")){
				ServerDBSubInfiSync.query(ServerDBSubInfiSync.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBSubInfiSync>(){
					@Override
					public void failure(StackMobException arg0){}

					@Override
					public void success(List<ServerDBSubInfiSync> arg0){
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
							final ServerDBSubInfiSync max = arg0.get(m);
							if(now < arg0.get(m).getValidUntilTs()){
								AccessSharedPrefs.setString(who, "infi_sync", "yes");
								deleteData();
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
										List<NameValuePair> params1 = new ArrayList<NameValuePair>();
										params1.add(new BasicNameValuePair("user_id", AccessSharedPrefs.mPrefs.getString("id", "")));
										params1.add(new BasicNameValuePair("json", jo.toString()));
										params1.add(new BasicNameValuePair("filname", "DeleteMatch-InfiSync"));
										final JSONParser jsonParser = new JSONParser();
										int trial = 1;
										JSONObject jn = null;
										while(jsonParser.isOnline(who)){
											Log.w("JSONParser", "SubinfiSync:: Called");
											jn = jsonParser.makeHttpRequest(who.getResources().getString(R.string.purchase_infi), "POST", params, who);
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
											if(jn != null){
												params1.add(new BasicNameValuePair("jn", "" + jn.toString()));
											}else{
												params1.add(new BasicNameValuePair("jn", "null"));
											}
											params1.add(new BasicNameValuePair("trial", "" + trial));
											jsonParser.makeHttpRequest(getResources().getString(R.string.send_mail), "POST", params1, who);
										}catch(Exception e){}
										try{
											if(jn.getInt("status") == 1){
												AccessSharedPrefs.setString(who, "infi_sync", "yes");
												deleteData();
											}else if(jn.getInt("status") == 0){
												AccessSharedPrefs.setString(who, "infi_sync", "no");
												AccessSharedPrefs.setString(who, "DeleteMatchServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
											}
										}catch(NullPointerException e){}catch(JSONException e){
											e.printStackTrace();
										}
									}
								});
							}
						}else{
							AccessSharedPrefs.setString(who, "infi_sync", "no");
						}
					}
				});
			}else if(AccessSharedPrefs.mPrefs.getString("sync", "no").equals("yes")){
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
								AccessSharedPrefs.setString(who, "infi_sync", "yes");
								deleteData();
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
										List<NameValuePair> params1 = new ArrayList<NameValuePair>();
										params1.add(new BasicNameValuePair("user_id", AccessSharedPrefs.mPrefs.getString("id", "")));
										params1.add(new BasicNameValuePair("json", jo.toString()));
										params1.add(new BasicNameValuePair("filname", "DeleteMatch-Sync"));
										final JSONParser jsonParser = new JSONParser();
										int trial = 1;
										JSONObject jn = null;
										while(jsonParser.isOnline(who)){
											Log.w("JSONParser", "SubinfiSync:: Called");
											jn = jsonParser.makeHttpRequest(who.getResources().getString(R.string.purchase_infi), "POST", params, who);
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
											if(jn != null){
												params1.add(new BasicNameValuePair("jn", "" + jn.toString()));
											}else{
												params1.add(new BasicNameValuePair("jn", "null"));
											}
											params1.add(new BasicNameValuePair("trial", "" + trial));
											jsonParser.makeHttpRequest(getResources().getString(R.string.send_mail), "POST", params1, who);
										}catch(Exception e){}
										try{
											if(jn.getInt("status") == 1){
												AccessSharedPrefs.setString(who, "sync", "yes");
												deleteData();
											}else if(jn.getInt("status") == 0){
												AccessSharedPrefs.setString(who, "sync", "no");
												AccessSharedPrefs.setString(who, "DeleteMatchServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
											}
										}catch(NullPointerException e){}catch(JSONException e){
											e.printStackTrace();
										}
									}
								});
							}
						}else{
							AccessSharedPrefs.setString(who, "sync", "no");
						}
					}
				});
			}else{
				AccessSharedPrefs.setString(who, "DeleteMatchServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
			}
		}
	}
}
