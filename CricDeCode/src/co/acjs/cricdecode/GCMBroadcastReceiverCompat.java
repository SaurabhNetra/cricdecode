package co.acjs.cricdecode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import co.acjs.cricdecode.WakefulIntentService;

public class GCMBroadcastReceiverCompat extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		intent.setClass(context, GCMIntentService.class);
		WakefulIntentService.sendWakefulWork(context, intent);
	}
}