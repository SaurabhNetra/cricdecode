package co.acjs.cricdecode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;

public class ProfileEditFragment extends SherlockFragment {
	static ProfileEditFragment profileEditFragment;

	// Declare Variables
	EditText nickname;
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
		// if (savedInstanceState != null) {
		// restoreInstanceState(savedInstanceState);
		// } else {
		onProfileEditing();
		// }
	}

	public void init(View view) {
		nickname = (EditText) view.findViewById(R.id.nickname);
		role = (Spinner) view.findViewById(R.id.role);
		batting_style = (Spinner) view.findViewById(R.id.batting_style);
		bowling_style = (Spinner) view.findViewById(R.id.bowling_style);
	}

	public void onProfileEditing() {
		Log.d("Debug", "On Profile Editting called");
		AccessSharedPrefs.mPrefs = getSherlockActivity().getSharedPreferences(
				"CricDeCode", Context.MODE_PRIVATE);
		nickname.setText(AccessSharedPrefs.mPrefs.getString("nickname", ""));
		selectFromSpinner(role, AccessSharedPrefs.mPrefs.getString("role", ""));
		selectFromSpinner(batting_style,
				AccessSharedPrefs.mPrefs.getString("battingStyle", ""));
		selectFromSpinner(bowling_style,
				AccessSharedPrefs.mPrefs.getString("bowlingStyle", ""));
	}

	public void saveEditedProfile() {
		AccessSharedPrefs.setString(getSherlockActivity(), "nickname", nickname
				.getText().toString());
		AccessSharedPrefs.setString(getSherlockActivity(), "role", role
				.getSelectedItem().toString());
		AccessSharedPrefs.setString(getSherlockActivity(), "battingStyle",
				batting_style.getSelectedItem().toString());
		AccessSharedPrefs.setString(getSherlockActivity(), "bowlingStyle",
				bowling_style.getSelectedItem().toString());
		AccessSharedPrefs.setString(getSherlockActivity(),
				"ProfileEditServiceCalled", CDCAppClass.NEEDS_TO_BE_CALLED);
		Intent intent = new Intent(MainActivity.main_context,
				ProfileEditService.class);
		try {
			if (ProfileEditService.started) {
				MainActivity.main_context.stopService(intent);
				MainActivity.main_context.startService(intent);
			}
		} catch (NullPointerException e) {
			MainActivity.main_context.startService(intent);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void selectFromSpinner(Spinner spinner, String selected) {
		ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
		int spinnerPosition = myAdap.getPosition(selected);
		spinner.setSelection(spinnerPosition);
	}

}
