package co.acjs.cricdecode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;


public class DiaryMatchesFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>{
	static DiaryMatchesFragment	diaryMatchesFragment;
	RelativeLayout				no_matches_added, fb_tip_added;
	ListView					listView;
	static LoaderManager		loader_diary_list;
	static DiaryMatchesFragment	diary_matches_fragment;
	private SimpleCursorAdapter	dataAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		diaryMatchesFragment = this;
		View rootView = inflater.inflate(R.layout.list_container, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		if(savedInstanceState != null){
			getSherlockActivity().getSupportLoaderManager().restartLoader(0, null, this);
		}
		displayListView(view);
		loader_diary_list = getSherlockActivity().getSupportLoaderManager();
		diary_matches_fragment = this;
	}

	@Override
	public void onResume(){
		super.onResume();
		// Starts a new or restarts an existing Loader in this manager
		getSherlockActivity().getSupportLoaderManager().restartLoader(0, null, this);
	}

	private void displayListView(View view){
		Log.d("Debug", "displayListView called");
		// The desired columns to be bound
		String[] columns = new String[ ]{MatchDb.KEY_ROWID, MatchDb.KEY_DEVICE_ID, MatchDb.KEY_INNINGS, MatchDb.KEY_MATCH_DATE, MatchDb.KEY_DURATION, MatchDb.KEY_FIRST_ACTION, MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM, MatchDb.KEY_VENUE, MatchDb.KEY_LEVEL, MatchDb.KEY_OVERS, MatchDb.KEY_SYNCED};
		// the XML defined views which the data will be bound to
		int[] to = new int[ ]{R.id._id, R.id.device_id, R.id.innings, R.id.day, R.id.month, R.id.year, R.id.my_team, R.id.opponent_team, R.id.venue, R.id.level, R.id.overs, R.id.txt_synced};
		// create an adapter from the SimpleCursorAdapter
		dataAdapter = new SimpleCursorAdapter(getSherlockActivity(), R.layout.diary_match_list_item, null, columns, to, 0);
		// get reference to the ListView
		listView = (ListView)view.findViewById(R.id.content_list);
		no_matches_added = (RelativeLayout)view.findViewById(R.id.no_matches_added);
		fb_tip_added = (RelativeLayout)view.findViewById(R.id.fb_tip_rl);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		// Ensures a loader is initialized and active.
		getSherlockActivity().getSupportLoaderManager().initLoader(0, null, this);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> listView, View view, int position, long id){
				Cursor cursor = (Cursor)listView.getItemAtPosition(position);
				int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(MatchDb.KEY_ROWID));
				String deviceId = cursor.getString(cursor.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID));
				int innings = cursor.getInt(cursor.getColumnIndexOrThrow(MatchDb.KEY_INNINGS));
				PerformanceFragmentView.performanceFragmentView = new PerformanceFragmentView();
				Bundle bundle = new Bundle();
				bundle.putInt("rowId", rowId);
				bundle.putString("deviceId", deviceId);
				bundle.putInt("innings", innings);
				PerformanceFragmentView.performanceFragmentView.setArguments(bundle);
				((MainActivity)getSherlockActivity()).currentFragment = MainActivity.PERFORMANCE_FRAGMENT_VIEW;
				((MainActivity)getSherlockActivity()).preFragment = MainActivity.DIARY_MATCHES_FRAGMENT;
				((MainActivity)getSherlockActivity()).onPrepareOptionsMenu(((MainActivity)getSherlockActivity()).current_menu);
				((MainActivity)getSherlockActivity()).selectItem(MainActivity.PERFORMANCE_FRAGMENT_VIEW, false);
			}
		});
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args){
		Log.d("Debug", "on Create Loader");
		MainActivity mainActivity = (MainActivity)getSherlockActivity();
		String[] projection = {MatchDb.KEY_ROWID, MatchDb.KEY_DEVICE_ID, MatchDb.KEY_INNINGS, MatchDb.KEY_MATCH_DATE, MatchDb.KEY_DURATION, MatchDb.KEY_FIRST_ACTION, MatchDb.KEY_MY_TEAM, MatchDb.KEY_OPPONENT_TEAM, MatchDb.KEY_VENUE, MatchDb.KEY_LEVEL, MatchDb.KEY_OVERS, MatchDb.KEY_SYNCED};
		CursorLoader cursorLoader = new CursorLoader(getSherlockActivity(), CricDeCodeContentProvider.CONTENT_URI_MATCH, projection, MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'" + mainActivity.myteam_whereClause + mainActivity.opponent_whereClause + mainActivity.venue_whereClause + mainActivity.overs_whereClause + mainActivity.innings_whereClause + mainActivity.level_whereClause + mainActivity.duration_whereClause + mainActivity.first_whereClause + mainActivity.season_whereClause + mainActivity.result_whereClause, null, MatchDb.KEY_MATCH_DATE + " DESC");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data){
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		Log.d("Debug", "on Load Finished");
		AccessSharedPrefs.mPrefs = getSherlockActivity().getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		AccessSharedPrefs.setInt(getSherlockActivity(), "diaryMatches", data.getCount());
		MatrixCursor mc = new MatrixCursor(data.getColumnNames(), data.getCount());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date date = new Date();
		if(data.getCount() != 0){
			data.moveToFirst();
			do{
				String[] values = new String[data.getColumnCount()];
				for(int i = 0; i < data.getColumnCount(); i++){
					if(i == data.getColumnIndex(MatchDb.KEY_MATCH_DATE)){
						try{
							date = sdf.parse(data.getString(i));
							Calendar cal = new GregorianCalendar();
							cal.setTime(date);
							values[i++] = cal.get(Calendar.DAY_OF_MONTH) + "";
							values[i++] = PerformanceFragmentView.month_str[cal.get(Calendar.MONTH)];
							values[i] = cal.get(Calendar.YEAR) + "";
						}catch(ParseException e){
							e.printStackTrace();
							Log.d("Debug", "Date Exception");
						}
					}else if(i == data.getColumnIndex(MatchDb.KEY_SYNCED)){
						if(data.getInt(i) == 1){
							values[i] = "Synced";
						}else{
							values[i] = "";
						}
					}else{
						values[i] = data.getString(i);
						if(i == data.getColumnIndexOrThrow(MatchDb.KEY_OVERS) && values[i].equals("-1")){
							values[i] = "Unlimited";
						}
					}
				}
				mc.addRow(values);
				data.moveToNext();
			}while(!data.isAfterLast());
			dataAdapter.swapCursor(mc);
		}else{
			dataAdapter.swapCursor(data);
		}
		Log.d("Debug", "List Count " + listView.getCount());
		if(listView != null){
			if(listView.getCount() == 0){
				no_matches_added.setVisibility(View.VISIBLE);
			}else{
				no_matches_added.setVisibility(View.GONE);
			}
			for(int i = 0; i < listView.getCount(); i++){}
		}
		Log.w("fb_got_it", "valu: " + AccessSharedPrefs.mPrefs.getString("fb_got_it", "no"));
		if(AccessSharedPrefs.mPrefs.getString("fb_got_it", "no").equals("no")){
			fb_tip_added.setVisibility(View.VISIBLE);
		}else{
			fb_tip_added.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader){
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		Log.d("Debug", "on Loader Reset");
		dataAdapter.swapCursor(null);
	}

	static String buildSelectedItemString(List<String> items, boolean isInt){
		String[] _items = items.toArray(new String[items.size()]);
		StringBuilder sb = new StringBuilder();
		boolean foundOne = false;
		for(int i = 0; i < _items.length; ++i){
			if(!isInt){
				_items[i] = "'" + _items[i] + "'";
			}else if(_items[i].equals("Unlimited")){
				_items[i] = "-1";
			}
			if(foundOne){
				sb.append(", ");
			}
			foundOne = true;
			sb.append(_items[i]);
		}
		return sb.toString();
	}

	public void deleteMatch(View view){
		RelativeLayout vwParentRow = (RelativeLayout)view.getParent().getParent();
		Log.w("DiaryMatchFragment", "delete");
		TextView child = (TextView)vwParentRow.getChildAt(0);
		String str = child.getText().toString();
		child = (TextView)vwParentRow.getChildAt(1);
		String d_str = child.getText().toString();
		// get the match status
		Cursor c = MainActivity.dbHandle.rawQuery("select " + MatchDb.KEY_SYNCED + " from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_ROWID + " = " + str + " and " + MatchDb.KEY_DEVICE_ID + " = '" + d_str + "'", null);
		c.moveToFirst();
		boolean is_synced = false;
		if(c.getInt(0) == 1){
			is_synced = true;
		}
		c.close();
		if((AccessSharedPrefs.mPrefs.getString("infi_sync", "no").equals("yes") || AccessSharedPrefs.mPrefs.getString("sync", "no").equals("yes")) && is_synced){
			Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/" + str + "/" + d_str);
			ContentValues values = new ContentValues();
			values.put(MatchDb.KEY_STATUS, MatchDb.MATCH_DELETED);
			getSherlockActivity().getContentResolver().update(uri, values, null, null);
			uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/" + str + "/" + d_str);
			ContentValues value = new ContentValues();
			value.put(PerformanceDb.KEY_STATUS, MatchDb.MATCH_DELETED);
			getSherlockActivity().getContentResolver().update(uri, value, null, null);
			AccessSharedPrefs.setString(getSherlockActivity(), "DeleteMatchServiceCalled", CDCAppClass.NEEDS_TO_BE_CALLED);
			Intent i = new Intent(MainActivity.main_context, DeleteMatchService.class);
			try{
				if(DeleteMatchService.started){
					MainActivity.main_context.stopService(i);
					MainActivity.main_context.startService(i);
				}else{
					MainActivity.main_context.startService(i);
				}
			}catch(NullPointerException e){
				MainActivity.main_context.startService(i);
			}
		}else{
			Uri uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE + "/" + str + "/" + d_str);
			getSherlockActivity().getContentResolver().delete(uri, null, null);
			uri = Uri.parse(CricDeCodeContentProvider.CONTENT_URI_MATCH + "/" + str + "/" + d_str);
			getSherlockActivity().getContentResolver().delete(uri, null, null);
		}
		AccessSharedPrefs.mPrefs = getSherlockActivity().getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		AccessSharedPrefs.setInt(getSherlockActivity(), "diaryMatches", AccessSharedPrefs.mPrefs.getInt("diaryMatches", 0) - 1);
		getSherlockActivity().getSupportLoaderManager().restartLoader(0, null, this);
	}
}
