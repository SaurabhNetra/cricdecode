package co.acjs.cricdecode;

import java.util.List;

import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobCallback;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
		StackMobAndroid.init(who, 0,
				"c52a9f47-baae-41e3-aa63-72177b0c23f7");
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
							who.notify();
						}

						@Override
						public void success(List<ServerDBUserTable> arg0) {
							if (arg0.size() != 0) {
								ServerDBUserTable d = new ServerDBUserTable();
								d.setID(arg0.get(0).getID());
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
										who.notify();
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
																who.notify();
															}

															@Override
															public void success(
																	List<ServerDBAndroidDevices> arg0) {
																Log.w("ProfileEditService",
																		"GCM Ids fetched"
																				+ arg0.size());
																AccessSharedPrefs
																		.setString(
																				who,
																				"ProfileEditServiceCalled",
																				CDCAppClass.DOESNT_NEED_TO_BE_CALLED);

																//who.notify();
															}
														});

									}
								});
							}

						}
					});

		/*	try {
				Log.w("ProfileEditService", "wait called");
				//who.wait();
				
			} catch (InterruptedException e) {
			e.printStackTrace();
			} */
			Log.w("ProfileEditService", "wait resumed");
			/*
			 * final JSONParser jsonParser = new JSONParser();
			 * List<NameValuePair> params = new ArrayList<NameValuePair>();
			 * params.add(new BasicNameValuePair("id", AccessSharedPrefs.mPrefs
			 * .getString("id", ""))); params.add(new
			 * BasicNameValuePair("nickname",
			 * AccessSharedPrefs.mPrefs.getString("nickname", "")));
			 * params.add(new BasicNameValuePair("role",
			 * AccessSharedPrefs.mPrefs .getString("role", ""))); params.add(new
			 * BasicNameValuePair("battingStyle",
			 * AccessSharedPrefs.mPrefs.getString("battingStyle", "")));
			 * params.add(new BasicNameValuePair("bowlingStyle",
			 * AccessSharedPrefs.mPrefs.getString("bowlingStyle", "")));
			 * Log.w("Sending User Data...", "ProfileEditService:" +
			 * jsonParser.isOnline(this)); int trial = 1; JSONObject jn = null;
			 * while (jsonParser.isOnline(this)) { jn =
			 * jsonParser.makeHttpRequest(
			 * getResources().getString(R.string.edit_profile_sync), "POST",
			 * params, this); Log.w("JSON returned", "ProfileEditService: " +
			 * jn); Log.w("trial value", "ProfileEditService: " + trial); if (jn
			 * != null) break; try { Thread.sleep(10 * trial); } catch
			 * (InterruptedException e) { } trial++; } try { if
			 * (jn.getInt("status") == 1) AccessSharedPrefs.setString(this,
			 * "ProfileEditServiceCalled",
			 * CDCAppClass.DOESNT_NEED_TO_BE_CALLED); } catch
			 * (NullPointerException e) { } catch (JSONException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */
		}
	}
}