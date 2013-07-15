package co.acjs.cricdecode;

import android.content.ContentProviderClient;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class CareerActivity extends SherlockFragmentActivity {

	private ContentProviderClient	client;
	private SQLiteDatabase			dbHandle;

	// Layouts
	private RelativeLayout			batting;

	// Career data
	private TextView				matches;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_career);
		batting = (RelativeLayout) findViewById(R.id.layBatting);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		client = getContentResolver().acquireContentProviderClient(
				CricDeCodeContentProvider.AUTHORITY);
		dbHandle = ((CricDeCodeContentProvider) client
				.getLocalContentProvider()).getDbHelper().getReadableDatabase();

		// Initialize Career Data
		matches = ((TextView) findViewById(R.id.lblMatches));
		Cursor cursor = dbHandle
				.rawQuery(
						"select count(" + MatchDb.KEY_ROWID + ") from " + MatchDb.SQLITE_TABLE + " where " + MatchDb.KEY_STATUS + "='" + MatchDb.MATCH_HISTORY + "'",
						null);
		cursor.moveToFirst();
		matches.setText(cursor.getInt(0) + "");
		cursor.close();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ttlBatting:
				if (batting.getVisibility() == View.GONE) {
					batting.setVisibility(View.VISIBLE);
				} else {
					batting.setVisibility(View.GONE);
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbHandle.close();
		client.release();
	}

}
