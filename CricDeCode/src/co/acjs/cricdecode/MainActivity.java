package co.acjs.cricdecode;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends SherlockFragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, OnClickListener,
		PlusClient.OnPersonLoadedListener {

	// Class Variables
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private static final String TAG = "CricDeCode";
	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	public static Context mainAct;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
		// Google Plus Button
		if (view.getId() == R.id.sign_button && !mPlusClient.isConnected()) {
			if (mConnectionResult == null) {
				mConnectionProgressDialog.show();
			} else {
				try {
					mConnectionResult.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					// Try connecting again.
					mConnectionResult = null;
					mPlusClient.connect();
				}
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			// The user clicked the sign-in button already. Start to resolve
			// connection errors. Wait until onConnected() to dismiss the
			// connection dialog.
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}
		// Save the intent so that we can start an activity when the user clicks
		// the sign-in button.
		mConnectionResult = result;
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR
				&& responseCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mConnectionProgressDialog.dismiss();
		Log.d(TAG, "onConnected");
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPersonLoaded(ConnectionResult arg0, Person arg1) {
		// TODO Auto-generated method stub

	}
}
