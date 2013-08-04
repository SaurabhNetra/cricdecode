package co.acjs.cricdecode;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ProfileEditFragment extends SherlockFragment {
	static ProfileEditFragment profileEditFragment;

	// Declare Variables
	String profilePicturePath;
	ImageView profile_picture;
	EditText name, nickname, city, state, country;
	TextView date_of_birth;
	Spinner role, batting_style, bowling_style;

	// Declare Constants
	static final int RESULT_LOAD_IMAGE = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		profileEditFragment = this;
		View rootView = inflater.inflate(R.layout.profile_edit, container,
				false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d("Debug", "On Activity Created called EditProfile");
		init(view);
		if (savedInstanceState != null) {
			restoreInstanceState(savedInstanceState);
		} else {
			onProfileEditing();
		}
	}

	public void init(View view) {
		profilePicturePath = "";
		profile_picture = (ImageView) view.findViewById(R.id.profile_picture);
		name = (EditText) view.findViewById(R.id.name);
		nickname = (EditText) view.findViewById(R.id.nickname);
		date_of_birth = (TextView) view.findViewById(R.id.date_of_birth);
		role = (Spinner) view.findViewById(R.id.role);
		batting_style = (Spinner) view.findViewById(R.id.batting_style);
		bowling_style = (Spinner) view.findViewById(R.id.bowling_style);
		city = (EditText) view.findViewById(R.id.city);
		state = (EditText) view.findViewById(R.id.state);
		country = (EditText) view.findViewById(R.id.country);
	}

	public void restoreInstanceState(Bundle savedInstanceState) {
		profilePicturePath = savedInstanceState.getString("profilePicturePath");
		if (!profilePicturePath.equals("")) {
			BitmapWorkerTask task = new BitmapWorkerTask(profile_picture);
			task.execute(profilePicturePath);
		}
		date_of_birth.setText(savedInstanceState.getString("date_of_birth"));
		Log.d("Debug",
				"profilePicturePath " + profilePicturePath + " date_of_birth"
						+ savedInstanceState.getString("date_of_birth"));
		// role.setSelection(savedInstanceState.getInt("role"));
		// batting_style.setSelection(savedInstanceState.getInt("batting_style"));
		// bowling_style.setSelection(savedInstanceState.getInt("bowling_style"));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("profilePicturePath", profilePicturePath);
		outState.putString("date_of_birth", date_of_birth.getText().toString());
		Log.d("Debug", "On Profile Saved Instance");
		// outState.putInt("role", role.getSelectedItemPosition());
		// outState.putInt("batting_style",
		// batting_style.getSelectedItemPosition());
		// outState.putInt("bowling_style",
		// bowling_style.getSelectedItemPosition());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("Debug", "On Activity Result called");
		// Case of Loading Image from Gallery
		if (requestCode == RESULT_LOAD_IMAGE
				&& resultCode == SherlockFragmentActivity.RESULT_OK
				&& null != data) {
			// Receive Image Path
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getSherlockActivity().getContentResolver().query(
					selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			profilePicturePath = cursor.getString(columnIndex);
			cursor.close();
			BitmapWorkerTask task = new BitmapWorkerTask(profile_picture);
			task.execute(profilePicturePath);
		}

	}

	public void getProfilePicture() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	public void onProfileEditing() {
		Log.d("Debug", "On Profile Editting called");
		ProfileData.mPrefs = getSherlockActivity().getSharedPreferences(
				"CricDeCode", Context.MODE_PRIVATE);
		profilePicturePath = ProfileData.mPrefs.getString("profilePicturePath",
				"");
		if (!profilePicturePath.equals("")) {
			profile_picture.setImageBitmap(BitmapFactory
					.decodeFile(profilePicturePath));
		}
		name.setText(ProfileData.mPrefs.getString("name", ""));
		nickname.setText(ProfileData.mPrefs.getString("nickname", ""));
		String dateOfBirth = ProfileData.mPrefs.getString("dateOfBirth", "");
		if (!dateOfBirth.equals("")) {
			date_of_birth.setText(dateOfBirth);
		}
		selectFromSpinner(role, ProfileData.mPrefs.getString("role", ""));
		selectFromSpinner(batting_style,
				ProfileData.mPrefs.getString("battingStyle", ""));
		selectFromSpinner(bowling_style,
				ProfileData.mPrefs.getString("bowlingStyle", ""));
		city.setText(ProfileData.mPrefs.getString("city", ""));
		state.setText(ProfileData.mPrefs.getString("state", ""));
		country.setText(ProfileData.mPrefs.getString("country", ""));
	}

	public void saveEditedProfile() {
		ProfileData.setProfilePicturePath(getSherlockActivity(),
				profilePicturePath);
		ProfileData.setName(getSherlockActivity(), name.getText().toString());
		ProfileData.setNickname(getSherlockActivity(), nickname.getText()
				.toString());
		String dob_str = date_of_birth.getText().toString();
		ProfileData.setDateOfBirth(getSherlockActivity(), dob_str);
		ProfileData.setRole(getSherlockActivity(), role.getSelectedItem()
				.toString());
		ProfileData.setBattingStyle(getSherlockActivity(), batting_style
				.getSelectedItem().toString());
		ProfileData.setBowlingStyle(getSherlockActivity(), bowling_style
				.getSelectedItem().toString());
		ProfileData.setCity(getSherlockActivity(), city.getText().toString());
		ProfileData.setState(getSherlockActivity(), state.getText().toString());
		ProfileData.setCountry(getSherlockActivity(), country.getText()
				.toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void selectFromSpinner(Spinner spinner, String selected) {
		ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
		int spinnerPosition = myAdap.getPosition(selected);
		spinner.setSelection(spinnerPosition);
	}

}
