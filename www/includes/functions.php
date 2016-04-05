<?php

function escape($db, $string) {
	return $db->real_escape_string($string);
}

// Original PHP code by Chirp Internet: www.chirp.com.au
// Please acknowledge use of this code by including this header.
// The salt generation was put separately
function generateSalt($length = 22) {
	$salt = "";
	$salt_chars = array_merge(range('A', 'Z'), range('a', 'z'), range(0, 9));
	for($i = 0; $i < $length; $i ++) {
		$salt .= $salt_chars [array_rand($salt_chars)];
	}
	return $salt;
}
function better_crypt($input, $rounds = 7) {
	$salt = generateSalt();
	return crypt($input, sprintf('$2a$%02d$', $rounds) . $salt);
}

function randomColor(){
	mt_srand((double)microtime()*1000000);
	$c = '';
	while(strlen($c)<6){
		$c .= sprintf("%02X", mt_rand(0, 255));
	}
	return $c;
}

?>