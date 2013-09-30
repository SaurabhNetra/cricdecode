package co.acjs.cricdecode;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
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

public class LogIn extends SherlockActivity {
	static GraphUser	user;
	static Context		login_activity;

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
			if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
				openMainActivity();
			} else {
				Session session = Session.getActiveSession();
				final LoginButton loginButton = (LoginButton) findViewById(R.id.login);
				if (session.isOpened()) {
					loginButton.setVisibility(View.GONE);
					((ProgressBar) findViewById(R.id.progress_bar))
							.setVisibility(View.VISIBLE);
					Request.newMeRequest(session,
							new Request.GraphUserCallback() {
								@Override
								public void onCompleted(GraphUser user, Response response) {
									if (user != null) {
										openMainActivity();
									}
								}
							}).executeAsync();
				}
			}
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

	static void startApp(String gcm_reg_id) {
		Log.w("Start App", "LogIn: ");
		AccessSharedPrefs.setString(login_activity, "gcm_reg_id", gcm_reg_id);
		AccessSharedPrefs.setString(login_activity, "SignInServiceCalled",
				CDCAppClass.NEEDS_TO_BE_CALLED);
		AccessSharedPrefs.setString(login_activity, "id", user.getId());
		AccessSharedPrefs.setString(login_activity, "f_name",
				user.getFirstName());
		AccessSharedPrefs.setString(login_activity, "l_name",
				user.getLastName());
		AccessSharedPrefs.setString(login_activity, "dob", user.getBirthday());
		AccessSharedPrefs.setString(login_activity, "fb_link", user.getLink());
		Intent intent = new Intent(login_activity, SignInService.class);
		login_activity.startService(intent);
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
