package co.acjs.cricdecode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class DatePickerFragment extends SherlockDialogFragment implements
		android.app.DatePickerDialog.OnDateSetListener {
	static DatePickerFragment datePickerFragment;
	private int view_callee;
	private String date_str;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year, month, day;

		if (savedInstanceState != null) {
			Log.d("Debug", "Date Picker Restored");
			date_str = savedInstanceState.getString("date_str");
			view_callee = savedInstanceState.getInt("view_callee");
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy",
				Locale.getDefault());
		try {
			c.setTime(sdf.parse(date_str));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Time chosenDate = new Time();
		chosenDate.set(dayOfMonth, monthOfYear, year);
		long dtDob = chosenDate.toMillis(true);
		TextView textView = ((TextView) getSherlockActivity().findViewById(
				view_callee));
		if (textView != null) {
			textView.setText(DateFormat.format("MMMM dd, yyyy", dtDob));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("date_str", date_str);
		savedInstanceState.putInt("view_callee", view_callee);
		Log.d("Debug", "Date Picker Save Instance");
	}

	public int getView_callee() {
		return view_callee;
	}

	public void setView_callee(int view_callee) {
		this.view_callee = view_callee;
	}

	public String getDate_str() {
		return date_str;
	}

	public void setDate_str(String date_str) {
		this.date_str = date_str;
	}
}
