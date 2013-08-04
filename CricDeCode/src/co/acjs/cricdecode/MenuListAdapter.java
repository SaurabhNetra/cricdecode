package co.acjs.cricdecode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {

	// Declare Variables
	Context context;
	String[] mTitle;
	LayoutInflater inflater;

	public MenuListAdapter(Context context, String[] title) {
		this.context = context;
		this.mTitle = title;
	}

	@Override
	public int getCount() {
		return mTitle.length;
	}

	@Override
	public Object getItem(int position) {
		return mTitle[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// Declare Variables
		TextView txtTitle;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.drawer_list_item, parent,
				false);

		// Locate the TextView in drawer_list_item.xml
		txtTitle = (TextView) itemView.findViewById(R.id.title);

		// Set the result into the TextView
		txtTitle.setText(mTitle[position]);

		return itemView;
	}

}