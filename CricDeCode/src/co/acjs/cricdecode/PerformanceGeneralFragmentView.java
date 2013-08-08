package co.acjs.cricdecode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceGeneralFragmentView extends SherlockFragment {
	static PerformanceGeneralFragmentView performanceGeneralFragmentView;

	TextView match_review, match_result;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		performanceGeneralFragmentView = this;
		View view = inflater.inflate(R.layout.performance_general_view,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((PerformanceFragmentView) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.GENERAL);

	}

	public void init(View view) {
		match_review = (TextView) view.findViewById(R.id.match_review);
		match_result = (TextView) view.findViewById(R.id.match_result);
	}
}
