<?php
$link = mysql_connect('localhost:3307', 'nkvabkuh_fc', 'N7(F#3@m3h!w');

if (!$link) {
    die('Could not connect: ' . mysql_error());
}
//echo 'Connected successfully\n';
mysql_select_db('nkvabkuh_fc', $link);


//http://localhost:8888/?user=kvilliers&lat=-37.23&long=142.34&biz=h3ffh093h&friend=leis2leis

$sql = "INSERT INTO `fc`.`loc` (`userid`, `lat`, `long`, `businessid`, `friend`,".
"`time`) VALUES ('".$_GET["user"]."', '".$_GET["lat"]."', '".$_GET["long"].
"', '".$_GET["biz"]."', '".$_GET["friend"]."', CURRENT_TIMESTAMP)";

$result = mysql_query($sql,$link);


//echo $result;

$output = "SELECT  `userid` ,  `lat` ,  `long` ,  `businessid` ,
`friend` , UNIX_TIMESTAMP(MAX(  `time` )) as `time` 
FROM  `loc` 
GROUP BY `userid`";

//echo $output."\n";

$result = mysql_query($output,$link);

if (!$result) {
    $message  = 'Invalid query: ' . mysql_error() . "\n";
    $message .= 'Whole query: ' . $query;
    die($message);
}

$arrayToBeEncoded = array();
$i = 0;
while ($row = mysql_fetch_assoc($result)) {
	$array = array();

	$array['userid'] = $row['userid'];
	$array['lat'] = $row['lat'];
	$array['long'] = $row['long'];
	$array['businessid'] = $row['businessid'];
	$array['friend'] = $row['friend'];
	$array['time'] = $row['time'];
	
	/*
    echo $row['userid'];
    echo $row['lat'];
    echo $row['long'];
    echo $row['businessid'];
    echo $row['friend'];
    echo $row['time'];
    
    echo "\n";
    echo $array;
    */
    $arrayToBeEncoded[$i++] = $array;
}

echo json_encode($arrayToBeEncoded);
mysql_close($link);
?>