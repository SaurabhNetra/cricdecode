<?
include_once "conn.php";
include_once 'GCmSend.php';
include_once 'GcMConsts.php';
include_once 'send_to_ids.php';
$id = $_POST ['id'];
$json_string = $_POST ['json'];
$device_id = $_POST ['dev'];
$json = str_replace ( "\\", "", json_encode ( $json_string));
$json_matches=json_decode($json['matches'],true);
$json_performance=json_decode($json['performance'],true);
for($i = 0; $i < count ( $json_matches->$json['matches'] ); $i++) {
	$match_id=$json_matches->$json['matches'][$i]->id;
	$date=$json_matches->$json['matches'][$i]->dat;
	$my_team=$json_matches->$json['matches'][$i]->myt;
	$opp=$json_matches->$json['matches'][$i]->opp;
	$ven=$json_matches->$json['matches'][$i]->ven;
	$ovr=$json_matches->$json['matches'][$i]->ovr;
	$inn=$json_matches->$json['matches'][$i]->inn;
	$res=$json_matches->$json['matches'][$i]->res;
	$lvl=$json_matches->$json['matches'][$i]->lvl;
	$first=$json_matches->$json['matches'][$i]->act;
	$dur=$json_matches->$json['matches'][$i]->dur;
	$rev=$json_matches->$json['matches'][$i]->rev;
	$sts=$json_matches->$json['matches'][$i]->sts;
	mysql_query("INSERT INTO cricket_match VALUES('$id',$match_id,$device_id,'$date','$my_team','$opp','$ven',$ovr,$inn,'$res','$lvl','$first','$dur','$rev',$sts)");
}

for($i = 0; $i < count ( $json_performance->$json['performance'] ); $i++) {
	$perid=$json_performance->$json['performance'][$i]->id;
	$mat=$json_performance->$json['performance'][$i]->mat;
	$inn=$json_performance->$json['performance'][$i]->inn;
	$bb=$json_performance->$json['performance'][$i]->bb;
	$bbt=$json_performance->$json['performance'][$i]->bbt;
	$bc=$json_performance->$json['performance'][$i]->bc;
	$bfp=$json_performance->$json['performance'][$i]->bfp;
	$bf=$json_performance->$json['performance'][$i]->bf;
	$bho=$json_performance->$json['performance'][$i]->bho;
	$bn=$json_performance->$json['performance'][$i]->bn;
	$br=$json_performance->$json['performance'][$i]->br;
	$bs=$json_performance->$json['performance'][$i]->bs;
	$bt=$json_performance->$json['performance'][$i]->bt;
	
	$ob=$json_performance->$json['performance'][$i]->ob;
	$ocd=$json_performance->$json['performance'][$i]->ocd;
	$of=$json_performance->$json['performance'][$i]->of;
	$om=$json_performance->$json['performance'][$i]->om;
	$ono=$json_performance->$json['performance'][$i]->ono;
	$oru=$json_performance->$json['performance'][$i]->oru;
	$osx=$json_performance->$json['performance'][$i]->osx;
	$osp=$json_performance->$json['performance'][$i]->osp;
	$ow=$json_performance->$json['performance'][$i]->ow;
	$owl=$json_performance->$json['performance'][$i]->owl;
	$owr=$json_performance->$json['performance'][$i]->owr;

	$fb=$json_performance->$json['performance'][$i]->fb;
	$fcd=$json_performance->$json['performance'][$i]->fcd;
	$fcc=$json_performance->$json['performance'][$i]->fcc;
	$fco=$json_performance->$json['performance'][$i]->fco;
	$fdc=$json_performance->$json['performance'][$i]->fdc;
	$fci=$json_performance->$json['performance'][$i]->fci;
	$fdd=$json_performance->$json['performance'][$i]->fdd;
	$fsc=$json_performance->$json['performance'][$i]->fsc;
	$fs=$json_performance->$json['performance'][$i]->fs;
	$sts=$json_performance->$json['performance'][$i]->sts;
	$frd=$json_performance->$json['performance'][$i]->frd;
	$frc=$json_performance->$json['performance'][$i]->frc;
	
	mysql_query("INSERT INTO performance VALUES('$id',$mat,$device_id,$perid,$inn,$bn,$br,$bb, $bt,$bf,$bs,'$bho','$bbt','$bfp',$bc,$ob,$osp,$om,$oru,$of,$osx,$owl,$owr,$ocd,$ono,$ow,$fsc,$fco,$fcc,$fdc,$frc,$fci,$frd,$fdd,$fs,$fb,,$fcd,)");
}



{
	$SendMsgArr = array (
			"gcmid" => MATCH_N_PERFORMANCE_DATA,
			"dev" => $device_id,
			"mat_per" => $mat_per
	);
	SendGCm ( sendToArr ( $id ), $SendMsgArr, $id );
}
$ax = array (
		"status" => 1
);
echo str_replace ( "\\", "", json_encode ( $ax ) );
?>