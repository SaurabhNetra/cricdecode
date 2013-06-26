package co.acjs.cricdecode;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;

public class MainActivity extends SherlockFragmentActivity {
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
				AdView adView = (AdView) ((SherlockFragmentActivity) mainAct).findViewById(R.id.adView);
				adView.loadAd(new AdRequest());
			}
		}).start();
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