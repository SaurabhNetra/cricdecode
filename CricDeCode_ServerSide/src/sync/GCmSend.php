<?
include_once "conn.php";
include_once "getAPIkEY.php";
include_once 'get_Consts.php';
function SendGCm($SendToArr, $SendMsgArr, $id) {
	$apiKey = API_KEY;
	$appName = APP_NAME;
	$urlToSend = "http://hansaacademy.org/api/GCmSend.php";
	$fields = json_encode ( array (
			'data' => array (
					$appName => json_encode ( $SendMsgArr ) 
			),
			'registration_ids' => $SendToArr 
	) );
	$postdata = http_build_query ( array (
			'fields' => $fields,
			'apiKey' => $apiKey 
	) );
	$opts = array (
			'http' => array (
					'method' => 'POST',
					'header' => 'Content-type: application/x-www-form-urlencoded',
					'content' => $postdata 
			) 
	);
	$context = stream_context_create ( $opts );
	$result = file_get_contents ( $urlToSend, false, $context );
	$gcmj = json_decode ( $result );
	for($i = 0; $i < count ( $gcmj->results ); $i ++) {
		if (isset ( $gcmj->results [$i]->error )) {
			if ($gcmj->results [$i]->error == "NotRegistered") {
				$regiderr = $SendToArr [$i];
				mysql_query ( "DELETE FROM " . TABLE_NAME . " WHERE " . GCM_ID_COLUMN_NAME . " = '$regiderr' AND " . USER_ID_COLUMN_NAME . " = '$id'" );
			}
		} elseif (isset ( $gcmj->results [$i]->registration_id )) {
			$newreg = $gcmj->results [$i]->registration_id;
			$regiderr = $SendToArr [$i];
			$result = mysql_query ( "SELECT * FROM " . TABLE_NAME . " WHERE " . GCM_ID_COLUMN_NAME . " = '$newreg' AND " . USER_ID_COLUMN_NAME . " = '$id'" );
			if (mysql_num_rows ( $result ) == 0) {
				mysql_query ( "UPDATE " . TABLE_NAME . " SET " . GCM_ID_COLUMN_NAME . " = '$newreg' WHERE " . GCM_ID_COLUMN_NAME . " = '$regiderr' AND " . USER_ID_COLUMN_NAME . " = '$id'" );
			} else {
				mysql_query ( "DELETE FROM " . TABLE_NAME . " WHERE " . GCM_ID_COLUMN_NAME . " = '$regiderr' AND " . USER_ID_COLUMN_NAME . " = '$id'" );
			}
		}
	}
}
?>