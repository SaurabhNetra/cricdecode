package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.stackmob.android.sdk.common.StackMobAndroid;

public class ProfileEditService extends IntentService {
	public static boolean started = true;
	public static Context who;

	public ProfileEditService() {
		super("ProfileEditService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		who = this;
		StackMobAndroid.init(
				getApplicationContext(),
				1,
				decrypt("5g28><6hi=2", "26j6jff", "29>5h;<=8>", "f8=f=if5",
						"6103927458", 5));
		Log.w("ProfileEditService", "Started");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w("ProfileEditService", "Ended");
	}

	public static String decrypt(String val1, String val2, String val3,
			String val4, String seq, int ci) {
		String val = val2 + val4 + val1 + val3;
		int num = val.length() / 10;
		char h[][] = new char[num + 1][10];
		int start = 0;
		int end = 10;
		for (int i = 0; i < num; i++) {
			String s = val.substring(start, end);
			h[i] = s.toCharArray();
			start = end;
			end = end + 10;
		}
		h[num] = val.substring(start, val.length()).toCharArray();
		char[][] un = new char[10][num];
		char s[] = seq.toCharArray();
		for (int i = 0; i < num; i++) {
			for (int j = 0; j < 10; j++) {
				String n = new String("" + s[j]);
				int ind = Integer.parseInt(n);
				un[ind][i] = h[i][j];
			}
		}
		String dec = "";
		for (int i = 0; i < 10; i++) {
			String n = new String(un[i]);
			dec = dec + n;
		}
		String ex = new String(h[num]);
		dec = dec + ex;
		char[] us = dec.toCharArray();
		char[] sh = new char[us.length];
		for (int i = 0; i < us.length; i++) {
			sh[i] = (char) (us[i] - ci);
		}
		return new String(sh);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		AccessSharedPrefs.mPrefs = getApplicationContext()
				.getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		if (AccessSharedPrefs.mPrefs.getString("ProfileEditServiceCalled",
				CDCAppClass.DOESNT_NEED_TO_BE_CALLED).equals(
				CDCAppClass.NEEDS_TO_BE_CALLED)) {

			try {
				final JSONParser jsonParser = new JSONParser();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("uid",
						AccessSharedPrefs.mPrefs.getString("id", "")));
				params.add(new BasicNameValuePair("uid",
						AccessSharedPrefs.mPrefs.getString("nickname",
								AccessSharedPrefs.mPrefs.getString("nickname",
										""))));
				params.add(new BasicNameValuePair("uid",
						AccessSharedPrefs.mPrefs.getString("role",
								AccessSharedPrefs.mPrefs.getString("role", ""))));
				params.add(new BasicNameValuePair("uid",
						AccessSharedPrefs.mPrefs.getString("bowlingStyle",
								AccessSharedPrefs.mPrefs.getString(
										"bowlingStyle", ""))));
				params.add(new BasicNameValuePair("uid",
						AccessSharedPrefs.mPrefs.getString("battingStyle",
								AccessSharedPrefs.mPrefs.getString(
										"battingStyle", ""))));
				Log.w("Sending User Data...", "ProfileEditService:"
						+ jsonParser.isOnline(who));
				int trial = 1;
				JSONObject jn = null;
				while (jsonParser.isOnline(who)) {
					Log.w("JSONParser", "ProfileEditService: Called");
					//TODO ping gae
					jn = jsonParser.makeHttpRequest(
							getResources()
									.getString(R.string.edit_profile_sync),
							"POST", params, who);
					Log.w("JSON returned", "ProfileEditService: " + jn);
					Log.w("trial value", "ProfileEditService: " + trial);
					if (jn != null)
						break;
					try {
						Thread.sleep(10 * trial);
					} catch (InterruptedException e) {
					}
					trial++;
				}

				if (jn.getInt("status") == 0) {
					AccessSharedPrefs.setString(who,
							"ProfileEditServiceCalled",
							CDCAppClass.NEEDS_TO_BE_CALLED);
					AccessSharedPrefs.setString(who,
							"ProfileEditServiceAzureCalled",
							CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
				} else if (jn.getInt("status") == 1) {
					AccessSharedPrefs.setString(who,
							"ProfileEditServiceCalled",
							CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
					AccessSharedPrefs.setString(who,
							"ProfileEditServiceAzureCalled",
							CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
				} else if (jn.getInt("status") == 2) {
					AccessSharedPrefs.setString(who,
							"ProfileEditServiceCalled",
							CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
					AccessSharedPrefs.setString(who,
							"ProfileEditServiceAzureCalled",
							CDCAppClass.NEEDS_TO_BE_CALLED);
				}

			} catch (NullPointerException e) {
			} catch (JSONException e) {
			}

			/*
			 * ServerDBUserTable.query(ServerDBUserTable.class, new
			 * StackMobQuery().field(new
			 * StackMobQueryField("user_id").isEqualTo(
			 * AccessSharedPrefs.mPrefs.getString("id", ""))), new
			 * StackMobQueryCallback<ServerDBUserTable>(){
			 * 
			 * @Override public void failure(StackMobException arg0){
			 * Log.w("ProfileEditService", "user fetch failed"); }
			 * 
			 * @Override public void success(List<ServerDBUserTable> arg0){
			 * if(arg0.size() != 0){ ServerDBUserTable d = new
			 * ServerDBUserTable(); d.setID(arg0.get(0).getID());
			 * d.setDeviceNo(arg0.get(0).getDeviceNo());
			 * d.setDob(arg0.get(0).getDob());
			 * d.setFbLink(arg0.get(0).getFbLink());
			 * d.setFirstName(arg0.get(0).getFirstName());
			 * d.setLastName(arg0.get(0).getLastName());
			 * d.setHasAndroid(arg0.get(0).getHasAndroid());
			 * d.setId(arg0.get(0).getId());
			 * d.setBattingStyle(AccessSharedPrefs.
			 * mPrefs.getString("battingStyle", ""));
			 * d.setBowlingStyle(AccessSharedPrefs
			 * .mPrefs.getString("bowlingStyle", ""));
			 * d.setRole(AccessSharedPrefs.mPrefs.getString("role", ""));
			 * d.setNickName(AccessSharedPrefs.mPrefs.getString("nickname",
			 * "")); d.save(new StackMobCallback(){
			 * 
			 * @Override public void failure(StackMobException arg0){
			 * Log.w("ProfileEditService", "Update failed"); }
			 * 
			 * @Override public void success(String arg0){
			 * Log.w("ProfileEditService", "Update success");
			 * ServerDBAndroidDevices.query(ServerDBAndroidDevices.class, new
			 * StackMobQuery().field(new
			 * StackMobQueryField("user_id").isEqualTo(
			 * AccessSharedPrefs.mPrefs.getString("id", ""))), new
			 * StackMobQueryCallback<ServerDBAndroidDevices>(){
			 * 
			 * @Override public void failure(StackMobException arg0){}
			 * 
			 * @Override public void success(List<ServerDBAndroidDevices> arg0){
			 * Log.w("ProfileEditService", "GCM Ids fetched" + arg0.size());
			 * String regids = ""; for(int i = 0; i < arg0.size(); i++){ regids
			 * = regids + " " + arg0.get(i).getGcmId(); } JSONObject msg = new
			 * JSONObject(); Log.w("ProfileEditService: ", "" + regids); try{
			 * msg.put("gcmid", 1); msg.put("nickname",
			 * AccessSharedPrefs.mPrefs.getString("nickname", ""));
			 * msg.put("role", AccessSharedPrefs.mPrefs.getString("role", ""));
			 * msg.put("bowlingStyle",
			 * AccessSharedPrefs.mPrefs.getString("bowlingStyle", ""));
			 * msg.put("battingStyle",
			 * AccessSharedPrefs.mPrefs.getString("battingStyle", ""));
			 * Log.w("ProfileEditService: ", "" + msg.toString()); final
			 * JSONParser jsonParser = new JSONParser(); List<NameValuePair>
			 * params = new ArrayList<NameValuePair>(); params.add(new
			 * BasicNameValuePair("SendToArrays", regids)); params.add(new
			 * BasicNameValuePair("MsgToSend", msg.toString())); params.add(new
			 * BasicNameValuePair("uid",
			 * AccessSharedPrefs.mPrefs.getString("id", "")));
			 * Log.w("Sending User Data...", "ProfileEditService:" +
			 * jsonParser.isOnline(who)); int trial = 1; JSONObject jn = null;
			 * while(jsonParser.isOnline(who)){ Log.w("JSONParser",
			 * "ProfileEditService: Called"); jn =
			 * jsonParser.makeHttpRequest(getResources
			 * ().getString(R.string.edit_profile_sync), "POST", params, who);
			 * Log.w("JSON returned", "ProfileEditService: " + jn);
			 * Log.w("trial value", "ProfileEditService: " + trial); if(jn !=
			 * null) break; try{ Thread.sleep(10 * trial);
			 * }catch(InterruptedException e){} trial++; } try{
			 * if(jn.getInt("status") == 1) AccessSharedPrefs.setString(who,
			 * "ProfileEditServiceCalled",
			 * CDCAppClass.DOESNT_NEED_TO_BE_CALLED);
			 * }catch(NullPointerException e){}catch(JSONException e){
			 * e.printStackTrace(); } }catch(JSONException e){  } } }); }
			 * }); } } });
			 */
		}
	}
}
