package co.acjs.cricdecode;

import java.util.List;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.actionbarsherlock.app.SherlockFragment;


public class CricDeCodePagerAdapter extends FragmentPagerAdapter{
	private List<SherlockFragment>	fragments;

	/**
	 * @param fm
	 * @param fragments
	 */
	public CricDeCodePagerAdapter(FragmentManager fm, List<SherlockFragment> fragments){
		super(fm);
		this.fragments = fragments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public SherlockFragment getItem(int position){
		return this.fragments.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount(){
		return this.fragments.size();
	}
}
