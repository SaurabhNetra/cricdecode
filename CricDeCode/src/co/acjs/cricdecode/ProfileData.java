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

	public static void setFName(Context cont, String fname) {
		try {
			s.acquire();
			init(cont);
			editor.putString("f_name", fname);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void setDOB(Context cont, String DOB) {
		try {
			s.acquire();
			init(cont);
			editor.putString("dob", DOB);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void setLName(Context cont, String lname) {
		try {
			s.acquire();
			init(cont);
			editor.putString("l_name", lname);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setFBLink(Context cont, String link) {
		try {
			s.acquire();
			init(cont);
			editor.putString("fb_link", link);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void setID(Context cont, String id) {
		try {
			s.acquire();
			init(cont);
			editor.putString("id", id);
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