package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobCallback;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class DeleteMatchService extends IntentService {
	public static boolean started = true;
	static List<ServerDBCricketMatch> match_list = new ArrayList<ServerDBCricketMatch>();

	static int delete_match_count, delete_performance_count;
	static boolean all_match_done = false, all_performance_done = false;
	static List<ServerDBPerformance> performance_list = new ArrayList<ServerDBPerformance>();

	public DeleteMatchService() {
		super("DeleteMatchService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w("DeleteMatchService", "Started");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("DeleteMatchService", "Ended");
	}

	@SuppressWarnings({ "unused" })
	@Override
	protected void onHandleIntent(Intent intent) {

		StackMobAndroid.init(this, 0, "c52a9f47-baae-41e3-aa63-72177b0c23f7");

		if (AccessSharedPrefs.mPrefs.getString("DeleteMatchServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED)) {

			// TODO Make array for status=deleted
			// create json array of dev, id , mid
			// JSONArray jsonArray = new JSONArray();
			Cursor c = MainActivity.dbHandle.rawQuery("select "
					+ MatchDb.KEY_DEVICE_ID + "," + MatchDb.KEY_ROWID
					+ " from " + MatchDb.SQLITE_TABLE + " where "
					+ MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_DELETED + "'",
					null);
			Log.d("Debug", "Matches to be deleted" + c.getCount());
			if (c.getCount() != 0) {
				delete_match_count = c.getCount();
				int i = 0;
				c.moveToFirst();
				do {

					// Delete Object from StackMob
					ServerDBCricketMatch
							.query(ServerDBCricketMatch.class,
									new StackMobQuery()
											.fieldIsEqualTo(
													"match_id",
													c.getInt(c
															.getColumnIndexOrThrow(MatchDb.KEY_ROWID)))
											.fieldIsEqualTo(
													MatchDb.KEY_DEVICE_ID,
													c.getInt(c
															.getColumnIndexOrThrow(MatchDb.KEY_DEVICE_ID))),
									new StackMobQueryCallback<ServerDBCricketMatch>() {
										@Override
										public void success(
												List<ServerDBCricketMatch> result) {
											Log.d("Debug",
													"Match fetch success");
											for (int i = 0; i < result.size(); i++) {
												match_list.add(result.get(i));
											}
											if (match_list.size() == delete_match_count) {
												all_match_done = true;
											}
											if (all_match_done
													&& all_performance_done) {
												Log.d("Debug",
														"Destroy Objects from Match Success");
												for (int i = 0; i < match_list
														.size(); i++) {
													match_list.get(i)
															.setStatus(2);
													match_list
															.get(i)
															.save(new StackMobCallback() {

																@Override
																public void success(
																		String arg0) {
																	// TODO
																	// Auto-generated
																	// method
																	// stub

																}

																@Override
																public void failure(
																		StackMobException arg0) {
																	// TODO
																	// Auto-generated
																	// method
																	// stub

																}
															});
												}
												for (int i = 0; i < performance_list
														.size(); i++) {
													performance_list.get(i)
															.setStatus(2);
													performance_list
															.get(i)
															.save(new StackMobCallback() {

																@Override
																public void success(
																		String arg0) {
																	// TODO
																	// Auto-generated
																	// method
																	// stub

																}

																@Override
																public void failure(
																		StackMobException arg0) {
																	// TODO
																	// Auto-generated
																	// method
																	// stub

																}
															});
												}
											}
										}

										@Override
										public void failure(StackMobException e) {
											Log.d("Debug", e.toString());
										}
									});

					/*
					 * JSONObject jsonObject = new JSONObject(); try {
					 * jsonObject.put(MatchDb.KEY_DEVICE_ID, c.getString(0));
					 * jsonObject.put(MatchDb.KEY_ROWID, c.getString(1));
					 * jsonArray.put(jsonObject); } catch (JSONException
					 * jsonException) {
					 * 
					 * } c.moveToNext();
					 */
					c.moveToNext();
				} while (!c.isAfterLast());
			}
			c.close();

			c = MainActivity.dbHandle.rawQuery("select "
					+ PerformanceDb.KEY_DEVICE_ID + ","
					+ PerformanceDb.KEY_MATCHID + " from "
					+ PerformanceDb.SQLITE_TABLE + " where "
					+ PerformanceDb.KEY_STATUS + "='" + MatchDb.MATCH_DELETED
					+ "'", null);

			if (c.getCount() != 0) {
				delete_performance_count = c.getCount();
				int i = 0;
				c.moveToFirst();
				do {

					// Delete Object from StackMob
					ServerDBPerformance
							.query(ServerDBPerformance.class,
									new StackMobQuery()
											.fieldIsEqualTo(
													PerformanceDb.KEY_MATCHID,
													c.getInt(c
															.getColumnIndexOrThrow(PerformanceDb.KEY_MATCHID)))
											.fieldIsEqualTo(
													PerformanceDb.KEY_DEVICE_ID,
													c.getInt(c
															.getColumnIndexOrThrow(PerformanceDb.KEY_DEVICE_ID))),
									new StackMobQueryCallback<ServerDBPerformance>() {
										@Override
										public void success(
												List<ServerDBPerformance> result) {
											Log.d("Debug",
													"Performance Success");
											for (int i = 0; i < result.size(); i++) {
												performance_list.add(result
														.get(i));
											}
											if (performance_list.size() == delete_performance_count) {
												all_performance_done = true;
											}
											if (all_match_done
													&& all_performance_done) {
												Log.d("Debug",
														"Destroy Objects from Performance Success");
												for (int i = 0; i < match_list
														.size(); i++) {
													match_list.get(i)
															.setStatus(2);
													match_list
															.get(i)
															.save(new StackMobCallback() {

																@Override
																public void success(
																		String arg0) {

																}

																@Override
																public void failure(
																		StackMobException arg0) {

																}
															});
												}
												for (int i = 0; i < performance_list
														.size(); i++) {
													performance_list.get(i)
															.setStatus(2);
													performance_list
															.get(i)
															.save(new StackMobCallback() {

																@Override
																public void success(
																		String arg0) {
																}

																@Override
																public void failure(
																		StackMobException arg0) {
																}
															});
												}
											}
										}

										@Override
										public void failure(StackMobException e) {
											Log.d("Debug",
													"Performance Failure");
										}
									});
					c.moveToNext();
				} while (!c.isAfterLast());
			}
			c.close();

			// TODO GCM for Match Delete

			/*
			 * JSONObject jo = new JSONObject(); try { jo.put("json",
			 * jsonArray); } catch (JSONException e1) { // TODO Auto-generated
			 * catch block e1.printStackTrace(); } List<NameValuePair> params =
			 * new ArrayList<NameValuePair>(); params.add(new
			 * BasicNameValuePair("id", AccessSharedPrefs.mPrefs
			 * .getString("id", ""))); params.add(new BasicNameValuePair("json",
			 * jo.toString()));
			 * 
			 * int trial = 1; JSONObject jn = null; final JSONParser jsonParser
			 * = new JSONParser(); while (jsonParser.isOnline(this)) { jn =
			 * jsonParser.makeHttpRequest(
			 * getResources().getString(R.string.match_delete), "POST", params,
			 * this); Log.w("JSON returned", "MatchDeleteService: " + jn);
			 * Log.w("trial value", "MatchDeleteService: " + trial); if (jn !=
			 * null) break; try { Thread.sleep(10 * trial); } catch
			 * (InterruptedException e) { } trial++; }
			 */
		}
	}
}