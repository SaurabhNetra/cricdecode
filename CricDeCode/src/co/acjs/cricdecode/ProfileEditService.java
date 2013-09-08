package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ProfileEditService extends IntentService {
	public ProfileEditService() {
		super("ProfileEditService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w("ProfileEditService", "Started");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("ProfileEditService", "Ended");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		AccessSharedPrefs.mPrefs = getApplicationContext()
				.getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		if (AccessSharedPrefs.mPrefs.getString("ProfileEditServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED)) {
			final JSONParser jsonParser = new JSONParser();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", AccessSharedPrefs.mPrefs
					.getString("id", "")));
			params.add(new BasicNameValuePair("nickname",
					AccessSharedPrefs.mPrefs.getString("nickname", "")));
			params.add(new BasicNameValuePair("role", AccessSharedPrefs.mPrefs
					.getString("role", "")));
			params.add(new BasicNameValuePair("battingStyle",
					AccessSharedPrefs.mPrefs.getString("battingStyle", "")));
			params.add(new BasicNameValuePair("bowlingStyle",
					AccessSharedPrefs.mPrefs.getString("bowlingStyle", "")));
			Log.w("Sending User Data...",
					"ProfileEditService:" + jsonParser.isOnline(this));
			int trial = 1;
			JSONObject jn = null;
			while (jsonParser.isOnline(this)) {
				jn = jsonParser.makeHttpRequest(
						getResources().getString(R.string.edit_profile_sync),
						"POST", params, this);
				Log.w("JSON returned", "ProfileEditService: " + jn);
				Log.w("trial value", "ProfileEditService: " + trial);
				if (jn != null) break;
				try {
					Thread.sleep(10 * trial);
				} catch (InterruptedException e) {
				}
				trial++;
			}
			try {
				if (jn.getInt("status") == 1)
					AccessSharedPrefs.setString(this,
							"ProfileEditServiceCalled",
							CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
			} catch (JSONException e) {
			}
		}
	}
}