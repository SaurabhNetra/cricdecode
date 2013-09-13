package co.acjs.cricdecode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GCMIntentService extends GCMBaseIntentServiceCompat {
	public static final int	UPDATE_PROFILE_DATA			= 1;
	public static final int	MATCH_N_PERFORMANCE_DATA	= 2;
	public static Context	context;

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
			JSONObject gcmData = new JSONObject(gcmString.toString());
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
						values.put(MatchDb.KEY_ROWID, gcmData.getInt("mid"));
						values.put(MatchDb.KEY_DEVICE_ID, gcmData.getInt("dev"));
						values.put(MatchDb.KEY_MATCH_DATE,
								gcmData.getString("dat"));
						values.put(MatchDb.KEY_MY_TEAM,
								gcmData.getString("myt"));
						values.put(MatchDb.KEY_OPPONENT_TEAM,
								gcmData.getString("opp"));
						values.put(MatchDb.KEY_VENUE, gcmData.getString("ven"));
						values.put(MatchDb.KEY_OVERS, gcmData.getInt("ovr"));
						values.put(MatchDb.KEY_INNINGS, gcmData.getInt("inn"));
						values.put(MatchDb.KEY_RESULT, gcmData.getString("res"));
						values.put(MatchDb.KEY_LEVEL, gcmData.getString("lvl"));
						values.put(MatchDb.KEY_FIRST_ACTION,
								gcmData.getString("act"));
						values.put(MatchDb.KEY_DURATION,
								gcmData.getString("dur"));
						values.put(MatchDb.KEY_REVIEW, gcmData.getString("rev"));
						values.put(MatchDb.KEY_STATUS, gcmData.getInt("sts"));
						// insert a record
						getApplicationContext().getContentResolver().insert(
								CricDeCodeContentProvider.CONTENT_URI_MATCH,
								values);

						JSONArray ja = gcmData.getJSONArray("per");
						for (int i = 0; i < ja.length(); i++) {
							JSONObject jo = ja.getJSONObject(i);
							ContentValues value = new ContentValues();

							value.put(PerformanceDb.KEY_MATCHID,
									jo.getInt("mid"));
							value.put(PerformanceDb.KEY_DEVICE_ID,
									gcmData.getInt("dev"));
							value.put(PerformanceDb.KEY_ROWID, jo.getInt("pid"));
							value.put(PerformanceDb.KEY_INNING,
									jo.getInt("inn"));

							value.put(PerformanceDb.KEY_BAT_NUM,
									jo.getInt("bn"));
							value.put(PerformanceDb.KEY_BAT_RUNS,
									jo.getInt("br"));
							value.put(PerformanceDb.KEY_BAT_BALLS,
									jo.getInt("bb"));
							value.put(PerformanceDb.KEY_BAT_TIME,
									jo.getInt("bt"));
							value.put(PerformanceDb.KEY_BAT_FOURS,
									jo.getInt("bf"));
							value.put(PerformanceDb.KEY_BAT_SIXES,
									jo.getInt("bs"));
							value.put(PerformanceDb.KEY_BAT_HOW_OUT,
									jo.getString("bho"));
							value.put(PerformanceDb.KEY_BAT_BOWLER_TYPE,
									jo.getString("bbt"));
							value.put(PerformanceDb.KEY_BAT_FIELDING_POSITION,
									jo.getString("bfp"));
							value.put(PerformanceDb.KEY_BAT_CHANCES,
									jo.getInt("bc"));

							value.put(PerformanceDb.KEY_BOWL_BALLS,
									jo.getInt("ob"));
							value.put(PerformanceDb.KEY_BOWL_SPELLS,
									jo.getInt("osp"));
							value.put(PerformanceDb.KEY_BOWL_MAIDENS,
									jo.getInt("om"));
							value.put(PerformanceDb.KEY_BOWL_RUNS,
									jo.getInt("oru"));
							value.put(PerformanceDb.KEY_BOWL_FOURS,
									jo.getInt("of"));
							value.put(PerformanceDb.KEY_BOWL_SIXES,
									jo.getInt("osx"));
							value.put(PerformanceDb.KEY_BOWL_WKTS_LEFT,
									jo.getInt("owl"));
							value.put(PerformanceDb.KEY_BOWL_WKTS_RIGHT,
									jo.getInt("owr"));
							value.put(PerformanceDb.KEY_BOWL_CATCHES_DROPPED,
									jo.getInt("ocd"));
							value.put(PerformanceDb.KEY_BOWL_NOBALLS,
									jo.getInt("ono"));
							value.put(PerformanceDb.KEY_BOWL_WIDES,
									jo.getInt("ow"));

							value.put(PerformanceDb.KEY_FIELD_SLIP_CATCH,
									jo.getInt("fsc"));
							value.put(PerformanceDb.KEY_FIELD_CLOSE_CATCH,
									jo.getInt("fco"));
							value.put(PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
									jo.getInt("fcc"));
							value.put(PerformanceDb.KEY_FIELD_DEEP_CATCH,
									jo.getInt("fdc"));
							value.put(PerformanceDb.KEY_FIELD_RO_CIRCLE,
									jo.getInt("frc"));
							value.put(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE,
									jo.getInt("fci"));
							value.put(PerformanceDb.KEY_FIELD_RO_DEEP,
									jo.getInt("frd"));
							value.put(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP,
									jo.getInt("fdd"));
							value.put(PerformanceDb.KEY_FIELD_STUMPINGS,
									jo.getInt("fs"));
							value.put(PerformanceDb.KEY_FIELD_BYES,
									jo.getInt("fb"));
							value.put(PerformanceDb.KEY_FIELD_MISFIELDS,
									jo.getInt("fmf"));
							value.put(PerformanceDb.KEY_FIELD_CATCHES_DROPPED,
									jo.getInt("fcd"));

							value.put(PerformanceDb.KEY_STATUS,
									jo.getInt("sts"));

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