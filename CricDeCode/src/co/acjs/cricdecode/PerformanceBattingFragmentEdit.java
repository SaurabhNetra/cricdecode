package co.acjs.cricdecode;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceBattingFragmentEdit extends SherlockFragment {
	static PerformanceBattingFragmentEdit performanceBattingFragmentEdit;

	EditText batting_no, runs, balls, time_spent, fours, sixes, lives;
	Spinner how_out, bowler_type, fielding_pos;
	ToggleButton bat_toggle;
	TableLayout batting_info;

	TextView my_team, opponent_team, venue, day, month, year, level,
			match_overs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		performanceBattingFragmentEdit = this;
		View view = inflater.inflate(R.layout.performance_batting_edit,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((PerformanceFragmentEdit) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.BATTING);

		// Set inputFilters
		batting_no.setFilters(new InputFilter[] { new InputFilterMinMax("1",
				"11") });

		bat_toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Log.d("Debug", "On Check Changed " + isChecked);
				if (isChecked) {
					batting_info.setVisibility(View.VISIBLE);
				} else {
					batting_info.setVisibility(View.GONE);
					how_out.setSelection(0);
					balls.setText("0");
				}
			}
		});

		how_out.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int i, long l) {
				String str = how_out.getSelectedItem().toString();
				if (str.equals("Not Out") || str.equals("Retired")
						|| str.equals("Timed Out")
						|| str.equals("Obstructing the Field")
						|| str.equals("Run Out")) {
					bowler_type.setEnabled(false);
					// bowler_type.setVisibility(View.INVISIBLE);
				} else {
					bowler_type.setEnabled(true);
					// bowler_type.setVisibility(View.VISIBLE);
				}
				if (str.equals("Caught") || str.equals("Run Out")) {
					fielding_pos.setEnabled(true);
					// fielding_pos.setVisibility(View.VISIBLE);
				} else {
					fielding_pos.setEnabled(false);
					// fielding_pos.setVisibility(View.INVISIBLE);

				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				return;
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

		batting_info = (TableLayout) view.findViewById(R.id.batting_info);
		batting_no = (EditText) view.findViewById(R.id.batting_no);
		batting_no.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (batting_no.getText().toString().equals("")) {
						batting_no.setText("1");
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
		balls = (EditText) view.findViewById(R.id.balls);
		balls.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (balls.getText().toString().equals("0")) {
						balls.setText("");
					}
				} else {
					if (balls.getText().toString().equals("")) {
						balls.setText("0");
					}
				}
			}
		});
		time_spent = (EditText) view.findViewById(R.id.time_spent);
		time_spent.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (time_spent.getText().toString().equals("0")) {
						time_spent.setText("");
					}
				} else {
					if (time_spent.getText().toString().equals("")) {
						time_spent.setText("0");
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
		lives = (EditText) view.findViewById(R.id.lives);
		lives.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (lives.getText().toString().equals("0")) {
						lives.setText("");
					}
				} else {
					if (lives.getText().toString().equals("")) {
						lives.setText("0");
					}
				}
			}
		});
		how_out = (Spinner) view.findViewById(R.id.how_out);
		bowler_type = (Spinner) view.findViewById(R.id.bowler_type);
		fielding_pos = (Spinner) view.findViewById(R.id.fielding_pos);

		bat_toggle = (ToggleButton) view.findViewById(R.id.bat_toggle);
	}

}
