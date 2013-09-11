<?
include_once "conn.php";
include_once 'GCmSend.php';
include_once 'GcMConsts.php';
include_once 'send_to_ids.php';
$id = $_POST ['id'];
$json = $_POST ['json'];
$device_id = $_POST ['dev'];



{
	$SendMsgArr = array (
			"gcmid" => MATCH_N_PERFORMANCE_DATA,
			"dev" => $device_id,
			"mat_per" => $mat_per 
	);
	SendGCm ( sendToArr ( $id ), $SendMsgArr, $id );
}
$ax = array (
		"status" => 1 
);
echo str_replace ( "\\", "", json_encode ( $ax ) );
?>