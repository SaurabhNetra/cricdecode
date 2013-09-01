package co.acjs.cricdecode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class DiaryMatchesFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	static DiaryMatchesFragment diaryMatchesFragment;
	RelativeLayout no_matches;
	ListView listView;

	// Filter Variables
	ArrayList<String> my_team_list, my_team_list_selected, opponent_list,
			opponent_list_selected, venue_list, venue_list_selected,
			overs_list, overs_list_selected, innings_list,
			innings_list_selected, level_list, level_list_selected,
			duration_list, duration_list_selected, first_list,
			first_list_selected, season_list, season_list_selected,
			result_list, result_list_selected;

	private SimpleCursorAdapter dataAdapter;
	String myteam_whereClause = "", opponent_whereClause = "",
			venue_whereClause = "", overs_whereClause = "",
			innings_whereClause = "", level_whereClause = "",
			duration_whereClause = "", first_whereClause = "",
			season_whereClause = "", result_whereClause = "";

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
		if (savedInstanceState == null) {

			season_list = new ArrayList<String>();
			season_list_selected = new ArrayList<String>();

			my_team_list = new ArrayList<String>();
			my_team_list_selected = new ArrayList<String>();

			opponent_list = new ArrayList<String>();
			opponent_list_selected = new ArrayList<String>();

			venue_list = new ArrayList<String>();
			venue_list_selected = new ArrayList<String>();

			result_list = new ArrayList<String>();
			result_list_selected = new ArrayList<String>();

			level_list = new ArrayList<String>();
			level_list_selected = new ArrayList<String>();

			overs_list = new ArrayList<String>();
			overs_list_selected = new ArrayList<String>();

			innings_list = new ArrayList<String>();
			innings_list_selected = new ArrayList<String>();

			duration_list = new ArrayList<String>();
			duration_list_selected = new ArrayList<String>();

			first_list = new ArrayList<String>();
			first_list_selected = new ArrayList<String>();

			fetchFromDb();
		} else {

			season_list = savedInstanceState.getStringArrayList("season_list");
			season_list_selected = savedInstanceState
					.getStringArrayList("season_list_selected");

			my_team_list = savedInstanceState
					.getStringArrayList("my_team_list");
			my_team_list_selected = savedInstanceState
					.getStringArrayList("my_team_list_selected");

			opponent_list = savedInstanceState
					.getStringArrayList("opponent_list");
			opponent_list_selected = savedInstanceState
					.getStringArrayList("opponent_list_selected");

			venue_list = savedInstanceState.getStringArrayList("venue_list");
			venue_list_selected = savedInstanceState
					.getStringArrayList("venue_list_selected");

			result_list = savedInstanceState.getStringArrayList("result_list");
			result_list_selected = savedInstanceState
					.getStringArrayList("result_list_selected");

			level_list = savedInstanceState.getStringArrayList("level_list");
			level_list_selected = savedInstanceState
					.getStringArrayList("level_list_selected");

			overs_list = savedInstanceState.getStringArrayList("overs_list");
			overs_list_selected = savedInstanceState
					.getStringArrayList("overs_list_selected");

			innings_list = savedInstanceState
					.getStringArrayList("innings_list");
			innings_list_selected = savedInstanceState
					.getStringArrayList("innings_list_selected");

			duration_list = savedInstanceState
					.getStringArrayList("duration_list");
			duration_list_selected = savedInstanceState
					.getStringArrayList("duration_list_selected");

			first_list = savedInstanceState.getStringArrayList("first_list");
			first_list_selected = savedInstanceState
					.getStringArrayList("first_list_selected");

			myteam_whereClause = savedInstanceState
					.getString("myteam_whereClause");
			opponent_whereClause = savedInstanceState
					.getString("opponent_whereClause");
			venue_whereClause = savedInstanceState
					.getString("venue_whereClause");
			level_whereClause = savedInstanceState
					.getString("level_whereClause");
			overs_whereClause = savedInstanceState
					.getString("overs_whereClause");
			innings_whereClause = savedInstanceState
					.getString("innings_whereClause");
			duration_whereClause = savedInstanceState
					.getString("duration_whereClause");
			first_whereClause = savedInstanceState
					.getString("first_whereClause");
			season_whereClause = savedInstanceState
					.getString("season_whereClause");
			result_whereClause = savedInstanceState
					.getString("result_whereClause");

			getSherlockActivity().getSupportLoaderManager().restartLoader(0,
					null, this);

		}
		displayListView(view);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putStringArrayList("season_list",
				(ArrayList<String>) season_list);
		outState.putStringArrayList("result_list",
				(ArrayList<String>) result_list);
		outState.putStringArrayList("my_team_list",
				(ArrayList<String>) my_team_list);
		outState.putStringArrayList("opponent_list",
				(ArrayList<String>) opponent_list);
		outState.putStringArrayList("venue_list",
				(ArrayList<String>) venue_list);
		outState.putStringArrayList("level_list",
				(ArrayList<String>) level_list);
		outState.putStringArrayList("overs_list",
				(ArrayList<String>) overs_list);
		outState.putStringArrayList("innings_list",
				(ArrayList<String>) innings_list);
		outState.putStringArrayList("duration_list",
				(ArrayList<String>) duration_list);
		outState.putStringArrayList("first_list",
				(ArrayList<String>) first_list);
		outState.putStringArrayList("season_list_selected",
				(ArrayList<String>) season_list_selected);
		outState.putStringArrayList("result_list_selected",
				(ArrayList<String>) result_list_selected);
		outState.putStringArrayList("my_team_list_selected",
				(ArrayList<String>) my_team_list_selected);
		outState.putStringArrayList("opponent_list_selected",
				(ArrayList<String>) opponent_list_selected);
		outState.putStringArrayList("venue_list_selected",
				(ArrayList<String>) venue_list_selected);
		outState.putStringArrayList("level_list_selected",
				(ArrayList<String>) level_list_selected);
		outState.putStringArrayList("overs_list_selected",
				(ArrayList<String>) overs_list_selected);
		outState.putStringArrayList("innings_list_selected",
				(ArrayList<String>) innings_list_selected);
		outState.putStringArrayList("duration_list_selected",
				(ArrayList<String>) duration_list_selected);
		outState.putStringArrayList("first_list_selected",
				(ArrayList<String>) first_list_selected);
		outState.putString("myteam_whereClause", myteam_whereClause);
		outState.putString("opponent_whereClause", opponent_whereClause);
		outState.putString("venue_whereClause", venue_whereClause);
		outState.putString("level_whereClause", level_whereClause);
		outState.putString("overs_whereClause", overs_whereClause);
		outState.putString("innings_whereClause", innings_whereClause);
		outState.putString("duration_whereClause", duration_whereClause);
		outState.putString("first_whereClause", first_whereClause);
		outState.putString("season_whereClause", season_whereClause);
		outState.putString("result_whereClause", result_whereClause);
		super.onSaveInstanceState(outState);
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
				MatchDb.KEY_DURATION, MatchDb.KEY_FIRST_ACTION,
				MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM,
				MatchDb.KEY_VENUE, MatchDb.KEY_LEVEL, MatchDb.KEY_OVERS };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id._id, R.id.innings, R.id.day, R.id.month,
				R.id.year, R.id.my_team, R.id.opponent_team, R.id.venue,
				R.id.level, R.id.overs };

		// create an adapter from the SimpleCursorAdapter
		dataAdapter = new SimpleCursorAdapter(getSherlockActivity(),
				R.layout.diary_match_list_item, null, columns, to, 0);

		// get reference to the ListView
		listView = (ListView) view.findViewById(R.id.content_list);
		no_matches = (RelativeLayout) view.findViewById(R.id.no_matches);
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
				((MainActivity) getSherlockActivity()).preFragment = MainActivity.DIARY_MATCHES_FRAGMENT;
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
				MatchDb.KEY_MATCH_DATE, MatchDb.KEY_DURATION,
				MatchDb.KEY_FIRST_ACTION, MatchDb.KEY_MY_TEAM,
				MatchDb.KEY_OPPONENT_TEAM, MatchDb.KEY_VENUE,
				MatchDb.KEY_LEVEL, MatchDb.KEY_OVERS };
		CursorLoader cursorLoader = new CursorLoader(getSherlockActivity(),
				CricDeCodeContentProvider.CONTENT_URI_MATCH, projection,
				MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause, null,
				MatchDb.KEY_MATCH_DATE + " DESC");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		Log.d("Debug", "on Load Finished");
		MatrixCursor mc = new MatrixCursor(data.getColumnNames(),
				data.getCount());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Date date = new Date();
		if (data.getCount() != 0) {
			data.moveToFirst();
			do {
				String[] values = new String[data.getColumnCount()];
				for (int i = 0; i < data.getColumnCount(); i++) {
					if (i == data.getColumnIndex(MatchDb.KEY_MATCH_DATE)) {
						try {
							date = sdf.parse(data.getString(i));
							Calendar cal = new GregorianCalendar();
							cal.setTime(date);
							values[i++] = cal.get(Calendar.DAY_OF_MONTH) + "";
							values[i++] = PerformanceFragmentView.month_str[cal
									.get(Calendar.MONTH)];
							values[i] = cal.get(Calendar.YEAR) + "";
						} catch (ParseException e) {
							e.printStackTrace();
							Log.d("Debug", "Date Exception");
						}
					} else {
						values[i] = data.getString(i);
						if (i == data.getColumnIndexOrThrow(MatchDb.KEY_OVERS)
								&& values[i].equals("-1")) {
							values[i] = "Unlimited";
						}
					}
				}
				mc.addRow(values);
				data.moveToNext();
			} while (!data.isAfterLast());
			dataAdapter.swapCursor(mc);
		} else {
			dataAdapter.swapCursor(data);
		}
		Log.d("Debug", "List Count " + listView.getCount());
		if (listView != null) {
			if (listView.getCount() == 0) {
				no_matches.setVisibility(View.VISIBLE);
			} else {
				no_matches.setVisibility(View.GONE);
			}
		}
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

		Cursor c = MainActivity.dbHandle.rawQuery(
				"select distinct strftime('%Y'," + MatchDb.KEY_MATCH_DATE
						+ ") as _id from " + MatchDb.SQLITE_TABLE + " where "
						+ MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
						+ "'", null);
		int count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				season_list.add(c.getString(0));
				season_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_MY_TEAM + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
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

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_RESULT + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				result_list.add(c.getString(0));
				result_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_LEVEL + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				level_list.add(c.getString(0));
				level_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_OVERS + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				int temp = c.getInt(0);
				if (temp == -1) {
					overs_list.add("Unlimited");
					overs_list_selected.add("Unlimited");
				} else {
					overs_list.add(c.getInt(0) + "");
					overs_list_selected.add(c.getInt(0) + "");
				}
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_INNINGS + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				innings_list.add(c.getInt(0) + "");
				innings_list_selected.add(c.getInt(0) + "");
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_DURATION + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				duration_list.add(c.getString(0));
				duration_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_FIRST_ACTION + " as _id from "
				+ MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='"
				+ MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				first_list.add(c.getString(0));
				first_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();
	}

	static String buildSelectedItemString(List<String> items, boolean isInt) {
		String[] _items = items.toArray(new String[items.size()]);
		StringBuilder sb = new StringBuilder();
		boolean foundOne = false;

		for (int i = 0; i < _items.length; ++i) {
			if (!isInt) {
				_items[i] = "'" + _items[i] + "'";
			} else if (_items[i].equals("Unlimited")) {
				_items[i] = "-1";
			}
			if (foundOne) {
				sb.append(", ");
			}
			foundOne = true;

			sb.append(_items[i]);
		}

		return sb.toString();
	}

	public void deleteMatch(View view) {
		RelativeLayout vwParentRow = (RelativeLayout) view.getParent()
				.getParent();

		TextView child = (TextView) vwParentRow.getChildAt(0);
		String str = child.getText().toString();
		Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE
				+ "/" + str);
		getSherlockActivity().getContentResolver().delete(uri, null, null);
		uri = Uri
				.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/" + str);
		getSherlockActivity().getContentResolver().delete(uri, null, null);

		getSherlockActivity().getSupportLoaderManager().restartLoader(0, null,
				this);
	}
}
