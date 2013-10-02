package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class DeleteMatchService extends IntentService {
	public static boolean started = true;

	public DeleteMatchService() {
		super("DeleteMatchService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w("DeleteMatchService", "Started");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("DeleteMatchService", "Ended");
	}

	@SuppressWarnings({ "unused" })
	@Override
	protected void onHandleIntent(Intent intent) {

		if (AccessSharedPrefs.mPrefs.getString("DeleteMatchServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED)) {

			// TODO Make array for status=deleted
			// create json array of dev, id , mid
			JSONArray jsonArray = new JSONArray();
			Cursor c = MainActivity.dbHandle.rawQuery("select "
					+ MatchDb.KEY_DEVICE_ID + "," + MatchDb.KEY_ROWID
					+ " from " + MatchDb.SQLITE_TABLE + " where "
					+ MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_DELETED + "'",
					null);
			if (c.getCount() != 0) {
				int i = 0;
				c.moveToFirst();
				do {
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put(MatchDb.KEY_DEVICE_ID, c.getString(0));
						jsonObject.put(MatchDb.KEY_ROWID, c.getString(1));
						jsonArray.put(jsonObject);
					} catch (JSONException jsonException) {

					}
					c.moveToNext();
				} while (!c.isAfterLast());
			}
			c.close();

			String mid = intent.getExtras().getString("mid");
			String dev = intent.getExtras().getString("dev");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", AccessSharedPrefs.mPrefs
					.getString("id", "")));
			// params.add(new BasicNameValuePair("json", json));

			int trial = 1;
			JSONObject jn = null;
			final JSONParser jsonParser = new JSONParser();
			while (jsonParser.isOnline(this)) {
				jn = jsonParser.makeHttpRequest(
						getResources().getString(R.string.match_delete),
						"POST", params, this);
				Log.w("JSON returned", "MatchDeleteService: " + jn);
				Log.w("trial value", "MatchDeleteService: " + trial);
				if (jn != null)
					break;
				try {
					Thread.sleep(10 * trial);
				} catch (InterruptedException e) {
				}
				trial++;
			}
		}
	}
}