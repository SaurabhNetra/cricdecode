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
import android.util.Log;

public class SignInService extends IntentService {

	public SignInService() {
		super("SignInService");

	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w("SignInService", "Started");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("SignInService", "Ended");
	}

	@SuppressWarnings("unused")
	@Override
	protected void onHandleIntent(Intent intent) {
		AccessSharedPrefs.mPrefs = getApplicationContext()
				.getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		Log.d("onCreate",
				"mPrefs Data: " + AccessSharedPrefs.mPrefs.getString("id", "") + " " + AccessSharedPrefs.mPrefs
						.getString("f_name", "") + " " + AccessSharedPrefs.mPrefs
						.getString("l_name", "") + " " + AccessSharedPrefs.mPrefs
						.getString("dob", "") + " " + AccessSharedPrefs.mPrefs
						.getString("fb_link", ""));
		if (AccessSharedPrefs.mPrefs.getString("SignInServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED)) {
			final JSONParser jsonParser = new JSONParser();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("gcmid", AccessSharedPrefs.mPrefs
					.getString("gcm_id", "")));
			params.add(new BasicNameValuePair("id", AccessSharedPrefs.mPrefs
					.getString("id", "")));
			params.add(new BasicNameValuePair("fname", AccessSharedPrefs.mPrefs
					.getString("f_name", "")));
			params.add(new BasicNameValuePair("lname", AccessSharedPrefs.mPrefs
					.getString("l_name", "")));
			params.add(new BasicNameValuePair("dob", AccessSharedPrefs.mPrefs
					.getString("dob", "")));
			params.add(new BasicNameValuePair("fblink",
					AccessSharedPrefs.mPrefs.getString("fb_link", "")));
			params.add(new BasicNameValuePair("android", "1"));
			Log.w("Sending User Data...",
					"SignInService:" + jsonParser.isOnline(this));
			int trial = 1;
			JSONObject jn = null;
			while (jsonParser.isOnline(this)) {
				jn = jsonParser.makeHttpRequest(
						getResources().getString(R.string.initdata), "POST",
						params, this);
				Log.w("JSON returned", "SignInService: " + jn);
				Log.w("trial value", "SignInService: " + trial);
				if (jn != null) break;
				try {
					Thread.sleep(10 * trial);
				} catch (InterruptedException e) {
				}
				trial++;
			}
			try {
				if (jn.getString("user").equals("existing")) {
					AccessSharedPrefs.setString(this, "nickname",
							jn.getString("nickname"));
					AccessSharedPrefs.setString(this, "role",
							jn.getString("role"));
					AccessSharedPrefs.setString(this, "battingStyle",
							jn.getString("battingStyle"));
					AccessSharedPrefs.setString(this, "bowlingStyle",
							jn.getString("bowlingStyle"));
					try {
						JSONArray all_matches = jn
								.getJSONArray("cricket_match_data");
						for (int i = 0; i < all_matches.length(); i++) {
							JSONObject single_match = all_matches
									.getJSONObject(i);
							int match_id = Integer.parseInt(single_match
									.getString("match_id"));
							String match_date = single_match
									.getString("match_date");
							String my_team = single_match.getString("my_team");
							String opponent_team = single_match
									.getString("opponent_team");
							String venue = single_match.getString("venue");
							int overs = Integer.parseInt(single_match
									.getString("overs"));
							int innings = Integer.parseInt(single_match
									.getString("innings"));
							String result = single_match.getString("result");
							String level = single_match.getString("level");
							String first_action = single_match
									.getString("first_action");
							String duration = single_match
									.getString("duration");
							String review = single_match.getString("review");
							String status = single_match.getString("status")
									.equals("1") ? MatchDb.MATCH_CURRENT : MatchDb.MATCH_HISTORY;
							Log.w("JSON Object", "Match Data: " + match_id);
							// TODO Saurabh Code Here
							// Saurabh Stop Code Here :P
						}
					} catch (JSONException e) {
					}
					try {
						JSONArray all_performance = jn
								.getJSONArray("performance_data");
						for (int i = 0; i < all_performance.length(); i++) {
							JSONObject single_performance = all_performance
									.getJSONObject(i);
							int match_id = Integer.parseInt(single_performance
									.getString("match_id"));
							int performance_id = Integer
									.parseInt(single_performance
											.getString("performance_id"));
							int inning = Integer.parseInt(single_performance
									.getString("inning"));
							int bat_num = Integer.parseInt(single_performance
									.getString("bat_num"));
							int bat_runs = Integer.parseInt(single_performance
									.getString("bat_runs"));
							int bat_balls = Integer.parseInt(single_performance
									.getString("bat_balls"));
							int bat_time = Integer.parseInt(single_performance
									.getString("bat_time"));
							int fours = Integer.parseInt(single_performance
									.getString("fours"));
							int sixes = Integer.parseInt(single_performance
									.getString("sixes"));
							String bat_dismissal = single_performance
									.getString("bat_dismissal");
							String bat_bowler_type = single_performance
									.getString("bat_bowler_type");
							String bat_fielding_position = single_performance
									.getString("bat_fielding_position");
							int bat_chances = Integer
									.parseInt(single_performance
											.getString("bat_chances"));
							int bowl_balls = Integer
									.parseInt(single_performance
											.getString("bowl_balls"));
							int bowl_spells = Integer
									.parseInt(single_performance
											.getString("bowl_spells"));
							int bowl_maidens = Integer
									.parseInt(single_performance
											.getString("bowl_maidens"));
							int bowl_fours = Integer
									.parseInt(single_performance
											.getString("bowl_fours"));
							int bowl_sixes = Integer
									.parseInt(single_performance
											.getString("bowl_sixes"));
							int bowl_wkts_left = Integer
									.parseInt(single_performance
											.getString("bowl_wkts_left"));
							int bowl_wkts_right = Integer
									.parseInt(single_performance
											.getString("bowl_wkts_right"));
							int bowl_catches_dropped = Integer
									.parseInt(single_performance
											.getString("bowl_catches_dropped"));
							int bowl_no_balls = Integer
									.parseInt(single_performance
											.getString("bowl_no_balls"));
							int bowl_wides = Integer
									.parseInt(single_performance
											.getString("bowl_wides"));
							int field_slip_catch = Integer
									.parseInt(single_performance
											.getString("field_slip_catch"));
							int field_close_catch = Integer
									.parseInt(single_performance
											.getString("field_close_catch"));
							int field_circle_catch = Integer
									.parseInt(single_performance
											.getString("field_circle_catch"));
							int field_deep_catch = Integer
									.parseInt(single_performance
											.getString("field_deep_catch"));
							int field_ro_circle = Integer
									.parseInt(single_performance
											.getString("field_ro_circle"));
							int field_ro_direct_circle = Integer
									.parseInt(single_performance
											.getString("field_ro_direct_circle"));
							int field_ro_deep = Integer
									.parseInt(single_performance
											.getString("field_ro_deep"));
							int field_ro_direct_deep = Integer
									.parseInt(single_performance
											.getString("field_ro_direct_deep"));
							int field_stumpings = Integer
									.parseInt(single_performance
											.getString("field_stumpings"));
							int field_byes = Integer
									.parseInt(single_performance
											.getString("field_byes"));
							int field_misfields = Integer
									.parseInt(single_performance
											.getString("field_misfields"));
							int field_catches_dropped = Integer
									.parseInt(single_performance
											.getString("field_catches_dropped"));
							String status = single_performance.getString(
									"status").equals("1") ? MatchDb.MATCH_CURRENT : MatchDb.MATCH_HISTORY;
							Log.w("JSON Object",
									"Performance Data: " + performance_id);
							// TODO Saurabh Code Here
							// Saurabh Stop Code Here :P
						}
					} catch (JSONException e) {
					}
				}
				AccessSharedPrefs.setString(this, "SignInServiceCalled",
						CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
			} catch (JSONException e) {
			}
		}
	}
}