package co.acjs.cricdecode;

import java.util.HashMap;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;

@ReportsCrashes(formKey = "", formUri = "http://buggs.acjs.co/report/index.php")
public class CDCAppClass extends Application {
	public static final String NEEDS_TO_BE_CALLED = "to_be";
	public static final String DOESNT_NEED_TO_BE_CALLED = "";

	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
	}
	public enum TrackerName {
	    APP_TRACKER
	  }
	  HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	  synchronized Tracker getTracker(TrackerName trackerId) {
		    if (!mTrackers.containsKey(trackerId)) {

		      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		      Tracker t = analytics.newTracker(R.xml.analytics);
		      mTrackers.put(trackerId, t);

		    }
		    return mTrackers.get(trackerId);
		  }
}