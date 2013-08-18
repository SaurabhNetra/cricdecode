package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.Arrays;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class AnalysisFragment extends SherlockFragment {

	static AnalysisFragment analysisFragment;

	public static final int XY_PLOT = 0, PIE_CHART = 1;

	// PIE CHART CONSTANTS
	// GENERAL
	public static final int MATCH_RESULT = 0;
	// BATTING
	public static final int HOW_OUT = 0;
	// BOWLING
	public static final int WICKETS = 0;
	// FIELDING
	public static final int CATCHES = 0, RUNOUTS = 1;

	// Layout Fields
	Spinner graph_facet, graph_type, graph_param1, graph_param2,
			graph_param_pie;
	RelativeLayout graph_category_1, graph_category_2;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		analysisFragment = this;
		View rootView = inflater.inflate(R.layout.analysis_fragment, container,
				false);
		init(rootView);
		return rootView;
	}

	public void init(View view) {
		graph_facet = (Spinner) view.findViewById(R.id.graph_facet);
		graph_type = (Spinner) view.findViewById(R.id.graph_type);
		graph_param1 = (Spinner) view.findViewById(R.id.graph_param1);
		graph_param2 = (Spinner) view.findViewById(R.id.graph_param2);
		graph_param_pie = (Spinner) view.findViewById(R.id.graph_param_pie);
		graph_category_1 = (RelativeLayout) view
				.findViewById(R.id.graph_category_1);
		graph_category_2 = (RelativeLayout) view
				.findViewById(R.id.graph_category_2);

		String[] arr = getResources().getStringArray(R.array.graph_param2);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getSherlockActivity(), android.R.layout.simple_spinner_item,
				arr);
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
				graph_param1.setAdapter(adapter);
				adapter = new ArrayAdapter<String>(getSherlockActivity(),
						android.R.layout.simple_spinner_item, arr2);
				graph_param_pie.setAdapter(adapter);
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
					graph_category_1.setVisibility(View.VISIBLE);
					graph_category_2.setVisibility(View.GONE);
					break;
				case PIE_CHART:
					graph_category_1.setVisibility(View.GONE);
					graph_category_2.setVisibility(View.VISIBLE);
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
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
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

	public void generateXYGraph() {
		Log.d("Debug", "generateXYGraph() called");
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
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		if (label != null) {
			Toast.makeText(getSherlockActivity(),
					Arrays.toString(label) + " " + Arrays.toString(values),
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getSherlockActivity(), "No Data Available ",
					Toast.LENGTH_LONG).show();
		}
	}
}
