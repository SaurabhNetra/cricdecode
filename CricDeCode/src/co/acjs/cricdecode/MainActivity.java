package co.acjs.cricdecode;

import android.content.ContentProviderClient;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;

public class MainActivity extends SherlockFragmentActivity {
	public static MainActivity	mainActivity;

	private static final int	POSITION_PROFILE	= 0, POSITION_MATCH = 1,
			POSITION_NEW_MATCH = 2;

	// Career data
	private int					matches;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mainActivity = this;

		ListView listView = (ListView) findViewById(R.id.activityList);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getResources()
						.getStringArray(R.array.activity_list));
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
				Intent intent;
				switch (position) {
					case POSITION_PROFILE:
						intent = new Intent(getBaseContext(),
								ProfileActivity.class);
						startActivity(intent);
						break;
					case POSITION_MATCH:
						intent = new Intent(getBaseContext(),
								MatchActivity.class);
						startActivity(intent);
						break;
					case POSITION_NEW_MATCH:
						intent = new Intent(getBaseContext(),
								MatchCreateActivity.class);
						startActivity(intent);
						break;
					default:
						break;
				}
			}
		});

		// Look up the AdView as a resource and load a request.
		(new Thread() {
			public void run() {
				Looper.prepare();
				AdView adView = (AdView) mainActivity.findViewById(R.id.adView);
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
	protected void onResume() {
		super.onResume();
		// Extract Career Data
		ContentProviderClient client = getContentResolver()
				.acquireContentProviderClient(
						CricDeCodeContentProvider.AUTHORITY);
		SQLiteDatabase dbHandle = ((CricDeCodeContentProvider) client
				.getLocalContentProvider()).getDbHelper().getReadableDatabase();

		Cursor cursor = dbHandle
				.rawQuery(
						"select count(" + MatchDb.KEY_ROWID + ") from " + MatchDb.SQLITE_TABLE,
						null);
		cursor.moveToFirst();
		matches = cursor.getInt(0);
		cursor.close();

		((TextView) findViewById(R.id.lblMatches)).setText(matches + "");

		dbHandle.close();
		client.release();
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Google Analytics Start
		EasyTracker.getInstance().activityStart(this);
	}

}