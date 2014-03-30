package co.acjs.cricdecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import android.os.Environment;
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
		writeToFile("in send data");
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
		writeToFile("no of matches" + c.getCount());
		int tcount = 0;
		if (c.getCount() != 0) {
			c.moveToFirst();
			do {
				try {
					JSONObject cm = new JSONObject();
					JSONObject per = new JSONObject();
					cm.put("uid", AccessSharedPrefs.mPrefs.getString("id", ""));
					cm.put("mid", Integer.parseInt(c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_ROWID))));
					cm.put("did",
							Integer.parseInt(AccessSharedPrefs.mPrefs.getString("device_id", "")));
					cm.put("m_dt", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_MATCH_DATE)));
					cm.put("tm", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_MY_TEAM)));
					cm.put("otm", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_OPPONENT_TEAM)));
					cm.put("vn", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_VENUE)));
					cm.put("ovr", Integer.parseInt(c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_OVERS))));
					cm.put("in", Integer.parseInt(c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_INNINGS))));
					cm.put("res", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_RESULT)));
					cm.put("lv", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_LEVEL)));
					cm.put("fa", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_FIRST_ACTION)));
					cm.put("dur", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_DURATION)));
					cm.put("rev", c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_REVIEW)));

					String matchId = c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_ROWID));
					String devid = c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID));
					writeToFile("mid: " + matchId + " dev: " + devid);

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
									PerformanceDb.KEY_DEVICE_ID,
									PerformanceDb.KEY_STATUS },
							PerformanceDb.KEY_SYNCED + "=" + "0 and "
									+ PerformanceDb.KEY_STATUS + "='"
									+ MatchDb.MATCH_HISTORY + "' and "
									+ PerformanceDb.KEY_MATCHID + "= "
									+ matchId + " and "
									+ PerformanceDb.KEY_DEVICE_ID + "= '"
									+ devid + "'", null, null);

					JSONArray ja = new JSONArray();
					writeToFile("No of pers: " + c1.getCount());
					if (c1.getCount() != 0) {
						c1.moveToFirst();
						do {
							JSONObject jo1 = new JSONObject();
							jo1.put("uid", AccessSharedPrefs.mPrefs.getString(
									"id", ""));
							writeToFile("Mid:"+Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID))));
							jo1.put("mid",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID))));
							jo1.put("did",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_DEVICE_ID))));
							jo1.put("pid",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_ROWID))));
						
							jo1.put("in",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_INNING))));
							jo1.put("btn",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_NUM))));
							jo1.put("btr",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_RUNS))));
							jo1.put("btb",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BALLS))));
							jo1.put("btt",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_TIME))));
							jo1.put("btf",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FOURS))));
							jo1.put("bts",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_SIXES))));
							jo1.put("btd",
									c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_HOW_OUT)));
							jo1.put("btbot",
									c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BOWLER_TYPE)));
							jo1.put("btfp",
									c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FIELDING_POSITION)));
							jo1.put("btc",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_CHANCES))));
							jo1.put("bob",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_BALLS))));
							jo1.put("bos",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SPELLS))));
							jo1.put("bom",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_MAIDENS))));
							jo1.put("bor",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_RUNS))));
							jo1.put("bof",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_FOURS))));
							jo1.put("bosx",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SIXES))));
							jo1.put("bowl",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_LEFT))));
							jo1.put("bowr",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_RIGHT))));
							jo1.put("bocd",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_CATCHES_DROPPED))));
							jo1.put("bonb",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_NOBALLS))));
							jo1.put("bow",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WIDES))));
							jo1.put("fslc",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_SLIP_CATCH))));
							jo1.put("fclc",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CLOSE_CATCH))));
							jo1.put("fcic",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CIRCLE_CATCH))));
							jo1.put("fdc",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_DEEP_CATCH))));
							jo1.put("froci",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_CIRCLE))));
							jo1.put("frodci",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE))));
							jo1.put("frode",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DEEP))));
							jo1.put("frodde",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP))));
							jo1.put("fst",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_STUMPINGS))));
							jo1.put("fby",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_BYES))));
							jo1.put("fmf",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_MISFIELDS))));
							jo1.put("fcd",
									Integer.parseInt(c1.getString(c1
											.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CATCHES_DROPPED))));

							ja.put(jo1);
							c1.moveToNext();
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
					writeToFile("match data: " + cm.toString());
					writeToFile("per data: " + per.toString());
					int trial = 1;
					JSONObject jn = null;
					while (jsonParser.isOnline(who)) {
						Log.w("JSONParser", "MatchHistory: Called");

						jn = jsonParser.makeHttpRequest(getResources()
								.getString(R.string.gae_match_insert), "POST",
								params, who);
						Log.w("JSON returned", "MatchHistory: " + jn);
						Log.w("trial value", "MatchHistory: " + trial);

						writeToFile("gae trial: " + jn);
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

					writeToFile("GAE response: " + jn);
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
						cm.put("per", ja);
						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("SendToArrays",
								regids));
						params.add(new BasicNameValuePair("MsgToSend", cm
								.toString()));
						jsonParser = new JSONParser();
						trial = 1;
						JSONObject jn1 = null;
						while (jsonParser.isOnline(who)) {
							Log.w("JSONParser", "Send gcm: Called");
							jn1 = jsonParser.makeHttpRequest(getResources()
									.getString(R.string.azure_sendgcm), "POST",
									params, who);
							Log.w("JSON returned", "Send Gcm: " + jn1);
							Log.w("trial value", "Send Gcm: " + trial);
							if (jn1 != null)
								break;
							try {
								Thread.sleep(10 * trial);
							} catch (InterruptedException e) {
							}
							trial++;

							if (trial == 50)
								break;
						}

						writeToFile("send gcm azure: " + jn1);

						if (jn1 == null) {
							while (jsonParser.isOnline(who)) {
								Log.w("JSONParser",
										"ProfileEditService: Called");
								jn1 = jsonParser.makeHttpRequest(getResources()
										.getString(R.string.ping_hansa_gcm),
										"POST", params, who);
								Log.w("JSON returned", "ProfileEditService: "
										+ jn1);
								Log.w("trial value", "ProfileEditService: "
										+ trial);
								writeToFile("Ping Hansa: " + trial);
								if (jn1 != null)
									break;
								try {
									Thread.sleep(10 * trial);
								} catch (InterruptedException e) {
								}
								trial++;

								if (trial == 50)
									break;
							}

						}

						writeToFile("send gcm hansa: " + jn1);

						if (jn1 == null) {
							while (jsonParser.isOnline(who)) {
								Log.w("JSONParser",
										"ProfileEditService: Called");
								jn1 = jsonParser.makeHttpRequest(getResources()
										.getString(R.string.ping_acjs_gcm),
										"POST", params, who);
								Log.w("JSON returned", "ProfileEditService: "
										+ jn1);
								Log.w("trial value", "ProfileEditService: "
										+ trial);
								writeToFile("Ping acjs: " + trial);
								if (jn1 != null)
									break;
								try {
									Thread.sleep(10 * trial);
								} catch (InterruptedException e) {
								}
								trial++;

								if (trial == 50)
									break;
							}

						}
						writeToFile("send gcm acjs: " + jn1);
						tcount++;

					} else {
						break;
					}
				} catch (Exception e) {
					Log.w("exception", "np");
					writeToFile("exception: "+e);
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
				params.add(new BasicNameValuePair("user_id",
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

				writeToFile("chking inif_sync: " + jn);
				try {
					if (jn.getInt("status") == 1) {
						writeToFile("calling send data");
						AccessSharedPrefs.setString(who, "infi_sync", "yes");
						sendData(jn.getString("reg_ids"));
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
						sendData(jn.getString("reg_ids"));
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

	public static void writeToFile(String data) {

		try {

			File root = new File(Environment.getExternalStorageDirectory(),
					"CricDeCode");

			if (!root.exists()) {

				root.mkdirs();
			}

			File gpxfile = new File(root, "matchsync.txt");

			FileWriter writer = new FileWriter(gpxfile, true);
			writer.write(data + "\n");
			writer.flush();

			writer.close();

		} catch (IOException e) {

			Log.e("Exception", "File write failed: " + e.toString());

		}

	}

}