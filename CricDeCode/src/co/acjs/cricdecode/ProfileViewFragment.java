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
	static ProfileViewFragment	profileViewFragment;
	ImageView					iv;
	int							width, height;
	int							size;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
		AccessSharedPrefs.mPrefs = getSherlockActivity().getSharedPreferences(
				"CricDeCode", Context.MODE_PRIVATE);
		String profilePicturePath = AccessSharedPrefs.mPrefs.getString("profilePicturePath", "");

		width = AccessSharedPrefs.mPrefs.getInt("width", 0);
		height = AccessSharedPrefs.mPrefs.getInt("height", 0);
		size = width < height ? width : height;
		size -= 50;

		iv = (ImageView) view.findViewById(R.id.profile_picture);

		iv.getLayoutParams().width = size;
		iv.getLayoutParams().height = size;

		if (!profilePicturePath.equals("")) {
			BitmapWorkerTask task = new BitmapWorkerTask(
					((ImageView) view.findViewById(R.id.profile_picture)));
			task.execute(
					profilePicturePath,
					getSherlockActivity().getResources().getString(
							R.string.profile_picture_size_view));
		}
		((TextView) view.findViewById(R.id.fname)).setText(AccessSharedPrefs.mPrefs
				.getString("f_name", ""));
		((TextView) view.findViewById(R.id.lname)).setText(AccessSharedPrefs.mPrefs.getString(
				"l_name", ""));
		((TextView) view.findViewById(R.id.nickname))
				.setText(AccessSharedPrefs.mPrefs.getString("nickname", ""));
		((TextView) view.findViewById(R.id.date_of_birth))
				.setText(AccessSharedPrefs.mPrefs.getString("dob", ""));
		((TextView) view.findViewById(R.id.role)).setText(AccessSharedPrefs.mPrefs
				.getString("role", ""));
		((TextView) view.findViewById(R.id.batting_style))
				.setText(AccessSharedPrefs.mPrefs.getString("battingStyle", ""));
		((TextView) view.findViewById(R.id.bowling_style))
				.setText(AccessSharedPrefs.mPrefs.getString("bowlingStyle", ""));
	}
}
