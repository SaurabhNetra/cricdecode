package co.acjs.cricdecode;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class MatchCreateActivity extends SherlockFragmentActivity {

	TextView	matchDate;
	EditText	myTeam, opponentTeam, venue, overs;
	Spinner		result, innings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_create);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		matchDate = (TextView) findViewById(R.id.txtMatchDate);
		myTeam = (EditText) findViewById(R.id.txtMyTeam);
		opponentTeam = (EditText) findViewById(R.id.txtOpponentTeam);
		venue = (EditText) findViewById(R.id.txtVenue);
		overs = (EditText) findViewById(R.id.txtOvers);
		innings = (Spinner) findViewById(R.id.spn_innings);
		result = (Spinner) findViewById(R.id.spnResult);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				String matchDate_str = matchDate.getText().toString();
				String myTeam_str = myTeam.getText().toString();
				String opponentTeam_str = opponentTeam.getText().toString();
				String venue_str = venue.getText().toString();
				String overs_str = overs.getText().toString();
				String innings_str = innings.getSelectedItem().toString();
				String result_str = result.getSelectedItem().toString();

				// check for blanks
				if (matchDate_str.trim().equalsIgnoreCase("")) {
					Toast.makeText(getBaseContext(), "Please enter MATCH DATE",
							Toast.LENGTH_LONG).show();
					break;
				}
				// check for blanks
				if (myTeam_str.trim().equalsIgnoreCase("")) {
					Toast.makeText(getBaseContext(), "Please enter YOUR TEAM",
							Toast.LENGTH_LONG).show();
					break;
				}

				// check for blanks
				if (opponentTeam_str.trim().equalsIgnoreCase("")) {
					Toast.makeText(getBaseContext(),
							"Please enter OPPONENT TEAM", Toast.LENGTH_LONG)
							.show();
					break;
				}

				// check for blanks
				if (venue_str.trim().equalsIgnoreCase("")) {
					Toast.makeText(getBaseContext(), "Please enter VENUE",
							Toast.LENGTH_LONG).show();
					break;
				}

				// check for blanks
				if (overs_str.trim().equalsIgnoreCase("")) {
					Toast.makeText(getBaseContext(),
							"Please enter NUMBER OF OVERS IN AN INNINGS",
							Toast.LENGTH_LONG).show();
					break;
				}

				// check for blanks
				if (result_str.trim().equalsIgnoreCase("")) {
					Toast.makeText(getBaseContext(),
							"Please enter RESULT of the MATCH",
							Toast.LENGTH_LONG).show();
					break;
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
			default:
				break;
		}
	}

}
