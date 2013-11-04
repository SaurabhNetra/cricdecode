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
import com.stackmob.sdk.callback.StackMobCallback;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;


public class MatchHistorySyncService extends IntentService{
	public static boolean	started				= true;
	public static Context	who;
	static int				tot_per				= 0;
	static int				tot_mat_per			= 0;
	static int				cnt_tot_per			= 0;
	static int				cnt_tot_mat_per		= 0;
	static String			deviceId;
	ArrayList<String>		match_id_arr		= new ArrayList<String>();
	ArrayList<String>		performance_id_arr	= new ArrayList<String>();

	public MatchHistorySyncService(){
		super("MatchCreateService");
	}

	@Override
	public void onCreate(){
		super.onCreate();
		who = this;
		Log.w("MatchCreateService", "Started");
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.w("MatchCreateService", "Ended");
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

	public void sendData(){
		final Cursor c = getContentResolver().query(CricDeCodeContentProvider.CONTENT_URI_MATCH, new String[ ]{MatchDb.KEY_ROWID, MatchDb.KEY_DEVICE_ID, MatchDb.KEY_MATCH_DATE, MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM, MatchDb.KEY_VENUE, MatchDb.KEY_OVERS, MatchDb.KEY_INNINGS, MatchDb.KEY_RESULT, MatchDb.KEY_LEVEL, MatchDb.KEY_FIRST_ACTION, MatchDb.KEY_DURATION, MatchDb.KEY_REVIEW, MatchDb.KEY_STATUS, MatchDb.KEY_SYNCED}, MatchDb.KEY_SYNCED + "=" + "0 and " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null, MatchDb.KEY_ROWID);
		final Cursor t = getContentResolver().query(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE, new String[ ]{PerformanceDb.KEY_ROWID, PerformanceDb.KEY_BAT_BALLS, PerformanceDb.KEY_BAT_BOWLER_TYPE, PerformanceDb.KEY_BAT_CHANCES, PerformanceDb.KEY_BAT_FIELDING_POSITION, PerformanceDb.KEY_BAT_FOURS, PerformanceDb.KEY_BAT_HOW_OUT, PerformanceDb.KEY_BAT_NUM, PerformanceDb.KEY_BAT_RUNS, PerformanceDb.KEY_BAT_SIXES, PerformanceDb.KEY_BAT_TIME, PerformanceDb.KEY_BOWL_BALLS, PerformanceDb.KEY_BOWL_CATCHES_DROPPED, PerformanceDb.KEY_BOWL_FOURS, PerformanceDb.KEY_BOWL_MAIDENS, PerformanceDb.KEY_BOWL_NOBALLS, PerformanceDb.KEY_BOWL_RUNS, PerformanceDb.KEY_BOWL_SIXES, PerformanceDb.KEY_BOWL_SPELLS, PerformanceDb.KEY_BOWL_WIDES, PerformanceDb.KEY_BOWL_WKTS_LEFT, PerformanceDb.KEY_BOWL_WKTS_RIGHT, PerformanceDb.KEY_FIELD_BYES, PerformanceDb.KEY_FIELD_CATCHES_DROPPED, PerformanceDb.KEY_FIELD_CIRCLE_CATCH, PerformanceDb.KEY_FIELD_CLOSE_CATCH, PerformanceDb.KEY_FIELD_DEEP_CATCH, PerformanceDb.KEY_FIELD_MISFIELDS, PerformanceDb.KEY_FIELD_RO_CIRCLE, PerformanceDb.KEY_FIELD_RO_DEEP, PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE, PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP, PerformanceDb.KEY_FIELD_SLIP_CATCH, PerformanceDb.KEY_FIELD_STUMPINGS, PerformanceDb.KEY_INNING, PerformanceDb.KEY_MATCHID, PerformanceDb.KEY_STATUS}, PerformanceDb.KEY_SYNCED + "=" + "0 and " + PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null, PerformanceDb.KEY_MATCHID);
		settotPerformance(t.getCount());
		Log.w("MatchHistorySync", "Number of Un Synced Matches " + c.getCount());
		Log.w("MatchHistorySync", "Number of Un Synced Performances " + t.getCount());
		if(c.getCount() != 0){
			c.moveToFirst();
			do{
				ServerDBCricketMatch cm = new ServerDBCricketMatch(AccessSharedPrefs.mPrefs.getString("id", ""), Integer.parseInt(c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_ROWID))), Integer.parseInt(AccessSharedPrefs.mPrefs.getString("device_id", "")), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_MATCH_DATE)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_MY_TEAM)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_OPPONENT_TEAM)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_VENUE)), Integer.parseInt(c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_OVERS))), Integer.parseInt(c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_INNINGS))), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_RESULT)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_LEVEL)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_FIRST_ACTION)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_DURATION)), c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_REVIEW)), 0);
				cm.setID(AccessSharedPrefs.mPrefs.getString("device_id", "") + "A" + c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_ROWID)) + "B" + AccessSharedPrefs.mPrefs.getString("id", ""));
				Log.w("MatchHistorySync", "Saving Match");
				cm.save(new StackMobCallback(){
					String	matchId	= c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_ROWID));
					String	devid	= c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID));

					@Override
					public void failure(StackMobException arg0){
						Log.w("MatchHistorySync", "Match Sync Failure" + arg0);
					}

					@Override
					public void success(String arg0){
						final Cursor c1 = getContentResolver().query(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE, new String[ ]{PerformanceDb.KEY_ROWID, PerformanceDb.KEY_BAT_BALLS, PerformanceDb.KEY_BAT_BOWLER_TYPE, PerformanceDb.KEY_BAT_CHANCES, PerformanceDb.KEY_BAT_FIELDING_POSITION, PerformanceDb.KEY_BAT_FOURS, PerformanceDb.KEY_BAT_HOW_OUT, PerformanceDb.KEY_BAT_NUM, PerformanceDb.KEY_BAT_RUNS, PerformanceDb.KEY_BAT_SIXES, PerformanceDb.KEY_BAT_TIME, PerformanceDb.KEY_BOWL_BALLS, PerformanceDb.KEY_BOWL_CATCHES_DROPPED, PerformanceDb.KEY_BOWL_FOURS, PerformanceDb.KEY_BOWL_MAIDENS, PerformanceDb.KEY_BOWL_NOBALLS, PerformanceDb.KEY_BOWL_RUNS, PerformanceDb.KEY_BOWL_SIXES, PerformanceDb.KEY_BOWL_SPELLS, PerformanceDb.KEY_BOWL_WIDES, PerformanceDb.KEY_BOWL_WKTS_LEFT, PerformanceDb.KEY_BOWL_WKTS_RIGHT, PerformanceDb.KEY_FIELD_BYES, PerformanceDb.KEY_FIELD_CATCHES_DROPPED, PerformanceDb.KEY_FIELD_CIRCLE_CATCH, PerformanceDb.KEY_FIELD_CLOSE_CATCH, PerformanceDb.KEY_FIELD_DEEP_CATCH, PerformanceDb.KEY_FIELD_MISFIELDS, PerformanceDb.KEY_FIELD_RO_CIRCLE, PerformanceDb.KEY_FIELD_RO_DEEP, PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE, PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP, PerformanceDb.KEY_FIELD_SLIP_CATCH, PerformanceDb.KEY_FIELD_STUMPINGS, PerformanceDb.KEY_INNING, PerformanceDb.KEY_MATCHID, PerformanceDb.KEY_STATUS}, PerformanceDb.KEY_SYNCED + "=" + "0 and " + PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "' and " + PerformanceDb.KEY_MATCHID + "= " + matchId + " and " + PerformanceDb.KEY_DEVICE_ID + "= '" + devid + "'", null, null);
						Log.w("MatchHistorySync", "Current Performances " + c1.getCount());
						setMatchPerformance(c1.getCount());
						if(c1.getCount() != 0){
							c1.moveToFirst();
							do{
								Log.w("MatchHistorySync", "Saving Performance");
								ServerDBPerformance sp = new ServerDBPerformance(AccessSharedPrefs.mPrefs.getString("id", ""), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID))), Integer.parseInt(AccessSharedPrefs.mPrefs.getString("device_id", "")), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_ROWID))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_INNING))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_NUM))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_RUNS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BALLS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_TIME))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FOURS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_SIXES))), c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_HOW_OUT)), c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BOWLER_TYPE)), c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FIELDING_POSITION)), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_CHANCES))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_BALLS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SPELLS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_MAIDENS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_RUNS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_FOURS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SIXES))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_LEFT))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_RIGHT))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_CATCHES_DROPPED))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_NOBALLS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WIDES))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_SLIP_CATCH))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CLOSE_CATCH))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CIRCLE_CATCH))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_DEEP_CATCH))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_CIRCLE))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DEEP))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_STUMPINGS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_BYES))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_MISFIELDS))), Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CATCHES_DROPPED))), 0);
								// Set The StackMob Primary Key ID
								sp.setID(AccessSharedPrefs.mPrefs.getString("device_id", "") + "A" + c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID)) + "B" + c1.getString(c1.getColumnIndexOrThrow(PerformanceDb.KEY_INNING)) + "C" + AccessSharedPrefs.mPrefs.getString("id", ""));
								sp.save(new StackMobCallback(){
									@Override
									public void failure(StackMobException arg0){
										Log.w("MatchHistorySync", "Performance Sync Failure");
									}

									@Override
									public void success(String arg0){
										Log.w("MatchHistorySync", "Performance Sync Update Success");
										Log.w("inv", "cnt mat per" + cnt_tot_mat_per + " tot" + tot_mat_per);
										if(cnt_MatchPerformance() == getMatchPerformance()){
											init_cnt_MatchPerformance();
											Log.w("MatchHistorySync", "Performance Sync Done");
										}
										Log.w("inv", "cnt per" + cnt_tot_per + " tot" + tot_per);
										if(cnt_totPerformance() == gettotPerformance()){
											init_cnt_TotPerformance();
											Log.w("MatchHistorySync", "Performance Sync Update Success ALL");
											ServerDBAndroidDevices.query(ServerDBAndroidDevices.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBAndroidDevices>(){
												@Override
												public void failure(StackMobException arg0){
													Log.w("MatchHistory", "Failure" + arg0);
												}

												public void changedSynced(Cursor c2){
													c2.moveToFirst();
													do{
														Log.w("MatchHistory", "jn==11");
														// Log.w("Marking Synced",""+c2.getString(c2.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID))+" "+c2.getString(c2.getColumnIndexOrThrow(PerformanceDb.KEY_DEVICE_ID)));
														Log.w("MatchHistory", "jn==12");
														try{
															Log.w("MatchHistory", "interim");
															Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_ROWID)) + "/" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
															ContentValues matchvalues = new ContentValues();
															Log.w("MatchHistory", "jn==13");
															matchvalues.put(MatchDb.KEY_SYNCED, 1);
															Log.w("MatchHistory", "jn==14");
															getApplicationContext().getContentResolver().update(uri, matchvalues, null, null);
															Log.w("MatchHistory", "jn==15");
															uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_ROWID)) + "/" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
															ContentValues values = new ContentValues();
															Log.w("MatchHistory", "jn==16" + uri.toString());
															values.put(PerformanceDb.KEY_SYNCED, 1);
															getApplicationContext().getContentResolver().update(uri, values, null, null);
														}catch(Exception e){
															Log.w("exception", "" + e);
														}
														c2.moveToNext();
													}while(!c2.isAfterLast());
													c2.close();
													AccessSharedPrefs.setString(who, "MatchHistorySyncServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
												}

												@Override
												public void success(List<ServerDBAndroidDevices> arg0){
													Log.w("MatchHistorySync", "GCM Ids fetched" + arg0.size());
													String regids = "";
													for(int i = 0; i < arg0.size(); i++){
														regids = regids + " " + arg0.get(i).getGcmId();
													}
													final Cursor c2 = getContentResolver().query(CricDeCodeContentProvider.CONTENT_URI_MATCH, new String[ ]{MatchDb.KEY_ROWID, MatchDb.KEY_DEVICE_ID, MatchDb.KEY_MATCH_DATE, MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM, MatchDb.KEY_VENUE, MatchDb.KEY_OVERS, MatchDb.KEY_INNINGS, MatchDb.KEY_RESULT, MatchDb.KEY_LEVEL, MatchDb.KEY_FIRST_ACTION, MatchDb.KEY_DURATION, MatchDb.KEY_REVIEW, MatchDb.KEY_STATUS, MatchDb.KEY_SYNCED}, MatchDb.KEY_SYNCED + "=" + "0 and " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null, MatchDb.KEY_ROWID);
													JSONObject j = new JSONObject();
													try{
														if(c2.getCount() != 0){
															j.put("gcmid", "2");
															JSONArray ja = new JSONArray();
															c2.moveToFirst();
															do{
																JSONObject jo = new JSONObject();
																jo.put("mid", "" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_ROWID)));
																jo.put("dev", "" + c2.getString(c2.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
																ja.put(jo);
																c2.moveToNext();
															}while(!c2.isAfterLast());
															j.put("matches", ja);
														}
													}catch(JSONException e){}
													Log.w("Json tosend", "" + j.toString());
													List<NameValuePair> params = new ArrayList<NameValuePair>();
													params.add(new BasicNameValuePair("SendToArrays", regids));
													params.add(new BasicNameValuePair("MsgToSend", j.toString()));
													params.add(new BasicNameValuePair("uid", AccessSharedPrefs.mPrefs.getString("id", "")));
													final JSONParser jsonParser = new JSONParser();
													Log.w("Sending User Data...", "MatchHistory:" + jsonParser.isOnline(who));
													int trial = 1;
													JSONObject jn = null;
													while(jsonParser.isOnline(who)){
														Log.w("JSONParser", "MatchHistory: Called");
														jn = jsonParser.makeHttpRequest(getResources().getString(R.string.edit_profile_sync), "POST", params, who);
														Log.w("JSON returned", "MatchHistory: " + jn);
														Log.w("trial value", "MatchHistory: " + trial);
														if(jn != null) break;
														try{
															Thread.sleep(10 * trial);
														}catch(InterruptedException e){}
														trial++;
													}
													try{
														if(jn.getInt("status") == 1){
															Log.w("MatchHistory", "jn==1");
															changedSynced(c2);
														}else{
															Log.w("MatchHistory", "jn!=1");
														}
													}catch(NullPointerException e){
														Log.w("exception", "np");
													}catch(JSONException e){
														Log.w("exception", "json");
													}
												}
											});
										}
									}
								});
								c1.moveToNext();
							}while(!c1.isAfterLast());
						}
						c1.close();
					}
				});
				c.moveToNext();
			}while(!c.isAfterLast());
		}
		c.close();
	}

	@Override
	protected void onHandleIntent(Intent intent){
		StackMobAndroid.init(getApplicationContext(), 1, decrypt("5g28><6hi=2", "26j6jff", "29>5h;<=8>", "f8=f=if5", "6103927458", 5));
		if(AccessSharedPrefs.mPrefs.getString("MatchHistorySyncServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED)){
			if(AccessSharedPrefs.mPrefs.getString("infi_sync", "no").equals("yes")){
				ServerDBSubInfiSync.query(ServerDBSubInfiSync.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBSubInfiSync>(){
					@Override
					public void failure(StackMobException arg0){}

					@Override
					public void success(List<ServerDBSubInfiSync> arg0){
						long now = new Date().getTime();
						if((arg0.size() > 0)){
							long t1 = arg0.get(0).getValidUntilTs();
							int m = 0;
							for(int i = 1; i < arg0.size(); i++){
								if(t1 < arg0.get(i).getValidUntilTs()){
									t1 = arg0.get(i).getValidUntilTs();
									m = i;
								}
							}
							final ServerDBSubInfiSync max = arg0.get(m);
							if(now < arg0.get(m).getValidUntilTs()){
								AccessSharedPrefs.setString(who, "infi_sync", "yes");
								sendData();
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
												AccessSharedPrefs.setString(who, "infi_sync", "yes");
												sendData();
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
			}else if(AccessSharedPrefs.mPrefs.getString("sync", "no").equals("yes")){
				ServerDBSubSync.query(ServerDBSubSync.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBSubSync>(){
					@Override
					public void failure(StackMobException arg0){}

					@Override
					public void success(List<ServerDBSubSync> arg0){
						long now = new Date().getTime();
						if((arg0.size() > 0)){
							long t1 = arg0.get(0).getValidUntilTs();
							int m = 0;
							for(int i = 1; i < arg0.size(); i++){
								if(t1 < arg0.get(i).getValidUntilTs()){
									t1 = arg0.get(i).getValidUntilTs();
									m = i;
								}
							}
							final ServerDBSubSync max = arg0.get(m);
							if(now < arg0.get(m).getValidUntilTs()){
								AccessSharedPrefs.setString(who, "infi_sync", "yes");
								sendData();
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
											Log.w("JSONParser", "SubSync:: Called");
											jn = jsonParser.makeHttpRequest(who.getResources().getString(R.string.purchase_infi_sync), "POST", params, who);
											Log.w("JSON returned", "SubSync:: " + jn);
											Log.w("trial value", "SubSync:: " + trial);
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
												sendData();
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
			}else{
				AccessSharedPrefs.setString(who, "MatchHistorySyncServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
			}
		}
	}

	static void setMatchPerformance(int t){
		tot_mat_per = t;
	}

	static void settotPerformance(int t){
		tot_per = t;
	}

	static int cnt_totPerformance(){
		cnt_tot_per++;
		Log.w("MatchHistorySync", "cnt_tot_per" + cnt_tot_per);
		return cnt_tot_per;
	}

	static int gettotPerformance(){
		return tot_per;
	}

	static int cnt_MatchPerformance(){
		cnt_tot_mat_per++;
		Log.w("MatchHistorySync", "cnt_tot_mat_per" + cnt_tot_mat_per);
		return cnt_tot_mat_per;
	}

	static int getMatchPerformance(){
		return tot_mat_per;
	}

	static void init_cnt_MatchPerformance(){
		tot_mat_per = 0;
		cnt_tot_mat_per = 0;
	}

	static void init_cnt_TotPerformance(){
		tot_per = 0;
		cnt_tot_per = 0;
	}
	/*
	 * private void writeToFile(String data) { try { File root = new
	 * File(Environment.getExternalStorageDirectory(), "CricDeCode"); if
	 * (!root.exists()) { root.mkdirs(); } File gpxfile = new File(root,
	 * "log.txt"); FileWriter writer = new FileWriter(gpxfile);
	 * writer.write(data); writer.flush(); writer.close(); } catch (IOException
	 * e) { Log.e("Exception", "File write failed: " + e.toString()); } }
	 */
}
