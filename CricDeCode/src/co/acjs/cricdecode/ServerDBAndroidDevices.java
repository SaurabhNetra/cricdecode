package co.acjs.cricdecode;

import com.stackmob.sdk.model.StackMobModel;


public class ServerDBAndroidDevices extends StackMobModel{
	String	user_id;
	String	gcm_id;

	public ServerDBAndroidDevices(String user_id, String gcm_id){
		super(ServerDBAndroidDevices.class);
		this.user_id = user_id;
		this.gcm_id = gcm_id;
	}

	public void setId(String user_id){
		this.user_id = user_id;
	}

	public void setGcmId(String gcm_id){
		this.gcm_id = gcm_id;
	}

	public String getId(){
		return this.user_id;
	}

	public String getGcmId(){
		return this.gcm_id;
	}
}
