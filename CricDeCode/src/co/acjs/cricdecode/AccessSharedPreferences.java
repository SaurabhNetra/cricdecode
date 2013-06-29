package co.acjs.cricdecode;

import java.util.concurrent.Semaphore;

import android.content.Context;
import android.content.SharedPreferences;

public class AccessSharedPreferences {
	public static SharedPreferences			mPrefs;
	public static SharedPreferences.Editor	editor;
	static Semaphore						s	= new Semaphore(1, true);

	public static void init(Context ParentContext) {
		Context context = ParentContext.getApplicationContext();
		mPrefs = context.getSharedPreferences("CrecDeCode", Context.MODE_PRIVATE);
		editor = mPrefs.edit();
	}
	public static void setUserID(Context cont, String userID) {
		try {
			s.acquire();
			init(cont);
			editor.putString("userID", userID);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}}