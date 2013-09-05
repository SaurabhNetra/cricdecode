package co.acjs.cricdecode;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gcm.GCMRegistrar;

public class ChooseAccountActivity extends SherlockFragmentActivity {
	RadioGroup	account_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!ProfileData.mPrefs.getString("email", "").equals("")) {
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			finish();
		}
		setContentView(R.layout.activity_choose_account);
		account_list = (RadioGroup) findViewById(R.id.account_list);

		AccountManager manager = (AccountManager) MainActivity.main_context
				.getSystemService(Context.ACCOUNT_SERVICE);
		Account[] list = manager.getAccounts();

		int i = 1;
		for (Account account : list) {
			RadioButton rb = new RadioButton(this);
			rb.setId(i);
			rb.setText(account.toString());
			account_list.addView(rb, i);
			i++;
		}

	}

	public void onClick(View v) {
		RadioButton selected_account = (RadioButton) findViewById(account_list
				.getCheckedRadioButtonId());
		String em = selected_account.getText().toString();
		
		ProfileData.setTempEmail(this, em);
		callGCMRegistration();
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
		finish();

	}

	public void callGCMRegistration() {

		if (GCMRegistrar.getRegistrationId(getApplicationContext()).equals("")) {
			GCMRegistrar.register(getApplicationContext(), getResources()
					.getString(R.string.projno));
			Log.w(GCMRegistrar.getRegistrationId(getApplicationContext()),
					"Registration id");
		}

		else {
			finish();
		}

	}

}
