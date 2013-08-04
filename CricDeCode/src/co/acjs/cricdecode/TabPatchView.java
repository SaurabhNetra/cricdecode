package co.acjs.cricdecode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TabHost;

public class TabPatchView extends View {

	public TabPatchView(Context context) {
		super(context);
	}

	public TabPatchView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TabPatchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		final TabHost tabhost = (TabHost) getRootView().findViewById(
				android.R.id.tabhost);
		tabhost.getViewTreeObserver().removeOnTouchModeChangeListener(tabhost);
	}
}