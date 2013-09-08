<?
include_once "conn.php";
include_once "mainGCM.php";
function SendGCm($SendToArr, $SendMsgArr) {
	$result = mainGCM ( $SendToArr, $SendMsgArr );
	$gcmj = json_decode ( $result );
	for($i = 0; $i < count ( $gcmj->results ); $i ++) {
		$eror = $gcmj->results [$i]->error;
		if ($eror == "InvalidRegistration" or $eror == "NotRegistered" or $eror == "MissingRegistration") {
			$regiderr = $SendToArr [$i];
			$SQL = "DELETE FROM user_android_devices WHERE gcm_id = '$regiderr'";
			mysql_query ( $SQL );
		}
		if (isset ( $gcmj->results [$i]->registration_id )) {
			$newreg = $gcmj->results [$i]->registration_id;
			$regiderr = $SendToArr [$i];
			$SQL = "UPDATE user_android_devices SET gcm_id = '$newreg' WHERE gcm_id = '$regiderr'";
			mysql_query ( $SQL );
		}
	}
}
?>