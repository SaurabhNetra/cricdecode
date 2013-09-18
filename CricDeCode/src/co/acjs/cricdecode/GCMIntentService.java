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
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class GCMIntentService extends GCMBaseIntentServiceCompat {
	public static final int	UPDATE_PROFILE_DATA			= 1;
	public static final int	MATCH_N_PERFORMANCE_DATA	= 2;
	public static Context	context;

	public GCMIntentService() {
		super("GCMIntentService");
		context = this;
	}

	private void writeToFile(String data) {
		try {
			File root = new File(Environment.getExternalStorageDirectory(),
					"CricDeCode");
			if (!root.exists()) {
				root.mkdirs();
			}

			File gpxfile = new File(root, "gcm.txt");
			FileWriter writer = new FileWriter(gpxfile);
			writer.write(data);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	@Override
	protected void onMessage(Intent message) {
		AccessSharedPrefs.mPrefs = context.getSharedPreferences("CricDeCode",
				Context.MODE_PRIVATE);
		final String gcmString = message.getStringExtra("cricdecode");
		try {
			Log.w("GCM Received", "GCMData: " + gcmString.toString());
			JSONObject gcmData = new JSONObject(gcmString.toString());
			writeToFile(gcmString.toString());

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
					break;
				case MATCH_N_PERFORMANCE_DATA:
					Log.w("JSON Object", "Match Data: " + gcmData.getInt("mid"));
					if (!AccessSharedPrefs.mPrefs.getString("device_id", "")
							.equals(gcmData.getInt("dev") + "")) {
						ContentValues values = new ContentValues();
						values.put(MatchDb.KEY_ROWID,
								Integer.parseInt(gcmData.getString("mid")));
						values.put(MatchDb.KEY_DEVICE_ID,
								gcmData.getString("dev"));
						values.put(MatchDb.KEY_MATCH_DATE,
								gcmData.getString("dat"));
						values.put(MatchDb.KEY_MY_TEAM,
								gcmData.getString("myt"));
						values.put(MatchDb.KEY_OPPONENT_TEAM,
								gcmData.getString("opp"));
						values.put(MatchDb.KEY_VENUE, gcmData.getString("ven"));
						values.put(MatchDb.KEY_OVERS,
								Integer.parseInt(gcmData.getString("ovr")));
						values.put(MatchDb.KEY_INNINGS,
								Integer.parseInt(gcmData.getString("inn")));
						values.put(MatchDb.KEY_RESULT, gcmData.getString("res"));
						values.put(MatchDb.KEY_LEVEL, gcmData.getString("lvl"));
						values.put(MatchDb.KEY_FIRST_ACTION,
								gcmData.getString("act"));
						values.put(MatchDb.KEY_DURATION,
								gcmData.getString("dur"));
						values.put(MatchDb.KEY_REVIEW, gcmData.getString("rev"));
						values.put(MatchDb.KEY_STATUS, gcmData.getString("sts"));
						// insert a record
						getApplicationContext().getContentResolver().insert(
								CricDeCodeContentProvider.CONTENT_URI_MATCH,
								values);

						JSONArray ja = gcmData.getJSONArray("per");
						for (int i = 0; i < ja.length(); i++) {
							JSONObject jo = ja.getJSONObject(i);
							ContentValues value = new ContentValues();

							value.put(PerformanceDb.KEY_MATCHID,
									Integer.parseInt(jo.getString("mid")));
							value.put(PerformanceDb.KEY_DEVICE_ID,
									gcmData.getString("dev"));
							value.put(PerformanceDb.KEY_ROWID,
									Integer.parseInt(jo.getString("pid")));
							value.put(PerformanceDb.KEY_INNING,
									Integer.parseInt(jo.getString("inn")));

							value.put(PerformanceDb.KEY_BAT_NUM,
									Integer.parseInt(jo.getString("bn")));
							value.put(PerformanceDb.KEY_BAT_RUNS,
									Integer.parseInt(jo.getString("br")));
							value.put(PerformanceDb.KEY_BAT_BALLS,
									Integer.parseInt(jo.getString("bb")));
							value.put(PerformanceDb.KEY_BAT_TIME,
									Integer.parseInt(jo.getString("bt")));
							value.put(PerformanceDb.KEY_BAT_FOURS,
									Integer.parseInt(jo.getString("bf")));
							value.put(PerformanceDb.KEY_BAT_SIXES,
									Integer.parseInt(jo.getString("bs")));
							value.put(PerformanceDb.KEY_BAT_HOW_OUT,
									jo.getString("bho"));
							value.put(PerformanceDb.KEY_BAT_BOWLER_TYPE,
									jo.getString("bbt"));
							value.put(PerformanceDb.KEY_BAT_FIELDING_POSITION,
									jo.getString("bfp"));
							value.put(PerformanceDb.KEY_BAT_CHANCES,
									Integer.parseInt(jo.getString("bc")));

							value.put(PerformanceDb.KEY_BOWL_BALLS,
									Integer.parseInt(jo.getString("ob")));
							value.put(PerformanceDb.KEY_BOWL_SPELLS,
									Integer.parseInt(jo.getString("osp")));
							value.put(PerformanceDb.KEY_BOWL_MAIDENS,
									Integer.parseInt(jo.getString("om")));
							value.put(PerformanceDb.KEY_BOWL_RUNS,
									Integer.parseInt(jo.getString("oru")));
							value.put(PerformanceDb.KEY_BOWL_FOURS,
									Integer.parseInt(jo.getString("of")));
							value.put(PerformanceDb.KEY_BOWL_SIXES,
									Integer.parseInt(jo.getString("osx")));
							value.put(PerformanceDb.KEY_BOWL_WKTS_LEFT,
									Integer.parseInt(jo.getString("owl")));
							value.put(PerformanceDb.KEY_BOWL_WKTS_RIGHT,
									Integer.parseInt(jo.getString("owr")));
							value.put(PerformanceDb.KEY_BOWL_CATCHES_DROPPED,
									Integer.parseInt(jo.getString("ocd")));
							value.put(PerformanceDb.KEY_BOWL_NOBALLS,
									Integer.parseInt(jo.getString("ono")));
							value.put(PerformanceDb.KEY_BOWL_WIDES,
									Integer.parseInt(jo.getString("ow")));

							value.put(PerformanceDb.KEY_FIELD_SLIP_CATCH,
									Integer.parseInt(jo.getString("fsc")));
							value.put(PerformanceDb.KEY_FIELD_CLOSE_CATCH,
									Integer.parseInt(jo.getString("fco")));
							value.put(PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
									Integer.parseInt(jo.getString("fcc")));
							value.put(PerformanceDb.KEY_FIELD_DEEP_CATCH,
									Integer.parseInt(jo.getString("fdc")));
							value.put(PerformanceDb.KEY_FIELD_RO_CIRCLE,
									Integer.parseInt(jo.getString("frc")));
							value.put(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE,
									Integer.parseInt(jo.getString("fci")));
							value.put(PerformanceDb.KEY_FIELD_RO_DEEP,
									Integer.parseInt(jo.getString("frd")));
							value.put(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP,
									Integer.parseInt(jo.getString("fdd")));
							value.put(PerformanceDb.KEY_FIELD_STUMPINGS,
									Integer.parseInt(jo.getString("fs")));
							value.put(PerformanceDb.KEY_FIELD_BYES,
									Integer.parseInt(jo.getString("fb")));
							value.put(PerformanceDb.KEY_FIELD_MISFIELDS,
									Integer.parseInt(jo.getString("fmf")));
							value.put(PerformanceDb.KEY_FIELD_CATCHES_DROPPED,
									jo.getString("fcd"));

							value.put(PerformanceDb.KEY_STATUS,
									jo.getString("sts"));

							getApplicationContext()
									.getContentResolver()
									.insert(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE,
											value);

						}
					}
					break;
			}
		} catch (JSONException e) {
		}

		try {
			((MainActivity) MainActivity.main_context)
					.runOnUiThread(new Runnable() {
						public void run() {
							DiaryMatchesFragment.loader_diary_list
									.restartLoader(
											0,
											null,
											DiaryMatchesFragment.diary_matches_fragment);
						}
					});
		} catch (Exception e) {

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
}