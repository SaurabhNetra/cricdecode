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

	public static void setEMailID(Context cont, String eMailID) {
		try {
			s.acquire();
			init(cont);
			editor.putString("eMailID", eMailID);
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

	public static void setNickName(Context cont, String nickName) {
		try {
			s.acquire();
			init(cont);
			editor.putString("nickName", nickName);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setSex(Context cont, int sex) {
		try {
			s.acquire();
			init(cont);
			editor.putInt("sex", sex);
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
			editor.putString("DateOfBirth", dateOfBirth);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setAge(Context cont, int age) {
		try {
			s.acquire();
			init(cont);
			editor.putInt("age", age);
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

	public static void setQualification(Context cont, String qualification) {
		try {
			s.acquire();
			init(cont);
			editor.putString("qualification", qualification);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setCountry(Context cont, String country) {
		try {
			s.acquire();
			init(cont);
			editor.putString("country", country);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setState(Context cont, String state) {
		try {
			s.acquire();
			init(cont);
			editor.putString("state", state);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setClub(Context cont, String club) {
		try {
			s.acquire();
			init(cont);
			editor.putString("club", club);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setUniversity(Context cont, String university) {
		try {
			s.acquire();
			init(cont);
			editor.putString("university", university);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setCollege(Context cont, String college) {
		try {
			s.acquire();
			init(cont);
			editor.putString("college", college);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setSchool(Context cont, String school) {
		try {
			s.acquire();
			init(cont);
			editor.putString("school", school);
			editor.commit();
			s.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}