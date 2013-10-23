package co.acjs.cricdecode;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class GCMSyncService extends IntentService{
	public static Context	who;
	static int				tot_mat			= 0;
	static int				cnt_mat			= 0;

	public GCMSyncService(){
		super("GCMSyncService");
	}

	@Override
	public void onCreate(){
		super.onCreate();
		who = this;
		StackMobAndroid.init(who, 0, "c52a9f47-baae-41e3-aa63-72177b0c23f7");
		Log.w("GCMSyncService", "Started");
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.w("GCMSyncService", "Ended");
	}

	@Override
	protected void onHandleIntent(Intent intent){
		AccessSharedPrefs.mPrefs = getApplicationContext().getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		Log.w("GCMSync", "fff");
		if(AccessSharedPrefs.mPrefs.getString("GCMSyncServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED)){
			JSONArray ja=null;
			int in=0;
			try{
				ja = (new JSONObject(AccessSharedPrefs.mPrefs.getString("GCMMatchData", ""))).getJSONArray("matches");
				tot_mat =ja.length();
				for(in= 0; in < ja.length(); in++){
					JSONObject jo = ja.getJSONObject(in);
					Log.w("GCMSync", "" + jo.toString());
					ServerDBCricketMatch.query(ServerDBCricketMatch.class, new StackMobQuery().field(new StackMobQueryField("match_id").isEqualTo(Integer.parseInt(jo.getString("mid")))).field(new StackMobQueryField("device_id").isEqualTo(Integer.parseInt(jo.getString("dev")))).field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBCricketMatch>(){
						@Override
						public void failure(StackMobException arg0){
							Log.w("GCMSync Cricket Match","failure");
						}

						@Override
						public void success(List<ServerDBCricketMatch> arg0){
							Log.w("GCMSync", "Cricket Match "+arg0.get(0).getDeviceId()+" "+arg0.get(0).getMatchId());
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
							values.put(MatchDb.KEY_STATUS, MatchDb.MATCH_HISTORY);
							values.put(MatchDb.KEY_SYNCED, 1);
							// insert a record
							getApplicationContext().getContentResolver().insert(CricDeCodeContentProvider.CONTENT_URI_MATCH, values);
							ServerDBPerformance.query(ServerDBPerformance.class, new StackMobQuery().field(new StackMobQueryField("match_id").isEqualTo(arg0.get(0).getMatchId())).field(new StackMobQueryField("device_id").isEqualTo(arg0.get(0).getDeviceId())).field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))), new StackMobQueryCallback<ServerDBPerformance>(){
								
								@Override
								public void failure(StackMobException arg0){
									Log.w("Sync failure","nfjf");
								}

								@Override
								public void success(List<ServerDBPerformance> arg0){
									
									for(int i = 0; i < arg0.size(); i++){
										Log.w("GCMSync", "Performance Data" + i+" "+arg0.get(i).getDeviceId()+" "+arg0.get(i).getMatchId());
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
										getApplicationContext().getContentResolver().insert(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE, value);

										Log.w("log","jff");
									
									}
									Log.w("log","jfkjf "+cnt_mat+ " "+tot_mat);
									if(cnt_Match()==gettotMAtch())
									{
										Log.w("ASP", "" + AccessSharedPrefs.mPrefs.getString("GCMMatchData", ""));
										AccessSharedPrefs.setString(who, "GCMMatchData", "");
										Log.w("ASP", "fg" + AccessSharedPrefs.mPrefs.getString("GCMMatchData", ""));										
									}
									Log.w("log","jfkjf12");
								}
							});
						}
					});
				}
			}catch(JSONException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.w("GCMSync", "Updating UI"+in+" "+ja.length());
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
	}
	
	static int gettotMAtch(){
		return tot_mat;
	}

	static int cnt_Match(){
		cnt_mat++;
		return cnt_mat;
	}
}
