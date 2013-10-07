package co.acjs.cricdecode;

import com.stackmob.sdk.model.StackMobModel;

public class ServerDBPerformance extends StackMobModel {
	String	user_id;
	int		match_id;
	int		device_id;
	int		per_id;
	int		inning;
	int		bat_num;
	int		bat_runs;
	int		bat_balls;
	int		bat_time;
	int		fours;
	int		sixes;
	String	bat_dismissal;
	String	bat_bowler_type;
	String	bat_feilding_position;
	int		bat_chances;
	int		bowl_balls;
	int		bowl_spells;
	int		bowl_maidens;
	int		bowl_runs;
	int		bowl_fours;
	int		bowl_sixes;
	int		bowl_wkts_left;
	int		bowl_wkts_right;
	int		bowl_catches_dropped;
	int		bowl_no_balls;
	int		bowl_wides;
	int		field_slip_catch;
	int		field_close_catch;
	int		field_circle_catch;
	int		field_deep_catch;
	int		field_ro_circle;
	int		field_ro_direct_circle;
	int		field_ro_deep;
	int		field_ro_direct_deep;
	int		field_stumpings;
	int		field_byes;
	int		field_misfield;
	int		field_catches_dropped;
	int		status;

	public ServerDBPerformance(String user_id, int match_id, int device_id, int per_id, int inning, int bat_num, int bat_runs, int bat_balls, int bat_time, int fours, int sixes, String bat_dismissal, String bat_bowler_type, String bat_feilding_position, int bat_chances, int bowl_balls, int bowl_spells, int bowl_maidens, int bowl_runs, int bowl_fours, int bowl_sixes, int bowl_wkts_left, int bowl_wkts_right, int bowl_catches_dropped, int bowl_no_balls, int bowl_wides, int field_slip_catch, int field_close_catch, int field_circle_catch, int field_deep_catch, int field_ro_circle, int field_ro_direct_circle, int field_ro_deep, int field_ro_direct_deep, int field_stumpings, int field_byes, int field_misfield, int field_catches_dropped, int status) {
		super(ServerDBPerformance.class);
		this.user_id = user_id;
		this.match_id = match_id;
		this.device_id = device_id;
		this.per_id = per_id;
		this.inning = inning;
		this.bat_num = bat_num;
		this.bat_runs = bat_runs;
		this.bat_balls = bat_balls;
		this.bat_time = bat_time;
		this.fours = fours;
		this.sixes = sixes;
		this.bat_dismissal = bat_dismissal;
		this.bat_bowler_type = bat_bowler_type;
		this.bat_feilding_position = bat_feilding_position;
		this.bat_chances = bat_chances;
		this.bowl_balls = bowl_balls;
		this.bowl_spells = bowl_spells;
		this.bowl_maidens = bowl_maidens;
		this.bowl_runs = bowl_runs;
		this.bowl_fours = bowl_fours;
		this.bowl_sixes = bowl_sixes;
		this.bowl_wkts_left = bowl_wkts_left;
		this.bowl_wkts_right = bowl_wkts_right;
		this.bowl_catches_dropped = bowl_catches_dropped;
		this.bowl_no_balls = bowl_no_balls;
		this.bowl_wides = bowl_wides;
		this.field_slip_catch = field_slip_catch;
		this.field_close_catch = field_close_catch;
		this.field_circle_catch = field_circle_catch;
		this.field_deep_catch = field_deep_catch;
		this.field_ro_circle = field_ro_circle;
		this.field_ro_direct_circle = field_ro_direct_circle;
		this.field_ro_deep = field_ro_deep;
		this.field_ro_direct_deep = field_ro_direct_deep;
		this.field_stumpings = field_stumpings;
		this.field_byes = field_byes;
		this.field_misfield = field_misfield;
		this.field_catches_dropped = field_catches_dropped;
		this.status = status;
	}

	public void setId(String user_id) {
		this.user_id = user_id;
	}

	public void setMatchId(int match_id) {
		this.match_id = match_id;
	}

	public void setPerId(int per_id) {
		this.per_id = per_id;
	}

	public void setInning(int inning) {
		this.inning = inning;
	}

	public void setBatNum(int bat_num) {
		this.bat_num = bat_num;
	}

	public void setBatRuns(int bat_runs) {
		this.bat_runs = bat_runs;
	}

	public void setBatBalls(int bat_balls) {
		this.bat_balls = bat_balls;
	}

	public void setBatTime(int bat_time) {
		this.bat_time = bat_time;
	}

	public void setBatFours(int fours) {
		this.fours = fours;
	}

	public void setBatSixes(int sixes) {
		this.sixes = sixes;
	}

	public void setBatDismissal(String bat_dismissal) {
		this.bat_dismissal = bat_dismissal;
	}

	public void setBatBowlerType(String bat_bowler_type) {
		this.bat_bowler_type = bat_bowler_type;
	}

	public void setBatFieldingPosition(String bat_feilding_position) {
		this.bat_feilding_position = bat_feilding_position;
	}

	public void setBatChances(int bat_chances) {
		this.bat_chances = bat_chances;
	}

	public void setBat(int bowl_balls) {
		this.bowl_balls = bowl_balls;
	}

	public void setBowlSpells(int bowl_spells) {
		this.bowl_spells = bowl_spells;
	}

	public void setBowlMaidens(int bowl_maidens) {
		this.bowl_maidens = bowl_maidens;
	}

	public void setBowlRuns(int bowl_runs) {
		this.bowl_runs = bowl_runs;
	}

	public void setBowlFours(int bowl_fours) {
		this.bowl_fours = bowl_fours;
	}

	public void setBowlSixes(int bowl_sixes) {
		this.bowl_sixes = bowl_sixes;
	}

	public void setBowlWktsLeft(int bowl_wkts_left) {
		this.bowl_wkts_left = bowl_wkts_left;
	}

	public void setBowlWktsRight(int bowl_wkts_right) {
		this.bowl_wkts_right = bowl_wkts_right;
	}

	public void setBowlCatchesDropped(int bowl_catches_dropped) {
		this.bowl_catches_dropped = bowl_catches_dropped;
	}

	public void setNoBalls(int bowl_no_balls) {
		this.bowl_no_balls = bowl_no_balls;
	}

	public void setBowlWides(int bowl_wides) {
		this.bowl_wides = bowl_wides;
	}

	public void setFieldSlipCatch(int field_slip_catch) {
		this.field_slip_catch = field_slip_catch;
	}

	public void setFieldCloseCatch(int field_close_catch) {
		this.field_close_catch = field_close_catch;
	}

	public void setFieldCircleCatch(int field_circle_catch) {
		this.field_circle_catch = field_circle_catch;
	}

	public void setFieldDeepCatch(int field_deep_catch) {
		this.field_deep_catch = field_deep_catch;
	}

	public void setFieldRoCircle(int field_ro_circle) {
		this.field_ro_circle = field_ro_circle;
	}

	public void setFieldRoDirectCircle(int field_ro_direct_circle) {
		this.field_ro_direct_circle = field_ro_direct_circle;
	}

	public void setFieldRoDeep(int field_ro_deep) {
		this.field_ro_deep = field_ro_deep;
	}

	public void setFieldRoDirectDeep(int field_ro_direct_deep) {
		this.field_ro_direct_deep = field_ro_direct_deep;
	}

	public void setFieldStumping(int field_stumpings) {
		this.field_stumpings = field_stumpings;
	}

	public void setFieldByes(int field_byes) {
		this.field_byes = field_byes;
	}

	public void setMisFields(int field_misfield) {
		this.field_misfield = field_misfield;
	}

	public void setCatchedDropped(int field_catches_dropped) {
		this.field_catches_dropped = field_catches_dropped;
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

	public int getPerId() {
		return this.per_id;
	}

	public int getInning() {
		return this.inning;
	}

	public int getBatNum() {
		return this.bat_num;
	}

	public int getBatRuns() {
		return this.bat_runs;
	}

	public int getBatBalls() {
		return this.bat_balls;
	}

	public int getBatTime() {
		return this.bat_time;
	}

	public int getBatFours() {
		return this.fours;
	}

	public int getBatSixes() {
		return this.sixes;
	}

	public String getBatDismissal() {
		return this.bat_dismissal;
	}

	public String getBatBowlerType() {
		return this.bat_bowler_type;
	}

	public String getBatFieldingPosition() {
		return this.bat_feilding_position;
	}

	public int getBatChances() {
		return this.bat_chances;
	}

	public int getBat() {
		return this.bowl_balls;
	}

	public int getBowlSpells() {
		return this.bowl_spells;
	}

	public int getBowlMaidens() {
		return this.bowl_maidens;
	}

	public int getBowlRuns() {
		return this.bowl_runs;
	}

	public int getBowlFours() {
		return this.bowl_fours;
	}

	public int getBowlSixes() {
		return this.bowl_sixes;
	}

	public int getBowlWktsLeft() {
		return this.bowl_wkts_left;
	}

	public int getBowlWktsRight() {
		return this.bowl_wkts_right;
	}

	public int getBowlCatchesDropped() {
		return this.bowl_catches_dropped;
	}

	public int getNoBalls() {
		return this.bowl_no_balls;
	}

	public int getBowlWides() {
		return this.bowl_wides;
	}

	public int getFieldSlipCatch() {
		return this.field_slip_catch;
	}

	public int getFieldCloseCatch() {
		return this.field_close_catch;
	}

	public int getFieldCircleCatch() {
		return this.field_circle_catch;
	}

	public int getFieldDeepCatch() {
		return this.field_deep_catch;
	}

	public int getFieldRoCircle() {
		return this.field_ro_circle;
	}

	public int getFieldRoDirectCircle() {
		return this.field_ro_direct_circle;
	}

	public int getFieldRoDeep() {
		return this.field_ro_deep;
	}

	public int getFieldRoDirectDeep() {
		return this.field_ro_direct_deep;
	}

	public int getFieldStumping() {
		return this.field_stumpings;
	}

	public int getFieldByes() {
		return this.field_byes;
	}

	public int getMisFields() {
		return this.field_misfield;
	}

	public int getCatchedDropped() {
		return this.field_catches_dropped;
	}

	public int getStatus() {
		return this.status;
	}
}