package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

	@Override
	protected void onHandleIntent(Intent intent) {

		ProfileData.mPrefs = getApplicationContext().getSharedPreferences(
				"CricDeCode", Context.MODE_PRIVATE);
		Log.d("onCreate",
				"mPrefs Data: " + ProfileData.mPrefs.getString("id", "") + " "
						+ ProfileData.mPrefs.getString("f_name", "") + " "
						+ ProfileData.mPrefs.getString("l_name", "") + " "
						+ ProfileData.mPrefs.getString("dob", "") + " "
						+ ProfileData.mPrefs.getString("fb_link", ""));
		if (ProfileData.mPrefs.getString("id", "").equals("")) {

			final JSONParser jsonParser = new JSONParser();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("gcmid", ProfileData.mPrefs
					.getString("gcm_id", "")));
			params.add(new BasicNameValuePair("id", ProfileData.mPrefs
					.getString("id", "")));
			params.add(new BasicNameValuePair("fname", ProfileData.mPrefs
					.getString("f_name", "")));
			params.add(new BasicNameValuePair("lname", ProfileData.mPrefs
					.getString("l_name", "")));
			params.add(new BasicNameValuePair("dob", ProfileData.mPrefs
					.getString("dob", "")));
			params.add(new BasicNameValuePair("fblink", ProfileData.mPrefs
					.getString("fb_link", "")));
			Log.w("Sending User Data...", "SignInService:");

			int trial = 1;
			while (jsonParser.isOnline(this)) {
				JSONObject jn = jsonParser.makeHttpRequest(getResources()
						.getString(R.string.initdata), "POST", params, this);
				Log.w("JSON returned", "SignInService: " + jn);
				Log.w("trial value", "SignInService: " + trial);
				if (jn != null)
					break;
				try {
					Thread.sleep(10 * trial);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				trial++;
			}

			// TODO jn parsing

		}

	}

}
