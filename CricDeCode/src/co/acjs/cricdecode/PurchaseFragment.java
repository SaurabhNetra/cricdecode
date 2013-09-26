package co.acjs.cricdecode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class PurchaseFragment extends SherlockFragment {

	// Declare Variables
	static PurchaseFragment	purchaseFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		purchaseFragment = this;
		View rootView = inflater.inflate(R.layout.purchase, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		((TextView) view.findViewById(R.id.remove_ads_price))
				.setText(MainActivity.pur_remove_adds_price);
		((TextView) view.findViewById(R.id.remove_ads_title))
				.setText(MainActivity.pur_remove_adds_title);
		((TextView) view.findViewById(R.id.remove_ads_descr))
				.setText(MainActivity.pur_remove_adds_descr);
		((TextView) view.findViewById(R.id.infi_price))
				.setText(MainActivity.pur_infi_price);
		((TextView) view.findViewById(R.id.infi_title))
				.setText(MainActivity.pur_infi_title);
		((TextView) view.findViewById(R.id.infi_descr))
				.setText(MainActivity.pur_infi_descr);
		((TextView) view.findViewById(R.id.infi_sync_price))
				.setText(MainActivity.pur_infi_sync_price);
		((TextView) view.findViewById(R.id.infi_sync_title))
				.setText(MainActivity.pur_infi_sync_title);
		((TextView) view.findViewById(R.id.infi_sync_descr))
				.setText(MainActivity.pur_infi_sync_descr);
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
