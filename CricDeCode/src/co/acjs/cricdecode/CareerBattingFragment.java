package co.acjs.cricdecode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class CareerBattingFragment extends SherlockFragment {
	static CareerBattingFragment careerBattingFragment;

	TextView innings, not_outs, runs, highest, avg, balls, str_rate, fifties,
			hundreds, time_spent, fours, sixes, lives;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		careerBattingFragment = this;
		View view = inflater.inflate(R.layout.career_batting, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d("Debug", "Career Batting Fragment Created");
		init(view);
		((CareerFragment) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.BATTING);

	}

	public void init(View view) {
		innings = (TextView) view.findViewById(R.id.innings);
		not_outs = (TextView) view.findViewById(R.id.not_outs);
		runs = (TextView) view.findViewById(R.id.runs);
		highest = (TextView) view.findViewById(R.id.highest);
		avg = (TextView) view.findViewById(R.id.average);
		balls = (TextView) view.findViewById(R.id.balls);
		str_rate = (TextView) view.findViewById(R.id.strike_rate);
		fifties = (TextView) view.findViewById(R.id.fifties);
		hundreds = (TextView) view.findViewById(R.id.hundreds);
		time_spent = (TextView) view.findViewById(R.id.time_spent);
		fours = (TextView) view.findViewById(R.id.fours);
		sixes = (TextView) view.findViewById(R.id.sixes);
		lives = (TextView) view.findViewById(R.id.lives);
	}
}
