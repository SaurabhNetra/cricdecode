package co.acjs.cricdecode;

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

	int param_pos, param1_sel, param2_sel, param_pie_sel, param_save_count;
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

			param1_sel = bundle.getInt("param1_sel");
			param2_sel = bundle.getInt("param2_sel");
			param_pie_sel = bundle.getInt("param_pie_sel");
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

		graph_facet.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				String[] arr = null;
				String[] arr2 = null;
				String[] arr3 = null, arr4 = null;
				switch (position) {
				case PerformanceFragmentEdit.GENERAL:
					arr = getResources().getStringArray(
							R.array.graph_param1_general);
					arr2 = getResources().getStringArray(
							R.array.graph_param_pie_general);
					arr3 = getResources().getStringArray(R.array.graph_param2);
					arr4 = new String[arr3.length - 2];
					System.arraycopy(arr3, 0, arr4, 0, arr3.length - 2);
					break;
				case PerformanceFragmentEdit.BATTING:
					arr = getResources().getStringArray(
							R.array.graph_param1_batting);
					arr2 = getResources().getStringArray(
							R.array.graph_param_pie_batting);
					arr4 = getResources().getStringArray(R.array.graph_param2);
					break;
				case PerformanceFragmentEdit.BOWLING:
					arr = getResources().getStringArray(
							R.array.graph_param1_bowling);
					arr2 = getResources().getStringArray(
							R.array.graph_param_pie_bowling);
					arr3 = getResources().getStringArray(R.array.graph_param2);
					arr4 = new String[arr3.length - 2];
					System.arraycopy(arr3, 0, arr4, 0, arr3.length - 2);
					break;
				case PerformanceFragmentEdit.FIELDING:
					arr = getResources().getStringArray(
							R.array.graph_param1_fielding);
					arr2 = getResources().getStringArray(
							R.array.graph_param_pie_fielding);
					arr3 = getResources().getStringArray(R.array.graph_param2);
					arr4 = new String[arr3.length - 2];
					System.arraycopy(arr3, 0, arr4, 0, arr3.length - 2);
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

				adapter = new ArrayAdapter<String>(getSherlockActivity(),
						android.R.layout.simple_spinner_item, arr4);
				adapter.setDropDownViewResource(R.layout.drop_down_menu_item);

				graph_param2.setAdapter(adapter);

				if (param_save_count != 0) {
					param_save_count--;
					if (param_save_count == 0) {
						if (graph_type.getSelectedItemPosition() == XY_PLOT) {
							graph_param1.setSelection(param1_sel);
							graph_param2.setSelection(param2_sel);
						} else {
							graph_param_pie.setSelection(param_pie_sel);
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
	public void onSaveInstanceState(Bundle outState) {

		outState.putInt("param_pos", graph_facet.getSelectedItemPosition());

		outState.putInt("param1_sel", graph_param1.getSelectedItemPosition());
		outState.putInt("param2_sel", graph_param2.getSelectedItemPosition());
		outState.putInt("param_pie_sel",
				graph_param_pie.getSelectedItemPosition());

		super.onSaveInstanceState(outState);
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

		MainActivity mainActivity = (MainActivity) getSherlockActivity();

		switch (graph_facet.getSelectedItemPosition()) {
		case PerformanceFragmentEdit.GENERAL:
			Log.d("Debug", "generateXYGraph() GENERAL");
			switch (graph_param1.getSelectedItemPosition()) {
			case MATCHES:
				cursor = MainActivity.dbHandle.rawQuery("select count(m."
						+ MatchDb.KEY_ROWID + ")," + column2 + " from "
						+ MatchDb.SQLITE_TABLE + " m where m."
						+ MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY
						+ "'" + mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause + " group by "
						+ column2, null);
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
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause
						+ mainActivity.batting_no_whereClause
						+ mainActivity.how_out_whereClause + " group by "
						+ column2, null);
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
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause
						+ mainActivity.batting_no_whereClause
						+ mainActivity.how_out_whereClause + " group by "
						+ column2, null);
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
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause
						+ mainActivity.batting_no_whereClause
						+ mainActivity.how_out_whereClause + " group by "
						+ column2, null);
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
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause
						+ mainActivity.batting_no_whereClause
						+ mainActivity.how_out_whereClause + " group by "
						+ column2, null);
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
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause
						+ mainActivity.batting_no_whereClause
						+ mainActivity.how_out_whereClause + " group by "
						+ column2, null);
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
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause
						+ mainActivity.batting_no_whereClause
						+ mainActivity.how_out_whereClause + " group by "
						+ column2, null);
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
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause + " group by "
						+ column2, null);
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
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause + " group by "
						+ column2, null);
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
					+ MatchDb.MATCH_HISTORY + "'"
					+ mainActivity.myteam_whereClause
					+ mainActivity.opponent_whereClause
					+ mainActivity.venue_whereClause
					+ mainActivity.overs_whereClause
					+ mainActivity.innings_whereClause
					+ mainActivity.level_whereClause
					+ mainActivity.duration_whereClause
					+ mainActivity.first_whereClause
					+ mainActivity.season_whereClause
					+ mainActivity.result_whereClause + " group by " + column2,
					null);
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
		MainActivity mainActivity = (MainActivity) getSherlockActivity();
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
						+ "'" + mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause + " group by "
						+ MatchDb.KEY_RESULT, null);
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
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause
						+ mainActivity.batting_no_whereClause
						+ mainActivity.how_out_whereClause + " group by p."
						+ PerformanceDb.KEY_BAT_HOW_OUT, null);
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
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause
						+ mainActivity.batting_no_whereClause
						+ mainActivity.how_out_whereClause + " group by p."
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
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause, null);
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
						+ MatchDb.MATCH_HISTORY + "'"
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause, null);
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
						+ MatchDb.MATCH_HISTORY + "'"
						+ mainActivity.myteam_whereClause
						+ mainActivity.opponent_whereClause
						+ mainActivity.venue_whereClause
						+ mainActivity.overs_whereClause
						+ mainActivity.innings_whereClause
						+ mainActivity.level_whereClause
						+ mainActivity.duration_whereClause
						+ mainActivity.first_whereClause
						+ mainActivity.season_whereClause
						+ mainActivity.result_whereClause, null);
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
