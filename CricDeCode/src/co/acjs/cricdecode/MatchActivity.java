package co.acjs.cricdecode;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class MatchActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter	dataAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Goal Setting Screen

		setContentView(R.layout.activity_match_list);

		displayListView();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Starts a new or restarts an existing Loader in this manager
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	private void displayListView() {

		Log.d("Debug", "displayListView called");

		// The desired columns to be bound
		String[] columns = new String[] { MatchDb.KEY_ROWID, MatchDb.KEY_INNINGS, MatchDb.KEY_MATCH_DATE, MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.lblMatchId, R.id.lblInnings, R.id.lblMatchDate, R.id.lblMyTeam, R.id.lblOpponentTeam };

		// create an adapter from the SimpleCursorAdapter
		dataAdapter = new SimpleCursorAdapter(this, R.layout.element_match,
				null, columns, to, 0);

		// get reference to the ListView
		ListView listView = (ListView) findViewById(R.id.matchList);

		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		// Ensures a loader is initialized and active.
		getSupportLoaderManager().initLoader(0, null, this);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
				Intent intent = new Intent(getBaseContext(),
						PerformanceInsertActivity.class);
				Cursor cursor = (Cursor) listView.getItemAtPosition(position);

				int rowId = cursor.getInt(cursor
						.getColumnIndexOrThrow(MatchDb.KEY_ROWID));
				int innings = cursor.getInt(cursor
						.getColumnIndexOrThrow(MatchDb.KEY_INNINGS));
				intent.putExtra("rowId", rowId);
				intent.putExtra("innings", innings);
				startActivity(intent);
			}
		});

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d("Debug", "on Create Loader");
		String[] projection = { MatchDb.KEY_ROWID, MatchDb.KEY_INNINGS, MatchDb.KEY_MATCH_DATE, MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM };
		CursorLoader cursorLoader = new CursorLoader(this,
				CricDeCodeContentProvider.CONTENT_URI_MATCH, projection, null,
				null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		Log.d("Debug", "on Load Finished");
		dataAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		Log.d("Debug", "on Loader Reset");
		dataAdapter.swapCursor(null);
	}

	public void onClick(View view) {
		Log.d("Debug", "onClick of Match Save");
		// get the row the clicked button is in
		LinearLayout vwParentRow = (LinearLayout) view.getParent();

		TextView child = (TextView) vwParentRow.getChildAt(0);
		String str = child.getText().toString();
		Uri uri = Uri
				.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/" + str);
		Cursor c = getContentResolver().query(uri,
				new String[] { PerformanceDb.KEY_ROWID }, null, null, null);
		if (c.getCount() == 0) {
			c.close();
			Toast.makeText(this, "First Save your Performance",
					Toast.LENGTH_SHORT).show();
		} else {
			c.close();
			uri = Uri
					.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/" + str);
			ContentValues values = new ContentValues();
			values.put(MatchDb.KEY_STATUS, MatchDb.MATCH_HISTORY);
			// update a record
			getContentResolver().update(uri, values, null, null);
			uri = Uri
					.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/update/" + str);
			getContentResolver().update(uri, values, null, null);
			Toast.makeText(this, "Match Saved", Toast.LENGTH_LONG).show();
			finish();
			startActivity(getIntent());
		}
	}

}
