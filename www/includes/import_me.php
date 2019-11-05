<?php

ini_set('display_errors', 'On');
error_reporting(E_ALL | E_STRICT);
date_default_timezone_set("Europe/Berlin");

// undo PHP's magic quotes "feature"
if (get_magic_quotes_gpc()) {

	function stripslashes_deep($value) {
		$value = is_array($value) ? array_map('stripslashes_deep', $value) : stripslashes($value);
		return $value;
	}
	$_POST = array_map('stripslashes_deep', $_POST);
	$_GET = array_map('stripslashes_deep', $_GET);
	$_COOKIE = array_map('stripslashes_deep', $_COOKIE);
	$_REQUEST = array_map('stripslashes_deep', $_REQUEST);
}

// import required shit
require_once 'includes/connection.php';
require_once 'includes/config.php';
require_once 'includes/DE.lang.php';
require_once 'includes/Account.class.php';
require_once 'includes/functions.php';

?>