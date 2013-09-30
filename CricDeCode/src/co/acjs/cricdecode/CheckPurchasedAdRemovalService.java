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
import android.view.View;

import com.google.ads.AdView;

public class CheckPurchasedAdRemovalService extends IntentService {
	public static boolean started = true;

	public CheckPurchasedAdRemovalService() {
		super("CheckPurchasedAdRemovalService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w("CheckPurchasedAdRemovalService", "Started");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("CheckPurchasedAdRemovalService", "Ended");
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
				getResources().getString(R.string.check_purchase_remove_ads),
				"POST", params, this);
		try {
			if (jn.getInt("status") == 1) {
				AccessSharedPrefs.setString(this, "ad_free", "no");
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
			}
		} catch (Exception e) {
		}
	}
}