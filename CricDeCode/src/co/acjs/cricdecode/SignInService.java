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

		AccessSharedPrefs.mPrefs = getApplicationContext().getSharedPreferences(
				"CricDeCode", Context.MODE_PRIVATE);
		Log.d("onCreate",
				"mPrefs Data: " + AccessSharedPrefs.mPrefs.getString("id", "") + " "
						+ AccessSharedPrefs.mPrefs.getString("f_name", "") + " "
						+ AccessSharedPrefs.mPrefs.getString("l_name", "") + " "
						+ AccessSharedPrefs.mPrefs.getString("dob", "") + " "
						+ AccessSharedPrefs.mPrefs.getString("fb_link", ""));
		if (AccessSharedPrefs.mPrefs.getString("id", "").equals("")) {

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
			params.add(new BasicNameValuePair("fblink", AccessSharedPrefs.mPrefs
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
