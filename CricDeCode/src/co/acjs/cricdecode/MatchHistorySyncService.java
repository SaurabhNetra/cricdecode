package co.acjs.cricdecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.stackmob.sdk.callback.StackMobCallback;
import com.stackmob.sdk.exception.StackMobException;

public class MatchHistorySyncService extends IntentService {
	public static boolean started = true;
	public static Context who;
	static int tot_per = 0;
	static int tot_mat_per = 0;
	static int cnt_tot_per = 0;
	static int cnt_tot_mat_per = 0;
	static String matchId;
	static String deviceId;
	ArrayList<String> match_id_arr = new ArrayList<String>();
	ArrayList<String> performance_id_arr = new ArrayList<String>();

	public MatchHistorySyncService() {
		super("MatchCreateService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		who = this;
		Log.w("MatchCreateService", "Started");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("MatchCreateService", "Ended");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (AccessSharedPrefs.mPrefs.getString("MatchHistorySyncServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED)) {

			Cursor c = getContentResolver().query(
					CricDeCodeContentProvider.CONTENT_URI_MATCH,
					new String[] { MatchDb.KEY_ROWID, MatchDb.KEY_DEVICE_ID,
							MatchDb.KEY_MATCH_DATE, MatchDb.KEY_MY_TEAM,
							MatchDb.KEY_OPPONENT_TEAM, MatchDb.KEY_VENUE,
							MatchDb.KEY_OVERS, MatchDb.KEY_INNINGS,
							MatchDb.KEY_RESULT, MatchDb.KEY_LEVEL,
							MatchDb.KEY_FIRST_ACTION, MatchDb.KEY_DURATION,
							MatchDb.KEY_REVIEW, MatchDb.KEY_STATUS,
							MatchDb.KEY_SYNCED },
					MatchDb.KEY_SYNCED + "=" + "0 and " + MatchDb.KEY_STATUS
							+ "='" + MatchDb.MATCH_HISTORY + "'", null,
					MatchDb.KEY_ROWID);
			Cursor t = getContentResolver()
					.query(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE,
							new String[] { PerformanceDb.KEY_ROWID,
									PerformanceDb.KEY_BAT_BALLS,
									PerformanceDb.KEY_BAT_BOWLER_TYPE,
									PerformanceDb.KEY_BAT_CHANCES,
									PerformanceDb.KEY_BAT_FIELDING_POSITION,
									PerformanceDb.KEY_BAT_FOURS,
									PerformanceDb.KEY_BAT_HOW_OUT,
									PerformanceDb.KEY_BAT_NUM,
									PerformanceDb.KEY_BAT_RUNS,
									PerformanceDb.KEY_BAT_SIXES,
									PerformanceDb.KEY_BAT_TIME,
									PerformanceDb.KEY_BOWL_BALLS,
									PerformanceDb.KEY_BOWL_CATCHES_DROPPED,
									PerformanceDb.KEY_BOWL_FOURS,
									PerformanceDb.KEY_BOWL_MAIDENS,
									PerformanceDb.KEY_BOWL_NOBALLS,
									PerformanceDb.KEY_BOWL_RUNS,
									PerformanceDb.KEY_BOWL_SIXES,
									PerformanceDb.KEY_BOWL_SPELLS,
									PerformanceDb.KEY_BOWL_WIDES,
									PerformanceDb.KEY_BOWL_WKTS_LEFT,
									PerformanceDb.KEY_BOWL_WKTS_RIGHT,
									PerformanceDb.KEY_FIELD_BYES,
									PerformanceDb.KEY_FIELD_CATCHES_DROPPED,
									PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
									PerformanceDb.KEY_FIELD_CLOSE_CATCH,
									PerformanceDb.KEY_FIELD_DEEP_CATCH,
									PerformanceDb.KEY_FIELD_MISFIELDS,
									PerformanceDb.KEY_FIELD_RO_CIRCLE,
									PerformanceDb.KEY_FIELD_RO_DEEP,
									PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE,
									PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP,
									PerformanceDb.KEY_FIELD_SLIP_CATCH,
									PerformanceDb.KEY_FIELD_STUMPINGS,
									PerformanceDb.KEY_INNING,
									PerformanceDb.KEY_MATCHID,
									PerformanceDb.KEY_STATUS },
							PerformanceDb.KEY_SYNCED + "=" + "0 and "
									+ PerformanceDb.KEY_STATUS + "='"
									+ MatchDb.MATCH_HISTORY + "'", null,
							PerformanceDb.KEY_MATCHID);
			settotPerformance(t.getCount());

			if (c.getCount() != 0) {
				c.moveToFirst();
				do {

					ServerDBCricketMatch cm = new ServerDBCricketMatch(
							AccessSharedPrefs.mPrefs.getString("id", ""),
							Integer.parseInt(c.getString(c
									.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID))),
							Integer.parseInt(AccessSharedPrefs.mPrefs
									.getString("device_id", "")),
							c.getString(c
									.getColumnIndexOrThrow(MatchDb.KEY_MATCH_DATE)),
							c.getString(c
									.getColumnIndexOrThrow(MatchDb.KEY_MY_TEAM)),
							c.getString(c
									.getColumnIndexOrThrow(MatchDb.KEY_OPPONENT_TEAM)),
							c.getString(c
									.getColumnIndexOrThrow(MatchDb.KEY_VENUE)),
							Integer.parseInt(c.getString(c
									.getColumnIndexOrThrow(MatchDb.KEY_OVERS))),
							Integer.parseInt(c.getString(c
									.getColumnIndexOrThrow(MatchDb.KEY_INNINGS))),
							c.getString(c
									.getColumnIndexOrThrow(MatchDb.KEY_RESULT)),
							c.getString(c
									.getColumnIndexOrThrow(MatchDb.KEY_LEVEL)),
							c.getString(c
									.getColumnIndexOrThrow(MatchDb.KEY_FIRST_ACTION)),
							c.getString(c
									.getColumnIndexOrThrow(MatchDb.KEY_DURATION)),
							c.getString(c
									.getColumnIndexOrThrow(MatchDb.KEY_REVIEW)),
							0);
					setMatchId(c.getString(c
							.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID)));
					setDeviceId(AccessSharedPrefs.mPrefs.getString("device_id",
							""));
					cm.save(new StackMobCallback() {

						@Override
						public void failure(StackMobException arg0) {
							

						}

						@Override
						public void success(String arg0) {
							Cursor c1 = getContentResolver()
									.query(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE,
											new String[] {
													PerformanceDb.KEY_ROWID,
													PerformanceDb.KEY_BAT_BALLS,
													PerformanceDb.KEY_BAT_BOWLER_TYPE,
													PerformanceDb.KEY_BAT_CHANCES,
													PerformanceDb.KEY_BAT_FIELDING_POSITION,
													PerformanceDb.KEY_BAT_FOURS,
													PerformanceDb.KEY_BAT_HOW_OUT,
													PerformanceDb.KEY_BAT_NUM,
													PerformanceDb.KEY_BAT_RUNS,
													PerformanceDb.KEY_BAT_SIXES,
													PerformanceDb.KEY_BAT_TIME,
													PerformanceDb.KEY_BOWL_BALLS,
													PerformanceDb.KEY_BOWL_CATCHES_DROPPED,
													PerformanceDb.KEY_BOWL_FOURS,
													PerformanceDb.KEY_BOWL_MAIDENS,
													PerformanceDb.KEY_BOWL_NOBALLS,
													PerformanceDb.KEY_BOWL_RUNS,
													PerformanceDb.KEY_BOWL_SIXES,
													PerformanceDb.KEY_BOWL_SPELLS,
													PerformanceDb.KEY_BOWL_WIDES,
													PerformanceDb.KEY_BOWL_WKTS_LEFT,
													PerformanceDb.KEY_BOWL_WKTS_RIGHT,
													PerformanceDb.KEY_FIELD_BYES,
													PerformanceDb.KEY_FIELD_CATCHES_DROPPED,
													PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
													PerformanceDb.KEY_FIELD_CLOSE_CATCH,
													PerformanceDb.KEY_FIELD_DEEP_CATCH,
													PerformanceDb.KEY_FIELD_MISFIELDS,
													PerformanceDb.KEY_FIELD_RO_CIRCLE,
													PerformanceDb.KEY_FIELD_RO_DEEP,
													PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE,
													PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP,
													PerformanceDb.KEY_FIELD_SLIP_CATCH,
													PerformanceDb.KEY_FIELD_STUMPINGS,
													PerformanceDb.KEY_INNING,
													PerformanceDb.KEY_MATCHID,
													PerformanceDb.KEY_STATUS },
											PerformanceDb.KEY_SYNCED
													+ "="
													+ "0 and "
													+ PerformanceDb.KEY_STATUS
													+ "='"
													+ MatchDb.MATCH_HISTORY
													+ "' and "
													+ PerformanceDb.KEY_MATCHID
													+ "= '"
													+ getMatchId()
													+ "' and "
													+ PerformanceDb.KEY_DEVICE_ID
													+ "= '" + getDeviceId()
													+ "'", null, null);
							setMatchPerformance(c1.getCount());
							if (c1.getCount() != 0) {
								c1.moveToFirst();
								do {
									ServerDBPerformance sp = new ServerDBPerformance(
											AccessSharedPrefs.mPrefs.getString(
													"id", ""),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID))),
											Integer.parseInt(AccessSharedPrefs.mPrefs
													.getString("device_id", "")),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_ROWID))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_INNING))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_NUM))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_RUNS))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BALLS))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_TIME))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FOURS))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_SIXES))),
											c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_HOW_OUT)),
											c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BOWLER_TYPE)),
											c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FIELDING_POSITION)),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_CHANCES))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_BALLS))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SPELLS))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_MAIDENS))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_RUNS))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_FOURS))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SIXES))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_LEFT))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_RIGHT))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_CATCHES_DROPPED))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_NOBALLS))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WIDES))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_SLIP_CATCH))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CLOSE_CATCH))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CIRCLE_CATCH))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_DEEP_CATCH))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_CIRCLE))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DEEP))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_STUMPINGS))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_BYES))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_MISFIELDS))),
											Integer.parseInt(c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CATCHES_DROPPED))),
											1);
									sp.save(new StackMobCallback() {

										@Override
										public void failure(
												StackMobException arg0) {

										}

										@Override
										public void success(String arg0) {
											if (cnt_MatchPerformance() == getMatchPerformance()) {
												//TODO update db
												init_cnt_MatchPerformance();

											}
											if (cnt_totPerformance() == gettotPerformance()) {
												AccessSharedPrefs
														.setString(
																who,
																"MatchHistorySyncServiceCalled",
																CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
											}
										}
									});
									c1.moveToNext();
								} while (!c1.isAfterLast());
							}
							c1.close();

						}
					});

					c.moveToNext();
				} while (!c.isAfterLast());
			}
			c.close();

			/*
			 * final JSONParser jsonParser = new JSONParser();
			 * List<NameValuePair> params = new ArrayList<NameValuePair>();
			 * Cursor c = getContentResolver().query(
			 * CricDeCodeContentProvider.CONTENT_URI_MATCH, new String[] {
			 * MatchDb.KEY_ROWID, MatchDb.KEY_DEVICE_ID, MatchDb.KEY_MATCH_DATE,
			 * MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM,
			 * MatchDb.KEY_VENUE, MatchDb.KEY_OVERS, MatchDb.KEY_INNINGS,
			 * MatchDb.KEY_RESULT, MatchDb.KEY_LEVEL, MatchDb.KEY_FIRST_ACTION,
			 * MatchDb.KEY_DURATION, MatchDb.KEY_REVIEW, MatchDb.KEY_STATUS,
			 * MatchDb.KEY_SYNCED }, MatchDb.KEY_SYNCED + "=" + "0 and " +
			 * MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'", null,
			 * MatchDb.KEY_ROWID); Cursor c1 = getContentResolver()
			 * .query(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE, new
			 * String[] { PerformanceDb.KEY_ROWID, PerformanceDb.KEY_BAT_BALLS,
			 * PerformanceDb.KEY_BAT_BOWLER_TYPE, PerformanceDb.KEY_BAT_CHANCES,
			 * PerformanceDb.KEY_BAT_FIELDING_POSITION,
			 * PerformanceDb.KEY_BAT_FOURS, PerformanceDb.KEY_BAT_HOW_OUT,
			 * PerformanceDb.KEY_BAT_NUM, PerformanceDb.KEY_BAT_RUNS,
			 * PerformanceDb.KEY_BAT_SIXES, PerformanceDb.KEY_BAT_TIME,
			 * PerformanceDb.KEY_BOWL_BALLS,
			 * PerformanceDb.KEY_BOWL_CATCHES_DROPPED,
			 * PerformanceDb.KEY_BOWL_FOURS, PerformanceDb.KEY_BOWL_MAIDENS,
			 * PerformanceDb.KEY_BOWL_NOBALLS, PerformanceDb.KEY_BOWL_RUNS,
			 * PerformanceDb.KEY_BOWL_SIXES, PerformanceDb.KEY_BOWL_SPELLS,
			 * PerformanceDb.KEY_BOWL_WIDES, PerformanceDb.KEY_BOWL_WKTS_LEFT,
			 * PerformanceDb.KEY_BOWL_WKTS_RIGHT, PerformanceDb.KEY_FIELD_BYES,
			 * PerformanceDb.KEY_FIELD_CATCHES_DROPPED,
			 * PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
			 * PerformanceDb.KEY_FIELD_CLOSE_CATCH,
			 * PerformanceDb.KEY_FIELD_DEEP_CATCH,
			 * PerformanceDb.KEY_FIELD_MISFIELDS,
			 * PerformanceDb.KEY_FIELD_RO_CIRCLE,
			 * PerformanceDb.KEY_FIELD_RO_DEEP,
			 * PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE,
			 * PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP,
			 * PerformanceDb.KEY_FIELD_SLIP_CATCH,
			 * PerformanceDb.KEY_FIELD_STUMPINGS, PerformanceDb.KEY_INNING,
			 * PerformanceDb.KEY_MATCHID, PerformanceDb.KEY_STATUS },
			 * PerformanceDb.KEY_SYNCED + "=" + "0 and " +
			 * PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'",
			 * null, PerformanceDb.KEY_MATCHID); JSONObject json = new
			 * JSONObject(); JSONArray matches = new JSONArray(); JSONArray
			 * performance = new JSONArray(); try { if (c1.getCount() != 0) {
			 * c1.moveToFirst(); do { JSONObject row = new JSONObject();
			 * performance_id_arr .add(c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_ROWID))); row.put("id",
			 * c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_ROWID))); row.put("mat",
			 * c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID)));
			 * row.put("inn", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_INNING))); row.put("bb",
			 * c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BALLS)));
			 * row.put("bbt", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BOWLER_TYPE)));
			 * row.put("bc", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BAT_CHANCES)));
			 * row.put("bfp", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb
			 * .KEY_BAT_FIELDING_POSITION))); row.put("bf", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FOURS)));
			 * row.put("bho", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BAT_HOW_OUT)));
			 * row.put("bn", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BAT_NUM)));
			 * row.put("br", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BAT_RUNS)));
			 * row.put("bs", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BAT_SIXES)));
			 * row.put("bt", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BAT_TIME)));
			 * row.put("ob", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_BALLS)));
			 * row.put("ocd", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_CATCHES_DROPPED)));
			 * row.put("of", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_FOURS)));
			 * row.put("om", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_MAIDENS)));
			 * row.put("ono", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_NOBALLS)));
			 * row.put("oru", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_RUNS)));
			 * row.put("osx", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SIXES)));
			 * row.put("osp", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SPELLS)));
			 * row.put("ow", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WIDES)));
			 * row.put("owl", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_LEFT)));
			 * row.put("owr", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_RIGHT)));
			 * 
			 * row.put("fb", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_BYES)));
			 * row.put("fcd", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb
			 * .KEY_FIELD_CATCHES_DROPPED))); row.put("fcc", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CIRCLE_CATCH)));
			 * row.put("fco", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CLOSE_CATCH)));
			 * row.put("fdc", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_DEEP_CATCH)));
			 * row.put("fmf", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_MISFIELDS)));
			 * row.put("fci", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb
			 * .KEY_FIELD_RO_DIRECT_CIRCLE))); row.put("fdd", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP)));
			 * row.put("fsc", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_SLIP_CATCH)));
			 * row.put("fs", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_STUMPINGS)));
			 * row.put("sts", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_STATUS)));
			 * row.put("frd", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DEEP)));
			 * row.put("frc", c1.getString(c1
			 * .getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_CIRCLE)));
			 * performance.put(row); c1.moveToNext(); } while
			 * (!c1.isAfterLast()); } c1.close(); if (c.getCount() != 0) {
			 * c.moveToFirst(); // Do for every Match Row in the Cursor do {
			 * JSONObject row = new JSONObject(); match_id_arr.add(c.getString(c
			 * .getColumnIndexOrThrow(MatchDb.KEY_ROWID))); row.put("id",
			 * c.getString(c .getColumnIndexOrThrow(MatchDb.KEY_ROWID)));
			 * row.put("dat", c.getString(c
			 * .getColumnIndexOrThrow(MatchDb.KEY_MATCH_DATE))); row.put("myt",
			 * c.getString(c .getColumnIndexOrThrow(MatchDb.KEY_MY_TEAM)));
			 * row.put("opp", c.getString(c
			 * .getColumnIndexOrThrow(MatchDb.KEY_OPPONENT_TEAM)));
			 * row.put("ven", c.getString(c
			 * .getColumnIndexOrThrow(MatchDb.KEY_VENUE))); row.put("ovr",
			 * c.getString(c .getColumnIndexOrThrow(MatchDb.KEY_OVERS)));
			 * row.put("inn", c.getString(c
			 * .getColumnIndexOrThrow(MatchDb.KEY_INNINGS))); row.put("res",
			 * c.getString(c .getColumnIndexOrThrow(MatchDb.KEY_RESULT)));
			 * row.put("lvl", c.getString(c
			 * .getColumnIndexOrThrow(MatchDb.KEY_LEVEL))); row.put("act",
			 * c.getString(c .getColumnIndexOrThrow(MatchDb.KEY_FIRST_ACTION)));
			 * row.put("dur", c.getString(c
			 * .getColumnIndexOrThrow(MatchDb.KEY_DURATION))); row.put("rev",
			 * c.getString(c .getColumnIndexOrThrow(MatchDb.KEY_REVIEW)));
			 * row.put("sts", c.getString(c
			 * .getColumnIndexOrThrow(MatchDb.KEY_STATUS))); matches.put(row);
			 * c.moveToNext(); } while (!c.isAfterLast()); } c.close();
			 * json.put("matches", matches); json.put("performance",
			 * performance); } catch (Exception e) { } params.add(new
			 * BasicNameValuePair("id", AccessSharedPrefs.mPrefs
			 * .getString("id", ""))); params.add(new BasicNameValuePair("dev",
			 * AccessSharedPrefs.mPrefs .getString("device_id", "")));
			 * params.add(new BasicNameValuePair("json", json.toString()));
			 * 
			 * Log.w("MATCH SYNC", "JSON: " + json.toString());
			 * writeToFile(json.toString());
			 * 
			 * int trial = 1; JSONObject jn = null; while
			 * (jsonParser.isOnline(this)) { jn = jsonParser.makeHttpRequest(
			 * getResources().getString(R.string.match_create_sync), "POST",
			 * params, this); Log.w("JSON returned", "MatchCreateService: " +
			 * jn); Log.w("trial value", "MatchCreateService: " + trial); if (jn
			 * != null) break; try { Thread.sleep(10 * trial); } catch
			 * (InterruptedException e) { } trial++; }
			 * 
			 * try { if (jn != null) { if (jn.getInt("status") == 1)
			 * AccessSharedPrefs.setString(this,
			 * "MatchHistorySyncServiceCalled",
			 * CDCAppClass.DOESNT_NEED_TO_BE_CALLED); // TODO sab // sync ho //
			 * gaye String selection = MatchDb.KEY_ROWID + " in (" +
			 * DiaryMatchesFragment.buildSelectedItemString( match_id_arr, true)
			 * + " ) and " + MatchDb.KEY_DEVICE_ID + "='" +
			 * AccessSharedPrefs.mPrefs.getString("device_id", "") + "'";
			 * Log.d("Debug", "Match id list " + DiaryMatchesFragment
			 * .buildSelectedItemString( match_id_arr, true)); ContentValues
			 * matchvalues = new ContentValues();
			 * matchvalues.put(MatchDb.KEY_SYNCED, 1);
			 * getContentResolver().update(
			 * CricDeCodeContentProvider.CONTENT_URI_MATCH, matchvalues,
			 * selection, null);
			 * 
			 * selection = PerformanceDb.KEY_ROWID + " in (" +
			 * DiaryMatchesFragment.buildSelectedItemString( performance_id_arr,
			 * true) + " ) and " + PerformanceDb.KEY_DEVICE_ID + "='" +
			 * AccessSharedPrefs.mPrefs.getString("device_id", "") + "'";
			 * Log.d("Debug", "Performance id list " + DiaryMatchesFragment
			 * .buildSelectedItemString( performance_id_arr, true)); matchvalues
			 * = new ContentValues(); matchvalues.put(PerformanceDb.KEY_SYNCED,
			 * 1); getContentResolver().update(
			 * CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE, matchvalues,
			 * selection, null);
			 * 
			 * Log.w("JSON returned", "" + jn.getInt("status")); } } catch
			 * (JSONException e) { }
			 */}
	}

	static int cnt_totPerformance() {
		cnt_tot_per++;
		return cnt_tot_per;
	}

	static void settotPerformance(int t) {
		tot_per = t;
	}

	static int gettotPerformance() {
		return tot_per;
	}

	static int cnt_MatchPerformance() {
		cnt_tot_mat_per++;
		return cnt_tot_mat_per;
	}

	static void init_cnt_MatchPerformance() {
		tot_mat_per = 0;
	}

	static void setMatchPerformance(int t) {
		tot_mat_per = t;
	}

	static int getMatchPerformance() {
		return tot_mat_per;
	}

	static String getMatchId() {
		return matchId;
	}

	static String getDeviceId() {
		return deviceId;
	}

	static void setMatchId(String m) {
		matchId = m;
	}

	static void setDeviceId(String d) {
		deviceId = d;
	}

	private void writeToFile(String data) {
		try {
			File root = new File(Environment.getExternalStorageDirectory(),
					"CricDeCode");
			if (!root.exists()) {
				root.mkdirs();
			}

			File gpxfile = new File(root, "log.txt");
			FileWriter writer = new FileWriter(gpxfile);
			writer.write(data);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}
}