package co.acjs.cricdecode;

import java.util.concurrent.Semaphore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class AccessSharedPrefs {
	static SharedPreferences		mPrefs;
	static SharedPreferences.Editor	editor;
	static Semaphore				s	= new Semaphore(1, true);

	@SuppressLint("CommitPrefEdits")
	public static void init(Context ParentContext) {
		Context context = ParentContext.getApplicationContext();
		mPrefs = context.getSharedPreferences("CricDeCode",
				Context.MODE_PRIVATE);
		editor = mPrefs.edit();
	}

	public static void setString(Context cont, String key, String value) {
		try {
			s.acquire();
			init(cont);
			editor.putString(key, value);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setInt(Context cont, String key, int value) {
		try {
			s.acquire();
			init(cont);
			editor.putInt(key, value);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}