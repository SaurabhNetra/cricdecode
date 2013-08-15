package co.acjs.cricdecode;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
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
		fireQueries();
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
			if (bat_avg == -1) {
				CareerBattingFragment.careerBattingFragment.avg.setText("NA");
			} else {
				CareerBattingFragment.careerBattingFragment.avg.setText(bat_avg
						+ "");
			}
			CareerBattingFragment.careerBattingFragment.balls.setText(bat_balls
					+ "");
			if (bat_str == -1) {
				CareerBattingFragment.careerBattingFragment.str_rate
						.setText("NA");
			} else {
				CareerBattingFragment.careerBattingFragment.str_rate
						.setText(bat_str + "");
			}
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
			if (eco_rate == -1) {
				CareerBowlingFragment.careerBowlingFragment.eco_rate
						.setText("NA");
			} else {
				CareerBowlingFragment.careerBowlingFragment.eco_rate
						.setText(eco_rate + "");
			}
			if (bowl_str == -1) {
				CareerBowlingFragment.careerBowlingFragment.str_rate
						.setText("NA");
			} else {
				CareerBowlingFragment.careerBowlingFragment.str_rate
						.setText(bowl_str + "");
			}
			if (bowl_avg == -1) {
				CareerBowlingFragment.careerBowlingFragment.avg.setText("NA");
			} else {
				CareerBowlingFragment.careerBowlingFragment.avg
						.setText(bowl_avg + "");
			}
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

	public void fireQueries() {

		// General
		matches = wins = losses = ties = no_results = 0;
		Cursor cursor = MainActivity.dbHandle.rawQuery("select count("
				+ MatchDb.KEY_ROWID + ")," + MatchDb.KEY_RESULT + " from "
				+ MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='"
				+ MatchDb.MATCH_HISTORY + "'" + " group by "
				+ MatchDb.KEY_RESULT, null);
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			int temp;
			String str;
			do {
				temp = cursor.getInt(0);
				str = cursor.getString(1);
				matches += temp;
				if (str.equals("Win")) {
					wins += temp;
				} else if (str.equals("Loss")) {
					losses += temp;
				} else if (str.equals("Tie")) {
					ties += temp;
				} else {
					no_results += temp;
				}
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			win_per = PerformanceFragmentEdit.round((float) 100 * wins
					/ matches, 2);
		}

		// Batting
		cursor = MainActivity.dbHandle.rawQuery("select count("
				+ PerformanceDb.KEY_ROWID + "),sum("
				+ PerformanceDb.KEY_BAT_RUNS + "),max("
				+ PerformanceDb.KEY_BAT_RUNS + "),sum("
				+ PerformanceDb.KEY_BAT_BALLS + "),sum("
				+ PerformanceDb.KEY_BAT_TIME + "),sum("
				+ PerformanceDb.KEY_BAT_FOURS + "),sum("
				+ PerformanceDb.KEY_BAT_SIXES + "),sum("
				+ PerformanceDb.KEY_BAT_CHANCES + ") from "
				+ PerformanceDb.SQLITE_TABLE + " where "
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and (" + PerformanceDb.KEY_BAT_HOW_OUT + "!='Not Out' or "
				+ PerformanceDb.KEY_BAT_BALLS + "!=0)", null);
		cursor.moveToFirst();
		bat_innings = cursor.getInt(0);
		bat_runs = cursor.getInt(1);
		highest = cursor.getInt(2);
		bat_balls = cursor.getInt(3);
		time_spent = cursor.getInt(4);
		bat_fours = cursor.getInt(5);
		bat_sixes = cursor.getInt(6);
		lives = cursor.getInt(7);
		cursor.close();

		cursor = MainActivity.dbHandle.rawQuery("select count("
				+ PerformanceDb.KEY_ROWID + ") from "
				+ PerformanceDb.SQLITE_TABLE + " where "
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and " + PerformanceDb.KEY_BAT_HOW_OUT + "!='Not Out'",
				null);
		cursor.moveToFirst();
		int outs = cursor.getInt(0);
		bat_not_outs = bat_innings - outs;
		cursor.close();
		if (outs != 0) {
			bat_avg = (float) bat_runs / outs;
			bat_avg = PerformanceFragmentEdit.round(bat_avg, 2);
		} else {
			bat_avg = -1;
		}
		if (bat_balls != 0) {
			bat_str = (float) bat_runs / bat_balls;
			bat_str = PerformanceFragmentEdit.round(bat_str, 2) * 100;
		} else {
			bat_str = -1;
		}

		cursor = MainActivity.dbHandle.rawQuery("select count("
				+ PerformanceDb.KEY_ROWID + ") from "
				+ PerformanceDb.SQLITE_TABLE + " where "
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and " + PerformanceDb.KEY_BAT_RUNS + ">=100", null);
		cursor.moveToFirst();
		bat_100 = cursor.getInt(0);
		cursor.close();

		cursor = MainActivity.dbHandle.rawQuery("select count("
				+ PerformanceDb.KEY_ROWID + ") from "
				+ PerformanceDb.SQLITE_TABLE + " where "
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and " + PerformanceDb.KEY_BAT_RUNS + ">=50", null);
		cursor.moveToFirst();
		bat_50 = cursor.getInt(0) - bat_100;
		cursor.close();

		// Bowling
		cursor = MainActivity.dbHandle.rawQuery("select count("
				+ PerformanceDb.KEY_ROWID + "),sum("
				+ PerformanceDb.KEY_BOWL_BALLS + "),sum("
				+ PerformanceDb.KEY_BOWL_RUNS + "),sum("
				+ PerformanceDb.KEY_BOWL_WKTS_LEFT + "),sum("
				+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + "),sum("
				+ PerformanceDb.KEY_BOWL_CATCHES_DROPPED + "),sum("
				+ PerformanceDb.KEY_BOWL_SPELLS + "),sum("
				+ PerformanceDb.KEY_BOWL_MAIDENS + "),sum("
				+ PerformanceDb.KEY_BOWL_FOURS + "),sum("
				+ PerformanceDb.KEY_BOWL_SIXES + "),sum("
				+ PerformanceDb.KEY_BOWL_NOBALLS + "),sum("
				+ PerformanceDb.KEY_BOWL_WIDES + ") from "
				+ PerformanceDb.SQLITE_TABLE + " where "
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and " + PerformanceDb.KEY_BOWL_BALLS + "!=0", null);
		cursor.moveToFirst();
		bowl_innings = cursor.getInt(0);
		int balls = cursor.getInt(1);
		overs = balls / 6 + (float) (balls % 6) / 10;
		bowl_runs = cursor.getInt(2);
		wickets = (wickets_left = cursor.getInt(3))
				+ (wickets_right = cursor.getInt(4));
		bowl_catches_dropped = cursor.getInt(5);
		spells = cursor.getInt(6);
		maidens = cursor.getInt(7);
		bowl_fours = cursor.getInt(8);
		bowl_sixes = cursor.getInt(9);
		noballs = cursor.getInt(10);
		wides = cursor.getInt(11);
		if (balls != 0) {
			eco_rate = PerformanceFragmentEdit.round((float) bowl_runs / balls
					* 6, 2);
		} else {
			eco_rate = -1;
		}
		if (wickets != 0) {
			bowl_str = PerformanceFragmentEdit
					.round((float) balls / wickets, 2);
			bowl_avg = PerformanceFragmentEdit.round((float) bowl_runs
					/ wickets, 2);
		} else {
			bowl_str = -1;
			bowl_avg = -1;
		}
		cursor.close();

		cursor = MainActivity.dbHandle.rawQuery("select count("
				+ PerformanceDb.KEY_ROWID + ") from "
				+ PerformanceDb.SQLITE_TABLE + " where "
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and " + PerformanceDb.KEY_BOWL_WKTS_LEFT + "+"
				+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + ">=5", null);
		cursor.moveToFirst();
		fwh = cursor.getInt(0);
		cursor.close();

		cursor = MainActivity.dbHandle.rawQuery("select sum("
				+ PerformanceDb.KEY_BOWL_WKTS_LEFT + "+"
				+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + ") as sumtotal from "
				+ PerformanceDb.SQLITE_TABLE + " where "
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' group by " + PerformanceDb.KEY_MATCHID
				+ " having sumtotal>=10", null);
		twm = cursor.getCount();
		cursor.close();

		cursor = MainActivity.dbHandle.rawQuery(
				"select max(" + PerformanceDb.KEY_BOWL_WKTS_LEFT + "+"
						+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + ") from "
						+ PerformanceDb.SQLITE_TABLE + " where "
						+ PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "'", null);
		cursor.moveToFirst();
		Log.d("Debug", "Length " + cursor.getCount());
		int max = cursor.getInt(0);
		cursor.close();

		cursor = MainActivity.dbHandle.rawQuery("select ("
				+ PerformanceDb.KEY_BOWL_WKTS_LEFT + "+"
				+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + ") as wkts,"
				+ PerformanceDb.KEY_BOWL_RUNS + " from "
				+ PerformanceDb.SQLITE_TABLE + " where "
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and wkts=" + max + " order by "
				+ PerformanceDb.KEY_BOWL_RUNS, null);
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			best_innings = cursor.getInt(0) + "/" + cursor.getInt(1);
		} else {
			best_innings = "NA";
		}
		cursor.close();

		cursor = MainActivity.dbHandle.rawQuery("select sum("
				+ PerformanceDb.KEY_BOWL_WKTS_LEFT + "+"
				+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + ") as wkts,sum("
				+ PerformanceDb.KEY_BOWL_RUNS + ") as runs from "
				+ PerformanceDb.SQLITE_TABLE + " where "
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' group by " + PerformanceDb.KEY_MATCHID
				+ " order by wkts desc,runs asc", null);
		cursor.moveToFirst();
		Log.d("Debug", "Length " + cursor.getCount());
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			best_match = cursor.getInt(0) + "/" + cursor.getInt(1);
		} else {
			best_match = "NA";
		}
		cursor.close();

		// Fielding
		cursor = MainActivity.dbHandle.rawQuery(
				"select sum(" + PerformanceDb.KEY_FIELD_SLIP_CATCH + "),sum("
						+ PerformanceDb.KEY_FIELD_CLOSE_CATCH + "),sum("
						+ PerformanceDb.KEY_FIELD_CIRCLE_CATCH + "),sum("
						+ PerformanceDb.KEY_FIELD_DEEP_CATCH + "),sum("
						+ PerformanceDb.KEY_FIELD_RO_CIRCLE + "),sum("
						+ PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE + "),sum("
						+ PerformanceDb.KEY_FIELD_RO_DEEP + "),sum("
						+ PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP + "),sum("
						+ PerformanceDb.KEY_FIELD_STUMPINGS + "),sum("
						+ PerformanceDb.KEY_FIELD_BYES + "),sum("
						+ PerformanceDb.KEY_FIELD_MISFIELDS + "),sum("
						+ PerformanceDb.KEY_FIELD_CATCHES_DROPPED + ") from "
						+ PerformanceDb.SQLITE_TABLE + " where "
						+ PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "'", null);
		cursor.moveToFirst();
		catches = (slip_catches = cursor.getInt(0))
				+ (close_catches = cursor.getInt(1))
				+ (circle_catches = cursor.getInt(2))
				+ (deep_catches = cursor.getInt(3));
		run_outs = (circle_run_outs = cursor.getInt(4))
				+ (circle_run_outs_direct = cursor.getInt(5))
				+ (deep_run_outs = cursor.getInt(6))
				+ (deep_run_outs_direct = cursor.getInt(7));
		stumpings = cursor.getInt(8);
		byes = cursor.getInt(9);
		misfields = cursor.getInt(10);
		field_catches_dropped = cursor.getInt(11);
		cursor.close();
	}
}
