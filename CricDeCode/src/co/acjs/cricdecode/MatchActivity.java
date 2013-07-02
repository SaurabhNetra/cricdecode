package co.acjs.cricdecode;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MatchActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter	dataAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Goal Setting Screen

		setContentView(R.layout.activity_match_list);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.actionbar_match_add);

		displayListView();
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
		String[] columns = new String[] { MatchDb.KEY_MATCH_DATE, MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.lblMatchDate, R.id.lblMyTeam, R.id.lblOpponentTeam };

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
				Intent intent = new Intent(MainActivity.mainAct,
						PerformanceInsertActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d("Debug", "on Create Loader");
		String[] projection = { MatchDb.KEY_ROWID, MatchDb.KEY_MATCH_DATE, MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM };
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
		switch (view.getId()) {
			case R.id.btnNewMatch:
				Intent intent = new Intent(this, MatchCreateActivity.class);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

}
