package co.acjs.cricdecode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;


public class PerformanceGeneralFragmentView extends SherlockFragment{
	static PerformanceGeneralFragmentView	performanceGeneralFragmentView;
	TextView								match_review, match_result, my_team, opponent_team, venue, day, month, year, level, match_overs, duration, first;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		performanceGeneralFragmentView = this;
		View view = inflater.inflate(R.layout.performance_general_view, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((PerformanceFragmentView)getParentFragment()).viewInfo(PerformanceFragmentEdit.GENERAL);
	}

	public void init(View view){
		day = (TextView)view.findViewById(R.id.day);
		month = (TextView)view.findViewById(R.id.month);
		year = (TextView)view.findViewById(R.id.year);
		my_team = (TextView)view.findViewById(R.id.my_team);
		opponent_team = (TextView)view.findViewById(R.id.opponent_team);
		venue = (TextView)view.findViewById(R.id.venue);
		level = (TextView)view.findViewById(R.id.level);
		match_overs = (TextView)view.findViewById(R.id.overs);
		duration = (TextView)view.findViewById(R.id.duration);
		first = (TextView)view.findViewById(R.id.first_action);
		match_review = (TextView)view.findViewById(R.id.match_review);
		match_result = (TextView)view.findViewById(R.id.match_result);
	}
}
