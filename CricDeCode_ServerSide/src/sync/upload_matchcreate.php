<?
include_once "conn.php";
include_once 'GCmSend.php';
include_once 'GcMConsts.php';
include_once 'send_to_ids.php';
$id = $_POST ['id'];
$json_string = $_POST ['json'];
$dev = $_POST ['dev'];
$json = str_replace("\\","",$json_string);
$json_array = json_decode($json, true);
foreach($json_array['matches'] as $row)
{
	$mid=$row['id'];
	$dat=$row['dat'];
	$myt=$row['myt'];
	$opp=$row['opp'];
	$ven=$row['ven'];
	$ovr=$row['ovr'];
	$inn=$row['inn'];
	$res=$row['res'];
	$lvl=$row['lvl'];
	$act=$row['act'];
	$dur=$row['dur'];
	$rev=$row['rev'];
	$sts=$row['sts'];
	$result=mysql_query("SELECT COUNT(user_id) AS c FROM cricket_match WHERE user_id='$id' AND match_id=$mid AND device_id=$dev");
	if($result['c']==0)
	{	$SQL="INSERT INTO cricket_match VALUES('$id',$mid,$dev,'$dat','$myt','$opp','$ven',$ovr,$inn,'$res','$lvl','$act','$dur','$rev',0)";
	mysql_query($SQL);
	}	
}
foreach($json_array['performance'] as $row)
{
	$per=$row['id'];
	$mat=$row['mat'];
	$inn=$row['inn'];
	$bb=$row['bb'];
	$bbt=$row['bbt'];
	$bc=$row['bc'];
	$bfp=$row['bfp'];
	$bf=$row['bf'];
	$bho=$row['bho'];
	$bn=$row['bn'];
	$br=$row['br'];
	$bs=$row['bs'];
	$bt=$row['bt'];
	$ob=$row['ob'];
	$ocd=$row['ocd'];
	$of=$row['of'];
	$om=$row['om'];
	$ono=$row['ono'];
	$oru=$row['oru'];
	$osx=$row['osx'];
	$osp=$row['osp'];
	$ow=$row['ow'];
	$owl=$row['owl'];
	$owr=$row['owr'];
	$fb=$row['fb'];
	$fcd=$row['fcd'];
	$fcc=$row['fcc'];
	$fco=$row['fco'];
	$fdc=$row['fdc'];
	$fci=$row['fci'];
	$fdd=$row['fdd'];
	$fsc=$row['fsc'];
	$fs=$row['fs'];
	$sts=$row['sts'];
	$frd=$row['frd'];
	$frc=$row['frc'];
	$fmf=$row['fmf'];
	$result=mysql_query("SELECT COUNT(user_id) AS c FROM performance WHERE user_id='$id' AND match_id=$mat AND device_id=$dev");
	if($result['c']==0)
	{
		$SQL="INSERT INTO performance VALUES('$id',$mat,$dev,$per,$inn,$bn,$br,$bb,$bt,$bf,$bs,'$bho','$bbt','$bfp',$bc,$ob,$osp,$om,$oru,$of,$osx,$owl,$owr,$ocd,$ono,$ow,$fsc,$fco,$fcc,$fdc,$frc,$fci,$frd,$fdd,$fs,$fb,$fmf,$fcd,0)";
		mysql_query($SQL);
	}
}
foreach($json_array['matches'] as $row)
{
    $mid=$row['id'];
	$dat=$row['dat'];
	$myt=$row['myt'];
	$opp=$row['opp'];
	$ven=$row['ven'];
	$ovr=$row['ovr'];
	$inn=$row['inn'];
	$res=$row['res'];
	$lvl=$row['lvl'];
	$act=$row['act'];
	$dur=$row['dur'];
	$rev=$row['rev'];
	$sts=$row['sts'];
	$pd=null;
	$i=0;
	foreach($json_array['performance'] as $row1)
	{
	if($row1['mat']==$mid)
	{
	$pd[$i]=array("pid"=>$row1['id'],"mid"=>$row1['mat'],"inn"=>$row1['inn'],"bb"=>$row1['bb'],"bbt"=>$row1['bbt'],"bc"=>$row1['bc'],"bfp"=>$row1['bfp'],"bf"=>$row1['bf'],"bho"=>$row1['bho'],"bn"=>$row1['bn'],"br"=>$row1['br'],"bs"=>$row1['bs'],"bt"=>$row1['bt'],"ob"=>$row1['ob'],"ocd"=>$row1['ocd'],"of"=>$row1['of'],"om"=>$row1['om'],"ono"=>$row1['ono'],"oru"=>$row1['oru'],"osx"=>$row1['osx'],"osp"=>$row1['osp'],"ow"=>$row1['ow'],"owl"=>$row1['owl'],"owr"=>$row1['owr'],"fb"=>$row1['fb'],"fcd"=>$row1['fcd'],"fcc"=>$row1['fcc'],"fco"=>$row1['fco'],"fdc"=>$row1['fdc'],"fci"=>$row1['fci'],"fdd"=>$row1['fdd'],"fsc"=>$row1['fsc'],"fs"=>$row1['fs'],"sts"=>$row1['sts'],"frd"=>$row1['frd'],"frc"=>$row1['frc'],"fmf"=>$row1['fmf']);
	$i++;
	}
	}	
$SendMsgArr = array (
		"gcmid" => MATCH_N_PERFORMANCE_DATA,
		"dev" => $dev,
		"mid" => $mid,
		"dat" => $dat,
		"myt" => $myt,
		"opp" => $opp,
		"ven" => $ven,
		"ovr" => $ovr,
		"inn" => $inn,
		"res" => $res,
		"lvl" => $lvl,
		"act" => $act,
		"dur" => $dur,
		"rev" => $rev,
		"sts" => $sts,
		"per" => $pd
		
);
SendGCm ( sendToArr ( $id ), $SendMsgArr, $id );
}
$ax = array (
		"status" => 1
);
echo str_replace ( "\\", "", json_encode ( $ax ) );
?>