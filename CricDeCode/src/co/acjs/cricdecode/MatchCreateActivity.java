package co.acjs.cricdecode;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MatchCreateActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_create);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.txtMatchDate:
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			// Create and show the dialog.
			DatePickerFragment newFragment = new DatePickerFragment();
			newFragment.setView_callee(findViewById(R.id.txtMatchDate));
			newFragment.show(ft, null);
			break;
		default:
			break;
		}
	}

}
