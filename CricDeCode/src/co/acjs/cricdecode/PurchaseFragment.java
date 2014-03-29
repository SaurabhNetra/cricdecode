package co.acjs.cricdecode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.acjs.cricdecode.CDCAppClass.TrackerName;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.analytics.HitBuilders;


public class PurchaseFragment extends SherlockFragment{
	// Declare Variables
	static PurchaseFragment	purchaseFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		purchaseFragment = this;
		View rootView = inflater.inflate(R.layout.purchase, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		((TextView)view.findViewById(R.id.remove_ads_price)).setText(AccessSharedPrefs.mPrefs.getString("pur_remove_adds_price", ""));
		((TextView)view.findViewById(R.id.remove_ads_title)).setText(AccessSharedPrefs.mPrefs.getString("pur_remove_adds_title", ""));
		((TextView)view.findViewById(R.id.remove_ads_descr)).setText(AccessSharedPrefs.mPrefs.getString("pur_remove_adds_descr", ""));
		((TextView)view.findViewById(R.id.infi_price)).setText(AccessSharedPrefs.mPrefs.getString("pur_infi_price", ""));
		((TextView)view.findViewById(R.id.infi_title)).setText(AccessSharedPrefs.mPrefs.getString("pur_infi_title", ""));
		((TextView)view.findViewById(R.id.infi_descr)).setText(AccessSharedPrefs.mPrefs.getString("pur_infi_descr", ""));
		((TextView)view.findViewById(R.id.infi_sync_price)).setText(AccessSharedPrefs.mPrefs.getString("pur_infi_sync_price", ""));
		((TextView)view.findViewById(R.id.infi_sync_title)).setText(AccessSharedPrefs.mPrefs.getString("pur_infi_sync_title", ""));
		((TextView)view.findViewById(R.id.infi_sync_descr)).setText(AccessSharedPrefs.mPrefs.getString("pur_infi_sync_descr", ""));
		((TextView)view.findViewById(R.id.sync_price)).setText(AccessSharedPrefs.mPrefs.getString("pur_sync_price", ""));
		((TextView)view.findViewById(R.id.sync_title)).setText(AccessSharedPrefs.mPrefs.getString("pur_sync_title", ""));
		((TextView)view.findViewById(R.id.sync_descr)).setText(AccessSharedPrefs.mPrefs.getString("pur_sync_descr", ""));
		if(AccessSharedPrefs.mPrefs.getString("infi_sync", "no").equals("yes")){
			((TextView)view.findViewById(R.id.infi_sync_pur)).setVisibility(View.VISIBLE);
			((TextView)view.findViewById(R.id.infi_pur)).setText("Not Applicable");
			((TextView)view.findViewById(R.id.infi_pur)).setVisibility(View.VISIBLE);
		}
		else if(AccessSharedPrefs.mPrefs.getString("infi_use", "no").equals("yes")){
			((TextView)view.findViewById(R.id.infi_pur)).setVisibility(View.VISIBLE);
			((TextView)view.findViewById(R.id.infi_pur)).setText("Purchased");
			((LinearLayout)view.findViewById(R.id.pur_subscribe_sync)).setVisibility(View.VISIBLE);
			((LinearLayout)view.findViewById(R.id.pur_subscribe_infi_sync)).setVisibility(View.GONE);
		}
		if(AccessSharedPrefs.mPrefs.getString("ad_free", "no").equals("yes")){
			((TextView)view.findViewById(R.id.rem_ads_pur)).setVisibility(View.VISIBLE);
		}
		if(AccessSharedPrefs.mPrefs.getString("sync", "no").equals("yes")){
			((TextView)view.findViewById(R.id.sync_pur)).setVisibility(View.VISIBLE);
		}
		
		com.google.android.gms.analytics.Tracker t = ((CDCAppClass) getActivity()
				.getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName(getResources().getString(R.string.analyticsPurchase));
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		Log.d("Debug", "On Activity Created called Profile");
		if(savedInstanceState != null){}
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
	}
}
