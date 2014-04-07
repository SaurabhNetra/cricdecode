package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import co.acjs.cricdecode.CDCAppClass.TrackerName;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LogIn extends SherlockActivity {
	static GraphUser user;
	static Context login_activity;
	ContentProviderClient client;
	static SQLiteDatabase dbHandle;
	static TextView progressText;
	static Boolean onActivityResult = false;
	static ContentResolver cr;
	static int trial = 1;
	static JSONObject jn = null;
	static JSONParser jsonParser;
	static List<NameValuePair> params;

	/*
	public static void writeToFile(String data) { 		 

 		try {

 

 			File root = new File(Environment.getExternalStorageDirectory(),

 					"CricDeCode");

 

 			if (!root.exists()) {

 

 				root.mkdirs();

 			}

 

File gpxfile = new File(root, "matchsync.txt");

 

 			FileWriter writer = new FileWriter(gpxfile, true);

 			writer.write(data + "\n");

 			writer.flush();

 

 			writer.close();

 

 		} catch (IOException e) {

 

 			Log.e("Exception", "File write failed: " + e.toString());

 

 		}
	} */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	

		setContentView(R.layout.activity_log_in);
		ActionBar actionBar = getSupportActionBar();

		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		cr = getContentResolver();
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.action_bar_login, null);
		actionBar.setCustomView(view);
		AccessSharedPrefs.mPrefs = getSharedPreferences("CricDeCode",
				Context.MODE_PRIVATE);
		login_activity = this;
		final LoginButton loginButton = (LoginButton) findViewById(R.id.login);
		progressText = (TextView) findViewById(R.id.warning_text);
		List<String> permissions = Arrays.asList("user_birthday");
		loginButton.setReadPermissions(permissions);
		loginButton.setApplicationId(getResources().getString(
				R.string.fb_app_id));
		loginButton.setSessionStatusCallback(new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				if (session.isOpened()) {
					loginButton.setVisibility(View.GONE);
					((ProgressBar) findViewById(R.id.progress_bar))
							.setVisibility(View.VISIBLE);
					// make request to the /me API
					Request.newMeRequest(session,
							new Request.GraphUserCallback() {
								// callback after Graph API response with user
								// object
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									if (user != null) {
										Log.w("Face Book Login Complete 1",
												"LogIn: " + user.getBirthday());
										LogIn.user = user;
										progressText.setText("Phase 1 of 3...");
										GCMRegistration();
									}
								}
							}).executeAsync();
				}
			}
		});
		if (getResources().getIdentifier("config_enableTranslucentDecor",
				"bool", "android") != 0)
			makeBarsTranslucent(getWindow());
		com.google.android.gms.analytics.Tracker t = ((CDCAppClass) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName(getResources().getString(R.string.analyticsLogin));
		t.send(new HitBuilders.AppViewBuilder().build());

	}

	public static void showDialog() {
		try {
			((LogIn) login_activity).runOnUiThread(new Runnable() {
				public void run() {
					try {
						new AlertDialog.Builder(login_activity)
								.setTitle("Weak Internet Connection")
								.setMessage(
										"Check your internet connection and restart app.")
								.setNeutralButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												((LogIn) login_activity)
														.finish();
											}
										}).show();
					} catch (Exception e) {
					}
				}
			});
		} catch (Exception e) {
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Google Analytics Stop
		if (!AccessSharedPrefs.mPrefs.getString("isSignedIn", "").equals("")) {
			ConnectivityManager connectivityManager = (ConnectivityManager) this
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
				login_activity.startService(new Intent(login_activity,
						FBRefreshService.class));
			}
			openMainActivity();
		} else if (!onActivityResult) {
			Session session = Session.getActiveSession();
			final LoginButton loginButton = (LoginButton) findViewById(R.id.login);
			try {
				if (loginButton.getVisibility() != View.GONE)
					if (session.isOpened()) {
						loginButton.setVisibility(View.GONE);
						((ProgressBar) findViewById(R.id.progress_bar))
								.setVisibility(View.VISIBLE);
						// make request to the /me API
						Request.newMeRequest(session,
								new Request.GraphUserCallback() {
									// callback after Graph API response with
									// user
									// object
									@Override
									public void onCompleted(GraphUser user,
											Response response) {
										if (user != null) {
											Log.w("Face Book Login Complete 2",
													"LogIn: "
															+ user.getBirthday());

											LogIn.user = user;
											progressText
													.setText("Phase 1 of 3...");
											GCMRegistration();
										}
									}
								}).executeAsync();
					}
			} catch (Exception e) {
				Log.w("Login", "Showing logout");
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Google Analytics Stop
		onActivityResult = false;
	}

	void GCMRegistration() {
		progressText.setText("Phase 2 of 3...");

		client = getContentResolver().acquireContentProviderClient(
				CricDeCodeContentProvider.AUTHORITY);
		dbHandle = ((CricDeCodeContentProvider) client
				.getLocalContentProvider()).getDbHelper().getReadableDatabase();
		AccessSharedPrefs.mPrefs = login_activity.getSharedPreferences(
				"CricDeCode", Context.MODE_PRIVATE);
		GCMRegistrarCompat.checkDevice(this);
		if (BuildConfig.DEBUG) {
			GCMRegistrarCompat.checkManifest(this);
		}
		if (isServiceAvailable()) {
			new RegisterTask(this).execute(getResources().getString(
					R.string.projno));
		}
	}

	boolean isServiceAvailable() {
		int isAvailable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (isAvailable == ConnectionResult.SUCCESS) {
			return true;
		} else if (isAvailable == ConnectionResult.SERVICE_MISSING
				|| isAvailable == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
			progressText
					.setText("Restart App after installing Google Play Services");
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog_play_service_chk);
			dialog.setTitle("Missing Vital App");
			((TextView) dialog.findViewById(R.id.dialog_text))
					.setText("Google Play Services App is not found/updated on your phone.");
			((Button) dialog.findViewById(R.id.yes))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms")));
							dialog.dismiss();
						}
					});
			((Button) dialog.findViewById(R.id.no))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							finish();
						}
					});
			dialog.show();
		}
		return false;
	}

	private static class RegisterTask extends
			GCMRegistrarCompat.BaseRegisterTask {
		RegisterTask(Context context) {
			super(context);
		}

		@Override
		public void onPostExecute(String regid) {
			Log.d(getClass().getSimpleName(), "registered as: " + regid);

			if (regid == null)
				((LogIn) login_activity).GCMRegistration();
			else {
				startApp(regid);
			}
		}
	}

	static void startApp(final String gcm_reg_id) {
		Log.w("Start App", "called");
		progressText.setText("Phase 3 of 3...");
		Log.w("Start App", "else ");
		AccessSharedPrefs.setString(login_activity, "id", user.getId());
		AccessSharedPrefs.setString(login_activity, "f_name",
				user.getFirstName());
		AccessSharedPrefs.setString(login_activity, "l_name",
				user.getLastName());
		AccessSharedPrefs.setString(login_activity, "dob", user.getBirthday());
		AccessSharedPrefs.setString(login_activity, "gcm_reg_id", gcm_reg_id);
		AccessSharedPrefs.setString(login_activity, "fb_link", user.getLink());

		Thread thread = new Thread() {
			@Override
			public void run() {
				params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("gcm_id", gcm_reg_id));
				params.add(new BasicNameValuePair("os", "1"));
				params.add(new BasicNameValuePair("l_name", user.getLastName()));
				params.add(new BasicNameValuePair("f_name", user.getFirstName()));
				params.add(new BasicNameValuePair("fb_link", user.getLink()));
				params.add(new BasicNameValuePair("dob", user.getBirthday()));
				params.add(new BasicNameValuePair("user_id",
						AccessSharedPrefs.mPrefs.getString("id", "")));
				jsonParser = new JSONParser();
				trial = 1;
				jn = null;
				while (jsonParser.isOnline(login_activity)) {
					Log.w("JSONParser", "usertable:: Called");
					jn = jsonParser.makeHttpRequest(
							login_activity.getResources().getString(
									R.string.gae_user_insert), "POST", params,
							login_activity);
					Log.w("JSON returned", "usertable:: " + jn);
					Log.w("trial value", "usertable:: " + trial);

					if (jn != null)
						break;
					try {
						Thread.sleep(10 * trial);
					} catch (InterruptedException e) {
					}
					trial++;
					if (trial == 50)
						break;
				}

				try {
					if (jn.getInt("status") == 1) {
						AccessSharedPrefs.setString(login_activity,
								"device_id", "1");
						openMainActivity();

					} else if (jn.getInt("status") == 2) {

						AccessSharedPrefs.setString(login_activity,
								"device_id", "" + jn.getInt("device_no"));
						AccessSharedPrefs.setString(login_activity, "role",
								jn.getString("role"));
						AccessSharedPrefs.setString(login_activity,
								"battingStyle", jn.getString("batting_style"));
						AccessSharedPrefs.setString(login_activity,
								"bowlingStyle", jn.getString("bowling_style"));
						AccessSharedPrefs.setString(login_activity, "nickname",
								jn.getString("nick_name"));

						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("user_id",
								AccessSharedPrefs.mPrefs.getString("id", "")));
						jsonParser = new JSONParser();
						trial = 1;
						jn = null;

						while (jsonParser.isOnline(login_activity)) {
							Log.w("JSONParser", "chk_removeads:: Called");
							// ping to gae ads table
							jn = jsonParser.makeHttpRequest(
									login_activity.getResources().getString(
											R.string.gae_ads_retrieve), "POST",
									params, login_activity);
							Log.w("JSON returned", "chk_removeads:: " + jn);
							Log.w("trial value", "chk_removeads:: " + trial);
							if (jn != null)
								break;
							try {
								Thread.sleep(10 * trial);
							} catch (InterruptedException e) {
							}
							trial++;
							if (trial == 50)
								break;
						}

						if (jn.getInt("status") == 0) {
							AccessSharedPrefs.setString(login_activity,
									"ad_free", "no");
						} else if (jn.getInt("status") == 1) {
							AccessSharedPrefs.setString(login_activity,
									"ad_free", "yes");
						}

						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("user_id",
								AccessSharedPrefs.mPrefs.getString("id", "")));
						jsonParser = new JSONParser();
						trial = 1;
						jn = null;
						while (jsonParser.isOnline(login_activity)) {
							Log.w("JSONParser", "chk_subinfi:: Called");
							// Ping to gae purchase infi table
							jn = jsonParser.makeHttpRequest(
									login_activity.getResources().getString(
											R.string.gae_infi_retrieve),
									"POST", params, login_activity);
							Log.w("JSON returned", "chk_subinfi:: " + jn);
							Log.w("trial value", "chk_subinfi:: " + trial);
							if (jn != null)
								break;
							try {
								Thread.sleep(10 * trial);
							} catch (InterruptedException e) {
							}
							trial++;
							if (trial == 50)
								break;
						}
						if (jn.getInt("status") == 0) {
							AccessSharedPrefs.setString(login_activity,
									"infi_use", "no");
						} else if (jn.getInt("status") == 1) {
							AccessSharedPrefs.setString(login_activity,
									"infi_use", "yes");
						}

						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("user_id",
								AccessSharedPrefs.mPrefs.getString("id", "")));
						jsonParser = new JSONParser();
						trial = 1;
						jn = null;
						while (jsonParser.isOnline(login_activity)) {
							Log.w("JSONParser", "chk_subinfisync:: Called");
							// Ping to gae purchase infisync table
							jn = jsonParser.makeHttpRequest(
									login_activity.getResources().getString(
											R.string.gae_infisync_retrieve),
									"POST", params, login_activity);
							if (jn != null)
								break;
							try {
								Thread.sleep(10 * trial);
							} catch (InterruptedException e) {
							}
							trial++;
							if (trial == 50)
								break;
						}
						if (jn.getInt("status") == 0) {
							AccessSharedPrefs.setString(login_activity,
									"infi_sync", "no");
						} else if (jn.getInt("status") == 1) {
							AccessSharedPrefs.setString(login_activity,
									"infi_sync", "yes");
						}

						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("user_id",
								AccessSharedPrefs.mPrefs.getString("id", "")));
						jsonParser = new JSONParser();
						trial = 1;
						jn = null;
						while (jsonParser.isOnline(login_activity)) {
							Log.w("JSONParser", "chk_subsync:: Called");
							// Ping to gae purchase infisync table
							jn = jsonParser.makeHttpRequest(
									login_activity.getResources().getString(
											R.string.gae_sync_retrieve),
									"POST", params, login_activity);
							Log.w("JSON returned", "chk_subsync:: " + jn);
							Log.w("trial value", "chk_subsync:: " + trial);
							if (jn != null)
								break;
							try {
								Thread.sleep(10 * trial);
							} catch (InterruptedException e) {
							}
							trial++;
							if (trial == 50)
								break;
						}
						if (jn.getInt("status") == 0) {
							AccessSharedPrefs.setString(login_activity, "sync",
									"no");
						} else if (jn.getInt("status") == 1) {
							AccessSharedPrefs.setString(login_activity, "sync",
									"yes");
						}

						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("user_id",
								AccessSharedPrefs.mPrefs.getString("id", "")));
						jsonParser = new JSONParser();
						trial = 1;
						jn = null;
						while (jsonParser.isOnline(login_activity)) {
							Log.w("JSONParser", "get_matches:: Called");
							// fetch matches where status=0
							jn = jsonParser.makeHttpRequest(
									login_activity.getResources().getString(
											R.string.gae_match_fetch), "POST",
									params, login_activity);
							Log.w("JSON returned", "get_matches:: " + jn);
							Log.w("trial value", "get_matches:: " + trial);
							if (jn != null)
								break;
							try {
								Thread.sleep(10 * trial);
							} catch (InterruptedException e) {
							}
							trial++;
							if (trial == 50)
								break;
						}

						JSONArray ja = jn.getJSONArray("matches");
						for (int i = 0; i < ja.length(); i++) {

							Log.w("LoginIn", "cricket_match in loop!!" + i);
							JSONObject jo = ja.getJSONObject(i);
							ContentValues values = new ContentValues();
							values.put(MatchDb.KEY_ROWID, jo.getInt("match_id"));
							values.put(MatchDb.KEY_DEVICE_ID,
									"" + jo.getInt("device_id"));
							values.put(MatchDb.KEY_MATCH_DATE,
									jo.getString("match_date"));
							values.put(MatchDb.KEY_MY_TEAM,
									jo.getString("my_team"));
							values.put(MatchDb.KEY_OPPONENT_TEAM,
									jo.getString("opponent_team"));
							values.put(MatchDb.KEY_VENUE, jo.getString("venue"));
							values.put(MatchDb.KEY_OVERS, jo.getInt("overs"));
							values.put(MatchDb.KEY_INNINGS,
									jo.getInt("innings"));
							values.put(MatchDb.KEY_RESULT,
									jo.getString("result"));
							values.put(MatchDb.KEY_LEVEL, jo.getString("level"));
							values.put(MatchDb.KEY_FIRST_ACTION,
									jo.getString("first_action"));
							values.put(MatchDb.KEY_DURATION,
									jo.getString("duration"));
							values.put(MatchDb.KEY_REVIEW,
									jo.getString("review"));
							values.put(MatchDb.KEY_STATUS,
									MatchDb.MATCH_HISTORY);
							values.put(MatchDb.KEY_SYNCED, 1);
							String devid = "" + jo.getInt("device_id");
							try {
								Cursor c = LogIn.dbHandle.rawQuery(
										"select " + MatchDb.KEY_ROWID
												+ " from "
												+ MatchDb.SQLITE_TABLE
												+ " where " + MatchDb.KEY_ROWID
												+ " = " + jo.getInt("match_id")
												+ " and "
												+ MatchDb.KEY_DEVICE_ID
												+ " = '" + devid + "'", null);
								if (c.getCount() == 0) {
									Uri u = cr
											.insert(CricDeCodeContentProvider.CONTENT_URI_MATCH,
													values);
								}
								c.close();
							} catch (Exception e) {
								Log.w("login", "Exception e" + e);
							}

						}

						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("user_id",
								AccessSharedPrefs.mPrefs.getString("id", "")));
						jsonParser = new JSONParser();
						trial = 1;
						jn = null;
						while (jsonParser.isOnline(login_activity)) {
							Log.w("JSONParser", "get_performances:: Called");
							// fetch performances where status=0
							jn = jsonParser.makeHttpRequest(
									login_activity.getResources().getString(
											R.string.gae_per_fetch), "POST",
									params, login_activity);
							Log.w("JSON returned", "get_performances:: " + jn);
							Log.w("trial value", "get_performances:: " + trial);
							if (jn != null)
								break;
							try {
								Thread.sleep(10 * trial);
							} catch (InterruptedException e) {
							}
							trial++;
							if (trial == 50)
								break;
						}

						ja = jn.getJSONArray("performances");
						for (int i = 0; i < ja.length(); i++) {
							JSONObject jo = ja.getJSONObject(i);
							ContentValues values = new ContentValues();
							values.put(PerformanceDb.KEY_MATCHID,
									jo.getInt("match_id"));
							values.put(PerformanceDb.KEY_DEVICE_ID,
									"" + jo.getInt("device_id"));
							values.put(PerformanceDb.KEY_ROWID,
									jo.getInt("per_id"));
							values.put(PerformanceDb.KEY_INNING,
									jo.getInt("inning"));
							values.put(PerformanceDb.KEY_BAT_NUM,
									jo.getInt("bat_num"));
							values.put(PerformanceDb.KEY_BAT_RUNS,
									jo.getInt("bat_runs"));
							values.put(PerformanceDb.KEY_BAT_BALLS,
									jo.getInt("bat_balls"));
							values.put(PerformanceDb.KEY_BAT_TIME,
									jo.getInt("bat_time"));
							values.put(PerformanceDb.KEY_BAT_FOURS,
									jo.getInt("bat_fours"));
							values.put(PerformanceDb.KEY_BAT_SIXES,
									jo.getInt("bat_sixes"));
							values.put(PerformanceDb.KEY_BAT_HOW_OUT,
									jo.getString("bat_dismissal"));
							values.put(PerformanceDb.KEY_BAT_BOWLER_TYPE,
									jo.getString("bat_bowler_type"));
							values.put(PerformanceDb.KEY_BAT_FIELDING_POSITION,
									jo.getString("bat_fielding_position"));
							values.put(PerformanceDb.KEY_BAT_CHANCES,
									jo.getInt("bat_chances"));
							values.put(PerformanceDb.KEY_BOWL_BALLS,
									jo.getInt("bowl_balls"));
							values.put(PerformanceDb.KEY_BOWL_SPELLS,
									jo.getInt("bowl_spells"));
							values.put(PerformanceDb.KEY_BOWL_MAIDENS,
									jo.getInt("bowl_maidens"));
							values.put(PerformanceDb.KEY_BOWL_RUNS,
									jo.getInt("bowl_runs"));
							values.put(PerformanceDb.KEY_BOWL_FOURS,
									jo.getInt("bowl_fours"));
							values.put(PerformanceDb.KEY_BOWL_SIXES,
									jo.getInt("bowl_sixes"));
							values.put(PerformanceDb.KEY_BOWL_WKTS_LEFT,
									jo.getInt("bowl_wkts_left"));
							values.put(PerformanceDb.KEY_BOWL_WKTS_RIGHT,
									jo.getInt("bowl_wkts_right"));
							values.put(PerformanceDb.KEY_BOWL_CATCHES_DROPPED,
									jo.getInt("bowl_catches_dropped"));
							values.put(PerformanceDb.KEY_BOWL_NOBALLS,
									jo.getInt("bowl_no_balls"));
							values.put(PerformanceDb.KEY_BOWL_WIDES,
									jo.getInt("bowl_wides"));
							values.put(PerformanceDb.KEY_FIELD_SLIP_CATCH,
									jo.getInt("field_slip_catch"));
							values.put(PerformanceDb.KEY_FIELD_CLOSE_CATCH,
									jo.getInt("field_close_catch"));
							values.put(PerformanceDb.KEY_FIELD_CIRCLE_CATCH,
									jo.getInt("field_circle_catch"));
							values.put(PerformanceDb.KEY_FIELD_DEEP_CATCH,
									jo.getInt("field_deep_catch"));
							values.put(PerformanceDb.KEY_FIELD_RO_CIRCLE,
									jo.getInt("field_ro_circle"));
							values.put(
									PerformanceDb.KEY_FIELD_RO_DIRECT_CIRCLE,
									jo.getInt("field_ro_direct_circle"));
							values.put(PerformanceDb.KEY_FIELD_RO_DEEP,
									jo.getInt("field_ro_deep"));
							values.put(PerformanceDb.KEY_FIELD_RO_DIRECT_DEEP,
									jo.getInt("field_ro_direct_deep"));
							values.put(PerformanceDb.KEY_FIELD_STUMPINGS,
									jo.getInt("field_stumpings"));
							values.put(PerformanceDb.KEY_FIELD_BYES,
									jo.getInt("field_byes"));
							values.put(PerformanceDb.KEY_FIELD_MISFIELDS,
									jo.getInt("field_misfield"));
							values.put(PerformanceDb.KEY_FIELD_CATCHES_DROPPED,
									jo.getInt("field_catches_dropped"));
							values.put(PerformanceDb.KEY_SYNCED, 1);
							values.put(PerformanceDb.KEY_STATUS,
									MatchDb.MATCH_HISTORY);
							String dev_per = "" + jo.getInt("device_id");
							Cursor c = LogIn.dbHandle.rawQuery(
									"select " + PerformanceDb.KEY_ROWID
											+ " from "
											+ PerformanceDb.SQLITE_TABLE
											+ " where "
											+ PerformanceDb.KEY_MATCHID + " = "
											+ jo.getInt("match_id") + " and "
											+ PerformanceDb.KEY_DEVICE_ID
											+ " = '" + dev_per + "' and "
											+ PerformanceDb.KEY_INNING + " = "
											+ jo.getInt("inning"), null);
							Log.w("LoginIn",
									"count of performance: " + c.getCount());
							if (c.getCount() == 0) {
								Uri u = cr
										.insert(CricDeCodeContentProvider.CONTENT_URI_PERFORMANCE,
												values);
							}
							c.close();
						}
						openMainActivity();
					}
				} catch (NullPointerException e) {
					showDialog();
				} catch (Exception e) {
				}
			}
		};

		thread.start();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
		onActivityResult = true;
	}

	public static void openMainActivity() {

		((Activity) login_activity).runOnUiThread(new Runnable() {
			public void run() {
				Intent intent = new Intent(login_activity, MainActivity.class);
				login_activity.startActivity(intent);
				((LogIn) login_activity).finish();
			}
		});

	}

	@TargetApi(19)
	private void makeBarsTranslucent(Window window) {
		findViewById(R.id.padding_top_frame1).setVisibility(View.VISIBLE);
		window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	}

}