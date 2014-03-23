package co.acjs.cricdecode;

import java.util.ArrayList;
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

public class MatchHistorySyncService extends IntentService {
	public static boolean started = true;
	public static Context who;
	static int match_count;

	public MatchHistorySyncService() {
		super("MatchCreateService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		who = this;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public void sendData(String regids) {
		final Cursor c = getContentResolver().query(
				CricDeCodeContentProvider.CONTENT_URI_MATCH,
				new String[] { MatchDb.KEY_ROWID, MatchDb.KEY_DEVICE_ID,
						MatchDb.KEY_MATCH_DATE, MatchDb.KEY_MY_TEAM,
						MatchDb.KEY_OPPONENT_TEAM, MatchDb.KEY_VENUE,
						MatchDb.KEY_OVERS, MatchDb.KEY_INNINGS,
						MatchDb.KEY_RESULT, MatchDb.KEY_LEVEL,
						MatchDb.KEY_FIRST_ACTION, MatchDb.KEY_DURATION,
						MatchDb.KEY_REVIEW, MatchDb.KEY_STATUS,
						MatchDb.KEY_SYNCED },
				MatchDb.KEY_SYNCED + "=" + "0 and " + MatchDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "'", null, MatchDb.KEY_ROWID);
		match_count = c.getCount();
		int tcount = 0;
		if (c.getCount() != 0) {
			c.moveToFirst();
			do {
				try {
					JSONObject cm = new JSONObject();
					JSONObject per = new JSONObject();
					cm.put("user_id",
							AccessSharedPrefs.mPrefs.getString("id", ""));
					cm.put("match_id", Integer.parseInt(c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_ROWID))));
					cm.put("device_id",
							AccessSharedPrefs.mPrefs.getString("device_id", ""));
					cm.put("match_date", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_MATCH_DATE)));
					cm.put("my_team", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_MY_TEAM)));
					cm.put("opponent_team", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_OPPONENT_TEAM)));
					cm.put("venue", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_OPPONENT_TEAM)));
					cm.put("overs", Integer.parseInt(c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_OVERS))));
					cm.put("innings", Integer.parseInt(c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_INNINGS))));
					cm.put("result", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_RESULT)));
					cm.put("level", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_LEVEL)));
					cm.put("first_action", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_FIRST_ACTION)));
					cm.put("duration", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_DURATION)));
					cm.put("review", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_REVIEW)));
					cm.put("id",
							AccessSharedPrefs.mPrefs.getString("device_id", "")
									+ "A"
									+ c.getString(c
											.getColumnIndexOrThrow(MatchDb.KEY_ROWID))
									+ "B"
									+ AccessSharedPrefs.mPrefs.getString("id",
											""));
					String matchId = c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_ROWID));
					String devid = c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID));

					final Cursor c1 = getContentResolver().query(
							CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE,
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
									+ MatchDb.MATCH_HISTORY + "' and "
									+ PerformanceDb.KEY_MATCHID + "= "
									+ matchId + " and "
									+ PerformanceDb.KEY_DEVICE_ID + "= '"
									+ devid + "'", null, null);

					JSONArray ja = new JSONArray();
					if (c1.getCount() != 0) {
						c1.moveToFirst();
						do {
							JSONObject jo1 = new JSONObject();
							jo1.put("user_id", AccessSharedPrefs.mPrefs
									.getString("id", ""));
							jo1.put("match_id",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID))));
							jo1.put("device_id",
									c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_DEVICE_ID)));
							jo1.put("per_id",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_ROWID))));
							jo1.put("inning",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_INNING))));
							jo1.put("bat_num",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_NUM))));
							jo1.put("bat_runs",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_RUNS))));
							jo1.put("bat_balls",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BALLS))));
							jo1.put("bat_time",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_TIME))));
							jo1.put("bat_fours",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FOURS))));
							jo1.put("bat_sixes",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_SIXES))));
							jo1.put("bat_dismissal",
									c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_HOW_OUT)));
							jo1.put("bat_bowler_type",
									c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BOWLER_TYPE)));
							jo1.put("bat_fielding_position",
									c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FIELDING_POSITION)));
							jo1.put("bat_chances",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_CHANCES))));
							jo1.put("bowl_balls",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_BALLS))));
							jo1.put("bowl_spells",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SPELLS))));
							jo1.put("bowl_maidens",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_MAIDENS))));
							jo1.put("bowl_runs",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_RUNS))));
							jo1.put("bowl_fours",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_FOURS))));
							jo1.put("bowl_sixes",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SIXES))));
							jo1.put("bowl_wkts_left",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_LEFT))));
							jo1.put("bowl_wkts_right",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_RIGHT))));
							jo1.put("bowl_catches_dropped",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_CATCHES_DROPPED))));
							jo1.put("bowl_no_balls",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_NOBALLS))));
							jo1.put("bowl_wides",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WIDES))));
							jo1.put("field_slip_catch",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_SLIP_CATCH))));
							jo1.put("field_close_catch",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CLOSE_CATCH))));
							jo1.put("field_circle_catch",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CIRCLE_CATCH))));
							jo1.put("field_deep_catch",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_DEEP_CATCH))));
							jo1.put("field_ro_circle",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_CIRCLE))));
							jo1.put("field_ro_direct_circle",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE))));
							jo1.put("field_ro_deep",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DEEP))));
							jo1.put("field_ro_direct_deep",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP))));
							jo1.put("field_stumpings",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_STUMPINGS))));
							jo1.put("field_byes",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_BYES))));
							jo1.put("field_misfield",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_MISFIELDS))));
							jo1.put("field_catches_dropped",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CATCHES_DROPPED))));
							jo1.put("id",
									AccessSharedPrefs.mPrefs.getString(
											"device_id", "")
											+ "A"
											+ c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID))
											+ "B"
											+ c1.getString(c1
													.getColumnIndexOrThrow(PerformanceDb.KEY_INNING))
											+ "C"
											+ AccessSharedPrefs.mPrefs
													.getString("id", ""));

							ja.put(jo1);
						} while (!c1.isAfterLast());
					}
					c1.close();

					per.put("per", ja);

					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("matchData", cm
							.toString()));
					params.add(new BasicNameValuePair("perData", per.toString()
							.toString()));
					JSONParser jsonParser = new JSONParser();
					int trial = 1;
					JSONObject jn = null;
					while (jsonParser.isOnline(who) && trial < 20) {
						Log.w("JSONParser", "MatchHistory: Called");
						
						jn = jsonParser.makeHttpRequest(getResources()
								.getString(R.string.gae_match_insert), "POST",
								params, who);
						Log.w("JSON returned", "MatchHistory: " + jn);
						Log.w("trial value", "MatchHistory: " + trial);
						if (jn != null)
							break;
						try {
							Thread.sleep(10 * trial);
						} catch (InterruptedException e) {
						}
						trial++;
					}

					if (jn.getInt("status") == 1) {
						Uri uri = Uri
								.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH
										+ "/"
										+ c.getString(c
												.getColumnIndexOrThrow(MatchDb.KEY_ROWID))
										+ "/"
										+ c.getString(c
												.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
						ContentValues matchvalues = new ContentValues();

						matchvalues.put(MatchDb.KEY_SYNCED, 1);
						getApplicationContext().getContentResolver().update(
								uri, matchvalues, null, null);
						uri = Uri
								.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE
										+ "/"
										+ c.getString(c
												.getColumnIndexOrThrow(MatchDb.KEY_ROWID))
										+ "/"
										+ c.getString(c
												.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
						ContentValues values = new ContentValues();

						values.put(PerformanceDb.KEY_SYNCED, 1);
						getApplicationContext().getContentResolver().update(
								uri, values, null, null);
						cm.put("gcmid", 2);
						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("SendToArrays",
								regids));
						params.add(new BasicNameValuePair("MsgToSend", cm
								.toString()));						
						jsonParser = new JSONParser();
						trial = 1;
						jn = null;
						while (jsonParser.isOnline(who) && trial < 20) {
							Log.w("JSONParser", "Send gcm: Called");
							jn = jsonParser.makeHttpRequest(getResources()
									.getString(R.string.azure_sendgcm),
									"POST", params, who);
							Log.w("JSON returned", "Send Gcm: " + jn);
							Log.w("trial value", "Send Gcm: " + trial);
							if (jn != null)
								break;
							try {
								Thread.sleep(10 * trial);
							} catch (InterruptedException e) {
							}
							trial++;
						}
						tcount++;

						if (jn.getInt("status") == 1) {
							// TODO
						} else {
							// TODO
						}

					} else {
						break;
					}
				} catch (NullPointerException e) {
					Log.w("exception", "np");
				} catch (JSONException e) {
					Log.w("exception", "json");
				}

				c.moveToNext();
			} while (!c.isAfterLast());
		}
		c.close();

		if (tcount == match_count)
			AccessSharedPrefs.setString(who, "MatchHistorySyncServiceCalled",
					CDCAppClass.DOESNT_NEED_TO_BE_CALLED);

	}

	@Override
	protected void onHandleIntent(Intent intent) {

		if (AccessSharedPrefs.mPrefs.getString("MatchHistorySyncServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED)) {
			if (AccessSharedPrefs.mPrefs.getString("infi_sync", "no").equals(
					"yes")) {

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id",
						AccessSharedPrefs.mPrefs.getString("id", "")));

				final JSONParser jsonParser = new JSONParser();
				int trial = 1;
				JSONObject jn = null;
				while (jsonParser.isOnline(who)) {
					Log.w("JSONParser", "SubinfiSync:: Called");
					// take user_id, max validts, valid? return status=1, not
					// valid then ping google to chk status=0, status=1, send
					// gcms, also give back regids
					jn = jsonParser.makeHttpRequest(who.getResources()
							.getString(R.string.gae_infisync_check), "POST",
							params, who);
					Log.w("JSON returned", "SubinfiSync:: " + jn);
					Log.w("trial value", "SubinfiSync:: " + trial);
					if (jn != null)
						break;
					try {
						Thread.sleep(10 * trial);
					} catch (InterruptedException e) {
					}
					trial++;
					if (trial == 50)
						break;
				}

				try {
					if (jn.getInt("status") == 1) {
						AccessSharedPrefs.setString(who, "infi_sync", "yes");
						sendData(jn.getString("regids"));
					} else if (jn.getInt("status") == 0) {
						AccessSharedPrefs.setString(who, "infi_sync", "no");
						AccessSharedPrefs.setString(who,
								"MatchHistorySyncServiceCalled",
								CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
					}
				} catch (NullPointerException e) {
				} catch (JSONException e) {
				}

			} else if (AccessSharedPrefs.mPrefs.getString("sync", "no").equals(
					"yes")) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("id",
						AccessSharedPrefs.mPrefs.getString("id", "")));

				final JSONParser jsonParser = new JSONParser();
				int trial = 1;
				JSONObject jn = null;
				while (jsonParser.isOnline(who)) {
					// take user_id, max validts, valid? return status=1, not
					// valid then ping google to chk status=0, status=1, send
					// gcms, also give back regids
					jn = jsonParser.makeHttpRequest(who.getResources()
							.getString(R.string.gae_sync_check), "POST",
							params, who);
					Log.w("JSON returned", "SubSync:: " + jn);
					Log.w("trial value", "SubSync:: " + trial);
					if (jn != null)
						break;
					try {
						Thread.sleep(10 * trial);
					} catch (InterruptedException e) {
					}
					trial++;
					if (trial == 50)
						break;
				}

				try {
					if (jn.getInt("status") == 1) {
						AccessSharedPrefs.setString(who, "sync", "yes");
						sendData(jn.getString("regids"));
					} else if (jn.getInt("status") == 0) {
						AccessSharedPrefs.setString(who, "sync", "no");
						AccessSharedPrefs.setString(who,
								"MatchHistorySyncServiceCalled",
								CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
					}
				} catch (NullPointerException e) {
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				AccessSharedPrefs.setString(who,
						"MatchHistorySyncServiceCalled",
						CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
			}

		}
	}

}