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
$url_to_send = "https://www.googleapis.com/androidpublisher/v1/applications/".PACKAGE_NAME."/subscriptions/".PRODUCT_ID_sub_infi_sync."/purchases/".$token;
$result = file_get_contents ( $urlToSend, false, null );
$reply=json_decode($result,true);
$init_ts=$reply['initiationTimestampMsec'];
$valid_until_ts=$reply['validUntilTimestampMsec'];
mysql_query("INSERT INTO sub_infi_sync VALUES('$id','$order_id','$token','$sign',$init_ts,$valid_until_ts)");
?>