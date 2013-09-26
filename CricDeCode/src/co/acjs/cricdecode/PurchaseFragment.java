package co.acjs.cricdecode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class PurchaseFragment extends SherlockFragment {

	// Declare Variables
	static PurchaseFragment purchaseFragment;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		purchaseFragment = this;
		View rootView = inflater.inflate(R.layout.purchase, container,
				false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("Debug", "On Activity Created called Profile");
		if (savedInstanceState != null) {
			
			}
		}

	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
	}

	
}
