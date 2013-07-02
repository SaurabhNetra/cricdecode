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

public class MatchCreateActivity extends SherlockFragmentActivity {

	TextView	matchDate;
	EditText	myTeam, opponentTeam, venue, overs, innings;
	Spinner		result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_create);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.actionbar_match_create);

		matchDate = (TextView) findViewById(R.id.txtMatchDate);
		myTeam = (EditText) findViewById(R.id.txtMyTeam);
		opponentTeam = (EditText) findViewById(R.id.txtOpponentTeam);
		venue = (EditText) findViewById(R.id.txtVenue);
		overs = (EditText) findViewById(R.id.txtOvers);
		innings = (EditText) findViewById(R.id.txtInnings);
		result = (Spinner) findViewById(R.id.spnResult);

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
				String matchDate_str = matchDate.getText().toString();
				String myTeam_str = myTeam.getText().toString();
				String opponentTeam_str = opponentTeam.getText().toString();
				String venue_str = venue.getText().toString();
				String overs_str = overs.getText().toString();
				String innings_str = innings.getText().toString();
				String result_str = result.getSelectedItem().toString();

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
					Toast.makeText(getBaseContext(),
							"Please enter OPPONENT TEAM", Toast.LENGTH_LONG)
							.show();
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
					Toast.makeText(getBaseContext(),
							"Please enter NUMBER OF OVERS IN AN INNINGS",
							Toast.LENGTH_LONG).show();
					return;
				}

				// check for blanks
				if (innings_str.trim().equalsIgnoreCase("")) {
					Toast.makeText(getBaseContext(),
							"Please enter the NUMBER OF INNINGS",
							Toast.LENGTH_LONG).show();
					return;
				}

				// check for blanks
				if (result_str.trim().equalsIgnoreCase("")) {
					Toast.makeText(getBaseContext(),
							"Please enter RESULT of the MATCH",
							Toast.LENGTH_LONG).show();
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
				values.put(MatchDb.KEY_STATUS, MatchDb.CURRENT);

				// insert a record
				getContentResolver().insert(
						CricDeCodeContentProvider.CONTENT_URI_MATCH, values);

				// Go to Match Activity
				finish();
				break;
			default:
				break;
		}
	}

}
