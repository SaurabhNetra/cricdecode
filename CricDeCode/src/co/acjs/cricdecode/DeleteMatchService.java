package co.acjs.cricdecode;

import java.util.ArrayList;
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

public class DeleteMatchService extends IntentService {
	public static boolean started = true;
	public static Context who;

	public DeleteMatchService() {
		super("DeleteMatchService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w("DeleteMatchService", "Started");
		who = this;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("DeleteMatchService", "Ended");
	}

	public void deleteData() {
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
				MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_DELETED + "'", null,
				MatchDb.KEY_ROWID);
		JSONObject del_matches = new JSONObject();
		JSONObject del_perf = new JSONObject();
		JSONObject del_gcm = new JSONObject();
		JSONArray ja = new JSONArray();
		JSONArray ja_gcm = new JSONArray();

		try {
			if (c.getCount() != 0) {
				c.moveToFirst();
				do {
					JSONObject jo = new JSONObject();
					JSONObject jo_gcm = new JSONObject();
					jo.put("m_id",
							c.getString(c
									.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID))
									+ "A"
									+ c.getString(c
											.getColumnIndexOrThrow(MatchDb.KEY_ROWID))
									+ "B"
									+ AccessSharedPrefs.mPrefs.getString("id",
											""));
					jo_gcm.put("match_id", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_ROWID)));
					jo_gcm.put("device_id", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
					ja.put(jo);
					ja_gcm.put(jo_gcm);

					c.moveToNext();
				} while (!c.isAfterLast());
			}
			c.close();
			del_matches.put("matches", ja);
			del_gcm.put("todel", ja_gcm);

			final Cursor c1 = getContentResolver()
					.query(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE,
							new String[] { PerformanceDb.KEY_MATCHID,
									PerformanceDb.KEY_DEVICE_ID,
									PerformanceDb.KEY_INNING,
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
							PerformanceDb.KEY_STATUS + "='"
									+ MatchDb.MATCH_DELETED + "'", null,
							PerformanceDb.KEY_MATCHID);

			ja = new JSONArray();
			if (c1.getCount() != 0) {
				c1.moveToFirst();
				do {
					JSONObject jo = new JSONObject();
					jo.put("p_id",
							c1.getString(c1
									.getColumnIndexOrThrow(PerformanceDb.KEY_DEVICE_ID))
									+ "A"
									+ c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID))
									+ "B"
									+ c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_INNING))
									+ "C"
									+ AccessSharedPrefs.mPrefs.getString("id",
											""));
					ja.put(jo);

					c1.moveToNext();
				} while (!c1.isAfterLast());
			}
			c1.close();

			del_perf.put("perf", ja);

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("del_matches", del_matches
					.toString()));
			params.add(new BasicNameValuePair("del_perf", del_perf.toString()));
			JSONParser jsonParser = new JSONParser();
			int trial = 1;
			JSONObject jn = null;
			while (jsonParser.isOnline(who)) {
				Log.w("JSONParser", "DeleteMatch:: Called");
				// ping gae-matches, gae pings gae-perf
				jn = jsonParser.makeHttpRequest(
						getResources().getString(R.string.gae_match_insert)
								, "POST", params, who);
				Log.w("JSON returned", "DeleteMatch:: " + jn);
				Log.w("trial value", "DeleteMatch:: " + trial);
				if (jn != null)
					break;
				try {
					Thread.sleep(10 * trial);
				} catch (InterruptedException e) {
				}
				trial++;
				if (trial == 50) {
					break;
				}
			}

			if (jn.getInt("status") == 1) {

				// get regids
				jsonParser = new JSONParser();
				params.add(new BasicNameValuePair("user_id", AccessSharedPrefs.mPrefs.getString("id", "")));
				trial = 1;
				jn = null;
				while (jsonParser.isOnline(who)) {
					jn = jsonParser.makeHttpRequest(
							getResources().getString(R.string.gae_andro_retrieve),
							"POST", params, who);
					Log.w("JSON returned", "DeleteMatch:: " + jn);
					Log.w("trial value", "DeleteMatch:: " + trial);
					if (jn != null)
						break;
					try {
						Thread.sleep(10 * trial);
					} catch (InterruptedException e) {
					}
					trial++;
					if (trial == 50) {
						break;
					}
				}
				if (jn != null) {
					del_gcm.put("gcmid", 3);
					params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("SendToArrays", jn
							.getString("regids")));
					params.add(new BasicNameValuePair("MsgToSend", del_gcm
							.toString()));
					params.add(new BasicNameValuePair("uid",
							AccessSharedPrefs.mPrefs.getString("id", "")));
					jsonParser = new JSONParser();
					trial = 1;
					jn = null;
					while (jsonParser.isOnline(who)) {
						jn = jsonParser.makeHttpRequest(getResources()
								.getString(R.string.azure_sendgcm), "POST",
								params, who);
						Log.w("JSON returned", "DeleteMatch:: " + jn);
						Log.w("trial value", "DeleteMatch:: " + trial);
						if (jn != null)
							break;
						try {
							Thread.sleep(10 * trial);
						} catch (InterruptedException e) {
						}
						trial++;
					}
					// TODO if gae completes but azure does not complete
					if (jn.getInt("status") == 1) {

						c = getContentResolver().query(
								CricDeCodeContentProvider.CONTENT_URI_MATCH,
								new String[] { MatchDb.KEY_ROWID,
										MatchDb.KEY_DEVICE_ID,
										MatchDb.KEY_MATCH_DATE,
										MatchDb.KEY_MY_TEAM,
										MatchDb.KEY_OPPONENT_TEAM,
										MatchDb.KEY_VENUE, MatchDb.KEY_OVERS,
										MatchDb.KEY_INNINGS,
										MatchDb.KEY_RESULT, MatchDb.KEY_LEVEL,
										MatchDb.KEY_FIRST_ACTION,
										MatchDb.KEY_DURATION,
										MatchDb.KEY_REVIEW, MatchDb.KEY_STATUS,
										MatchDb.KEY_SYNCED },
								MatchDb.KEY_STATUS + "='"
										+ MatchDb.MATCH_DELETED + "'", null,
								MatchDb.KEY_ROWID);

						c.moveToFirst();
						do {
							Uri uri = Uri
									.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE
											+ "/"
											+ c.getString(c
													.getColumnIndexOrThrow(MatchDb.KEY_ROWID))
											+ "/"
											+ c.getString(c
													.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
							getApplicationContext().getContentResolver()
									.delete(uri, null, null);
							uri = Uri
									.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH
											+ "/"
											+ c.getString(c
													.getColumnIndexOrThrow(MatchDb.KEY_ROWID))
											+ "/"
											+ c.getString(c
													.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID)));
							getApplicationContext().getContentResolver()
									.delete(uri, null, null);
							c.moveToNext();
						} while (!c.isAfterLast());
						c.close();
						AccessSharedPrefs.setString(who,
								"DeleteMatchServiceCalled",
								CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
					}
				}

			}
		} catch (NullPointerException e) {
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			((MainActivity) MainActivity.main_context)
					.runOnUiThread(new Runnable() {
						public void run() {
							try {
								DiaryMatchesFragment.loader_diary_list
										.restartLoader(
												0,
												null,
												DiaryMatchesFragment.diary_matches_fragment);
							} catch (Exception e) {
							}
						}
					});
		} catch (Exception e) {
		}

	}

	@Override
	protected void onHandleIntent(Intent intent) {

		if (AccessSharedPrefs.mPrefs.getString("DeleteMatchServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED)) {
			if (AccessSharedPrefs.mPrefs.getString("infi_sync", "no").equals(
					"yes")
					|| AccessSharedPrefs.mPrefs.getString("sync", "no").equals(
							"yes")) {
				deleteData();
			} else {
				AccessSharedPrefs.setString(who, "DeleteMatchServiceCalled",
						CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
			}

		}
	}
}
