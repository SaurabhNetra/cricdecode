package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class PingChk extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pingchk);

		JSONParser jsonParser = new JSONParser();
		int trial = 1;
		JSONObject jn = null;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("del_matches", "nuffin"));
		while (jsonParser.isOnline(this)) {
			Log.w("JSONParser", "DeleteMatch:: Called");
			// ping gae-matches, gae pings gae-perf
			jn = jsonParser.makeHttpRequest(
					"http://acjs.azurewebsites.net/acjs/pingChk.php", "POST",
					params, this);
			// jn =
			// jsonParser.makeHttpRequest("http://cdc-acjs-user.appspot.com/pingchk",
			// "POST", params, this);
			Log.w("JSON returned", "DeleteMatch:: " + jn);
			Log.w("trial value", "DeleteMatch:: " + trial);
			if (jn != null)
				break;
			try {
				Thread.sleep(10 * trial);
			} catch (InterruptedException e) {
			}
			trial++;
			if (trial == 50) {
				break;
			}
		}

		try {
			if (jn != null) {

				Toast.makeText(this, jn.getInt("status"), Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(this, "jn=null", Toast.LENGTH_LONG)
						.show();
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
