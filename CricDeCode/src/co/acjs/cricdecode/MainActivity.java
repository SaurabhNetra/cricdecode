package co.acjs.cricdecode;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;

public class MainActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		 setContentView(R.layout.activity_profile);
		// setContentView(R.layout.activity_goal);

	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStop(this);
	}
}