<?
include_once "conn.php";
include_once 'GCmSend.php';
include_once 'GcMConsts.php';
try {
	$id = $_POST ['id'];
	$nickname = $_POST ['nickname'];
	$battingStyle = $_POST ['battingStyle'];
	$bowlingStyle = $_POST ['bowlingStyle'];
	$role = $_POST ['role'];
	$result = mysql_query ( "SELECT id FROM user_table WHERE id='$id'" );
	if (mysql_num_rows ( $result ) == 1) {
		$res = mysql_query ( "UPDATE user_table SET nickname='$nickname', battingStyle='$battingStyle', bowlingStyle='$bowlingStyle',role='$role' WHERE id='$id'" );
	}
	$ax = array (
			"status" => 1 
	);
	$result = mysql_query ( "SELECT gcm_id FROM user_android_devices WHERE id='$id'" );
	$SendToArr = "";
	for($i = 0; $i < mysql_num_rows ( $result ); $i ++) {
		$temp = mysql_fetch_array ( $result );
	}
	for($i = 0; $i < mysql_num_rows ( $result ); $i ++) {
		$SendToArr [$i] = $temp [$i] ['gcm_id'];
	}
	$SendMsgArr = array (
			"gcmid" => UPDATE_PROFILE_DATA,
			"nickname" => $nickname,
			"battingStyle" => $battingStyle,
			"bowlingStyle" => $bowlingStyle,
			"role" => $role 
	);
	SendGCm ( $SendToArr, $SendMsgArr );
} catch ( Exception $e ) {
	$ax = array (
			"status" => 0 
	);
}
echo str_replace ( "\\", "", json_encode ( $ax ) );
?>