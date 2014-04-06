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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PurchasedInfiService extends IntentService {
	public static boolean started = true;
	public Context con;

	public PurchasedInfiService() {
		super("PurchasedInfiService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w("PurchasedInfiService", "Started");
		con = this;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("PurchasedInfiService", "Ended");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		AccessSharedPrefs.mPrefs = getApplicationContext()
				.getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		if (AccessSharedPrefs.mPrefs.getString("PurchaseInfiServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED)) {

			try {
				final JSONParser jsonParser = new JSONParser();
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("id",
						AccessSharedPrefs.mPrefs.getString("id", "")));
				params.add(new BasicNameValuePair("product_id", "sub_infi"));
				params.add(new BasicNameValuePair("json",
						AccessSharedPrefs.mPrefs.getString("pur_infi_data", "")));
				int trial = 1;
				JSONObject jn = null;
				while (jsonParser.isOnline(con)) {
					jn = jsonParser.makeHttpRequest(
							getResources().getString(
									R.string.azure_subscription), "POST",
							params, con);
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

					try {
						if (jn.getInt("status") == 1) {
							AccessSharedPrefs.setString(con,
									"PurchaseInfiServiceCalled",
									CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
							AccessSharedPrefs.setString(con, "pur_infi_data",
									"");
							AccessSharedPrefs.setString(con, "infi_use", "yes");
							try {
								((MainActivity) MainActivity.main_context)
										.runOnUiThread(new Runnable() {
											public void run() {
												try {
													((TextView) ((MainActivity) MainActivity.main_context)
															.findViewById(R.id.infi_pur))
															.setVisibility(View.VISIBLE);
													((TextView) ((MainActivity) MainActivity.main_context)
															.findViewById(R.id.infi_pur))
															.setText("Purchased");
													((LinearLayout) ((MainActivity) MainActivity.main_context)
															.findViewById(R.id.pur_subscribe_sync))
															.setVisibility(View.VISIBLE);
													((LinearLayout) ((MainActivity) MainActivity.main_context)
															.findViewById(R.id.pur_subscribe_infi_sync))
															.setVisibility(View.GONE);
												} catch (Exception e) {
												}
											}
										});
							} catch (Exception e) {
							}
						} else if (jn.getInt("status") == 0) {
							AccessSharedPrefs.setString(con,
									"PurchaseInfiServiceCalled",
									CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
							AccessSharedPrefs.setString(con, "pur_infi_data",
									"");
							AccessSharedPrefs.setString(con, "infi_use", "no");
							try {
								((MainActivity) MainActivity.main_context)
										.runOnUiThread(new Runnable() {
											public void run() {
												try {
													((TextView) ((MainActivity) MainActivity.main_context)
															.findViewById(R.id.infi_pur))
															.setVisibility(View.GONE);
												} catch (Exception e) {
												}
											}
										});
							} catch (Exception e) {
							}
						} else if (jn.getInt("status") == 4) {
							if (jn.getString("regids").length() > 20) {
								AccessSharedPrefs.setString(con,
										"PurchaseInfiServiceCalled",
										CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
								AccessSharedPrefs.setString(con,
										"pur_infi_data", "");
								if (jn.getInt("retry_gcm_id") == 5) {
									AccessSharedPrefs.setString(con,
											"infi_use", "yes");
									try {
										((MainActivity) MainActivity.main_context)
												.runOnUiThread(new Runnable() {
													public void run() {
														try {
															((TextView) ((MainActivity) MainActivity.main_context)
																	.findViewById(R.id.infi_pur))
																	.setVisibility(View.VISIBLE);
															((TextView) ((MainActivity) MainActivity.main_context)
																	.findViewById(R.id.infi_pur))
																	.setText("Purchased");
															((LinearLayout) ((MainActivity) MainActivity.main_context)
																	.findViewById(R.id.pur_subscribe_sync))
																	.setVisibility(View.VISIBLE);
															((LinearLayout) ((MainActivity) MainActivity.main_context)
																	.findViewById(R.id.pur_subscribe_infi_sync))
																	.setVisibility(View.GONE);
														} catch (Exception e) {
														}
													}
												});
									} catch (Exception e) {
									}
								} else if (jn.getInt("retry_gcm_id") == 8) {
									AccessSharedPrefs.setString(con,
											"infi_use", "no");
									try {
										((MainActivity) MainActivity.main_context)
												.runOnUiThread(new Runnable() {
													public void run() {
														try {
															((TextView) ((MainActivity) MainActivity.main_context)
																	.findViewById(R.id.infi_pur))
																	.setVisibility(View.GONE);
														} catch (Exception e) {
														}
													}
												});
									} catch (Exception e) {
									}
								}
								
								params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("uid",
										AccessSharedPrefs.mPrefs.getString("id", "")));
								params.add(new BasicNameValuePair("SendToArrays", jn
										.getString("regids")));
								JSONObject jo = new JSONObject();
								jo.put("gcmid", jn.getInt("retry_gcm_id"));
								params.add(new BasicNameValuePair("MsgToSend", jo
										.toString()));

								jn = null;
								
								trial = 1;
								if (jn == null) {
									while (jsonParser.isOnline(con)) {
										Log.w("JSONParser",
												"ProfileEditService: Called");
										jn = jsonParser.makeHttpRequest(getResources()
												.getString(R.string.gae_send_gcm),
												"POST", params, con);
										Log.w("JSON returned", "ProfileEditService: "
												+ jn);
										Log.w("trial value", "ProfileEditService: "
												+ trial);
										if (jn != null)
											break;
										try {
											Thread.sleep(10 * trial);
										} catch (InterruptedException e) {
										}
										trial++;

										if (trial == 50)
											break;
									}

								}
								
								
								trial = 1;
								while (jsonParser.isOnline(con)) {
									Log.w("JSONParser", "ProfileEditService: Called");
									jn = jsonParser.makeHttpRequest(getResources()
											.getString(R.string.ping_hansa_gcm),
											"POST", params, con);
									if (jn != null)
										break;
									try {
										Thread.sleep(10 * trial);
									} catch (InterruptedException e) {
									}
									trial++;

									if (trial == 50)
										break;
								}

								trial = 1;
								if (jn == null) {
									while (jsonParser.isOnline(con)) {
										Log.w("JSONParser",
												"ProfileEditService: Called");
										jn = jsonParser.makeHttpRequest(getResources()
												.getString(R.string.ping_acjs_gcm),
												"POST", params, con);
										if (jn != null)
											break;
										try {
											Thread.sleep(10 * trial);
										} catch (InterruptedException e) {
										}
										trial++;

										if (trial == 50)
											break;
									}

								}
							}
						}
					} catch (JSONException e) {
						Log.w("PurchaseInfiService", "Caught JSON Exeption");
					}
				} catch (Exception e) {
				}
			} catch (Exception e) {
			}
		}

	}
	
	
}
