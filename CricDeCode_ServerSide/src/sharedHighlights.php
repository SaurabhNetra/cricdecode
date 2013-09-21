<html>
<head>
<link rel="stylesheet" type="text/css" href="main.css">
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
?>
<div id="topbar"></div>
<img id="logo" src="icon.png" />
<div id="appname">CricDeCode</div>
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
	</div>
	<div id="info_l_bottom">
<? echo $lvl." Level"; ?>
</div>
</div>
<div id="info_r1">
	Batting Performance
	<hr id="rule" />
	<div id="per">
<? echo $bat; ?>
</div>
</div>
<div id="info_r2">
	Bowling Performance
	<hr id="rule" />
	<div id="per">
<? echo $bowl; ?>
</div>
</div>
<div id="info_r3">
	Fielding Performance
	<hr id="rule" />
	<div id="per">
<? echo $field; ?>
</div>
</div>
<div id="bottomCard"></div>
</html>