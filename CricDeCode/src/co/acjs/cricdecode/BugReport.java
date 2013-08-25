package co.acjs.cricdecode;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.util.Log;

@ReportsCrashes(formKey = "", formUri = "http://buggs.acjs.co/report/")
public class BugReport extends Application {
	@SuppressWarnings("null")
	@Override
	public void onCreate() {
		super.onCreate();
		// The following line triggers the initialization of ACRA
		ACRA.init(this);
		
		//int a[]= new int[4];
		//Log.w("",""+a[10]);			
	}
}