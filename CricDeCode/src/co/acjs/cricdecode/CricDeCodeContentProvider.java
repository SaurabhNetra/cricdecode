package co.acjs.cricdecode;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class CricDeCodeContentProvider extends ContentProvider {

	private CricDeCodeDatabaseHelper dbHelper;

	private static final int MATCH = 1;
	private static final int SINGLE_MATCH = 2;
	private static final int SINGLE_PERFORMANCE = 3;
	private static final int PERFORMANCE = 4;

	// authority is the symbolic name of your provider
	// To avoid conflicts with other providers, you should use
	// Internet domain ownership (in reverse) as the basis of your provider
	// authority.
	static final String AUTHORITY = "co.acjs.cricdecode.contentprovider";

	// create content URIs from the authority by appending path to database
	// table
	public static final Uri CONTENT_URI_MATCH = Uri.parse("content://"
			+ AUTHORITY + "/match");
	public static final Uri CONTENT_URI_PERFORMANCE = Uri.parse("content://"
			+ AUTHORITY + "/performance");

	// a content URI pattern matches content URIs using wildcard characters:
	// *: Matches a string of any valid characters of any length.
	// #: Matches a string of numeric characters of any length.
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "match", MATCH);
		uriMatcher.addURI(AUTHORITY, "match/#", SINGLE_MATCH);
		uriMatcher.addURI(AUTHORITY, "performance", PERFORMANCE);
		uriMatcher.addURI(AUTHORITY, "performance/#", SINGLE_PERFORMANCE);
	}

	// system calls onCreate() when it starts up the provider.
	@Override
	public boolean onCreate() {
		// get access to the database helper
		dbHelper = new CricDeCodeDatabaseHelper(getContext());
		Log.d("Debug", "Content Provider Created");
		return false;
	}

	// Return the MIME type corresponding to a content URI
	@Override
	public String getType(Uri uri) {

		return null;
	}

	// The insert() method adds a new row to the appropriate table, using the
	// values
	// in the ContentValues argument. If a column name is not in the
	// ContentValues argument,
	// you may want to provide a default value for it either in your provider
	// code or in
	// your database schema.
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String table;
		Uri content_uri;
		// Log.d("Debug", "Insert Uri " + uriMatcher.match(uri));
		switch (uriMatcher.match(uri)) {
		case MATCH:
			table = MatchDb.SQLITE_TABLE;
			content_uri = CONTENT_URI_MATCH;
			break;
		case PERFORMANCE:
			table = PerformanceDb.SQLITE_TABLE;
			content_uri = CONTENT_URI_PERFORMANCE;
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		long id = db.insert(table, null, values);
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(content_uri + "/" + id);

	}

	// The query() method must return a Cursor object, or if it fails,
	// throw an Exception. If you are using an SQLite database as your data
	// storage,
	// you can simply return the Cursor returned by one of the query() methods
	// of the
	// SQLiteDatabase class. If the query does not match any rows, you should
	// return a
	// Cursor instance whose getCount() method returns 0. You should return null
	// only
	// if an internal error occurred during the query process.
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String id;
		switch (uriMatcher.match(uri)) {
		case MATCH:
			queryBuilder.setTables(MatchDb.SQLITE_TABLE);

			break;
		case SINGLE_MATCH:
			queryBuilder.setTables(MatchDb.SQLITE_TABLE);
			id = uri.getPathSegments().get(1);
			selection = MatchDb.KEY_ROWID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			break;
		case SINGLE_PERFORMANCE:
			Log.d("Debug", "query Single Performance");
			queryBuilder.setTables(PerformanceDb.SQLITE_TABLE);
			id = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(PerformanceDb.KEY_MATCHID + "=" + id);
			Log.d("Debug", "query Single Performance");
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);

		return cursor;

	}

	// The delete() method deletes rows based on the seletion or if an id is
	// provided then it deleted a single row. The methods returns the numbers
	// of records delete from the database. If you choose not to delete the data
	// physically then just update a flag here.
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String table_name, id;
		int deleteCount;
		switch (uriMatcher.match(uri)) {
		case SINGLE_MATCH:
			table_name = MatchDb.SQLITE_TABLE;
			id = uri.getPathSegments().get(1);
			selection = MatchDb.KEY_ROWID + "=" + id;
			break;
		case SINGLE_PERFORMANCE:
			table_name = PerformanceDb.SQLITE_TABLE;
			id = uri.getPathSegments().get(1);
			selection = PerformanceDb.KEY_MATCHID + "=" + id;
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		deleteCount = db.delete(table_name, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return deleteCount;
	}

	// The update method() is same as delete() which updates multiple rows
	// based on the selection or a single row if the row id is provided. The
	// update method returns the number of updated rows.
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String table_name, id;

		switch (uriMatcher.match(uri)) {
		case SINGLE_MATCH:
			table_name = MatchDb.SQLITE_TABLE;
			id = uri.getPathSegments().get(1);
			selection = MatchDb.KEY_ROWID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			break;
		case SINGLE_PERFORMANCE:
			table_name = PerformanceDb.SQLITE_TABLE;
			id = uri.getPathSegments().get(1);
			selection = PerformanceDb.KEY_MATCHID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			if (values.containsKey(PerformanceDb.KEY_INNING)) {
				selection = PerformanceDb.KEY_INNING
						+ "="
						+ values.getAsInteger(PerformanceDb.KEY_INNING)
								.intValue()
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
								+ ')' : "");
			}
			break;

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		int updateCount = db.update(table_name, values, selection,
				selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return updateCount;

	}

	public CricDeCodeDatabaseHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(CricDeCodeDatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

}
