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
				"ProfileEditServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED);
		boolean NotSyncedMatchHistory = AccessSharedPrefs.mPrefs.getString(
				"MatchHistorySyncServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED);
		boolean NotDeleted = AccessSharedPrefs.mPrefs.getString(
				"DeleteMatchServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED);
		if (NotSyncedMatchHistory | NotSyncedEditProfile | NotSignedIn) {
			boolean isConnected = isOnline(context);
			if (isConnected)
				Log.w("Connection Detector", "detected");
			else
				Log.w("Connection Detector", "no connection");

			if (NotSyncedMatchHistory & isConnected) {
				Log.w("Starting MatchCreateService", "ConnectionDetector");
				Intent in = new Intent(context, MatchHistorySyncService.class);
				context.startService(in);
			} else if (NotSyncedEditProfile & isConnected) {
				Log.w("Starting ProfileEditService", "ConnectionDetector");
				Intent in = new Intent(context, ProfileEditService.class);
				context.startService(in);
			} else if (NotSignedIn & isConnected) {
				Log.w("Starting SignInService", "ConnectionDetector");
				Intent in = new Intent(context, SignInService.class);
				context.startService(in);
			} else if (NotDeleted & isConnected) {
				Log.w("Starting DeleteMatch", "ConnectionDetector");
				Intent in = new Intent(context, DeleteMatchService.class);
				context.startService(in);
			}
		}
	}

	boolean isOnline(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}