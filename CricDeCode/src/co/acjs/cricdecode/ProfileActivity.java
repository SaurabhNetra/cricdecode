package co.acjs.cricdecode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;

public class ProfileActivity extends SherlockFragmentActivity {

	// Class Variables
	private static final String		TAG					= "CricDeCode";
	public static SharedPreferences	mPrefs;
	public static ProfileActivity	profileActivity;

	private static String			btnEditProfile;

	// Constants
	static final int				RESULT_LOAD_IMAGE	= 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.actionbar_profile);

		if (savedInstanceState != null) {
			btnEditProfile = savedInstanceState.getString("fragment");
			if (btnEditProfile.equals(getResources().getString(R.string.edit))) {
				viewFragment(new ProfileFragment());
			} else {
				viewFragment(new ProfileEditFragment());
				((Button) findViewById(R.id.btnEditProfile))
						.setText(R.string.view);
			}
		} else {
			viewFragment(new ProfileFragment());
		}

		// Main Activity Context
		profileActivity = this;

		// Shared Preferences initialize
		mPrefs = getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		String str = ((Button) findViewById(R.id.btnEditProfile)).getText()
				.toString();
		outState.putString("fragment", str);
		if (str.equals(getResources().getString(R.string.view))) {
			outState.putString("name", ((EditText) findViewById(R.id.txtName))
					.getText().toString());
			outState.putString("nickname",
					((EditText) findViewById(R.id.txtNickname)).getText()
							.toString());
			outState.putString("dateOfBirth",
					((TextView) findViewById(R.id.lblDOBEdit)).getText()
							.toString());
			outState.putString("playingRole",
					((Spinner) findViewById(R.id.spnPlayingRole))
							.getSelectedItem().toString());
			outState.putString("battingStyle",
					((Spinner) findViewById(R.id.spnBattingStyle))
							.getSelectedItem().toString());
			outState.putString("bowlingStyle",
					((Spinner) findViewById(R.id.spnBowlingStyle))
							.getSelectedItem().toString());
		}

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null && btnEditProfile.equals(getResources()
				.getString(R.string.view))) {
			((EditText) findViewById(R.id.txtName)).setText(savedInstanceState
					.getString("name"));
			((EditText) findViewById(R.id.txtNickname))
					.setText(savedInstanceState.getString("nickname"));
			((TextView) findViewById(R.id.lblDOBEdit))
					.setText(savedInstanceState.getString("dateOfBirth"));
			selectFromSpinner(R.id.spnPlayingRole,
					savedInstanceState.getString("playingRole"));
			selectFromSpinner(R.id.spnBattingStyle,
					savedInstanceState.getString("battingStyle"));
			selectFromSpinner(R.id.spnBowlingStyle,
					savedInstanceState.getString("bowlingStyle"));
		}
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("Debug", "onActivityResult called");

		// Case of Loading Image from Gallery
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			// Receive Image Path
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			AccessSharedPreferences.setProfilePicPath(this, picturePath);
			((ImageView) findViewById(R.id.imgProfilePicEdit))
					.setImageBitmap(BitmapFactory.decodeFile(picturePath));

		}

	}

	/* Non overridden methods */

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
					saveEditedProfile();
					viewFragment(new ProfileFragment());
					((Button) view).setText(getResources().getString(
							R.string.edit));
				}
				break;
			case R.id.lblDOBEdit:
				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				// Create and show the dialog.
				DatePickerFragment newFragment = new DatePickerFragment();
				newFragment.setView_callee(findViewById(R.id.lblDOBEdit));
				newFragment.show(ft, null);
				break;
			case R.id.imgProfilePicEdit:
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, ProfileActivity.RESULT_LOAD_IMAGE);
				break;
			default:
				break;
		}
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
		String picturePath = mPrefs.getString("profilePicPath", "");
		if (!picturePath.equals("")) {
			((ImageView) findViewById(R.id.imgProfilePicEdit))
					.setImageBitmap(BitmapFactory.decodeFile(picturePath));
		}
		((EditText) findViewById(R.id.txtName)).setText(mPrefs.getString(
				"name", ""));
		((EditText) findViewById(R.id.txtNickname)).setText(mPrefs.getString(
				"nickname", ""));
		String dob_str = mPrefs.getString("dateOfBirth", "");
		if (!dob_str.equals("")) {
			((TextView) findViewById(R.id.lblDOBEdit)).setText(dob_str);
		}
		selectFromSpinner(R.id.spnPlayingRole,
				mPrefs.getString("playingRole", ""));
		selectFromSpinner(R.id.spnBattingStyle,
				mPrefs.getString("battingStyle", ""));
		selectFromSpinner(R.id.spnBowlingStyle,
				mPrefs.getString("bowlingStyle", ""));
	}

	public void onProfileViewing() {

		((TextView) findViewById(R.id.lblName)).setText(mPrefs.getString(
				"name", ""));
		((TextView) findViewById(R.id.lblNickame)).setText(mPrefs.getString(
				"nickname", ""));
		((TextView) findViewById(R.id.lblDOB)).setText(mPrefs.getString(
				"dateOfBirth", ""));
		((TextView) findViewById(R.id.lblPlayingRole)).setText(mPrefs
				.getString("playingRole", ""));
		((TextView) findViewById(R.id.lblBattingStyle)).setText(mPrefs
				.getString("battingStyle", ""));
		((TextView) findViewById(R.id.lblBowlingStyle)).setText(mPrefs
				.getString("bowlingStyle", ""));
		String picturePath = mPrefs.getString("profilePicPath", "");
		if (!picturePath.equals("")) {
			((ImageView) findViewById(R.id.imgProfilePic))
					.setImageBitmap(BitmapFactory.decodeFile(picturePath));
		}
	}

	public void saveEditedProfile() {
		AccessSharedPreferences.setName(this,
				((EditText) findViewById(R.id.txtName)).getText().toString());
		AccessSharedPreferences.setNickName(this,
				((EditText) findViewById(R.id.txtNickname)).getText()
						.toString());
		String dob_str = ((TextView) findViewById(R.id.lblDOBEdit)).getText()
				.toString();
		AccessSharedPreferences.setDateOfBirth(this, dob_str);
		AccessSharedPreferences.setPlayingRole(this,
				((Spinner) findViewById(R.id.spnPlayingRole)).getSelectedItem()
						.toString());
		AccessSharedPreferences.setBattingStyle(this,
				((Spinner) findViewById(R.id.spnBattingStyle))
						.getSelectedItem().toString());
		AccessSharedPreferences.setBowlingStyle(this,
				((Spinner) findViewById(R.id.spnBowlingStyle))
						.getSelectedItem().toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void selectFromSpinner(int id, String selected) {
		Spinner spinner = (Spinner) findViewById(id);
		ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
		int spinnerPosition = myAdap.getPosition(selected);
		spinner.setSelection(spinnerPosition);
	}
}