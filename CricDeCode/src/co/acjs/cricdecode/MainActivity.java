package co.acjs.cricdecode;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends SherlockFragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, OnClickListener, PlusClient.OnPersonLoadedListener {

	// Class Variables
	private static final int		REQUEST_CODE_RESOLVE_ERR	= 271516;
	private static final String		TAG							= "CricDeCode";
	private static ProgressDialog	mConnectionProgressDialog;
	private static PlusClient		mPlusClient;
	private static ConnectionResult	mConnectionResult;
	public static Context			mainAct;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewFragment(new SignInFragment());

		// Main Activity Context
		mainAct = this;

		// Look up the AdView as a resource and load a request.
		(new Thread() {
			public void run() {
				Looper.prepare();
				AdView adView = (AdView) ((SherlockFragmentActivity) mainAct)
						.findViewById(R.id.adView);
				adView.loadAd(new AdRequest());
			}
		}).start();

		// Google Plus Sign In
		mPlusClient = new PlusClient.Builder(this, this, this)
				.setVisibleActivities("http://schemas.google.com/AddActivity",
						"http://schemas.google.com/BuyActivity").build();
		try {
			findViewById(R.id.sign_button).setOnClickListener(
					new View.OnClickListener() {
						public void onClick(View v) {
							// Google Plus Button
							if (!mPlusClient.isConnected()) {
								Log.d(TAG, "Not Connected yet");
								if (mConnectionResult == null) {
									Log.d(TAG, "Connect Called");
									mConnectionProgressDialog.show();
								} else {
									try {
										mConnectionResult
												.startResolutionForResult(
														(MainActivity) mainAct,
														REQUEST_CODE_RESOLVE_ERR);
									} catch (SendIntentException e) {
										Log.d(TAG, "Try connecting again");
										// Try connecting again
										mConnectionResult = null;
										mPlusClient.connect();
									}
								}
							}
						}
					});
		} catch (Exception e) {
		}
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Google Analytics Start
		EasyTracker.getInstance().activityStart(this);

		// Google+ Button
		mPlusClient.disconnect();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Google Analytics Stop
		EasyTracker.getInstance().activityStop(this);

		// Google+ Button
		mPlusClient.connect();
	}

	@Override
	public void onClick(View view) {
		Log.d(TAG, "onClick Called");

		switch (view.getId()) {
			case R.id.btnEditProfile:
				String btn_str = ((Button) view).getText().toString();
				if (btn_str.equals(getResources().getString(R.string.edit))) {
					viewFragment(new ProfileEditFragment());
					((Button) view).setText(getResources().getString(
							R.string.view));
				} else {
					viewFragment(new ProfileFragment());
					((Button) view).setText(getResources().getString(
							R.string.edit));
				}
				break;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d(TAG, "Connection Failed");
		if (mConnectionProgressDialog.isShowing()) {
			// The user clicked the sign-in button already. Start to resolve connection errors. Wait until onConnected() to dismiss the connection dialog.
			if (result.hasResolution()) {
				Log.d(TAG, "hasResolution");
				try {
					Log.d(TAG, "Connection Try");
					result.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}
		// Save the intent so that we can start an activity when the user clicks the sign-in button.
		mConnectionResult = result;
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
			Log.d(TAG, "onActivityResult");
			mConnectionResult = null;
			mPlusClient.connect();
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		String accountName = mPlusClient.getAccountName();
		mConnectionProgressDialog.dismiss();
		Log.d(TAG, accountName + " Connected");

		// Add the Fragment
		viewFragment(new ProfileFragment());

		// Edit Profile Button
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.actionbar_profile);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPersonLoaded(ConnectionResult arg0, Person arg1) {
		// TODO Auto-generated method stub

	}

	public void viewFragment(SherlockFragment fragment) {

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment, and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.container_profile, fragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}
}
