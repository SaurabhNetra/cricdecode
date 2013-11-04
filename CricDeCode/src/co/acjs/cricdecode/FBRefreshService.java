package co.acjs.cricdecode;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;


public class FBRefreshService extends IntentService{
	@Override
	public void onCreate(){
		super.onCreate();
		Log.w("FBRefreshService", "Started");
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.w("FBRefreshService", "Ended");
	}

	public FBRefreshService(){
		super("FBRefreshService");
	}

	@Override
	protected void onHandleIntent(Intent intent){
		Session session = Session.getActiveSession();
		if(session.isOpened()){
			Request.newMeRequest(session, new Request.GraphUserCallback(){
				@Override
				public void onCompleted(GraphUser user, Response response){
					if(user != null){
						Log.w("FBRefreshService", "User: " + user.getName());
					}
				}
			}).executeAndWait();
		}
	}
}
