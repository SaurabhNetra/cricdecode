package co.acjs.cricdecode;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;


public class SupportFragment extends SherlockFragment{
	// Declare Variables
	static SupportFragment	supportFragment;
	static int				currentProfileFragment;
	static final int		TERMS_OF_SERVICE	= 0, PRIVACY_POLICY = 1, SUPPORT = 2, SHARE = 3, VERSION=4;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		supportFragment = this;
		View rootView = inflater.inflate(R.layout.support_fragment, container, false);
		ListView listView = (ListView)rootView.findViewById(R.id.content_list1);
		String[] values = getResources().getStringArray(R.array.support_list_item);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, values);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id){
				switch(position){
					case TERMS_OF_SERVICE:
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://cdc.acjs.co/terms_of_service.html")));
						break;
					case PRIVACY_POLICY:
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://cdc.acjs.co/privacy.html")));
						break;
					case SUPPORT:
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://cdc.acjs.co/support.html")));
						break;
					case SHARE:
						Intent sendIntent = new Intent();
						sendIntent.setAction(Intent.ACTION_SEND);
						sendIntent.putExtra(Intent.EXTRA_TEXT, "Download this awesome app! https://play.google.com/store/apps/details?id=co.acjs.cricdecode");
						sendIntent.setType("text/plain");
						startActivity(Intent.createChooser(sendIntent, "Share Via.."));
						break;
					case VERSION:
						try{
							((MainActivity)MainActivity.main_context).runOnUiThread(new Runnable(){
								public void run(){
									try{
										String versionName="";
										try{
											versionName = MainActivity.main_context.getPackageManager().getPackageInfo(MainActivity.main_context.getPackageName(), 0).versionName;
										}catch(NameNotFoundException e1){
											e1.printStackTrace();
										}
										new AlertDialog.Builder(MainActivity.main_context).setTitle("App Version").setMessage("Version "+versionName).setNeutralButton("OK", new DialogInterface.OnClickListener(){
											public void onClick(DialogInterface dialog, int which){
												dialog.dismiss();
											}
										}).show();
									}catch(Exception e){}
								}
							});
						}catch(Exception e){}
					default:
						break;
				}
			}
		});
		return rootView;
	}
}
