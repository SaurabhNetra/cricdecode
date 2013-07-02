package co.acjs.cricdecode;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MatchDb {

	public static final String	CURRENT				= "current";

	public static final String	KEY_ROWID			= "_id";
	public static final String	KEY_MATCH_DATE		= "match_date";
	public static final String	KEY_MY_TEAM			= "my_team";
	public static final String	KEY_OPPONENT_TEAM	= "opponent_team";
	public static final String	KEY_VENUE			= "venue";
	public static final String	KEY_OVERS			= "overs";
	public static final String	KEY_INNINGS			= "innings";
	public static final String	KEY_RESULTS			= "results";
	public static final String	KEY_STATUS			= "status";

	private static final String	LOG_TAG				= "MatchDb";
	public static final String	SQLITE_TABLE		= "match";

	private static final String	DATABASE_CREATE		= "CREATE TABLE if not exists " + SQLITE_TABLE + " (" + KEY_ROWID + " integer PRIMARY KEY autoincrement," + KEY_MATCH_DATE + "," + KEY_MY_TEAM + "," + KEY_OPPONENT_TEAM + "," + KEY_VENUE + "," + KEY_OVERS + " integer, " + KEY_INNINGS + " integer, " + KEY_RESULTS + "," + KEY_STATUS + ");";

	public static void onCreate(SQLiteDatabase db) {
		Log.w(LOG_TAG, DATABASE_CREATE);
		db.execSQL(DATABASE_CREATE);
		// Test Inserts
		// db.execSQL("insert into match values(1,'12/12/12','abc','def','lords',10,1,'Win','current')");
		// db.execSQL("insert into match values(2,'12/12/13','abcd','defg','eden',20,1,'Win','history')");
		// db.execSQL("insert into match values(3,'12/12/12','abc','def','lords',10,1,'Win','current')");
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(LOG_TAG,
				"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
		onCreate(db);
	}
}
