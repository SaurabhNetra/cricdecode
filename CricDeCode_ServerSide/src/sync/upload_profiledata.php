<?
include_once "conn.php";
include_once 'GCmSend.php';
include_once 'GcMConsts.php';
include_once 'send_to_ids.php';
$id = $_POST ['id'];
$nickname = $_POST ['nickname'];
$battingStyle = $_POST ['battingStyle'];
$bowlingStyle = $_POST ['bowlingStyle'];
$role = $_POST ['role'];
$result = mysql_query ( "SELECT id FROM user_table WHERE id='$id'");
if (mysql_num_rows ( $result ) == 1) {
	mysql_query ( "UPDATE user_table SET nickname='$nickname', battingStyle='$battingStyle', bowlingStyle='$bowlingStyle',role='$role' WHERE id='$id'" );
	$SendMsgArr = array (
			"gcmid" => UPDATE_PROFILE_DATA,
			"nickname" => $nickname,
			"battingStyle" => $battingStyle,
			"bowlingStyle" => $bowlingStyle,
			"role" => $role 
	);
	SendGCm ( sendToArr ( $id ), $SendMsgArr, $id );
	$ax = array (
			"status" => 1 
	);
}
echo str_replace ( "\\", "", json_encode ( $ax ) );
?>