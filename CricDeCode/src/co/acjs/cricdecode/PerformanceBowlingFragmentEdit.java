package co.acjs.cricdecode;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceBowlingFragmentEdit extends SherlockFragment {
	static PerformanceBowlingFragmentEdit performanceBowlingFragmentEdit;

	EditText overs, spells, maidens, runs, fours, sixes, wkts_left, wkts_right,
			catches_dropped, noballs, wides;
	ToggleButton bowl_toggle;
	TableLayout bowling_info;
	TextView my_team, opponent_team, venue, day, month, year, level,
			match_overs, toggle_on, toggle_off;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		performanceBowlingFragmentEdit = this;
		View view = inflater.inflate(R.layout.performance_bowling_edit,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((PerformanceFragmentEdit) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.BOWLING);
		// Set Input Filter
		overs.setFilters(new InputFilter[] { new OversInputFilter() });

		bowl_toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					bowling_info.setVisibility(View.VISIBLE);
					toggle_off.setBackgroundColor(getResources().getColor(
							R.color.light_grey));
					toggle_off.setTextColor(getResources().getColor(
							R.color.light_grey));
					toggle_on.setTextColor(getResources().getColor(
							R.color.dark_grey));
					toggle_on.setBackgroundColor(getResources().getColor(
							R.color.green));
				} else {
					bowling_info.setVisibility(View.GONE);
					overs.setText("0");
					toggle_off.setBackgroundColor(getResources().getColor(
							R.color.dark_grey));
					toggle_off.setTextColor(getResources().getColor(
							R.color.light_grey));
					toggle_on.setTextColor(getResources().getColor(
							R.color.light_grey));
					toggle_on.setBackgroundColor(getResources().getColor(
							R.color.light_grey));
				}
			}
		});
	}

	public void init(View view) {
		day = (TextView) view.findViewById(R.id.day);
		month = (TextView) view.findViewById(R.id.month);
		year = (TextView) view.findViewById(R.id.year);
		my_team = (TextView) view.findViewById(R.id.my_team);
		opponent_team = (TextView) view.findViewById(R.id.opponent_team);
		venue = (TextView) view.findViewById(R.id.venue);
		level = (TextView) view.findViewById(R.id.level);
		match_overs = (TextView) view.findViewById(R.id.overs);

		toggle_on = (TextView) view.findViewById(R.id.toggle_on);
		toggle_off = (TextView) view.findViewById(R.id.toggle_off);
		bowling_info = (TableLayout) view.findViewById(R.id.bowling_info);
		overs = (EditText) view.findViewById(R.id.overs_bowled);
		overs.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (Float.parseFloat(overs.getText().toString()) == 0) {
						overs.setText("");
					}
				} else {
					if (overs.getText().toString().equals("")) {
						overs.setText("0.0");
					}
				}
			}
		});
		spells = (EditText) view.findViewById(R.id.spells);
		spells.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (spells.getText().toString().equals("0")) {
						spells.setText("");
					}
				} else {
					if (spells.getText().toString().equals("")) {
						spells.setText("0");
					}
				}
			}
		});
		maidens = (EditText) view.findViewById(R.id.maidens);
		maidens.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (maidens.getText().toString().equals("0")) {
						maidens.setText("");
					}
				} else {
					if (maidens.getText().toString().equals("")) {
						maidens.setText("0");
					}
				}
			}
		});
		runs = (EditText) view.findViewById(R.id.runs);
		runs.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (runs.getText().toString().equals("0")) {
						runs.setText("");
					}
				} else {
					if (runs.getText().toString().equals("")) {
						runs.setText("0");
					}
				}
			}
		});
		fours = (EditText) view.findViewById(R.id.fours);
		fours.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (fours.getText().toString().equals("0")) {
						fours.setText("");
					}
				} else {
					if (fours.getText().toString().equals("")) {
						fours.setText("0");
					}
				}
			}
		});
		sixes = (EditText) view.findViewById(R.id.sixes);
		sixes.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (sixes.getText().toString().equals("0")) {
						sixes.setText("");
					}
				} else {
					if (sixes.getText().toString().equals("")) {
						sixes.setText("0");
					}
				}
			}
		});
		wkts_left = (EditText) view.findViewById(R.id.wickets_left);
		wkts_left.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (wkts_left.getText().toString().equals("0")) {
						wkts_left.setText("");
					}
				} else {
					if (wkts_left.getText().toString().equals("")) {
						wkts_left.setText("0");
					}
				}
			}
		});
		wkts_right = (EditText) view.findViewById(R.id.wickets_right);
		wkts_right.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (wkts_right.getText().toString().equals("0")) {
						wkts_right.setText("");
					}
				} else {
					if (wkts_right.getText().toString().equals("")) {
						wkts_right.setText("0");
					}
				}
			}
		});
		catches_dropped = (EditText) view
				.findViewById(R.id.catches_dropped_bowling);
		catches_dropped.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (catches_dropped.getText().toString().equals("0")) {
						catches_dropped.setText("");
					}
				} else {
					if (catches_dropped.getText().toString().equals("")) {
						catches_dropped.setText("0");
					}
				}
			}
		});
		noballs = (EditText) view.findViewById(R.id.noballs);
		noballs.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (noballs.getText().toString().equals("0")) {
						noballs.setText("");
					}
				} else {
					if (noballs.getText().toString().equals("")) {
						noballs.setText("0");
					}
				}
			}
		});
		wides = (EditText) view.findViewById(R.id.wides);
		wides.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (wides.getText().toString().equals("0")) {
						wides.setText("");
					}
				} else {
					if (wides.getText().toString().equals("")) {
						wides.setText("0");
					}
				}
			}
		});
		bowl_toggle = (ToggleButton) view.findViewById(R.id.bowl_toggle);

	}
}
