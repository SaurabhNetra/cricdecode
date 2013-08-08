package co.acjs.cricdecode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceGeneralFragmentEdit extends SherlockFragment {
	static PerformanceGeneralFragmentEdit performanceGeneralFragmentEdit;

	EditText match_review;
	Spinner match_result;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		performanceGeneralFragmentEdit = this;
		View view = inflater.inflate(R.layout.performance_general_edit,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((PerformanceFragmentEdit) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.GENERAL);

	}

	public void init(View view) {
		match_review = (EditText) view.findViewById(R.id.match_review);
		match_result = (Spinner) view.findViewById(R.id.match_result);
	}
}
