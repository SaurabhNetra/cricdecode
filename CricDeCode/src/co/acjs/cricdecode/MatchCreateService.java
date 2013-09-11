package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class MatchCreateService extends IntentService {
	public MatchCreateService() {
		super("MatchCreateService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w("MatchCreateService", "Started");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("MatchCreateService", "Ended");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (AccessSharedPrefs.mPrefs.getString("MatchCreateServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED)) {
			final JSONParser jsonParser = new JSONParser();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", AccessSharedPrefs.mPrefs
					.getString("id", "")));
			Cursor c = getContentResolver()
					.query(CricDeCodeContentProvider.CONTENT_URI_MATCH,
							new String[] { MatchDb.KEY_ROWID, MatchDb.KEY_DEVICE_ID, MatchDb.KEY_MATCH_DATE, MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM, MatchDb.KEY_VENUE, MatchDb.KEY_OVERS, MatchDb.KEY_INNINGS, MatchDb.KEY_RESULT, MatchDb.KEY_LEVEL, MatchDb.KEY_FIRST_ACTION, MatchDb.KEY_DURATION, MatchDb.KEY_REVIEW, MatchDb.KEY_STATUS, MatchDb.KEY_SYNCED },
							MatchDb.KEY_SYNCED + "=" + "0", null, null);
			if (c.getCount() != 0) {
				c.moveToFirst();
				// Do for every Match Row in the Cursor
				do {
					// eg. int
					// rowid=c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_ROWID));
					// Form your Json
					c.moveToNext();
				} while (!c.isAfterLast());
			}
			c.close();
			int trial = 1;
			JSONObject jn = null;
			while (jsonParser.isOnline(this)) {
				jn = jsonParser.makeHttpRequest(
						getResources().getString(R.string.match_create_sync),
						"POST", params, this);
				Log.w("JSON returned", "MatchCreateService: " + jn);
				Log.w("trial value", "MatchCreateService: " + trial);
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
							"MatchCreateServiceCalled",
							CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
			} catch (JSONException e) {
			}
		}
	}
}