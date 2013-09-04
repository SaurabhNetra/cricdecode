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

	public static void setEmail(Context cont, String em) {
		try {
			s.acquire();
			init(cont);
			editor.putString("email", em);
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

	public static void setScr_Width(Context cont, int w) {
		try {
			s.acquire();
			init(cont);
			editor.putInt("width", w);
			editor.commit();
			s.release();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setScr_Height(Context cont, int h) {
		try {
			s.acquire();
			init(cont);
			editor.putInt("height", h);
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

	public static void setFirstTym(Context cont, int x) {
		try {
			s.acquire();
			init(cont);
			editor.putInt("FirstTym", x);
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

	public static void setOngoingMatches(Context cont, int ongoingMatches) {
		try {
			s.acquire();
			init(cont);
			editor.putInt("ongoingMatches", ongoingMatches);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setDiaryMatches(Context cont, int diaryMatches) {
		try {
			s.acquire();
			init(cont);
			editor.putInt("diaryMatches", diaryMatches);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}