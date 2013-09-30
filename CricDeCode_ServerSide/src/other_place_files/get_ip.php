<?
$externalContent = file_get_contents('http://checkip.dyndns.com/');
preg_match('/Current IP Address: ([\[\]:.[0-9a-fA-F]+)</', $externalContent, $m);
$externalIp = $m[1];
echo $externalIp."<br><br><br><br>";
$externalIp = file_get_contents('http://phihag.de/ip/');
echo $externalIp."<br><br><br><br>";
// Pull contents from ip6.me
$file = file_get_contents('http://ip6.me/');

// Trim IP based on HTML formatting
$pos = strpos( $file, '+3' ) + 3;
$ip = substr( $file, $pos, strlen( $file ) );

// Trim IP based on HTML formatting
$pos = strpos( $ip, '</' );
$ip = substr( $ip, 0, $pos );

// Output the IP address of your box
echo "My IP address is $ip";

// Debug only -- all lines following can be removed
echo "\r\n<br/>\r\n<br/>Full results from ip6.me:\r\n<br/>";
echo $file."<br><br><br><br>";
$externalIp = file_get_contents('www.whatismyip.com/');
echo $externalIp."<br><br><br><br>";
$externalIp = file_get_contents('whatismyipaddress.com/');
echo $externalIp."<br><br><br><br>";
?>