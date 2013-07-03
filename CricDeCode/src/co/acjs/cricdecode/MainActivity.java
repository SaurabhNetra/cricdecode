package co.acjs.cricdecode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends SherlockFragmentActivity implements OnClickListener {

	// Class Variables
	private static final String		TAG	= "CricDeCode";
	public static SharedPreferences	mPrefs;
	public static MainActivity		mainAct;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.actionbar_profile);
		viewFragment(new ProfileEditFragment());

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
		mPrefs = getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);

	}

	@Override
	protected void onStart() {
		super.onStart();

		// Google Analytics Stop
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Google Analytics Start
		EasyTracker.getInstance().activityStart(this);
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
			case R.id.btnCurrentMatches:
				Intent intent = new Intent(this, MatchActivity.class);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

	/** Non overridden methods */

	@SuppressLint("SimpleDateFormat")
	private void setAge(String dateOfBirthString) {
		Calendar dateOfBirth = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dateOfBirth.setTime(sdf.parse(dateOfBirthString));
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

		// Replace whatever is in the fragment_container view with this fragment
		transaction.replace(R.id.container_profile, fragment);

		// Commit the transaction
		transaction.commit();
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
			default:
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
			default:
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
		((TextView) findViewById(R.id.lblAge))
				.setText(mPrefs.getInt("age", 0) + "");
		((TextView) findViewById(R.id.lblGender)).setText(sex);
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
		int sex = 0;
		switch (((RadioGroup) findViewById(R.id.rdgrpGender))
				.getCheckedRadioButtonId()) {
			case R.id.male:
				sex = 0;
				break;
			case R.id.female:
				sex = 1;
				break;
			default:
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
}