package co.acjs.cricdecode;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import com.actionbarsherlock.app.SherlockFragment;

public class CareerFragment extends SherlockFragment implements
		TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

	static CareerFragment careerFragment;

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, CareerFragment.TabInfo>();
	private CricDeCodePagerAdapter mPagerAdapter;

	// General
	private int matches, wins, losses, ties, no_results;
	private float win_per;

	// Batting
	private int bat_innings, bat_not_outs, bat_runs, highest, bat_balls,
			bat_100, bat_50, time_spent, bat_fours, bat_sixes, lives;
	private float bat_avg, bat_str;

	// Bowling
	private int bowl_innings, spells, bowl_runs, maidens, wickets,
			wickets_left, wickets_right, bowl_catches_dropped, fwh, twm,
			bowl_fours, bowl_sixes, noballs, wides;
	private String best_innings, best_match;
	private float bowl_avg, eco_rate, bowl_str, overs;

	// Fielding
	private int catches, slip_catches, close_catches, circle_catches,
			deep_catches, run_outs, circle_run_outs, circle_run_outs_direct,
			deep_run_outs, deep_run_outs_direct, stumpings, byes, misfields,
			field_catches_dropped;

	private class TabInfo {
		private String tag;
		@SuppressWarnings("unused")
		private Class<?> clss;
		@SuppressWarnings("unused")
		private Bundle args;
		@SuppressWarnings("unused")
		private SherlockFragment fragment;

		TabInfo(String tag, Class<?> clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}

	}

	class TabFactory implements TabContentFactory {

		private final Context mContext;

		public TabFactory(Context context) {
			mContext = context;
		}

		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		careerFragment = this;
		View rootView = inflater.inflate(R.layout.performance_fragment,
				container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.initialiseTabHost(view, savedInstanceState);
		this.intialiseViewPager(view);
		TabPatchView tabPatchView = new TabPatchView(getSherlockActivity());
		RelativeLayout relativeLayout = (RelativeLayout) view
				.findViewById(R.id.performance_fragment_layout);
		relativeLayout.addView(tabPatchView);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("tab", mTabHost.getCurrentTabTag());
		super.onSaveInstanceState(outState);
	}

	private void intialiseViewPager(View view) {

		List<SherlockFragment> fragments = new Vector<SherlockFragment>();
		fragments.add((SherlockFragment) SherlockFragment.instantiate(
				getSherlockActivity(), CareerGeneralFragment.class.getName()));
		fragments.add((SherlockFragment) SherlockFragment.instantiate(
				getSherlockActivity(), CareerBattingFragment.class.getName()));
		fragments.add((SherlockFragment) SherlockFragment.instantiate(
				getSherlockActivity(), CareerBowlingFragment.class.getName()));
		fragments.add((SherlockFragment) SherlockFragment.instantiate(
				getSherlockActivity(), CareerFieldingFragment.class.getName()));

		this.mPagerAdapter = new CricDeCodePagerAdapter(
				getChildFragmentManager(), fragments);

		this.mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
		this.mViewPager.setAdapter(this.mPagerAdapter);
		this.mViewPager.setCurrentItem(mTabHost.getCurrentTab());
		this.mViewPager.setOnPageChangeListener(this);
	}

	private void initialiseTabHost(View view, Bundle args) {
		mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		CareerFragment.AddTab(this, this.mTabHost,
				this.mTabHost.newTabSpec("General").setIndicator("General"),
				(tabInfo = new TabInfo("General", CareerGeneralFragment.class,
						args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		CareerFragment.AddTab(this, this.mTabHost,
				this.mTabHost.newTabSpec("Batting").setIndicator("Batting"),
				(tabInfo = new TabInfo("Batting", CareerBattingFragment.class,
						args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		CareerFragment.AddTab(this, this.mTabHost,
				this.mTabHost.newTabSpec("Bowling").setIndicator("Bowling"),
				(tabInfo = new TabInfo("Bowling", CareerBowlingFragment.class,
						args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		CareerFragment.AddTab(this, this.mTabHost,
				this.mTabHost.newTabSpec("Fielding").setIndicator("Fielding"),
				(tabInfo = new TabInfo("Fielding",
						CareerFieldingFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		if (args != null) {
			mTabHost.setCurrentTabByTag(args.getString("tab"));
		}
		mTabHost.setOnTabChangedListener(this);
	}

	private static void AddTab(CareerFragment careerFragment, TabHost tabHost,
			TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(careerFragment.new TabFactory(careerFragment
				.getSherlockActivity()));
		tabHost.addTab(tabSpec);
	}

	public void onTabChanged(String tag) {
		// TabInfo newTab = this.mapTabInfo.get(tag);
		int pos = this.mTabHost.getCurrentTab();
		Log.d("Debug", "Position " + pos);
		this.mViewPager.setCurrentItem(pos);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		this.mTabHost.setCurrentTab(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	public void viewInfo(int id) {
		switch (id) {
		case PerformanceFragmentEdit.GENERAL:
			CareerGeneralFragment.careerGeneralFragment.matches.setText(matches
					+ "");
			CareerGeneralFragment.careerGeneralFragment.wins.setText(wins + "");
			CareerGeneralFragment.careerGeneralFragment.losses.setText(losses
					+ "");
			CareerGeneralFragment.careerGeneralFragment.ties.setText(ties + "");
			CareerGeneralFragment.careerGeneralFragment.no_results
					.setText(no_results + "");
			CareerGeneralFragment.careerGeneralFragment.win_per.setText(win_per
					+ "");
			break;
		case PerformanceFragmentEdit.BATTING:
			CareerBattingFragment.careerBattingFragment.innings
					.setText(bat_innings + "");
			CareerBattingFragment.careerBattingFragment.not_outs
					.setText(bat_not_outs + "");
			CareerBattingFragment.careerBattingFragment.runs.setText(bat_runs
					+ "");
			CareerBattingFragment.careerBattingFragment.highest.setText(highest
					+ "");
			CareerBattingFragment.careerBattingFragment.avg.setText(bat_avg
					+ "");
			CareerBattingFragment.careerBattingFragment.balls.setText(bat_balls
					+ "");
			CareerBattingFragment.careerBattingFragment.str_rate
					.setText(bat_str + "");
			CareerBattingFragment.careerBattingFragment.fifties.setText(bat_50
					+ "");
			CareerBattingFragment.careerBattingFragment.hundreds
					.setText(bat_100 + "");
			CareerBattingFragment.careerBattingFragment.time_spent
					.setText(time_spent + "");
			CareerBattingFragment.careerBattingFragment.fours.setText(bat_fours
					+ "");
			CareerBattingFragment.careerBattingFragment.sixes.setText(bat_sixes
					+ "");
			CareerBattingFragment.careerBattingFragment.lives.setText(lives
					+ "");
			break;
		case PerformanceFragmentEdit.BOWLING:
			CareerBowlingFragment.careerBowlingFragment.innings
					.setText(bowl_innings + "");
			CareerBowlingFragment.careerBowlingFragment.overs.setText(overs
					+ "");
			CareerBowlingFragment.careerBowlingFragment.spells.setText(spells
					+ "");
			CareerBowlingFragment.careerBowlingFragment.runs.setText(bowl_runs
					+ "");
			CareerBowlingFragment.careerBowlingFragment.maidens.setText(maidens
					+ "");
			CareerBowlingFragment.careerBowlingFragment.wickets.setText(wickets
					+ "");
			CareerBowlingFragment.careerBowlingFragment.wickets_left
					.setText(wickets_left + "");
			CareerBowlingFragment.careerBowlingFragment.wickets_right
					.setText(wickets_right + "");
			CareerBowlingFragment.careerBowlingFragment.catches_dropped
					.setText(bowl_catches_dropped + "");
			CareerBowlingFragment.careerBowlingFragment.eco_rate
					.setText(eco_rate + "");
			CareerBowlingFragment.careerBowlingFragment.str_rate
					.setText(bowl_str + "");
			CareerBowlingFragment.careerBowlingFragment.avg.setText(bowl_avg
					+ "");
			CareerBowlingFragment.careerBowlingFragment.fwi.setText(fwh + "");
			CareerBowlingFragment.careerBowlingFragment.twm.setText(twm + "");
			CareerBowlingFragment.careerBowlingFragment.bbi
					.setText(best_innings);
			CareerBowlingFragment.careerBowlingFragment.bbm.setText(best_match);
			CareerBowlingFragment.careerBowlingFragment.fours
					.setText(bowl_fours + "");
			CareerBowlingFragment.careerBowlingFragment.sixes
					.setText(bowl_sixes + "");
			CareerBowlingFragment.careerBowlingFragment.noballs.setText(noballs
					+ "");
			CareerBowlingFragment.careerBowlingFragment.wides.setText(wides
					+ "");
			break;
		case PerformanceFragmentEdit.FIELDING:
			CareerFieldingFragment.careerFieldingFragment.catches
					.setText(catches + "");
			CareerFieldingFragment.careerFieldingFragment.slip_catches
					.setText(slip_catches + "");
			CareerFieldingFragment.careerFieldingFragment.close_catches
					.setText(close_catches + "");
			CareerFieldingFragment.careerFieldingFragment.circle_catches
					.setText(circle_catches + "");
			CareerFieldingFragment.careerFieldingFragment.deep_catches
					.setText(deep_catches + "");
			CareerFieldingFragment.careerFieldingFragment.runouts
					.setText(run_outs + "");
			CareerFieldingFragment.careerFieldingFragment.circle_runouts
					.setText(circle_run_outs + "");
			CareerFieldingFragment.careerFieldingFragment.circle_runouts_direct
					.setText(circle_run_outs_direct + "");
			CareerFieldingFragment.careerFieldingFragment.deep_runouts
					.setText(deep_run_outs + "");
			CareerFieldingFragment.careerFieldingFragment.deep_runouts_direct
					.setText(deep_run_outs_direct + "");
			CareerFieldingFragment.careerFieldingFragment.stumpings
					.setText(stumpings + "");
			CareerFieldingFragment.careerFieldingFragment.byes.setText(byes
					+ "");
			CareerFieldingFragment.careerFieldingFragment.misfields
					.setText(misfields + "");
			CareerFieldingFragment.careerFieldingFragment.catches_dropped
					.setText(field_catches_dropped + "");
			break;
		default:
			break;

		}
	}

}
