package co.acjs.cricdecode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class PerformanceInsertActivity extends SherlockFragmentActivity implements ActionBar.TabListener {

	private static final String	STATE_SELECTED_NAVIGATION_ITEM	= "selected_navigation_item";

	private String				rowId;
	private int					innings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.element_container);

		// Set up the action bar to show tabs.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
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
		rowId = intent.getStringExtra("rowId");
		innings = intent.getIntExtra("innings", 0);

		Spinner spinner = (Spinner) findViewById(R.id.spnInning_no);
		ArrayAdapter<Integer> spinnerArrayAdapter;
		if (innings == 1) {
			spinnerArrayAdapter = new ArrayAdapter<Integer>(this,
					android.R.layout.simple_spinner_item, new Integer[] { 1 });
		} else {
			spinnerArrayAdapter = new ArrayAdapter<Integer>(this,
					android.R.layout.simple_spinner_item,
					new Integer[] { 1, 2 });
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
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
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
	}

}
