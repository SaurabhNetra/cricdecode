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
$url_to_send = "https://www.googleapis.com/androidpublisher/v1.1/applications/" . PACKAGE_NAME. "/inapp/" . PRODUCT_ID_ad_removal. "/purchases/" . $token;
$result = file_get_contents ( $urlToSend, false, null );
$reply=json_decode($result,true);
$purchase_time=$reply['purchaseTime'];
mysql_query("INSERT INTO remove_ads VALUES('$id','$order_id','$token','$sign',$purchase_time)");
?>