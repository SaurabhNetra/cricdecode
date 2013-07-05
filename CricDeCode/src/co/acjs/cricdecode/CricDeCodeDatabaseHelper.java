package co.acjs.cricdecode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CricDeCodeDatabaseHelper extends SQLiteOpenHelper {

	private static final String	DATABASE_NAME		= "cricDeCode";
	private static final int	DATABASE_VERSION	= 1;

	CricDeCodeDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		MatchDb.onCreate(db);
		PerformanceDb.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		MatchDb.onUpgrade(db, oldVersion, newVersion);
		PerformanceDb.onUpgrade(db, oldVersion, newVersion);
	}

}
