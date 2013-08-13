package co.acjs.cricdecode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class CareerGeneralFragment extends SherlockFragment {
	static CareerGeneralFragment careerGeneralFragment;

	TextView matches, wins, losses, ties, no_results, win_per;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		careerGeneralFragment = this;
		View view = inflater.inflate(R.layout.career_general, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((CareerFragment) getParentFragment())
				.viewInfo(PerformanceFragmentEdit.GENERAL);

	}

	public void init(View view) {
		matches = (TextView) view.findViewById(R.id.matches);
		wins = (TextView) view.findViewById(R.id.wins);
		losses = (TextView) view.findViewById(R.id.losses);
		ties = (TextView) view.findViewById(R.id.ties);
		no_results = (TextView) view.findViewById(R.id.no_result);
		win_per = (TextView) view.findViewById(R.id.win_per);
	}
}
