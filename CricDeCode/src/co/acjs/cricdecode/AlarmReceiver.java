package co.acjs.cricdecode;

import java.io.IOException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.XmlResourceParser;
import co.acjs.cricdecode.WakefulIntentService.AlarmListener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AlarmReceiver extends BroadcastReceiver {
	private static final String	WAKEFUL_META_DATA	= "co.acjs.cricdecode.wakeful";

	@Override
	public void onReceive(Context ctxt, Intent intent) {
		AlarmListener listener = getListener(ctxt);

		if (listener != null) {
			if (intent.getAction() == null) {
				SharedPreferences prefs = ctxt.getSharedPreferences(
						WakefulIntentService.NAME, 0);

				prefs.edit()
						.putLong(WakefulIntentService.LAST_ALARM,
								System.currentTimeMillis()).commit();

				listener.sendWakefulWork(ctxt);
			} else {
				WakefulIntentService.scheduleAlarms(listener, ctxt, true);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private WakefulIntentService.AlarmListener getListener(Context ctxt) {
		PackageManager pm = ctxt.getPackageManager();
		ComponentName cn = new ComponentName(ctxt, getClass());

		try {
			ActivityInfo ai = pm.getReceiverInfo(cn,
					PackageManager.GET_META_DATA);
			XmlResourceParser xpp = ai.loadXmlMetaData(pm, WAKEFUL_META_DATA);

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				if (xpp.getEventType() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("WakefulIntentService")) {
						String clsName = xpp
								.getAttributeValue(null, "listener");
						Class<AlarmListener> cls = (Class<AlarmListener>) Class
								.forName(clsName);

						return (cls.newInstance());
					}
				}

				xpp.next();
			}
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Cannot find own info???", e);
		} catch (XmlPullParserException e) {
			throw new RuntimeException("Malformed metadata resource XML", e);
		} catch (IOException e) {
			throw new RuntimeException("Could not read resource XML", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Listener class not found", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"Listener is not public or lacks public constructor", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("Could not create instance of listener",
					e);
		}

		return (null);
	}
}