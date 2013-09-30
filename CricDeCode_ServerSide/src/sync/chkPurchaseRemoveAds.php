<?php
include_once "conn.php";
include_once "AppConsts.php";
$id = $_POST ['id'];
$json_string = $_POST ['json'];
$json = str_replace ( "\\", "", $json_string );
$json_array = json_decode ( $json, true );
$order_id = $json_array ['orderId'];
$token = $json_array ['Token'];
$sign = $json_array ['Sign'];
$cnt = mysql_query ( "SELECT * FROM remove_ads WHERE order_id = '$order_id' AND id='$id' AND token='$token' AND sign='$sign'" );
if (mysql_num_rows ( $cnt ) == 0) {
	$ax = array (
			"status" => 1 
	);
} else {
	$ax = array (
			"status" => 0
	);
}
echo str_replace ( "\\", "", json_encode ( $ax ) );
?>