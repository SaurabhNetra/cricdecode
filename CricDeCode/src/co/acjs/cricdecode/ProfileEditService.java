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

import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobCallback;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

public class ProfileEditService extends IntentService {
	public static boolean started = true;
	public static Context who;

	public ProfileEditService() {
		super("ProfileEditService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		who = this;
		StackMobAndroid.init(who, 0, "c52a9f47-baae-41e3-aa63-72177b0c23f7");
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
			ServerDBUserTable.query(ServerDBUserTable.class,
					new StackMobQuery().field(new StackMobQueryField("user_id")
							.isEqualTo(AccessSharedPrefs.mPrefs.getString("id",
									""))),
					new StackMobQueryCallback<ServerDBUserTable>() {

						@Override
						public void failure(StackMobException arg0) {
							Log.w("ProfileEditService", "user fetch failed");

						}

						@Override
						public void success(List<ServerDBUserTable> arg0) {
							if (arg0.size() != 0) {
								ServerDBUserTable d = new ServerDBUserTable();
								d.setID(arg0.get(0).getID());
								d.setDeviceNo(arg0.get(0).getDeviceNo());
								d.setDob(arg0.get(0).getDob());
								d.setFbLink(arg0.get(0).getFbLink());
								d.setFirstName(arg0.get(0).getFirstName());
								d.setLastName(arg0.get(0).getLastName());
								d.setHasAndroid(arg0.get(0).getHasAndroid());
								d.setId(arg0.get(0).getId());
								d.setBattingStyle(AccessSharedPrefs.mPrefs
										.getString("battingStyle", ""));
								d.setBowlingStyle(AccessSharedPrefs.mPrefs
										.getString("bowlingStyle", ""));
								d.setRole(AccessSharedPrefs.mPrefs.getString(
										"role", ""));
								d.setNickName(AccessSharedPrefs.mPrefs
										.getString("nickname", ""));
								d.save(new StackMobCallback() {

									@Override
									public void failure(StackMobException arg0) {
										Log.w("ProfileEditService",
												"Update failed");
									}

									@Override
									public void success(String arg0) {
										Log.w("ProfileEditService",
												"Update success");
										ServerDBAndroidDevices
												.query(ServerDBAndroidDevices.class,
														new StackMobQuery()
																.field(new StackMobQueryField(
																		"user_id")
																		.isEqualTo(AccessSharedPrefs.mPrefs
																				.getString(
																						"id",
																						""))),
														new StackMobQueryCallback<ServerDBAndroidDevices>() {

															@Override
															public void failure(
																	StackMobException arg0) {
															}

															@Override
															public void success(
																	List<ServerDBAndroidDevices> arg0) {
																Log.w("ProfileEditService",
																		"GCM Ids fetched"
																				+ arg0.size());
																String regids = "";
																for (int i = 0; i < arg0
																		.size(); i++) {
																	regids = regids
																			+ " "
																			+ arg0.get(
																					i)
																					.getGcmId();
																}
																JSONObject msg = new JSONObject();
																Log.w("ProfileEditService: ",""+regids);
																try {
																	msg.put("gcmid",
																			1);
																	msg.put("nickname",
																			AccessSharedPrefs.mPrefs
																					.getString(
																							"nickname",
																							""));
																	msg.put("role",
																			AccessSharedPrefs.mPrefs
																					.getString(
																							"role",
																							""));
																	msg.put("bowlingStyle",
																			AccessSharedPrefs.mPrefs
																					.getString(
																							"bowlingStyle",
																							""));
																	msg.put("battingStyle",
																			AccessSharedPrefs.mPrefs
																					.getString(
																							"battingStyle",
																							""));
																	Log.w("ProfileEditService: ",""+msg.toString());
																	final JSONParser jsonParser = new JSONParser();
																	List<NameValuePair> params = new ArrayList<NameValuePair>();
																	params.add(new BasicNameValuePair(
																			"SendToArrays",
																			regids));
																	params.add(new BasicNameValuePair(
																			"MsgToSend",
																			msg.toString()));
																	params.add(new BasicNameValuePair("uid", AccessSharedPrefs.mPrefs.getString("id", "")));
																	Log.w("Sending User Data...",
																			"ProfileEditService:"
																					+ jsonParser
																							.isOnline(who));
																	int trial = 1;
																	JSONObject jn = null;
																	while (jsonParser
																			.isOnline(who)) {
																		Log.w("JSONParser",
																				"ProfileEditService: Called"
																						);
																		jn = jsonParser
																				.makeHttpRequest(
																						getResources()
																								.getString(
																										R.string.edit_profile_sync),
																						"POST",
																						params,
																						who);
																		Log.w("JSON returned",
																				"ProfileEditService: "
																						+ jn);
																		Log.w("trial value",
																				"ProfileEditService: "
																						+ trial);
																		if (jn != null)
																			break;
																		try {
																			Thread.sleep(10 * trial);
																		} catch (InterruptedException e) {
																		}
																		trial++;
																	}
																	try {
																		if (jn.getInt("status") == 1)
																			AccessSharedPrefs
																					.setString(
																							who,
																							"ProfileEditServiceCalled",
																							CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
																	} catch (NullPointerException e) {
																	} catch (JSONException e) {
																		e.printStackTrace();
																	}

																} catch (JSONException e) {
																	// TODO
																	// Auto-generated
																	// catch
																	// block
																	e.printStackTrace();
																}

															}
														});

									}
								});
							}

						}
					});

			/*
			 * try { Log.w("ProfileEditService", "wait called"); //who.wait(); }
			 * catch (InterruptedException e) { e.printStackTrace(); }
			 */
			Log.w("ProfileEditService", "wait resumed");

		}
	}
}