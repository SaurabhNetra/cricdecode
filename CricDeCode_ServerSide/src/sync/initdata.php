<?
include_once "conn.php";
	$existing = false;
	$id = $_POST ['id'];
	$gcmid = $_POST ['gcmid'];
	$fname = $_POST ['fname'];
	$lname = $_POST ['lname'];
	$dob = date ( 'Y-m-d', strtotime ( $_POST ['dob'] ) );
	$fblink = $_POST ['fblink'];
	if ($_POST ['android'] == "1")
		$android = 1;
	else
		$android = 0;
	$result = mysql_query ( "SELECT COUNT(*) AS c FROM user_table WHERE id='$id'" );
	$count=mysql_fetch_assoc($result);
	if ($count ['c'] == 0) {
		mysql_query ( "INSERT INTO user_table(id, first_name, last_name, fb_link, dob, has_android) values('$id','$fname','$lname','$fblink','$dob',$android)" );
	} else {
		$existing = true;
		$result = mysql_query ( "SELECT * FROM cricket_match WHERE user_id='$id' AND status<2" );
		$cricket_match_data = "";
		$performance_data = "";
		if (mysql_num_rows ( $result ) != 0) {
			for($i = 0; $i < mysql_num_rows ( $result ); $i ++) {
				$cricket_match[$i] = mysql_fetch_array ( $result );
			}
			for($i = 0; $i < mysql_num_rows ( $result ); $i ++) {
				$row = "";
				$row = array (
						"match_id" => $cricket_match [$i] ['match_id'],
						"device_id" => $cricket_match [$i] ['device_id'],
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
						"review" => $cricket_match [$i] ['review'],
						"status" => $cricket_match [$i] ['status'] 
				);
				$cricket_match_data [$i] = $row;
			}
			$result1 = mysql_query ( "SELECT * FROM performance WHERE user_id='$id' AND status<2" );
			for($i = 0; $i < mysql_num_rows ( $result1 ); $i ++) {
				$performance[$i] = mysql_fetch_array ( $result1 );
			}
			for($i = 0; $i < mysql_num_rows ( $result1 ); $i ++) {
				$row = "";
				$row = array (
						"match_id" => $performance [$i] ['match_id'],
						"device_id" => $performance [$i] ['device_id'],
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
						"bowl_runs" => $performance [$i] ['bowl_runs'],
						"bowl_fours" => $performance [$i] ['bowl_fours'],
						"bowl_sixes" => $performance [$i] ['bowl_sixes'],
						"bowl_wkts_left" => $performance [$i] ['bowl_wkts_left'],
						"bowl_wkts_right" => $performance [$i] ['bowl_wkts_right'],
						"bowl_catches_dropped" => $performance [$i] ['bowl_catches_dropped'],
						"bowl_no_balls" => $performance [$i] ['bowl_no_balls'],
						"bowl_wides" => $performance [$i] ['bowl_wides'],
						"field_slip_catch" => $performance [$i] ['field_slip_catch'],
						"field_close_catch" => $performance [$i] ['field_close_catch'],
						"field_circle_catch" => $performance [$i] ['field_circle_catch'],
						"field_deep_catch" => $performance [$i] ['field_deep_catch'],
						"field_ro_circle" => $performance [$i] ['field_ro_circle'],
						"field_ro_direct_circle" => $performance [$i] ['field_ro_direct_circle'],
						"field_ro_deep" => $performance [$i] ['field_ro_deep'],
						"field_ro_direct_deep" => $performance [$i] ['field_ro_direct_deep'],
						"field_stumpings" => $performance [$i] ['field_stumpings'],
						"field_byes" => $performance [$i] ['field_byes'],
						"field_misfield" => $performance [$i] ['field_misfield'],
						"field_catches_dropped" => $performance [$i] ['field_catches_dropped'],
						"status" => $performance [$i] ['status'] 
				);
				$performance_data [$i] = $row;
			}
		}
		$user_info = mysql_query ( "SELECT * FROM user_table WHERE id='$id'" );
		$user_array = mysql_fetch_assoc($user_info);
	}
	if ($android) {
		$result = mysql_query ( "SELECT * FROM user_android_devices WHERE id='$id' AND gcm_id='$gcmid'" );
		if (mysql_num_rows ( $result ) == 0) {
			date_default_timezone_set ( 'Asia/Kolkata' );
			$tday = date ( "d-m-Y, H:i:s" );
			mysql_query ( "INSERT INTO user_android_devices values('$id','$gcmid','$tday')" );
			$device_no_temp = mysql_query ( "SELECT device_no FROM user_table WHERE id='$id'" );
			$device_no = mysql_fetch_array ( $device_no_temp );
			$new_no = $device_no ['device_no'] + 1;
			mysql_query ( "UPDATE user_table SET device_no = $new_no" );
		}
		if ($existing) {
			$ax = array (
					"user" => "existing",
					"nickname" => $user_array ['nickname'],
					"role" => $user_array ['role'],
					"battingStyle" => $user_array ['battingStyle'],
					"bowlingStyle" => $user_array ['bowlingStyle'],
					"performance_data" => $performance_data,
					"cricket_match_data" => $cricket_match_data,
					"device_id" => $new_no 
			);
		} else {
			$ax = array (
					"user" => "new"
			);
		}
	} else {
		if ($existing) {
			$ax = array (
					"user" => "existing",
					"nickname" => $user_array ['nickname'],
					"role" => $user_array ['role'],
					"battingStyle" => $user_array ['battingStyle'],
					"bowlingStyle" => $user_array ['bowlingStyle'],
					"performance_data" => $performance_data,
					"cricket_match_data" => $cricket_match_data 
			);
		} else {
			$ax = array (
					"user" => "new" 
			);
		}
	}
	echo str_replace ( "\\", "", json_encode ( $ax ) );
?>