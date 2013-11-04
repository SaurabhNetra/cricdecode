package co.acjs.cricdecode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;


public class OversInputFilter implements InputFilter{
	Pattern	mPattern;

	public OversInputFilter(){
		mPattern = Pattern.compile("[0-9]+\\.?[0-5]?");
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
		String textToCheck = dest.subSequence(0, dstart).toString() + source.subSequence(start, end) + dest.subSequence(dend, dest.length()).toString();
		Log.d("Debug", textToCheck);
		Matcher matcher = mPattern.matcher(textToCheck);
		if(!matcher.matches()) return "";
		return null;
	}
}
