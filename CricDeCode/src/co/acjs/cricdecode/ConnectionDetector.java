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
		boolean NotAdRemoved = AccessSharedPrefs.mPrefs.getString(
				"PurchaseAdRemovalServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED);
		boolean NotInfied = AccessSharedPrefs.mPrefs.getString(
				"PurchaseInfiServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED);
		boolean NotInfiSynced = AccessSharedPrefs.mPrefs.getString(
				"PurchaseInfiSyncServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED);
		if (NotSyncedMatchHistory | NotSyncedEditProfile | NotDeleted | NotAdRemoved | NotInfied | NotInfiSynced) {
			boolean isConnected = isOnline(context);
			if (isConnected)
				Log.w("Connection Detector", "detected");
			else Log.w("Connection Detector", "no connection");

			if (NotSyncedMatchHistory & isConnected) {
				Log.w("Starting MatchCreateService", "ConnectionDetector");
				context.startService(new Intent(context,
						MatchHistorySyncService.class));
			}
			if (NotSyncedEditProfile & isConnected) {
				Log.w("Starting ProfileEditService", "ConnectionDetector");
				context.startService(new Intent(context,
						ProfileEditService.class));
			}
			if (NotDeleted & isConnected) {
				Log.w("Starting DeleteMatch", "ConnectionDetector");
				context.startService(new Intent(context,
						DeleteMatchService.class));
			}
			if (NotAdRemoved & isConnected) {
				Log.w("Starting PurchasedAdRemovalService",
						"ConnectionDetector");
				context.startService(new Intent(context,
						PurchasedAdRemovalService.class));
			}
			if (NotInfied & isConnected) {
				Log.w("Starting PurchasedInfiService", "ConnectionDetector");
				context.startService(new Intent(context,
						PurchasedInfiService.class));
			}
			if (NotInfiSynced & isConnected) {
				Log.w("Starting PurchasedInfiSyncService", "ConnectionDetector");
				context.startService(new Intent(context,
						PurchasedInfiSyncService.class));
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