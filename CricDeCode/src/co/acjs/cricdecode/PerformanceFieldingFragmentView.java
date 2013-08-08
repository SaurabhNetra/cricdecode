package co.acjs.cricdecode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceFieldingFragmentView extends SherlockFragment {
	static PerformanceFieldingFragmentView performanceFieldingFragmentView;

	TextView slip_catches, close_catches, circle_catches, deep_catches,
			circle_runouts, circle_runouts_direct, deep_runouts,
			deep_runouts_direct, stumpings, byes, misfields, catches_dropped;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		performanceFieldingFragmentView = this;
		View view = inflater.inflate(R.layout.performance_fielding_view,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((PerformanceFragmentView) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.FIELDING);
	}

	public void init(View view) {
		slip_catches = (TextView) view.findViewById(R.id.slip_catches);
		close_catches = (TextView) view.findViewById(R.id.close_catches);
		circle_catches = (TextView) view.findViewById(R.id.circle_catches);
		deep_catches = (TextView) view.findViewById(R.id.deep_catches);
		circle_runouts = (TextView) view.findViewById(R.id.circle_runouts);
		circle_runouts_direct = (TextView) view
				.findViewById(R.id.circle_runouts_direct);
		deep_runouts = (TextView) view.findViewById(R.id.deep_runouts);
		deep_runouts_direct = (TextView) view
				.findViewById(R.id.deep_runouts_direct);
		stumpings = (TextView) view.findViewById(R.id.stumpings);
		byes = (TextView) view.findViewById(R.id.byes);
		misfields = (TextView) view.findViewById(R.id.misfields);
		catches_dropped = (TextView) view
				.findViewById(R.id.catches_dropped_fielding);
	}
}
