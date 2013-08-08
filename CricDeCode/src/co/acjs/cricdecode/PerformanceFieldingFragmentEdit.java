package co.acjs.cricdecode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceFieldingFragmentEdit extends SherlockFragment {
	static PerformanceFieldingFragmentEdit performanceFieldingFragmentEdit;

	EditText slip_catches, close_catches, circle_catches, deep_catches,
			circle_runouts, circle_runouts_direct, deep_runouts,
			deep_runouts_direct, stumpings, byes, misfields, catches_dropped;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		performanceFieldingFragmentEdit = this;
		View view = inflater.inflate(R.layout.performance_fielding_edit,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((PerformanceFragmentEdit) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.FIELDING);
	}

	public void init(View view) {
		slip_catches = (EditText) view.findViewById(R.id.slip_catches);
		close_catches = (EditText) view.findViewById(R.id.close_catches);
		circle_catches = (EditText) view.findViewById(R.id.circle_catches);
		deep_catches = (EditText) view.findViewById(R.id.deep_catches);
		circle_runouts = (EditText) view.findViewById(R.id.circle_runouts);
		circle_runouts_direct = (EditText) view
				.findViewById(R.id.circle_runouts_direct);
		deep_runouts = (EditText) view.findViewById(R.id.deep_runouts);
		deep_runouts_direct = (EditText) view
				.findViewById(R.id.deep_runouts_direct);
		stumpings = (EditText) view.findViewById(R.id.stumpings);
		byes = (EditText) view.findViewById(R.id.byes);
		misfields = (EditText) view.findViewById(R.id.misfields);
		catches_dropped = (EditText) view
				.findViewById(R.id.catches_dropped_fielding);
	}
}
