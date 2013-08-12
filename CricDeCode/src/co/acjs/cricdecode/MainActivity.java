package co.acjs.cricdecode;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentProviderClient;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;

public class MainActivity extends SherlockFragmentActivity {

	// Declare Variables
	DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	ActionBarDrawerToggle mDrawerToggle;
	MenuListAdapter mMenuAdapter;
	String[] title;
	int currentFragment;
	Menu current_menu;
	static SQLiteDatabase dbHandle;

	static ContentProviderClient client;

	// Declare Constants
	static final int PROFILE_FRAGMENT = 0, DIARY_MATCHES_FRAGMENT = 2,
			ONGOING_MATCHES_FRAGMENT = 3, MATCH_CREATION_FRAGMENT = 4,
			PERFORMANCE_FRAGMENT_EDIT = 5, PERFORMANCE_FRAGMENT_VIEW = 6;

	static {
		Log.d("Debug", "Static Initializer");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_main);

		getSupportActionBar().setDisplayShowTitleEnabled(false);

		client = getContentResolver().acquireContentProviderClient(
				CricDeCodeContentProvider.AUTHORITY);

		dbHandle = ((CricDeCodeContentProvider) client
				.getLocalContentProvider()).getDbHelper().getReadableDatabase();

		// Generate title
		title = getResources().getStringArray(R.array.drawer_list_item);

		// Locate DrawerLayout in drawer_main.xml
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// Locate ListView in drawer_main.xml
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// Pass results to MenuListAdapter Class
		mMenuAdapter = new MenuListAdapter(this, title);

		// Set the MenuListAdapter to the ListView
		mDrawerList.setAdapter(mMenuAdapter);

		// Capture button clicks on side menu
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				// TODO Auto-generated method stub
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerOpened(drawerView);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			Log.d("Debug", "Saved is null");
			currentFragment = PROFILE_FRAGMENT;
			ProfileFragment.currentProfileFragment = ProfileFragment.PROFILE_VIEW_FRAGMENT;
			selectItem(currentFragment, true);
		} else {
			currentFragment = savedInstanceState.getInt("currentFragment");
			switch (currentFragment) {
			case PROFILE_FRAGMENT:
				ProfileFragment.profileFragment = (ProfileFragment) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case MATCH_CREATION_FRAGMENT:
				MatchCreationFragment.matchCreationFragment = (MatchCreationFragment) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case DIARY_MATCHES_FRAGMENT:
				DiaryMatchesFragment.diaryMatchesFragment = (DiaryMatchesFragment) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case ONGOING_MATCHES_FRAGMENT:
				OngoingMatchesFragment.ongoingMatchesFragment = (OngoingMatchesFragment) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case PERFORMANCE_FRAGMENT_EDIT:
				PerformanceFragmentEdit.performanceFragmentEdit = (PerformanceFragmentEdit) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case PERFORMANCE_FRAGMENT_VIEW:
				PerformanceFragmentView.performanceFragmentView = (PerformanceFragmentView) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			default:
				break;
			}
			Log.d("Debug", "currentFragment " + currentFragment);
			selectItem(currentFragment, false);
		}

		// Look up the AdView as a resource and load a request. final AdView

		final AdView adView = (AdView) findViewById(R.id.adView);
		(new Thread() {
			public void run() {
				Looper.prepare();
				adView.loadAd(new AdRequest());
			}
		}).start();

	}

	@Override
	protected void onStart() {
		super.onStart();

		// Google Analytics Stop
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		switch (currentFragment) {
		case PROFILE_FRAGMENT:
			if (ProfileFragment.currentProfileFragment == ProfileFragment.PROFILE_VIEW_FRAGMENT) {
				menu.add(Menu.NONE, R.string.edit_profile, Menu.NONE,
						R.string.edit_profile);
				menu.findItem(R.string.edit_profile).setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM);

			} else {
				menu.add(Menu.NONE, R.string.save_profile, Menu.NONE,
						R.string.save_profile);
				menu.findItem(R.string.save_profile).setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM);
			}
			break;
		case MATCH_CREATION_FRAGMENT:
			menu.add(Menu.NONE, R.string.create_match, Menu.NONE,
					R.string.create_match);
			menu.findItem(R.string.create_match).setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
			break;
		case DIARY_MATCHES_FRAGMENT:
			menu.add(Menu.NONE, R.string.filter, Menu.NONE, R.string.filter);
			menu.findItem(R.string.filter).setShowAsAction(
					MenuItem.SHOW_AS_ACTION_IF_ROOM);
			break;
		case PERFORMANCE_FRAGMENT_EDIT:
			menu.add(Menu.NONE, R.string.save_performance, Menu.NONE,
					R.string.save_performance);
			menu.findItem(R.string.save_performance).setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
			break;
		default:
			break;
		}
		current_menu = menu;
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("Debug", "On option item selected");
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			break;
		case R.string.edit_profile:
			ProfileFragment.currentProfileFragment = ProfileFragment.PROFILE_EDIT_FRAGMENT;
			onPrepareOptionsMenu(current_menu);
			ProfileFragment.profileFragment.viewFragment();
			break;
		case R.string.save_profile:
			ProfileEditFragment.profileEditFragment.saveEditedProfile();
			ProfileFragment.currentProfileFragment = ProfileFragment.PROFILE_VIEW_FRAGMENT;
			onPrepareOptionsMenu(current_menu);
			ProfileFragment.profileFragment.viewFragment();
			break;
		case R.string.create_match:
			MatchCreationFragment.matchCreationFragment.insertMatch();
			onPrepareOptionsMenu(current_menu);
			break;
		case R.string.save_performance:
			PerformanceFragmentEdit.performanceFragmentEdit.insertOrUpdate();
			onPrepareOptionsMenu(current_menu);
			break;
		case R.string.filter:
			// custom dialog
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.filter_general);
			dialog.setTitle("Filter");

			final MultiSelectSpinner season_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.season_list);
			season_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.season_list);
			season_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.season_list_selected);
			season_spinner._proxyAdapter.clear();
			season_spinner._proxyAdapter.add(season_spinner
					.buildSelectedItemString());
			season_spinner.setSelection(0);

			final MultiSelectSpinner my_team_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.my_team_list);
			my_team_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.my_team_list);
			my_team_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.my_team_list_selected);
			my_team_spinner._proxyAdapter.clear();
			my_team_spinner._proxyAdapter.add(my_team_spinner
					.buildSelectedItemString());
			my_team_spinner.setSelection(0);

			final MultiSelectSpinner opponent_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.opponent_list);
			opponent_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.opponent_list);
			opponent_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.opponent_list_selected);
			opponent_spinner._proxyAdapter.clear();
			opponent_spinner._proxyAdapter.add(opponent_spinner
					.buildSelectedItemString());
			opponent_spinner.setSelection(0);

			final MultiSelectSpinner venue_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.venue_list);
			venue_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.venue_list);
			venue_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.venue_list_selected);
			venue_spinner._proxyAdapter.clear();
			venue_spinner._proxyAdapter.add(venue_spinner
					.buildSelectedItemString());
			venue_spinner.setSelection(0);

			final MultiSelectSpinner result_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.result_list);
			result_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.result_list);
			result_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.result_list_selected);
			result_spinner._proxyAdapter.clear();
			result_spinner._proxyAdapter.add(result_spinner
					.buildSelectedItemString());
			result_spinner.setSelection(0);

			final MultiSelectSpinner level_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.level_list);
			level_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.level_list);
			level_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.level_list_selected);
			level_spinner._proxyAdapter.clear();
			level_spinner._proxyAdapter.add(level_spinner
					.buildSelectedItemString());
			level_spinner.setSelection(0);

			final MultiSelectSpinner overs_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.overs_list);
			overs_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.overs_list);
			overs_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.overs_list_selected);
			overs_spinner._proxyAdapter.clear();
			overs_spinner._proxyAdapter.add(overs_spinner
					.buildSelectedItemString());
			overs_spinner.setSelection(0);

			final MultiSelectSpinner innings_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.innings_list);
			innings_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.innings_list);
			innings_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.innings_list_selected);
			innings_spinner._proxyAdapter.clear();
			innings_spinner._proxyAdapter.add(innings_spinner
					.buildSelectedItemString());
			innings_spinner.setSelection(0);

			final MultiSelectSpinner duration_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.duration_list);
			duration_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.duration_list);
			duration_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.duration_list_selected);
			duration_spinner._proxyAdapter.clear();
			duration_spinner._proxyAdapter.add(duration_spinner
					.buildSelectedItemString());
			duration_spinner.setSelection(0);

			final MultiSelectSpinner first_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.first_list);
			first_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.first_list);
			first_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.first_list_selected);
			first_spinner._proxyAdapter.clear();
			first_spinner._proxyAdapter.add(first_spinner
					.buildSelectedItemString());
			first_spinner.setSelection(0);

			Button dialogButton = (Button) dialog.findViewById(R.id.okay);
			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					DiaryMatchesFragment.diaryMatchesFragment.season_list_selected = season_spinner
							.getSelectedStrings();
					String str = DiaryMatchesFragment.diaryMatchesFragment
							.buildSelectedItemString(
									DiaryMatchesFragment.diaryMatchesFragment.season_list_selected,
									false);
					if (!str.equals("")) {
						DiaryMatchesFragment.diaryMatchesFragment.season_whereClause = " and strftime('%Y',"
								+ MatchDb.KEY_MATCH_DATE + ") in(" + str + ")";
					} else {
						DiaryMatchesFragment.diaryMatchesFragment.season_whereClause = " and strftime('%Y',"
								+ MatchDb.KEY_MATCH_DATE + ") in('')";
					}

					DiaryMatchesFragment.diaryMatchesFragment.my_team_list_selected = my_team_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.diaryMatchesFragment
							.buildSelectedItemString(
									DiaryMatchesFragment.diaryMatchesFragment.my_team_list_selected,
									false);
					if (!str.equals("")) {
						DiaryMatchesFragment.diaryMatchesFragment.myteam_whereClause = " and "
								+ MatchDb.KEY_MY_TEAM + " in(" + str + ")";
					} else {
						DiaryMatchesFragment.diaryMatchesFragment.myteam_whereClause = " and "
								+ MatchDb.KEY_MY_TEAM + " in('')";
					}

					DiaryMatchesFragment.diaryMatchesFragment.opponent_list_selected = opponent_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.diaryMatchesFragment
							.buildSelectedItemString(
									DiaryMatchesFragment.diaryMatchesFragment.opponent_list_selected,
									false);
					if (!str.equals("")) {
						DiaryMatchesFragment.diaryMatchesFragment.opponent_whereClause = " and "
								+ MatchDb.KEY_OPPONENT_TEAM
								+ " in("
								+ str
								+ ")";
					} else {
						DiaryMatchesFragment.diaryMatchesFragment.opponent_whereClause = " and "
								+ MatchDb.KEY_OPPONENT_TEAM + " in('')";
					}

					DiaryMatchesFragment.diaryMatchesFragment.venue_list_selected = venue_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.diaryMatchesFragment
							.buildSelectedItemString(
									DiaryMatchesFragment.diaryMatchesFragment.venue_list_selected,
									false);
					if (!str.equals("")) {
						DiaryMatchesFragment.diaryMatchesFragment.venue_whereClause = " and "
								+ MatchDb.KEY_VENUE + " in(" + str + ")";
					} else {
						DiaryMatchesFragment.diaryMatchesFragment.venue_whereClause = " and "
								+ MatchDb.KEY_VENUE + " in('')";
					}

					DiaryMatchesFragment.diaryMatchesFragment.result_list_selected = result_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.diaryMatchesFragment
							.buildSelectedItemString(
									DiaryMatchesFragment.diaryMatchesFragment.result_list_selected,
									false);
					if (!str.equals("")) {
						DiaryMatchesFragment.diaryMatchesFragment.result_whereClause = " and "
								+ MatchDb.KEY_RESULT + " in(" + str + ")";
					} else {
						DiaryMatchesFragment.diaryMatchesFragment.result_whereClause = " and "
								+ MatchDb.KEY_RESULT + " in('')";
					}

					DiaryMatchesFragment.diaryMatchesFragment.level_list_selected = level_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.diaryMatchesFragment
							.buildSelectedItemString(
									DiaryMatchesFragment.diaryMatchesFragment.level_list_selected,
									false);
					if (!str.equals("")) {
						DiaryMatchesFragment.diaryMatchesFragment.level_whereClause = " and "
								+ MatchDb.KEY_LEVEL + " in(" + str + ")";
					} else {
						DiaryMatchesFragment.diaryMatchesFragment.level_whereClause = " and "
								+ MatchDb.KEY_LEVEL + " in('')";
					}

					DiaryMatchesFragment.diaryMatchesFragment.overs_list_selected = overs_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.diaryMatchesFragment
							.buildSelectedItemString(
									DiaryMatchesFragment.diaryMatchesFragment.overs_list_selected,
									true);
					if (!str.equals("")) {
						DiaryMatchesFragment.diaryMatchesFragment.overs_whereClause = " and "
								+ MatchDb.KEY_OVERS + " in(" + str + ")";
					} else {
						DiaryMatchesFragment.diaryMatchesFragment.overs_whereClause = " and "
								+ MatchDb.KEY_OVERS + " in(-2)";
					}

					DiaryMatchesFragment.diaryMatchesFragment.innings_list_selected = innings_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.diaryMatchesFragment
							.buildSelectedItemString(
									DiaryMatchesFragment.diaryMatchesFragment.innings_list_selected,
									true);
					if (!str.equals("")) {
						DiaryMatchesFragment.diaryMatchesFragment.innings_whereClause = " and "
								+ MatchDb.KEY_INNINGS + " in(" + str + ")";
					} else {
						DiaryMatchesFragment.diaryMatchesFragment.innings_whereClause = " and "
								+ MatchDb.KEY_INNINGS + " in(-2)";
					}

					DiaryMatchesFragment.diaryMatchesFragment.duration_list_selected = duration_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.diaryMatchesFragment
							.buildSelectedItemString(
									DiaryMatchesFragment.diaryMatchesFragment.duration_list_selected,
									false);
					if (!str.equals("")) {
						DiaryMatchesFragment.diaryMatchesFragment.duration_whereClause = " and "
								+ MatchDb.KEY_DURATION + " in(" + str + ")";
					} else {
						DiaryMatchesFragment.diaryMatchesFragment.duration_whereClause = " and "
								+ MatchDb.KEY_DURATION + " in('')";
					}

					DiaryMatchesFragment.diaryMatchesFragment.first_list_selected = first_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.diaryMatchesFragment
							.buildSelectedItemString(
									DiaryMatchesFragment.diaryMatchesFragment.first_list_selected,
									false);
					if (!str.equals("")) {
						DiaryMatchesFragment.diaryMatchesFragment.first_whereClause = " and "
								+ MatchDb.KEY_FIRST_ACTION + " in(" + str + ")";
					} else {
						DiaryMatchesFragment.diaryMatchesFragment.first_whereClause = " and "
								+ MatchDb.KEY_FIRST_ACTION + " in('')";
					}

					DiaryMatchesFragment.diaryMatchesFragment
							.getSherlockActivity()
							.getSupportLoaderManager()
							.restartLoader(0, null,
									DiaryMatchesFragment.diaryMatchesFragment);
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		default:
			break;

		}
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			invalidateOptionsMenu();
		}

		return super.onOptionsItemSelected(item);
	}

	// The click listener for ListView in the navigation drawer
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (currentFragment != position) {
				currentFragment = position;
				onPrepareOptionsMenu(current_menu);
				selectItem(position, true);
			} else {
				// Close drawer
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		}
	}

	public void selectItem(int position, boolean newInstance) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// Locate Position
		switch (position) {
		case PROFILE_FRAGMENT:
			Log.d("Debug", "Select Profile");
			getSupportActionBar().setDisplayShowCustomEnabled(false);
			if (newInstance) {
				ft.replace(R.id.content_frame, new ProfileFragment());
			} else {
				ft.replace(R.id.content_frame, ProfileFragment.profileFragment);
			}
			break;
		case ONGOING_MATCHES_FRAGMENT:
			getSupportActionBar().setDisplayShowCustomEnabled(false);
			if (newInstance) {
				ft.replace(R.id.content_frame, new OngoingMatchesFragment());
			} else {
				ft.replace(R.id.content_frame,
						OngoingMatchesFragment.ongoingMatchesFragment);
			}
			break;
		case DIARY_MATCHES_FRAGMENT:
			getSupportActionBar().setDisplayShowCustomEnabled(false);
			if (newInstance) {
				ft.replace(R.id.content_frame, new DiaryMatchesFragment());
			} else {
				ft.replace(R.id.content_frame,
						DiaryMatchesFragment.diaryMatchesFragment);
			}
			break;
		case MATCH_CREATION_FRAGMENT:
			getSupportActionBar().setDisplayShowCustomEnabled(false);
			if (newInstance) {
				ft.replace(R.id.content_frame, new MatchCreationFragment());
			} else {
				ft.replace(R.id.content_frame,
						MatchCreationFragment.matchCreationFragment);
			}
			break;
		case PERFORMANCE_FRAGMENT_EDIT:
			getSupportActionBar().setDisplayShowCustomEnabled(true);
			getSupportActionBar().setCustomView(R.layout.innings_spinner);
			if (newInstance) {
				ft.replace(R.id.content_frame, new PerformanceFragmentEdit());
			} else {
				ft.replace(R.id.content_frame,
						PerformanceFragmentEdit.performanceFragmentEdit);
			}
			break;
		case PERFORMANCE_FRAGMENT_VIEW:
			getSupportActionBar().setDisplayShowCustomEnabled(true);
			getSupportActionBar().setCustomView(R.layout.innings_spinner);
			if (newInstance) {
				ft.replace(R.id.content_frame, new PerformanceFragmentView());
			} else {
				ft.replace(R.id.content_frame,
						PerformanceFragmentView.performanceFragmentView);
			}
			break;
		default:
			break;
		}
		ft.commit();
		mDrawerList.setItemChecked(position, true);
		// Close drawer
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("Debug", "Save currentFragment " + currentFragment);
		outState.putInt("currentFragment", currentFragment);

		switch (currentFragment) {
		case PROFILE_FRAGMENT:
			getSupportFragmentManager().putFragment(outState,
					"currentFragmentInstance", ProfileFragment.profileFragment);
			break;
		case ONGOING_MATCHES_FRAGMENT:
			getSupportFragmentManager().putFragment(outState,
					"currentFragmentInstance",
					OngoingMatchesFragment.ongoingMatchesFragment);
			break;
		case DIARY_MATCHES_FRAGMENT:
			getSupportFragmentManager().putFragment(outState,
					"currentFragmentInstance",
					DiaryMatchesFragment.diaryMatchesFragment);
			break;
		case MATCH_CREATION_FRAGMENT:
			getSupportFragmentManager().putFragment(outState,
					"currentFragmentInstance",
					MatchCreationFragment.matchCreationFragment);
			break;
		case PERFORMANCE_FRAGMENT_EDIT:
			getSupportFragmentManager().putFragment(outState,
					"currentFragmentInstance",
					PerformanceFragmentEdit.performanceFragmentEdit);
			break;
		case PERFORMANCE_FRAGMENT_VIEW:
			getSupportFragmentManager().putFragment(outState,
					"currentFragmentInstance",
					PerformanceFragmentView.performanceFragmentView);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onStop() {
		super.onStop();

		// Google Analytics Start
		EasyTracker.getInstance().activityStart(this);
	}

	public void onClick(View view) {
		final Dialog dialog;
		final View finalview;
		switch (view.getId()) {
		case R.id.date_of_birth:
			showDatePicker(R.id.date_of_birth);
			break;
		case R.id.date_of_match_row:
			showDatePicker(R.id.date_of_match);
			break;
		case R.id.profile_picture:
			ProfileEditFragment.profileEditFragment.getProfilePicture();
			break;
		case R.id.add_to_career:
			dialog = new Dialog(this);
			finalview = view;
			dialog.setContentView(R.layout.dialog_confirmation);
			dialog.setTitle("Add to Career");

			TextView dialogText = (TextView) dialog
					.findViewById(R.id.dialog_text);
			dialogText
					.setText("Are you sure you want to Add this Match to your Career?");

			Button yes = (Button) dialog.findViewById(R.id.yes);
			yes.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					OngoingMatchesFragment.ongoingMatchesFragment
							.addToCareer(finalview);
					dialog.dismiss();
				}
			});

			Button no = (Button) dialog.findViewById(R.id.no);
			no.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();

			break;
		case R.id.delete_ongoing:
			dialog = new Dialog(this);
			finalview = view;
			dialog.setContentView(R.layout.dialog_confirmation);
			dialog.setTitle("Delete Match");

			dialogText = (TextView) dialog.findViewById(R.id.dialog_text);
			dialogText.setText("Are you sure you want to Delete this Match?");

			yes = (Button) dialog.findViewById(R.id.yes);
			yes.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					OngoingMatchesFragment.ongoingMatchesFragment
							.deleteMatch(finalview);
					dialog.dismiss();
				}
			});

			no = (Button) dialog.findViewById(R.id.no);
			no.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
			break;
		case R.id.delete_diary:
			dialog = new Dialog(this);
			finalview = view;
			dialog.setContentView(R.layout.dialog_confirmation);
			dialog.setTitle("Delete Match");

			dialogText = (TextView) dialog.findViewById(R.id.dialog_text);
			dialogText
					.setText("Are you sure you want to Delete this Match from your Career?");

			yes = (Button) dialog.findViewById(R.id.yes);
			yes.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DiaryMatchesFragment.diaryMatchesFragment
							.deleteMatch(finalview);
					dialog.dismiss();
				}
			});

			no = (Button) dialog.findViewById(R.id.no);
			no.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
			break;
		default:
			break;
		}
	}

	public void showDatePicker(int view_callee) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// Create and show the dialog.
		DatePickerFragment.datePickerFragment = new DatePickerFragment();
		DatePickerFragment.datePickerFragment.setView_callee(view_callee);
		DatePickerFragment.datePickerFragment
				.setDate_str(((TextView) findViewById(view_callee)).getText()
						.toString());
		DatePickerFragment.datePickerFragment.show(ft, null);
	}
}