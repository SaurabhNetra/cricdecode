package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
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

public class DisplayLineChart extends SherlockFragmentActivity {
	private GraphicalView mChartView;
	XYMultipleSeriesRenderer renderer;
	String labels[];
	double values[];
	LinearLayout layout;
	public static InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_graph);
		// Ads

		if (!(AccessSharedPrefs.mPrefs.getString("ad_free", "no").equals("yes"))) {
			interstitial = new InterstitialAd(this);
			interstitial.setAdUnitId(getResources().getString(
					R.string.publisher_id));
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
		// ((TextView)findViewById(R.id.chart_title)).setText("How Out vs Opponents");
		labels = getIntent().getExtras().getStringArray("labels");
		values = getIntent().getExtras().getDoubleArray("values");
		int[] colors = new int[] { Color.GREEN };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
		renderer = buildRenderer(colors, styles);
		renderer.setMarginsColor(getResources().getColor(R.color.light_grey));
		renderer.setAxesColor(Color.BLACK);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
		((TextView) findViewById(R.id.cht_ttl)).setText(getIntent().getExtras()
				.getString("Y-Axis")
				+ " vs "
				+ getIntent().getExtras().getString("X-Axis"));
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
		}
		String[] titles = new String[] { "spens1" };
		List<double[]> x = new ArrayList<double[]>();
		x.add(values);
		List<double[]> y = new ArrayList<double[]>();
		double x1[] = new double[labels.length];
		int j = 0;
		for (double i = 1; i <= x1.length; i++) {
			x1[j] = i;
			j++;
		}
		Log.w("fgf", "" + Arrays.toString(x1));
		y.add(x1);
		for (int k = 0; k < values.length; k++) {
			renderer.addXTextLabel(k + 1, labels[k]);
		}
		if (mChartView == null) {
			mChartView = ChartFactory.getBarChartView(this,
					mDataset(titles, y, x), renderer, Type.DEFAULT);
			mChartView.setBackgroundColor(getResources().getColor(
					R.color.light_grey));
			layout = (LinearLayout) findViewById(R.id.chart);
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mChartView.repaint();
		} else {
			mChartView.repaint();
		}

		if (getResources().getIdentifier("config_enableTranslucentDecor",
				"bool", "android") != 0)
			makeBarsTranslucent(getWindow());

		com.google.android.gms.analytics.Tracker t = ((CDCAppClass) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName(getResources().getString(R.string.analyticsLineChart));
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
	public void onBackPressed() {

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

	private XYMultipleSeriesDataset mDataset(String[] titles,
			List<double[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
		addXYSeries(dataset1, titles, xValues, yValues, 0);
		return dataset1;
	}

	private void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles,
			List<double[]> xValues, List<double[]> yValues, int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			double[] xV = xValues.get(i);
			XYSeries series = new XYSeries(titles[i], scale);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
	}

	private XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}

	private void setRenderer(XYMultipleSeriesRenderer renderer2, int[] colors,
			PointStyle[] styles) {
		renderer2.setAxisTitleTextSize(16);
		renderer2.setLabelsTextSize(12);
		renderer2.setLegendTextSize(15);
		renderer2.setShowGridX(true);
		renderer2.setShowGridY(true);
		renderer2.setXLabelsAlign(Align.LEFT);
		renderer2.setYLabelsAlign(Align.RIGHT);
		renderer2.setYLabelsPadding(5);
		renderer2.setPanEnabled(true);
		renderer2.setZoomEnabled(true);
		renderer2.setShowLegend(false);
		renderer2.setXLabels(0);
		renderer2.setXLabelsAngle(45);
		renderer2.setZoomButtonsVisible(true);
		renderer2.setZoomEnabled(false, true);
		renderer2.setXAxisMin(-1 * 0.05);
		renderer2.setYAxisMin(0.0);
		renderer2.setBarSpacing(0.5);
		Log.w("YAxisMax", "" + renderer2.getYAxisMax());
		if (getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0) {
			renderer2.setXAxisMax(5);
			renderer2.setYLabels(7);
		} else {
			renderer2.setXAxisMax(8);
			renderer2.setYLabels(5);
		}
		renderer2.setMargins(new int[] { 5, 30, 20, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			renderer2.addSeriesRenderer(r);
		}
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
