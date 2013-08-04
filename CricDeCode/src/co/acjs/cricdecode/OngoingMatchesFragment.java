package co.acjs.cricdecode;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class OngoingMatchesFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	static OngoingMatchesFragment ongoingMatchesFragment;

	private SimpleCursorAdapter dataAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ongoingMatchesFragment = this;
		View rootView = inflater.inflate(R.layout.list_container, container,
				false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		displayListView(view);
	}

	@Override
	public void onResume() {
		super.onResume();
		// Starts a new or restarts an existing Loader in this manager
		getSherlockActivity().getSupportLoaderManager().restartLoader(0, null,
				this);
	}

	private void displayListView(View view) {

		Log.d("Debug", "displayListView called");

		// The desired columns to be bound
		String[] columns = new String[] { MatchDb.KEY_ROWID,
				MatchDb.KEY_INNINGS, MatchDb.KEY_MATCH_DATE,
				MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id._id, R.id.innings, R.id.match_date,
				R.id.my_team, R.id.opponent_team };

		// create an adapter from the SimpleCursorAdapter
		dataAdapter = new SimpleCursorAdapter(getSherlockActivity(),
				R.layout.match_list_item, null, columns, to, 0);

		// get reference to the ListView
		ListView listView = (ListView) view.findViewById(R.id.content_list);

		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		// Ensures a loader is initialized and active.
		getSherlockActivity().getSupportLoaderManager().initLoader(0, null,
				this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {

				Cursor cursor = (Cursor) listView.getItemAtPosition(position);

				int rowId = cursor.getInt(cursor
						.getColumnIndexOrThrow(MatchDb.KEY_ROWID));
				int innings = cursor.getInt(cursor
						.getColumnIndexOrThrow(MatchDb.KEY_INNINGS));

				PerformanceFragment.performanceFragment = new PerformanceFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("rowId", rowId);
				bundle.putInt("innings", innings);
				PerformanceFragment.performanceFragment.setArguments(bundle);
				((MainActivity) getSherlockActivity()).currentFragment = MainActivity.PERFORMANCE_FRAGMENT;
				((MainActivity) getSherlockActivity()).selectItem(
						MainActivity.PERFORMANCE_FRAGMENT, false);
			}
		});

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d("Debug", "on Create Loader");
		String[] projection = { MatchDb.KEY_ROWID, MatchDb.KEY_INNINGS,
				MatchDb.KEY_MATCH_DATE, MatchDb.KEY_MY_TEAM,
				MatchDb.KEY_OPPONENT_TEAM };
		CursorLoader cursorLoader = new CursorLoader(getSherlockActivity(),
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

	/*
	 * public void onClick(View view) { Log.d("Debug", "onClick of Match Save");
	 * // get the row the clicked button is in LinearLayout vwParentRow =
	 * (LinearLayout) view.getParent();
	 * 
	 * TextView child = (TextView) vwParentRow.getChildAt(0); String str =
	 * child.getText().toString(); Uri uri =
	 * Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/" + str);
	 * Cursor c = getContentResolver().query(uri, new String[] {
	 * PerformanceDb.KEY_ROWID }, null, null, null); if (c.getCount() == 0) {
	 * c.close(); Toast.makeText(this, "First Save your Performance",
	 * Toast.LENGTH_SHORT).show(); } else { c.close(); uri =
	 * Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/" + str);
	 * ContentValues values = new ContentValues();
	 * values.put(MatchDb.KEY_STATUS, MatchDb.MATCH_HISTORY); // update a record
	 * getContentResolver().update(uri, values, null, null); uri =
	 * Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/update/"
	 * + str); getContentResolver().update(uri, values, null, null);
	 * Toast.makeText(this, "Match added to Career", Toast.LENGTH_LONG) .show();
	 * 
	 * finish(); startActivity(getIntent()); } }
	 */

}
