package co.acjs.cricdecode;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceBowlingFragmentEdit extends SherlockFragment {
	static PerformanceBowlingFragmentEdit performanceBowlingFragmentEdit;

	EditText overs, spells, maidens, runs, fours, sixes, wkts_left, wkts_right,
			catches_dropped, noballs, wides;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		performanceBowlingFragmentEdit = this;
		View view = inflater.inflate(R.layout.performance_bowling_edit,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((PerformanceFragmentEdit) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.BOWLING);
		// Set Input Filter
		overs.setFilters(new InputFilter[] { new OversInputFilter() });
	}

	public void init(View view) {
		overs = (EditText) view.findViewById(R.id.overs);
		spells = (EditText) view.findViewById(R.id.spells);
		maidens = (EditText) view.findViewById(R.id.maidens);
		runs = (EditText) view.findViewById(R.id.runs);
		fours = (EditText) view.findViewById(R.id.fours);
		sixes = (EditText) view.findViewById(R.id.sixes);
		wkts_left = (EditText) view.findViewById(R.id.wickets_left);
		wkts_right = (EditText) view.findViewById(R.id.wickets_right);
		catches_dropped = (EditText) view
				.findViewById(R.id.catches_dropped_bowling);
		noballs = (EditText) view.findViewById(R.id.noballs);
		wides = (EditText) view.findViewById(R.id.wides);

	}
}
