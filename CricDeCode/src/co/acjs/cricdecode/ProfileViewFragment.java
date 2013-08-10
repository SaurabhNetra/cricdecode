package co.acjs.cricdecode;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class ProfileViewFragment extends SherlockFragment {
	static ProfileViewFragment profileViewFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		profileViewFragment = this;
		View rootView = inflater.inflate(R.layout.profile_view, container,
				false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		onProfileViewing(view);
	}

	public void onProfileViewing(View view) {
		Log.d("Debug", "on Profile Viewing called");
		ProfileData.mPrefs = getSherlockActivity().getSharedPreferences(
				"CricDeCode", Context.MODE_PRIVATE);
		String profilePicturePath = ProfileData.mPrefs.getString(
				"profilePicturePath", "");
		if (!profilePicturePath.equals("")) {
			BitmapWorkerTask task = new BitmapWorkerTask(
					((ImageView) view.findViewById(R.id.profile_picture)));
			task.execute(
					profilePicturePath,
					getSherlockActivity().getResources().getString(
							R.string.profile_picture_size_view));
		}
		((TextView) view.findViewById(R.id.name)).setText(ProfileData.mPrefs
				.getString("name", ""));
		((TextView) view.findViewById(R.id.nickname))
				.setText(ProfileData.mPrefs.getString("nickname", ""));
		((TextView) view.findViewById(R.id.date_of_birth))
				.setText(ProfileData.mPrefs.getString("dateOfBirth", ""));
		((TextView) view.findViewById(R.id.role)).setText(ProfileData.mPrefs
				.getString("role", ""));
		((TextView) view.findViewById(R.id.batting_style))
				.setText(ProfileData.mPrefs.getString("battingStyle", ""));
		((TextView) view.findViewById(R.id.bowling_style))
				.setText(ProfileData.mPrefs.getString("bowlingStyle", ""));
	}
}
