package co.acjs.cricdecode;

import com.stackmob.sdk.model.StackMobModel;

public class ServerDBCricketMatch extends StackMobModel {

	private String	user_id;
	private int		match_id;
	private int		device_id;
	private String	match_date;
	private String	my_team;
	private String	opponent_team;
	private String	venue;
	private int		overs;
	private int		innings;
	private String	result;
	private String	level;
	private String	first_action;
	private String	duration;
	private String	review;
	private int		status;

	public ServerDBCricketMatch(String user_id, int match_id, int device_id, String match_date, String my_team, String opponent_team, String venue, int overs, int innings, String result, String level, String first_action, String duration, String review, int status) {
		super(ServerDBCricketMatch.class);
		this.user_id = user_id;
		this.match_id = match_id;
		this.device_id = device_id;
		this.match_date = match_date;
		this.my_team = my_team;
		this.opponent_team = opponent_team;
		this.venue = venue;
		this.overs = overs;
		this.innings = innings;
		this.result = result;
		this.level = level;
		this.first_action = first_action;
		this.duration = duration;
		this.review = review;
		this.status = status;
	}

	public void setId(String user_id) {
		this.user_id = user_id;
	}

	public void setMatchId(int match_id) {
		this.match_id = match_id;
	}

	public void setDeviceId(int device_id) {
		this.device_id = device_id;
	}

	public void setMatchDate(String match_date) {
		this.match_date = match_date;
	}

	public void setOpponentTeam(String opponent_team) {
		this.opponent_team = opponent_team;
	}

	public void setMyTeam(String my_team) {
		this.my_team = my_team;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public void setOvers(int overs) {
		this.overs = overs;
	}

	public void setInnings(int innings) {
		this.innings = innings;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public void setFirstAction(String first_action) {
		this.first_action = first_action;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getId() {
		return this.user_id;
	}

	public int getMatchId() {
		return this.match_id;
	}

	public int getDeviceId() {
		return this.device_id;
	}

	public String getMatchDate() {
		return this.match_date;
	}

	public String getMyTeam() {
		return this.my_team;
	}

	public String getOpponentTeam() {
		return this.opponent_team;
	}

	public String getVenue() {
		return this.venue;
	}

	public int getOvers() {
		return this.overs;
	}

	public int getInnings() {
		return this.innings;
	}

	public String getResult() {
		return this.result;
	}

	public String getLevel() {
		return this.level;
	}

	public String getFirstAction() {
		return this.first_action;
	}

	public String getDuration() {
		return this.duration;
	}

	public String getReview() {
		return this.review;
	}

	public int getStatus() {
		return this.status;
	}
}