package co.acjs.cricdecode;

import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.api.StackMobQueryField;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;


public class CheckPurchaseInfiSync extends IntentService{
	public static boolean	started	= true;
	public static Context	who;
	public String			orderId, token, sign;

	public CheckPurchaseInfiSync(){
		super("CheckPurchasedAdRemovalService");
	}

	@Override
	public void onCreate(){
		super.onCreate();
		who = this;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent){
		AccessSharedPrefs.mPrefs = getApplicationContext().getSharedPreferences("CricDeCode", Context.MODE_PRIVATE);
		try{
			JSONObject jn = new JSONObject(intent.getExtras().getString("json"));
			orderId = jn.getString("orderId");
			token = jn.getString("Token");
			sign = jn.getString("Sign");
		}catch(JSONException e1){
			e1.printStackTrace();
		}
		long t = new Date().getTime();
		ServerDBSubInfiSync.query(ServerDBSubInfiSync.class, new StackMobQuery().field(new StackMobQueryField("user_id").isEqualTo(AccessSharedPrefs.mPrefs.getString("id", ""))).field(new StackMobQueryField("order_id").isEqualTo(orderId)).field(new StackMobQueryField("token").isEqualTo(token)).field(new StackMobQueryField("sign").isEqualTo(sign)).field(new StackMobQueryField("validuntil_ts_msec").isGreaterThan(t)), new StackMobQueryCallback<ServerDBSubInfiSync>(){
			@Override
			public void failure(StackMobException arg0){}

			@Override
			public void success(List<ServerDBSubInfiSync> arg0){
				if(arg0.size() == 0){
					AccessSharedPrefs.setString(who, "infi_sync", "no");
				}else{
					AccessSharedPrefs.setString(who, "infi_sync", "yes");
				}
			}
		});
		/* final JSONParser jsonParser = new JSONParser(); List<NameValuePair> params = new ArrayList<NameValuePair>(); params.add(new BasicNameValuePair("id", AccessSharedPrefs.mPrefs .getString("id", ""))); params.add(new BasicNameValuePair("json", intent.getExtras().getString( "json"))); JSONObject jn = null; jn = jsonParser.makeHttpRequest( getResources().getString(R.string.check_purchase_infi_sync), "POST", params, this); try { if (jn.getInt("status") == 1) { AccessSharedPrefs.setString(this, "infi_sync", "no"); } } catch (Exception e) { } */
	}
}
