package co.acjs.cricdecode;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class PerformanceFragmentEdit extends SherlockFragment implements
		ViewPager.OnPageChangeListener {

	public static final int GENERAL = 0, BATTING = 1, BOWLING = 2,
			FIELDING = 3;

	static PerformanceFragmentEdit performanceFragmentEdit;

	// Declare Fields
	// Match
	Spinner inning_no;
	int match_id, innings, current_innings, current_position;
	String device_id;

	// General
	private String result, review, duration, first, my_team, opponent_team,
			venue, level, day, month, year, match_overs;

	// Batting
	private int[] batting_no = { 1, 1 }, bat_runs, bat_balls, time_spent,
			bat_fours, bat_sixes, lives;
	private boolean[] batted;
	private String[] how_out, bowler_type, fielding_pos;

	// Bowling
	private int[] spells, maidens, bowl_runs, bowl_fours, bowl_sixes,
			wkts_left, wkts_right, bowl_catches_dropped, noballs, wides;
	private float[] overs;
	private boolean[] bowled;

	// Fielding
	private int[] slip_catches, close_catches, circle_catches, deep_catches,
			circle_runouts, circle_runouts_direct, deep_runouts,
			deep_runouts_direct, stumpings, byes, misfields,
			field_catches_dropped;

	ViewPager mViewPager;
	private CricDeCodePagerAdapter mPagerAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		performanceFragmentEdit = this;
		View view = inflater.inflate(R.layout.performance_fragment, container,
				false);
		return view;
	}

	public void init() {

		// Initialize arrays
		result = getSherlockActivity().getResources().getStringArray(
				R.array.match_result)[0];
		Log.d("Debug", "Result init " + result);
		duration = getSherlockActivity().getResources().getStringArray(
				R.array.match_duration)[0];
		first = getSherlockActivity().getResources().getStringArray(
				R.array.first)[0];

		batted = new boolean[2];
		bat_runs = new int[2];
		bat_balls = new int[2];
		time_spent = new int[2];
		bat_fours = new int[2];
		bat_sixes = new int[2];
		lives = new int[2];
		how_out = new String[] {
				getSherlockActivity().getResources().getStringArray(
						R.array.how_out)[0],
				getSherlockActivity().getResources().getStringArray(
						R.array.how_out)[0] };
		bowler_type = new String[] {
				getSherlockActivity().getResources().getStringArray(
						R.array.bowling_style)[0],
				getSherlockActivity().getResources().getStringArray(
						R.array.bowling_style)[0] };
		fielding_pos = new String[] {
				getSherlockActivity().getResources().getStringArray(
						R.array.fielding_pos)[0],
				getSherlockActivity().getResources().getStringArray(
						R.array.fielding_pos)[0] };

		bowled = new boolean[2];
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
		device_id = getArguments().getString("deviceId");
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
		spinnerArrayAdapter
				.setDropDownViewResource(R.layout.drop_down_menu_item);
		inning_no.setAdapter(spinnerArrayAdapter);
		current_innings = 0;
		inning_no
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> adapterView,
							View v, int i, long l) {
						if (current_innings != i) {
							Log.d("Debug", "On Selected Item called");
							saveInfo(PerformanceFragmentEdit.BATTING);
							saveInfo(PerformanceFragmentEdit.BOWLING);
							saveInfo(PerformanceFragmentEdit.FIELDING);
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
				+ "/" + match_id + "/" + device_id);

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
				if (bat_balls[i - 1] != 0 || !how_out[i - 1].equals("Not Out")) {
					batted[i - 1] = true;
				}

				spells[i - 1] = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_SPELLS));
				int balls = c.getInt(c
						.getColumnIndexOrThrow(PerformanceDb.KEY_BOWL_BALLS));
				if (balls != 0) {
					bowled[i - 1] = true;
				}
				overs[i - 1] = balls / 6 + (float) (balls % 6) / 10;
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
				+ match_id + "/" + device_id);
		c = getSherlockActivity().getContentResolver().query(
				uri,
				new String[] { MatchDb.KEY_RESULT, MatchDb.KEY_REVIEW,
						MatchDb.KEY_DURATION, MatchDb.KEY_FIRST_ACTION,
						MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM,
						MatchDb.KEY_VENUE, MatchDb.KEY_LEVEL,
						MatchDb.KEY_MATCH_DATE, MatchDb.KEY_OVERS }, null,
				null, null);
		c.moveToFirst();
		String temp = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_RESULT));
		if (!temp.equals("")) {
			result = temp;
		}
		review = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_REVIEW));
		temp = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_DURATION));
		if (!temp.equals("")) {
			duration = temp;
		}
		temp = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_FIRST_ACTION));
		if (!temp.equals("")) {
			first = temp;
		}
		my_team = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_MY_TEAM));
		opponent_team = c.getString(c
				.getColumnIndexOrThrow(MatchDb.KEY_OPPONENT_TEAM));
		venue = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_VENUE));
		level = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_LEVEL));
		String date_str = c.getString(c
				.getColumnIndexOrThrow(MatchDb.KEY_MATCH_DATE));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Date date = new Date();
		try {
			date = sdf.parse(date_str);
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			day = cal.get(Calendar.DAY_OF_MONTH) + "";
			month = PerformanceFragmentView.month_str[cal.get(Calendar.MONTH)];
			year = cal.get(Calendar.YEAR) + "";
		} catch (ParseException e) {
			e.printStackTrace();
			Log.d("Debug", "Date Exception");
		}
		match_overs = c.getString(c.getColumnIndexOrThrow(MatchDb.KEY_OVERS));
		if (match_overs.equals("-1")) {
			match_overs = "Unlimited";
		}
		c.close();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
		if (savedInstanceState != null) {
			// Restore the previously serialized current tab position.
			Log.d("Debug", "On Restore Instance State called");

			current_position = savedInstanceState.getInt("current_position");
			MainActivity.changeTabLayout(current_position);
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
			day = savedInstanceState.getString("day");
			month = savedInstanceState.getString("month");
			year = savedInstanceState.getString("year");

			batted = savedInstanceState.getBooleanArray("batted");
			batting_no = savedInstanceState.getIntArray("batting_no");
			bat_runs = savedInstanceState.getIntArray("bat_runs");
			bat_balls = savedInstanceState.getIntArray("bat_balls");
			time_spent = savedInstanceState.getIntArray("time_spent");
			bat_fours = savedInstanceState.getIntArray("bat_fours");
			bat_sixes = savedInstanceState.getIntArray("bat_sixes");
			how_out = savedInstanceState.getStringArray("how_out");
			bowler_type = savedInstanceState.getStringArray("bowler_type");
			fielding_pos = savedInstanceState.getStringArray("fielding_pos");

			bowled = savedInstanceState.getBooleanArray("bowled");
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
		this.intialiseViewPager(view);
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("current_position", mViewPager.getCurrentItem());

		outState.putInt("current_innings", current_innings);
		outState.putBooleanArray("batted", batted);
		outState.putBooleanArray("bowled", bowled);

		saveInfo(mViewPager.getCurrentItem());

		outState.putString("result", result);
		outState.putString("match_overs", match_overs);
		outState.putString("review", review);
		outState.putString("duration", duration);
		outState.putString("first", first);
		outState.putString("my_team", my_team);
		outState.putString("opponent_team", opponent_team);
		outState.putString("venue", venue);
		outState.putString("level", level);
		outState.putString("day", day);
		outState.putString("month", month);
		outState.putString("year", year);

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
				PerformanceGeneralFragmentEdit.class.getName()));
		fragments.add((SherlockFragment) SherlockFragment.instantiate(
				getSherlockActivity(),
				PerformanceBattingFragmentEdit.class.getName()));
		fragments.add((SherlockFragment) SherlockFragment.instantiate(
				getSherlockActivity(),
				PerformanceBowlingFragmentEdit.class.getName()));
		fragments.add((SherlockFragment) SherlockFragment.instantiate(
				getSherlockActivity(),
				PerformanceFieldingFragmentEdit.class.getName()));

		this.mPagerAdapter = new CricDeCodePagerAdapter(
				getChildFragmentManager(), fragments);

		this.mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
		this.mViewPager.setAdapter(this.mPagerAdapter);
		this.mViewPager.setCurrentItem(current_position);
		this.mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				Log.d("PerformanceFragmentView", "**** onPageSelected = "
						+ position);
				current_position = position;
				MainActivity.changeTabLayout(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		this.mViewPager.setOffscreenPageLimit(3);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		current_position = position;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}

	public void saveInfo(int id) {
		String str;
		Log.d("Debug", "Save Info " + id);
		switch (id) {
		case GENERAL:
			result = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.match_result
					.getSelectedItem().toString();
			review = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.match_review
					.getText().toString();
			duration = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.duration
					.getSelectedItem().toString();
			first = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.first
					.getSelectedItem().toString();
			my_team = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.my_team
					.getText().toString();
			opponent_team = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.opponent_team
					.getText().toString();
			venue = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.venue
					.getText().toString();
			level = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.level
					.getText().toString();

			day = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.day
					.getText().toString();
			month = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.month
					.getText().toString();
			year = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.year
					.getText().toString();

			match_overs = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.match_overs
					.getText().toString();
			break;
		case BATTING:
			batted[current_innings] = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.bat_toggle
					.isChecked();
			str = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.batting_no
					.getText().toString();
			if (!str.equals("")) {
				batting_no[current_innings] = Integer.parseInt(str);
			} else {
				batting_no[current_innings] = 1;
			}
			str = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.runs
					.getText().toString();
			if (!str.equals("")) {
				bat_runs[current_innings] = Integer.parseInt(str);
			} else {
				bat_runs[current_innings] = 0;
			}
			str = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.balls
					.getText().toString();
			if (!str.equals("")) {
				bat_balls[current_innings] = Integer.parseInt(str);
			} else {
				bat_balls[current_innings] = 0;
			}
			str = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.time_spent
					.getText().toString();
			if (!str.equals("")) {
				time_spent[current_innings] = Integer.parseInt(str);
			} else {
				time_spent[current_innings] = 0;
			}
			str = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.fours
					.getText().toString();
			if (!str.equals("")) {
				bat_fours[current_innings] = Integer.parseInt(str);
			} else {
				bat_fours[current_innings] = 0;
			}
			str = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.sixes
					.getText().toString();
			if (!str.equals("")) {
				bat_sixes[current_innings] = Integer.parseInt(str);
			} else {
				bat_sixes[current_innings] = 0;
			}
			str = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.lives
					.getText().toString();
			if (!str.equals("")) {
				lives[current_innings] = Integer.parseInt(str);
			} else {
				lives[current_innings] = 0;
			}
			how_out[current_innings] = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.how_out
					.getSelectedItem().toString();
			bowler_type[current_innings] = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.bowler_type
					.getSelectedItem().toString();
			fielding_pos[current_innings] = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.fielding_pos
					.getSelectedItem().toString();
			break;
		case BOWLING:
			bowled[current_innings] = PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.bowl_toggle
					.isChecked();
			str = PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.overs
					.getText().toString();
			if (!str.equals("")) {
				overs[current_innings] = Float.parseFloat(str);
			} else {
				overs[current_innings] = 0;
			}
			str = PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.spells
					.getText().toString();
			if (!str.equals("")) {
				spells[current_innings] = Integer.parseInt(str);
			} else {
				spells[current_innings] = 0;
			}
			str = PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.maidens
					.getText().toString();
			if (!str.equals("")) {
				maidens[current_innings] = Integer.parseInt(str);
			} else {
				maidens[current_innings] = 0;
			}
			str = PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.runs
					.getText().toString();
			if (!str.equals("")) {
				bowl_runs[current_innings] = Integer.parseInt(str);
			} else {
				bowl_runs[current_innings] = 0;
			}
			str = PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.fours
					.getText().toString();
			if (!str.equals("")) {
				bowl_fours[current_innings] = Integer.parseInt(str);
			} else {
				bowl_fours[current_innings] = 0;
			}
			str = PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.sixes
					.getText().toString();
			if (!str.equals("")) {
				bowl_sixes[current_innings] = Integer.parseInt(str);
			} else {
				bowl_sixes[current_innings] = 0;
			}
			str = PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.wkts_left
					.getText().toString();
			if (!str.equals("")) {
				wkts_left[current_innings] = Integer.parseInt(str);
			} else {
				wkts_left[current_innings] = 0;
			}
			str = PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.wkts_right
					.getText().toString();
			if (!str.equals("")) {
				wkts_right[current_innings] = Integer.parseInt(str);
			} else {
				wkts_right[current_innings] = 0;
			}
			str = PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.catches_dropped
					.getText().toString();
			if (!str.equals("")) {
				bowl_catches_dropped[current_innings] = Integer.parseInt(str);
			} else {
				bowl_catches_dropped[current_innings] = 0;
			}
			str = PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.noballs
					.getText().toString();
			if (!str.equals("")) {
				noballs[current_innings] = Integer.parseInt(str);
			} else {
				noballs[current_innings] = 0;
			}
			str = PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.wides
					.getText().toString();
			if (!str.equals("")) {
				wides[current_innings] = Integer.parseInt(str);
			} else {
				wides[current_innings] = 0;
			}
			break;
		case FIELDING:
			str = PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.slip_catches
					.getText().toString();
			if (!str.equals("")) {
				slip_catches[current_innings] = Integer.parseInt(str);
			} else {
				slip_catches[current_innings] = 0;
			}
			str = PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.close_catches
					.getText().toString();
			if (!str.equals("")) {
				close_catches[current_innings] = Integer.parseInt(str);
			} else {
				close_catches[current_innings] = 0;
			}
			str = PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.circle_catches
					.getText().toString();
			if (!str.equals("")) {
				circle_catches[current_innings] = Integer.parseInt(str);
			} else {
				circle_catches[current_innings] = 0;
			}
			str = PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.deep_catches
					.getText().toString();
			if (!str.equals("")) {
				deep_catches[current_innings] = Integer.parseInt(str);
			} else {
				deep_catches[current_innings] = 0;
			}
			str = PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.circle_runouts
					.getText().toString();
			if (!str.equals("")) {
				circle_runouts[current_innings] = Integer.parseInt(str);
			} else {
				circle_runouts[current_innings] = 0;
			}
			str = PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.circle_runouts_direct
					.getText().toString();
			if (!str.equals("")) {
				circle_runouts_direct[current_innings] = Integer.parseInt(str);
			} else {
				circle_runouts_direct[current_innings] = 0;
			}
			str = PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.deep_runouts
					.getText().toString();
			if (!str.equals("")) {
				deep_runouts[current_innings] = Integer.parseInt(str);
			} else {
				deep_runouts[current_innings] = 0;
			}
			str = PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.deep_runouts_direct
					.getText().toString();
			if (!str.equals("")) {
				deep_runouts_direct[current_innings] = Integer.parseInt(str);
			} else {
				deep_runouts_direct[current_innings] = 0;
			}
			str = PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.stumpings
					.getText().toString();
			if (!str.equals("")) {
				stumpings[current_innings] = Integer.parseInt(str);
			} else {
				stumpings[current_innings] = 0;
			}
			str = PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.byes
					.getText().toString();
			if (!str.equals("")) {
				byes[current_innings] = Integer.parseInt(str);
			} else {
				byes[current_innings] = 0;
			}
			str = PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.misfields
					.getText().toString();
			if (!str.equals("")) {
				misfields[current_innings] = Integer.parseInt(str);
			} else {
				misfields[current_innings] = 0;
			}
			str = PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.catches_dropped
					.getText().toString();
			if (!str.equals("")) {
				field_catches_dropped[current_innings] = Integer.parseInt(str);
			} else {
				field_catches_dropped[current_innings] = 0;
			}
			break;
		default:
			break;
		}
		Log.d("Debug", "Save Info finished");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void viewInfo(int tab_index) {
		Log.d("Debug", "On View Info called " + tab_index);
		switch (tab_index) {
		case GENERAL:
			Spinner spinner = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.match_result;
			ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
			int spinnerPosition = myAdap.getPosition(result);
			Log.d("Debug", "Spinner Position " + spinnerPosition
					+ " ArrayAdapter " + myAdap.getCount() + " result "
					+ result);
			spinner.setSelection(spinnerPosition);
			PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.match_review
					.setText(review);
			spinner = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.duration;
			myAdap = (ArrayAdapter) spinner.getAdapter();
			spinnerPosition = myAdap.getPosition(duration);
			spinner.setSelection(spinnerPosition);
			spinner = PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.first;
			myAdap = (ArrayAdapter) spinner.getAdapter();
			spinnerPosition = myAdap.getPosition(first);
			spinner.setSelection(spinnerPosition);
			PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.my_team
					.setText(my_team);
			PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.opponent_team
					.setText(opponent_team);
			PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.venue
					.setText(venue);
			PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.day
					.setText(day);
			PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.month
					.setText(month);
			PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.year
					.setText(year);
			PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.level
					.setText(level);
			PerformanceGeneralFragmentEdit.performanceGeneralFragmentEdit.match_overs
					.setText(match_overs);
			break;
		case BATTING:

			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.my_team
					.setText(my_team);
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.opponent_team
					.setText(opponent_team);
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.venue
					.setText(venue);
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.day
					.setText(day);
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.month
					.setText(month);
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.year
					.setText(year);
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.level
					.setText(level);
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.match_overs
					.setText(match_overs);

			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.batting_no
					.setText(batting_no[current_innings] + "");
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.runs
					.setText(bat_runs[current_innings] + "");
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.balls
					.setText(bat_balls[current_innings] + "");
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.time_spent
					.setText(time_spent[current_innings] + "");
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.fours
					.setText(bat_fours[current_innings] + "");
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.sixes
					.setText(bat_sixes[current_innings] + "");
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.lives
					.setText(lives[current_innings] + "");
			spinner = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.how_out;
			myAdap = (ArrayAdapter) spinner.getAdapter();
			spinnerPosition = myAdap.getPosition(how_out[current_innings]);
			spinner.setSelection(spinnerPosition);
			spinner = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.bowler_type;
			myAdap = (ArrayAdapter) spinner.getAdapter();
			spinnerPosition = myAdap.getPosition(bowler_type[current_innings]);
			spinner.setSelection(spinnerPosition);
			spinner = PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.fielding_pos;
			myAdap = (ArrayAdapter) spinner.getAdapter();
			spinnerPosition = myAdap.getPosition(fielding_pos[current_innings]);
			spinner.setSelection(spinnerPosition);

			if (batted[current_innings]) {
				PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.batting_info
						.setVisibility(View.VISIBLE);
			} else {
				PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.batting_info
						.setVisibility(View.GONE);
			}
			PerformanceBattingFragmentEdit.performanceBattingFragmentEdit.bat_toggle
					.setChecked(batted[current_innings]);
			break;
		case BOWLING:
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.my_team
					.setText(my_team);
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.opponent_team
					.setText(opponent_team);
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.venue
					.setText(venue);
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.day
					.setText(day);
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.month
					.setText(month);
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.year
					.setText(year);
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.level
					.setText(level);
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.match_overs
					.setText(match_overs);

			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.overs
					.setText(overs[current_innings] + "");
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.spells
					.setText(spells[current_innings] + "");
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.maidens
					.setText(maidens[current_innings] + "");
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.runs
					.setText(bowl_runs[current_innings] + "");
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.fours
					.setText(bowl_fours[current_innings] + "");
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.sixes
					.setText(bowl_sixes[current_innings] + "");
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.wkts_left
					.setText(wkts_left[current_innings] + "");
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.wkts_right
					.setText(wkts_right[current_innings] + "");
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.catches_dropped
					.setText(bowl_catches_dropped[current_innings] + "");
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.noballs
					.setText(noballs[current_innings] + "");
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.wides
					.setText(wides[current_innings] + "");

			if (bowled[current_innings]) {
				PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.bowling_info
						.setVisibility(View.VISIBLE);

			} else {
				PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.bowling_info
						.setVisibility(View.GONE);
			}
			PerformanceBowlingFragmentEdit.performanceBowlingFragmentEdit.bowl_toggle
					.setChecked(bowled[current_innings]);
			break;
		case FIELDING:
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.my_team
					.setText(my_team);
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.opponent_team
					.setText(opponent_team);
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.venue
					.setText(venue);
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.day
					.setText(day);
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.month
					.setText(month);
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.year
					.setText(year);
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.level
					.setText(level);
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.match_overs
					.setText(match_overs);

			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.slip_catches
					.setText(slip_catches[current_innings] + "");
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.close_catches
					.setText(close_catches[current_innings] + "");
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.circle_catches
					.setText(circle_catches[current_innings] + "");
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.deep_catches
					.setText(deep_catches[current_innings] + "");
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.circle_runouts
					.setText(circle_runouts[current_innings] + "");
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.circle_runouts_direct
					.setText(circle_runouts_direct[current_innings] + "");
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.deep_runouts
					.setText(deep_runouts[current_innings] + "");
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.deep_runouts_direct
					.setText(deep_runouts_direct[current_innings] + "");
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.stumpings
					.setText(stumpings[current_innings] + "");
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.byes
					.setText(byes[current_innings] + "");
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.misfields
					.setText(misfields[current_innings] + "");
			PerformanceFieldingFragmentEdit.performanceFieldingFragmentEdit.catches_dropped
					.setText(field_catches_dropped[current_innings] + "");
			break;
		default:
			break;
		}
		Log.d("Debug", "On View info Finished");
	}

	public void insertOrUpdate() {
		saveInfo(GENERAL);
		saveInfo(BATTING);
		saveInfo(BOWLING);
		saveInfo(FIELDING);
		Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/"
				+ match_id + "/" + device_id);
		ContentValues matchvalues = new ContentValues();
		matchvalues.put(MatchDb.KEY_RESULT, result);
		matchvalues.put(MatchDb.KEY_REVIEW, review);
		matchvalues.put(MatchDb.KEY_DURATION, duration);
		matchvalues.put(MatchDb.KEY_FIRST_ACTION, first);

		// update a record
		getSherlockActivity().getContentResolver().update(uri, matchvalues,
				null, null);
		uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/"
				+ match_id + "/" + device_id);
		Cursor c = getSherlockActivity().getContentResolver().query(uri,
				new String[] { PerformanceDb.KEY_ROWID }, null, null, null);
		ContentValues[] values = new ContentValues[2];
		for (int i = 1; i <= innings; i++) {
			values[i - 1] = new ContentValues();

			values[i - 1].put(PerformanceDb.KEY_MATCHID, match_id);
			values[i - 1].put(PerformanceDb.KEY_DEVICE_ID, device_id);
			values[i - 1].put(PerformanceDb.KEY_INNING, i);

			values[i - 1].put(PerformanceDb.KEY_BAT_NUM, batting_no[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_RUNS, bat_runs[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_BALLS, bat_balls[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_TIME, time_spent[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_FOURS, bat_fours[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_SIXES, bat_sixes[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_HOW_OUT, how_out[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_BOWLER_TYPE,
					bowler_type[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_FIELDING_POSITION,
					fielding_pos[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BAT_CHANCES, lives[i - 1]);

			overs[i - 1] = round(overs[i - 1], 1);
			int full_overs = (int) overs[i - 1];
			float balls = (overs[i - 1] - full_overs) * 10;
			values[i - 1].put(PerformanceDb.KEY_BOWL_BALLS, full_overs * 6
					+ (int) round(balls, 1));
			Log.d("Debug", "Overs " + overs[i - 1] + " " + full_overs + " "
					+ balls);
			values[i - 1].put(PerformanceDb.KEY_BOWL_SPELLS, spells[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_MAIDENS, maidens[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_RUNS, bowl_runs[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_FOURS, bowl_fours[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_SIXES, bowl_sixes[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_WKTS_LEFT,
					wkts_left[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_WKTS_RIGHT,
					wkts_right[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_CATCHES_DROPPED,
					bowl_catches_dropped[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_NOBALLS, noballs[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_BOWL_WIDES, wides[i - 1]);

			values[i - 1].put(PerformanceDb.KEY_FIELD_SLIP_CATCH,
					slip_catches[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_CLOSE_CATCH,
					close_catches[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
					circle_catches[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_DEEP_CATCH,
					deep_catches[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_RO_CIRCLE,
					circle_runouts[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE,
					circle_runouts_direct[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_RO_DEEP,
					deep_runouts[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP,
					deep_runouts_direct[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_STUMPINGS,
					stumpings[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_BYES, byes[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_MISFIELDS,
					misfields[i - 1]);
			values[i - 1].put(PerformanceDb.KEY_FIELD_CATCHES_DROPPED,
					field_catches_dropped[i - 1]);
		}

		if (c.getCount() == 0) {
			c.close();
			// not found in database
			for (int i = 1; i <= innings; i++) {
				values[i - 1].put(PerformanceDb.KEY_STATUS,
						MatchDb.MATCH_CURRENT);
				// insert a record
				getSherlockActivity().getContentResolver().insert(
						CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE,
						values[i - 1]);
			}
			Toast.makeText(getSherlockActivity(), "Performance Saved",
					Toast.LENGTH_LONG).show();
		} else {
			c.close();
			for (int i = 1; i <= innings; i++) {

				// update a record
				getSherlockActivity().getContentResolver().update(uri,
						values[i - 1], null, null);
			}
			Toast.makeText(getSherlockActivity(), "Performance Saved",
					Toast.LENGTH_LONG).show();
		}

		// Go to Ongoing Matches
		((MainActivity) getSherlockActivity()).currentFragment = MainActivity.ONGOING_MATCHES_FRAGMENT;
		((MainActivity) getSherlockActivity()).preFragment = MainActivity.CAREER_FRAGMENT;
		((MainActivity) getSherlockActivity()).selectItem(
				MainActivity.ONGOING_MATCHES_FRAGMENT, true);

	}

	public static float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

}
