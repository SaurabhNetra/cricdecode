package co.acjs.cricdecode;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class PerformanceInsertActivity extends SherlockFragmentActivity implements ActionBar.TabListener {

	private static final String	STATE_SELECTED_NAVIGATION_ITEM	= "selected_navigation_item";

	private int					innings;

	// Fields

	// Batting
	private static int			bat_no, bat_runs, bat_balls, bat_time;
	private static String		how_out, bowler_type;

	// Bowling
	private static int			overs, maidens, bowl_runs, wkts_left,
			wkts_right, noballs, wides;

	// Fielding
	private static int			close_catch, circle_catch, deep_catch,
			circle_ro, direct_ro, deep_ro, stumpings, byes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.element_container);

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
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		int index = getSupportActionBar().getSelectedNavigationIndex();
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, index);
		saveInfo(index);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		saveInfo(getSupportActionBar().getSelectedNavigationIndex());
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the
		// container view.
		SherlockFragment fragment = new SectionFragment();
		Bundle args = new Bundle();
		args.putInt(SectionFragment.ARG_SECTION_NUMBER, tab.getPosition() + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		switch (tab.getPosition()) {
			case 0:
				saveInfo(0);
				break;
			case 1:
				saveInfo(1);
				break;
			case 2:
				saveInfo(2);
				break;
			default:
				break;
		}
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/** A dummy fragment representing a section of the app */

	public static class SectionFragment extends SherlockFragment {
		public static final String	ARG_SECTION_NUMBER	= "placeholder_text";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
			return view;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			switch (getArguments().getInt(ARG_SECTION_NUMBER)) {

				case 1:
					((EditText) getActivity()
							.findViewById(R.id.txtBatBattingNo))
							.setText(bat_no + "");
					((EditText) getActivity().findViewById(R.id.txtBatRuns))
							.setText(bat_runs + "");
					((EditText) getActivity().findViewById(R.id.txtBatBalls))
							.setText(bat_balls + "");
					((EditText) getActivity()
							.findViewById(R.id.txtBatTimeSpent))
							.setText(bat_time + "");
					Spinner spinner = ((Spinner) getActivity().findViewById(
							R.id.spnBatHowOut));
					ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
					int spinnerPosition = myAdap.getPosition(how_out);
					spinner.setSelection(spinnerPosition);
					spinner = ((Spinner) getActivity().findViewById(
							R.id.spnBatBowlerType));
					myAdap = (ArrayAdapter) spinner.getAdapter();
					spinnerPosition = myAdap.getPosition(bowler_type);
					spinner.setSelection(spinnerPosition);

					break;
				case 2:

					break;
				case 3:

					break;
				default:
					break;
			}
		}

	}

	public void saveInfo(int id) {
		switch (id) {
			case 0:
				String str = ((EditText) findViewById(R.id.txtBatBattingNo))
						.getText().toString();
				if (!str.equals("")) {
					bat_no = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtBatRuns)).getText()
						.toString();
				if (!str.equals("")) {
					bat_runs = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtBatBalls)).getText()
						.toString();
				if (!str.equals("")) {
					bat_balls = Integer.parseInt(str);
				}
				str = ((EditText) findViewById(R.id.txtBatTimeSpent)).getText()
						.toString();
				if (!str.equals("")) {
					bat_time = Integer.parseInt(str);
				}
				how_out = ((Spinner) findViewById(R.id.spnBatHowOut))
						.getSelectedItem().toString();
				bowler_type = ((Spinner) findViewById(R.id.spnBatBowlerType))
						.getSelectedItem().toString();
				break;
			case 1:
				break;
			case 2:
				break;
			default:
				break;
		}
	}
}
