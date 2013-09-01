package co.acjs.cricdecode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceGeneralFragmentEdit extends SherlockFragment {
	static PerformanceGeneralFragmentEdit performanceGeneralFragmentEdit;

	EditText match_review;
	TextView my_team, opponent_team, venue, day, month, year, level,
			match_overs;
	Spinner match_result, duration, first;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		performanceGeneralFragmentEdit = this;
		View view = inflater.inflate(R.layout.performance_general_edit,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((PerformanceFragmentEdit) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.GENERAL);

	}

	public void init(View view) {
		day = (TextView) view.findViewById(R.id.day);
		month = (TextView) view.findViewById(R.id.month);
		year = (TextView) view.findViewById(R.id.year);
		my_team = (TextView) view.findViewById(R.id.my_team);
		opponent_team = (TextView) view.findViewById(R.id.opponent_team);
		venue = (TextView) view.findViewById(R.id.venue);
		level = (TextView) view.findViewById(R.id.level);
		match_review = (EditText) view.findViewById(R.id.match_review);
		match_result = (Spinner) view.findViewById(R.id.match_result);
		duration = (Spinner) view.findViewById(R.id.duration);
		first = (Spinner) view.findViewById(R.id.first_action);
		match_overs = (TextView) view.findViewById(R.id.overs);
	}
}
