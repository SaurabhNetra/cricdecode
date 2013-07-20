package co.acjs.cricdecode;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class MatchCreateActivity extends SherlockFragmentActivity {

	private static ContentProviderClient	client;
	private static SQLiteDatabase			dbHandle;
	private static String					queryStr;

	TextView								matchDate, overs_text;
	AutoCompleteTextView					myTeam, opponentTeam, venue, overs;
	Spinner									result, innings, limited;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_create);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.actionbar_match_create);

		matchDate = (TextView) findViewById(R.id.txtMatchDate);
		overs_text = (TextView) findViewById(R.id.lblOvers);
		myTeam = (AutoCompleteTextView) findViewById(R.id.txtMyTeam);
		opponentTeam = (AutoCompleteTextView) findViewById(R.id.txtOpponentTeam);
		venue = (AutoCompleteTextView) findViewById(R.id.txtVenue);
		overs = (AutoCompleteTextView) findViewById(R.id.txtOvers);
		innings = (Spinner) findViewById(R.id.spn_innings);
		result = (Spinner) findViewById(R.id.spnResult);
		limited = (Spinner) findViewById(R.id.spnLimited);

		client = getContentResolver().acquireContentProviderClient(
				CricDeCodeContentProvider.AUTHORITY);
		dbHandle = ((CricDeCodeContentProvider) client
				.getLocalContentProvider()).getDbHelper().getReadableDatabase();

		setAutoSuggestions();

		limited.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if (i == 1) {
					overs_text.setVisibility(View.GONE);
					overs.setVisibility(View.GONE);
				} else {
					overs_text.setVisibility(View.VISIBLE);
					overs.setVisibility(View.VISIBLE);
				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				return;
			}
		});

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("matchDate", matchDate.getText().toString());
		outState.putInt("limited", limited.getSelectedItemPosition());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		matchDate.setText(savedInstanceState.getString("matchDate"));
		limited.setSelection(savedInstanceState.getInt("limited"));
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

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.txtMatchDate:
				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				// Create and show the dialog.
				DatePickerFragment newFragment = new DatePickerFragment();
				newFragment.setView_callee(findViewById(R.id.txtMatchDate));
				newFragment.show(ft, null);
				break;
			case R.id.btnMatchCreate:
				saveAndBack();
				break;
			default:
				break;
		}
	}

	public void saveAndBack() {
		String matchDate_str = matchDate.getText().toString();
		String myTeam_str = myTeam.getText().toString();
		String opponentTeam_str = opponentTeam.getText().toString();
		String venue_str = venue.getText().toString();
		String overs_str = overs.getText().toString();
		String innings_str = innings.getSelectedItem().toString();
		String result_str = result.getSelectedItem().toString();
		int limited_int = limited.getSelectedItemPosition();

		// check for blanks
		if (matchDate_str.trim().equalsIgnoreCase("")) {
			Toast.makeText(getBaseContext(), "Please enter MATCH DATE",
					Toast.LENGTH_LONG).show();
			return;
		}
		// check for blanks
		if (myTeam_str.trim().equalsIgnoreCase("")) {
			Toast.makeText(getBaseContext(), "Please enter YOUR TEAM",
					Toast.LENGTH_LONG).show();
			return;
		}

		// check for blanks
		if (opponentTeam_str.trim().equalsIgnoreCase("")) {
			Toast.makeText(getBaseContext(), "Please enter OPPONENT TEAM",
					Toast.LENGTH_LONG).show();
			return;
		}

		// check for blanks
		if (venue_str.trim().equalsIgnoreCase("")) {
			Toast.makeText(getBaseContext(), "Please enter VENUE",
					Toast.LENGTH_LONG).show();
			return;
		}

		// check for blanks
		if (overs_str.trim().equalsIgnoreCase("")) {
			if (limited_int == 1) {
				overs_str = "-1";
			} else {
				Toast.makeText(getBaseContext(),
						"Please enter NUMBER OF OVERS IN AN INNINGS",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		// check for blanks
		if (result_str.trim().equalsIgnoreCase("")) {
			Toast.makeText(getBaseContext(),
					"Please enter RESULT of the MATCH", Toast.LENGTH_LONG)
					.show();
			return;
		}

		ContentValues values = new ContentValues();
		values.put(MatchDb.KEY_MATCH_DATE, matchDate_str);
		values.put(MatchDb.KEY_MY_TEAM, myTeam_str);
		values.put(MatchDb.KEY_OPPONENT_TEAM, opponentTeam_str);
		values.put(MatchDb.KEY_VENUE, venue_str);
		values.put(MatchDb.KEY_OVERS, Integer.parseInt(overs_str));
		values.put(MatchDb.KEY_INNINGS, Integer.parseInt(innings_str));
		values.put(MatchDb.KEY_RESULTS, result_str);
		values.put(MatchDb.KEY_STATUS, MatchDb.MATCH_CURRENT);

		// insert a record
		getContentResolver().insert(
				CricDeCodeContentProvider.CONTENT_URI_MATCH, values);

		// Go to Match Activity
		Toast.makeText(this, "Match Created", Toast.LENGTH_LONG).show();
		finish();
	}

	public void setAutoSuggestions() {
		// Get the string array
		String[] auto_overs = getResources().getStringArray(R.array.auto_overs);
		// Create the adapter and set it to the AutoCompleteTextView
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, auto_overs);
		overs.setAdapter(adapter);
		overs.setThreshold(1);

		// Create an ItemAutoTextAdapter for the State Name field,
		// and set it as the OnItemClickListener for that field.
		queryStr = "select distinct " + MatchDb.KEY_MY_TEAM + " as _id from " + MatchDb.SQLITE_TABLE;
		MyCursorAdapter mAdapter = this.new MyCursorAdapter(this, null, true);
		myTeam.setAdapter(mAdapter);
		myTeam.setThreshold(1);

		queryStr = "select distinct " + MatchDb.KEY_OPPONENT_TEAM + " as _id from " + MatchDb.SQLITE_TABLE;
		mAdapter = this.new MyCursorAdapter(this, null, true);
		opponentTeam.setAdapter(mAdapter);
		opponentTeam.setThreshold(1);

		queryStr = "select distinct " + MatchDb.KEY_VENUE + " as _id from " + MatchDb.SQLITE_TABLE;
		mAdapter = this.new MyCursorAdapter(this, null, true);
		venue.setAdapter(mAdapter);
		venue.setThreshold(1);

	}

	class MyCursorAdapter extends CursorAdapter {
		String	query_str;

		public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
			query_str = queryStr;

		}

		@Override
		public CharSequence convertToString(Cursor cursor) {
			return cursor.getString(0);
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			return dbHandle.rawQuery(query_str, null);
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			final LayoutInflater inflater = LayoutInflater.from(arg0);
			final View view = inflater.inflate(
					android.R.layout.simple_dropdown_item_1line, arg2, false);
			return view;
		}

		@Override
		public void bindView(View arg0, Context arg1, Cursor arg2) {
			final String text = (String) convertToString(arg2);
			((TextView) arg0).setText(text);

		}

	}

}
