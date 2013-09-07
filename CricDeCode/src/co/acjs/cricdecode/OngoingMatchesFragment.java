package co.acjs.cricdecode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class OngoingMatchesFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	static OngoingMatchesFragment ongoingMatchesFragment;

	private SimpleCursorAdapter dataAdapter;
	RelativeLayout no_matches_created;
	ListView listView;

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
				MatchDb.KEY_DURATION, MatchDb.KEY_FIRST_ACTION,
				MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM,
				MatchDb.KEY_VENUE, MatchDb.KEY_LEVEL, MatchDb.KEY_OVERS };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id._id, R.id.innings, R.id.day, R.id.month,
				R.id.year, R.id.my_team, R.id.opponent_team, R.id.venue,
				R.id.level, R.id.overs };

		// create an adapter from the SimpleCursorAdapter
		dataAdapter = new SimpleCursorAdapter(getSherlockActivity(),
				R.layout.ongoing_match_list_item, null, columns, to, 0);

		// get reference to the ListView
		listView = (ListView) view.findViewById(R.id.content_list);
		no_matches_created = (RelativeLayout) view
				.findViewById(R.id.no_matches_created);

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

				PerformanceFragmentEdit.performanceFragmentEdit = new PerformanceFragmentEdit();
				Bundle bundle = new Bundle();
				bundle.putInt("rowId", rowId);
				bundle.putInt("innings", innings);
				PerformanceFragmentEdit.performanceFragmentEdit
						.setArguments(bundle);
				((MainActivity) getSherlockActivity()).currentFragment = MainActivity.PERFORMANCE_FRAGMENT_EDIT;
				((MainActivity) getSherlockActivity()).preFragment = MainActivity.ONGOING_MATCHES_FRAGMENT;
				((MainActivity) getSherlockActivity())
						.onPrepareOptionsMenu(((MainActivity) getSherlockActivity()).current_menu);

				((MainActivity) getSherlockActivity()).selectItem(
						MainActivity.PERFORMANCE_FRAGMENT_EDIT, false);
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
				MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_CURRENT + "'", null,
				null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		Log.d("Debug",
				"on Load Finished" + " cursor columns "
						+ Arrays.toString(data.getColumnNames()));
		AccessSharedPrefs.mPrefs = getSherlockActivity().getSharedPreferences(
				"CricDeCode", Context.MODE_PRIVATE);
		AccessSharedPrefs.setInt(getSherlockActivity(), "ongoingMatches", data.getCount());
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
		if (listView != null) {
			if (listView.getCount() == 0) {
				no_matches_created.setVisibility(View.VISIBLE);
			} else {
				no_matches_created.setVisibility(View.GONE);
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

	public void addToCareer(View view) {
		RelativeLayout vwParentRow = (RelativeLayout) view.getParent()
				.getParent();

		TextView child = (TextView) vwParentRow.getChildAt(0);
		String str = child.getText().toString();
		Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE
				+ "/" + str);
		Cursor c = getSherlockActivity().getContentResolver().query(uri,
				new String[] { PerformanceDb.KEY_MATCHID }, null, null, null);
		if (c.getCount() == 0) {
			c.close();
			Toast.makeText(getSherlockActivity(),
					"First Save your Performance", Toast.LENGTH_SHORT).show();
		} else {
			c.close();
			uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/"
					+ str);
			ContentValues values = new ContentValues();
			values.put(MatchDb.KEY_STATUS, MatchDb.MATCH_HISTORY);

			getSherlockActivity().getContentResolver().update(uri, values,
					null, null);
			uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE
					+ "/" + str);
			getSherlockActivity().getContentResolver().update(uri, values,
					null, null);
			Toast.makeText(getSherlockActivity(), "Match added to Career",
					Toast.LENGTH_LONG).show();

			AccessSharedPrefs.mPrefs = getSherlockActivity().getSharedPreferences(
					"CricDeCode", Context.MODE_PRIVATE);
			AccessSharedPrefs.setInt(getSherlockActivity(),"ongoingMatches", 
					AccessSharedPrefs.mPrefs.getInt("ongoingMatches", 0) - 1);
			AccessSharedPrefs.setInt(getSherlockActivity(), "diaryMatches", 
					AccessSharedPrefs.mPrefs.getInt("diaryMatches", 0) + 1);

			getSherlockActivity().getSupportLoaderManager().restartLoader(0,
					null, this);
		}
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

		AccessSharedPrefs.mPrefs = getSherlockActivity().getSharedPreferences(
				"CricDeCode", Context.MODE_PRIVATE);
		AccessSharedPrefs.setInt(getSherlockActivity(),"ongoingMatches", 
				AccessSharedPrefs.mPrefs.getInt("ongoingMatches", 0) - 1);

		getSherlockActivity().getSupportLoaderManager().restartLoader(0, null,
				this);
	}
}
