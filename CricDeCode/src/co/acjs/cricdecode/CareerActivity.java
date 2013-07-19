package co.acjs.cricdecode;

import java.math.BigDecimal;

import android.content.ContentProviderClient;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.internal.ec;

public class CareerActivity extends SherlockFragmentActivity implements ActionBar.TabListener {

	private static final String		STATE_SELECTED_NAVIGATION_ITEM	= "selected_navigation_item";

	private ContentProviderClient	client;
	private SQLiteDatabase			dbHandle;

	// Fields

	// Batting
	private int						matches, bat_innings, bat_not_outs,
			bat_runs, bat_highest, bat_balls, bat_100, bat_50;
	private float					bat_avg, bat_str;

	// Bowling
	private int						bowl_innings, bowl_runs, wickets, fwh, twm;
	private String					best_innings, best_match;
	private float					bowl_avg, eco_rate, bowl_str, overs;

	// Fielding
	private int						catches, run_outs, stumpings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.career_container);

		// Set up the action bar to show tabs.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText(R.string.batting)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.bowling)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.fielding)
				.setTabListener(this));

		// Initialize Database Handles

		client = getContentResolver().acquireContentProviderClient(
				CricDeCodeContentProvider.AUTHORITY);
		dbHandle = ((CricDeCodeContentProvider) client
				.getLocalContentProvider()).getDbHelper().getReadableDatabase();
		fireQueries();

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		int index = getSupportActionBar().getSelectedNavigationIndex();
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, index);

	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the
		// container view.
		Log.d("Debug", "On Tab Selected called " + tab.getPosition());
		SherlockFragment fragment = new CareerSectionFragment();
		Bundle args = new Bundle();
		args.putInt(CareerSectionFragment.ARG_SECTION_NUMBER,
				tab.getPosition() + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		Log.d("Debug", "On Tab Selected finished");
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		Log.d("Debug", "On Tab Unselected called");
		Log.d("Debug", "Tab Position " + tab.getPosition() + "");
		Log.d("Debug", "On Tab Unselected finished");
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	public static class CareerSectionFragment extends SherlockFragment {
		public static final String	ARG_SECTION_NUMBER	= "placeholder_text";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			Log.d("Debug", "On Create Fragment called");
			View view = null;
			switch (getArguments().getInt(ARG_SECTION_NUMBER)) {

				case 1:
					view = inflater.inflate(R.layout.activity_career_batting,
							container, false);
					break;
				case 2:
					view = inflater.inflate(R.layout.activity_career_bowling,
							container, false);
					break;
				case 3:
					view = inflater.inflate(R.layout.activity_career_fielding,
							container, false);
					break;
				default:
					break;
			}
			Log.d("Debug", "On Create View finished");
			return view;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			Log.d("Debug", "On View Created called");
			((CareerActivity) getActivity()).viewInfo(getArguments().getInt(
					ARG_SECTION_NUMBER) - 1);
			Log.d("Debug", "On View Created finished");
		}
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
	protected void onDestroy() {
		super.onDestroy();
		dbHandle.close();
		client.release();
	}

	public void viewInfo(int tab_index) {
		Log.d("Debug", "On View Info called");
		switch (tab_index) {
			case 0:
				((TextView) findViewById(R.id.lblMatches))
						.setText(matches + "");
				((TextView) findViewById(R.id.lblInnings))
						.setText(bat_innings + "");
				((TextView) findViewById(R.id.lblNO))
						.setText(bat_not_outs + "");
				((TextView) findViewById(R.id.lblRuns)).setText(bat_runs + "");
				((TextView) findViewById(R.id.lblBalls))
						.setText(bat_balls + "");
				((TextView) findViewById(R.id.lblHS)).setText(bat_highest + "");
				if (bat_avg != -1) {
					((TextView) findViewById(R.id.lblAvg))
							.setText(bat_avg + "");
				} else {
					((TextView) findViewById(R.id.lblAvg)).setText("NA");
				}
				if (bat_str != -1) {
					((TextView) findViewById(R.id.lblSR)).setText(bat_str + "");
				} else {
					((TextView) findViewById(R.id.lblSR)).setText("NA");
				}
				((TextView) findViewById(R.id.lbl100s)).setText(bat_100 + "");
				((TextView) findViewById(R.id.lbl50s)).setText(bat_50 + "");
				break;
			case 1:
				((TextView) findViewById(R.id.lblMatches))
						.setText(matches + "");
				((TextView) findViewById(R.id.lblInnings))
						.setText(bowl_innings + "");
				((TextView) findViewById(R.id.lblOvers)).setText(overs + "");
				((TextView) findViewById(R.id.lblRuns)).setText(bowl_runs + "");
				((TextView) findViewById(R.id.lblWickets))
						.setText(wickets + "");
				((TextView) findViewById(R.id.lblEco)).setText(eco_rate + "");
				if (eco_rate != -1) {
					((TextView) findViewById(R.id.lblEco))
							.setText(eco_rate + "");
				} else {
					((TextView) findViewById(R.id.lblEco)).setText("NA");
				}
				if (bowl_avg != -1) {
					((TextView) findViewById(R.id.lblAvg))
							.setText(bowl_avg + "");
				} else {
					((TextView) findViewById(R.id.lblAvg)).setText("NA");
				}
				if (bowl_str != -1) {
					((TextView) findViewById(R.id.lblSR))
							.setText(bowl_str + "");
				} else {
					((TextView) findViewById(R.id.lblSR)).setText("NA");
				}
				((TextView) findViewById(R.id.lblFWI)).setText(fwh + "");
				((TextView) findViewById(R.id.lblFWM)).setText(twm + "");
				break;
			case 2:
				((TextView) findViewById(R.id.lblMatches))
						.setText(matches + "");
				((TextView) findViewById(R.id.lblCT)).setText(catches + "");
				((TextView) findViewById(R.id.lblRO)).setText(run_outs + "");
				((TextView) findViewById(R.id.lblST)).setText(stumpings + "");
				break;
			default:
				break;
		}
		Log.d("Debug", "On View info Finished");
	}

	public void fireQueries() {
		// Initialize Fields

		// Batting
		Cursor cursor = dbHandle
				.rawQuery(
						"select count(" + MatchDb.KEY_ROWID + ") from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'",
						null);
		cursor.moveToFirst();
		matches = cursor.getInt(0);
		cursor.close();
		cursor = dbHandle
				.rawQuery(
						"select count(" + PerformanceDb.KEY_ROWID + "),sum(" + PerformanceDb.KEY_BAT_RUNS + "),max(" + PerformanceDb.KEY_BAT_RUNS + "),sum(" + PerformanceDb.KEY_BAT_BALLS + ") from " + PerformanceDb.SQLITE_TABLE + " where " + PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "' and (" + PerformanceDb.KEY_BAT_HOW_OUT + "!='Not Out' or " + PerformanceDb.KEY_BAT_BALLS + "!=0)",
						null);
		cursor.moveToFirst();
		bat_innings = cursor.getInt(0);
		bat_runs = cursor.getInt(1);
		bat_highest = cursor.getInt(2);
		bat_balls = cursor.getInt(3);
		cursor.close();
		cursor = dbHandle
				.rawQuery(
						"select count(" + PerformanceDb.KEY_ROWID + ") from " + PerformanceDb.SQLITE_TABLE + " where " + PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "' and " + PerformanceDb.KEY_BAT_HOW_OUT + "!='Not Out'",
						null);
		cursor.moveToFirst();
		int outs = cursor.getInt(0);
		bat_not_outs = bat_innings - outs;
		cursor.close();
		if (outs != 0) {
			bat_avg = (float) bat_runs / outs;
			bat_avg = round(bat_avg, 2);
		} else {
			bat_avg = -1;
		}
		if (bat_balls != 0) {
			bat_str = (float) bat_runs / bat_balls;
			bat_str = round(bat_str, 2) * 100;
		} else {
			bat_str = -1;
		}

		cursor = dbHandle
				.rawQuery(
						"select count(" + PerformanceDb.KEY_ROWID + ") from " + PerformanceDb.SQLITE_TABLE + " where " + PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "' and " + PerformanceDb.KEY_BAT_RUNS + ">=100",
						null);
		cursor.moveToFirst();
		bat_100 = cursor.getInt(0);
		cursor.close();
		cursor = dbHandle
				.rawQuery(
						"select count(" + PerformanceDb.KEY_ROWID + ") from " + PerformanceDb.SQLITE_TABLE + " where " + PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "' and " + PerformanceDb.KEY_BAT_RUNS + ">=50",
						null);
		cursor.moveToFirst();
		bat_50 = cursor.getInt(0) - bat_100;
		cursor.close();

		// Bowling
		cursor = dbHandle
				.rawQuery(
						"select count(" + PerformanceDb.KEY_ROWID + "),sum(" + PerformanceDb.KEY_BOWL_OVERS + "),sum(" + PerformanceDb.KEY_BOWL_RUNS + "),sum(" + PerformanceDb.KEY_BOWL_WKTS_LEFT + "),sum(" + PerformanceDb.KEY_BOWL_WKTS_RIGHT + ") from " + PerformanceDb.SQLITE_TABLE + " where " + PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "' and " + PerformanceDb.KEY_BOWL_OVERS + "!=0",
						null);
		cursor.moveToFirst();
		bowl_innings = cursor.getInt(0);
		int balls = cursor.getInt(1);
		overs = balls / 6 + (float) (balls % 6) / 10;
		bowl_runs = cursor.getInt(2);
		wickets = cursor.getInt(3) + cursor.getInt(4);
		if (balls != 0) {
			eco_rate = round((float) bowl_runs / balls * 6, 2);
		} else {
			eco_rate = -1;
		}
		if (wickets != 0) {
			bowl_str = round((float) balls / wickets, 2);
			bowl_avg = round((float) bowl_runs / wickets, 2);
		} else {
			bowl_str = -1;
			bowl_avg = -1;
		}
		cursor.close();
		cursor = dbHandle
				.rawQuery(
						"select count(" + PerformanceDb.KEY_ROWID + ") from " + PerformanceDb.SQLITE_TABLE + " where " + PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "' and " + PerformanceDb.KEY_BOWL_WKTS_LEFT + "+" + PerformanceDb.KEY_BOWL_WKTS_RIGHT + ">=5",
						null);
		cursor.moveToFirst();
		fwh = cursor.getInt(0);
		cursor.close();
		cursor = dbHandle
				.rawQuery(
						"select sum(" + PerformanceDb.KEY_BOWL_WKTS_LEFT + "+" + PerformanceDb.KEY_BOWL_WKTS_RIGHT + ") as sumtotal from " + PerformanceDb.SQLITE_TABLE + " where " + PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "' group by " + PerformanceDb.KEY_ROWID + " having sumtotal>=10",
						null);
		twm = cursor.getCount();
		cursor.close();

		// Fielding
		cursor = dbHandle
				.rawQuery(
						"select sum(" + PerformanceDb.KEY_FIELD_CLOSE_CATCH + "),sum(" + PerformanceDb.KEY_FIELD_CIRCLE_CATCH + "),sum(" + PerformanceDb.KEY_FIELD_DEEP_CATCH + "),sum(" + PerformanceDb.KEY_FIELD_RO_CIRCLE + "),sum(" + PerformanceDb.KEY_FIELD_RO_DEEP + "),sum(" + PerformanceDb.KEY_FIELD_RO_DIRECT + "),sum(" + PerformanceDb.KEY_FIELD_STUMPINGS + ") from " + PerformanceDb.SQLITE_TABLE + " where " + PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'",
						null);
		cursor.moveToFirst();
		catches = cursor.getInt(0) + cursor.getInt(1) + cursor.getInt(2);
		run_outs = cursor.getInt(3) + cursor.getInt(4) + cursor.getInt(5);
		stumpings = cursor.getInt(6);
		cursor.close();
	}

	public static float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

}
