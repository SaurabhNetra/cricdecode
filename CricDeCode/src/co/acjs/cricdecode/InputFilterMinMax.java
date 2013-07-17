package co.acjs.cricdecode;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {

	private int	min, max;

	public InputFilterMinMax(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public InputFilterMinMax(String min, String max) {
		this.min = Integer.parseInt(min);
		this.max = Integer.parseInt(max);
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
		try {
			String textToCheck = dest.subSequence(0, dstart).toString() + source
					.subSequence(start, end) + dest.subSequence(dend,
					dest.length()).toString();
			int input = Integer.parseInt(textToCheck);
			if (isInRange(min, max, input)) return null;
		} catch (NumberFormatException nfe) {
		}
		return "";
	}

	private boolean isInRange(int a, int b, int c) {
		return b > a ? c >= a && c <= b : c >= b && c <= a;
	}
}
