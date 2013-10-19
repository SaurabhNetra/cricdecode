package co.acjs.cricdecode;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	Context cont;
	String response;

	// constructor
	public JSONParser() {

	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method,
			List<NameValuePair> params, Context cont) {

		// Making HTTP request
		try {
			this.cont = cont;
			// check for request method
			if (method == "POST") {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpPost httpPost = new HttpPost(url);

				if (isOnline(cont)) {

					Log.w("Is Online", "JSONParser");
					HttpResponse httpResponse = httpClient.execute(httpPost);
					Log.d("Login: Response",
							EntityUtils.toString(httpResponse.getEntity()));
					HttpEntity httpEntity = httpResponse.getEntity();
					is = httpEntity.getContent();
				} else {
					Log.w("Not Online", "JSONParser");
				}
				/*
				 * if(isOnline(cont)) {
				 * Log.w("JSONParser","sending post request"); DefaultHttpClient
				 * httpClient = new DefaultHttpClient(); ResponseHandler<String>
				 * resonseHandler = new BasicResponseHandler(); HttpPost
				 * postMethod = new HttpPost(url); postMethod.setEntity(new
				 * UrlEncodedFormEntity(params)); String authorizationString =
				 * "Basic " + Base64.encodeToString(("bhajji" + ":" +
				 * "__tube_l8").getBytes(), Base64.NO_WRAP); //this line is
				 * diffe postMethod.setHeader("Authorization",
				 * authorizationString); response =
				 * httpClient.execute(postMethod,resonseHandler);
				 * Log.w("HTTP respone","response: "+response); } else {
				 * Log.w("Not Online", "JSONParser"); }
				 */

			} else if (method == "GET") {
				// request method is GET

				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);

				if (isOnline(cont)) {

					Log.w("Is Online", "JSONParser");
					HttpResponse httpResponse = httpClient.execute(httpGet);
					Log.d("Login: Response",
							EntityUtils.toString(httpResponse.getEntity()));
					HttpEntity httpEntity = httpResponse.getEntity();
					is = httpEntity.getContent();
				} else {
					Log.w("Not Online", "JSONParser");
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * try { BufferedReader reader = new BufferedReader(new
		 * InputStreamReader( is, "iso-8859-1"), 8); StringBuilder sb = new
		 * StringBuilder(); String line = null; while ((line =
		 * reader.readLine()) != null) { sb.append(line + "\n"); } is.close();
		 * json = sb.toString(); } catch (Exception e) { Log.e("Buffer Error",
		 * "Error converting result " + e.toString()); }
		 * 
		 * // try parse the string to a JSON object try { jObj = new
		 * JSONObject(json); } catch (JSONException e) { Log.e("JSON Parser",
		 * "Error parsing data " + e.toString()); }
		 */

		// return JSON String
		JSONObject k = null;
		try {
			k = new JSONObject(response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return k;
	}

	public Boolean isOnline(Context cont) {
		ConnectivityManager connectivityManager = (ConnectivityManager) cont
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}