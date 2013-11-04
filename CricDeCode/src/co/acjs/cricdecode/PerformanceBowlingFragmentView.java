package co.acjs.cricdecode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceBowlingFragmentView extends SherlockFragment {
	static PerformanceBowlingFragmentView performanceBowlingFragmentView;

	TextView overs, spells, maidens, runs, fours, sixes, wkts_left, wkts_right,
			catches_dropped, noballs, wides, bowled;
	TextView my_team, opponent_team, venue, day, month, year, level,
			match_overs;
	TableLayout bowling_info;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		performanceBowlingFragmentView = this;
		View view = inflater.inflate(R.layout.performance_bowling_view,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((PerformanceFragmentView) getTargetFragment())
				.viewInfo(PerformanceFragmentEdit.BOWLING);
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

		bowling_info = (TableLayout) view.findViewById(R.id.bowling_info);
		bowled = (TextView) view.findViewById(R.id.bowled);
		overs = (TextView) view.findViewById(R.id.overs_bowled);
		spells = (TextView) view.findViewById(R.id.spells);
		maidens = (TextView) view.findViewById(R.id.maidens);
		runs = (TextView) view.findViewById(R.id.runs);
		fours = (TextView) view.findViewById(R.id.fours);
		sixes = (TextView) view.findViewById(R.id.sixes);
		wkts_left = (TextView) view.findViewById(R.id.wickets_left);
		wkts_right = (TextView) view.findViewById(R.id.wickets_right);
		catches_dropped = (TextView) view
				.findViewById(R.id.catches_dropped_bowling);
		noballs = (TextView) view.findViewById(R.id.noballs);
		wides = (TextView) view.findViewById(R.id.wides);

	}
}
