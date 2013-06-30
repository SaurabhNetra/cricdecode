package co.acjs.cricdecode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

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
	public static SharedPreferences	mPrefs;
	public static MainActivity		mainAct;

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
				AdView adView = (AdView) (mainAct).findViewById(R.id.adView);
				adView.loadAd(new AdRequest());
			}
		}).start();

		// Shared Preferences initialize
		mPrefs = getSharedPreferences("CrecDeCode", Context.MODE_PRIVATE);
		mPrefs.getString("eMailID", "");
		mPrefs.getString("name", "");
		mPrefs.getString("nickName", "");
		mPrefs.getString("dateOfBirth", "");
		mPrefs.getString("playingRole", "");
		mPrefs.getString("battingStyle", "");
		mPrefs.getString("bowlingStyle", "");
		mPrefs.getString("qualification", "");
		mPrefs.getString("country", "");
		mPrefs.getString("state", "");
		mPrefs.getString("club", "");
		mPrefs.getString("university", "");
		mPrefs.getString("college", "");
		mPrefs.getString("school", "");
		mPrefs.getInt("age", 0);
		mPrefs.getInt("sex", 0);

		// Google Plus Sign In
		mPlusClient = new PlusClient.Builder(this, this, this)
				.setVisibleActivities("http://schemas.google.com/AddActivity",
						"http://schemas.google.com/BuyActivity").build();
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

		// Display profile if needed
		if (!"".equals(mPrefs.getString("eMailID", ""))) {
			showProfile();
		} else {
			viewFragment(new SignInFragment());
		}
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
					MainActivity.mainAct.saveEditedProfile();
					viewFragment(new ProfileFragment());
					((Button) view).setText(getResources().getString(
							R.string.edit));
				}
				break;
			case R.id.sign_button:
				// Google Plus Button
				if (!mPlusClient.isConnected()) {
					Log.d(TAG, "Not Connected yet");
					if (mConnectionResult == null) {
						Log.d(TAG, "Connect Called");
						mConnectionProgressDialog.show();
					} else {
						try {
							mConnectionResult.startResolutionForResult(
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
				break;
			default:
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

		if ("".equals(mPrefs.getString("eMailID", ""))) {
			// Get My Profile
			mPlusClient.loadPerson(this, "me");
		}
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onPersonLoaded(ConnectionResult arg0, Person person) {
		setProfile(person);

		// When my information is retrieved open profile page.
		showProfile();
	}

	/** Non overridden methods */
	private void setProfile(Person person) {
		// Set my Profile
		if (person.hasImage()) {
			Intent intent = new Intent(this,
					DownloadProfilePictureService.class);
			intent.putExtra("PPImg", person.getImage().getUrl());
			startService(intent);
		}
		AccessSharedPreferences.setEMailID(MainActivity.mainAct,
				mPlusClient.getAccountName());
		if (person.hasDisplayName()) {
			AccessSharedPreferences.setName(MainActivity.mainAct,
					person.getDisplayName());
		}
		if (person.hasNickname()) {
			AccessSharedPreferences.setNickName(MainActivity.mainAct,
					person.getNickname());
		}
		if (person.hasGender()) {
			AccessSharedPreferences.setSex(MainActivity.mainAct,
					person.getGender());
		}
		if (person.hasBirthday()) {
			AccessSharedPreferences.setDateOfBirth(MainActivity.mainAct,
					person.getBirthday());
			setAge(person);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void setAge(Person person) {
		Calendar dateOfBirth = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dateOfBirth.setTime(sdf.parse(person.getBirthday()));
		} catch (ParseException e) {
		}
		int year = dateOfBirth.get(Calendar.YEAR);
		int month = dateOfBirth.get(Calendar.MONTH);
		int day = dateOfBirth.get(Calendar.DAY_OF_MONTH);
		int age = 0;
		if (year > 2000) {
			Calendar today = Calendar.getInstance();
			int curYear = today.get(Calendar.YEAR);
			int curMonth = today.get(Calendar.MONTH);
			int curDay = today.get(Calendar.DAY_OF_MONTH);
			age = curYear - year;
			if (curMonth < month || (month == curMonth && curDay < day)) {
				age--;
			}
		}
		AccessSharedPreferences.setAge(MainActivity.mainAct, age);
	}

	public void viewFragment(SherlockFragment fragment) {

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment, and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.container_profile, fragment);

		// Commit the transaction
		transaction.commit();
	}

	private void showProfile() {
		// Add the Fragment
		viewFragment(new ProfileFragment());
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.actionbar_profile);
	}

	public void onProfileEditing() {
		((EditText) findViewById(R.id.txtNickame)).setText(mPrefs.getString(
				"nickName", ""));
		((EditText) findViewById(R.id.txtQualification)).setText(mPrefs
				.getString("qualification", ""));
		((EditText) findViewById(R.id.txtCountry)).setText(mPrefs.getString(
				"country", ""));
		((EditText) findViewById(R.id.txtState)).setText(mPrefs.getString(
				"state", ""));
		((EditText) findViewById(R.id.txtClub)).setText(mPrefs.getString(
				"club", ""));
		((EditText) findViewById(R.id.txtUniversity)).setText(mPrefs.getString(
				"university", ""));
		((EditText) findViewById(R.id.txtCollege)).setText(mPrefs.getString(
				"college", ""));
		((EditText) findViewById(R.id.txtSchool)).setText(mPrefs.getString(
				"school", ""));
		switch (mPrefs.getInt("sex", 0)) {
			case 0:
				((RadioButton) findViewById(R.id.male)).setChecked(true);
				break;
			case 1:
				((RadioButton) findViewById(R.id.female)).setChecked(true);
				break;
			case 2:
				break;
		}
		selectFromSpinner(R.id.spnPlayingRole,
				mPrefs.getString("playingRole", ""));
		selectFromSpinner(R.id.spnBattingStyle,
				mPrefs.getString("battingStyle", ""));
		selectFromSpinner(R.id.spnBowlingStyle,
				mPrefs.getString("bowlingStyle", ""));
	}

	public void onProfileViewing() {
		String sex = "";
		switch (mPrefs.getInt("sex", 0)) {
			case 0:
				sex = "Male";
				break;
			case 1:
				sex = "Female";
				break;
			case 2:
				sex = "Other";
				break;
		}

		((TextView) findViewById(R.id.lblName)).setText(mPrefs.getString(
				"name", ""));
		((TextView) findViewById(R.id.lblNickame)).setText(mPrefs.getString(
				"nickName", ""));
		((TextView) findViewById(R.id.lblDOB)).setText(mPrefs.getString(
				"dateOfBirth", ""));
		((TextView) findViewById(R.id.lblPlayingRole)).setText(mPrefs
				.getString("playingRole", ""));
		((TextView) findViewById(R.id.lblBattingStyle)).setText(mPrefs
				.getString("battingStyle", ""));
		((TextView) findViewById(R.id.lblBowlingStyle)).setText(mPrefs
				.getString("bowlingStyle", ""));
		((TextView) findViewById(R.id.lblQualification)).setText(mPrefs
				.getString("qualification", ""));
		((TextView) findViewById(R.id.lblCountry)).setText(mPrefs.getString(
				"country", ""));
		((TextView) findViewById(R.id.lblState)).setText(mPrefs.getString(
				"state", ""));
		((TextView) findViewById(R.id.lblClub)).setText(mPrefs.getString(
				"club", ""));
		((TextView) findViewById(R.id.lblUniversity)).setText(mPrefs.getString(
				"university", ""));
		((TextView) findViewById(R.id.lblCollege)).setText(mPrefs.getString(
				"college", ""));
		((TextView) findViewById(R.id.lblSchool)).setText(mPrefs.getString(
				"school", ""));
		int age = mPrefs.getInt("age", 0);
		if (age != 0) {
			((TextView) findViewById(R.id.lblAge)).setText(age + "");
		}
		((TextView) findViewById(R.id.lblGender)).setText(sex);
		try {
			updateProfilePicture();
		} catch (Exception e) {
		}
	}

	public void saveEditedProfile() {
		AccessSharedPreferences
				.setNickName(mainAct,
						((EditText) findViewById(R.id.txtNickame)).getText()
								.toString());
		AccessSharedPreferences.setQualification(mainAct,
				((EditText) findViewById(R.id.txtQualification)).getText()
						.toString());
		AccessSharedPreferences
				.setCountry(mainAct, ((EditText) findViewById(R.id.txtCountry))
						.getText().toString());
		AccessSharedPreferences.setState(mainAct,
				((EditText) findViewById(R.id.txtState)).getText().toString());
		AccessSharedPreferences.setClub(mainAct,
				((EditText) findViewById(R.id.txtClub)).getText().toString());
		AccessSharedPreferences.setUniversity(mainAct,
				((EditText) findViewById(R.id.txtUniversity)).getText()
						.toString());
		AccessSharedPreferences
				.setCollege(mainAct, ((EditText) findViewById(R.id.txtCollege))
						.getText().toString());
		AccessSharedPreferences.setSchool(mainAct,
				((EditText) findViewById(R.id.txtSchool)).getText().toString());
		AccessSharedPreferences.setPlayingRole(mainAct,
				((Spinner) findViewById(R.id.spnPlayingRole)).getSelectedItem()
						.toString());
		AccessSharedPreferences.setBattingStyle(mainAct,
				((Spinner) findViewById(R.id.spnBattingStyle))
						.getSelectedItem().toString());
		AccessSharedPreferences.setBowlingStyle(mainAct,
				((Spinner) findViewById(R.id.spnBowlingStyle))
						.getSelectedItem().toString());
		int sex = 1;
		switch (((RadioGroup) findViewById(R.id.rdgrpGender))
				.getCheckedRadioButtonId()) {
			case R.id.male:
				sex = 0;
				break;
			case R.id.female:
				sex = 1;
				break;
		}
		AccessSharedPreferences.setSex(mainAct, sex);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void selectFromSpinner(int id, String selected) {
		Spinner spinner = (Spinner) findViewById(id);
		ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
		int spinnerPosition = myAdap.getPosition(selected);
		spinner.setSelection(spinnerPosition);
	}

	public static void updateProfilePicture() {
		Bitmap bm = BitmapFactory.decodeFile(Environment
				.getExternalStorageDirectory().getAbsolutePath() + mainAct
				.getResources().getString(R.string.profile_picture_image));
		ImageView ProPic = (ImageView) ((MainActivity) mainAct)
				.findViewById(R.id.imgProfilePic);
		ProPic.setImageBitmap(bm);
	}
}