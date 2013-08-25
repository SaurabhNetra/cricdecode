package co.acjs.cricdecode;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class DisplayPieChart extends SherlockFragmentActivity {
	/** Colors to be used for the pie slices. */
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,
			Color.MAGENTA, Color.CYAN, Color.RED, Color.YELLOW, Color.BLACK };
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The chart view that displays the data. */
	private GraphicalView mChartView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_graph);	
		getSupportActionBar().setDisplayShowTitleEnabled(false);		
		//mRenderer.setChartTitle(getIntent().getExtras().getString("X-Axis")+" vs "+getIntent().getExtras().getString("Y-Axis"));
		((TextView)findViewById(R.id.chart_title)).setText("How Out vs Opponents");
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(45);
		mRenderer.setFitLegend(true);		
		mRenderer.setMargins(new int[] { 2, 50, 20, 50 });
		mRenderer.setDisplayValues(true);
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
		layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		mChartView.repaint();
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
}