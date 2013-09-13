package co.acjs.cricdecode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class MatchCreationFragment extends SherlockFragment {
	static MatchCreationFragment matchCreationFragment;
	private static SQLiteDatabase dbHandle;
	private static String queryStr, field_str;

	TextView lbl_overs;
	EditText date_of_match;
	AutoCompleteTextView myTeam, opponentTeam, venue, overs,
			autoCompleteTextView;
	Spinner innings, limited, level;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		matchCreationFragment = this;
		View rootView = inflater.inflate(R.layout.match_creation, container,
				false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			Log.d("Debug", "Fragment Restore State");
			date_of_match
					.setText(savedInstanceState.getString("date_of_match"));
			limited.setSelection(savedInstanceState.getInt("limited"));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("date_of_match", date_of_match.getText().toString());
		outState.putInt("limited", limited.getSelectedItemPosition());
		Log.d("Debug", "Fragment Save State");
	}

	public void init(View view) {
		date_of_match = (EditText) view.findViewById(R.id.date_of_match);
		myTeam = (AutoCompleteTextView) view.findViewById(R.id.my_team);
		opponentTeam = (AutoCompleteTextView) view
				.findViewById(R.id.opponent_team);
		venue = (AutoCompleteTextView) view.findViewById(R.id.venue);
		level = (Spinner) view.findViewById(R.id.level);
		limited = (Spinner) view.findViewById(R.id.over_limit);
		lbl_overs = (TextView) view.findViewById(R.id.lbl_overs);
		overs = (AutoCompleteTextView) view.findViewById(R.id.overs);
		innings = (Spinner) view.findViewById(R.id.innings);

		dbHandle = ((CricDeCodeContentProvider) MainActivity.client
				.getLocalContentProvider()).getDbHelper().getReadableDatabase();

		setAutoSuggestions();

		limited.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int i, long l) {
				if (i == 1) {
					overs.setVisibility(View.GONE);
					lbl_overs.setVisibility(View.GONE);
				} else {
					overs.setVisibility(View.VISIBLE);
					lbl_overs.setVisibility(View.VISIBLE);
				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				return;
			}
		});
	}

	public void insertMatch() {
		String device_id = AccessSharedPrefs.mPrefs.getString("device_id", "");
		String matchDate_str = date_of_match.getText().toString();
		String myTeam_str = myTeam.getText().toString();
		String opponentTeam_str = opponentTeam.getText().toString();
		String venue_str = venue.getText().toString();
		String level_str = level.getSelectedItem().toString();
		String overs_str = overs.getText().toString();
		String innings_str = innings.getSelectedItem().toString();
		String result_str = "";
		String review_str = "";
		String first_action_str = "";
		String duration_str = "";
		String synced_str = "0";
		int limited_int = limited.getSelectedItemPosition();

		// check for blanks
		if (matchDate_str.trim().equalsIgnoreCase("")) {
			Toast.makeText(getSherlockActivity(), "Please enter MATCH DATE",
					Toast.LENGTH_LONG).show();
			return;
		}

		// check for blanks
		if (myTeam_str.trim().equalsIgnoreCase("")) {
			Toast.makeText(getSherlockActivity(), "Please enter YOUR TEAM",
					Toast.LENGTH_LONG).show();
			return;
		}

		// check for blanks
		if (opponentTeam_str.trim().equalsIgnoreCase("")) {
			Toast.makeText(getSherlockActivity(), "Please enter OPPONENT TEAM",
					Toast.LENGTH_LONG).show();
			return;
		}

		// check for blanks
		if (venue_str.trim().equalsIgnoreCase("")) {
			Toast.makeText(getSherlockActivity(), "Please enter VENUE",
					Toast.LENGTH_LONG).show();
			return;
		}

		// check for blanks
		if (overs_str.trim().equalsIgnoreCase("")) {
			if (limited_int == 1) {
				overs_str = "-1";
			} else {
				Toast.makeText(getSherlockActivity(),
						"Please enter NUMBER OF OVERS IN AN INNINGS",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy",
				Locale.getDefault());

		Date date = new Date();
		try {
			date = sdf.parse(matchDate_str);
		} catch (ParseException e) {
			e.printStackTrace();
			Log.d("Debug", "Date Exception");
		}

		Log.d("Debug", "Device Id" + device_id);
		ContentValues values = new ContentValues();
		values.put(MatchDb.KEY_DEVICE_ID, device_id);
		values.put(MatchDb.KEY_MATCH_DATE, DateFormat
				.format("yyyy-MM-dd", date).toString());
		values.put(MatchDb.KEY_MY_TEAM, myTeam_str);
		values.put(MatchDb.KEY_OPPONENT_TEAM, opponentTeam_str);
		values.put(MatchDb.KEY_VENUE, venue_str);
		values.put(MatchDb.KEY_LEVEL, level_str);
		values.put(MatchDb.KEY_OVERS, Integer.parseInt(overs_str));
		values.put(MatchDb.KEY_INNINGS, Integer.parseInt(innings_str));
		values.put(MatchDb.KEY_RESULT, result_str);
		values.put(MatchDb.KEY_REVIEW, review_str);
		values.put(MatchDb.KEY_DURATION, duration_str);
		values.put(MatchDb.KEY_FIRST_ACTION, first_action_str);
		values.put(MatchDb.KEY_STATUS, MatchDb.MATCH_CURRENT);
		values.put(MatchDb.KEY_SYNCED, synced_str);
		int id = AccessSharedPrefs.mPrefs.getInt("max_match_id", 0) + 1;
		AccessSharedPrefs.setInt(getSherlockActivity(), "max_match_id", id);
		values.put(MatchDb.KEY_ROWID, id);
		// insert a record
		getSherlockActivity().getContentResolver().insert(
				CricDeCodeContentProvider.CONTENT_URI_MATCH, values);

		// Go to Match Activity
		Toast.makeText(getSherlockActivity(), "Match Created",
				Toast.LENGTH_LONG).show();

		// Increment Ongoing Matches Count
		AccessSharedPrefs.setInt(getSherlockActivity(), "ongoingMatches",
				AccessSharedPrefs.mPrefs.getInt("ongoingMatches", 0) + 1);

		// Go to Ongoing Matches
		Date d = new Date();
		if (date.after(d) || date.equals(d)) {
			((MainActivity) getSherlockActivity()).currentFragment = MainActivity.ONGOING_MATCHES_FRAGMENT;
			((MainActivity) getSherlockActivity()).preFragment = MainActivity.MATCH_CREATION_FRAGMENT;
			((MainActivity) getSherlockActivity()).selectItem(
					MainActivity.ONGOING_MATCHES_FRAGMENT, true);
		} else if (date.before(d)) {
			PerformanceFragmentEdit.performanceFragmentEdit = new PerformanceFragmentEdit();
			Bundle bundle = new Bundle();
			bundle.putInt("rowId", id);
			bundle.putString("deviceId", device_id);
			bundle.putInt("innings", Integer.parseInt(innings_str));
			PerformanceFragmentEdit.performanceFragmentEdit
					.setArguments(bundle);
			((MainActivity) getSherlockActivity()).currentFragment = MainActivity.PERFORMANCE_FRAGMENT_EDIT;
			((MainActivity) getSherlockActivity()).preFragment = MainActivity.MATCH_CREATION_FRAGMENT;
			((MainActivity) getSherlockActivity())
					.onPrepareOptionsMenu(((MainActivity) getSherlockActivity()).current_menu);
			((MainActivity) getSherlockActivity()).selectItem(
					MainActivity.PERFORMANCE_FRAGMENT_EDIT, false);
		}
	}

	public void setAutoSuggestions() {
		// Get the string array
		String[] auto_overs = getResources().getStringArray(R.array.overs);
		// Create the adapter and set it to the AutoCompleteTextView
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getSherlockActivity(), R.layout.autocomplete_suggestion_item,
				auto_overs);
		overs.setAdapter(adapter);
		overs.setThreshold(1);

		// Create an ItemAutoTextAdapter for the State Name field,
		// and set it as the OnItemClickListener for that field.
		queryStr = "select distinct " + MatchDb.KEY_MY_TEAM + " as _id from "
				+ MatchDb.SQLITE_TABLE;
		autoCompleteTextView = myTeam;
		field_str = MatchDb.KEY_MY_TEAM;
		MyCursorAdapter mAdapter = this.new MyCursorAdapter(
				getSherlockActivity(), null, true);
		myTeam.setAdapter(mAdapter);
		myTeam.setThreshold(1);

		queryStr = "select distinct " + MatchDb.KEY_OPPONENT_TEAM
				+ " as _id from " + MatchDb.SQLITE_TABLE;
		autoCompleteTextView = opponentTeam;
		field_str = MatchDb.KEY_OPPONENT_TEAM;
		mAdapter = this.new MyCursorAdapter(getSherlockActivity(), null, true);
		opponentTeam.setAdapter(mAdapter);
		opponentTeam.setThreshold(1);

		queryStr = "select distinct " + MatchDb.KEY_VENUE + " as _id from "
				+ MatchDb.SQLITE_TABLE;
		autoCompleteTextView = venue;
		field_str = MatchDb.KEY_VENUE;
		mAdapter = this.new MyCursorAdapter(getSherlockActivity(), null, true);
		venue.setAdapter(mAdapter);
		venue.setThreshold(1);

	}

	class MyCursorAdapter extends CursorAdapter {
		String query, field;
		AutoCompleteTextView textView;

		public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
			query = queryStr;
			textView = autoCompleteTextView;
			field = field_str;
		}

		@Override
		public CharSequence convertToString(Cursor cursor) {
			return cursor.getString(0);
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			String str = textView.getText().toString();
			str = " where " + field + " like '%" + str + "%'";
			return dbHandle.rawQuery(query + str, null);
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			final LayoutInflater inflater = LayoutInflater.from(arg0);
			final View view = inflater.inflate(
					R.layout.autocomplete_suggestion_item, arg2, false);
			return view;
		}

		@Override
		public void bindView(View arg0, Context arg1, Cursor arg2) {
			final String text = (String) convertToString(arg2);
			((TextView) arg0).setText(text);

		}

	}

}
