package co.acjs.cricdecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.google.android.gms.ads.*;

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

	public GCMIntentService() {
		super("GCMIntentService");
		context = this;
	}

	@Override
	protected void onMessage(Intent message) {
		AccessSharedPrefs.mPrefs = context.getSharedPreferences("CricDeCode",
				Context.MODE_PRIVATE);
		final String gcmString = message.getStringExtra("cricdecode");
		try {
			Log.w("GCM Received", "GCMData: " + gcmString.toString());
			String s = gcmString.toString();
			s = s.replace("\\", "");
			writeToFile("gcm Recieved: "+s);
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
				Log.w("Match and Per Sync", "GCM");
				if (AccessSharedPrefs.mPrefs.getString("infi_sync", "no")
						.equals("yes")
						|| AccessSharedPrefs.mPrefs.getString("sync", "no")
								.equals("yes")) {

					ContentValues values = new ContentValues();
					values.put(MatchDb.KEY_ROWID, gcmData.getInt("match_id"));
					values.put(MatchDb.KEY_DEVICE_ID,
							gcmData.getString("device_id"));
					values.put(MatchDb.KEY_MATCH_DATE,
							gcmData.getString("match_date"));
					values.put(MatchDb.KEY_MY_TEAM,
							gcmData.getString("my_team"));
					values.put(MatchDb.KEY_OPPONENT_TEAM,
							gcmData.getString("my_opponent"));
					values.put(MatchDb.KEY_VENUE, gcmData.getString("venue"));
					values.put(MatchDb.KEY_OVERS, gcmData.getInt("overs"));
					values.put(MatchDb.KEY_INNINGS, gcmData.getInt("innings"));
					values.put(MatchDb.KEY_RESULT, gcmData.getString("result"));
					values.put(MatchDb.KEY_LEVEL, gcmData.getString("level"));
					values.put(MatchDb.KEY_FIRST_ACTION,
							gcmData.getString("first_action"));
					values.put(MatchDb.KEY_DURATION, gcmData.getInt("duration"));
					values.put(MatchDb.KEY_REVIEW, gcmData.getString("review"));
					values.put(MatchDb.KEY_STATUS, MatchDb.MATCH_HOLD);
					values.put(MatchDb.KEY_SYNCED, 1);
					Cursor c = dbHandle.rawQuery(
							"select " + MatchDb.KEY_ROWID + " from "
									+ MatchDb.SQLITE_TABLE + " where "
									+ MatchDb.KEY_ROWID + " = "
									+ gcmData.getInt("match_id") + " and "
									+ MatchDb.KEY_DEVICE_ID + " = '"
									+ gcmData.getString("device_id") + "'",
							null);
					if (c.getCount() == 0) {
						getApplicationContext().getContentResolver().insert(
								CricDeCodeContentProvider.CONTENT_URI_MATCH,
								values);
					}
					c.close();

					JSONArray ja1 = gcmData.getJSONArray("per");
					for (int j = 0; j < ja1.length(); j++) {
						JSONObject jo1 = ja1.getJSONObject(j);
						values = new ContentValues();
						values.put(PerformanceDb.KEY_MATCHID,
								jo1.getInt("match_id"));
						values.put(PerformanceDb.KEY_DEVICE_ID,
								jo1.getString("device_id"));
						values.put(PerformanceDb.KEY_ROWID,
								jo1.getInt("per_id"));
						values.put(PerformanceDb.KEY_INNING,
								jo1.getInt("inning"));
						values.put(PerformanceDb.KEY_BAT_NUM,
								jo1.getInt("bat_num"));
						values.put(PerformanceDb.KEY_BAT_RUNS,
								jo1.getInt("bat_runs"));
						values.put(PerformanceDb.KEY_BAT_BALLS,
								jo1.getInt("bat_balls"));
						values.put(PerformanceDb.KEY_BAT_TIME,
								jo1.getInt("bat_time"));
						values.put(PerformanceDb.KEY_BAT_FOURS,
								jo1.getInt("bat_fours"));
						values.put(PerformanceDb.KEY_BAT_SIXES,
								jo1.getInt("bat_sixes"));
						values.put(PerformanceDb.KEY_BAT_HOW_OUT,
								jo1.getString("bat_dismissal"));
						values.put(PerformanceDb.KEY_BAT_BOWLER_TYPE,
								jo1.getString("bat_bowler_type"));
						values.put(PerformanceDb.KEY_BAT_FIELDING_POSITION,
								jo1.getString("bat_fielding_position"));
						values.put(PerformanceDb.KEY_BAT_CHANCES,
								jo1.getInt("bat_chances"));
						values.put(PerformanceDb.KEY_BOWL_BALLS,
								jo1.getInt("bowl_balls"));
						values.put(PerformanceDb.KEY_BOWL_SPELLS,
								jo1.getInt("bowl_spells"));
						values.put(PerformanceDb.KEY_BOWL_MAIDENS,
								jo1.getInt("bowl_maidens"));
						values.put(PerformanceDb.KEY_BOWL_RUNS,
								jo1.getInt("bowl_runs"));
						values.put(PerformanceDb.KEY_BOWL_FOURS,
								jo1.getInt("bowl_fours"));
						values.put(PerformanceDb.KEY_BOWL_SIXES,
								jo1.getInt("bowl_sixes"));
						values.put(PerformanceDb.KEY_BOWL_WKTS_LEFT,
								jo1.getInt("bowl_wkts_left"));
						values.put(PerformanceDb.KEY_BOWL_WKTS_RIGHT,
								jo1.getInt("bowl_wkts_right"));
						values.put(PerformanceDb.KEY_BOWL_CATCHES_DROPPED,
								jo1.getInt("bowl_catches_dropped"));
						values.put(PerformanceDb.KEY_BOWL_NOBALLS,
								jo1.getInt("bowl_no_balls"));
						values.put(PerformanceDb.KEY_BOWL_WIDES,
								jo1.getInt("bowl_wides"));
						values.put(PerformanceDb.KEY_FIELD_SLIP_CATCH,
								jo1.getInt("field_slip_catch"));
						values.put(PerformanceDb.KEY_FIELD_CLOSE_CATCH,
								jo1.getInt("field_close_catch"));
						values.put(PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
								jo1.getInt("field_circle_catch"));
						values.put(PerformanceDb.KEY_FIELD_DEEP_CATCH,
								jo1.getInt("field_deep_catch"));
						values.put(PerformanceDb.KEY_FIELD_RO_CIRCLE,
								jo1.getInt("field_ro_circle"));
						values.put(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE,
								jo1.getInt("field_ro_direct_circle"));
						values.put(PerformanceDb.KEY_FIELD_RO_DEEP,
								jo1.getInt("field_ro_deep"));
						values.put(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP,
								jo1.getInt("field_ro_direct_deep"));
						values.put(PerformanceDb.KEY_FIELD_STUMPINGS,
								jo1.getInt("field_stumpings"));
						values.put(PerformanceDb.KEY_FIELD_BYES,
								jo1.getInt("field_byes"));
						values.put(PerformanceDb.KEY_FIELD_MISFIELDS,
								jo1.getInt("field_misfield"));
						values.put(PerformanceDb.KEY_FIELD_CATCHES_DROPPED,
								jo1.getInt("field_catches_dropped"));
						values.put(PerformanceDb.KEY_SYNCED, 1);
						values.put(PerformanceDb.KEY_STATUS,
								MatchDb.MATCH_HISTORY);

						c = dbHandle.rawQuery(
								"select " + PerformanceDb.KEY_ROWID + " from "
										+ PerformanceDb.SQLITE_TABLE
										+ " where " + PerformanceDb.KEY_MATCHID
										+ " = " + jo1.getInt("match_id")
										+ " and " + PerformanceDb.KEY_DEVICE_ID
										+ " = '" + jo1.getInt("device_id")
										+ "' and " + PerformanceDb.KEY_INNING
										+ " = " + jo1.getInt("innings"), null);
						if (c.getCount() == 0) {
							getApplicationContext()
									.getContentResolver()
									.insert(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE,
											values);
						}
						c.close();

					}

					Uri uri = Uri
							.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH
									+ "/" + gcmData.getInt("match_id") + "/"
									+ gcmData.getInt("device_id"));
					ContentValues matchvalues = new ContentValues();
					matchvalues.put(MatchDb.KEY_STATUS, MatchDb.MATCH_HISTORY);
					getApplicationContext().getContentResolver().update(uri,
							matchvalues, null, null);

				}
				break;
			case DELETE_MATCH:
				JSONArray ja2 = gcmData.getJSONArray("todel");
				Log.w("GCM delete Match", "with gcm data");
				if (AccessSharedPrefs.mPrefs.getString("infi_sync", "no")
						.equals("yes")
						|| AccessSharedPrefs.mPrefs.getString("sync", "no")
								.equals("yes")) {
					for (int i = 0; i < ja2.length(); i++) {
						JSONObject jo = ja2.getJSONObject(i);
						String str = jo.getString("match_id");
						String d_str = jo.getString("device_id");
						Uri uri = Uri
								.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE
										+ "/" + str + "/" + d_str);
						getApplicationContext().getContentResolver().delete(
								uri, null, null);
						uri = Uri
								.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH
										+ "/" + str + "/" + d_str);
						getApplicationContext().getContentResolver().delete(
								uri, null, null);
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
											Log.w("GCMSync", "UI update error"
													+ e);
										}
									}
								});
					} catch (Exception e) {
						Log.w("GCMSync", "UI update error" + e);
					}
				}
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
