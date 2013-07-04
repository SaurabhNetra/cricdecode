package co.acjs.cricdecode;

import java.util.concurrent.Semaphore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class AccessSharedPreferences {
	public static SharedPreferences			mPrefs;
	public static SharedPreferences.Editor	editor;
	static Semaphore						s	= new Semaphore(1, true);

	@SuppressLint("CommitPrefEdits")
	public static void init(Context ParentContext) {
		Context context = ParentContext.getApplicationContext();
		mPrefs = context.getSharedPreferences("CricDeCode",
				Context.MODE_PRIVATE);
		editor = mPrefs.edit();
	}

	public static void setName(Context cont, String name) {
		try {
			s.acquire();
			init(cont);
			editor.putString("name", name);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setNickName(Context cont, String nickName) {
		try {
			s.acquire();
			init(cont);
			editor.putString("nickname", nickName);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setDateOfBirth(Context cont, String dateOfBirth) {
		try {
			s.acquire();
			init(cont);
			editor.putString("dateOfBirth", dateOfBirth);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setPlayingRole(Context cont, String playingRole) {
		try {
			s.acquire();
			init(cont);
			editor.putString("playingRole", playingRole);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setBattingStyle(Context cont, String battingStyle) {
		try {
			s.acquire();
			init(cont);
			editor.putString("battingStyle", battingStyle);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setBowlingStyle(Context cont, String bowlingStyle) {
		try {
			s.acquire();
			init(cont);
			editor.putString("bowlingStyle", bowlingStyle);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setProfilePicPath(Context cont, String profilePicPath) {
		try {
			s.acquire();
			init(cont);
			editor.putString("profilePicPath", profilePicPath);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}