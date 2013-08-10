package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.List;

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

public class DiaryMatchesFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	static DiaryMatchesFragment diaryMatchesFragment;

	// Filter Variables
	List<String> my_team_list, my_team_list_selected, opponent_list,
			opponent_list_selected, venue_list, venue_list_selected;

	private SimpleCursorAdapter dataAdapter;
	String myteam_whereClause = "", opponent_whereClause = "",
			venue_whereClause = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		diaryMatchesFragment = this;
		View rootView = inflater.inflate(R.layout.list_container, container,
				false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		displayListView(view);

		my_team_list = new ArrayList<String>();
		my_team_list_selected = new ArrayList<String>();

		opponent_list = new ArrayList<String>();
		opponent_list_selected = new ArrayList<String>();

		venue_list = new ArrayList<String>();
		venue_list_selected = new ArrayList<String>();

		fetchFromDb();
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
				R.layout.diary_match_list_item, null, columns, to, 0);

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

				PerformanceFragmentView.performanceFragmentView = new PerformanceFragmentView();
				Bundle bundle = new Bundle();
				bundle.putInt("rowId", rowId);
				bundle.putInt("innings", innings);
				PerformanceFragmentView.performanceFragmentView
						.setArguments(bundle);
				((MainActivity) getSherlockActivity()).currentFragment = MainActivity.PERFORMANCE_FRAGMENT_VIEW;

				((MainActivity) getSherlockActivity())
						.onPrepareOptionsMenu(((MainActivity) getSherlockActivity()).current_menu);

				((MainActivity) getSherlockActivity()).selectItem(
						MainActivity.PERFORMANCE_FRAGMENT_VIEW, false);
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
				CricDeCodeContentProvider.CONTENT_URI_MATCH, projection,
				MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause, null, MatchDb.KEY_MATCH_DATE
						+ " DESC");
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

	public void fetchFromDb() {

		Cursor c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_MY_TEAM + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		int count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				my_team_list.add(c.getString(0));
				my_team_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_OPPONENT_TEAM + " as _id from "
				+ MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='"
				+ MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				opponent_list.add(c.getString(0));
				opponent_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_VENUE + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				venue_list.add(c.getString(0));
				venue_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();
	}

	String buildSelectedItemString(List<String> items) {
		String[] _items = items.toArray(new String[items.size()]);
		StringBuilder sb = new StringBuilder();
		boolean foundOne = false;

		for (int i = 0; i < _items.length; ++i) {
			_items[i] = "'" + _items[i] + "'";
			if (foundOne) {
				sb.append(", ");
			}
			foundOne = true;

			sb.append(_items[i]);
		}

		return sb.toString();
	}

}
