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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class DeleteMatchService extends IntentService {
	public static boolean started = true;
	public static Context who;
	List<NameValuePair> params;
	JSONParser jsonParser;
	int trial;
	JSONObject jn;

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
		JSONObject del_gcm = new JSONObject();
		JSONArray ja = new JSONArray();
		JSONArray ja_gcm = new JSONArray();
		writeToFile("del cnt: " + c.getCount());
		try {
			del_matches.put("user_id",
					AccessSharedPrefs.mPrefs.getString("id", ""));
			if (c.getCount() != 0) {
				c.moveToFirst();
				do {
					JSONObject jo = new JSONObject();
					JSONObject jo_gcm = new JSONObject();
					jo.put("mid", c.getInt(c
							.getColumnIndexOrThrow(MatchDb.KEY_ROWID)));
					jo.put("devid", Integer.parseInt(c.getString(c
							.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID))));
					jo_gcm.put("match_id", c.getInt(c
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
			writeToFile("" + del_matches.toString());

			params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("del_matches", del_matches
					.toString()));
			jsonParser = new JSONParser();
			trial = 1;
			jn = null;
			while (jsonParser.isOnline(who)) {
				Log.w("JSONParser", "DeleteMatch:: Called");
				jn = jsonParser.makeHttpRequest(
						getResources().getString(R.string.gae_match_delete),
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
				if (jn.getInt("status") == 1) {

					// get regids
					jsonParser = new JSONParser();
					params.add(new BasicNameValuePair("user_id",
							AccessSharedPrefs.mPrefs.getString("id", "")));
					trial = 1;
					jn = null;
					while (jsonParser.isOnline(who)) {
						jn = jsonParser.makeHttpRequest(getResources()
								.getString(R.string.gae_andro_retrieve),
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
					writeToFile("gcmids: " + jn);
					if (jn != null) {
						del_gcm.put("gcmid", 3);
						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("SendToArrays", jn
								.getString("reg_ids")));
						params.add(new BasicNameValuePair("MsgToSend", del_gcm
								.toString()));
						params.add(new BasicNameValuePair("uid",
								AccessSharedPrefs.mPrefs.getString("id", "")));
						jsonParser = new JSONParser();
						writeToFile("Sending gcm: " + del_gcm.toString());
						trial = 1;
						jn = null;
						while (jsonParser.isOnline(who)) {
							jn = jsonParser.makeHttpRequest(getResources()
									.getString(R.string.azure_sendgcm), "POST",
									params, who);
							Log.w("JSON returned", "DeleteMatch:: " + jn);
							Log.w("trial value", "DeleteMatch:: " + trial);
							writeToFile("ping azure t:" + trial);
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
						writeToFile("ping azure: " + jn);
						if (jn == null) {
							while (jsonParser.isOnline(who)) {
								jn = jsonParser.makeHttpRequest(getResources()
										.getString(R.string.ping_hansa_gcm),
										"POST", params, who);
								Log.w("JSON returned", "DeleteMatch:: " + jn);
								Log.w("trial value", "DeleteMatch:: " + trial);
								writeToFile("ping hansa t:" + trial);
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

						}
						writeToFile("ping hansa:" + jn);
						if (jn == null) {
							while (jsonParser.isOnline(who)) {
								jn = jsonParser.makeHttpRequest(getResources()
										.getString(R.string.ping_acjs_gcm),
										"POST", params, who);
								Log.w("JSON returned", "DeleteMatch:: " + jn);
								Log.w("trial value", "DeleteMatch:: " + trial);
								writeToFile("ping gcm t:" + trial);
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
						}
						writeToFile("ping gcm:" + jn);
						if (jn != null) {

							c = getContentResolver()
									.query(CricDeCodeContentProvider.CONTENT_URI_MATCH,
											new String[] { MatchDb.KEY_ROWID,
													MatchDb.KEY_DEVICE_ID,
													MatchDb.KEY_MATCH_DATE,
													MatchDb.KEY_MY_TEAM,
													MatchDb.KEY_OPPONENT_TEAM,
													MatchDb.KEY_VENUE,
													MatchDb.KEY_OVERS,
													MatchDb.KEY_INNINGS,
													MatchDb.KEY_RESULT,
													MatchDb.KEY_LEVEL,
													MatchDb.KEY_FIRST_ACTION,
													MatchDb.KEY_DURATION,
													MatchDb.KEY_REVIEW,
													MatchDb.KEY_STATUS,
													MatchDb.KEY_SYNCED },
											MatchDb.KEY_STATUS + "='"
													+ MatchDb.MATCH_DELETED
													+ "'", null,
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

	public static void writeToFile(String data) {

		try {

			File root = new File(Environment.getExternalStorageDirectory(),
					"CricDeCode");

			if (!root.exists()) {

				root.mkdirs();
			}

			File gpxfile = new File(root, "delete.txt");

			FileWriter writer = new FileWriter(gpxfile, true);
			writer.write(data + "\n");
			writer.flush();

			writer.close();

		} catch (IOException e) {

			Log.e("Exception", "File write failed: " + e.toString());

		}

	}
}