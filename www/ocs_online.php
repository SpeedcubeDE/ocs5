<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css">

* {margin:0px;padding:0px;}
BODY  {font-family:Arial, Helvetica, sans-serif; font-size:10px; background-color: #000000;}
#main { text-align:center; background-color:#2c2c2c; width:466px; height:25px; padding:0px; border:1px solid #444444; margin-top:0px; color:#FFFFFF}
A { color:#FFFF66; text-decoration:none; }
A:hover { text-decoration:underline; }

</style>
<title>OCS Online-Tafel</title>
</head>
<body>
<div id="main">

<table style="padding; 0; border-spacing: 0; width: 458px;"><tr>

<td style="padding:0px; padding-left:4px;" valign="top" align="left" width="66" rowspan="2"><img border="0" alt="OCS" src="img/logo.png" /></td>

<td style="padding-left:4px; text-align:left; padding:0px;"><?php

require_once("includes/rgb2hsv.php");

$online = true;

if (!$online) {
	die("Das OCS wurde vorübergehend deaktiviert!");
}

$json = file_get_contents("online.json");
$json = json_decode($json);
$users = $json->users;

function rgb2hex($str, $offset) {
	return hexdec(substr($str, $offset, 2));
}

usort($users, function($a, $b) {
	$hsv1 = RGB_TO_HSV(rgb2hex($a->namecolor, 0), rgb2hex($a->namecolor, 2), rgb2hex($a->namecolor, 4));
	$hsv2 = RGB_TO_HSV(rgb2hex($b->namecolor, 0), rgb2hex($b->namecolor, 2), rgb2hex($b->namecolor, 4));
	return 255 * ($hsv1["H"] - $hsv2["H"]);
	//hexdec(substr($a->namecolor, 0, 2));
});

$num = count($users);

if ($num == 1) {
	?>
Im <a href="//ocs.speedcube.de/" target="_blank">OCS</a> (online-cubing-system) ist <b>1</b> User online:
	<?php
} elseif ($num == 0) {
	?>
Im <a href="//ocs.speedcube.de/" target="_blank">OCS</a> (online-cubing-system) sind <b>keine</b> User online.
	<?php
} else {
	?>
Im <a href="//ocs.speedcube.de/" target="_blank">OCS</a> (online-cubing-system) sind <b><?php echo $num; ?></b> User online, u.a. Folgende:
	<?php
}

?> (<a target="_blank" href="ocs_online.php">alle</a>)</td>
</tr><tr><td style="overflow:hidden; text-align:left; padding:0px; padding-left:6px;">&nbsp;<?php
if ($num > 0) {
	$userrows = array();
	foreach ($users as $user) {
		$userrows[] = '<span style="color:#'.$user->namecolor.';">'.$user->username.'</span>';
	}
	//shuffle($userrows);
	$first = true;
	foreach ($userrows as $userrow) {
		if (!$first) echo ', ';
		$first = false;
		echo $userrow;
	}
}
?>
</td></tr></table>

</div>
</body>
</html>
