<?
include_once "conn.php";
$id = "";
$gcmid = "";
$fname = "";
$lname = "";
$dob = "";
$fblink = "";
if (isset ( $_POST ['id'] )) {
	$id = $_POST ['id'];
}
if (isset ( $_POST ['gcmid'] )) {
	$gcmid = $_POST ['gcmid'];
}
if (isset ( $_POST ['fname'] )) {
	$fname = $_POST ['fname'];
}
if (isset ( $_POST ['lname'] )) {
	$lname = $_POST ['lname'];
}
if (isset ( $_POST ['dob'] )) {
	$dob = $_POST ['dob'];
}
if (isset ( $_POST ['fblink'] )) {
	$fblink = $_POST ['fblink'];
}
echo "1. " . $id . " " . $gcmid . " " . $fname . " " . $lname . " " . $dob . " " . $fblink;
$result = mysql_query ( "SELECT * FROM user_table WHERE id='$id'" );
if (mysql_num_rows ( $result ) == 0) {
	mysql_query ( "INSERT INTO user_table values('$id','$fname','$lname','$fblink','$dob',1)" );
	$tday = date ( "F j, Y, g:i a" );
	// assuming user has android
	mysql_query ( "INSERT INTO user_android_devices values('$id','$gcmid','$tday')" );
	$ax = array (
			"status" => 1 
	);
	echo str_replace ( "\\", "", json_encode ( $ax ) );
} else {
	// uthao uska data and stuff json
	$result = mysql_query ( "SELECT * FROM cricket_match WHERE user_id='$id' AND status=0" );
	if (mysql_num_rows ( $result ) != 0) {
		print "2. " . mysql_num_rows ( $result );
		for($i = 0; $i < mysql_num_rows ( $result ); $i ++) {
			$cricket_match = mysql_fetch_array ( $result );
		}
		$cricket_match_data = "";
		for($i = 0; $i < mysql_num_rows ( $result ); $i ++) {
			$row = "";
			$row = array (
					"user_id" => $cricket_match [$i] ['user_id'],
					"match_id" => $cricket_match [$i] ['match_id'],
					"match_date" => $cricket_match [$i] ['match_date'],
					"my_team" => $cricket_match [$i] ['my_team'],
					"opponent_team" => $cricket_match [$i] ['opponent_team'],
					"venue" => $cricket_match [$i] ['venue'],
					"overs" => $cricket_match [$i] ['overs'],
					"innings" => $cricket_match [$i] ['innings'],
					"result" => $cricket_match [$i] ['result'],
					"level" => $cricket_match [$i] ['level'],
					"first_action" => $cricket_match [$i] ['first_action'],
					"duration" => $cricket_match [$i] ['duration'],
					"review" => $cricket_match [$i] ['review'] 
			);
			$cricket_match_data [$i] = $row;
		}
		$result1 = mysql_query ( "SELECT * FROM performance WHERE user_id='$id' AND status=0" );
		for($i = 0; $i < mysql_num_rows ( $result1 ); $i ++) {
			$performance = mysql_fetch_array ( $result1 );
		}
		for($i = 0; $i < mysql_num_rows ( $result1 ); $i ++) {
			$row = "";
			$row = array (
					"user_id" => $performance [$i] ['user_id'],
					"match_id" => $performance [$i] ['match_id'],
					"performance_id" => $performance [$i] ['performance_id'],
					"inning" => $performance [$i] ['inning'],
					"bat_num" => $performance [$i] ['bat_num'],
					"bat_runs" => $performance [$i] ['bat_runs'],
					"bat_balls" => $performance [$i] ['bat_balls'],
					"bat_time" => $performance [$i] ['bat_time'],
					"fours" => $performance [$i] ['fours'],
					"sixes" => $performance [$i] ['sixes'],
					"bat_dismissal" => $performance [$i] ['bat_dismissal'],
					"bat_bowler_type" => $performance [$i] ['bat_bowler_type'],
					"bat_fielding_position" => $performance [$i] ['bat_fielding_position'],
					"bat_chances" => $performance [$i] ['bat_chances'],
					"bowl_balls" => $performance [$i] ['bowl_balls'],
					"bowl_spells" => $performance [$i] ['bowl_spells'],
					"bowl_maidens" => $performance [$i] ['bowl_maidens'],
					"bowl_sixes" => $performance [$i] ['bowl_sixes'],
					"bowl_fours" => $performance [$i] ['bowl_fours'],
					"user_id" => $performance [$i] ['user_id'],
					"bowl_wkts_left" => $performance [$i] ['bowl_wkts_left'],
					"bowl_wkts_right" => $performance [$i] ['bowl_wkts_right'],
					"bowl_catches_dropped" => $performance [$i] ['bowl_catches_dropped'],
					"bowl_no_balls" => $performance [$i] ['bowl_no_balls'],
					"bowl_wides" => $performance [$i] ['bowl_wides'],
					"field_slip_catch" => $performance [$i] ['field_slip_catch'],
					"field_close_catch" => $performance [$i] ['field_close_catch'],
					"field_ro_deep" => $performance [$i] ['field_circle_catch'],
					"field_deep_catch" => $performance [$i] ['field_deep_catch'],
					"field_ro_circle" => $performance [$i] ['field_ro_circle'],
					"field_ro_direct_circle" => $performance [$i] ['field_ro_direct_circle'],
					"field_ro_deep" => $performance [$i] ['field_ro_deep'],
					"field_ro_direct_deep" => $performance [$i] ['field_ro_direct_deep'],
					"field_stumpings" => $performance [$i] ['field_stumpings'],
					"field_byes" => $performance [$i] ['field_byes'],
					"field_misfields" => $performance [$i] ['field_misfields'],
					"field_catches_dropped" => $performance [$i] ['field_catches_dropped'] 
			);
			$performance_data [$i] = $row;
		}
		$ax = array (
				"performance" => $performance_data,
				"cricket_match_data" => $cricket_match_data,
				"status" => 2 
		);
		echo str_replace ( "\\", "", json_encode ( $ax ) );
	}
}
?>