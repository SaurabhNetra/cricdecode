package co.acjs.cricdecode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionDetector extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		NetworkInfo mobNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isConnected = false;
		if (activeNetInfo != null) {
			isConnected = true;
			Log.w("connection detected", "ConnectionDetector");
		}
		if (mobNetInfo != null) {
			Log.w("connection detected", "ConnectionDetector");
			isConnected = true;
		}

		AccessSharedPrefs.mPrefs = context.getApplicationContext()
				.getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);

		if (!AccessSharedPrefs.mPrefs.getString("id", "").equals("") && isConnected) {

			Log.w("Starting Service ", "ConnectionDetector");
			Intent in = new Intent(context, SignInService.class);
			context.startService(in);

		} else {

		}
	}
}