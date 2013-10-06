package co.acjs.cricdecode;

import com.stackmob.sdk.model.StackMobModel;

public class ServerDBAndroidDevices extends StackMobModel {
	String id;
	String gcm_id;
	String ts;

	public ServerDBAndroidDevices(String id, String gcm_id, String ts) {
		super(ServerDBAndroidDevices.class);
		this.id = id;
		this.gcm_id = id;
		this.ts = id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setGcmId(String gcm_id) {
		this.gcm_id = gcm_id;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getId() {
		return this.id;
	}

	public String getGcmId() {
		return this.gcm_id;
	}

	public String getTs() {
		return this.ts;
	}

}