package co.acjs.cricdecode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceBattingFragmentView extends SherlockFragment {
	static PerformanceBattingFragmentView performanceBattingFragmentView;

	TextView batting_no, runs, balls, time_spent, fours, sixes, lives, how_out,
			lbl_bowler_type, bowler_type, lbl_fielding_pos, fielding_pos;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		performanceBattingFragmentView = this;
		View view = inflater.inflate(R.layout.performance_batting_view,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((PerformanceFragmentView) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.BATTING);
	}

	public void init(View view) {
		batting_no = (TextView) view.findViewById(R.id.batting_no);
		runs = (TextView) view.findViewById(R.id.runs);
		balls = (TextView) view.findViewById(R.id.balls);
		time_spent = (TextView) view.findViewById(R.id.time_spent);
		fours = (TextView) view.findViewById(R.id.fours);
		sixes = (TextView) view.findViewById(R.id.sixes);
		lives = (TextView) view.findViewById(R.id.lives);
		how_out = (TextView) view.findViewById(R.id.how_out);
		lbl_bowler_type = (TextView) view.findViewById(R.id.lbl_bowler_type);
		bowler_type = (TextView) view.findViewById(R.id.bowler_type);
		lbl_fielding_pos = (TextView) view.findViewById(R.id.lbl_fielding_pos);
		fielding_pos = (TextView) view.findViewById(R.id.fielding_pos);
	}
}
