<?php

header('Content-Type: text/html; charset=utf-8');
require_once 'import_me.php';

?>

<!DOCTYPE html>
<html lang="de">
<head>
<title>ocs5</title>
<meta charset="utf-8" />
<link rel="stylesheet" href="style.css">
<style type="text/css">
</style>
</head>

<?php
$links ['Login'] = 'index.php';
$links ['Registrieren'] = 'register.php';
$links ['Passwort ändern'] = 'changepw.php';
$links ['Über das OCS'] = 'about.php';
$links ['Impressum'] = 'impressum.php';
?>

<body>
	<div style="display: none;" id="additionalCSS"></div>
	<div id="container">
		<div id="main">
			<div style="height: 8%;"></div>
			<div id="wrapper">
				<div class="enboxed">
					<?php
					foreach ($links as $text => $link) {
						if ($link == basename($_SERVER['PHP_SELF'])) $l[] = '<span style="font-weight:bold;">' . $text . '</span>';
						else $l[] = '<a href="' . $link . '">' . $text . '</a>';
					}
					echo implode('<div class="vr"></div>', $l);
					?>
				</div>