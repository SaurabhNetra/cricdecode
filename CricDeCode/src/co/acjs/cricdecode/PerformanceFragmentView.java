package co.acjs.cricdecode;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceFragmentView extends SherlockFragment implements
		TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

	static PerformanceFragmentView performanceFragmentView;

	// Declare Fields
	// Match
	Spinner inning_no;
	private int match_id, innings, current_innings, current_position;

	// General
	private String result, review, duration, first, my_team, opponent_team,
			venue, level, date, match_overs;

	// Batting
	private int[] batting_no = { 1, 1 }, bat_runs, bat_balls, time_spent,
			bat_fours, bat_sixes, lives;
	private String[] how_out, bowler_type, fielding_pos;

	// Bowling
	private int[] spells, maidens, bowl_runs, bowl_fours, bowl_sixes,
			wkts_left, wkts_right, bowl_catches_dropped, noballs, wides;
	private float[] overs;

	// Fielding
	private int[] slip_catches, close_catches, circle_catches, deep_catches,
			circle_runouts, circle_runouts_direct, deep_runouts,
			deep_runouts_direct, stumpings, byes, misfields,
			field_catches_dropped;

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, PerformanceFragmentView.TabInfo>();
	private CricDeCodePagerAdapter mPagerAdapter;

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
		performanceFragmentView = this;
		View view = inflater.inflate(R.layout.performance_fragment, container,
				false);
		return view;
	}

	public void init(final View view) {

		// Initialize arrays
		bat_runs = new int[2];
		bat_balls = new int[2];
		time_spent = new int[2];
		bat_fours = new int[2];
		bat_sixes = new int[2];
		lives = new int[2];
		how_out = new String[2];
		bowler_type = new String[2];
		fielding_pos = new String[2];

		overs = new float[2];
		spells = new int[2];
		maidens = new int[2];
		bowl_runs = new int[2];
		bowl_fours = new int[2];
		bowl_sixes = new int[2];
		wkts_left = new int[2];
		wkts_right = new int[2];
		bowl_catches_dropped = new int[2];
		noballs = new int[2];
		wides = new int[2];

		slip_catches = new int[2];
		close_catches = new int[2];
		circle_catches = new int[2];
		deep_catches = new int[2];
		circle_runouts = new int[2];
		circle_runouts_direct = new int[2];
		deep_runouts = new int[2];
		deep_runouts_direct = new int[2];
		stumpings = new int[2];
		byes = new int[2];
		misfields = new int[2];
		field_catches_dropped = new int[2];

		match_id = getArguments().getInt("rowId");
		innings = getArguments().getInt("innings");
		Log.d("Debug", "innings " + innings);

		inning_no = (Spinner) getSherlockActivity().getSupportActionBar()
				.getCustomView().findViewById(R.id.inning_no);
		ArrayAdapter<String> spinnerArrayAdapter;
		if (innings == 1) {
			spinnerArrayAdapter = new ArrayAdapter<String>(
					getSherlockActivity(),
					android.R.layout.simple_spinner_item,
					new String[] { "1st Innings" });
		} else {
			spinnerArrayAdapter = new ArrayAdapter<String>(
					getSherlockActivity(),
					android.R.layout.simple_spinner_item, new String[] {
							"1st Innings", "2nd Innings" });
		}
		spinnerArrayAdapter.setDropDownViewResource(R.layout.drop_down_menu_item);
		inning_no.setAdapter(spinnerArrayAdapter);
		current_innings = 0;
		inning_no
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> adapterView,
							View v, int i, long l) {
						if (current_innings != i) {
							Log.d("Debug", "On Selected Item called");
							int index = mTabHost.getCurrentTab();
							// saveInfo(index);
							current_innings = i;
							viewInfo(PerformanceFragmentEdit.BATTING);
							viewInfo(PerformanceFragmentEdit.BOWLING);
							viewInfo(PerformanceFragmentEdit.FIELDING);
							Log.d("Debug", "On Selected Item finished");
						}
					}

					public void onNothingSelected(AdapterView<?> adapterView) {
						return;
					}
				});

		initFetchFromDb();


	}

	public void initFetchFromDb() {
		Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE
				+ "/" + match_id);

		Cursor c = getSherlockActivity().getContentResolver().query(
				uri,
				new String[] { PerformanceDb.KEY_BAT_NUM,
						PerformanceDb.KEY_BAT_RUNS,
						PerformanceDb.KEY_BAT_BALLS,
						PerformanceDb.KEY_BAT_TIME,
						PerformanceDb.KEY_BAT_FOURS,
						PerformanceDb.KEY_BAT_SIXES,
						PerformanceDb.KEY_BAT_HOW_OUT,
						PerformanceDb.KEY_BAT_BOWLER_TYPE,
						PerformanceDb.KEY_BAT_FIELDING_POSITION,
						PerformanceDb.KEY_BAT_CHANCES,
						PerformanceDb.KEY_BOWL_BALLS,
						PerformanceDb.KEY_BOWL_SPELLS,
						PerformanceDb.KEY_BOWL_MAIDENS,
						PerformanceDb.KEY_BOWL_RUNS,
						PerformanceDb.KEY_BOWL_FOURS,
						PerformanceDb.KEY_BOWL_SIXES,
						PerformanceDb.KEY_BOWL_WKTS_LEFT,
						PerformanceDb.KEY_BOWL_WKTS_RIGHT,
						PerformanceDb.KEY_BOWL_CATCHES_DROPPED,
						PerformanceDb.KEY_BOWL_NOBALLS,
						PerformanceDb.KEY_BOWL_WIDES,
						PerformanceDb.KEY_FIELD_SLIP_CATCH,
						PerformanceDb.KEY_FIELD_CLOSE_CATCH,
						PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
						PerformanceDb.KEY_FIELD_DEEP_CATCH,
						PerformanceDb.KEY_FIELD_RO_CIRCLE,
						PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE,
						PerformanceDb.KEY_FIELD_RO_DEEP,
						PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP,
						PerformanceDb.KEY_FIELD_STUMPINGS,
						PerformanceDb.KEY_FIELD_BYES,
						PerformanceDb.KEY_FIELD_MISFIELDS,
						PerformanceDb.KEY_FIELD_CATCHES_DROPPED }, null, null,
				PerformanceDb.KEY_INNING);

		if (c.getCount() != 0) {
			Log.d("Debug", "Displaying for db " + c.getCount());
			c.moveToFirst();
			for (int i = 1; i <= innings; i++) {

				batting_no[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_NUM));
				bat_runs[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_RUNS));
				bat_balls[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BALLS));
				time_spent[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_TIME));
				bat_fours[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FOURS));
				bat_sixes[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_SIXES));
				lives[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_CHANCES));
				how_out[i - 1] = c.getString(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_HOW_OUT));
				bowler_type[i - 1] = c
						.getString(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_BOWLER_TYPE));
				fielding_pos[i - 1] = c
						.getString(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_BAT_FIELDING_POSITION));

				spells[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SPELLS));
				int balls = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_BALLS));
				overs[i - 1] = balls / 6 + (float) (balls % 6) / 10;
				Log.d("Debug", " overs Innings" + i + " " + overs[i - 1]);
				maidens[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_MAIDENS));
				bowl_runs[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_RUNS));
				bowl_fours[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_FOURS));
				bowl_sixes[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SIXES));
				wkts_left[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_LEFT));
				wkts_right[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WKTS_RIGHT));
				bowl_catches_dropped[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_CATCHES_DROPPED));
				noballs[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_NOBALLS));
				wides[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_WIDES));

				slip_catches[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_SLIP_CATCH));
				close_catches[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CLOSE_CATCH));
				circle_catches[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CIRCLE_CATCH));
				deep_catches[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_DEEP_CATCH));
				circle_runouts[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_CIRCLE));
				circle_runouts_direct[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE));
				deep_runouts[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DEEP));
				deep_runouts_direct[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP));
				stumpings[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_STUMPINGS));
				byes[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_BYES));
				misfields[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_MISFIELDS));
				field_catches_dropped[i - 1] = c
						.getInt(c
								.getColumnIndexOrThrow(PerformanceDb.KEY_FIELD_CATCHES_DROPPED));

				c.moveToNext();
			}
		}
		c.close();
		uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/"
				+ match_id);
		c = getSherlockActivity().getContentResolver().query(
				uri,
				new String[] { MatchDb.KEY_RESULT, MatchDb.KEY_REVIEW,
						MatchDb.KEY_DURATION, MatchDb.KEY_FIRST_ACTION,
						MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM,
						MatchDb.KEY_VENUE, MatchDb.KEY_LEVEL,
						MatchDb.KEY_MATCH_DATE, MatchDb.KEY_OVERS }, null,
				null, null);
		c.moveToFirst();
		result = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_RESULT));
		review = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_REVIEW));
		duration = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_DURATION));
		first = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_FIRST_ACTION));
		my_team = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_MY_TEAM));
		opponent_team = c.getString(c
				.getColumnIndexOrThrow(MatchDb.KEY_OPPONENT_TEAM));
		venue = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_VENUE));
		level = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_LEVEL));
		date = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_MATCH_DATE));
		match_overs = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_OVERS));
		if (match_overs.equals("-1")) {
			match_overs = "Unlimited";
		}
		c.close();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		if (savedInstanceState != null) {
			// Restore the previously serialized current tab position.
			Log.d("Debug", "On Restore Instance State called");

			current_innings = savedInstanceState.getInt("current_innings");
			inning_no.setSelection(current_innings);

			match_overs = savedInstanceState.getString("match_overs");
			result = savedInstanceState.getString("result");
			review = savedInstanceState.getString("review");
			duration = savedInstanceState.getString("duration");
			first = savedInstanceState.getString("first");
			my_team = savedInstanceState.getString("my_team");
			opponent_team = savedInstanceState.getString("opponent_team");
			venue = savedInstanceState.getString("venue");
			level = savedInstanceState.getString("level");
			date = savedInstanceState.getString("date");

			batting_no = savedInstanceState.getIntArray("batting_no");
			bat_runs = savedInstanceState.getIntArray("bat_runs");
			bat_balls = savedInstanceState.getIntArray("bat_balls");
			time_spent = savedInstanceState.getIntArray("time_spent");
			bat_fours = savedInstanceState.getIntArray("bat_fours");
			bat_sixes = savedInstanceState.getIntArray("bat_sixes");
			how_out = savedInstanceState.getStringArray("how_out");
			bowler_type = savedInstanceState.getStringArray("bowler_type");
			fielding_pos = savedInstanceState.getStringArray("fielding_pos");

			overs = savedInstanceState.getFloatArray("overs");
			spells = savedInstanceState.getIntArray("spells");
			maidens = savedInstanceState.getIntArray("maidens");
			bowl_runs = savedInstanceState.getIntArray("bowl_runs");
			bowl_fours = savedInstanceState.getIntArray("bowl_fours");
			bowl_sixes = savedInstanceState.getIntArray("bowl_sixes");
			wkts_left = savedInstanceState.getIntArray("wkts_left");
			wkts_right = savedInstanceState.getIntArray("wkts_right");
			bowl_catches_dropped = savedInstanceState
					.getIntArray("bowl_catches_dropped");
			noballs = savedInstanceState.getIntArray("noballs");
			wides = savedInstanceState.getIntArray("wides");

			slip_catches = savedInstanceState.getIntArray("slip_catches");
			close_catches = savedInstanceState.getIntArray("close_catches");
			circle_catches = savedInstanceState.getIntArray("circle_catches");
			deep_catches = savedInstanceState.getIntArray("deep_catches");
			circle_runouts = savedInstanceState.getIntArray("circle_runouts");
			circle_runouts_direct = savedInstanceState
					.getIntArray("circle_runouts_direct");
			deep_runouts = savedInstanceState.getIntArray("deep_runouts");
			deep_runouts_direct = savedInstanceState
					.getIntArray("deep_runouts_direct");
			stumpings = savedInstanceState.getIntArray("stumpings");
			byes = savedInstanceState.getIntArray("byes");
			misfields = savedInstanceState.getIntArray("misfields");
			field_catches_dropped = savedInstanceState
					.getIntArray("field_catches_dropped");

			Log.d("Debug", "On Restore Instance State finished");
		}
		this.initialiseTabHost(view, savedInstanceState);
		this.intialiseViewPager(view);
		TabPatchView tabPatchView = new TabPatchView(getSherlockActivity());
		RelativeLayout relativeLayout = (RelativeLayout) view
				.findViewById(R.id.performance_fragment_layout);
		relativeLayout.addView(tabPatchView);
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());

		outState.putInt("current_innings", current_innings);

		// saveInfo(mTabHost.getCurrentTab());

		outState.putString("result", result);
		outState.putString("match_overs", match_overs);
		outState.putString("review", review);
		outState.putString("duration", duration);
		outState.putString("first", first);
		outState.putString("my_team", my_team);
		outState.putString("opponent_team", opponent_team);
		outState.putString("venue", venue);
		outState.putString("level", level);
		outState.putString("date", date);

		outState.putIntArray("batting_no", batting_no);
		outState.putIntArray("bat_runs", bat_runs);
		outState.putIntArray("bat_balls", bat_balls);
		outState.putIntArray("time_spent", time_spent);
		outState.putIntArray("bat_fours", bat_fours);
		outState.putIntArray("bat_sixes", bat_sixes);
		outState.putIntArray("lives", lives);
		outState.putStringArray("how_out", how_out);
		outState.putStringArray("bowler_type", bowler_type);
		outState.putStringArray("fielding_pos", fielding_pos);

		outState.putFloatArray("overs", overs);
		outState.putIntArray("spells", spells);
		outState.putIntArray("maidens", maidens);
		outState.putIntArray("bowl_runs", bowl_runs);
		outState.putIntArray("bowl_fours", bowl_fours);
		outState.putIntArray("bowl_sixes", bowl_sixes);
		outState.putIntArray("wkts_left", wkts_left);
		outState.putIntArray("wkts_right", wkts_right);
		outState.putIntArray("bowl_catches_dropped", bowl_catches_dropped);
		outState.putIntArray("noballs", noballs);
		outState.putIntArray("wides", wides);

		outState.putIntArray("slip_catches", slip_catches);
		outState.putIntArray("close_catches", close_catches);
		outState.putIntArray("circle_catches", circle_catches);
		outState.putIntArray("deep_catches", deep_catches);
		outState.putIntArray("circle_runouts", circle_runouts);
		outState.putIntArray("circle_runouts_direct", circle_runouts_direct);
		outState.putIntArray("deep_runouts", deep_runouts);
		outState.putIntArray("deep_runouts_direct", deep_runouts_direct);
		outState.putIntArray("stumpings", stumpings);
		outState.putIntArray("byes", byes);
		outState.putIntArray("misfields", misfields);
		outState.putIntArray("field_catches_dropped", field_catches_dropped);
		Log.d("Debug", "On Save Instance finished");
	}

	private void intialiseViewPager(View view) {

		List<SherlockFragment> fragments = new Vector<SherlockFragment>();
		fragments.add((SherlockFragment) SherlockFragment.instantiate(
				getSherlockActivity(),
				PerformanceGeneralFragmentView.class.getName()));
		fragments.add((SherlockFragment) SherlockFragment.instantiate(
				getSherlockActivity(),
				PerformanceBattingFragmentView.class.getName()));
		fragments.add((SherlockFragment) SherlockFragment.instantiate(
				getSherlockActivity(),
				PerformanceBowlingFragmentView.class.getName()));
		fragments.add((SherlockFragment) SherlockFragment.instantiate(
				getSherlockActivity(),
				PerformanceFieldingFragmentView.class.getName()));

		this.mPagerAdapter = new CricDeCodePagerAdapter(
				getChildFragmentManager(), fragments);

		this.mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
		this.mViewPager.setAdapter(this.mPagerAdapter);
		this.mViewPager.setCurrentItem(mTabHost.getCurrentTab());
		current_position = mTabHost.getCurrentTab();
		this.mViewPager.setOnPageChangeListener(this);
		this.mViewPager.setOffscreenPageLimit(3);
	}

	private void initialiseTabHost(View view, Bundle args) {
		mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		PerformanceFragmentView.AddTab(this, this.mTabHost, this.mTabHost
				.newTabSpec("General").setIndicator("General"),
				(tabInfo = new TabInfo("General",
						PerformanceBattingFragmentView.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		PerformanceFragmentView.AddTab(this, this.mTabHost, this.mTabHost
				.newTabSpec("Batting").setIndicator("Batting"),
				(tabInfo = new TabInfo("Batting",
						PerformanceBattingFragmentView.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		PerformanceFragmentView.AddTab(this, this.mTabHost, this.mTabHost
				.newTabSpec("Bowling").setIndicator("Bowling"),
				(tabInfo = new TabInfo("Bowling",
						PerformanceBowlingFragmentView.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		PerformanceFragmentView.AddTab(this, this.mTabHost, this.mTabHost
				.newTabSpec("Fielding").setIndicator("Fielding"),
				(tabInfo = new TabInfo("Fielding",
						PerformanceFieldingFragmentView.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		if (args != null) {
			mTabHost.setCurrentTabByTag(args.getString("tab"));
		}
		MainActivity.customizeTabs(mTabHost);
		mTabHost.setOnTabChangedListener(this);
		
	}

	private static void AddTab(PerformanceFragmentView performanceFragment,
			TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(performanceFragment.new TabFactory(
				performanceFragment.getSherlockActivity()));
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
		// saveInfo(current_position);
		current_position = position;
		// viewInfo(current_position);
		this.mTabHost.setCurrentTab(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	/*
	 * public void saveInfo(int id) { String str; Log.d("Debug", "Save Info " +
	 * id); switch (id) { case PerformanceFragmentEdit.GENERAL: result =
	 * PerformanceGeneralFragmentView
	 * .performanceGeneralFragmentView.match_result .getText().toString();
	 * review =
	 * PerformanceGeneralFragmentView.performanceGeneralFragmentView.match_review
	 * .getText().toString(); duration =
	 * PerformanceGeneralFragmentView.performanceGeneralFragmentView.duration
	 * .getText().toString(); first =
	 * PerformanceGeneralFragmentView.performanceGeneralFragmentView.first
	 * .getText().toString(); my_team =
	 * PerformanceGeneralFragmentView.performanceGeneralFragmentView.my_team
	 * .getText().toString(); opponent_team =
	 * PerformanceGeneralFragmentView.performanceGeneralFragmentView
	 * .opponent_team .getText().toString(); venue =
	 * PerformanceGeneralFragmentView.performanceGeneralFragmentView.venue
	 * .getText().toString(); level =
	 * PerformanceGeneralFragmentView.performanceGeneralFragmentView.level
	 * .getText().toString(); date =
	 * PerformanceGeneralFragmentView.performanceGeneralFragmentView.date
	 * .getText().toString(); match_overs =
	 * PerformanceGeneralFragmentView.performanceGeneralFragmentView.match_overs
	 * .getText().toString(); break; case PerformanceFragmentEdit.BATTING: str =
	 * PerformanceBattingFragmentView.performanceBattingFragmentView.batting_no
	 * .getText().toString(); if (!str.equals("")) { batting_no[current_innings]
	 * = Integer.parseInt(str); } else { batting_no[current_innings] = 1; } str
	 * = PerformanceBattingFragmentView.performanceBattingFragmentView.runs
	 * .getText().toString(); if (!str.equals("")) { bat_runs[current_innings] =
	 * Integer.parseInt(str); } else { bat_runs[current_innings] = 0; } str =
	 * PerformanceBattingFragmentView.performanceBattingFragmentView.balls
	 * .getText().toString(); if (!str.equals("")) { bat_balls[current_innings]
	 * = Integer.parseInt(str); } else { bat_balls[current_innings] = 0; } str =
	 * PerformanceBattingFragmentView.performanceBattingFragmentView.time_spent
	 * .getText().toString(); if (!str.equals("")) { time_spent[current_innings]
	 * = Integer.parseInt(str); } else { time_spent[current_innings] = 0; } str
	 * = PerformanceBattingFragmentView.performanceBattingFragmentView.fours
	 * .getText().toString(); if (!str.equals("")) { bat_fours[current_innings]
	 * = Integer.parseInt(str); } else { bat_fours[current_innings] = 0; } str =
	 * PerformanceBattingFragmentView.performanceBattingFragmentView.sixes
	 * .getText().toString(); if (!str.equals("")) { bat_sixes[current_innings]
	 * = Integer.parseInt(str); } else { bat_sixes[current_innings] = 0; } str =
	 * PerformanceBattingFragmentView.performanceBattingFragmentView.lives
	 * .getText().toString(); if (!str.equals("")) { lives[current_innings] =
	 * Integer.parseInt(str); } else { lives[current_innings] = 0; }
	 * how_out[current_innings] =
	 * PerformanceBattingFragmentView.performanceBattingFragmentView.how_out
	 * .getText().toString(); bowler_type[current_innings] =
	 * PerformanceBattingFragmentView.performanceBattingFragmentView.bowler_type
	 * .getText().toString(); fielding_pos[current_innings] =
	 * PerformanceBattingFragmentView
	 * .performanceBattingFragmentView.fielding_pos .getText().toString();
	 * break; case PerformanceFragmentEdit.BOWLING: str =
	 * PerformanceBowlingFragmentView.performanceBowlingFragmentView.overs
	 * .getText().toString(); if (!str.equals("")) { overs[current_innings] =
	 * Float.parseFloat(str); } else { overs[current_innings] = 0; } str =
	 * PerformanceBowlingFragmentView.performanceBowlingFragmentView.spells
	 * .getText().toString(); if (!str.equals("")) { spells[current_innings] =
	 * Integer.parseInt(str); } else { spells[current_innings] = 0; } str =
	 * PerformanceBowlingFragmentView.performanceBowlingFragmentView.maidens
	 * .getText().toString(); if (!str.equals("")) { maidens[current_innings] =
	 * Integer.parseInt(str); } else { maidens[current_innings] = 0; } str =
	 * PerformanceBowlingFragmentView.performanceBowlingFragmentView.runs
	 * .getText().toString(); if (!str.equals("")) { bowl_runs[current_innings]
	 * = Integer.parseInt(str); } else { bowl_runs[current_innings] = 0; } str =
	 * PerformanceBowlingFragmentView.performanceBowlingFragmentView.fours
	 * .getText().toString(); if (!str.equals("")) { bowl_fours[current_innings]
	 * = Integer.parseInt(str); } else { bowl_fours[current_innings] = 0; } str
	 * = PerformanceBowlingFragmentView.performanceBowlingFragmentView.sixes
	 * .getText().toString(); if (!str.equals("")) { bowl_sixes[current_innings]
	 * = Integer.parseInt(str); } else { bowl_sixes[current_innings] = 0; } str
	 * = PerformanceBowlingFragmentView.performanceBowlingFragmentView.wkts_left
	 * .getText().toString(); if (!str.equals("")) { wkts_left[current_innings]
	 * = Integer.parseInt(str); } else { wkts_left[current_innings] = 0; } str =
	 * PerformanceBowlingFragmentView.performanceBowlingFragmentView.wkts_right
	 * .getText().toString(); if (!str.equals("")) { wkts_right[current_innings]
	 * = Integer.parseInt(str); } else { wkts_right[current_innings] = 0; } str
	 * =
	 * PerformanceBowlingFragmentView.performanceBowlingFragmentView.catches_dropped
	 * .getText().toString(); if (!str.equals("")) {
	 * bowl_catches_dropped[current_innings] = Integer.parseInt(str); } else {
	 * bowl_catches_dropped[current_innings] = 0; } str =
	 * PerformanceBowlingFragmentView.performanceBowlingFragmentView.noballs
	 * .getText().toString(); if (!str.equals("")) { noballs[current_innings] =
	 * Integer.parseInt(str); } else { noballs[current_innings] = 0; } str =
	 * PerformanceBowlingFragmentView.performanceBowlingFragmentView.wides
	 * .getText().toString(); if (!str.equals("")) { wides[current_innings] =
	 * Integer.parseInt(str); } else { wides[current_innings] = 0; } break; case
	 * PerformanceFragmentEdit.FIELDING: str =
	 * PerformanceFieldingFragmentView.performanceFieldingFragmentView
	 * .slip_catches .getText().toString(); if (!str.equals("")) {
	 * slip_catches[current_innings] = Integer.parseInt(str); } else {
	 * slip_catches[current_innings] = 0; } str =
	 * PerformanceFieldingFragmentView
	 * .performanceFieldingFragmentView.close_catches .getText().toString(); if
	 * (!str.equals("")) { close_catches[current_innings] =
	 * Integer.parseInt(str); } else { close_catches[current_innings] = 0; } str
	 * = PerformanceFieldingFragmentView.performanceFieldingFragmentView.
	 * circle_catches .getText().toString(); if (!str.equals("")) {
	 * circle_catches[current_innings] = Integer.parseInt(str); } else {
	 * circle_catches[current_innings] = 0; } str =
	 * PerformanceFieldingFragmentView
	 * .performanceFieldingFragmentView.deep_catches .getText().toString(); if
	 * (!str.equals("")) { deep_catches[current_innings] =
	 * Integer.parseInt(str); } else { deep_catches[current_innings] = 0; } str
	 * = PerformanceFieldingFragmentView.performanceFieldingFragmentView.
	 * circle_runouts .getText().toString(); if (!str.equals("")) {
	 * circle_runouts[current_innings] = Integer.parseInt(str); } else {
	 * circle_runouts[current_innings] = 0; } str =
	 * PerformanceFieldingFragmentView
	 * .performanceFieldingFragmentView.circle_runouts_direct
	 * .getText().toString(); if (!str.equals("")) {
	 * circle_runouts_direct[current_innings] = Integer.parseInt(str); } else {
	 * circle_runouts_direct[current_innings] = 0; } str =
	 * PerformanceFieldingFragmentView
	 * .performanceFieldingFragmentView.deep_runouts .getText().toString(); if
	 * (!str.equals("")) { deep_runouts[current_innings] =
	 * Integer.parseInt(str); } else { deep_runouts[current_innings] = 0; } str
	 * = PerformanceFieldingFragmentView.performanceFieldingFragmentView.
	 * deep_runouts_direct .getText().toString(); if (!str.equals("")) {
	 * deep_runouts_direct[current_innings] = Integer.parseInt(str); } else {
	 * deep_runouts_direct[current_innings] = 0; } str =
	 * PerformanceFieldingFragmentView.performanceFieldingFragmentView.stumpings
	 * .getText().toString(); if (!str.equals("")) { stumpings[current_innings]
	 * = Integer.parseInt(str); } else { stumpings[current_innings] = 0; } str =
	 * PerformanceFieldingFragmentView.performanceFieldingFragmentView.byes
	 * .getText().toString(); if (!str.equals("")) { byes[current_innings] =
	 * Integer.parseInt(str); } else { byes[current_innings] = 0; } str =
	 * PerformanceFieldingFragmentView.performanceFieldingFragmentView.misfields
	 * .getText().toString(); if (!str.equals("")) { misfields[current_innings]
	 * = Integer.parseInt(str); } else { misfields[current_innings] = 0; } str =
	 * PerformanceFieldingFragmentView
	 * .performanceFieldingFragmentView.catches_dropped .getText().toString();
	 * if (!str.equals("")) { field_catches_dropped[current_innings] =
	 * Integer.parseInt(str); } else { field_catches_dropped[current_innings] =
	 * 0; } break; default: break; } Log.d("Debug", "Save Info finished"); }
	 */

	public void viewInfo(int tab_index) {
		Log.d("Debug", "On View Info called " + tab_index);
		switch (tab_index) {
		case PerformanceFragmentEdit.GENERAL:
			PerformanceGeneralFragmentView.performanceGeneralFragmentView.match_result
					.setText(result);
			if (review.equals("")) {
				PerformanceGeneralFragmentView.performanceGeneralFragmentView.match_review
						.setText("LEFT BLANK");
			} else {
				PerformanceGeneralFragmentView.performanceGeneralFragmentView.match_review
						.setText(review);
			}
			PerformanceGeneralFragmentView.performanceGeneralFragmentView.first
					.setText(first);
			PerformanceGeneralFragmentView.performanceGeneralFragmentView.duration
					.setText(duration);
			PerformanceGeneralFragmentView.performanceGeneralFragmentView.my_team
					.setText(my_team);
			PerformanceGeneralFragmentView.performanceGeneralFragmentView.opponent_team
					.setText(opponent_team);
			PerformanceGeneralFragmentView.performanceGeneralFragmentView.venue
					.setText(venue);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
					Locale.getDefault());
			Date d = new Date();
			try {
				d = sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
				Log.d("Debug", "Date Exception");
			}
			PerformanceGeneralFragmentView.performanceGeneralFragmentView.date
					.setText(DateFormat.format("MMMM dd, yyyy", d).toString());
			PerformanceGeneralFragmentView.performanceGeneralFragmentView.level
					.setText(level);
			PerformanceGeneralFragmentView.performanceGeneralFragmentView.match_overs
					.setText(match_overs);
			break;
		case PerformanceFragmentEdit.BATTING:
			PerformanceBattingFragmentView.performanceBattingFragmentView.batting_no
					.setText(batting_no[current_innings] + "");
			PerformanceBattingFragmentView.performanceBattingFragmentView.runs
					.setText(bat_runs[current_innings] + "");
			PerformanceBattingFragmentView.performanceBattingFragmentView.balls
					.setText(bat_balls[current_innings] + "");
			PerformanceBattingFragmentView.performanceBattingFragmentView.time_spent
					.setText(time_spent[current_innings] + "");
			PerformanceBattingFragmentView.performanceBattingFragmentView.fours
					.setText(bat_fours[current_innings] + "");
			PerformanceBattingFragmentView.performanceBattingFragmentView.sixes
					.setText(bat_sixes[current_innings] + "");
			PerformanceBattingFragmentView.performanceBattingFragmentView.lives
					.setText(lives[current_innings] + "");
			PerformanceBattingFragmentView.performanceBattingFragmentView.how_out
					.setText(how_out[current_innings] + "");
			PerformanceBattingFragmentView.performanceBattingFragmentView.bowler_type
					.setText(bowler_type[current_innings] + "");
			PerformanceBattingFragmentView.performanceBattingFragmentView.fielding_pos
					.setText(fielding_pos[current_innings] + "");
			String str = how_out[current_innings] + "";
			if (str.equals("Not Out") || str.equals("Retired")
					|| str.equals("Timed Out")
					|| str.equals("Obstructing the Field")
					|| str.equals("Run Out")) {
				PerformanceBattingFragmentView.performanceBattingFragmentView.lbl_bowler_type
						.setVisibility(View.GONE);
				PerformanceBattingFragmentView.performanceBattingFragmentView.bowler_type
						.setVisibility(View.GONE);
			} else {
				PerformanceBattingFragmentView.performanceBattingFragmentView.lbl_bowler_type
						.setVisibility(View.VISIBLE);
				PerformanceBattingFragmentView.performanceBattingFragmentView.bowler_type
						.setVisibility(View.VISIBLE);
			}
			if (str.equals("Caught") || str.equals("Run Out")) {
				PerformanceBattingFragmentView.performanceBattingFragmentView.lbl_fielding_pos
						.setVisibility(View.VISIBLE);
				PerformanceBattingFragmentView.performanceBattingFragmentView.fielding_pos
						.setVisibility(View.VISIBLE);
			} else {
				PerformanceBattingFragmentView.performanceBattingFragmentView.lbl_fielding_pos
						.setVisibility(View.GONE);
				PerformanceBattingFragmentView.performanceBattingFragmentView.fielding_pos
						.setVisibility(View.GONE);

			}
			if (PerformanceBattingFragmentView.performanceBattingFragmentView.how_out
					.getText().toString().equals("Not Out")
					&& PerformanceBattingFragmentView.performanceBattingFragmentView.balls
							.getText().toString().equals("0")) {
				PerformanceBattingFragmentView.performanceBattingFragmentView.batting_info
						.setVisibility(View.GONE);
				PerformanceBattingFragmentView.performanceBattingFragmentView.batted
						.setVisibility(View.VISIBLE);
			} else {
				PerformanceBattingFragmentView.performanceBattingFragmentView.batting_info
						.setVisibility(View.VISIBLE);
				PerformanceBattingFragmentView.performanceBattingFragmentView.batted
						.setVisibility(View.GONE);
			}
			break;
		case PerformanceFragmentEdit.BOWLING:
			PerformanceBowlingFragmentView.performanceBowlingFragmentView.overs
					.setText(overs[current_innings] + "");
			PerformanceBowlingFragmentView.performanceBowlingFragmentView.spells
					.setText(spells[current_innings] + "");
			PerformanceBowlingFragmentView.performanceBowlingFragmentView.maidens
					.setText(maidens[current_innings] + "");
			PerformanceBowlingFragmentView.performanceBowlingFragmentView.runs
					.setText(bowl_runs[current_innings] + "");
			PerformanceBowlingFragmentView.performanceBowlingFragmentView.fours
					.setText(bowl_fours[current_innings] + "");
			PerformanceBowlingFragmentView.performanceBowlingFragmentView.sixes
					.setText(bowl_sixes[current_innings] + "");
			PerformanceBowlingFragmentView.performanceBowlingFragmentView.wkts_left
					.setText(wkts_left[current_innings] + "");
			PerformanceBowlingFragmentView.performanceBowlingFragmentView.wkts_right
					.setText(wkts_right[current_innings] + "");
			PerformanceBowlingFragmentView.performanceBowlingFragmentView.catches_dropped
					.setText(bowl_catches_dropped[current_innings] + "");
			PerformanceBowlingFragmentView.performanceBowlingFragmentView.noballs
					.setText(noballs[current_innings] + "");
			PerformanceBowlingFragmentView.performanceBowlingFragmentView.wides
					.setText(wides[current_innings] + "");
			Log.d("Debug", " overs Innings" + current_innings + " "
					+ overs[current_innings]);
			if (overs[current_innings] == 0.0f) {
				Log.d("Debug", " In zero overs");
				PerformanceBowlingFragmentView.performanceBowlingFragmentView.bowling_info
						.setVisibility(View.GONE);
				PerformanceBowlingFragmentView.performanceBowlingFragmentView.bowled
						.setVisibility(View.VISIBLE);
			} else {
				PerformanceBowlingFragmentView.performanceBowlingFragmentView.bowling_info
						.setVisibility(View.VISIBLE);
				PerformanceBowlingFragmentView.performanceBowlingFragmentView.bowled
						.setVisibility(View.GONE);
			}
			break;
		case PerformanceFragmentEdit.FIELDING:
			PerformanceFieldingFragmentView.performanceFieldingFragmentView.slip_catches
					.setText(slip_catches[current_innings] + "");
			PerformanceFieldingFragmentView.performanceFieldingFragmentView.close_catches
					.setText(close_catches[current_innings] + "");
			PerformanceFieldingFragmentView.performanceFieldingFragmentView.circle_catches
					.setText(circle_catches[current_innings] + "");
			PerformanceFieldingFragmentView.performanceFieldingFragmentView.deep_catches
					.setText(deep_catches[current_innings] + "");
			PerformanceFieldingFragmentView.performanceFieldingFragmentView.circle_runouts
					.setText(circle_runouts[current_innings] + "");
			PerformanceFieldingFragmentView.performanceFieldingFragmentView.circle_runouts_direct
					.setText(circle_runouts_direct[current_innings] + "");
			PerformanceFieldingFragmentView.performanceFieldingFragmentView.deep_runouts
					.setText(deep_runouts[current_innings] + "");
			PerformanceFieldingFragmentView.performanceFieldingFragmentView.deep_runouts_direct
					.setText(deep_runouts_direct[current_innings] + "");
			PerformanceFieldingFragmentView.performanceFieldingFragmentView.stumpings
					.setText(stumpings[current_innings] + "");
			PerformanceFieldingFragmentView.performanceFieldingFragmentView.byes
					.setText(byes[current_innings] + "");
			PerformanceFieldingFragmentView.performanceFieldingFragmentView.misfields
					.setText(misfields[current_innings] + "");
			PerformanceFieldingFragmentView.performanceFieldingFragmentView.catches_dropped
					.setText(field_catches_dropped[current_innings] + "");
			break;
		default:
			break;
		}
		Log.d("Debug", "On View info Finished");
	}

	public static float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

}
