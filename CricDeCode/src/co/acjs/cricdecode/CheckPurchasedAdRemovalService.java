package co.acjs.cricdecode;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.google.ads.AdView;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

public class CheckPurchasedAdRemovalService extends IntentService {
	public static boolean started = true;
	public static Context who;
	public String orderId, token, sign;

	public CheckPurchasedAdRemovalService() {
		super("CheckPurchasedAdRemovalService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		who = this;
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
		try {
			JSONObject jn = new JSONObject(intent.getExtras().getString("json"));
			orderId = jn.getString("orderId");
			token = jn.getString("Token");
			sign = jn.getString("Sign");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		ServerDBRemoveAds
				.query(ServerDBRemoveAds.class,
						new StackMobQuery()
								.field(new StackMobQueryField("user_id")
										.isEqualTo(AccessSharedPrefs.mPrefs
												.getString("id", "")))
								.field(new StackMobQueryField("order_id")
										.isEqualTo(orderId))
								.field(new StackMobQueryField("token")
										.isEqualTo(token))
								.field(new StackMobQueryField("sign")
										.isEqualTo(sign)),
						new StackMobQueryCallback<ServerDBRemoveAds>() {

							@Override
							public void failure(StackMobException arg0) {
							}

							@Override
							public void success(List<ServerDBRemoveAds> arg0) {
								if (arg0.size() == 0) {
									AccessSharedPrefs.setString(who, "ad_free",
											"no");
									try {
										((MainActivity) MainActivity.main_context)
												.runOnUiThread(new Runnable() {
													public void run() {
														try {
															final AdView adView = (AdView) ((MainActivity) MainActivity.main_context)
																	.findViewById(R.id.adView);
															adView.setVisibility(View.VISIBLE);
														} catch (Exception e) {
														}
													}
												});
									} catch (Exception e) {
									}

								} else {
									AccessSharedPrefs.setString(who, "ad_free",
											"yes");
									try {
										((MainActivity) MainActivity.main_context)
												.runOnUiThread(new Runnable() {
													public void run() {
														try {
															final AdView adView = (AdView) ((MainActivity) MainActivity.main_context)
																	.findViewById(R.id.adView);
															adView.setVisibility(View.GONE);
														} catch (Exception e) {
														}
													}
												});
									} catch (Exception e) {
									}
								}

							}
						});
		/*
		 * final JSONParser jsonParser = new JSONParser(); List<NameValuePair>
		 * params = new ArrayList<NameValuePair>(); params.add(new
		 * BasicNameValuePair("id", AccessSharedPrefs.mPrefs .getString("id",
		 * ""))); params.add(new BasicNameValuePair("json",
		 * intent.getExtras().getString( "json"))); JSONObject jn = null; jn =
		 * jsonParser.makeHttpRequest(
		 * getResources().getString(R.string.check_purchase_remove_ads), "POST",
		 * params, this); try { if (jn.getInt("status") == 1) {
		 * AccessSharedPrefs.setString(this, "ad_free", "no"); ((MainActivity)
		 * MainActivity.main_context) .runOnUiThread(new Runnable() { public
		 * void run() { try { final AdView adView = (AdView) ((MainActivity)
		 * MainActivity.main_context) .findViewById(R.id.adView);
		 * adView.setVisibility(View.INVISIBLE); } catch (Exception e) {
		 * 
		 * } } }); } } catch (Exception e) { }
		 */
	}


}