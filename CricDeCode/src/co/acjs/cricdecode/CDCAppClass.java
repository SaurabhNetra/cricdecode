package co.acjs.cricdecode;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

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
	/* public static void init_serverdbusertable() { Kii.initialize("59cf075e",
	 * "a5266c06b907b176a208d9a9f68d9b1e", Site.JP); }
	 * 
	 * public static void init_serverdbandroiddevices() {
	 * Kii.initialize("e54bbe6d", "cbc395a0f84f1cf262bd3aeec783dd4a", Site.JP);
	 * }
	 * 
	 * public static void init_serverdbpurchases() { Kii.initialize("85b9c85c",
	 * "2c8a39b84d9023eea94ce59ccfad6686", Site.JP); }
	 * 
	 * public static void init_serverdbperformances() {
	 * Kii.initialize("999ab194", "3c96b002fad4c8c11cb600b60c0ce20a", Site.JP);
	 * }
	 */
}