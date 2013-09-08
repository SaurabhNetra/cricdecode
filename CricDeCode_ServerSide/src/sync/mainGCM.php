<?
include_once "getAPIkEY.php";
function mainGCM($SendToArr, $SendMsgArr) {
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
	return file_get_contents ( $urlToSend, false, $context );
}
?>