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
import android.widget.TextView;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class PurchasedAdRemovalService extends IntentService {
	public static boolean started = true;
	public Context con;

	public PurchasedAdRemovalService() {
		super("PurchasedAdRemovalService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w("PurchasedAdRemovalService", "Started");
		con = this;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("PurchasedAdRemovalService", "Ended");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		AccessSharedPrefs.mPrefs = getApplicationContext()
				.getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		if (AccessSharedPrefs.mPrefs.getString(
				"PurchaseAdRemovalServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED)) {

			try {
				final JSONParser jsonParser = new JSONParser();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id",
						AccessSharedPrefs.mPrefs.getString("id", "")));
				params.add(new BasicNameValuePair("product_id", "ad_removal"));
				params.add(new BasicNameValuePair("json",
						AccessSharedPrefs.mPrefs.getString("pur_ad_data", "")));
				int trial = 1;
				JSONObject jn = null;
				while (jsonParser.isOnline(con)) {
					
					jn = jsonParser.makeHttpRequest(
							getResources().getString(
									R.string.azure_inapppurchase), "POST",
							params, con);
					Log.w("JSON returned", "PurchasedAdRemovalService: " + jn);
					Log.w("trial value", "PurchasedAdRemovalService: " + trial);
					if (jn != null)
						break;
					try {
						Thread.sleep(10 * trial);
					} catch (InterruptedException e) {
					}
					trial++;
					if (trial == 50) {
						break;
					}
				}
				try {
					Log.w("PurchaseAdRemovalServiceCalled", "Reply" + jn);
					if (jn.getInt("status") == 1) {
						Log.w("PurAdRemoval", "Reply" + jn);
						AccessSharedPrefs.setString(con,
								"PurchaseAdRemovalServiceCalled",
								CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
						AccessSharedPrefs.setString(con, "pur_ad_data", "");
						AccessSharedPrefs.setString(con, "ad_free", "yes");
						try {
							((MainActivity) MainActivity.main_context)
									.runOnUiThread(new Runnable() {
										public void run() {
											try {
												final AdView adView = (AdView) ((MainActivity) MainActivity.main_context)
														.findViewById(R.id.adView);
												adView.setAdUnitId(R.string.publisher_id
														+ "");
												adView.setAdSize(AdSize.BANNER);
												adView.setVisibility(View.GONE);
												((TextView) ((MainActivity) MainActivity.main_context)
														.findViewById(R.id.rem_ads_pur))
														.setVisibility(View.VISIBLE);
											} catch (Exception e) {
											}
										}
									});
						} catch (Exception e) {
						}
					} else if (jn.getInt("status") == 0) {
						AccessSharedPrefs.setString(con,
								"PurchaseAdRemovalServiceCalled",
								CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
						AccessSharedPrefs.setString(con, "pur_ad_data", "");
						AccessSharedPrefs.setString(con, "ad_free", "no");
						try {
							((MainActivity) MainActivity.main_context)
									.runOnUiThread(new Runnable() {
										public void run() {
											try {
												final AdView adView = (AdView) ((MainActivity) MainActivity.main_context)
														.findViewById(R.id.adView);
												adView.setAdUnitId(R.string.publisher_id
														+ "");
												adView.setAdSize(AdSize.BANNER);
												adView.setVisibility(View.VISIBLE);
												((TextView) ((MainActivity) MainActivity.main_context)
														.findViewById(R.id.rem_ads_pur))
														.setVisibility(View.GONE);
											} catch (Exception e) {
											}
										}
									});
						} catch (Exception e) {
						}
					}
				} catch (Exception e) {
				}
			} catch (Exception e) {
			}

		}
	}
}
