<?php
include_once "conn.php";
include_once 'GCmSend.php';
include_once 'GcMConsts.php';
include_once 'send_to_ids.php';
$id = $_POST ['id'];
$json_string = $_POST ['json'];
$json = str_replace ( "\\", "", $json_string );
$json_array = json_decode ( $json, true );
$res = mysql_query ( "SELECT * FROM sub_infi_sync WHERE id='$id'" );
if (mysql_num_rows ( $res ) > 0) {
	$i = 0;
	$delete_these = null;
	foreach ( $json_array ['deleted'] as $row ) {
		$mid = $row ['mid'];
		$dev = $row ['dev'];
		$SQL = "UPDATE cricket_match SET status=2 WHERE user_id='$id' AND match_id=$mid AND device_id=$dev";
		mysql_query ( $SQL );
		$SQL = "UPDATE performance SET status=2 WHERE user_id='$id' AND match_id=$mid AND device_id=$dev";
		mysql_query ( $SQL );
		$delete_these [$i] = array (
				"mid" => $mid,
				"dev" => $dev 
		);
		$i ++;
	}
	$SendMsgArr = array (
			"gcmid" => DELETE_MATCH,
			"todelete" => $delete_these 
	);
	SendGCm ( sendToArr ( $id ), $SendMsgArr, $id );
}
$ax = array (
		"status" => 1 
);
echo str_replace ( "\\", "", json_encode ( $ax ) );
?>