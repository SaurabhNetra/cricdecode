package co.acjs.cricdecode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class ProfileFragment extends SherlockFragment {

	// Declare Variables
	static ProfileFragment profileFragment;
	static int currentProfileFragment;

	// Declare Constants
	static final int PROFILE_VIEW_FRAGMENT = 0, PROFILE_EDIT_FRAGMENT = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		profileFragment = this;
		View rootView = inflater.inflate(R.layout.profile_fragment, container,
				false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("Debug", "On Activity Created called Profile");
		if (savedInstanceState != null) {
			switch (currentProfileFragment) {
			case PROFILE_VIEW_FRAGMENT:
				ProfileViewFragment.profileViewFragment = (ProfileViewFragment) getChildFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			case PROFILE_EDIT_FRAGMENT:
				ProfileEditFragment.profileEditFragment = (ProfileEditFragment) getChildFragmentManager()
						.getFragment(savedInstanceState,
								"currentFragmentInstance");
				break;
			default:
				break;
			}
		}
		viewFragment();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (ProfileEditFragment.profileEditFragment != null) {
			ProfileEditFragment.profileEditFragment.onActivityResult(
					requestCode, resultCode, intent);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		switch (currentProfileFragment) {
		case PROFILE_VIEW_FRAGMENT:
			getChildFragmentManager().putFragment(outState,
					"currentFragmentInstance",
					ProfileViewFragment.profileViewFragment);
			((MainActivity) getSherlockActivity()).setPageName(0);
			break;
		case PROFILE_EDIT_FRAGMENT:
			getChildFragmentManager().putFragment(outState,
					"currentFragmentInstance",
					ProfileEditFragment.profileEditFragment);
			((MainActivity) getSherlockActivity()).setPageName(1);
			break;
		default:
			break;
		}
	}

	public void viewFragment() {
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		if (currentProfileFragment == PROFILE_VIEW_FRAGMENT) {
			ProfileViewFragment profileViewFragment;
			if (ProfileViewFragment.profileViewFragment == null) {
				profileViewFragment = new ProfileViewFragment();
			} else {
				profileViewFragment = ProfileViewFragment.profileViewFragment;
			}
			ft.replace(R.id.content_profile, profileViewFragment);
		} else {
			ProfileEditFragment profileEditFragment;
			if (ProfileEditFragment.profileEditFragment == null) {
				profileEditFragment = new ProfileEditFragment();
			} else {
				profileEditFragment = ProfileEditFragment.profileEditFragment;
			}
			ft.replace(R.id.content_profile, profileEditFragment);
		}
		ft.commit();
	}

}
