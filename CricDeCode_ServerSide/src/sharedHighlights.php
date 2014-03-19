<html>
<script language="javascript">(function(d, s, id) {
	var js, fjs = d.getElementsByTagName(s)[0];
	if (d.getElementById(id)) return;
	js = d.createElement(s); js.id = id;
	js.src = "//connect.facebook.net/en_GB/all.js#xfbml=1";
	fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));
!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");
(function() {
	var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
	po.src = 'https://apis.google.com/js/plusone.js';
	var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
})();
!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
<head>
<link rel="stylesheet" type="text/css" href="main.css">
<script type="text/javascript" src="GlAna.js"></script>
</head>
<?php
$fn = $_GET ['fn'];
$ln = $_GET ['ln'];
$ta = $_GET ['ta'];
$tb = $_GET ['tb'];
$v = $_GET ['v'];
$lvl = $_GET ['lvl'];
$bat = $_GET ['bat'];
$bowl = $_GET ['bowl'];
$field = $_GET ['field'];
if (($bat == "") && ($bowl == "") && ($field == "")) {
	echo '<script language="javascript">parent.location="index.html"</script>';
} else {
	?>
<div id="topbar"></div>
<img id="logo" src="icon.png" />
<div id="appname">CricDeCode</div>
<a id="getApp" href="https://play.google.com/store/apps/details?id=co.acjs.cricdecode">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>
<div id="info_l">
	<div id="info_l_top">
<? echo $fn.'<br />'; ?>
<? echo '<b>'.$ln.'</b><br />'; ?>
<hr id="rule" />
	</div>
	<div id="info_l_mid">
<? echo $ta.'<br />'; ?>
<div style="font-size: 0.7em;">vs</div>
<? echo $tb.'<br />'; ?>
<div style="font-size: 0.7em; margin-top: 2px;"><? echo $v.'<br />'; ?></div>
		<div style="font-size: 0.7em; padding-top: 10px;">
<? echo $lvl." Level"; ?>
</div>
	</div>
</div>
<div id="info_r1">
	Batting Performance
	<hr id="rule" />
	<div id="per">
<?
	if ($bat == "") {
		echo "No data available";
	} else {
		echo $bat;
	}
	?>
</div>
</div>
<div id="info_r2">
	Bowling Performance
	<hr id="rule" />
	<div id="per">
<?
	if ($bowl == "") {
		echo "No data available";
	} else {
		echo $bowl;
	}
	?>
</div>
</div>
<div id="info_r3">
	Fielding Performance
	<hr id="rule" />
	<div id="per">

<?
	if ($field == "") {
		echo "No data available";
	} else {
		echo $field;
	}
}
?>
</div>
</div>
<div id="bottomCard">CricDeCode provides a personal, local and easy to
	maintain data store with intuitive statistics generation. CricDeCode
	requires the cricketer to feed his match performances. CricDeCode then
	provides speedy access to detailed career and statistics generation and
	intuitive graphical views, enabling effective personal performance
	profiling. Your data is secured with cloud backup and inter device data
	syncing. CricDeCode also provides a platform to post the highlights of
	your performances on Facebook.</div>
<div id="menuStrip"><a href="http://cdc.acjs.co/index.html">Home</a><br /><br /><a href="http://cdc.acjs.co/privacy.html">Privacy</a><br /><br /><a href="http://cdc.acjs.co/terms_of_service.html">Terms</a><br /><br /><a href="http://cdc.acjs.co/support.html">Support</a></div>
<div style="position:absolute; left: 50px; top: 220px; ">
<div class="fb-like" data-href="https://www.facebook.com/CricDeCode" data-send="true" data-layout="button_count" data-width="34" data-show-faces="true" data-font="trebuchet ms"></div></div>
</body>
</html>
