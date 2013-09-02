package co.acjs.cricdecode;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
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
	Spinner spinner;
	MenuListAdapter mMenuAdapter;
	String[] title;
	int currentFragment, preFragment;
	Menu current_menu;
	static SQLiteDatabase dbHandle;
	public static Context main_context;

	static ContentProviderClient client;

	// Declare Constants
	static final int NO_FRAGMENT = -1, PROFILE_FRAGMENT = 0,
			CAREER_FRAGMENT = 1, ANALYSIS_FRAGMENT = 2,
			DIARY_MATCHES_FRAGMENT = 3, ONGOING_MATCHES_FRAGMENT = 4,
			MATCH_CREATION_FRAGMENT = 5, PERFORMANCE_FRAGMENT_EDIT = 6,
			PERFORMANCE_FRAGMENT_VIEW = 7;

	static {
		Log.d("Debug", "Static Initializer");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_main);

		main_context = this;

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		ProfileData.mPrefs = getSharedPreferences("CricDeCode",
				Context.MODE_PRIVATE);
		ProfileData.mPrefs.getInt("height", 0);
		ProfileData.mPrefs.getInt("width", 0);
		if (ProfileData.mPrefs.getInt("height", 0) == 0) {
			ProfileData.setScr_Height(main_context, ProfileData.mPrefs.getInt(
					"height", displaymetrics.heightPixels));
		}

		if (ProfileData.mPrefs.getInt("width", 0) == 0) {
			ProfileData.setScr_Width(main_context, ProfileData.mPrefs.getInt(
					"width", displaymetrics.widthPixels));
		}

		Log.w("Width and Height", "Display: " + displaymetrics.heightPixels
				+ " " + displaymetrics.widthPixels);

		client = getContentResolver().acquireContentProviderClient(
				CricDeCodeContentProvider.AUTHORITY);

		dbHandle = ((CricDeCodeContentProvider) client
				.getLocalContentProvider()).getDbHelper().getReadableDatabase();

		make_directory();

		// Action Bar Customization
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.action_bar, null);
		actionBar.setCustomView(view);

		// Spinner
		spinner = (Spinner) findViewById(R.id.inning_no);

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

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			ImageView icon = (ImageView) findViewById(R.id.icon);

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				icon.setPadding(0, 0, 0, 0);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				icon.setPadding(-5, 0, 0, 0);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if(ProfileData.mPrefs.getInt("FirstTym", 0)==0)
		{
			//TODO Debug this Code
			mDrawerLayout.openDrawer(mDrawerList);
			ProfileData.setFirstTym(this, 1);
		}
		if (savedInstanceState == null) {
			Log.d("Debug", "Saved is null");
			currentFragment = CAREER_FRAGMENT;
			preFragment = NO_FRAGMENT;
			ProfileFragment.currentProfileFragment = ProfileFragment.PROFILE_VIEW_FRAGMENT;
			selectItem(currentFragment, true);
		} else {
			currentFragment = savedInstanceState.getInt("currentFragment");
			preFragment = savedInstanceState.getInt("preFragment");
			switch (currentFragment) {
			case PROFILE_FRAGMENT:
				spinner.setVisibility(View.GONE);
				ProfileFragment.profileFragment = (ProfileFragment) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case CAREER_FRAGMENT:
				spinner.setVisibility(View.GONE);
				CareerFragment.careerFragment = (CareerFragment) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case ANALYSIS_FRAGMENT:
				spinner.setVisibility(View.GONE);
				AnalysisFragment.analysisFragment = (AnalysisFragment) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case MATCH_CREATION_FRAGMENT:
				spinner.setVisibility(View.GONE);
				MatchCreationFragment.matchCreationFragment = (MatchCreationFragment) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case DIARY_MATCHES_FRAGMENT:
				spinner.setVisibility(View.GONE);
				DiaryMatchesFragment.diaryMatchesFragment = (DiaryMatchesFragment) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case ONGOING_MATCHES_FRAGMENT:
				spinner.setVisibility(View.GONE);
				OngoingMatchesFragment.ongoingMatchesFragment = (OngoingMatchesFragment) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case PERFORMANCE_FRAGMENT_EDIT:
				spinner.setVisibility(View.VISIBLE);
				PerformanceFragmentEdit.performanceFragmentEdit = (PerformanceFragmentEdit) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case PERFORMANCE_FRAGMENT_VIEW:
				spinner.setVisibility(View.VISIBLE);
				PerformanceFragmentView.performanceFragmentView = (PerformanceFragmentView) getSupportFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			default:
				break;
			}
			Log.d("Debug", "currentFragment " + currentFragment);
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
	protected void onStop() {
		super.onStop();

		// Google Analytics Stop
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		switch (currentFragment) {
		case PROFILE_FRAGMENT:
			if (ProfileFragment.currentProfileFragment == ProfileFragment.PROFILE_VIEW_FRAGMENT) {
				// menu.add(Menu.NONE, R.string.edit_profile, Menu.NONE,
				// R.string.edit_profile);
				// menu.findItem(R.string.edit_profile).setShowAsAction(
				// MenuItem.SHOW_AS_ACTION_IF_ROOM);
				ImageButton button = (ImageButton) getSupportActionBar()
						.getCustomView().findViewById(R.id.button_menu);
				button.setImageDrawable(getResources().getDrawable(
						R.drawable.edit));
				button.setContentDescription(getResources().getString(
						R.string.edit_profile));
				// button.setText(getResources().getString(R.string.edit_profile));

				((RelativeLayout) findViewById(R.id.rl_button))
						.setVisibility(View.VISIBLE);

			} else {
				/*
				 * menu.add(Menu.NONE, R.string.save_profile, Menu.NONE,
				 * R.string.save_profile);
				 * menu.findItem(R.string.save_profile).setShowAsAction(
				 * MenuItem.SHOW_AS_ACTION_IF_ROOM);
				 */
				ImageButton button = (ImageButton) getSupportActionBar()
						.getCustomView().findViewById(R.id.button_menu);
				button.setImageDrawable(getResources().getDrawable(
						R.drawable.save));
				button.setContentDescription(getResources().getString(
						R.string.save_profile));
				// button.setText(getResources().getString(R.string.save_profile));
				((RelativeLayout) findViewById(R.id.rl_button))
						.setVisibility(View.VISIBLE);
			}
			break;
		case MATCH_CREATION_FRAGMENT:
			/*
			 * menu.add(Menu.NONE, R.string.create_match, Menu.NONE,
			 * R.string.create_match);
			 * menu.findItem(R.string.create_match).setShowAsAction(
			 * MenuItem.SHOW_AS_ACTION_ALWAYS);
			 */
			ImageButton button = (ImageButton) getSupportActionBar()
					.getCustomView().findViewById(R.id.button_menu);
			button.setImageDrawable(getResources().getDrawable(R.drawable.save));
			button.setContentDescription(getResources().getString(
					R.string.create_match));
			// button.setText(getResources().getString(R.string.create_match));
			((RelativeLayout) findViewById(R.id.rl_button))
					.setVisibility(View.VISIBLE);
			break;
		case ONGOING_MATCHES_FRAGMENT:
			/*
			 * menu.add(Menu.NONE, R.string.new_match, Menu.NONE,
			 * R.string.new_match);
			 * menu.findItem(R.string.new_match).setShowAsAction(
			 * MenuItem.SHOW_AS_ACTION_ALWAYS);
			 */
			button = (ImageButton) getSupportActionBar().getCustomView()
					.findViewById(R.id.button_menu);
			button.setImageDrawable(getResources().getDrawable(R.drawable.add));
			button.setContentDescription(getResources().getString(
					R.string.new_match));
			// button.setText(getResources().getString(R.string.new_match));
			((RelativeLayout) findViewById(R.id.rl_button))
					.setVisibility(View.VISIBLE);
			break;
		case DIARY_MATCHES_FRAGMENT:
		case CAREER_FRAGMENT:
			/*
			 * menu.add(Menu.NONE, R.string.filter, Menu.NONE, R.string.filter);
			 * menu.findItem(R.string.filter).setShowAsAction(
			 * MenuItem.SHOW_AS_ACTION_IF_ROOM);
			 */
			button = (ImageButton) getSupportActionBar().getCustomView()
					.findViewById(R.id.button_menu);
			button.setImageDrawable(getResources().getDrawable(
					R.drawable.filter));
			button.setContentDescription(getResources().getString(
					R.string.filter));
			// button.setText(getResources().getString(R.string.filter));
			((RelativeLayout) findViewById(R.id.rl_button))
					.setVisibility(View.VISIBLE);
			break;
		case PERFORMANCE_FRAGMENT_EDIT:
			/*
			 * menu.add(Menu.NONE, R.string.save_performance, Menu.NONE,
			 * R.string.save_performance);
			 * menu.findItem(R.string.save_performance).setShowAsAction(
			 * MenuItem.SHOW_AS_ACTION_ALWAYS);
			 */
			button = (ImageButton) getSupportActionBar().getCustomView()
					.findViewById(R.id.button_menu);
			button.setContentDescription(getResources().getString(
					R.string.save_performance));
			button.setImageDrawable(getResources().getDrawable(R.drawable.save));
			// button.setText(getResources().getString(R.string.save_performance));
			((RelativeLayout) findViewById(R.id.rl_button))
					.setVisibility(View.VISIBLE);
			break;
		default:
			((RelativeLayout) findViewById(R.id.rl_button))
					.setVisibility(View.GONE);
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
		/*
		 * case R.string.new_match: preFragment = currentFragment;
		 * currentFragment = MATCH_CREATION_FRAGMENT;
		 * selectItem(MATCH_CREATION_FRAGMENT, true);
		 * onPrepareOptionsMenu(current_menu); break; case
		 * R.string.edit_profile: ProfileFragment.currentProfileFragment =
		 * ProfileFragment.PROFILE_EDIT_FRAGMENT;
		 * onPrepareOptionsMenu(current_menu);
		 * ProfileFragment.profileFragment.viewFragment(); break; case
		 * R.string.save_profile:
		 * ProfileEditFragment.profileEditFragment.saveEditedProfile();
		 * ProfileFragment.currentProfileFragment =
		 * ProfileFragment.PROFILE_VIEW_FRAGMENT;
		 * onPrepareOptionsMenu(current_menu);
		 * ProfileFragment.profileFragment.viewFragment(); break; case
		 * R.string.create_match:
		 * MatchCreationFragment.matchCreationFragment.insertMatch();
		 * onPrepareOptionsMenu(current_menu); break; case
		 * R.string.save_performance:
		 * PerformanceFragmentEdit.performanceFragmentEdit.insertOrUpdate();
		 * onPrepareOptionsMenu(current_menu); break; case R.string.filter:
		 * showFilterDialog(currentFragment); break;
		 */
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
				preFragment = currentFragment;
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
			spinner.setVisibility(View.GONE);
			Log.d("Debug", "Select Profile");
			if (newInstance) {
				ft.replace(R.id.content_frame, new ProfileFragment());
			} else {
				ft.replace(R.id.content_frame, ProfileFragment.profileFragment);
			}
			break;
		case CAREER_FRAGMENT:
			spinner.setVisibility(View.GONE);
			if (newInstance) {
				ft.replace(R.id.content_frame, new CareerFragment());
			} else {
				ft.replace(R.id.content_frame, CareerFragment.careerFragment);
			}
			break;
		case ANALYSIS_FRAGMENT:
			spinner.setVisibility(View.GONE);
			if (newInstance) {
				ft.replace(R.id.content_frame, new AnalysisFragment());
			} else {
				ft.replace(R.id.content_frame,
						AnalysisFragment.analysisFragment);
			}
			break;
		case ONGOING_MATCHES_FRAGMENT:
			spinner.setVisibility(View.GONE);
			if (newInstance) {
				ft.replace(R.id.content_frame, new OngoingMatchesFragment());
			} else {
				ft.replace(R.id.content_frame,
						OngoingMatchesFragment.ongoingMatchesFragment);
			}
			break;
		case DIARY_MATCHES_FRAGMENT:
			spinner.setVisibility(View.GONE);
			if (newInstance) {
				ft.replace(R.id.content_frame, new DiaryMatchesFragment());
			} else {
				ft.replace(R.id.content_frame,
						DiaryMatchesFragment.diaryMatchesFragment);
			}
			break;
		case MATCH_CREATION_FRAGMENT:
			spinner.setVisibility(View.GONE);
			if (newInstance) {
				ft.replace(R.id.content_frame, new MatchCreationFragment());
			} else {
				ft.replace(R.id.content_frame,
						MatchCreationFragment.matchCreationFragment);
			}
			break;
		case PERFORMANCE_FRAGMENT_EDIT:
			spinner.setVisibility(View.VISIBLE);
			if (newInstance) {
				ft.replace(R.id.content_frame, new PerformanceFragmentEdit());
			} else {
				ft.replace(R.id.content_frame,
						PerformanceFragmentEdit.performanceFragmentEdit);
			}
			break;
		case PERFORMANCE_FRAGMENT_VIEW:
			spinner.setVisibility(View.VISIBLE);
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
		outState.putInt("preFragment", preFragment);

		switch (currentFragment) {
		case PROFILE_FRAGMENT:
			getSupportFragmentManager().putFragment(outState,
					"currentFragmentInstance", ProfileFragment.profileFragment);
			break;
		case CAREER_FRAGMENT:
			getSupportFragmentManager().putFragment(outState,
					"currentFragmentInstance", CareerFragment.careerFragment);
			break;
		case ANALYSIS_FRAGMENT:
			getSupportFragmentManager().putFragment(outState,
					"currentFragmentInstance",
					AnalysisFragment.analysisFragment);
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
	protected void onStart() {
		super.onStart();

		// Google Analytics Start
		EasyTracker.getInstance().activityStart(this);
	}

	public static void changeTabLayout(int x) {
		switch (x) {
		case 0:
			if (android.os.Build.VERSION.SDK_INT < 16) {
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.gen_tab))
						.setBackgroundDrawable(main_context.getResources()
								.getDrawable(R.drawable.tab_selected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bat_tab))
						.setBackgroundDrawable(main_context
								.getResources()
								.getDrawable(R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bowl_tab))
						.setBackgroundDrawable(main_context
								.getResources()
								.getDrawable(R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.field_tab))
						.setBackgroundDrawable(main_context
								.getResources()
								.getDrawable(R.drawable.tab_unselected_selector));
			} else {
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.gen_tab)).setBackground(main_context
						.getResources().getDrawable(
								R.drawable.tab_selected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bat_tab)).setBackground(main_context
						.getResources().getDrawable(
								R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bowl_tab))
						.setBackground(main_context.getResources().getDrawable(
								R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.field_tab))
						.setBackground(main_context.getResources().getDrawable(
								R.drawable.tab_unselected_selector));

			}
			break;
		case 1:

			if (android.os.Build.VERSION.SDK_INT < 16) {
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.gen_tab))
						.setBackgroundDrawable(main_context
								.getResources()
								.getDrawable(R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bat_tab))
						.setBackgroundDrawable(main_context.getResources()
								.getDrawable(R.drawable.tab_selected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bowl_tab))
						.setBackgroundDrawable(main_context
								.getResources()
								.getDrawable(R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.field_tab))
						.setBackgroundDrawable(main_context
								.getResources()
								.getDrawable(R.drawable.tab_unselected_selector));
			} else {
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.gen_tab)).setBackground(main_context
						.getResources().getDrawable(
								R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bat_tab)).setBackground(main_context
						.getResources().getDrawable(
								R.drawable.tab_selected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bowl_tab))
						.setBackground(main_context.getResources().getDrawable(
								R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.field_tab))
						.setBackground(main_context.getResources().getDrawable(
								R.drawable.tab_unselected_selector));
			}

			break;
		case 2:

			if (android.os.Build.VERSION.SDK_INT < 16) {
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.gen_tab))
						.setBackgroundDrawable(main_context
								.getResources()
								.getDrawable(R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bat_tab))
						.setBackgroundDrawable(main_context
								.getResources()
								.getDrawable(R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bowl_tab))
						.setBackgroundDrawable(main_context.getResources()
								.getDrawable(R.drawable.tab_selected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.field_tab))
						.setBackgroundDrawable(main_context
								.getResources()
								.getDrawable(R.drawable.tab_unselected_selector));
			} else {
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.gen_tab)).setBackground(main_context
						.getResources().getDrawable(
								R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bat_tab)).setBackground(main_context
						.getResources().getDrawable(
								R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bowl_tab))
						.setBackground(main_context.getResources().getDrawable(
								R.drawable.tab_selected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.field_tab))
						.setBackground(main_context.getResources().getDrawable(
								R.drawable.tab_unselected_selector));

			}
			break;
		case 3:
			if (android.os.Build.VERSION.SDK_INT < 16) {
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.gen_tab))
						.setBackgroundDrawable(main_context
								.getResources()
								.getDrawable(R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bat_tab))
						.setBackgroundDrawable(main_context
								.getResources()
								.getDrawable(R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bowl_tab))
						.setBackgroundDrawable(main_context
								.getResources()
								.getDrawable(R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.field_tab))
						.setBackgroundDrawable(main_context.getResources()
								.getDrawable(R.drawable.tab_selected_selector));
			} else {
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.gen_tab)).setBackground(main_context
						.getResources().getDrawable(
								R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bat_tab)).setBackground(main_context
						.getResources().getDrawable(
								R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.bowl_tab))
						.setBackground(main_context.getResources().getDrawable(
								R.drawable.tab_unselected_selector));
				((RelativeLayout) ((SherlockFragmentActivity) MainActivity.main_context)
						.findViewById(R.id.field_tab))
						.setBackground(main_context.getResources().getDrawable(
								R.drawable.tab_selected_selector));
			}
			break;
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void onClick(View view) {
		final Dialog dialog;
		final View finalview;

		switch (view.getId()) {
		case R.id.gen_tab:
			switch (currentFragment) {
			case CAREER_FRAGMENT:
				CareerFragment.careerFragment.mViewPager
						.setCurrentItem(PerformanceFragmentEdit.GENERAL);
				break;
			case PERFORMANCE_FRAGMENT_EDIT:
				PerformanceFragmentEdit.performanceFragmentEdit.mViewPager
						.setCurrentItem(PerformanceFragmentEdit.GENERAL);
				break;
			case PERFORMANCE_FRAGMENT_VIEW:
				PerformanceFragmentView.performanceFragmentView.mViewPager
						.setCurrentItem(PerformanceFragmentEdit.GENERAL);
				break;
			default:
				break;
			}

			break;
		case R.id.bat_tab:
			switch (currentFragment) {
			case CAREER_FRAGMENT:
				CareerFragment.careerFragment.mViewPager
						.setCurrentItem(PerformanceFragmentEdit.BATTING);
				break;
			case PERFORMANCE_FRAGMENT_EDIT:
				PerformanceFragmentEdit.performanceFragmentEdit.mViewPager
						.setCurrentItem(PerformanceFragmentEdit.BATTING);
				break;
			case PERFORMANCE_FRAGMENT_VIEW:
				PerformanceFragmentView.performanceFragmentView.mViewPager
						.setCurrentItem(PerformanceFragmentEdit.BATTING);
				break;
			default:
				break;
			}

			break;
		case R.id.bowl_tab:
			switch (currentFragment) {
			case CAREER_FRAGMENT:
				CareerFragment.careerFragment.mViewPager
						.setCurrentItem(PerformanceFragmentEdit.BOWLING);
				break;
			case PERFORMANCE_FRAGMENT_EDIT:
				PerformanceFragmentEdit.performanceFragmentEdit.mViewPager
						.setCurrentItem(PerformanceFragmentEdit.BOWLING);
				break;
			case PERFORMANCE_FRAGMENT_VIEW:
				PerformanceFragmentView.performanceFragmentView.mViewPager
						.setCurrentItem(PerformanceFragmentEdit.BOWLING);
				break;
			default:
				break;
			}

			break;

		case R.id.field_tab:
			switch (currentFragment) {
			case CAREER_FRAGMENT:
				CareerFragment.careerFragment.mViewPager
						.setCurrentItem(PerformanceFragmentEdit.FIELDING);
				break;
			case PERFORMANCE_FRAGMENT_EDIT:
				PerformanceFragmentEdit.performanceFragmentEdit.mViewPager
						.setCurrentItem(PerformanceFragmentEdit.FIELDING);
				break;
			case PERFORMANCE_FRAGMENT_VIEW:
				PerformanceFragmentView.performanceFragmentView.mViewPager
						.setCurrentItem(PerformanceFragmentEdit.FIELDING);
				break;
			default:
				break;
			}

			break;
		case R.id.filter:
			showFilterDialog(currentFragment);
			break;
		case R.id.generate_graph:

			if (AnalysisFragment.type_sel == 0) {
				AnalysisFragment.analysisFragment.generateXYGraph();
			} else {
				AnalysisFragment.analysisFragment.generatePieGraph();
			}
			break;
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

	public void showFilterDialog(int id) {
		// custom dialog
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.filter_general);
		dialog.setTitle("Filter");
		final MultiSelectSpinner season_spinner, my_team_spinner, opponent_spinner, venue_spinner, result_spinner, level_spinner, overs_spinner, innings_spinner, duration_spinner, first_spinner, batting_no_spinner, how_out_spinner;
		switch (id) {
		case DIARY_MATCHES_FRAGMENT:
			dialog.findViewById(R.id.lbl_batting_no_list).setVisibility(
					View.GONE);
			dialog.findViewById(R.id.batting_no_list).setVisibility(View.GONE);
			dialog.findViewById(R.id.lbl_how_out_list).setVisibility(View.GONE);
			dialog.findViewById(R.id.how_out_list).setVisibility(View.GONE);
			season_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.season_list);
			season_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.season_list);
			season_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.season_list_selected);
			season_spinner._proxyAdapter.clear();
			season_spinner._proxyAdapter.add(season_spinner
					.buildSelectedItemString());
			season_spinner.setSelection(0);

			my_team_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.my_team_list);
			my_team_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.my_team_list);
			my_team_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.my_team_list_selected);
			my_team_spinner._proxyAdapter.clear();
			my_team_spinner._proxyAdapter.add(my_team_spinner
					.buildSelectedItemString());
			my_team_spinner.setSelection(0);

			opponent_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.opponent_list);
			opponent_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.opponent_list);
			opponent_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.opponent_list_selected);
			opponent_spinner._proxyAdapter.clear();
			opponent_spinner._proxyAdapter.add(opponent_spinner
					.buildSelectedItemString());
			opponent_spinner.setSelection(0);

			venue_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.venue_list);
			venue_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.venue_list);
			venue_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.venue_list_selected);
			venue_spinner._proxyAdapter.clear();
			venue_spinner._proxyAdapter.add(venue_spinner
					.buildSelectedItemString());
			venue_spinner.setSelection(0);

			result_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.result_list);
			result_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.result_list);
			result_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.result_list_selected);
			result_spinner._proxyAdapter.clear();
			result_spinner._proxyAdapter.add(result_spinner
					.buildSelectedItemString());
			result_spinner.setSelection(0);

			level_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.level_list);
			level_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.level_list);
			level_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.level_list_selected);
			level_spinner._proxyAdapter.clear();
			level_spinner._proxyAdapter.add(level_spinner
					.buildSelectedItemString());
			level_spinner.setSelection(0);

			overs_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.overs_list);
			overs_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.overs_list);
			overs_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.overs_list_selected);
			overs_spinner._proxyAdapter.clear();
			overs_spinner._proxyAdapter.add(overs_spinner
					.buildSelectedItemString());
			overs_spinner.setSelection(0);

			innings_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.innings_list);
			innings_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.innings_list);
			innings_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.innings_list_selected);
			innings_spinner._proxyAdapter.clear();
			innings_spinner._proxyAdapter.add(innings_spinner
					.buildSelectedItemString());
			innings_spinner.setSelection(0);

			duration_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.duration_list);
			duration_spinner
					.setItems(DiaryMatchesFragment.diaryMatchesFragment.duration_list);
			duration_spinner
					.setSelection(DiaryMatchesFragment.diaryMatchesFragment.duration_list_selected);
			duration_spinner._proxyAdapter.clear();
			duration_spinner._proxyAdapter.add(duration_spinner
					.buildSelectedItemString());
			duration_spinner.setSelection(0);

			first_spinner = (MultiSelectSpinner) dialog
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
			dialogButton.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					v.setBackgroundColor(getResources().getColor(
							R.color.light_red));
					return false;
				}
			});
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					DiaryMatchesFragment.diaryMatchesFragment.season_list_selected = season_spinner
							.getSelectedStrings();
					String str = DiaryMatchesFragment
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
					str = DiaryMatchesFragment
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
					str = DiaryMatchesFragment
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
					str = DiaryMatchesFragment
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
					str = DiaryMatchesFragment
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
					str = DiaryMatchesFragment
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
					str = DiaryMatchesFragment
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
					str = DiaryMatchesFragment
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
					str = DiaryMatchesFragment
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
					str = DiaryMatchesFragment
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
					Toast.makeText(MainActivity.main_context, "Filter Set",
							Toast.LENGTH_LONG).show();
					dialog.dismiss();

					v.setBackgroundColor(getResources().getColor(
							R.color.dark_red));

				}
			});
			break;
		case CAREER_FRAGMENT:

			batting_no_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.batting_no_list);
			batting_no_spinner
					.setItems(CareerFragment.careerFragment.batting_no_list);
			batting_no_spinner
					.setSelection(CareerFragment.careerFragment.batting_no_list_selected);
			batting_no_spinner._proxyAdapter.clear();
			batting_no_spinner._proxyAdapter.add(batting_no_spinner
					.buildSelectedItemString());
			batting_no_spinner.setSelection(0);

			how_out_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.how_out_list);
			how_out_spinner
					.setItems(CareerFragment.careerFragment.how_out_list);
			how_out_spinner
					.setSelection(CareerFragment.careerFragment.how_out_list_selected);
			how_out_spinner._proxyAdapter.clear();
			how_out_spinner._proxyAdapter.add(how_out_spinner
					.buildSelectedItemString());
			how_out_spinner.setSelection(0);

			season_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.season_list);
			season_spinner.setItems(CareerFragment.careerFragment.season_list);
			season_spinner
					.setSelection(CareerFragment.careerFragment.season_list_selected);
			season_spinner._proxyAdapter.clear();
			season_spinner._proxyAdapter.add(season_spinner
					.buildSelectedItemString());
			season_spinner.setSelection(0);

			my_team_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.my_team_list);
			my_team_spinner
					.setItems(CareerFragment.careerFragment.my_team_list);
			my_team_spinner
					.setSelection(CareerFragment.careerFragment.my_team_list_selected);
			my_team_spinner._proxyAdapter.clear();
			my_team_spinner._proxyAdapter.add(my_team_spinner
					.buildSelectedItemString());
			my_team_spinner.setSelection(0);

			opponent_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.opponent_list);
			opponent_spinner
					.setItems(CareerFragment.careerFragment.opponent_list);
			opponent_spinner
					.setSelection(CareerFragment.careerFragment.opponent_list_selected);
			opponent_spinner._proxyAdapter.clear();
			opponent_spinner._proxyAdapter.add(opponent_spinner
					.buildSelectedItemString());
			opponent_spinner.setSelection(0);

			venue_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.venue_list);
			venue_spinner.setItems(CareerFragment.careerFragment.venue_list);
			venue_spinner
					.setSelection(CareerFragment.careerFragment.venue_list_selected);
			venue_spinner._proxyAdapter.clear();
			venue_spinner._proxyAdapter.add(venue_spinner
					.buildSelectedItemString());
			venue_spinner.setSelection(0);

			result_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.result_list);
			result_spinner.setItems(CareerFragment.careerFragment.result_list);
			result_spinner
					.setSelection(CareerFragment.careerFragment.result_list_selected);
			result_spinner._proxyAdapter.clear();
			result_spinner._proxyAdapter.add(result_spinner
					.buildSelectedItemString());
			result_spinner.setSelection(0);

			level_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.level_list);
			level_spinner.setItems(CareerFragment.careerFragment.level_list);
			level_spinner
					.setSelection(CareerFragment.careerFragment.level_list_selected);
			level_spinner._proxyAdapter.clear();
			level_spinner._proxyAdapter.add(level_spinner
					.buildSelectedItemString());
			level_spinner.setSelection(0);

			overs_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.overs_list);
			overs_spinner.setItems(CareerFragment.careerFragment.overs_list);
			overs_spinner
					.setSelection(CareerFragment.careerFragment.overs_list_selected);
			overs_spinner._proxyAdapter.clear();
			overs_spinner._proxyAdapter.add(overs_spinner
					.buildSelectedItemString());
			overs_spinner.setSelection(0);

			innings_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.innings_list);
			innings_spinner
					.setItems(CareerFragment.careerFragment.innings_list);
			innings_spinner
					.setSelection(CareerFragment.careerFragment.innings_list_selected);
			innings_spinner._proxyAdapter.clear();
			innings_spinner._proxyAdapter.add(innings_spinner
					.buildSelectedItemString());
			innings_spinner.setSelection(0);

			duration_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.duration_list);
			duration_spinner
					.setItems(CareerFragment.careerFragment.duration_list);
			duration_spinner
					.setSelection(CareerFragment.careerFragment.duration_list_selected);
			duration_spinner._proxyAdapter.clear();
			duration_spinner._proxyAdapter.add(duration_spinner
					.buildSelectedItemString());
			duration_spinner.setSelection(0);

			first_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.first_list);
			first_spinner.setItems(CareerFragment.careerFragment.first_list);
			first_spinner
					.setSelection(CareerFragment.careerFragment.first_list_selected);
			first_spinner._proxyAdapter.clear();
			first_spinner._proxyAdapter.add(first_spinner
					.buildSelectedItemString());
			first_spinner.setSelection(0);

			dialogButton = (Button) dialog.findViewById(R.id.okay);
			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					CareerFragment.careerFragment.batting_no_list_selected = batting_no_spinner
							.getSelectedStrings();
					String str = DiaryMatchesFragment
							.buildSelectedItemString(
									CareerFragment.careerFragment.batting_no_list_selected,
									false);
					if (!str.equals("")) {
						CareerFragment.careerFragment.batting_no_whereClause = " and p."
								+ PerformanceDb.KEY_BAT_NUM
								+ " in("
								+ str
								+ ")";
					} else {
						CareerFragment.careerFragment.batting_no_whereClause = " and p."
								+ PerformanceDb.KEY_BAT_NUM + " in('')";
					}

					CareerFragment.careerFragment.how_out_list_selected = how_out_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									CareerFragment.careerFragment.how_out_list_selected,
									false);
					if (!str.equals("")) {
						CareerFragment.careerFragment.how_out_whereClause = " and p."
								+ PerformanceDb.KEY_BAT_HOW_OUT
								+ " in("
								+ str
								+ ")";
					} else {
						CareerFragment.careerFragment.how_out_whereClause = " and p."
								+ PerformanceDb.KEY_BAT_HOW_OUT + " in('')";
					}

					CareerFragment.careerFragment.season_list_selected = season_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.buildSelectedItemString(
							CareerFragment.careerFragment.season_list_selected,
							false);
					if (!str.equals("")) {
						CareerFragment.careerFragment.season_whereClause = " and strftime('%Y',m."
								+ MatchDb.KEY_MATCH_DATE + ") in(" + str + ")";
					} else {
						CareerFragment.careerFragment.season_whereClause = " and strftime('%Y',m."
								+ MatchDb.KEY_MATCH_DATE + ") in('')";
					}

					CareerFragment.careerFragment.my_team_list_selected = my_team_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									CareerFragment.careerFragment.my_team_list_selected,
									false);
					if (!str.equals("")) {
						CareerFragment.careerFragment.myteam_whereClause = " and m."
								+ MatchDb.KEY_MY_TEAM + " in(" + str + ")";
					} else {
						CareerFragment.careerFragment.myteam_whereClause = " and m."
								+ MatchDb.KEY_MY_TEAM + " in('')";
					}

					CareerFragment.careerFragment.opponent_list_selected = opponent_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									CareerFragment.careerFragment.opponent_list_selected,
									false);
					if (!str.equals("")) {
						CareerFragment.careerFragment.opponent_whereClause = " and m."
								+ MatchDb.KEY_OPPONENT_TEAM
								+ " in("
								+ str
								+ ")";
					} else {
						CareerFragment.careerFragment.opponent_whereClause = " and m."
								+ MatchDb.KEY_OPPONENT_TEAM + " in('')";
					}

					CareerFragment.careerFragment.venue_list_selected = venue_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.buildSelectedItemString(
							CareerFragment.careerFragment.venue_list_selected,
							false);
					if (!str.equals("")) {
						CareerFragment.careerFragment.venue_whereClause = " and m."
								+ MatchDb.KEY_VENUE + " in(" + str + ")";
					} else {
						CareerFragment.careerFragment.venue_whereClause = " and m."
								+ MatchDb.KEY_VENUE + " in('')";
					}

					CareerFragment.careerFragment.result_list_selected = result_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.buildSelectedItemString(
							CareerFragment.careerFragment.result_list_selected,
							false);
					if (!str.equals("")) {
						CareerFragment.careerFragment.result_whereClause = " and m."
								+ MatchDb.KEY_RESULT + " in(" + str + ")";
					} else {
						CareerFragment.careerFragment.result_whereClause = " and m."
								+ MatchDb.KEY_RESULT + " in('')";
					}

					CareerFragment.careerFragment.level_list_selected = level_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.buildSelectedItemString(
							CareerFragment.careerFragment.level_list_selected,
							false);
					if (!str.equals("")) {
						CareerFragment.careerFragment.level_whereClause = " and m."
								+ MatchDb.KEY_LEVEL + " in(" + str + ")";
					} else {
						CareerFragment.careerFragment.level_whereClause = " and m."
								+ MatchDb.KEY_LEVEL + " in('')";
					}

					CareerFragment.careerFragment.overs_list_selected = overs_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.buildSelectedItemString(
							CareerFragment.careerFragment.overs_list_selected,
							true);
					if (!str.equals("")) {
						CareerFragment.careerFragment.overs_whereClause = " and m."
								+ MatchDb.KEY_OVERS + " in(" + str + ")";
					} else {
						CareerFragment.careerFragment.overs_whereClause = " and m."
								+ MatchDb.KEY_OVERS + " in(-2)";
					}

					CareerFragment.careerFragment.innings_list_selected = innings_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									CareerFragment.careerFragment.innings_list_selected,
									true);
					if (!str.equals("")) {
						CareerFragment.careerFragment.innings_whereClause = " and m."
								+ MatchDb.KEY_INNINGS + " in(" + str + ")";
					} else {
						CareerFragment.careerFragment.innings_whereClause = " and m."
								+ MatchDb.KEY_INNINGS + " in(-2)";
					}

					CareerFragment.careerFragment.duration_list_selected = duration_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									CareerFragment.careerFragment.duration_list_selected,
									false);
					if (!str.equals("")) {
						CareerFragment.careerFragment.duration_whereClause = " and m."
								+ MatchDb.KEY_DURATION + " in(" + str + ")";
					} else {
						CareerFragment.careerFragment.duration_whereClause = " and m."
								+ MatchDb.KEY_DURATION + " in('')";
					}

					CareerFragment.careerFragment.first_list_selected = first_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment.buildSelectedItemString(
							CareerFragment.careerFragment.first_list_selected,
							false);
					if (!str.equals("")) {
						CareerFragment.careerFragment.first_whereClause = " and m."
								+ MatchDb.KEY_FIRST_ACTION + " in(" + str + ")";
					} else {
						CareerFragment.careerFragment.first_whereClause = " and m."
								+ MatchDb.KEY_FIRST_ACTION + " in('')";
					}

					// view Info
					CareerFragment.careerFragment.fireQueries();
					CareerFragment.careerFragment
							.viewInfo(CareerFragment.careerFragment.mViewPager
									.getCurrentItem());

					dialog.dismiss();
				}
			});
			break;
		case ANALYSIS_FRAGMENT:

			batting_no_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.batting_no_list);
			batting_no_spinner
					.setItems(AnalysisFragment.analysisFragment.batting_no_list);
			batting_no_spinner
					.setSelection(AnalysisFragment.analysisFragment.batting_no_list_selected);
			batting_no_spinner._proxyAdapter.clear();
			batting_no_spinner._proxyAdapter.add(batting_no_spinner
					.buildSelectedItemString());
			batting_no_spinner.setSelection(0);

			how_out_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.how_out_list);
			how_out_spinner
					.setItems(AnalysisFragment.analysisFragment.how_out_list);
			how_out_spinner
					.setSelection(AnalysisFragment.analysisFragment.how_out_list_selected);
			how_out_spinner._proxyAdapter.clear();
			how_out_spinner._proxyAdapter.add(how_out_spinner
					.buildSelectedItemString());
			how_out_spinner.setSelection(0);

			season_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.season_list);
			season_spinner
					.setItems(AnalysisFragment.analysisFragment.season_list);
			season_spinner
					.setSelection(AnalysisFragment.analysisFragment.season_list_selected);
			season_spinner._proxyAdapter.clear();
			season_spinner._proxyAdapter.add(season_spinner
					.buildSelectedItemString());
			season_spinner.setSelection(0);

			my_team_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.my_team_list);
			my_team_spinner
					.setItems(AnalysisFragment.analysisFragment.my_team_list);
			my_team_spinner
					.setSelection(AnalysisFragment.analysisFragment.my_team_list_selected);
			my_team_spinner._proxyAdapter.clear();
			my_team_spinner._proxyAdapter.add(my_team_spinner
					.buildSelectedItemString());
			my_team_spinner.setSelection(0);

			opponent_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.opponent_list);
			opponent_spinner
					.setItems(AnalysisFragment.analysisFragment.opponent_list);
			opponent_spinner
					.setSelection(AnalysisFragment.analysisFragment.opponent_list_selected);
			opponent_spinner._proxyAdapter.clear();
			opponent_spinner._proxyAdapter.add(opponent_spinner
					.buildSelectedItemString());
			opponent_spinner.setSelection(0);

			venue_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.venue_list);
			venue_spinner
					.setItems(AnalysisFragment.analysisFragment.venue_list);
			venue_spinner
					.setSelection(AnalysisFragment.analysisFragment.venue_list_selected);
			venue_spinner._proxyAdapter.clear();
			venue_spinner._proxyAdapter.add(venue_spinner
					.buildSelectedItemString());
			venue_spinner.setSelection(0);

			result_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.result_list);
			result_spinner
					.setItems(AnalysisFragment.analysisFragment.result_list);
			result_spinner
					.setSelection(AnalysisFragment.analysisFragment.result_list_selected);
			result_spinner._proxyAdapter.clear();
			result_spinner._proxyAdapter.add(result_spinner
					.buildSelectedItemString());
			result_spinner.setSelection(0);

			level_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.level_list);
			level_spinner
					.setItems(AnalysisFragment.analysisFragment.level_list);
			level_spinner
					.setSelection(AnalysisFragment.analysisFragment.level_list_selected);
			level_spinner._proxyAdapter.clear();
			level_spinner._proxyAdapter.add(level_spinner
					.buildSelectedItemString());
			level_spinner.setSelection(0);

			overs_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.overs_list);
			overs_spinner
					.setItems(AnalysisFragment.analysisFragment.overs_list);
			overs_spinner
					.setSelection(AnalysisFragment.analysisFragment.overs_list_selected);
			overs_spinner._proxyAdapter.clear();
			overs_spinner._proxyAdapter.add(overs_spinner
					.buildSelectedItemString());
			overs_spinner.setSelection(0);

			innings_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.innings_list);
			innings_spinner
					.setItems(AnalysisFragment.analysisFragment.innings_list);
			innings_spinner
					.setSelection(AnalysisFragment.analysisFragment.innings_list_selected);
			innings_spinner._proxyAdapter.clear();
			innings_spinner._proxyAdapter.add(innings_spinner
					.buildSelectedItemString());
			innings_spinner.setSelection(0);

			duration_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.duration_list);
			duration_spinner
					.setItems(AnalysisFragment.analysisFragment.duration_list);
			duration_spinner
					.setSelection(AnalysisFragment.analysisFragment.duration_list_selected);
			duration_spinner._proxyAdapter.clear();
			duration_spinner._proxyAdapter.add(duration_spinner
					.buildSelectedItemString());
			duration_spinner.setSelection(0);

			first_spinner = (MultiSelectSpinner) dialog
					.findViewById(R.id.first_list);
			first_spinner
					.setItems(AnalysisFragment.analysisFragment.first_list);
			first_spinner
					.setSelection(AnalysisFragment.analysisFragment.first_list_selected);
			first_spinner._proxyAdapter.clear();
			first_spinner._proxyAdapter.add(first_spinner
					.buildSelectedItemString());
			first_spinner.setSelection(0);

			dialogButton = (Button) dialog.findViewById(R.id.okay);
			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					AnalysisFragment.analysisFragment.batting_no_list_selected = batting_no_spinner
							.getSelectedStrings();
					String str = DiaryMatchesFragment
							.buildSelectedItemString(
									AnalysisFragment.analysisFragment.batting_no_list_selected,
									false);
					if (!str.equals("")) {
						AnalysisFragment.analysisFragment.batting_no_whereClause = " and p."
								+ PerformanceDb.KEY_BAT_NUM
								+ " in("
								+ str
								+ ")";
					} else {
						AnalysisFragment.analysisFragment.batting_no_whereClause = " and p."
								+ PerformanceDb.KEY_BAT_NUM + " in('')";
					}

					AnalysisFragment.analysisFragment.how_out_list_selected = how_out_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									AnalysisFragment.analysisFragment.how_out_list_selected,
									false);
					if (!str.equals("")) {
						AnalysisFragment.analysisFragment.how_out_whereClause = " and p."
								+ PerformanceDb.KEY_BAT_HOW_OUT
								+ " in("
								+ str
								+ ")";
					} else {
						AnalysisFragment.analysisFragment.how_out_whereClause = " and p."
								+ PerformanceDb.KEY_BAT_HOW_OUT + " in('')";
					}

					AnalysisFragment.analysisFragment.season_list_selected = season_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									AnalysisFragment.analysisFragment.season_list_selected,
									false);
					if (!str.equals("")) {
						AnalysisFragment.analysisFragment.season_whereClause = " and strftime('%Y',m."
								+ MatchDb.KEY_MATCH_DATE + ") in(" + str + ")";
					} else {
						AnalysisFragment.analysisFragment.season_whereClause = " and strftime('%Y',m."
								+ MatchDb.KEY_MATCH_DATE + ") in('')";
					}

					AnalysisFragment.analysisFragment.my_team_list_selected = my_team_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									AnalysisFragment.analysisFragment.my_team_list_selected,
									false);
					if (!str.equals("")) {
						AnalysisFragment.analysisFragment.myteam_whereClause = " and m."
								+ MatchDb.KEY_MY_TEAM + " in(" + str + ")";
					} else {
						AnalysisFragment.analysisFragment.myteam_whereClause = " and m."
								+ MatchDb.KEY_MY_TEAM + " in('')";
					}

					AnalysisFragment.analysisFragment.opponent_list_selected = opponent_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									AnalysisFragment.analysisFragment.opponent_list_selected,
									false);
					if (!str.equals("")) {
						AnalysisFragment.analysisFragment.opponent_whereClause = " and m."
								+ MatchDb.KEY_OPPONENT_TEAM
								+ " in("
								+ str
								+ ")";
					} else {
						AnalysisFragment.analysisFragment.opponent_whereClause = " and m."
								+ MatchDb.KEY_OPPONENT_TEAM + " in('')";
					}

					AnalysisFragment.analysisFragment.venue_list_selected = venue_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									AnalysisFragment.analysisFragment.venue_list_selected,
									false);
					if (!str.equals("")) {
						AnalysisFragment.analysisFragment.venue_whereClause = " and m."
								+ MatchDb.KEY_VENUE + " in(" + str + ")";
					} else {
						AnalysisFragment.analysisFragment.venue_whereClause = " and m."
								+ MatchDb.KEY_VENUE + " in('')";
					}

					AnalysisFragment.analysisFragment.result_list_selected = result_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									AnalysisFragment.analysisFragment.result_list_selected,
									false);
					if (!str.equals("")) {
						AnalysisFragment.analysisFragment.result_whereClause = " and m."
								+ MatchDb.KEY_RESULT + " in(" + str + ")";
					} else {
						AnalysisFragment.analysisFragment.result_whereClause = " and m."
								+ MatchDb.KEY_RESULT + " in('')";
					}

					AnalysisFragment.analysisFragment.level_list_selected = level_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									AnalysisFragment.analysisFragment.level_list_selected,
									false);
					if (!str.equals("")) {
						AnalysisFragment.analysisFragment.level_whereClause = " and m."
								+ MatchDb.KEY_LEVEL + " in(" + str + ")";
					} else {
						AnalysisFragment.analysisFragment.level_whereClause = " and m."
								+ MatchDb.KEY_LEVEL + " in('')";
					}

					AnalysisFragment.analysisFragment.overs_list_selected = overs_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									AnalysisFragment.analysisFragment.overs_list_selected,
									true);
					if (!str.equals("")) {
						AnalysisFragment.analysisFragment.overs_whereClause = " and m."
								+ MatchDb.KEY_OVERS + " in(" + str + ")";
					} else {
						AnalysisFragment.analysisFragment.overs_whereClause = " and m."
								+ MatchDb.KEY_OVERS + " in(-2)";
					}

					AnalysisFragment.analysisFragment.innings_list_selected = innings_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									AnalysisFragment.analysisFragment.innings_list_selected,
									true);
					if (!str.equals("")) {
						AnalysisFragment.analysisFragment.innings_whereClause = " and m."
								+ MatchDb.KEY_INNINGS + " in(" + str + ")";
					} else {
						AnalysisFragment.analysisFragment.innings_whereClause = " and m."
								+ MatchDb.KEY_INNINGS + " in(-2)";
					}

					AnalysisFragment.analysisFragment.duration_list_selected = duration_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									AnalysisFragment.analysisFragment.duration_list_selected,
									false);
					if (!str.equals("")) {
						AnalysisFragment.analysisFragment.duration_whereClause = " and m."
								+ MatchDb.KEY_DURATION + " in(" + str + ")";
					} else {
						AnalysisFragment.analysisFragment.duration_whereClause = " and m."
								+ MatchDb.KEY_DURATION + " in('')";
					}

					AnalysisFragment.analysisFragment.first_list_selected = first_spinner
							.getSelectedStrings();
					str = DiaryMatchesFragment
							.buildSelectedItemString(
									AnalysisFragment.analysisFragment.first_list_selected,
									false);
					if (!str.equals("")) {
						AnalysisFragment.analysisFragment.first_whereClause = " and m."
								+ MatchDb.KEY_FIRST_ACTION + " in(" + str + ")";
					} else {
						AnalysisFragment.analysisFragment.first_whereClause = " and m."
								+ MatchDb.KEY_FIRST_ACTION + " in('')";
					}

					dialog.dismiss();
				}
			});
			break;
		default:
			break;
		}
		dialog.show();
	}

	@Override
	public void onBackPressed() {
		switch (currentFragment) {
		case CAREER_FRAGMENT:
			break;
		case MATCH_CREATION_FRAGMENT:
			currentFragment = ONGOING_MATCHES_FRAGMENT;
			preFragment = CAREER_FRAGMENT;
			selectItem(ONGOING_MATCHES_FRAGMENT, true);
			onPrepareOptionsMenu(current_menu);
			return;
		case PERFORMANCE_FRAGMENT_EDIT:
			PerformanceFragmentEdit.performanceFragmentEdit.insertOrUpdate();
			onPrepareOptionsMenu(current_menu);
			return;
		case PERFORMANCE_FRAGMENT_VIEW:
			currentFragment = DIARY_MATCHES_FRAGMENT;
			preFragment = CAREER_FRAGMENT;
			selectItem(currentFragment, true);
			onPrepareOptionsMenu(current_menu);
			return;
		case PROFILE_FRAGMENT:
			if (ProfileFragment.currentProfileFragment == ProfileFragment.PROFILE_EDIT_FRAGMENT) {
				Log.d("Debug", "Profile Edit Hi");
				ProfileEditFragment.profileEditFragment.saveEditedProfile();
				ProfileFragment.currentProfileFragment = ProfileFragment.PROFILE_VIEW_FRAGMENT;
				onPrepareOptionsMenu(current_menu);
				ProfileFragment.profileFragment.viewFragment();
				return;
			}
		default:
			switch (preFragment) {
			case PROFILE_FRAGMENT:
			case ANALYSIS_FRAGMENT:
			case CAREER_FRAGMENT:
			case DIARY_MATCHES_FRAGMENT:
			case ONGOING_MATCHES_FRAGMENT:
				currentFragment = CAREER_FRAGMENT;
				preFragment = NO_FRAGMENT;
				selectItem(CAREER_FRAGMENT, true);
				onPrepareOptionsMenu(current_menu);
				return;
			}
			break;
		}
		super.onBackPressed();
	}

	public void make_directory() {

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			try {
				File extStore = Environment.getExternalStorageDirectory();
				File projDir = new File(extStore.getAbsolutePath() + "/"
						+ getResources().getString(R.string.cricdecode_dir));
				if (!projDir.exists())
					projDir.mkdirs();

				Log.w("MainActivity", "Creating Directory");

			} catch (Exception e) {

			}
		}
	}

	public void showNavigationDrawer(View view) {
		toggleNavigationDrawer();
	}

	private void toggleNavigationDrawer() {
		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			mDrawerLayout.openDrawer(mDrawerList);
		}
	}

	public void onMenuButtonClick(View v) {

		String str = ((ImageButton) v).getContentDescription().toString();
		if (str.equals(getResources().getString(R.string.new_match))) {
			preFragment = currentFragment;
			currentFragment = MATCH_CREATION_FRAGMENT;
			selectItem(MATCH_CREATION_FRAGMENT, true);
			onPrepareOptionsMenu(current_menu);
		} else if (str.equals(getResources().getString(R.string.edit_profile))) {
			ProfileFragment.currentProfileFragment = ProfileFragment.PROFILE_EDIT_FRAGMENT;
			onPrepareOptionsMenu(current_menu);
			ProfileFragment.profileFragment.viewFragment();
		} else if (str.equals(getResources().getString(R.string.save_profile))) {
			ProfileEditFragment.profileEditFragment.saveEditedProfile();
			ProfileFragment.currentProfileFragment = ProfileFragment.PROFILE_VIEW_FRAGMENT;
			onPrepareOptionsMenu(current_menu);
			ProfileFragment.profileFragment.viewFragment();
		} else if (str.equals(getResources().getString(R.string.create_match))) {
			MatchCreationFragment.matchCreationFragment.insertMatch();
			onPrepareOptionsMenu(current_menu);
		} else if (str.equals(getResources().getString(
				R.string.save_performance))) {
			PerformanceFragmentEdit.performanceFragmentEdit.insertOrUpdate();
			onPrepareOptionsMenu(current_menu);
		} else if (str.equals(getResources().getString(R.string.filter))) {
			showFilterDialog(currentFragment);
		}
	}
}
