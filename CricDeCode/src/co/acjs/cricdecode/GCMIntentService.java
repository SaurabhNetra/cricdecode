package co.acjs.cricdecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class GCMIntentService extends GCMBaseIntentServiceCompat {
	public static final int UPDATE_PROFILE_DATA = 1;
	public static final int MATCH_N_PERFORMANCE_DATA = 2;
	public static final int DELETE_MATCH = 3;
	public static final int REMOVE_ADS = 4;
	public static final int SUB_INFI = 5;
	public static final int SUB_INFI_SYNC = 6;
	public static final int NO_REMOVE_ADS = 7;
	public static final int NO_SUB_INFI = 8;
	public static final int NO_SUB_INFI_SYNC = 9;
	public static final int SUB_SYNC = 10;
	public static final int NO_SUB_SYNC = 11;
	public static Context context;
	static SQLiteDatabase dbHandle;
	ContentProviderClient client;
	Uri uri;
	String m_devid, p_devid;

	public GCMIntentService() {
		super("GCMIntentService");
		context = this;
	}

	@Override
	protected void onMessage(Intent message) {

		client = getContentResolver().acquireContentProviderClient(
				CricDeCodeContentProvider.AUTHORITY);
		dbHandle = ((CricDeCodeContentProvider) client
				.getLocalContentProvider()).getDbHelper().getReadableDatabase();
		AccessSharedPrefs.mPrefs = context.getSharedPreferences("CricDeCode",
				Context.MODE_PRIVATE);
		final String gcmString = message.getStringExtra("cricdecode");
		try {
			Log.w("GCM Received", "GCMData: " + gcmString.toString());
			String s = gcmString.toString();
			s = s.replace("\\", "");
			writeToFile("gcm Recieved: " + s);
			JSONObject gcmData = new JSONObject(s);
			switch (gcmData.getInt("gcmid")) {
			case UPDATE_PROFILE_DATA:
				AccessSharedPrefs.setString(context, "nickname",
						gcmData.getString("nickname"));
				AccessSharedPrefs.setString(context, "role",
						gcmData.getString("role"));
				AccessSharedPrefs.setString(context, "battingStyle",
						gcmData.getString("battingStyle"));
				AccessSharedPrefs.setString(context, "bowlingStyle",
						gcmData.getString("bowlingStyle"));
				Log.w("Updating profile data", "with gcm data");
				try {
					((MainActivity) MainActivity.main_context)
							.runOnUiThread(new Runnable() {
								public void run() {
									try {
										((TextView) ((SherlockFragmentActivity) MainActivity.main_context)
												.findViewById(R.id.role))
												.setText(AccessSharedPrefs.mPrefs
														.getString("role", ""));
										((TextView) ((SherlockFragmentActivity) MainActivity.main_context)
												.findViewById(R.id.nickname))
												.setText(AccessSharedPrefs.mPrefs
														.getString("nickname",
																""));
										((TextView) ((SherlockFragmentActivity) MainActivity.main_context)
												.findViewById(R.id.batting_style))
												.setText(AccessSharedPrefs.mPrefs
														.getString(
																"battingStyle",
																""));
										((TextView) ((SherlockFragmentActivity) MainActivity.main_context)
												.findViewById(R.id.bowling_style))
												.setText(AccessSharedPrefs.mPrefs
														.getString(
																"bowlingStyle",
																""));
									} catch (Exception e) {
									}
								}
							});
				} catch (Exception e) {
				}
				break;
			case MATCH_N_PERFORMANCE_DATA:
				writeToFile("in 2:");
			//	if (AccessSharedPrefs.mPrefs.getString("infi_sync", "no")
			//			.equals("yes")
			//			|| AccessSharedPrefs.mPrefs.getString("sync", "no")
			//					.equals("yes")) {
					try {
						ContentValues values = new ContentValues();
						writeToFile("" + gcmData.getInt("mid"));
						writeToFile(gcmData.getString("did"));
						writeToFile(gcmData.getString("a"));
						writeToFile(gcmData.getString("b"));
						writeToFile(gcmData.getString("c"));
						writeToFile(gcmData.getString("d"));
						writeToFile("" + gcmData.getInt("e"));
						writeToFile("" + gcmData.getInt("f"));
						writeToFile(gcmData.getString("g"));
						writeToFile(gcmData.getString("h"));
						writeToFile(gcmData.getString("i"));
						writeToFile(gcmData.getString("j"));
						writeToFile(gcmData.getString("k"));
						values.put(MatchDb.KEY_ROWID, gcmData.getInt("mid"));
						values.put(MatchDb.KEY_DEVICE_ID,
								gcmData.getString("did"));
						values.put(MatchDb.KEY_MATCH_DATE,
								gcmData.getString("a"));
						values.put(MatchDb.KEY_MY_TEAM, gcmData.getString("b"));
						values.put(MatchDb.KEY_OPPONENT_TEAM,
								gcmData.getString("c"));
						values.put(MatchDb.KEY_VENUE, gcmData.getString("d"));
						values.put(MatchDb.KEY_OVERS, gcmData.getInt("e"));
						values.put(MatchDb.KEY_INNINGS, gcmData.getInt("f"));
						values.put(MatchDb.KEY_RESULT, gcmData.getString("g"));
						values.put(MatchDb.KEY_LEVEL, gcmData.getString("h"));
						values.put(MatchDb.KEY_FIRST_ACTION,
								gcmData.getString("i"));
						values.put(MatchDb.KEY_DURATION,
								gcmData.getString("j"));
						values.put(MatchDb.KEY_REVIEW, gcmData.getString("k"));
						values.put(MatchDb.KEY_STATUS, MatchDb.MATCH_HOLD);
						values.put(MatchDb.KEY_SYNCED, 1);
						writeToFile("formed...");
						Cursor c = dbHandle.rawQuery(
								"select " + MatchDb.KEY_ROWID + " from "
										+ MatchDb.SQLITE_TABLE + " where "
										+ MatchDb.KEY_ROWID + " = "
										+ gcmData.getInt("mid") + " and "
										+ MatchDb.KEY_DEVICE_ID + " = '"
										+ gcmData.getString("did") + "'", null);

						writeToFile("cnt: " + c.getCount());
						if (c.getCount() == 0) {

							Uri irm = getApplicationContext()
									.getContentResolver()
									.insert(CricDeCodeContentProvider.CONTENT_URI_MATCH,
											values);
							writeToFile("Inserting match " + irm);
						}
						c.close();

						JSONArray ja1 = gcmData.getJSONArray("per");
						writeToFile("performance data: " + ja1);
						for (int j = 0; j < ja1.length(); j++) {
							JSONObject jo1 = ja1.getJSONObject(j);
							values = new ContentValues();
							values.put(PerformanceDb.KEY_MATCHID,
									gcmData.getInt("mid"));
							values.put(PerformanceDb.KEY_DEVICE_ID,
									gcmData.getString("did"));
							values.put(PerformanceDb.KEY_ROWID,
									jo1.getInt("pid"));
							values.put(PerformanceDb.KEY_INNING,
									jo1.getInt("l"));
							values.put(PerformanceDb.KEY_BAT_NUM,
									jo1.getInt("m"));
							values.put(PerformanceDb.KEY_BAT_RUNS,
									jo1.getInt("n"));
							values.put(PerformanceDb.KEY_BAT_BALLS,
									jo1.getInt("o"));
							values.put(PerformanceDb.KEY_BAT_TIME,
									jo1.getInt("p"));
							values.put(PerformanceDb.KEY_BAT_FOURS,
									jo1.getInt("q"));
							values.put(PerformanceDb.KEY_BAT_SIXES,
									jo1.getInt("r"));
							values.put(PerformanceDb.KEY_BAT_HOW_OUT,
									jo1.getString("s"));
							values.put(PerformanceDb.KEY_BAT_BOWLER_TYPE,
									jo1.getString("t"));
							values.put(PerformanceDb.KEY_BAT_FIELDING_POSITION,
									jo1.getString("u"));
							values.put(PerformanceDb.KEY_BAT_CHANCES,
									jo1.getInt("v"));
							values.put(PerformanceDb.KEY_BOWL_BALLS,
									jo1.getInt("w"));
							values.put(PerformanceDb.KEY_BOWL_SPELLS,
									jo1.getInt("x"));
							values.put(PerformanceDb.KEY_BOWL_MAIDENS,
									jo1.getInt("y"));
							values.put(PerformanceDb.KEY_BOWL_RUNS,
									jo1.getInt("z"));
							values.put(PerformanceDb.KEY_BOWL_FOURS,
									jo1.getInt("a1"));
							values.put(PerformanceDb.KEY_BOWL_SIXES,
									jo1.getInt("a2"));
							values.put(PerformanceDb.KEY_BOWL_WKTS_LEFT,
									jo1.getInt("a3"));
							values.put(PerformanceDb.KEY_BOWL_WKTS_RIGHT,
									jo1.getInt("a4"));
							values.put(PerformanceDb.KEY_BOWL_CATCHES_DROPPED,
									jo1.getInt("a5"));
							values.put(PerformanceDb.KEY_BOWL_NOBALLS,
									jo1.getInt("a6"));
							values.put(PerformanceDb.KEY_BOWL_WIDES,
									jo1.getInt("a7"));
							values.put(PerformanceDb.KEY_FIELD_SLIP_CATCH,
									jo1.getInt("a8"));
							values.put(PerformanceDb.KEY_FIELD_CLOSE_CATCH,
									jo1.getInt("a9"));
							values.put(PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
									jo1.getInt("a0"));
							values.put(PerformanceDb.KEY_FIELD_DEEP_CATCH,
									jo1.getInt("b1"));
							values.put(PerformanceDb.KEY_FIELD_RO_CIRCLE,
									jo1.getInt("b2"));
							values.put(
									PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE,
									jo1.getInt("b3"));
							values.put(PerformanceDb.KEY_FIELD_RO_DEEP,
									jo1.getInt("b4"));
							values.put(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP,
									jo1.getInt("b5"));
							values.put(PerformanceDb.KEY_FIELD_STUMPINGS,
									jo1.getInt("b6"));
							values.put(PerformanceDb.KEY_FIELD_BYES,
									jo1.getInt("b7"));
							values.put(PerformanceDb.KEY_FIELD_MISFIELDS,
									jo1.getInt("b8"));
							values.put(PerformanceDb.KEY_FIELD_CATCHES_DROPPED,
									jo1.getInt("b9"));
							values.put(PerformanceDb.KEY_SYNCED, 1);
							values.put(PerformanceDb.KEY_STATUS,
									MatchDb.MATCH_HISTORY);

							c = dbHandle.rawQuery(
									"select " + PerformanceDb.KEY_ROWID
											+ " from "
											+ PerformanceDb.SQLITE_TABLE
											+ " where "
											+ PerformanceDb.KEY_MATCHID + " = "
											+ gcmData.getInt("mid") + " and "
											+ PerformanceDb.KEY_DEVICE_ID
											+ " = '" + gcmData.getString("did")
											+ "' and "
											+ PerformanceDb.KEY_INNING + " = "
											+ jo1.getInt("l"), null);
							if (c.getCount() == 0) {
								writeToFile("Inserting perf...");
								Uri irm = getApplicationContext()
										.getContentResolver()
										.insert(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE,
												values);
								writeToFile("Inserting perf..." + irm);
							}
							c.close();

						}

						uri = Uri
								.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH
										+ "/"
										+ gcmData.getInt("mid")
										+ "/"
										+ gcmData.getString("did"));
						ContentValues matchvalues = new ContentValues();
						matchvalues.put(MatchDb.KEY_STATUS,
								MatchDb.MATCH_HISTORY);
						getApplicationContext().getContentResolver().update(
								uri, matchvalues, null, null);
					} catch (Exception e) {
						writeToFile("" + e);
					}

				//}
				break;
			case DELETE_MATCH:
				JSONArray ja2 = gcmData.getJSONArray("todel");
				writeToFile("in 3: " + ja2.toString());
				//if (AccessSharedPrefs.mPrefs.getString("infi_sync", "no")
				//		.equals("yes")
				//		|| AccessSharedPrefs.mPrefs.getString("sync", "no")
				//				.equals("yes")) {
					try {
						for (int i = 0; i < ja2.length(); i++) {
							JSONObject jo = ja2.getJSONObject(i);
							String str = "" + jo.getInt("match_id");
							String d_str = jo.getString("device_id");
							uri = Uri
									.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE
											+ "/" + str + "/" + d_str);
							getApplicationContext().getContentResolver()
									.delete(uri, null, null);
							uri = Uri
									.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH
											+ "/" + str + "/" + d_str);
							getApplicationContext().getContentResolver()
									.delete(uri, null, null);
						}

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
											Log.w("GCMSync", "UI update error"
													+ e);
										}
									}
								});
					} catch (Exception e) {
						Log.w("GCMSync", "" + e);
					}
				//}
				break;
			case REMOVE_ADS:
				Log.w("GCM: ", "gcm remove ads");
				AccessSharedPrefs.setString(this, "ad_free", "yes");
				try {
					((MainActivity) MainActivity.main_context)
							.runOnUiThread(new Runnable() {
								public void run() {
									try {
										final AdView adView = (AdView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.adView);
										adView.setAdUnitId(R.string.publisher_id
												+ "");
										adView.setAdSize(AdSize.BANNER);
										adView.setVisibility(View.GONE);
										((TextView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.rem_ads_pur))
												.setVisibility(View.VISIBLE);
									} catch (Exception e) {
									}
								}
							});
				} catch (Exception e) {
				}
				break;
			case SUB_INFI:
				Log.w("GCM: ", "gcm infi use");
				AccessSharedPrefs.setString(this, "infi_use", "yes");
				try {
					((MainActivity) MainActivity.main_context)
							.runOnUiThread(new Runnable() {
								public void run() {
									try {
										((LinearLayout) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.pur_subscribe_sync))
												.setVisibility(View.VISIBLE);
										((LinearLayout) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.pur_subscribe_infi_sync))
												.setVisibility(View.GONE);
										((TextView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.infi_pur))
												.setVisibility(View.VISIBLE);
										((TextView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.infi_pur))
												.setText("Purchased");
									} catch (Exception e) {
									}
								}
							});
				} catch (Exception e) {
				}
				break;
			case SUB_INFI_SYNC:
				Log.w("GCM: ", "gcm infi sync");
				AccessSharedPrefs.setString(this, "infi_sync", "yes");
				try {
					((MainActivity) MainActivity.main_context)
							.runOnUiThread(new Runnable() {
								public void run() {
									try {
										((TextView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.infi_sync_pur))
												.setVisibility(View.VISIBLE);
										((TextView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.infi_pur))
												.setText("Not Applicable");
										((TextView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.infi_pur))
												.setVisibility(View.VISIBLE);
									} catch (Exception e) {
									}
								}
							});
				} catch (Exception e) {
				}
				break;
			case NO_REMOVE_ADS:
				Log.w("GCM: ", "gcm no remove ads");
				AccessSharedPrefs.setString(this, "ad_free", "no");
				try {
					((MainActivity) MainActivity.main_context)
							.runOnUiThread(new Runnable() {
								public void run() {
									try {
										final AdView adView = (AdView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.adView);
										adView.setAdUnitId(R.string.publisher_id
												+ "");
										adView.setAdSize(AdSize.BANNER);
										adView.setVisibility(View.VISIBLE);
										((TextView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.rem_ads_pur))
												.setVisibility(View.GONE);
									} catch (Exception e) {
									}
								}
							});
				} catch (Exception e) {
				}
				break;
			case NO_SUB_INFI:
				Log.w("GCM: ", "gcm no infi use");
				AccessSharedPrefs.setString(this, "infi_use", "no");
				try {
					((MainActivity) MainActivity.main_context)
							.runOnUiThread(new Runnable() {
								public void run() {
									try {
										((TextView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.infi_pur))
												.setVisibility(View.GONE);
									} catch (Exception e) {
									}
								}
							});
				} catch (Exception e) {
				}
				break;
			case NO_SUB_INFI_SYNC:
				Log.w("GCM: ", "gcm no infi sync");
				AccessSharedPrefs.setString(this, "infi_sync", "no");
				try {
					((MainActivity) MainActivity.main_context)
							.runOnUiThread(new Runnable() {
								public void run() {
									try {
										((TextView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.infi_sync_pur))
												.setVisibility(View.GONE);
									} catch (Exception e) {
									}
								}
							});
				} catch (Exception e) {
				}
				break;
			case NO_SUB_SYNC:
				Log.w("GCM: ", "gcm no sync");
				AccessSharedPrefs.setString(this, "sync", "no");
				try {
					((MainActivity) MainActivity.main_context)
							.runOnUiThread(new Runnable() {
								public void run() {
									try {
										((TextView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.sync_pur))
												.setVisibility(View.GONE);
									} catch (Exception e) {
									}
								}
							});
				} catch (Exception e) {
				}
				break;
			case SUB_SYNC:
				Log.w("GCM: ", "gcm sync");
				AccessSharedPrefs.setString(this, "sync", "yes");
				try {
					((MainActivity) MainActivity.main_context)
							.runOnUiThread(new Runnable() {
								public void run() {
									try {
										((TextView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.sync_pur))
												.setVisibility(View.VISIBLE);
									} catch (Exception e) {
									}
								}
							});
				} catch (Exception e) {
				}
				break;
			}
		} catch (JSONException e) {
			Log.w("Json exception", "ex in gcm" + e);
		}
	}

	@Override
	protected void onError(Intent message) {
		dumpEvent("onError", message);
	}

	@Override
	protected void onDeleted(Intent message) {
		dumpEvent("onDeleted", message);
	}

	private void dumpEvent(String event, Intent message) {
		Bundle extras = message.getExtras();
		for (String key : extras.keySet()) {
			Log.d(getClass().getSimpleName(),
					String.format("%s: %s=%s", event, key,
							extras.getString(key)));
		}
	}

	public static void writeToFile(String data) {

		try {

			File root = new File(Environment.getExternalStorageDirectory(),
					"CricDeCode");

			if (!root.exists()) {

				root.mkdirs();
			}

			File gpxfile = new File(root, "gcm.txt");

			FileWriter writer = new FileWriter(gpxfile, true);
			writer.write(data + "\n");
			writer.flush();

			writer.close();

		} catch (IOException e) {

			Log.e("Exception", "File write failed: " + e.toString());

		}

	}
}
