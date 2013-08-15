package co.acjs.cricdecode;

import java.util.ArrayList;
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

	TabHost mTabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, CareerFragment.TabInfo>();
	private CricDeCodePagerAdapter mPagerAdapter;

	// Filter Variables
	ArrayList<String> my_team_list, my_team_list_selected, opponent_list,
			opponent_list_selected, venue_list, venue_list_selected,
			overs_list, overs_list_selected, innings_list,
			innings_list_selected, level_list, level_list_selected,
			duration_list, duration_list_selected, first_list,
			first_list_selected, season_list, season_list_selected,
			result_list, result_list_selected;
	String myteam_whereClause = "", opponent_whereClause = "",
			venue_whereClause = "", overs_whereClause = "",
			innings_whereClause = "", level_whereClause = "",
			duration_whereClause = "", first_whereClause = "",
			season_whereClause = "", result_whereClause = "";

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
		if (savedInstanceState == null) {

			season_list = new ArrayList<String>();
			season_list_selected = new ArrayList<String>();

			my_team_list = new ArrayList<String>();
			my_team_list_selected = new ArrayList<String>();

			opponent_list = new ArrayList<String>();
			opponent_list_selected = new ArrayList<String>();

			venue_list = new ArrayList<String>();
			venue_list_selected = new ArrayList<String>();

			result_list = new ArrayList<String>();
			result_list_selected = new ArrayList<String>();

			level_list = new ArrayList<String>();
			level_list_selected = new ArrayList<String>();

			overs_list = new ArrayList<String>();
			overs_list_selected = new ArrayList<String>();

			innings_list = new ArrayList<String>();
			innings_list_selected = new ArrayList<String>();

			duration_list = new ArrayList<String>();
			duration_list_selected = new ArrayList<String>();

			first_list = new ArrayList<String>();
			first_list_selected = new ArrayList<String>();

			fetchFromDb();

		} else {

			season_list = savedInstanceState.getStringArrayList("season_list");
			season_list_selected = savedInstanceState
					.getStringArrayList("season_list_selected");

			my_team_list = savedInstanceState
					.getStringArrayList("my_team_list");
			my_team_list_selected = savedInstanceState
					.getStringArrayList("my_team_list_selected");

			opponent_list = savedInstanceState
					.getStringArrayList("opponent_list");
			opponent_list_selected = savedInstanceState
					.getStringArrayList("opponent_list_selected");

			venue_list = savedInstanceState.getStringArrayList("venue_list");
			venue_list_selected = savedInstanceState
					.getStringArrayList("venue_list_selected");

			result_list = savedInstanceState.getStringArrayList("result_list");
			result_list_selected = savedInstanceState
					.getStringArrayList("result_list_selected");

			level_list = savedInstanceState.getStringArrayList("level_list");
			level_list_selected = savedInstanceState
					.getStringArrayList("level_list_selected");

			overs_list = savedInstanceState.getStringArrayList("overs_list");
			overs_list_selected = savedInstanceState
					.getStringArrayList("overs_list_selected");

			innings_list = savedInstanceState
					.getStringArrayList("innings_list");
			innings_list_selected = savedInstanceState
					.getStringArrayList("innings_list_selected");

			duration_list = savedInstanceState
					.getStringArrayList("duration_list");
			duration_list_selected = savedInstanceState
					.getStringArrayList("duration_list_selected");

			first_list = savedInstanceState.getStringArrayList("first_list");
			first_list_selected = savedInstanceState
					.getStringArrayList("first_list_selected");

			myteam_whereClause = savedInstanceState
					.getString("myteam_whereClause");
			opponent_whereClause = savedInstanceState
					.getString("opponent_whereClause");
			venue_whereClause = savedInstanceState
					.getString("venue_whereClause");
			level_whereClause = savedInstanceState
					.getString("level_whereClause");
			overs_whereClause = savedInstanceState
					.getString("overs_whereClause");
			innings_whereClause = savedInstanceState
					.getString("innings_whereClause");
			duration_whereClause = savedInstanceState
					.getString("duration_whereClause");
			first_whereClause = savedInstanceState
					.getString("first_whereClause");
			season_whereClause = savedInstanceState
					.getString("season_whereClause");
			result_whereClause = savedInstanceState
					.getString("result_whereClause");

		}
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
		outState.putStringArrayList("season_list",
				(ArrayList<String>) season_list);
		outState.putStringArrayList("result_list",
				(ArrayList<String>) result_list);
		outState.putStringArrayList("my_team_list",
				(ArrayList<String>) my_team_list);
		outState.putStringArrayList("opponent_list",
				(ArrayList<String>) opponent_list);
		outState.putStringArrayList("venue_list",
				(ArrayList<String>) venue_list);
		outState.putStringArrayList("level_list",
				(ArrayList<String>) level_list);
		outState.putStringArrayList("overs_list",
				(ArrayList<String>) overs_list);
		outState.putStringArrayList("innings_list",
				(ArrayList<String>) innings_list);
		outState.putStringArrayList("duration_list",
				(ArrayList<String>) duration_list);
		outState.putStringArrayList("first_list",
				(ArrayList<String>) first_list);
		outState.putStringArrayList("season_list_selected",
				(ArrayList<String>) season_list_selected);
		outState.putStringArrayList("result_list_selected",
				(ArrayList<String>) result_list_selected);
		outState.putStringArrayList("my_team_list_selected",
				(ArrayList<String>) my_team_list_selected);
		outState.putStringArrayList("opponent_list_selected",
				(ArrayList<String>) opponent_list_selected);
		outState.putStringArrayList("venue_list_selected",
				(ArrayList<String>) venue_list_selected);
		outState.putStringArrayList("level_list_selected",
				(ArrayList<String>) level_list_selected);
		outState.putStringArrayList("overs_list_selected",
				(ArrayList<String>) overs_list_selected);
		outState.putStringArrayList("innings_list_selected",
				(ArrayList<String>) innings_list_selected);
		outState.putStringArrayList("duration_list_selected",
				(ArrayList<String>) duration_list_selected);
		outState.putStringArrayList("first_list_selected",
				(ArrayList<String>) first_list_selected);
		outState.putString("myteam_whereClause", myteam_whereClause);
		outState.putString("opponent_whereClause", opponent_whereClause);
		outState.putString("venue_whereClause", venue_whereClause);
		outState.putString("level_whereClause", level_whereClause);
		outState.putString("overs_whereClause", overs_whereClause);
		outState.putString("innings_whereClause", innings_whereClause);
		outState.putString("duration_whereClause", duration_whereClause);
		outState.putString("first_whereClause", first_whereClause);
		outState.putString("season_whereClause", season_whereClause);
		outState.putString("result_whereClause", result_whereClause);
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
		viewInfo(position);
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
				+ MatchDb.SQLITE_TABLE + " m where " + MatchDb.KEY_STATUS
				+ "='" + MatchDb.MATCH_HISTORY + "'" + myteam_whereClause
				+ opponent_whereClause + venue_whereClause + overs_whereClause
				+ innings_whereClause + level_whereClause
				+ duration_whereClause + first_whereClause + season_whereClause
				+ result_whereClause + " group by " + MatchDb.KEY_RESULT, null);
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
		cursor = MainActivity.dbHandle.rawQuery("select count(p."
				+ PerformanceDb.KEY_ROWID + "),sum(p."
				+ PerformanceDb.KEY_BAT_RUNS + "),max(p."
				+ PerformanceDb.KEY_BAT_RUNS + "),sum(p."
				+ PerformanceDb.KEY_BAT_BALLS + "),sum(p."
				+ PerformanceDb.KEY_BAT_TIME + "),sum(p."
				+ PerformanceDb.KEY_BAT_FOURS + "),sum(p."
				+ PerformanceDb.KEY_BAT_SIXES + "),sum(p."
				+ PerformanceDb.KEY_BAT_CHANCES + ") from "
				+ PerformanceDb.SQLITE_TABLE + " p inner join "
				+ MatchDb.SQLITE_TABLE + " m on p." + PerformanceDb.KEY_MATCHID
				+ "=m." + MatchDb.KEY_ROWID + " where p."
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and (p." + PerformanceDb.KEY_BAT_HOW_OUT
				+ "!='Not Out' or p." + PerformanceDb.KEY_BAT_BALLS + "!=0)"
				+ myteam_whereClause + opponent_whereClause + venue_whereClause
				+ overs_whereClause + innings_whereClause + level_whereClause
				+ duration_whereClause + first_whereClause + season_whereClause
				+ result_whereClause, null);
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

		cursor = MainActivity.dbHandle.rawQuery("select count(p."
				+ PerformanceDb.KEY_ROWID + ") from "
				+ PerformanceDb.SQLITE_TABLE + " p inner join "
				+ MatchDb.SQLITE_TABLE + " m on p." + PerformanceDb.KEY_MATCHID
				+ "=m." + MatchDb.KEY_ROWID + " where p."
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and p." + PerformanceDb.KEY_BAT_HOW_OUT + "!='Not Out'"
				+ myteam_whereClause + opponent_whereClause + venue_whereClause
				+ overs_whereClause + innings_whereClause + level_whereClause
				+ duration_whereClause + first_whereClause + season_whereClause
				+ result_whereClause, null);
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

		cursor = MainActivity.dbHandle.rawQuery("select count(p."
				+ PerformanceDb.KEY_ROWID + ") from "
				+ PerformanceDb.SQLITE_TABLE + " p inner join "
				+ MatchDb.SQLITE_TABLE + " m on p." + PerformanceDb.KEY_MATCHID
				+ "=m." + MatchDb.KEY_ROWID + " where p."
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and p." + PerformanceDb.KEY_BAT_RUNS + ">=100"
				+ myteam_whereClause + opponent_whereClause + venue_whereClause
				+ overs_whereClause + innings_whereClause + level_whereClause
				+ duration_whereClause + first_whereClause + season_whereClause
				+ result_whereClause, null);
		cursor.moveToFirst();
		bat_100 = cursor.getInt(0);
		cursor.close();

		cursor = MainActivity.dbHandle.rawQuery("select count(p."
				+ PerformanceDb.KEY_ROWID + ") from "
				+ PerformanceDb.SQLITE_TABLE + " p inner join "
				+ MatchDb.SQLITE_TABLE + " m on p." + PerformanceDb.KEY_MATCHID
				+ "=m." + MatchDb.KEY_ROWID + " where p."
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and p." + PerformanceDb.KEY_BAT_RUNS + ">=50"
				+ myteam_whereClause + opponent_whereClause + venue_whereClause
				+ overs_whereClause + innings_whereClause + level_whereClause
				+ duration_whereClause + first_whereClause + season_whereClause
				+ result_whereClause, null);
		cursor.moveToFirst();
		bat_50 = cursor.getInt(0) - bat_100;
		cursor.close();

		// Bowling
		cursor = MainActivity.dbHandle.rawQuery("select count(p."
				+ PerformanceDb.KEY_ROWID + "),sum(p."
				+ PerformanceDb.KEY_BOWL_BALLS + "),sum(p."
				+ PerformanceDb.KEY_BOWL_RUNS + "),sum(p."
				+ PerformanceDb.KEY_BOWL_WKTS_LEFT + "),sum(p."
				+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + "),sum(p."
				+ PerformanceDb.KEY_BOWL_CATCHES_DROPPED + "),sum(p."
				+ PerformanceDb.KEY_BOWL_SPELLS + "),sum(p."
				+ PerformanceDb.KEY_BOWL_MAIDENS + "),sum(p."
				+ PerformanceDb.KEY_BOWL_FOURS + "),sum(p."
				+ PerformanceDb.KEY_BOWL_SIXES + "),sum(p."
				+ PerformanceDb.KEY_BOWL_NOBALLS + "),sum(p."
				+ PerformanceDb.KEY_BOWL_WIDES + ") from "
				+ PerformanceDb.SQLITE_TABLE + " p inner join "
				+ MatchDb.SQLITE_TABLE + " m on p." + PerformanceDb.KEY_MATCHID
				+ "=m." + MatchDb.KEY_ROWID + " where p."
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and p." + PerformanceDb.KEY_BOWL_BALLS + "!=0"
				+ myteam_whereClause + opponent_whereClause + venue_whereClause
				+ overs_whereClause + innings_whereClause + level_whereClause
				+ duration_whereClause + first_whereClause + season_whereClause
				+ result_whereClause, null);
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

		cursor = MainActivity.dbHandle.rawQuery("select count(p."
				+ PerformanceDb.KEY_ROWID + ") from "
				+ PerformanceDb.SQLITE_TABLE + " p inner join "
				+ MatchDb.SQLITE_TABLE + " m on p." + PerformanceDb.KEY_MATCHID
				+ "=m." + MatchDb.KEY_ROWID + " where p."
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and p." + PerformanceDb.KEY_BOWL_WKTS_LEFT + "+p."
				+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + ">=5"
				+ myteam_whereClause + opponent_whereClause + venue_whereClause
				+ overs_whereClause + innings_whereClause + level_whereClause
				+ duration_whereClause + first_whereClause + season_whereClause
				+ result_whereClause, null);
		cursor.moveToFirst();
		fwh = cursor.getInt(0);
		cursor.close();

		cursor = MainActivity.dbHandle.rawQuery("select sum(p."
				+ PerformanceDb.KEY_BOWL_WKTS_LEFT + "+p."
				+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + ") as sumtotal from "
				+ PerformanceDb.SQLITE_TABLE + " p inner join "
				+ MatchDb.SQLITE_TABLE + " m on p." + PerformanceDb.KEY_MATCHID
				+ "=m." + MatchDb.KEY_ROWID + " where p."
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'"
				+ myteam_whereClause + opponent_whereClause + venue_whereClause
				+ overs_whereClause + innings_whereClause + level_whereClause
				+ duration_whereClause + first_whereClause + season_whereClause
				+ result_whereClause + " group by p."
				+ PerformanceDb.KEY_MATCHID + " having sumtotal>=10", null);
		twm = cursor.getCount();
		cursor.close();

		cursor = MainActivity.dbHandle.rawQuery("select max(p."
				+ PerformanceDb.KEY_BOWL_WKTS_LEFT + "+p."
				+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + ") from "
				+ PerformanceDb.SQLITE_TABLE + " p inner join "
				+ MatchDb.SQLITE_TABLE + " m on p." + PerformanceDb.KEY_MATCHID
				+ "=m." + MatchDb.KEY_ROWID + " where p."
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'"
				+ myteam_whereClause + opponent_whereClause + venue_whereClause
				+ overs_whereClause + innings_whereClause + level_whereClause
				+ duration_whereClause + first_whereClause + season_whereClause
				+ result_whereClause, null);
		cursor.moveToFirst();
		Log.d("Debug", "Length " + cursor.getCount());
		int max = cursor.getInt(0);
		cursor.close();

		cursor = MainActivity.dbHandle.rawQuery("select (p."
				+ PerformanceDb.KEY_BOWL_WKTS_LEFT + "+p."
				+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + ") as wkts,p."
				+ PerformanceDb.KEY_BOWL_RUNS + " from "
				+ PerformanceDb.SQLITE_TABLE + " p inner join "
				+ MatchDb.SQLITE_TABLE + " m on p." + PerformanceDb.KEY_MATCHID
				+ "=m." + MatchDb.KEY_ROWID + " where p."
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and wkts=" + max + myteam_whereClause
				+ opponent_whereClause + venue_whereClause + overs_whereClause
				+ innings_whereClause + level_whereClause
				+ duration_whereClause + first_whereClause + season_whereClause
				+ result_whereClause + " order by p."
				+ PerformanceDb.KEY_BOWL_RUNS, null);
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			best_innings = cursor.getInt(0) + "/" + cursor.getInt(1);
		} else {
			best_innings = "NA";
		}
		cursor.close();

		cursor = MainActivity.dbHandle.rawQuery("select sum(p."
				+ PerformanceDb.KEY_BOWL_WKTS_LEFT + "+p."
				+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + ") as wkts,sum(p."
				+ PerformanceDb.KEY_BOWL_RUNS + ") as runs from "
				+ PerformanceDb.SQLITE_TABLE + " p inner join "
				+ MatchDb.SQLITE_TABLE + " m on p." + PerformanceDb.KEY_MATCHID
				+ "=m." + MatchDb.KEY_ROWID + " where p."
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'"
				+ myteam_whereClause + opponent_whereClause + venue_whereClause
				+ overs_whereClause + innings_whereClause + level_whereClause
				+ duration_whereClause + first_whereClause + season_whereClause
				+ result_whereClause + " group by p."
				+ PerformanceDb.KEY_MATCHID + " order by wkts desc,runs asc",
				null);
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
		cursor = MainActivity.dbHandle.rawQuery("select sum(p."
				+ PerformanceDb.KEY_FIELD_SLIP_CATCH + "),sum(p."
				+ PerformanceDb.KEY_FIELD_CLOSE_CATCH + "),sum(p."
				+ PerformanceDb.KEY_FIELD_CIRCLE_CATCH + "),sum(p."
				+ PerformanceDb.KEY_FIELD_DEEP_CATCH + "),sum(p."
				+ PerformanceDb.KEY_FIELD_RO_CIRCLE + "),sum(p."
				+ PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE + "),sum(p."
				+ PerformanceDb.KEY_FIELD_RO_DEEP + "),sum(p."
				+ PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP + "),sum(p."
				+ PerformanceDb.KEY_FIELD_STUMPINGS + "),sum(p."
				+ PerformanceDb.KEY_FIELD_BYES + "),sum(p."
				+ PerformanceDb.KEY_FIELD_MISFIELDS + "),sum(p."
				+ PerformanceDb.KEY_FIELD_CATCHES_DROPPED + ") from "
				+ PerformanceDb.SQLITE_TABLE + " p inner join "
				+ MatchDb.SQLITE_TABLE + " m on p." + PerformanceDb.KEY_MATCHID
				+ "=m." + MatchDb.KEY_ROWID + " where p."
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'"
				+ myteam_whereClause + opponent_whereClause + venue_whereClause
				+ overs_whereClause + innings_whereClause + level_whereClause
				+ duration_whereClause + first_whereClause + season_whereClause
				+ result_whereClause, null);
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

	public void fetchFromDb() {

		Cursor c = MainActivity.dbHandle.rawQuery(
				"select distinct strftime('%Y'," + MatchDb.KEY_MATCH_DATE
						+ ") as _id from " + MatchDb.SQLITE_TABLE + " where "
						+ MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
						+ "'", null);
		int count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				season_list.add(c.getString(0));
				season_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_MY_TEAM + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				my_team_list.add(c.getString(0));
				my_team_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_OPPONENT_TEAM + " as _id from "
				+ MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='"
				+ MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				opponent_list.add(c.getString(0));
				opponent_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_VENUE + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				venue_list.add(c.getString(0));
				venue_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_RESULT + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				result_list.add(c.getString(0));
				result_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_LEVEL + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				level_list.add(c.getString(0));
				level_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_OVERS + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				int temp = c.getInt(0);
				if (temp == -1) {
					overs_list.add("Unlimited");
					overs_list_selected.add("Unlimited");
				} else {
					overs_list.add(c.getInt(0) + "");
					overs_list_selected.add(c.getInt(0) + "");
				}
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_INNINGS + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				innings_list.add(c.getInt(0) + "");
				innings_list_selected.add(c.getInt(0) + "");
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_DURATION + " as _id from " + MatchDb.SQLITE_TABLE
				+ " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				duration_list.add(c.getString(0));
				duration_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ MatchDb.KEY_FIRST_ACTION + " as _id from "
				+ MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='"
				+ MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				first_list.add(c.getString(0));
				first_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();
	}

}
