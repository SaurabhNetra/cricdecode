package co.acjs.cricdecode;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {
	
	@Override
	protected void onError(Context arg0, String arg1) {

	}

	@Override
	protected String[] getSenderIds(Context context) {
	
		return super.getSenderIds(context);

	}

	@Override
	protected void onMessage(Context arg0, Intent intent) {
	

	}

	@Override
	protected void onRegistered(Context ctx, String regId) {
		Log.w(GCMRegistrar.getRegistrationId(getApplicationContext()),
				"Registration id");
		if (GCMRegistrar.getRegistrationId(getApplicationContext()) != null) {
			Intent intent = new Intent(this, SignInService.class);
			startService(intent);
		} else {
			((MainActivity) MainActivity.main_context).runOnUiThread(new Runnable() {
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.main_context);
					builder.setIcon(android.R.drawable.ic_dialog_alert)
							.setTitle("Cannot Open Appliction!")
							.setMessage(
									"Google Server Busy. Please try again in some time.")
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											((MainActivity) MainActivity.main_context)
													.finish();
										}

									}).show();
				}
			});
		}

	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {

	}

	

}