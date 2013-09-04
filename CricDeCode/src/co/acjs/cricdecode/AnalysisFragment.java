package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class AnalysisFragment extends SherlockFragment {

	static AnalysisFragment analysisFragment;

	public static final int XY_PLOT = 0, PIE_CHART = 1;
	public static int type_sel = 0;

	int param_pos, param_save_count;
	String XAxis = "", YAxis = "";

	// XY GRAPH CONSTANTS
	// PARAM1
	// GENERAL
	public static final int MATCHES = 0;
	// BATTING
	public static final int BAT_INNINGS = 0, NO = 1, BAT_RUNS = 2, HIGHEST = 3,
			BAT_AVG = 4, BAT_BALL = 5, BAT_STR = 6, BAT_50 = 7, BAT_100 = 8,
			TIME_SPENT = 9, BAT_FOURS = 10, BAT_SIXES = 11, LIVES = 12;
	// BOWLING
	public static final int BOWL_INNINGS = 0, BOWL_OVERS = 1, SPELLS = 2,
			BOWL_RUNS = 3, MAIDENS = 4, WKTS_LEFT = 5, WKTS_RIGHT = 6,
			WKTS = 7, BOWL_DROPPED = 8, ECO = 9, BOWL_STR = 10, BOWL_AVG = 11,
			BOWL_FOURS = 12, BOWL_SIXES = 13, NOBALLS = 14, WIDES = 15;
	// FIELDING
	public static final int SLIP = 0, CLOSE = 1, CIRCLE = 2, DEEP = 3,
			TOTCATCHES = 4, CIR_RO_DI = 5, CIR_RO = 6, DEEP_RO_DI = 7,
			DEEP_RO = 8, TOT_RO = 9, STUMP = 10, BYES = 11, MISFIELD = 12,
			CATCH_DROP = 13;
	// PARAM2
	public static final int SEASONS = 0, MY_TEAM = 1, OPPONENTS = 2,
			VENUES = 3, RESULTS = 4, LEVELS = 5, OVERS = 6, INNINGS = 7,
			DURATION = 8, FIRST = 9, BATTING_NO = 10, HOW_OUT_P2 = 11;

	// PIE CHART CONSTANTS
	// GENERAL
	public static final int MATCH_RESULT = 0;
	// BATTING
	public static final int HOW_OUT = 0, WHERE_OUT_CAUGHT = 1;
	// BOWLING
	public static final int WICKETS = 0;
	// FIELDING
	public static final int CATCHES = 0, RUNOUTS = 1;

	// Layout Fields
	Spinner graph_facet, graph_type, graph_param1, graph_param2,
			graph_param_pie;
	LinearLayout line_params;

	// Filter Variables
	ArrayList<String> my_team_list, my_team_list_selected, opponent_list,
			opponent_list_selected, venue_list, venue_list_selected,
			overs_list, overs_list_selected, innings_list,
			innings_list_selected, level_list, level_list_selected,
			duration_list, duration_list_selected, first_list,
			first_list_selected, season_list, season_list_selected,
			result_list, result_list_selected, batting_no_list,
			batting_no_list_selected, how_out_list, how_out_list_selected;
	String myteam_whereClause = "", opponent_whereClause = "",
			venue_whereClause = "", overs_whereClause = "",
			innings_whereClause = "", level_whereClause = "",
			duration_whereClause = "", first_whereClause = "",
			season_whereClause = "", result_whereClause = "",
			batting_no_whereClause = "", how_out_whereClause = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		analysisFragment = this;
		View rootView = inflater.inflate(R.layout.analysis_fragment, container,
				false);
		init(rootView, savedInstanceState);
		return rootView;
	}

	public void init(View view, Bundle bundle) {

		if (bundle != null) {

			param_pos = bundle.getInt("param_pos");
			if (param_pos == 0) {
				param_save_count = 1;
			} else {
				param_save_count = 2;
			}
		} else {
			param_save_count = 0;
		}

		XAxis = "";
		YAxis = "";
		graph_facet = (Spinner) view.findViewById(R.id.graph_facet);
		graph_type = (Spinner) view.findViewById(R.id.graph_type);
		graph_param1 = (Spinner) view.findViewById(R.id.graph_param1);
		graph_param2 = (Spinner) view.findViewById(R.id.graph_param2);
		graph_param_pie = (Spinner) view.findViewById(R.id.graph_param_pie);
		line_params = (LinearLayout) view.findViewById(R.id.line_graph_params);

		String[] arr = getResources().getStringArray(R.array.graph_param2);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getSherlockActivity(), android.R.layout.simple_spinner_item,
				arr);
		adapter.setDropDownViewResource(R.layout.drop_down_menu_item);
		graph_param2.setAdapter(adapter);

		graph_facet.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				String[] arr = null;
				String[] arr2 = null;
				switch (position) {
				case PerformanceFragmentEdit.GENERAL:
					arr = getResources().getStringArray(
							R.array.graph_param1_general);
					arr2 = getResources().getStringArray(
							R.array.graph_param_pie_general);
					break;
				case PerformanceFragmentEdit.BATTING:
					arr = getResources().getStringArray(
							R.array.graph_param1_batting);
					arr2 = getResources().getStringArray(
							R.array.graph_param_pie_batting);
					break;
				case PerformanceFragmentEdit.BOWLING:
					arr = getResources().getStringArray(
							R.array.graph_param1_bowling);
					arr2 = getResources().getStringArray(
							R.array.graph_param_pie_bowling);
					break;
				case PerformanceFragmentEdit.FIELDING:
					arr = getResources().getStringArray(
							R.array.graph_param1_fielding);
					arr2 = getResources().getStringArray(
							R.array.graph_param_pie_fielding);
					break;
				default:
					break;
				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getSherlockActivity(),
						android.R.layout.simple_spinner_item, arr);
				adapter.setDropDownViewResource(R.layout.drop_down_menu_item);
				graph_param1.setAdapter(adapter);
				adapter = new ArrayAdapter<String>(getSherlockActivity(),
						android.R.layout.simple_spinner_item, arr2);
				adapter.setDropDownViewResource(R.layout.drop_down_menu_item);

				graph_param_pie.setAdapter(adapter);

				if (param_save_count != 0) {
					param_save_count--;
					if (param_save_count == 0) {
						if (graph_type.getSelectedItemPosition() == XY_PLOT) {
							graph_param1.setSelection(param_pos);
						} else {
							graph_param_pie.setSelection(param_pos);
						}
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		graph_type.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case XY_PLOT:
					line_params.setVisibility(View.VISIBLE);
					graph_param_pie.setVisibility(View.INVISIBLE);
					AnalysisFragment.type_sel = 0;
					break;
				case PIE_CHART:
					line_params.setVisibility(View.INVISIBLE);
					graph_param_pie.setVisibility(View.VISIBLE);
					AnalysisFragment.type_sel = 1;
					break;
				default:
					break;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		XAxis = "";
		YAxis = "";
		Log.w("Analysis Frag", "On Resume Called");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState == null) {

			batting_no_list = new ArrayList<String>();
			batting_no_list_selected = new ArrayList<String>();

			how_out_list = new ArrayList<String>();
			how_out_list_selected = new ArrayList<String>();

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

			batting_no_list = savedInstanceState
					.getStringArrayList("batting_no_list");
			batting_no_list_selected = savedInstanceState
					.getStringArrayList("batting_no_list_selected");

			how_out_list = savedInstanceState
					.getStringArrayList("how_out_list");
			how_out_list_selected = savedInstanceState
					.getStringArrayList("how_out_list_selected");

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

			batting_no_whereClause = savedInstanceState
					.getString("batting_no_whereClause");
			how_out_whereClause = savedInstanceState
					.getString("how_out_whereClause");
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

			if (((MainActivity) getSherlockActivity()).filter_showing) {
				((MainActivity) getSherlockActivity())
						.showFilterDialog(MainActivity.ANALYSIS_FRAGMENT);
			}

		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		if (graph_type.getSelectedItemPosition() == XY_PLOT) {
			outState.putInt("param_pos", graph_param1.getSelectedItemPosition());
		} else {
			outState.putInt("param_pos",
					graph_param_pie.getSelectedItemPosition());
		}

		outState.putInt("param1_sel", graph_param1.getSelectedItemPosition());
		outState.putInt("param2_sel", graph_param2.getSelectedItemPosition());
		outState.putInt("param_pie_sel",
				graph_param_pie.getSelectedItemPosition());

		outState.putStringArrayList("batting_no_list",
				(ArrayList<String>) batting_no_list);
		outState.putStringArrayList("how_out_list",
				(ArrayList<String>) how_out_list);
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
		outState.putStringArrayList("batting_no_list_selected",
				(ArrayList<String>) batting_no_list_selected);
		outState.putStringArrayList("how_out_list_selected",
				(ArrayList<String>) how_out_list_selected);
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
		outState.putString("batting_no_whereClause", batting_no_whereClause);
		outState.putString("how_out_whereClause", how_out_whereClause);
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

		c = MainActivity.dbHandle.rawQuery("select distinct "
				+ PerformanceDb.KEY_BAT_NUM + " as _id from "
				+ PerformanceDb.SQLITE_TABLE + " where "
				+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
				+ "' and (" + PerformanceDb.KEY_BAT_HOW_OUT + "!='Not Out' or "
				+ PerformanceDb.KEY_BAT_BALLS + "!=0)", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				batting_no_list.add(c.getString(0));
				batting_no_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();

		c = MainActivity.dbHandle.rawQuery(
				"select distinct " + PerformanceDb.KEY_BAT_HOW_OUT
						+ " as _id from " + PerformanceDb.SQLITE_TABLE
						+ " where " + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "'", null);
		count = c.getCount();
		if (count != 0) {
			c.moveToFirst();
			do {
				how_out_list.add(c.getString(0));
				how_out_list_selected.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();
	}

	public void generateXYGraph() {
		Log.d("Debug", "generateXYGraph() called");
		Cursor cursor = null;
		String column2 = null;
		String[] label = null;
		int[] values = null;
		switch (graph_param2.getSelectedItemPosition()) {
		case BATTING_NO:
			column2 = "p." + PerformanceDb.KEY_BAT_NUM;
			if (graph_facet.getSelectedItemPosition() != PerformanceFragmentEdit.BATTING) {
				Toast.makeText(
						getSherlockActivity(),
						"Batting Number Cannot be Applied to "
								+ graph_facet.getSelectedItem(),
						Toast.LENGTH_LONG).show();
				XAxis = "";
				YAxis = "";
				return;
			}
			if ("".equals(XAxis)) {
				XAxis = "Batting No.";
			}
			break;
		case HOW_OUT_P2:
			column2 = "p." + PerformanceDb.KEY_BAT_HOW_OUT;
			if (graph_facet.getSelectedItemPosition() != PerformanceFragmentEdit.BATTING) {
				Toast.makeText(
						getSherlockActivity(),
						"How Out Cannot be Applied to "
								+ graph_facet.getSelectedItem(),
						Toast.LENGTH_LONG).show();
				XAxis = "";
				YAxis = "";
				return;
			}
			if ("".equals(XAxis)) {
				XAxis = "Dismissal";
			}
			break;
		case SEASONS:
			column2 = "strftime('%Y',m." + MatchDb.KEY_MATCH_DATE + ")";
			if ("".equals(XAxis)) {
				XAxis = "Seasons";
			}
			break;
		case MY_TEAM:
			column2 = "m." + MatchDb.KEY_MY_TEAM;
			if ("".equals(XAxis)) {
				XAxis = "My Team";
			}
			break;
		case OPPONENTS:
			column2 = "m." + MatchDb.KEY_OPPONENT_TEAM;
			if ("".equals(XAxis)) {
				XAxis = "Opponents";
			}
			break;
		case VENUES:
			column2 = "m." + MatchDb.KEY_VENUE;
			if ("".equals(XAxis)) {
				XAxis = "Venue";
			}
			break;
		case RESULTS:
			column2 = "m." + MatchDb.KEY_RESULT;
			if ("".equals(XAxis)) {
				XAxis = "Results";
			}
			break;
		case LEVELS:
			column2 = "m." + MatchDb.KEY_LEVEL;
			if ("".equals(XAxis)) {
				XAxis = "Levels";
			}
			break;
		case OVERS:
			column2 = "m." + MatchDb.KEY_OVERS;
			if ("".equals(XAxis)) {
				XAxis = "Overs";
			}
			break;
		case INNINGS:
			column2 = "m." + MatchDb.KEY_INNINGS;
			if ("".equals(XAxis)) {
				XAxis = "Innings";
			}
			break;
		case DURATION:
			column2 = "m." + MatchDb.KEY_DURATION;
			if ("".equals(XAxis)) {
				XAxis = "Duration";
			}
			break;
		case FIRST:
			column2 = "m." + MatchDb.KEY_FIRST_ACTION;
			if ("".equals(XAxis)) {
				XAxis = "First";
			}
			break;
		default:
			break;
		}

		switch (graph_facet.getSelectedItemPosition()) {
		case PerformanceFragmentEdit.GENERAL:
			Log.d("Debug", "generateXYGraph() GENERAL");
			switch (graph_param1.getSelectedItemPosition()) {
			case MATCHES:
				cursor = MainActivity.dbHandle.rawQuery("select count(m."
						+ MatchDb.KEY_ROWID + ")," + column2 + " from "
						+ MatchDb.SQLITE_TABLE + " m where m."
						+ MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
						+ "'" + myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause
						+ " group by " + column2, null);
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					label = new String[cursor.getCount()];
					values = new int[cursor.getCount()];
					int i = 0;
					do {
						label[i] = cursor.getString(1);
						values[i] = cursor.getInt(0);
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
				}
				cursor.close();

				if ("".equals(YAxis)) {
					YAxis = "Matches";
				}

				break;
			default:
				break;
			}
			break;
		case PerformanceFragmentEdit.BATTING:
			Log.d("Debug", "generateXYGraph() BATTING");
			String column1 = null;
			switch (graph_param1.getSelectedItemPosition()) {
			case BAT_INNINGS:
				if ("".equals(YAxis)) {
					YAxis = "Innings";
				}
			case NO:
				if ("".equals(YAxis)) {
					YAxis = "Not Out";
				}
			case BAT_AVG:
				column1 = "count(p." + PerformanceDb.KEY_ROWID + ")";
				if ("".equals(YAxis)) {
					YAxis = "Avg";
				}
				break;
			case BAT_RUNS:
				column1 = "sum(p." + PerformanceDb.KEY_BAT_RUNS + ")";
				if ("".equals(YAxis)) {
					YAxis = "Runs";
				}
				break;
			case HIGHEST:
				column1 = "max(p." + PerformanceDb.KEY_BAT_RUNS + ")";
				if ("".equals(YAxis)) {
					YAxis = "HS";
				}
				break;
			case BAT_BALL:
				column1 = "sum(p." + PerformanceDb.KEY_BAT_BALLS + ")";
				if ("".equals(YAxis)) {
					YAxis = "Balls";
				}
				break;
			case TIME_SPENT:
				column1 = "sum(p." + PerformanceDb.KEY_BAT_TIME + ")";
				if ("".equals(YAxis)) {
					YAxis = "Time(min)";
				}
				break;
			case BAT_FOURS:
				column1 = "sum(p." + PerformanceDb.KEY_BAT_FOURS + ")";
				if ("".equals(YAxis)) {
					YAxis = "4s";
				}
				break;
			case BAT_SIXES:
				column1 = "sum(p." + PerformanceDb.KEY_BAT_SIXES + ")";
				if ("".equals(YAxis)) {
					YAxis = "6s";
				}
				break;
			case LIVES:
				column1 = "sum(p." + PerformanceDb.KEY_BAT_CHANCES + ")";
				if ("".equals(YAxis)) {
					YAxis = "Lives";
				}
				break;
			default:
				break;
			}
			if (column1 != null) {
				Log.d("Debug", "generateXYGraph() BATTING PART1 Query");
				cursor = MainActivity.dbHandle.rawQuery("select " + column2
						+ "," + column1 + " from " + PerformanceDb.SQLITE_TABLE
						+ " p inner join " + MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "' and (p."
						+ PerformanceDb.KEY_BAT_HOW_OUT + "!='Not Out' or p."
						+ PerformanceDb.KEY_BAT_BALLS + "!=0)"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause
						+ batting_no_whereClause + how_out_whereClause
						+ " group by " + column2, null);
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					label = new String[cursor.getCount()];
					values = new int[cursor.getCount()];
					int i = 0;
					do {
						label[i] = cursor.getString(0);
						values[i] = cursor.getInt(1);
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
				}
				cursor.close();
				Log.d("Debug", "Label " + Arrays.toString(label));
				Log.d("Debug", graph_param1.getSelectedItemPosition() + " "
						+ BAT_STR);
				if (graph_param1.getSelectedItemPosition() != NO
						&& graph_param1.getSelectedItemPosition() != BAT_AVG) {
					Log.d("Debug", "generateXYGraph() BATTING BREAK");
					break;
				}
			}
			Log.d("Debug", "generateXYGraph() BATTING PART2");
			switch (graph_param1.getSelectedItemPosition()) {
			case NO:
				if ("".equals(YAxis)) {
					YAxis = "Not Out";
				}
			case BAT_AVG:
				String[] bat_labels = label;
				int[] bat_innings = values;
				int[] outs = null;
				String[] out_label = null;
				cursor = MainActivity.dbHandle.rawQuery("select " + column2
						+ ",count(p." + PerformanceDb.KEY_ROWID + ") from "
						+ PerformanceDb.SQLITE_TABLE + " p inner join "
						+ MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "' and p."
						+ PerformanceDb.KEY_BAT_HOW_OUT + "!='Not Out'"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause
						+ batting_no_whereClause + how_out_whereClause
						+ " group by " + column2, null);
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					label = new String[cursor.getCount()];
					values = new int[cursor.getCount()];
					outs = new int[cursor.getCount()];
					out_label = new String[cursor.getCount()];
					Log.d("Debug",
							cursor.getCount() + " OUTS "
									+ Arrays.toString(outs));
					int i = 0;
					do {
						label[i] = cursor.getString(0);
						outs[i] = cursor.getInt(1);
						int j = 0;
						for (; j < bat_labels.length; j++) {
							if (bat_labels[j].equals(label[i])) {
								break;
							}
						}
						Log.d("Debug", "Matched Labels " + bat_labels[j]);
						bat_innings[j] = bat_innings[j] - cursor.getInt(1);
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
					values = bat_innings;
					out_label = label;
					label = bat_labels;
				}
				cursor.close();
				Log.d("Debug", "Label " + Arrays.toString(label));
				if (graph_param1.getSelectedItemPosition() != BAT_AVG) {
					break;
				}
				if (outs == null) {
					label = null;
					values = null;
					break;
				}
				Log.d("Debug", "AVERAGE LOOP " + Arrays.toString(outs));
				cursor = MainActivity.dbHandle.rawQuery("select " + column2
						+ ",sum(p." + PerformanceDb.KEY_BAT_RUNS + ") from "
						+ PerformanceDb.SQLITE_TABLE + " p inner join "
						+ MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "' and (p."
						+ PerformanceDb.KEY_BAT_HOW_OUT + "!='Not Out' or p."
						+ PerformanceDb.KEY_BAT_BALLS + "!=0)"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause
						+ batting_no_whereClause + how_out_whereClause
						+ " group by " + column2, null);
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					String[] bat_label = new String[cursor.getCount()];
					values = new int[cursor.getCount()];
					int i = 0;
					do {
						bat_label[i] = cursor.getString(0);
						values[i] = cursor.getInt(1);
						// if (outs[i] == 0) {
						// values[i] = 0;
						// } else {
						// values[i] = values[i] / outs[i];
						// }
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
					for (int j = 0; j < bat_label.length; j++) {
						int k = 0;
						for (; k < out_label.length; k++) {
							if (bat_label[j].equals(out_label[k])) {
								if (outs[k] == 0) {
									values[j] = 0;
								} else {
									values[j] = (int) Math
											.round((double) values[j] / outs[k]);
								}
								break;
							}
						}
						if (k == out_label.length) {
							values[j] = -1;
						}
					}
					label = bat_label;
				}
				cursor.close();
				if ("".equals(YAxis)) {
					YAxis = "Avg";
				}
				break;
			case BAT_STR:
				Log.d("Debug", "generateXYGraph() BATTING STR");
				if (cursor != null) {
					Log.d("Debug", "Leaky Cursor");
					cursor.close();
				}
				cursor = MainActivity.dbHandle.rawQuery("select " + column2
						+ ",sum(p." + PerformanceDb.KEY_BAT_RUNS + "),sum(p."
						+ PerformanceDb.KEY_BAT_BALLS + ") from "
						+ PerformanceDb.SQLITE_TABLE + " p inner join "
						+ MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "' and (p."
						+ PerformanceDb.KEY_BAT_HOW_OUT + "!='Not Out' or p."
						+ PerformanceDb.KEY_BAT_BALLS + "!=0)"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause
						+ batting_no_whereClause + how_out_whereClause
						+ " group by " + column2, null);
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					label = new String[cursor.getCount()];
					values = new int[cursor.getCount()];
					int i = 0;
					do {
						label[i] = cursor.getString(0);
						if (cursor.getInt(2) == 0) {
							values[i] = -1;
						} else {
							float temp = (float) cursor.getInt(1)
									/ cursor.getInt(2) * 100;
							values[i] = (int) Math.round(temp);
						}
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
				}
				cursor.close();
				if ("".equals(YAxis)) {
					YAxis = "SR";
				}
				break;
			case BAT_50:
				if ("".equals(YAxis)) {
					YAxis = "50s";
				}
			case BAT_100:
				if (cursor != null) {
					Log.d("Debug", "Leaky Cursor");
					cursor.close();
				}
				cursor = MainActivity.dbHandle.rawQuery("select " + column2
						+ ",count(p." + PerformanceDb.KEY_ROWID + ") from "
						+ PerformanceDb.SQLITE_TABLE + " p inner join "
						+ MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "' and p."
						+ PerformanceDb.KEY_BAT_RUNS + ">=100"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause
						+ batting_no_whereClause + how_out_whereClause
						+ " group by " + column2, null);
				Log.d("Debug", "100 cursor Count " + cursor.getCount());
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					label = new String[cursor.getCount()];
					values = new int[cursor.getCount()];
					int i = 0;
					do {
						label[i] = cursor.getString(0);
						values[i] = cursor.getInt(1);
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
				}
				cursor.close();
				if (graph_param1.getSelectedItemPosition() != BAT_50) {
					break;
				}
				cursor = MainActivity.dbHandle.rawQuery("select " + column2
						+ ",count(p." + PerformanceDb.KEY_ROWID + ") from "
						+ PerformanceDb.SQLITE_TABLE + " p inner join "
						+ MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "' and p."
						+ PerformanceDb.KEY_BAT_RUNS + ">=50"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause
						+ batting_no_whereClause + how_out_whereClause
						+ " group by " + column2, null);
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					label = new String[cursor.getCount()];
					int[] bat_100s;
					if (values == null) {
						bat_100s = new int[cursor.getCount()];
					} else {
						bat_100s = values;
					}
					values = new int[cursor.getCount()];
					int i = 0;
					do {
						label[i] = cursor.getString(0);
						values[i] = cursor.getInt(1) - bat_100s[i];
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
				}
				cursor.close();
				break;
			default:
				break;
			}
			if ("".equals(YAxis)) {
				YAxis = "100s";
			}
			break;
		case PerformanceFragmentEdit.BOWLING:
			column1 = null;
			switch (graph_param1.getSelectedItemPosition()) {
			case BOWL_INNINGS:
				column1 = "count(p." + PerformanceDb.KEY_ROWID + ")";
				if ("".equals(YAxis)) {
					YAxis = "Innings";
				}
				break;
			case BOWL_OVERS:
				column1 = "sum(p." + PerformanceDb.KEY_BOWL_BALLS + ")";
				if ("".equals(YAxis)) {
					YAxis = "Overs";
				}
				break;
			case BOWL_RUNS:
				column1 = "sum(p." + PerformanceDb.KEY_BOWL_RUNS + ")";
				if ("".equals(YAxis)) {
					YAxis = "Runs Given";
				}
				break;
			case WKTS_LEFT:
				column1 = "sum(p." + PerformanceDb.KEY_BOWL_WKTS_LEFT + ")";
				if ("".equals(YAxis)) {
					YAxis = "Left Bat Wkt";
				}
				break;
			case WKTS_RIGHT:
				column1 = "sum(p." + PerformanceDb.KEY_BOWL_WKTS_RIGHT + ")";
				if ("".equals(YAxis)) {
					YAxis = "Right Bat Wkt";
				}
				break;
			case BOWL_STR:
				if ("".equals(YAxis)) {
					YAxis = "SR";
				}
			case BOWL_AVG:
				if ("".equals(YAxis)) {
					YAxis = "Avg";
				}
			case WKTS:
				column1 = "sum(p." + PerformanceDb.KEY_BOWL_WKTS_LEFT + "+p."
						+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + ")";
				if ("".equals(YAxis)) {
					YAxis = "Wkts";
				}
				break;
			case BOWL_DROPPED:
				column1 = "sum(p." + PerformanceDb.KEY_BOWL_CATCHES_DROPPED
						+ ")";
				if ("".equals(YAxis)) {
					YAxis = "Dropped";
				}
				break;
			case SPELLS:
				column1 = "sum(p." + PerformanceDb.KEY_BOWL_SPELLS + ")";
				if ("".equals(YAxis)) {
					YAxis = "Spells";
				}
				break;
			case MAIDENS:
				column1 = "sum(p." + PerformanceDb.KEY_BOWL_MAIDENS + ")";
				if ("".equals(YAxis)) {
					YAxis = "Maidens";
				}
				break;
			case BOWL_FOURS:
				column1 = "sum(p." + PerformanceDb.KEY_BOWL_FOURS + ")";
				if ("".equals(YAxis)) {
					YAxis = "4s given";
				}
				break;
			case BOWL_SIXES:
				column1 = "sum(p." + PerformanceDb.KEY_BOWL_SIXES + ")";
				if ("".equals(YAxis)) {
					YAxis = "6s Given";
				}
				break;
			case NOBALLS:
				column1 = "sum(p." + PerformanceDb.KEY_BOWL_NOBALLS + ")";
				if ("".equals(YAxis)) {
					YAxis = "No Balls";
				}
				break;
			case WIDES:
				column1 = "sum(p." + PerformanceDb.KEY_BOWL_WIDES + ")";
				if ("".equals(YAxis)) {
					YAxis = "Wides";
				}
				break;
			default:
				break;
			}
			if (column1 != null) {
				cursor = MainActivity.dbHandle.rawQuery("select " + column2
						+ "," + column1 + " from " + PerformanceDb.SQLITE_TABLE
						+ " p inner join " + MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "' and p."
						+ PerformanceDb.KEY_BOWL_BALLS + "!=0"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause
						+ " group by " + column2, null);
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					label = new String[cursor.getCount()];
					values = new int[cursor.getCount()];
					int i = 0;
					do {
						label[i] = cursor.getString(0);
						if (graph_param1.getSelectedItemPosition() == BOWL_OVERS) {
							values[i] = (int) Math.round((double) cursor
									.getInt(1) / 6);
						} else {
							values[i] = cursor.getInt(1);
						}
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
				}
				cursor.close();
				if (graph_param1.getSelectedItemPosition() != BOWL_STR
						&& graph_param1.getSelectedItemPosition() != BOWL_AVG) {
					break;
				}
			}
			switch (graph_param1.getSelectedItemPosition()) {
			case BOWL_STR:
				if ("".equals(YAxis)) {
					YAxis = "SR";
				}
			case BOWL_AVG:
				if ("".equals(YAxis)) {
					YAxis = "Avg";
				}
			case ECO:
				if ("".equals(YAxis)) {
					YAxis = "Economy";
				}
				cursor = MainActivity.dbHandle.rawQuery("select " + column2
						+ ",sum(p." + PerformanceDb.KEY_BOWL_RUNS + "),sum(p."
						+ PerformanceDb.KEY_BOWL_BALLS + ")" + " from "
						+ PerformanceDb.SQLITE_TABLE + " p inner join "
						+ MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "' and p."
						+ PerformanceDb.KEY_BOWL_BALLS + "!=0"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause
						+ " group by " + column2, null);
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					label = new String[cursor.getCount()];
					if (graph_param1.getSelectedItemPosition() == ECO
							|| values == null) {
						values = new int[cursor.getCount()];
					}

					int i = 0;
					do {
						label[i] = cursor.getString(0);
						if (graph_param1.getSelectedItemPosition() == ECO) {
							if (cursor.getInt(2) == 0) {
								values[i] = -1;
							} else {
								values[i] = (int) Math.round((double) cursor
										.getInt(1) * 6 / cursor.getInt(2));
							}
						} else if (graph_param1.getSelectedItemPosition() == BOWL_STR) {
							if (values[i] != 0) {
								values[i] = (int) Math.round((double) cursor
										.getInt(2) / values[i]);
							} else {
								values[i] = -1;
							}
						} else {
							if (values[i] != 0) {
								values[i] = (int) Math.round((double) cursor
										.getInt(1) / values[i]);
							} else {
								values[i] = -1;
							}
						}
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
				}
				cursor.close();
				break;
			default:
				break;
			}
			break;
		case PerformanceFragmentEdit.FIELDING:
			column1 = null;
			switch (graph_param1.getSelectedItemPosition()) {
			case SLIP:
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_SLIP_CATCH + ")";
				if ("".equals(YAxis)) {
					YAxis = "Slip Catches";
				}
				break;
			case CLOSE:
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_CLOSE_CATCH + ")";
				if ("".equals(YAxis)) {
					YAxis = "Close Catches";
				}
				break;
			case CIRCLE:
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_CIRCLE_CATCH + ")";
				if ("".equals(YAxis)) {
					YAxis = "Circle Catches";
				}
				break;
			case DEEP:
				if ("".equals(YAxis)) {
					YAxis = "Deep Catches";
				}
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_DEEP_CATCH + ")";
				break;
			case TOTCATCHES:
				if ("".equals(YAxis)) {
					YAxis = "Total Catches";
				}
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_SLIP_CATCH + "+p."
						+ PerformanceDb.KEY_FIELD_CLOSE_CATCH + "+p."
						+ PerformanceDb.KEY_FIELD_CIRCLE_CATCH + "+p."
						+ PerformanceDb.KEY_FIELD_DEEP_CATCH + ")";
				break;
			case CIR_RO_DI:
				if ("".equals(YAxis)) {
					YAxis = "Direct Runout in Circle";
				}

				column1 = "sum(p." + PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE
						+ ")";
				break;
			case CIR_RO:
				if ("".equals(YAxis)) {
					YAxis = "Runout in Circle";
				}
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_RO_CIRCLE + ")";
				break;
			case DEEP_RO_DI:
				if ("".equals(YAxis)) {
					YAxis = "Direct Runout in Deep";
				}
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP
						+ ")";
				break;
			case DEEP_RO:
				if ("".equals(YAxis)) {
					YAxis = "Runout in Deep";
				}
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_RO_DEEP + ")";
				break;
			case TOT_RO:
				if ("".equals(YAxis)) {
					YAxis = "Total Runout";
				}
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE
						+ "+p." + PerformanceDb.KEY_FIELD_RO_CIRCLE + "+p."
						+ PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP + "+p."
						+ PerformanceDb.KEY_FIELD_RO_DEEP + ")";
				break;
			case STUMP:
				if ("".equals(YAxis)) {
					YAxis = "Stumpings";
				}
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_STUMPINGS + ")";
				break;
			case BYES:
				if ("".equals(YAxis)) {
					YAxis = "Byes";
				}
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_BYES + ")";
				break;
			case MISFIELD:
				if ("".equals(YAxis)) {
					YAxis = "Misfields";
				}
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_MISFIELDS + ")";
				break;
			case CATCH_DROP:
				if ("".equals(YAxis)) {
					YAxis = "Catches Drop";
				}
				column1 = "sum(p." + PerformanceDb.KEY_FIELD_CATCHES_DROPPED
						+ ")";
				break;
			default:
				break;
			}
			cursor = MainActivity.dbHandle.rawQuery("select " + column2 + ","
					+ column1 + " from " + PerformanceDb.SQLITE_TABLE
					+ " p inner join " + MatchDb.SQLITE_TABLE + " m on p."
					+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
					+ " where p." + PerformanceDb.KEY_STATUS + "='"
					+ MatchDb.MATCH_HISTORY + "'" + myteam_whereClause
					+ opponent_whereClause + venue_whereClause
					+ overs_whereClause + innings_whereClause
					+ level_whereClause + duration_whereClause
					+ first_whereClause + season_whereClause
					+ result_whereClause + " group by " + column2, null);
			if (column1 != null) {
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					label = new String[cursor.getCount()];
					values = new int[cursor.getCount()];
					int i = 0;
					do {
						label[i] = cursor.getString(0);
						values[i] = cursor.getInt(1);
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
				}
				cursor.close();
			}
			break;
		default:
			break;
		}
		if (label != null) {
			for (int index = 0; index < label.length; index++) {
				if (label[index].equals("-1")) {
					label[index] = "Unlimited";
				}
			}
			Log.w("Label vs Values",
					"" + Arrays.toString(label) + " " + Arrays.toString(values));
			Intent intent = new Intent(getActivity(), DisplayLineChart.class);

			int count = 0;
			for (int i = 0; i < values.length; i++) {
				if (values[i] == -1) {
					count++;
				}
			}

			String[] fin_label = new String[label.length - count];
			double d[] = new double[label.length - count];
			int j = 0;
			for (int i = 0; i < label.length; i++) {
				if (values[i] != -1) {
					d[j] = values[i];
					fin_label[j++] = label[i];
				}
			}
			intent.putExtra("X-Axis", XAxis);
			intent.putExtra("Y-Axis", YAxis);
			intent.putExtra("labels", fin_label);
			intent.putExtra("values", d);
			startActivity(intent);
		} else {
			Toast.makeText(getSherlockActivity(), "No Data Available ",
					Toast.LENGTH_LONG).show();
			XAxis = "";
			YAxis = "";
		}
	}

	public void generatePieGraph() {
		Log.d("Debug", "generatePieGraph() called");
		Cursor cursor;
		String[] label = null;
		int[] values = null;
		switch (graph_facet.getSelectedItemPosition()) {
		case PerformanceFragmentEdit.GENERAL:
			switch (graph_param_pie.getSelectedItemPosition()) {
			case MATCH_RESULT:
				cursor = MainActivity.dbHandle.rawQuery("select count("
						+ MatchDb.KEY_ROWID + ")," + MatchDb.KEY_RESULT
						+ " from " + MatchDb.SQLITE_TABLE + " m where "
						+ MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
						+ "'" + myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause
						+ " group by " + MatchDb.KEY_RESULT, null);
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					label = new String[cursor.getCount()];
					values = new int[cursor.getCount()];
					int i = 0;
					do {
						label[i] = cursor.getString(1);
						values[i] = cursor.getInt(0);
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
				}
				YAxis = "Match Results";
				cursor.close();
				break;
			default:
				break;
			}
			break;
		case PerformanceFragmentEdit.BATTING:
			switch (graph_param_pie.getSelectedItemPosition()) {
			case HOW_OUT:
				cursor = MainActivity.dbHandle.rawQuery("select p."
						+ PerformanceDb.KEY_BAT_HOW_OUT + ",count(p."
						+ PerformanceDb.KEY_ROWID + ") from "
						+ PerformanceDb.SQLITE_TABLE + " p inner join "
						+ MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "' and (p."
						+ PerformanceDb.KEY_BAT_HOW_OUT + "!='Not Out' or p."
						+ PerformanceDb.KEY_BAT_BALLS + "!=0)"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause
						+ batting_no_whereClause + how_out_whereClause
						+ " group by p." + PerformanceDb.KEY_BAT_HOW_OUT, null);
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					label = new String[cursor.getCount()];
					values = new int[cursor.getCount()];
					int i = 0;
					do {
						label[i] = cursor.getString(0);
						values[i] = cursor.getInt(1);
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
				}
				cursor.close();
				if ("".equals(YAxis)) {
					YAxis = "Dismissal";
				}
				break;
			case WHERE_OUT_CAUGHT:
				cursor = MainActivity.dbHandle.rawQuery("select p."
						+ PerformanceDb.KEY_BAT_FIELDING_POSITION + ",count(p."
						+ PerformanceDb.KEY_ROWID + ") from "
						+ PerformanceDb.SQLITE_TABLE + " p inner join "
						+ MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "' and p."
						+ PerformanceDb.KEY_BAT_HOW_OUT + "='Caught'"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause
						+ batting_no_whereClause + how_out_whereClause
						+ " group by p."
						+ PerformanceDb.KEY_BAT_FIELDING_POSITION, null);
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					label = new String[cursor.getCount()];
					values = new int[cursor.getCount()];
					int i = 0;
					do {
						label[i] = cursor.getString(0);
						values[i] = cursor.getInt(1);
						i++;
						cursor.moveToNext();
					} while (!cursor.isAfterLast());
				}
				if ("".equals(YAxis)) {
					YAxis = "Caught Out At";
				}
				cursor.close();
				break;
			default:
				break;
			}
			break;
		case PerformanceFragmentEdit.BOWLING:
			switch (graph_param_pie.getSelectedItemPosition()) {
			case WICKETS:
				cursor = MainActivity.dbHandle.rawQuery("select sum(p."
						+ PerformanceDb.KEY_BOWL_WKTS_LEFT + "),sum(p."
						+ PerformanceDb.KEY_BOWL_WKTS_RIGHT + ") from "
						+ PerformanceDb.SQLITE_TABLE + " p inner join "
						+ MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "' and p."
						+ PerformanceDb.KEY_BOWL_BALLS + "!=0"
						+ myteam_whereClause + opponent_whereClause
						+ venue_whereClause + overs_whereClause
						+ innings_whereClause + level_whereClause
						+ duration_whereClause + first_whereClause
						+ season_whereClause + result_whereClause, null);
				cursor.moveToFirst();
				label = new String[] { "Left Handed Batsman",
						"Right Handed Batsman" };
				values = new int[] { cursor.getInt(0), cursor.getInt(1) };
				cursor.close();
				if ("".equals(YAxis)) {
					YAxis = "Wkts";
				}
				break;
			default:
				break;
			}
			break;
		case PerformanceFragmentEdit.FIELDING:
			switch (graph_param_pie.getSelectedItemPosition()) {
			case CATCHES:
				cursor = MainActivity.dbHandle.rawQuery("select sum(p."
						+ PerformanceDb.KEY_FIELD_SLIP_CATCH + "),sum(p."
						+ PerformanceDb.KEY_FIELD_CLOSE_CATCH + "),sum(p."
						+ PerformanceDb.KEY_FIELD_CIRCLE_CATCH + "),sum(p."
						+ PerformanceDb.KEY_FIELD_DEEP_CATCH + ") from "
						+ PerformanceDb.SQLITE_TABLE + " p inner join "
						+ MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "'" + myteam_whereClause
						+ opponent_whereClause + venue_whereClause
						+ overs_whereClause + innings_whereClause
						+ level_whereClause + duration_whereClause
						+ first_whereClause + season_whereClause
						+ result_whereClause, null);
				cursor.moveToFirst();
				label = new String[] { "Slip Catches", "Close Catches",
						"Catches on the Circle", "Catches in the Deep" };
				values = new int[] { cursor.getInt(0), cursor.getInt(1),
						cursor.getInt(2), cursor.getInt(3) };
				if ("".equals(YAxis)) {
					YAxis = "Catches";
				}
				cursor.close();
				break;
			case RUNOUTS:
				cursor = MainActivity.dbHandle.rawQuery("select sum(p."
						+ PerformanceDb.KEY_FIELD_RO_CIRCLE + "),sum(p."
						+ PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE + "),sum(p."
						+ PerformanceDb.KEY_FIELD_RO_DEEP + "),sum(p."
						+ PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP + ") from "
						+ PerformanceDb.SQLITE_TABLE + " p inner join "
						+ MatchDb.SQLITE_TABLE + " m on p."
						+ PerformanceDb.KEY_MATCHID + "=m." + MatchDb.KEY_ROWID
						+ " where p." + PerformanceDb.KEY_STATUS + "='"
						+ MatchDb.MATCH_HISTORY + "'" + myteam_whereClause
						+ opponent_whereClause + venue_whereClause
						+ overs_whereClause + innings_whereClause
						+ level_whereClause + duration_whereClause
						+ first_whereClause + season_whereClause
						+ result_whereClause, null);
				cursor.moveToFirst();
				label = new String[] { "Run Outs in Circle",
						"Direct Hit Run Outs in Circle",
						"Run Outs from the Deep",
						"Direct Hit Run Outs from the Deep" };
				values = new int[] { cursor.getInt(0), cursor.getInt(1),
						cursor.getInt(2), cursor.getInt(3) };
				if ("".equals(YAxis)) {
					YAxis = "Run Outs";
				}
				cursor.close();
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		if (label != null) {
			Log.w("Label vs Values",
					"" + Arrays.toString(label) + " " + Arrays.toString(values));

			Intent intent = new Intent(getActivity(), DisplayPieChart.class);

			double d[] = new double[label.length];
			for (int i = 0; i < label.length; i++) {
				d[i] = values[i];
			}
			intent.putExtra("X-Axis", XAxis);
			intent.putExtra("Y-Axis", YAxis);
			intent.putExtra("labels", label);
			intent.putExtra("values", d);
			startActivity(intent);
		} else {
			Toast.makeText(getSherlockActivity(), "No Data Available ",
					Toast.LENGTH_LONG).show();
			XAxis = "";
			YAxis = "";
		}
	}
}
