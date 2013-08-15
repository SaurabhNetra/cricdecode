package co.acjs.cricdecode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class CareerBowlingFragment extends SherlockFragment {
	static CareerBowlingFragment careerBowlingFragment;

	TextView innings, overs, spells, runs, maidens, wickets, wickets_left,
			wickets_right, catches_dropped, eco_rate, str_rate, avg, fwi, twm,
			bbi, bbm, fours, sixes, noballs, wides;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		careerBowlingFragment = this;
		View view = inflater.inflate(R.layout.career_bowling, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		Log.d("Debug", "Career Bowling Fragment Created");
		((CareerFragment) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.BOWLING);

	}

	public void init(View view) {
		innings = (TextView) view.findViewById(R.id.innings);
		overs = (TextView) view.findViewById(R.id.overs);
		spells = (TextView) view.findViewById(R.id.spells);
		runs = (TextView) view.findViewById(R.id.runs);
		maidens = (TextView) view.findViewById(R.id.maidens);
		wickets = (TextView) view.findViewById(R.id.wickets);
		wickets_left = (TextView) view.findViewById(R.id.wickets_left);
		wickets_right = (TextView) view.findViewById(R.id.wickets_right);
		catches_dropped = (TextView) view
				.findViewById(R.id.catches_dropped_bowling);
		eco_rate = (TextView) view.findViewById(R.id.economy_rate);
		str_rate = (TextView) view.findViewById(R.id.strike_rate);
		avg = (TextView) view.findViewById(R.id.average);
		fwi = (TextView) view.findViewById(R.id.fwi);
		twm = (TextView) view.findViewById(R.id.twm);
		bbi = (TextView) view.findViewById(R.id.bbi);
		bbm = (TextView) view.findViewById(R.id.bbm);
		fours = (TextView) view.findViewById(R.id.fours);
		sixes = (TextView) view.findViewById(R.id.sixes);
		noballs = (TextView) view.findViewById(R.id.noballs);
		wides = (TextView) view.findViewById(R.id.wides);
	}
}
