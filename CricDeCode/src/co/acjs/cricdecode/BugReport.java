package co.acjs.cricdecode;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "", formUri = "http://buggs.acjs.co/report/index.php")
public class BugReport extends Application {
	@Override
	public void onCreate() {
		super.onCreate();		
		ACRA.init(this);	
	}
}