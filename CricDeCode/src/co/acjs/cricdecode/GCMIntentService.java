package co.acjs.cricdecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.ads.AdView;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class GCMIntentService extends GCMBaseIntentServiceCompat {
	public static final int UPDATE_PROFILE_DATA = 1;
	public static final int MATCH_N_PERFORMANCE_DATA = 2;
	public static final int DELETE_MATCH = 3;
	public static final int REMOVE_ADS = 4;
	public static final int SUB_INFI = 5;
	public static final int SUB_INFI_SYNC = 6;
	public static Context context;

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
			String s = gcmString.toString();
			s = s.replace("\\", "");
			s = s.substring(1, s.length() - 1);
			writeToFile(s);
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
				JSONArray ja = gcmData.getJSONArray("matches");
				for (int i = 0; i < ja.length(); i++) {
					JSONObject jo = ja.getJSONObject(i);
					ServerDBCricketMatch.query(
							ServerDBCricketMatch.class,
							new StackMobQuery()
									.field(new StackMobQueryField("match_id")
											.isEqualTo(Integer.parseInt(jo
													.getString("mid"))))
									.field(new StackMobQueryField("device_id")
											.isEqualTo(Integer.parseInt(jo
													.getString("dev"))))
									.field(new StackMobQueryField("user_id")
											.isEqualTo(AccessSharedPrefs.mPrefs
													.getString("id", ""))),
							new StackMobQueryCallback<ServerDBCricketMatch>() {

								@Override
								public void failure(StackMobException arg0) {
									// TODO Auto-generated method stub

								}

								@Override
								public void success(
										List<ServerDBCricketMatch> arg0) {
									ContentValues values = new ContentValues();
									values.put(MatchDb.KEY_ROWID, arg0.get(0)
											.getMatchId());
									values.put(MatchDb.KEY_DEVICE_ID, ""
											+ arg0.get(0).getDeviceId());
									values.put(MatchDb.KEY_MATCH_DATE, arg0
											.get(0).getMatchDate());
									values.put(MatchDb.KEY_MY_TEAM, arg0.get(0)
											.getMyTeam());
									values.put(MatchDb.KEY_OPPONENT_TEAM, arg0
											.get(0).getOpponentTeam());
									values.put(MatchDb.KEY_VENUE, arg0.get(0)
											.getVenue());
									values.put(MatchDb.KEY_OVERS, arg0.get(0)
											.getOvers());
									values.put(MatchDb.KEY_INNINGS, arg0.get(0)
											.getInnings());
									values.put(MatchDb.KEY_RESULT, arg0.get(0)
											.getResult());
									values.put(MatchDb.KEY_LEVEL, arg0.get(0)
											.getLevel());
									values.put(MatchDb.KEY_FIRST_ACTION, arg0
											.get(0).getFirstAction());
									values.put(MatchDb.KEY_DURATION, arg0
											.get(0).getDuration());
									values.put(MatchDb.KEY_REVIEW, arg0.get(0)
											.getReview());
									values.put(MatchDb.KEY_STATUS, arg0.get(0)
											.getStatus());
									// insert a record
									getApplicationContext()
											.getContentResolver()
											.insert(CricDeCodeContentProvider.CONTENT_URI_MATCH,
													values);
									ServerDBPerformance
											.query(ServerDBPerformance.class,
													new StackMobQuery()
															.field(new StackMobQueryField(
																	"match_id")
																	.isEqualTo(arg0
																			.get(0)
																			.getMatchId()))
															.field(new StackMobQueryField(
																	"device_id")
																	.isEqualTo(arg0
																			.get(0)
																			.getDeviceId()))
															.field(new StackMobQueryField(
																	"user_id")
																	.isEqualTo(AccessSharedPrefs.mPrefs
																			.getString(
																					"id",
																					""))),
													new StackMobQueryCallback<ServerDBPerformance>() {

														@Override
														public void failure(
																StackMobException arg0) {
															// TODO
															// Auto-generated
															// method stub

														}

														@Override
														public void success(
																List<ServerDBPerformance> arg0) {
															for (int i = 0; i < arg0
																	.size(); i++) {
																ContentValues value = new ContentValues();

																value.put(
																		PerformanceDb.KEY_MATCHID,
																		arg0.get(
																				i)
																				.getMatchId());
																value.put(
																		PerformanceDb.KEY_DEVICE_ID,
																		arg0.get(
																				i)
																				.getDeviceId());
																value.put(
																		PerformanceDb.KEY_ROWID,
																		arg0.get(
																				i)
																				.getPerId());
																value.put(
																		PerformanceDb.KEY_INNING,
																		arg0.get(
																				i)
																				.getInning());

																value.put(
																		PerformanceDb.KEY_BAT_NUM,
																		arg0.get(
																				i)
																				.getBatNum());
																value.put(
																		PerformanceDb.KEY_BAT_RUNS,
																		arg0.get(
																				i)
																				.getBatRuns());
																value.put(
																		PerformanceDb.KEY_BAT_BALLS,
																		arg0.get(
																				i)
																				.getBatBalls());
																value.put(
																		PerformanceDb.KEY_BAT_TIME,
																		arg0.get(
																				i)
																				.getBatTime());
																value.put(
																		PerformanceDb.KEY_BAT_FOURS,
																		arg0.get(
																				i)
																				.getBatFours());
																value.put(
																		PerformanceDb.KEY_BAT_SIXES,
																		arg0.get(
																				i)
																				.getBatSixes());
																value.put(
																		PerformanceDb.KEY_BAT_HOW_OUT,
																		arg0.get(
																				i)
																				.getBatDismissal());
																value.put(
																		PerformanceDb.KEY_BAT_BOWLER_TYPE,
																		arg0.get(
																				i)
																				.getBatBowlerType());
																value.put(
																		PerformanceDb.KEY_BAT_FIELDING_POSITION,
																		arg0.get(
																				i)
																				.getBatFieldingPosition());
																value.put(
																		PerformanceDb.KEY_BAT_CHANCES,
																		arg0.get(
																				i)
																				.getBatChances());

																value.put(
																		PerformanceDb.KEY_BOWL_BALLS,
																		arg0.get(
																				i)
																				.getBowlBalls());
																value.put(
																		PerformanceDb.KEY_BOWL_SPELLS,
																		arg0.get(
																				i)
																				.getBowlSpells());
																value.put(
																		PerformanceDb.KEY_BOWL_MAIDENS,
																		arg0.get(
																				i)
																				.getBowlMaidens());
																value.put(
																		PerformanceDb.KEY_BOWL_RUNS,
																		arg0.get(
																				i)
																				.getBowlRuns());
																value.put(
																		PerformanceDb.KEY_BOWL_FOURS,
																		arg0.get(
																				i)
																				.getBowlFours());
																value.put(
																		PerformanceDb.KEY_BOWL_SIXES,
																		arg0.get(
																				i)
																				.getBowlSixes());
																value.put(
																		PerformanceDb.KEY_BOWL_WKTS_LEFT,
																		arg0.get(
																				i)
																				.getBowlWktsLeft());
																value.put(
																		PerformanceDb.KEY_BOWL_WKTS_RIGHT,
																		arg0.get(
																				i)
																				.getBowlWktsRight());
																value.put(
																		PerformanceDb.KEY_BOWL_CATCHES_DROPPED,
																		arg0.get(
																				i)
																				.getCatchedDropped());
																value.put(
																		PerformanceDb.KEY_BOWL_NOBALLS,
																		arg0.get(
																				i)
																				.getNoBalls());
																value.put(
																		PerformanceDb.KEY_BOWL_WIDES,
																		arg0.get(
																				i)
																				.getBowlWides());

																value.put(
																		PerformanceDb.KEY_FIELD_SLIP_CATCH,
																		arg0.get(
																				i)
																				.getFieldSlipCatch());
																value.put(
																		PerformanceDb.KEY_FIELD_CLOSE_CATCH,
																		arg0.get(
																				i)
																				.getFieldCloseCatch());
																value.put(
																		PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
																		arg0.get(
																				i)
																				.getFieldCircleCatch());
																value.put(
																		PerformanceDb.KEY_FIELD_DEEP_CATCH,
																		arg0.get(
																				i)
																				.getFieldDeepCatch());
																value.put(
																		PerformanceDb.KEY_FIELD_RO_CIRCLE,
																		arg0.get(
																				i)
																				.getFieldRoCircle());
																value.put(
																		PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE,
																		arg0.get(
																				i)
																				.getFieldRoDirectCircle());
																value.put(
																		PerformanceDb.KEY_FIELD_RO_DEEP,
																		arg0.get(
																				i)
																				.getFieldRoDeep());
																value.put(
																		PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP,
																		arg0.get(
																				i)
																				.getFieldRoDirectDeep());
																value.put(
																		PerformanceDb.KEY_FIELD_STUMPINGS,
																		arg0.get(
																				i)
																				.getFieldStumping());
																value.put(
																		PerformanceDb.KEY_FIELD_BYES,
																		arg0.get(
																				i)
																				.getFieldByes());
																value.put(
																		PerformanceDb.KEY_FIELD_MISFIELDS,
																		arg0.get(
																				i)
																				.getMisFields());
																value.put(
																		PerformanceDb.KEY_FIELD_CATCHES_DROPPED,
																		arg0.get(
																				i)
																				.getCatchedDropped());

																value.put(
																		PerformanceDb.KEY_STATUS,
																		arg0.get(
																				i)
																				.getStatus());

																getApplicationContext()
																		.getContentResolver()
																		.insert(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE,
																				value);
															}

														}
													});

								}
							});
				}
				/*
				 * if (!AccessSharedPrefs.mPrefs.getString("device_id", "")
				 * .equals(gcmData.getInt("dev") + "")) {
				 * 
				 * JSONArray ja1 = gcmData.getJSONArray("per"); for (int i = 0;
				 * i < ja1.length(); i++) { JSONObject jo =
				 * ja1.getJSONObject(i); ContentValues value = new
				 * ContentValues();
				 * 
				 * value.put(PerformanceDb.KEY_MATCHID,
				 * Integer.parseInt(jo.getString("mid")));
				 * value.put(PerformanceDb.KEY_DEVICE_ID,
				 * gcmData.getString("dev")); value.put(PerformanceDb.KEY_ROWID,
				 * Integer.parseInt(jo.getString("pid")));
				 * value.put(PerformanceDb.KEY_INNING,
				 * Integer.parseInt(jo.getString("inn")));
				 * 
				 * value.put(PerformanceDb.KEY_BAT_NUM,
				 * Integer.parseInt(jo.getString("bn")));
				 * value.put(PerformanceDb.KEY_BAT_RUNS,
				 * Integer.parseInt(jo.getString("br")));
				 * value.put(PerformanceDb.KEY_BAT_BALLS,
				 * Integer.parseInt(jo.getString("bb")));
				 * value.put(PerformanceDb.KEY_BAT_TIME,
				 * Integer.parseInt(jo.getString("bt")));
				 * value.put(PerformanceDb.KEY_BAT_FOURS,
				 * Integer.parseInt(jo.getString("bf")));
				 * value.put(PerformanceDb.KEY_BAT_SIXES,
				 * Integer.parseInt(jo.getString("bs")));
				 * value.put(PerformanceDb.KEY_BAT_HOW_OUT,
				 * jo.getString("bho"));
				 * value.put(PerformanceDb.KEY_BAT_BOWLER_TYPE,
				 * jo.getString("bbt"));
				 * value.put(PerformanceDb.KEY_BAT_FIELDING_POSITION,
				 * jo.getString("bfp"));
				 * value.put(PerformanceDb.KEY_BAT_CHANCES,
				 * Integer.parseInt(jo.getString("bc")));
				 * 
				 * value.put(PerformanceDb.KEY_BOWL_BALLS,
				 * Integer.parseInt(jo.getString("ob")));
				 * value.put(PerformanceDb.KEY_BOWL_SPELLS,
				 * Integer.parseInt(jo.getString("osp")));
				 * value.put(PerformanceDb.KEY_BOWL_MAIDENS,
				 * Integer.parseInt(jo.getString("om")));
				 * value.put(PerformanceDb.KEY_BOWL_RUNS,
				 * Integer.parseInt(jo.getString("oru")));
				 * value.put(PerformanceDb.KEY_BOWL_FOURS,
				 * Integer.parseInt(jo.getString("of")));
				 * value.put(PerformanceDb.KEY_BOWL_SIXES,
				 * Integer.parseInt(jo.getString("osx")));
				 * value.put(PerformanceDb.KEY_BOWL_WKTS_LEFT,
				 * Integer.parseInt(jo.getString("owl")));
				 * value.put(PerformanceDb.KEY_BOWL_WKTS_RIGHT,
				 * Integer.parseInt(jo.getString("owr")));
				 * value.put(PerformanceDb.KEY_BOWL_CATCHES_DROPPED,
				 * Integer.parseInt(jo.getString("ocd")));
				 * value.put(PerformanceDb.KEY_BOWL_NOBALLS,
				 * Integer.parseInt(jo.getString("ono")));
				 * value.put(PerformanceDb.KEY_BOWL_WIDES,
				 * Integer.parseInt(jo.getString("ow")));
				 * 
				 * value.put(PerformanceDb.KEY_FIELD_SLIP_CATCH,
				 * Integer.parseInt(jo.getString("fsc")));
				 * value.put(PerformanceDb.KEY_FIELD_CLOSE_CATCH,
				 * Integer.parseInt(jo.getString("fco")));
				 * value.put(PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
				 * Integer.parseInt(jo.getString("fcc")));
				 * value.put(PerformanceDb.KEY_FIELD_DEEP_CATCH,
				 * Integer.parseInt(jo.getString("fdc")));
				 * value.put(PerformanceDb.KEY_FIELD_RO_CIRCLE,
				 * Integer.parseInt(jo.getString("frc")));
				 * value.put(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE,
				 * Integer.parseInt(jo.getString("fci")));
				 * value.put(PerformanceDb.KEY_FIELD_RO_DEEP,
				 * Integer.parseInt(jo.getString("frd")));
				 * value.put(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP,
				 * Integer.parseInt(jo.getString("fdd")));
				 * value.put(PerformanceDb.KEY_FIELD_STUMPINGS,
				 * Integer.parseInt(jo.getString("fs")));
				 * value.put(PerformanceDb.KEY_FIELD_BYES,
				 * Integer.parseInt(jo.getString("fb")));
				 * value.put(PerformanceDb.KEY_FIELD_MISFIELDS,
				 * Integer.parseInt(jo.getString("fmf")));
				 * value.put(PerformanceDb.KEY_FIELD_CATCHES_DROPPED,
				 * jo.getString("fcd"));
				 * 
				 * value.put(PerformanceDb.KEY_STATUS, jo.getString("sts"));
				 * 
				 * getApplicationContext() .getContentResolver()
				 * .insert(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE,
				 * value);
				 * 
				 * } }
				 */
				break;
			case DELETE_MATCH:
				JSONArray ja2 = gcmData.getJSONArray("todelete");
				for (int i = 0; i < ja2.length(); i++) {
					JSONObject jo = ja2.getJSONObject(i);
					String str = jo.getString("mid");
					String d_str = jo.getString("dev");
					Uri uri = Uri
							.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE
									+ "/" + str + "/" + d_str);
					getApplicationContext().getContentResolver().delete(uri,
							null, null);
					uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH
							+ "/" + str + "/" + d_str);
					getApplicationContext().getContentResolver().delete(uri,
							null, null);
				}
				break;
			case REMOVE_ADS:
				AccessSharedPrefs.setString(this, "ad_free", "yes");
				try {
					((MainActivity) MainActivity.main_context)
							.runOnUiThread(new Runnable() {
								public void run() {
									try {
										final AdView adView = (AdView) ((MainActivity) MainActivity.main_context)
												.findViewById(R.id.adView);
										adView.setVisibility(View.INVISIBLE);
									} catch (Exception e) {

									}
								}
							});
				} catch (Exception e) {

				}
				break;
			case SUB_INFI:
				AccessSharedPrefs.setString(this, "infi_use", "yes");
				break;
			case SUB_INFI_SYNC:
				AccessSharedPrefs.setString(this, "infi_sync", "yes");
				break;
			}
		} catch (JSONException e) {
			Log.w("Json exception", "" + e);
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