package co.acjs.cricdecode;

import com.stackmob.sdk.model.StackMobModel;


public class ServerDBSubInfi extends StackMobModel{
	String	user_id;
	String	order_id;
	String	token;
	String	sign;
	int		autorenewing;
	long	initiation_ts_msec;
	long	validUntil_ts_msec;

	public ServerDBSubInfi(String user_id, String order_id, String token, String sign, int autorenewing, int initiation_ts_msec, int validUntil_ts_msec){
		super(ServerDBSubInfi.class);
		this.user_id = user_id;
		this.order_id = order_id;
		this.token = token;
		this.sign = sign;
		this.autorenewing = autorenewing;
		this.initiation_ts_msec = initiation_ts_msec;
		this.validUntil_ts_msec = validUntil_ts_msec;
	}

	public void setId(String user_id){
		this.user_id = user_id;
	}

	public void setOrderId(String order_id){
		this.order_id = order_id;
	}

	public void setToken(String token){
		this.token = token;
	}

	public void setSign(String sign){
		this.sign = sign;
	}

	public void setAutoRenewing(int autorenewing){
		this.autorenewing = autorenewing;
	}

	public void setInitiationTs(long initiation_ts_msec){
		this.initiation_ts_msec = initiation_ts_msec;
	}

	public void setValidUntilTs(long validUntil_ts_msec){
		this.validUntil_ts_msec = validUntil_ts_msec;
	}

	public String getId(){
		return this.user_id;
	}

	public String getOrderId(){
		return this.order_id;
	}

	public String getToken(){
		return this.token;
	}

	public String getSign(){
		return this.sign;
	}

	public int getAutoRenewing(){
		return this.autorenewing;
	}

	public long getInitiationTs(){
		return this.initiation_ts_msec;
	}

	public long getValidUntilTs(){
		return this.validUntil_ts_msec;
	}
}
