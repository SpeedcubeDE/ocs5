<?php

header('Cache-Control: no-cache, must-revalidate');
header('Expires: Mon, 26 Jul 1997 05:00:00 GMT');
header('Content-Type: application/json; charset=utf-8');
require_once 'includes/import_me.php';

$submit = isset($_REQUEST ['username']) && isset($_REQUEST ['password']);

$response = (object)[];
$response->success = false;
$errors = array ();

if ($submit) {
	
	$username = $_REQUEST ['username'];
	$password = $_REQUEST ['password'];
	
	$error = Account::login($db, $username, $password, $user);
	
	$response->success = $error == AccountError::NO_ERROR;
	
	if ($response->success) {
		$response->token = $user->loginToken;
	} else {
		$response->error = AccountError::str($error, $lang);
	}

} else {
	$response->error = $lang['no_data_submitted'];
}

echo json_encode($response);

?>