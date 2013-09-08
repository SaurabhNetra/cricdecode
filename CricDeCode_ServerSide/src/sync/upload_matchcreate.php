<?
include_once "conn.php";
include_once 'GCmSend.php';
include_once 'GcMConsts.php';
try {
	$id = $_POST ['id'];
	$match_id = $_POST ['match_id'];
	$match_date = $_POST ['match_date'];
	$my_team = $_POST ['my_team'];
	$opponent_team = $_POST ['opponent_team'];
	$venue = $_POST ['venue'];
	$overs = $_POST ['overs'];
	$innings = $_POST ['innings'];
	$match_result = $_POST ['match_result'];
	$level = $_POST ['level'];
	$first_action = $_POST ['first_action'];
	$duration = $_POST ['duration'];
	$review = $_POST ['review'];
	$status = $_POST ['status'];
	
	$SendMsgArr = array (
			"gcmid" => MATCH_CREATE_DATA,
			"nickname" => $nickname,
			"battingStyle" => $battingStyle,
			"bowlingStyle" => $bowlingStyle,
			"role" => $role 
	);
	SendGCm ( $SendToArr, $SendMsgArr, $id );
	$ax = array (
			"status" => 1 
	);
} catch ( Exception $e ) {
	$ax = array (
			"status" => 0 
	);
}
echo str_replace ( "\\", "", json_encode ( $ax ) );
?>