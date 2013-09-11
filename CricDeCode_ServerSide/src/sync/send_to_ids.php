<?php
include_once 'conn.php';
include_once 'get_Consts.php';
function sendToArr($id) {
	$result = mysql_query ( "SELECT " . GCM_ID_COLUMN_NAME . " FROM " . TABLE_NAME . " WHERE id='$id'" );
	$SendToArr = null;
	$temp = null;
	for($i = 0; $i < mysql_num_rows ( $result ); $i ++) {
		$temp [$i] = mysql_fetch_array ( $result );
	}
	for($i = 0; $i < mysql_num_rows ( $result ); $i ++) {
		$SendToArr [$i] = $temp [$i] ['gcm_id'];
	}
	return $SendToArr;
}
?>