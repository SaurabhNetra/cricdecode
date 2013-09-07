<?php
define ( 'SQL_HOST', 'localhost' );
define ( 'SQL_USER', 'acjscwl5_cricUsr' );
define ( 'SQL_PASS', 'JbZZ^K0])G+wvZb8=X' );
define ( 'SQL_DB', 'acjscwl5_cric' );
$conn = mysql_connect ( SQL_HOST, SQL_USER, SQL_PASS ) or die ( 'Could not connect to the database; ' . mysql_error () );
mysql_select_db ( SQL_DB, $conn ) or die ( 'Could not select database; ' . mysql_error () );
?>