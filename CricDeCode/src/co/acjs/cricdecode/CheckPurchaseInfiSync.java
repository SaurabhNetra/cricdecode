package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class CheckPurchaseInfiSync extends IntentService {
	public static boolean started = true;

	public CheckPurchaseInfiSync() {
		super("CheckPurchasedAdRemovalService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		AccessSharedPrefs.mPrefs = getApplicationContext()
				.getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		final JSONParser jsonParser = new JSONParser();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", AccessSharedPrefs.mPrefs
				.getString("id", "")));
		params.add(new BasicNameValuePair("json", intent.getExtras().getString(
				"json")));
		JSONObject jn = null;
		jn = jsonParser.makeHttpRequest(
				getResources().getString(R.string.check_purchase_infi_sync),
				"POST", params, this);
		try {
			if (jn.getInt("status") == 1) {
				AccessSharedPrefs.setString(this, "infi_sync", "no");				
			}
		} catch (Exception e) {
		}
	}
}