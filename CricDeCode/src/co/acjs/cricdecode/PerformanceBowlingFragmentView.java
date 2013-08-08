package co.acjs.cricdecode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceBowlingFragmentView extends SherlockFragment {
	static PerformanceBowlingFragmentView performanceBowlingFragmentView;

	TextView overs, spells, maidens, runs, fours, sixes, wkts_left, wkts_right,
			catches_dropped, noballs, wides;

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
		((PerformanceFragmentView) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.BOWLING);
	}

	public void init(View view) {
		overs = (TextView) view.findViewById(R.id.overs);
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
