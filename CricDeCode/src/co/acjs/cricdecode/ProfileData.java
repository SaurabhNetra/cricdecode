package co.acjs.cricdecode;

import java.util.concurrent.Semaphore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class ProfileData {
	static SharedPreferences mPrefs;
	static SharedPreferences.Editor editor;
	static Semaphore s = new Semaphore(1, true);

	@SuppressLint("CommitPrefEdits")
	public static void init(Context ParentContext) {
		Context context = ParentContext.getApplicationContext();
		mPrefs = context.getSharedPreferences("CricDeCode",
				Context.MODE_PRIVATE);
		editor = mPrefs.edit();
	}

	public static void setProfilePicturePath(Context cont,
			String profilePicturePath) {
		try {
			s.acquire();
			init(cont);
			editor.putString("profilePicturePath", profilePicturePath);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

	public static void setNickname(Context cont, String nickname) {
		try {
			s.acquire();
			init(cont);
			editor.putString("nickname", nickname);
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

	public static void setRole(Context cont, String role) {
		try {
			s.acquire();
			init(cont);
			editor.putString("role", role);
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

}