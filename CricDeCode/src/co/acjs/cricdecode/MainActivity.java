package co.acjs.cricdecode;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
	public boolean onCreateOptionsMenu(Menu menu) {
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
		case PERFORMANCE_FRAGMENT_EDIT:
			menu.add(Menu.NONE, R.string.save_performance, Menu.NONE,
					R.string.save_performance);
			menu.findItem(R.string.save_performance).setShowAsAction(
					MenuItem.SHOW_AS_ACTION_IF_ROOM);
			break;
		default:
			break;
		}
		current_menu = menu;
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
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
		switch (view.getId()) {
		case R.id.date_of_birth:
			showDatePicker(R.id.date_of_birth);
			break;
		case R.id.date_of_match:
			showDatePicker(R.id.date_of_match);
			break;
		case R.id.profile_picture:
			ProfileEditFragment.profileEditFragment.getProfilePicture();
			break;
		case R.id.add_to_career:
			OngoingMatchesFragment.ongoingMatchesFragment.addToCareer(view);
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