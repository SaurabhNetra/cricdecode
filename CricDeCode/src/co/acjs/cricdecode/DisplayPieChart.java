package co.acjs.cricdecode;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.acjs.cricdecode.CDCAppClass.TrackerName;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;

public class DisplayPieChart extends SherlockFragmentActivity {
	/** Colors to be used for the pie slices. */
	private static int[] COLORS = new int[] { Color.BLUE, Color.YELLOW,
			Color.MAGENTA, Color.CYAN, Color.RED, Color.GREEN, Color.WHITE };
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The chart view that displays the data. */
	private GraphicalView mChartView;
	public static InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_graph);
		// Ads			
				
				if (!(AccessSharedPrefs.mPrefs.getString("ad_free", "no")
						.equals("yes")))
				{
					interstitial = new InterstitialAd(this);
					interstitial.setAdUnitId(getResources()
							.getString(R.string.publisher_id));
					AdRequest adRequest = new AdRequest.Builder().build();
					interstitial.loadAd(adRequest);
					
				}	
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.action_bar_others, null);
		actionBar.setCustomView(view);
		((TextView) findViewById(R.id.cht_ttl)).setText(getIntent().getExtras()
				.getString("Y-Axis"));
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(45);
		mRenderer.setFitLegend(true);
		mRenderer.setMargins(new int[] { 2, 50, 20, 50 });
		mRenderer.setDisplayValues(true);
		mRenderer.setAxesColor(Color.BLACK);
		mRenderer.setLabelsColor(Color.BLACK);
		String labels[] = getIntent().getExtras().getStringArray("labels");
		double values[] = getIntent().getExtras().getDoubleArray("values");
		Log.w("DisplayPieChart", "onCreate called");
		for (int i = 0; i < labels.length; i++) {
			mSeries.add(labels[i], values[i]);
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[i % COLORS.length]);
			mRenderer.addSeriesRenderer(renderer);
		}
		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
		mRenderer.setClickEnabled(true);
		mChartView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SeriesSelection seriesSelection = mChartView
						.getCurrentSeriesAndPoint();
				if (seriesSelection == null) {
				} else {
					for (int i = 0; i < mSeries.getItemCount(); i++) {
						Log.w("Not Null", "" + seriesSelection.getPointIndex());
						mRenderer.getSeriesRendererAt(i).setHighlighted(
								i == seriesSelection.getPointIndex());
					}
					mChartView.repaint();
				}
			}
		});
		layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mChartView.repaint();

		if (getResources().getIdentifier("config_enableTranslucentDecor",
				"bool", "android") != 0)
			makeBarsTranslucent(getWindow());
		com.google.android.gms.analytics.Tracker t = ((CDCAppClass) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName(getResources().getString(R.string.analyticsPieChart));
		t.send(new HitBuilders.AppViewBuilder().build());

	}
	
	public static void createAd() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}

	@TargetApi(19)
	private void makeBarsTranslucent(Window window) {
		findViewById(R.id.padding_top_frame).setVisibility(View.VISIBLE);

		if (!ViewConfiguration.get(getApplicationContext())
				.hasPermanentMenuKey()
				| !KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)) {
			int h = 0;
			findViewById(R.id.padding_bottom_frame).setVisibility(View.VISIBLE);
			int resourceId1 = getResources().getIdentifier(
					"navigation_bar_height", "dimen", "android");
			if (resourceId1 > 0) {
				h = getResources().getDimensionPixelSize(resourceId1);
			}
			FrameLayout paddingView = (FrameLayout) findViewById(R.id.padding_bottom_frame);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) paddingView
					.getLayoutParams();
			params.height = h;
			paddingView.setLayoutParams(params);
		}
		window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mSeries = (CategorySeries) savedState.getSerializable("current_series");
		mRenderer = (DefaultRenderer) savedState
				.getSerializable("current_renderer");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("current_series", mSeries);
		outState.putSerializable("current_renderer", mRenderer);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mChartView.repaint();
	}

	public void endMe(View v) {
		if (!(AccessSharedPrefs.mPrefs.getString("ad_free", "no")
				.equals("yes")))
			createAd();
		finish();
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
				super.onBackPressed();
				if (!(AccessSharedPrefs.mPrefs.getString("ad_free", "no")
						.equals("yes")))
					createAd();
				
		}
		return true;
}
}
