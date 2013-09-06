package co.acjs.cricdecode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gcm.GCMRegistrar;

public class LogIn extends SherlockActivity {
	static GraphUser user;
	static Context login_activity;

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
		ProfileData.mPrefs = getSharedPreferences("CricDeCode",
				Context.MODE_PRIVATE);
		login_activity = this;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("onStart", ProfileData.mPrefs.getString("id", ""));
		if (!ProfileData.mPrefs.getString("id", "").equals("")) {
			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			startActivity(intent);
			finish();
		}

		// Google Analytics Stop
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Google Analytics Stop
		EasyTracker.getInstance().activityStop(this);
	}

	public void FBLogin(View v) {
		// start FB Login
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				if (session.isOpened()) {
					((Button) findViewById(R.id.login))
							.setVisibility(View.GONE);
					((ProgressBar) findViewById(R.id.progress_bar))
							.setVisibility(View.VISIBLE);
					// make request to the /me API
					Request.executeMeRequestAsync(session,
							new Request.GraphUserCallback() {

								// callback after Graph API response with user
								// object
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									if (user != null) {
										Log.w("Face Book Login Complete",
												"LogIn: ");
										LogIn.user = user;
										GCMRegistration();
									}
								}
							});
				}
			}
		});
	}

	void GCMRegistration() {

		GCMRegistrar.unregister(this);
		GCMRegistrar.register(this, getResources().getString(R.string.projno));

	}

	static void startApp() {

		Log.w("Start App", "LogIn: ");
		Intent intent = new Intent(login_activity, SignInService.class);
		intent.putExtra("GCM_ID",
				GCMRegistrar.getRegistrationId(login_activity));
		intent.putExtra("ID", user.getId());
		intent.putExtra("FName", user.getFirstName());
		intent.putExtra("LName", user.getLastName());
		intent.putExtra("DOB", user.getBirthday());
		intent.putExtra("FBLink", user.getLink());
		ProfileData.setGCMID(login_activity,
				GCMRegistrar.getRegistrationId(login_activity));
		ProfileData.setID(login_activity, user.getId());
		ProfileData.setFName(login_activity, user.getFirstName());
		ProfileData.setLName(login_activity, user.getLastName());
		ProfileData.setDOB(login_activity, user.getBirthday());
		ProfileData.setFbLink(login_activity, user.getLink());
		login_activity.startService(intent);
		intent = new Intent(login_activity, MainActivity.class);
		login_activity.startActivity(intent);
		((LogIn) login_activity).finish();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}
}
