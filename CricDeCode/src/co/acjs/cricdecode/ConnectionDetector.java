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
		AccessSharedPrefs.mPrefs = context.getApplicationContext()
				.getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		boolean NotSignedIn = AccessSharedPrefs.mPrefs.getString(
				"SignInServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED)
				.equals(CDCAppClass.NEEDS_TO_BE_CALLED);
		boolean NotSyncedEditProfile = AccessSharedPrefs.mPrefs.getString(
						"ProfileEditServiceCalled", CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(CDCAppClass.NEEDS_TO_BE_CALLED);
		if (NotSyncedEditProfile|NotSignedIn) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager
					.getActiveNetworkInfo();
			NetworkInfo mobNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			boolean isConnected = false;
			if ((activeNetInfo != null) | (mobNetInfo != null)) {
				Log.w("connection detected", "ConnectionDetector");
				isConnected = true;
			}
			if(NotSyncedEditProfile & isConnected){
				Log.w("Starting ProfileEditService", "ConnectionDetector");
				Intent in = new Intent(context, ProfileEditService.class);
				context.startService(in);
			}
			else if (NotSignedIn & isConnected) {
				Log.w("Starting SignInService", "ConnectionDetector");
				Intent in = new Intent(context, SignInService.class);
				context.startService(in);
			}
		}
	}
}