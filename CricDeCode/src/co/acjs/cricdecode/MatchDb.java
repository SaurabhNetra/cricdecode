package co.acjs.cricdecode;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class MatchDb{
	public static final String	MATCH_CURRENT		= "current";
	public static final String	MATCH_HISTORY		= "history";
	public static final String	MATCH_DELETED		= "deleted";
	public static final String	MATCH_HOLD			= "hold";
	public static final String	KEY_ROWID			= "_id";
	public static final String	KEY_DEVICE_ID		= "device_id";
	public static final String	KEY_MATCH_DATE		= "match_date";
	public static final String	KEY_MY_TEAM			= "my_team";
	public static final String	KEY_OPPONENT_TEAM	= "opponent_team";
	public static final String	KEY_VENUE			= "venue";
	public static final String	KEY_OVERS			= "overs";
	public static final String	KEY_INNINGS			= "innings";
	public static final String	KEY_RESULT			= "result";
	public static final String	KEY_LEVEL			= "level";
	public static final String	KEY_FIRST_ACTION	= "first_action";
	public static final String	KEY_DURATION		= "duration";
	public static final String	KEY_REVIEW			= "review";
	public static final String	KEY_STATUS			= "status";
	public static final String	KEY_SYNCED			= "synced";
	private static final String	LOG_TAG				= "MatchDb";
	public static final String	SQLITE_TABLE		= "match";
	private static final String	DATABASE_CREATE		= "CREATE TABLE if not exists " + SQLITE_TABLE + " (" + KEY_ROWID + " integer," + KEY_DEVICE_ID + "," + KEY_MATCH_DATE + " date," + KEY_MY_TEAM + "," + KEY_OPPONENT_TEAM + "," + KEY_VENUE + "," + KEY_OVERS + " integer, " + KEY_INNINGS + " integer, " + KEY_RESULT + "," + KEY_REVIEW + " text," + KEY_LEVEL + "," + KEY_FIRST_ACTION + "," + KEY_DURATION + "," + KEY_STATUS + "," + KEY_SYNCED + " integer);";

	public static void onCreate(SQLiteDatabase db){
		Log.w(LOG_TAG, DATABASE_CREATE);
		db.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
		onCreate(db);
	}
}
