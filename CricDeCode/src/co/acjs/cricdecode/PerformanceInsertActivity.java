package co.acjs.cricdecode;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class PerformanceInsertActivity extends SherlockFragmentActivity implements ActionBar.TabListener {

	private static final String	STATE_SELECTED_NAVIGATION_ITEM	= "selected_navigation_item";

	private int					innings, row_id, cur_inn;

	// Fields

	// Batting
	private int[]				bat_no, bat_runs, bat_balls, bat_time;
	private String[]			how_out, bowler_type;

	// Bowling
	private int[]				overs, maidens, bowl_runs, wkts_left,
			wkts_right, noballs, wides;

	// Fielding
	private int[]				close_catch, circle_catch, deep_catch,
			circle_ro, direct_ro, deep_ro, stumpings, byes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.element_container);
		Log.d("Debug", "On Create called");

		// Initialize arrays
		bat_no = new int[2];
		bat_runs = new int[2];
		bat_balls = new int[2];
		bat_time = new int[2];
		how_out = new String[2];
		bowler_type = new String[2];

		overs = new int[2];
		maidens = new int[2];
		bowl_runs = new int[2];
		wkts_left = new int[2];
		wkts_right = new int[2];
		noballs = new int[2];
		wides = new int[2];

		close_catch = new int[2];
		circle_catch = new int[2];
		deep_catch = new int[2];
		circle_ro = new int[2];
		direct_ro = new int[2];
		deep_ro = new int[2];
		stumpings = new int[2];
		byes = new int[2];

		// Set up the action bar to show tabs.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.actionbar_performance_insert);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText(R.string.batting)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.bowling)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.fielding)
				.setTabListener(this));

		Intent intent = getIntent();
		innings = intent.getIntExtra("innings", 0);
		row_id = intent.getIntExtra("rowId", 0);
		Log.d("Debug", "RowID " + row_id + " Innings " + innings);
		if (savedInstanceState != null) {
			innings = savedInstanceState.getInt("innings");
			row_id = savedInstanceState.getInt("rowId");
			Log.d("Debug", "RowID " + row_id + " Innings " + innings);
		}

		Spinner spinner = (Spinner) findViewById(R.id.spnInning_no);
		ArrayAdapter<String> spinnerArrayAdapter;
		if (innings == 1) {
			spinnerArrayAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item,
					new String[] { "1st Innings" });
		} else {
			spinnerArrayAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item,
					new String[] { "1st Innings", "2nd Innings" });
		}

		spinner.setAdapter(spinnerArrayAdapter);

		cur_inn = 0;

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if (cur_inn != i) {
					Log.d("Debug", "On Selected Item called");
					int index = getSupportActionBar()
							.getSelectedNavigationIndex();
					saveInfo(index);
					cur_inn = i;
					viewInfo(index);
					Log.d("Debug", "On Selected Item finished");
				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				return;
			}
		});

		Uri uri = Uri
				.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/" + row_id);

		Cursor c = getContentResolver()
				.query(uri,
						new String[] { PerformanceDb.KEY_ROWID, PerformanceDb.KEY_BAT_NUM, PerformanceDb.KEY_BAT_RUNS, PerformanceDb.KEY_BAT_BALLS, PerformanceDb.KEY_BAT_TIME, PerformanceDb.KEY_BAT_HOW_OUT, PerformanceDb.KEY_BAT_BOWLER_TYPE, PerformanceDb.KEY_BOWL_OVERS, PerformanceDb.KEY_BOWL_MAIDENS, PerformanceDb.KEY_BOWL_RUNS, PerformanceDb.KEY_BOWL_WKTS_LEFT, PerformanceDb.KEY_BOWL_WKTS_RIGHT, PerformanceDb.KEY_BOWL_NOBALLS, PerformanceDb.KEY_BOWL_WIDES, PerformanceDb.KEY_FIELD_CLOSE_CATCH, PerformanceDb.KEY_FIELD_CIRCLE_CATCH, PerformanceDb.KEY_FIELD_DEEP_CATCH, PerformanceDb.KEY_FIELD_RO_CIRCLE, PerformanceDb.KEY_FIELD_RO_DIRECT, PerformanceDb.KEY_FIELD_RO_DEEP, PerformanceDb.KEY_FIELD_STUMPINGS, PerformanceDb.KEY_FIELD_BYES },
						null, null, PerformanceDb.KEY_INNING);
		if (c.getCount() != 0) {
			Log.d("Debug", "Displaying for db " + c.getCount());
			c.moveToFirst();
			for (int i = 1; i <= innings; i++) {
				bat_no[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_NUM));
				Log.d("Debug", "Bat No. " + bat_no[i - 1]);
				c.moveToNext();
			}
		}
		c.close();
		Log.d("Debug", "On Create finished");
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		Log.d("Debug", "On Restore Instance State called");
		cur_inn = savedInstanceState.getInt("cur_inn");

		bat_no = savedInstanceState.getIntArray("bat_no");
		bat_runs = savedInstanceState.getIntArray("bat_runs");
		bat_balls = savedInstanceState.getIntArray("bat_balls");
		bat_time = savedInstanceState.getIntArray("bat_time");
		how_out = savedInstanceState.getStringArray("how_out");
		bowler_type = savedInstanceState.getStringArray("bowler_type");

		overs = savedInstanceState.getIntArray("overs");
		maidens = savedInstanceState.getIntArray("maidens");
		bowl_runs = savedInstanceState.getIntArray("bowl_runs");
		wkts_left = savedInstanceState.getIntArray("wkts_left");
		wkts_right = savedInstanceState.getIntArray("wkts_right");
		noballs = savedInstanceState.getIntArray("noballs");
		wides = savedInstanceState.getIntArray("wides");

		close_catch = savedInstanceState.getIntArray("close_catch");
		circle_catch = savedInstanceState.getIntArray("circle_catch");
		deep_catch = savedInstanceState.getIntArray("deep_catch");
		circle_ro = savedInstanceState.getIntArray("circle_ro");
		direct_ro = savedInstanceState.getIntArray("direct_ro");
		deep_ro = savedInstanceState.getIntArray("deep_ro");
		stumpings = savedInstanceState.getIntArray("stumpings");
		byes = savedInstanceState.getIntArray("byes");

		((Spinner) findViewById(R.id.spnInning_no)).setSelection(cur_inn);
		viewInfo(getSupportActionBar().getSelectedNavigationIndex());
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
		Log.d("Debug", "On Restore Instance State finished");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		Log.d("Debug", "On Save Instance State called");
		int index = getSupportActionBar().getSelectedNavigationIndex();
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, index);

		outState.putInt("innings", innings);
		outState.putInt("rowId", row_id);

		outState.putInt("cur_inn", cur_inn);
		saveInfo(index);

		outState.putIntArray("bat_no", bat_no);
		outState.putIntArray("bat_runs", bat_runs);
		outState.putIntArray("bat_balls", bat_balls);
		outState.putIntArray("bat_time", bat_time);
		outState.putStringArray("how_out", how_out);
		outState.putStringArray("bowler_type", bowler_type);

		outState.putIntArray("overs", overs);
		outState.putIntArray("maidens", maidens);
		outState.putIntArray("bowl_runs", bowl_runs);
		outState.putIntArray("wkts_left", wkts_left);
		outState.putIntArray("wkts_right", wkts_right);
		outState.putIntArray("noballs", noballs);
		outState.putIntArray("wides", wides);

		outState.putIntArray("close_catch", close_catch);
		outState.putIntArray("circle_catch", circle_catch);
		outState.putIntArray("deep_catch", deep_catch);
		outState.putIntArray("circle_ro", circle_ro);
		outState.putIntArray("direct_ro", direct_ro);
		outState.putIntArray("deep_ro", deep_ro);
		outState.putIntArray("stumpings", stumpings);
		outState.putIntArray("byes", byes);
		Log.d("Debug", "On Save Instance finished");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		insertOrUpdate();
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the
		// container view.
		Log.d("Debug", "On Tab Selected called " + tab.getPosition());
		SherlockFragment fragment = new SectionFragment();
		Bundle args = new Bundle();
		args.putInt(SectionFragment.ARG_SECTION_NUMBER, tab.getPosition() + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		Log.d("Debug", "On Tab Selected finished");
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		Log.d("Debug", "On Tab Unselected called");
		Log.d("Debug", "Tab Position " + tab.getPosition() + "");
		saveInfo(tab.getPosition());
		Log.d("Debug", "On Tab Unselected finished");
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/** A dummy fragment representing a section of the app */

	public static class SectionFragment extends SherlockFragment {
		public static final String	ARG_SECTION_NUMBER	= "placeholder_text";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			Log.d("Debug", "On Create Fragment called");
			View view = null;
			switch (getArguments().getInt(ARG_SECTION_NUMBER)) {

				case 1:
					view = inflater.inflate(
							R.layout.activity_performance_insert_batting,
							container, false);
					break;
				case 2:
					view = inflater.inflate(
							R.layout.activity_performance_insert_bowling,
							container, false);
					break;
				case 3:
					view = inflater.inflate(
							R.layout.activity_performance_insert_fielding,
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
			((PerformanceInsertActivity) getActivity()).viewInfo(getArguments()
					.getInt(ARG_SECTION_NUMBER) - 1);
			Log.d("Debug", "On View Created finished");
		}

	}

	public void saveInfo(int id) {
		String str;
		Log.d("Debug", "Save Info " + id);
		switch (id) {
			case 0:
				str = ((EditText) findViewById(R.id.txtBatBattingNo)).getText()
						.toString();
				if (!str.equals("")) {
					bat_no[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtBatRuns)).getText()
						.toString();
				if (!str.equals("")) {
					bat_runs[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtBatBalls)).getText()
						.toString();
				if (!str.equals("")) {
					bat_balls[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtBatTimeSpent)).getText()
						.toString();
				if (!str.equals("")) {
					bat_time[cur_inn] = Integer.parseInt(str);
				}
				how_out[cur_inn] = ((Spinner) findViewById(R.id.spnBatHowOut))
						.getSelectedItem().toString();
				bowler_type[cur_inn] = ((Spinner) findViewById(R.id.spnBatBowlerType))
						.getSelectedItem().toString();
				break;
			case 1:
				str = ((EditText) findViewById(R.id.txtBowlOvers)).getText()
						.toString();
				if (!str.equals("")) {
					overs[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtBowlMaidens)).getText()
						.toString();
				if (!str.equals("")) {
					maidens[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtBowlRuns)).getText()
						.toString();
				if (!str.equals("")) {
					bowl_runs[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtBowlWicketsLeft))
						.getText().toString();
				if (!str.equals("")) {
					wkts_left[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtBowlWicketsRight))
						.getText().toString();
				if (!str.equals("")) {
					wkts_right[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtBowlNoballs)).getText()
						.toString();
				if (!str.equals("")) {
					noballs[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtBowlWides)).getText()
						.toString();
				if (!str.equals("")) {
					wides[cur_inn] = Integer.parseInt(str);
				}
				break;
			case 2:
				str = ((EditText) findViewById(R.id.txtFieldCloseCatches))
						.getText().toString();
				if (!str.equals("")) {
					close_catch[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtFieldCircleCatches))
						.getText().toString();
				if (!str.equals("")) {
					circle_catch[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtFieldDeepCatches))
						.getText().toString();
				if (!str.equals("")) {
					deep_catch[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtFieldCircleRunouts))
						.getText().toString();
				if (!str.equals("")) {
					circle_ro[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtFieldCircleDirectHitRunouts))
						.getText().toString();
				if (!str.equals("")) {
					direct_ro[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtFieldDeepRunouts))
						.getText().toString();
				if (!str.equals("")) {
					deep_ro[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtFieldStumpings))
						.getText().toString();
				if (!str.equals("")) {
					stumpings[cur_inn] = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtFieldByesGiven))
						.getText().toString();
				if (!str.equals("")) {
					byes[cur_inn] = Integer.parseInt(str);
				}
				break;
			default:
				break;
		}
		Log.d("Debug", "Save Info finished");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void viewInfo(int tab_index) {
		Log.d("Debug", "On View Info called");
		switch (tab_index) {
			case 0:
				((EditText) findViewById(R.id.txtBatBattingNo))
						.setText(bat_no[cur_inn] + "");
				((EditText) findViewById(R.id.txtBatRuns))
						.setText(bat_runs[cur_inn] + "");
				((EditText) findViewById(R.id.txtBatBalls))
						.setText(bat_balls[cur_inn] + "");
				((EditText) findViewById(R.id.txtBatTimeSpent))
						.setText(bat_time[cur_inn] + "");
				Spinner spinner = ((Spinner) findViewById(R.id.spnBatHowOut));
				ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
				int spinnerPosition = myAdap.getPosition(how_out[cur_inn]);
				spinner.setSelection(spinnerPosition);
				spinner = ((Spinner) findViewById(R.id.spnBatBowlerType));
				myAdap = (ArrayAdapter) spinner.getAdapter();
				spinnerPosition = myAdap.getPosition(bowler_type[cur_inn]);
				spinner.setSelection(spinnerPosition);
				break;
			case 1:
				((EditText) findViewById(R.id.txtBowlOvers))
						.setText(overs[cur_inn] + "");
				((EditText) findViewById(R.id.txtBowlMaidens))
						.setText(maidens[cur_inn] + "");
				((EditText) findViewById(R.id.txtBowlRuns))
						.setText(bowl_runs[cur_inn] + "");
				((EditText) findViewById(R.id.txtBowlWicketsLeft))
						.setText(wkts_left[cur_inn] + "");
				((EditText) findViewById(R.id.txtBowlWicketsRight))
						.setText(wkts_right[cur_inn] + "");
				((EditText) findViewById(R.id.txtBowlNoballs))
						.setText(noballs[cur_inn] + "");
				((EditText) findViewById(R.id.txtBowlWides))
						.setText(wides[cur_inn] + "");
				break;
			case 2:
				((EditText) findViewById(R.id.txtFieldCloseCatches))
						.setText(close_catch[cur_inn] + "");
				((EditText) findViewById(R.id.txtFieldCircleCatches))
						.setText(circle_catch[cur_inn] + "");
				((EditText) findViewById(R.id.txtFieldDeepCatches))
						.setText(deep_catch[cur_inn] + "");
				((EditText) findViewById(R.id.txtFieldCircleRunouts))
						.setText(circle_ro[cur_inn] + "");
				((EditText) findViewById(R.id.txtFieldCircleDirectHitRunouts))
						.setText(direct_ro[cur_inn] + "");
				((EditText) findViewById(R.id.txtFieldDeepRunouts))
						.setText(deep_ro[cur_inn] + "");
				((EditText) findViewById(R.id.txtFieldStumpings))
						.setText(stumpings[cur_inn] + "");
				((EditText) findViewById(R.id.txtFieldByesGiven))
						.setText(byes[cur_inn] + "");
				break;
			default:
				break;
		}
		Log.d("Debug", "On View info Finished");
	}

	public void insertOrUpdate() {
		saveInfo(getSupportActionBar().getSelectedNavigationIndex());
		Uri uri = Uri
				.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/" + row_id);
		Cursor c = getContentResolver().query(uri,
				new String[] { PerformanceDb.KEY_ROWID }, null, null, null);
		ContentValues[] values = new ContentValues[2];
		for (int i = 1; i <= innings; i++) {
			values[i - 1] = new ContentValues();

			values[i - 1].put(PerformanceDb.KEY_ROWID, row_id);
			values[i - 1].put(PerformanceDb.KEY_INNING, i);

			values[i - 1].put(PerformanceDb.KEY_BAT_NUM, bat_no[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_RUNS, bat_runs[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_BALLS, bat_balls[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_TIME, bat_time[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_HOW_OUT, how_out[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_BOWLER_TYPE,
					bowler_type[i - 1]);

			values[i - 1].put(PerformanceDb.KEY_BOWL_OVERS, overs[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_MAIDENS, maidens[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_RUNS, bowl_runs[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_WKTS_LEFT,
					wkts_left[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_WKTS_RIGHT,
					wkts_right[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_WKTS_RIGHT,
					wkts_right[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_NOBALLS, noballs[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_WIDES, wides[i - 1]);

			values[i - 1].put(PerformanceDb.KEY_FIELD_CLOSE_CATCH,
					close_catch[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
					circle_catch[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_DEEP_CATCH,
					deep_catch[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_RO_CIRCLE,
					circle_ro[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_RO_DIRECT,
					direct_ro[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_RO_DEEP, deep_ro[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_STUMPINGS,
					stumpings[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_BYES, byes[i - 1]);
		}

		if (c.getCount() == 0) {
			c.close();
			// not found in database
			for (int i = 1; i <= innings; i++) {

				// insert a record
				getContentResolver().insert(
						CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE,
						values[i - 1]);
			}
			Toast.makeText(this, "Performance Saved", Toast.LENGTH_LONG).show();
		} else {
			c.close();
			for (int i = 1; i <= innings; i++) {

				// update a record
				getContentResolver().update(uri, values[i - 1], null, null);
			}
			Toast.makeText(this, "Performed Updated", Toast.LENGTH_LONG).show();
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnPerformanceSave:
				insertOrUpdate();
				finish();
				break;
			default:
				break;
		}
	}
}
