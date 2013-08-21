package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class DisplayLineChart extends SherlockFragmentActivity {
	private GraphicalView mChartView;
	XYMultipleSeriesRenderer renderer;
	String labels[];
	double values[];
	LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_graph);
		labels = getIntent().getExtras().getStringArray("labels");
		values = getIntent().getExtras().getDoubleArray("values");
		int[] colors = new int[] { Color.GREEN };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
		renderer = buildRenderer(colors, styles);

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
			mChartView = ChartFactory.getLineChartView(this,
					mDataset(titles, y, x), renderer);
			layout = (LinearLayout) findViewById(R.id.chart);
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mChartView.repaint();
		} else {
			mChartView.repaint();

		}
	}

	private XYMultipleSeriesDataset mDataset(String[] titles,
			List<double[]> xValues, List<double[]> yValues) {
		// TODO Auto-generated method stub
		XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
		addXYSeries(dataset1, titles, xValues, yValues, 0);
		return dataset1;
	}

	private void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles,
			List<double[]> xValues, List<double[]> yValues, int scale) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}

	private void setRenderer(XYMultipleSeriesRenderer renderer2, int[] colors,
			PointStyle[] styles) {
		// TODO Auto-generated method stub
		renderer2.setAxisTitleTextSize(16);
		renderer2.setChartTitleTextSize(20);
		renderer2.setLabelsTextSize(12);
		renderer2.setLegendTextSize(15);
		renderer2.setPointSize(5f);
		renderer2.setShowGridX(true);
		renderer2.setShowGridY(true);
		renderer2.setXLabelsAlign(Align.CENTER);
		renderer2.setYLabelsAlign(Align.CENTER);
		renderer2.setLabelsColor(Color.LTGRAY);
		renderer2.setPanEnabled(true);
		renderer2.setZoomEnabled(true);
		renderer2.setShowLegend(false);
		renderer2.setYLabels(10);
		renderer2.setXLabels(0);
		renderer2.setXLabelsAngle(90);
		if (getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0) {
			renderer2.setXAxisMax(5);
		} else {
			renderer2.setXAxisMax(8);
		}

		renderer2.setMargins(new int[] { 20, 30, 15, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer2.addSeriesRenderer(r);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mChartView.repaint();
	}

}