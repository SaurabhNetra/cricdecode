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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;


public class GCMSyncService extends IntentService{
	public static Context	who;
	static int				tot_mat	= 0;
	static int				cnt_mat	= 0;

	public GCMSyncService(){
		super("GCMSyncService");
	}

	@Override
	public void onCreate(){
		super.onCreate();
		who = this;
		StackMobAndroid.init(getApplicationContext(), 1, decrypt("5g28><6hi=2", "26j6jff", "29>5h;<=8>", "f8=f=if5", "6103927458", 5));
		Log.w("GCMSyncService", "Started");
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.w("GCMSyncService", "Ended");
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
	
	void getGCMData()
	{

		JSONArray ja = null;
		int in = 0;
		try{
			ja = (new JSONObject(AccessSharedPrefs.mPrefs.getString("GCMMatchData", ""))).getJSONArray("matches");
			tot_mat = ja.length();
			for(in = 0; in < ja.length(); in++){
				JSONObject json = ja.getJSONObject(in);
				Log.w("GCMSync", "" + json.toString());
				ServerDBCricketMatch.query(ServerDBCricketMatch.class, new StackMobQuery().field(new StackMobQueryField("match_id").isEqualTo(Integer.parseInt(json.getString("mid")))).field(new StackMobQueryField("device_id").isEqualTo(Integer.parseInt(json.getString("dev")))).field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))).field(new StackMobQueryField("status").isEqualTo(0)), new StackMobQueryCallback<ServerDBCricketMatch>(){
					@Override
					public void failure(StackMobException arg0){
						Log.w("GCMSync Cricket Match", "failure");
					}

					@Override
					public void success(List<ServerDBCricketMatch> arg0){
						final int device_id = arg0.get(0).getDeviceId(), match_id = arg0.get(0).getMatchId();
						Log.w("GCMSync", "Cricket Match " + arg0.get(0).getDeviceId() + " " + arg0.get(0).getMatchId());
						ContentValues values = new ContentValues();
						values.put(MatchDb.KEY_ROWID, arg0.get(0).getMatchId());
						values.put(MatchDb.KEY_DEVICE_ID, "" + arg0.get(0).getDeviceId());
						values.put(MatchDb.KEY_MATCH_DATE, arg0.get(0).getMatchDate());
						values.put(MatchDb.KEY_MY_TEAM, arg0.get(0).getMyTeam());
						values.put(MatchDb.KEY_OPPONENT_TEAM, arg0.get(0).getOpponentTeam());
						values.put(MatchDb.KEY_VENUE, arg0.get(0).getVenue());
						values.put(MatchDb.KEY_OVERS, arg0.get(0).getOvers());
						values.put(MatchDb.KEY_INNINGS, arg0.get(0).getInnings());
						values.put(MatchDb.KEY_RESULT, arg0.get(0).getResult());
						values.put(MatchDb.KEY_LEVEL, arg0.get(0).getLevel());
						values.put(MatchDb.KEY_FIRST_ACTION, arg0.get(0).getFirstAction());
						values.put(MatchDb.KEY_DURATION, arg0.get(0).getDuration());
						values.put(MatchDb.KEY_REVIEW, arg0.get(0).getReview());
						values.put(MatchDb.KEY_STATUS, MatchDb.MATCH_HOLD);
						values.put(MatchDb.KEY_SYNCED, 1);
						// insert a record
						// Sheetal Test This
						Cursor c = MainActivity.dbHandle.rawQuery("select " + MatchDb.KEY_ROWID + " from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_ROWID + " = " + arg0.get(0).getMatchId() + " and " + MatchDb.KEY_DEVICE_ID + " = '" + arg0.get(0).getDeviceId() + "'", null);
					
						if(c.getCount() == 0){
							getApplicationContext().getContentResolver().insert(CricDeCodeContentProvider.CONTENT_URI_MATCH, values);
						}
						c.close();
						ServerDBPerformance.query(ServerDBPerformance.class, new StackMobQuery().field(new StackMobQueryField("match_id").isEqualTo(arg0.get(0).getMatchId())).field(new StackMobQueryField("device_id").isEqualTo(arg0.get(0).getDeviceId())).field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))).field(new StackMobQueryField("status").isEqualTo(0)), new StackMobQueryCallback<ServerDBPerformance>(){
							int	m_id	= match_id;
							int	d_id	= device_id;

							@Override
							public void failure(StackMobException arg0){
								Log.w("Sync failure", "nfjf");
							}

							@Override
							public void success(List<ServerDBPerformance> arg0){
								for(int i = 0; i < arg0.size(); i++){
									Log.w("GCMSync", "Performance Data" + i + " " + arg0.get(i).getDeviceId() + " " + arg0.get(i).getMatchId());
									ContentValues value = new ContentValues();
									value.put(PerformanceDb.KEY_MATCHID, arg0.get(i).getMatchId());
									value.put(PerformanceDb.KEY_DEVICE_ID, arg0.get(i).getDeviceId());
									value.put(PerformanceDb.KEY_ROWID, arg0.get(i).getPerId());
									value.put(PerformanceDb.KEY_INNING, arg0.get(i).getInning());
									value.put(PerformanceDb.KEY_BAT_NUM, arg0.get(i).getBatNum());
									value.put(PerformanceDb.KEY_BAT_RUNS, arg0.get(i).getBatRuns());
									value.put(PerformanceDb.KEY_BAT_BALLS, arg0.get(i).getBatBalls());
									value.put(PerformanceDb.KEY_BAT_TIME, arg0.get(i).getBatTime());
									value.put(PerformanceDb.KEY_BAT_FOURS, arg0.get(i).getBatFours());
									value.put(PerformanceDb.KEY_BAT_SIXES, arg0.get(i).getBatSixes());
									value.put(PerformanceDb.KEY_BAT_HOW_OUT, arg0.get(i).getBatDismissal());
									value.put(PerformanceDb.KEY_BAT_BOWLER_TYPE, arg0.get(i).getBatBowlerType());
									value.put(PerformanceDb.KEY_BAT_FIELDING_POSITION, arg0.get(i).getBatFieldingPosition());
									value.put(PerformanceDb.KEY_BAT_CHANCES, arg0.get(i).getBatChances());
									value.put(PerformanceDb.KEY_BOWL_BALLS, arg0.get(i).getBowlBalls());
									value.put(PerformanceDb.KEY_BOWL_SPELLS, arg0.get(i).getBowlSpells());
									value.put(PerformanceDb.KEY_BOWL_MAIDENS, arg0.get(i).getBowlMaidens());
									value.put(PerformanceDb.KEY_BOWL_RUNS, arg0.get(i).getBowlRuns());
									value.put(PerformanceDb.KEY_BOWL_FOURS, arg0.get(i).getBowlFours());
									value.put(PerformanceDb.KEY_BOWL_SIXES, arg0.get(i).getBowlSixes());
									value.put(PerformanceDb.KEY_BOWL_WKTS_LEFT, arg0.get(i).getBowlWktsLeft());
									value.put(PerformanceDb.KEY_BOWL_WKTS_RIGHT, arg0.get(i).getBowlWktsRight());
									value.put(PerformanceDb.KEY_BOWL_CATCHES_DROPPED, arg0.get(i).getCatchedDropped());
									value.put(PerformanceDb.KEY_BOWL_NOBALLS, arg0.get(i).getNoBalls());
									value.put(PerformanceDb.KEY_BOWL_WIDES, arg0.get(i).getBowlWides());
									value.put(PerformanceDb.KEY_FIELD_SLIP_CATCH, arg0.get(i).getFieldSlipCatch());
									value.put(PerformanceDb.KEY_FIELD_CLOSE_CATCH, arg0.get(i).getFieldCloseCatch());
									value.put(PerformanceDb.KEY_FIELD_CIRCLE_CATCH, arg0.get(i).getFieldCircleCatch());
									value.put(PerformanceDb.KEY_FIELD_DEEP_CATCH, arg0.get(i).getFieldDeepCatch());
									value.put(PerformanceDb.KEY_FIELD_RO_CIRCLE, arg0.get(i).getFieldRoCircle());
									value.put(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE, arg0.get(i).getFieldRoDirectCircle());
									value.put(PerformanceDb.KEY_FIELD_RO_DEEP, arg0.get(i).getFieldRoDeep());
									value.put(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP, arg0.get(i).getFieldRoDirectDeep());
									value.put(PerformanceDb.KEY_FIELD_STUMPINGS, arg0.get(i).getFieldStumping());
									value.put(PerformanceDb.KEY_FIELD_BYES, arg0.get(i).getFieldByes());
									value.put(PerformanceDb.KEY_FIELD_MISFIELDS, arg0.get(i).getMisFields());
									value.put(PerformanceDb.KEY_FIELD_CATCHES_DROPPED, arg0.get(i).getCatchedDropped());
									value.put(PerformanceDb.KEY_SYNCED, 1);
									value.put(PerformanceDb.KEY_STATUS, MatchDb.MATCH_HISTORY);
									// Sheetal Test
									Cursor c = MainActivity.dbHandle.rawQuery("select " + PerformanceDb.KEY_ROWID + " from " + PerformanceDb.SQLITE_TABLE + " where " + PerformanceDb.KEY_MATCHID + " = " + arg0.get(i).getMatchId() + " and " + PerformanceDb.KEY_DEVICE_ID + " = '" + arg0.get(i).getDeviceId() + "' and " + PerformanceDb.KEY_INNING + " = " + arg0.get(i).getInning(), null);
									
									if(c.getCount() == 0){
										getApplicationContext().getContentResolver().insert(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE, value);
									}
									c.close();
								}
								// Update status of match to history again
								Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/" + m_id + "/" + d_id);
								ContentValues matchvalues = new ContentValues();
								matchvalues.put(MatchDb.KEY_STATUS, MatchDb.MATCH_HISTORY);
								// update a record
								getApplicationContext().getContentResolver().update(uri, matchvalues, null, null);
								Log.w("log", "jfkjf " + cnt_mat + " " + tot_mat);
								if(cnt_Match() == gettotMAtch()){
									Log.w("ASP", "" + AccessSharedPrefs.mPrefs.getString("GCMMatchData", ""));
									AccessSharedPrefs.setString(who, "GCMMatchData", "");
									Log.w("ASP", "fg" + AccessSharedPrefs.mPrefs.getString("GCMMatchData", ""));
								}
								Log.w("log", "jfkjf12");
							}
						});
					}
				});
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		Log.w("GCMSync", "Updating UI" + in + " " + ja.length());
		try{
			((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
				public void run(){
					try{
						DiaryMatchesFragment.loader_diary_list.restartLoader(0, null, DiaryMatchesFragment.diary_matches_fragment);
					}catch(Exception e){}
				}
			});
		}catch(Exception e){
			Log.w("GCMSync", "");
		}
	
	}


	@Override
	protected void onHandleIntent(Intent intent){
		StackMobAndroid.init(getApplicationContext(), 0, decrypt("5d00e6", "97:4fde", "5id74d3f56i", "h:06::h8<d0", "7295013486", 3));
		AccessSharedPrefs.mPrefs = getApplicationContext().getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		Log.w("GCMSync", "fff");
		if(AccessSharedPrefs.mPrefs.getString("GCMSyncServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED)){
			
			if(AccessSharedPrefs.mPrefs.getString("infi_sync", "no").equals("yes"))
			{
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
							getGCMData();
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
										if(jn.getInt("status") == 1){
											AccessSharedPrefs.setString(who, "infi_sync", "yes");
											getGCMData();
										}else{
											AccessSharedPrefs.setString(who, "infi_sync", "no");
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
		}
		}
		else if(AccessSharedPrefs.mPrefs.getString("sync", "no").equals("yes"))
		{

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
							getGCMData();
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
									params.add(new BasicNameValuePair("product_id", "sub_sync"));
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
										if(jn.getInt("status") == 1){
											AccessSharedPrefs.setString(who, "sync", "yes");
											getGCMData();
										}else{
											AccessSharedPrefs.setString(who, "sync", "no");
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
		
		}
		else
		{
			AccessSharedPrefs.setString(who, "GCMSyncServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
		}
	}

	static int gettotMAtch(){
		return tot_mat;
	}

	static int cnt_Match(){
		cnt_mat++;
		return cnt_mat;
	}
}
