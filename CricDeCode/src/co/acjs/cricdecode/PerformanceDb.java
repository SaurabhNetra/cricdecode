package co.acjs.cricdecode;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PerformanceDb {

	public static final String CURRENT = "current";

	public static final String KEY_ROWID = "_id";
	public static final String KEY_DEVICE_ID = "device_id";
	public static final String KEY_MATCHID = "match_id";
	public static final String KEY_INNING = "inning";

	public static final String KEY_BAT_NUM = "bat_num";
	public static final String KEY_BAT_RUNS = "bat_runs";
	public static final String KEY_BAT_BALLS = "bat_balls";
	public static final String KEY_BAT_TIME = "bat_time";
	public static final String KEY_BAT_FOURS = "fours";
	public static final String KEY_BAT_SIXES = "sixes";
	public static final String KEY_BAT_HOW_OUT = "bat_how_out";
	public static final String KEY_BAT_BOWLER_TYPE = "bat_bowler_type";
	public static final String KEY_BAT_FIELDING_POSITION = "bat_fielding_position";
	public static final String KEY_BAT_CHANCES = "bat_chances";

	public static final String KEY_BOWL_BALLS = "bowl_balls";
	public static final String KEY_BOWL_SPELLS = "bowl_spells";
	public static final String KEY_BOWL_MAIDENS = "bowl_maidens";
	public static final String KEY_BOWL_RUNS = "bowl_runs";
	public static final String KEY_BOWL_FOURS = "bowl_fours";
	public static final String KEY_BOWL_SIXES = "bowl_sixes";
	public static final String KEY_BOWL_WKTS_LEFT = "bowl_wkts_left";
	public static final String KEY_BOWL_WKTS_RIGHT = "bowl_wkts_right";
	public static final String KEY_BOWL_CATCHES_DROPPED = "bowl_catches_dropped";
	public static final String KEY_BOWL_NOBALLS = "bowl_noballs";
	public static final String KEY_BOWL_WIDES = "bowl_wides";

	public static final String KEY_FIELD_SLIP_CATCH = "field_slip_catch";
	public static final String KEY_FIELD_CLOSE_CATCH = "field_close_catch";
	public static final String KEY_FIELD_CIRCLE_CATCH = "field_circle_catch";
	public static final String KEY_FIELD_DEEP_CATCH = "field_deep_catch";
	public static final String KEY_FIELD_RO_CIRCLE = "field_ro_circle";
	public static final String KEY_FIELD_RO_DIRECT_CIRCLE = "field_ro_direct_cirlce";
	public static final String KEY_FIELD_RO_DEEP = "field_ro_deep";
	public static final String KEY_FIELD_RO_DIRECT_DEEP = "field_ro_direct_deep";
	public static final String KEY_FIELD_STUMPINGS = "field_stumpings";
	public static final String KEY_FIELD_BYES = "field_byes";
	public static final String KEY_FIELD_MISFIELDS = "field_misfield";
	public static final String KEY_FIELD_CATCHES_DROPPED = "field_catches_dropped";

	public static final String KEY_STATUS = "status";
	public static final String KEY_SYNCED = "synced";

	private static final String LOG_TAG = "ProfileDb";
	public static final String SQLITE_TABLE = "performance";

	private static final String DATABASE_CREATE = "CREATE TABLE if not exists "
			+ SQLITE_TABLE + " (" + KEY_ROWID + " integer," + KEY_DEVICE_ID
			+ "," + KEY_MATCHID + " integer," + KEY_INNING + " integer,"
			+ KEY_BAT_NUM + " integer," + KEY_BAT_RUNS + " integer,"
			+ KEY_BAT_BALLS + " integer," + KEY_BAT_TIME + " integer,"
			+ KEY_BAT_FOURS + " integer," + KEY_BAT_SIXES + " integer,"
			+ KEY_BAT_HOW_OUT + ", " + KEY_BAT_BOWLER_TYPE + ", "
			+ KEY_BAT_FIELDING_POSITION + ", " + KEY_BAT_CHANCES + " integer, "
			+ KEY_BOWL_BALLS + " integer," + KEY_BOWL_SPELLS + " integer,"
			+ KEY_BOWL_MAIDENS + " integer," + KEY_BOWL_RUNS + " integer,"
			+ KEY_BOWL_FOURS + " integer," + KEY_BOWL_SIXES + " integer,"
			+ KEY_BOWL_WKTS_LEFT + " integer," + KEY_BOWL_WKTS_RIGHT
			+ " integer," + KEY_BOWL_CATCHES_DROPPED + " integer,"
			+ KEY_BOWL_NOBALLS + " integer," + KEY_BOWL_WIDES + " integer,"
			+ KEY_FIELD_SLIP_CATCH + " integer," + KEY_FIELD_CLOSE_CATCH
			+ " integer," + KEY_FIELD_CIRCLE_CATCH + " integer,"
			+ KEY_FIELD_DEEP_CATCH + " integer," + KEY_FIELD_RO_CIRCLE
			+ " integer," + KEY_FIELD_RO_DIRECT_CIRCLE + " integer,"
			+ KEY_FIELD_RO_DEEP + " integer," + KEY_FIELD_RO_DIRECT_DEEP
			+ " integer," + KEY_FIELD_STUMPINGS + " integer," + KEY_FIELD_BYES
			+ " integer," + KEY_FIELD_MISFIELDS + " integer,"
			+ KEY_FIELD_CATCHES_DROPPED + " integer," + KEY_STATUS + ","
			+ KEY_SYNCED + " integer);";

	public static void onCreate(SQLiteDatabase db) {
		Log.w(LOG_TAG, DATABASE_CREATE);
		db.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
		onCreate(db);
	}
}
