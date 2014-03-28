package co.acjs.cricdecode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import co.acjs.cricdecode.CDCAppClass.TrackerName;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.analytics.HitBuilders;

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
		((MainActivity) getSherlockActivity()).setPageName(8);
		onProfileEditing();
		// }
		com.google.android.gms.analytics.Tracker t = ((CDCAppClass) getActivity()
				.getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName(getResources().getString(R.string.analyticsProfileEdit));
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	public void init(View view) {
		nickname = (EditText) view.findViewById(R.id.nickname);
		role = (Spinner) view.findViewById(R.id.role);
		batting_style = (Spinner) view.findViewById(R.id.batting_style);
		bowling_style = (Spinner) view.findViewById(R.id.bowling_style);
		InputFilter filter = new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				for (int i = start; i < end; i++) {
					if (!Character.isLetterOrDigit(source.charAt(i))
							&& !(source.charAt(i) == ' ')) {
						return "";
					}
				}
				return null;
			}
		};
		nickname.setFilters(new InputFilter[] { filter });
	}

	public void onProfileEditing() {
		Log.d("Debug", "On Profile Editting called");
		AccessSharedPrefs.mPrefs = getSherlockActivity().getSharedPreferences(
				"CricDeCode", Context.MODE_PRIVATE);
		Log.w("hhf",
				"In Edit: "
						+ AccessSharedPrefs.mPrefs.getString("nickname", ""));
		try {
			((MainActivity) MainActivity.main_context)
					.runOnUiThread(new Runnable() {
						public void run() {
							try {
								nickname.setText(AccessSharedPrefs.mPrefs
										.getString("nickname", ""));
								selectFromSpinner(role,
										AccessSharedPrefs.mPrefs.getString(
												"role", ""));
								selectFromSpinner(batting_style,
										AccessSharedPrefs.mPrefs.getString(
												"battingStyle", ""));
								selectFromSpinner(bowling_style,
										AccessSharedPrefs.mPrefs.getString(
												"bowlingStyle", ""));
							} catch (Exception e) {
							}
						}
					});
		} catch (Exception e) {
		}
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
