package co.acjs.cricdecode;

import java.util.Arrays;
import java.util.List;

import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.analytics.tracking.android.EasyTracker;
import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobCallback;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

public class LogIn extends SherlockActivity {
	static GraphUser		user;
	static Context			login_activity;
	ContentProviderClient	client;
	SQLiteDatabase			dbHandle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.action_bar_login, null);
		actionBar.setCustomView(view);
		AccessSharedPrefs.mPrefs = getSharedPreferences("CricDeCode",
				Context.MODE_PRIVATE);
		login_activity = this;
		final LoginButton loginButton = (LoginButton) findViewById(R.id.login);
		List<String> permissions = Arrays.asList("user_birthday");
		loginButton.setReadPermissions(permissions);
		loginButton.setApplicationId(getResources().getString(
				R.string.fb_app_id));
		loginButton.setSessionStatusCallback(new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					loginButton.setVisibility(View.GONE);
					((ProgressBar) findViewById(R.id.progress_bar))
							.setVisibility(View.VISIBLE);
					// make request to the /me API
					Request.newMeRequest(session,
							new Request.GraphUserCallback() {
								// callback after Graph API response with user
								// object
								@Override
								public void onCompleted(GraphUser user, Response response) {
									if (user != null) {
										Log.w("Face Book Login Complete",
												"LogIn: " + user.getBirthday());
										LogIn.user = user;
										GCMRegistration();
									}
								}
							}).executeAsync();
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Google Analytics Stop
		EasyTracker.getInstance().activityStart(this);
		if (!AccessSharedPrefs.mPrefs.getString("isSignedIn", "").equals("")) {
			ConnectivityManager connectivityManager = (ConnectivityManager) this
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
				login_activity.startService(new Intent(login_activity,
						FBRefreshService.class));
			}
			openMainActivity();
		} else if (!AccessSharedPrefs.mPrefs.getString("id", "").equals("")) {
			Session session = Session.getActiveSession();
			final LoginButton loginButton = (LoginButton) findViewById(R.id.login);
			if (session.isOpened()) {
				loginButton.setVisibility(View.GONE);
				((ProgressBar) findViewById(R.id.progress_bar))
						.setVisibility(View.VISIBLE);
				// make request to the /me API
				Request.newMeRequest(session, new Request.GraphUserCallback() {

					// callback after Graph API response with user
					// object
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							Log.w("Face Book Login Complete",
									"LogIn: " + user.getBirthday());
							LogIn.user = user;
							GCMRegistration();
						}
					}
				}).executeAsync();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Google Analytics Stop
		EasyTracker.getInstance().activityStop(this);
	}

	void GCMRegistration() {
		// TODO encrypt
		StackMobAndroid.init(login_activity, 0,
				"c52a9f47-baae-41e3-aa63-72177b0c23f7");
		client = getContentResolver().acquireContentProviderClient(
				CricDeCodeContentProvider.AUTHORITY);
		dbHandle = ((CricDeCodeContentProvider) client
				.getLocalContentProvider()).getDbHelper().getReadableDatabase();
		AccessSharedPrefs.mPrefs = login_activity.getSharedPreferences(
				"CricDeCode", Context.MODE_PRIVATE);
		GCMRegistrarCompat.checkDevice(this);
		if (BuildConfig.DEBUG) {
			GCMRegistrarCompat.checkManifest(this);
		}
		final String regId = GCMRegistrarCompat.getRegistrationId(this);
		if (regId.length() == 0) {
			new RegisterTask(this).execute(getResources().getString(
					R.string.projno));
		} else {
			Log.d(getClass().getSimpleName(), "Existing registration: " + regId);
		}
	}

	private static class RegisterTask extends GCMRegistrarCompat.BaseRegisterTask {
		RegisterTask(Context context) {
			super(context);
		}

		@Override
		public void onPostExecute(String regid) {
			Log.d(getClass().getSimpleName(), "registered as: " + regid);
		}
	}

	static void startApp(final String gcm_reg_id) {
		if ((gcm_reg_id == "") | (gcm_reg_id == null)){
			((LogIn) login_activity).GCMRegistration();
		Log.w("Start App", "GCM ID null recalling");}
		else {
			Log.w("Start App", "else ");
			AccessSharedPrefs.setString(login_activity, "id", user.getId());
			AccessSharedPrefs.setString(login_activity, "f_name",
					user.getFirstName());
			AccessSharedPrefs.setString(login_activity, "l_name",
					user.getLastName());
			AccessSharedPrefs.setString(login_activity, "dob",
					user.getBirthday());
			AccessSharedPrefs.setString(login_activity, "gcm_reg_id",
					gcm_reg_id);
			AccessSharedPrefs.setString(login_activity, "fb_link",
					user.getLink());
			ServerDBUserTable.query(ServerDBUserTable.class,
					new StackMobQuery().field(new StackMobQueryField("user_id")
							.isEqualTo(user.getId())),
					new StackMobQueryCallback<ServerDBUserTable>() {
						@Override
						public void failure(StackMobException arg0) {
							Log.w("SELECT COUNT(*) AS c FROM user_table WHERE id='$id'",
									arg0);
						}

						@Override
						public void success(List<ServerDBUserTable> returenedVar) {
							Log.w("SELECT COUNT(*) AS c FROM user_table WHERE id='$id'",
									"success");
							ServerDBAndroidDevices
									.query(ServerDBAndroidDevices.class,
											new StackMobQuery()
													.field(new StackMobQueryField(
															"user_id")
															.isEqualTo(user
																	.getId()))
													.field(new StackMobQueryField(
															"gcm_id")
															.isEqualTo(gcm_reg_id)),
											new StackMobQueryCallback<ServerDBAndroidDevices>() {
												@Override
												public void failure(StackMobException arg0) {
													Log.w("SELECT * FROM user_android_devices WHERE id='$id' AND gcm_id='$gcmid'",
															arg0);
												}

												@Override
												public void success(List<ServerDBAndroidDevices> returenedVar) {
													Log.w("SELECT * FROM user_android_devices WHERE id='$id' AND gcm_id='$gcmid'",
															"success");
													if (returenedVar.size() == 0) {
														Log.w("SELECT * FROM user_android_devices WHERE id='$id' AND gcm_id='$gcmid'",
																"size = 0");
														new ServerDBAndroidDevices(
																user.getId(),
																gcm_reg_id)
																.save(new StackMobCallback() {
																	@Override
																	public void success(String arg0) {
																		Log.w("INSERT INTO user_android_devices values('$id','$gcmid','$tday')",
																				"success");
																	}

																	@Override
																	public void failure(StackMobException arg0) {
																		Log.w("INSERT INTO user_android_devices values('$id','$gcmid','$tday')",
																				arg0);
																	}
																});
													}
												}
											});
							if (returenedVar.size() == 0)
								new ServerDBUserTable(user.getId(), 1, user
										.getFirstName(), user.getLastName(),
										"", user.getLink(), user.getBirthday(),
										"", "", "", 1)
										.save(new StackMobCallback() {
											@Override
											public void success(String arg0) {
												openMainActivity();
											}

											@Override
											public void failure(StackMobException arg0) {
											}
										});
							else {
								// TODO Bigggg else
							}
						}
					});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	public static void openMainActivity() {
		Intent intent = new Intent(login_activity, MainActivity.class);
		login_activity.startActivity(intent);
		((LogIn) login_activity).finish();
	}
}
