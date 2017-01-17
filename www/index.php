<?php
require_once 'includes/header.php';

$submit = isset($_POST ['submit']);
$ip = @$_GET ['ip'];
$ip = preg_replace("/[^a-zA-Z0-9.]/", "", $ip);

$errors = array ();
$success = false;

if ($submit) {
	
	$username = $_POST ['username'];
	$password = $_POST ['password'];
	
	if (empty($ip) || $ip == "localhost" || $ip == "127.0.0.1") {
		$error = Account::login($db, $username, $password, $user);
		
		$success = $error == AccountError::NO_ERROR;
		
		if ($success) {
			$token = $user->loginToken;
			setcookie("token", $token);
		} else {
			$errors [] = AccountError::str($error, $lang);
		}
	} else {
		// log in remotely using botlogin.php
		$req = "http://".$ip."/botlogin.php?username=".urlencode($username)."&password=".urlencode($password);
		$response = file_get_contents($req);
		if ($response === false) {
			$errors [] = "Could not log in remotely, login script not found on remote server!";
		} else {
			$response = json_decode($response);
			$success = $response->success;
			if (!$success) {
				$errors [] = $response->error;
			} else {
				$token = $response->token;
				setcookie("token", $token);
			}
		}
	}

}

if ($success) {
	echo <<<EOT
	<script type="text/javascript">
		window.onload = function() {
			//document.forms["silentlogin"].submit();	
			window.setTimeout(function() {
				window.location = "http://ocs.speedcube.de/ocs.php?ip=
EOT;
	echo $ip;
	echo '";}, 500);}</script>';

}

?>
<br />
<iframe src="ocs_online.php" style="border: 0px;" scrolling="no" height="27px" width="468px"></iframe>
<h2>Login</h2>
<?php
foreach ($errors as $error) {
	echo '<div class="enboxed" style="color:red;">' . $error . '</div>';
}

$gets ['registered'] = EMAIL_VERIFICATION ? $lang ['registered_email'] : $lang ['registered'];
$gets ['changedpw'] = $lang ['changed_pw'];
$gets ['resetpw'] = $lang ['sent_reset_mail'];
$gets ['activated'] = $lang ['activated'];
$gets ['activation_failed'] = $lang ['activation_failed'];

foreach ($gets as $get => $text) {
	if (isset($_GET [$get])) echo '<div class="enboxed" style="color:green;">' . $text . '</div>';
}

?>
<!--<div style="float: right;">
	<small>powered by</small><br /> <a href="../"><img src="../logo_krativ2.png" alt="picocom.net" /></a>
</div>-->
<form method="post" action="<?php echo $_SERVER['PHP_SELF']."?ip=".$ip; ?>">
	<table>
		<tr>
			<td colspan="2"><input type="hidden" value="1" name="submit" /><input type="hidden" name="ip" value="<?php echo $ip; ?>" /></td>
		</tr>
		<tr>
			<td>Username:</td>
			<td><input type="text" name="username" /></td>
		</tr>
		<tr>
			<td>Password:</td>
			<td><input type="password" name="password" /></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="Abschicken!" /> <a href="resetpw.php">Passwort vergessen?</a></td>
		</tr>
	</table>
</form>
<?php
require_once 'includes/footer.php';
?>