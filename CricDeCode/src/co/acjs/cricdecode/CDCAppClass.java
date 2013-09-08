package co.acjs.cricdecode;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "", formUri = "http://buggs.acjs.co/report/index.php")
public class CDCAppClass extends Application {
	public static final String	NEEDS_TO_BE_CALLED			= "to_be";
	public static final String	DOESNT_NEED_TO_BE_CALLED	= "";

	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
	}
}