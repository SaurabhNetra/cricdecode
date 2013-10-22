package co.acjs.cricdecode;

import com.stackmob.sdk.model.StackMobModel;

public class ServerDBUserTable extends StackMobModel {
	String	user_id;
	int		device_no;
	String	first_name;
	String	last_name;
	String	nick_name;
	String	fb_link;
	String	dob;
	String	role;
	String	battingStyle;
	String	bowlingStyle;
	int		hasAndroid;
	
	public ServerDBUserTable()
	{
		super(ServerDBUserTable.class);
	}

	public ServerDBUserTable(String user_id, int device_no, String first_name, String last_name, String nick_name, String fb_link, String dob, String role, String battingStyle, String bowlingStyle, int hasAndroid) {
		super(ServerDBUserTable.class);
		this.user_id = user_id;
		this.device_no = device_no;
		this.first_name = first_name;
		this.last_name = last_name;
		this.nick_name = nick_name;
		this.fb_link = fb_link;
		this.dob = dob;
		this.role = role;
		this.bowlingStyle = bowlingStyle;
		this.battingStyle = battingStyle;
		this.hasAndroid = hasAndroid;
	}

	public void setId(String user_id) {
		this.user_id = user_id;
	}

	public void setDeviceNo(int device_no) {
		this.device_no = device_no;
	}

	public void setFirstName(String first_name) {
		this.first_name = first_name;
	}

	public void setLastName(String last_name) {
		this.last_name = last_name;
	}

	public void setNickName(String nick_name) {
		this.nick_name = nick_name;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public void setFbLink(String fb_link) {
		this.fb_link = fb_link;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setBowlingStyle(String bowlingStyle) {
		this.bowlingStyle = bowlingStyle;
	}

	public void setBattingStyle(String battingStyle) {
		this.battingStyle = battingStyle;
	}

	public void setHasAndroid(int hasAndroid) {
		this.hasAndroid = hasAndroid;
	}

	public String getId() {
		return this.user_id;
	}

	public int getDeviceNo() {
		return this.device_no;
	}

	public String getFirstName() {
		return this.first_name;
	}

	public String getLastName() {
		return this.last_name;
	}

	public String getNickName() {
		return this.nick_name;
	}

	public String getDob() {
		return this.dob;
	}

	public String getFbLink() {
		return this.fb_link;
	}
	public String getRole() {
		return this.role;
	}

	public String getBowlingStyle() {
		return this.bowlingStyle;
	}

	public String getBattingStyle() {
		return this.battingStyle;
	}

	public int getHasAndroid() {
		return this.hasAndroid;
	}

}