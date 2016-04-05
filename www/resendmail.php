<?php
require_once 'includes/header.php';

$submit = isset($_POST ['submit']);

$errors = array ();

if ($submit) {
	
	$username = $_POST ['username'];
	$email = $_POST ['email'];
	
	if (empty($username)) {
		if (empty($email)) {
			$errors [] = "Bitte geben sie Usernamen und/oder E-Mail-Adresse an!";
		} else {
			$result = $db->query("SELECT * FROM " . DB_PREFIX . "user WHERE email = '" . escape($db, $email) . "'");
		}
	} else {
		if (empty($email)) {
			$result = $db->query("SELECT * FROM " . DB_PREFIX . "user WHERE name = '" . escape($db, $username) . "'");
		} else {
			$result = $db->query("SELECT * FROM " . DB_PREFIX . "user WHERE name = '" . escape($db, $username) . "' AND email = '" . escape($db, $email) . "'");
		}
	}
	
	if ($result->num_rows == 0) {
		$errors [] = $lang['resend_no_match'];
	}
	
	if (count($errors) == 0) {
		$result = $result->fetch_object();
		if ($result->registerToken === "") {
			$errors [] = $lang ['activation_obsolete'];
		} else {
			$token = Account::sendRegisterMail($lang, $result->email, $result->name);
			if ($token === false) {
				$errors [] = $lang ['email_send_error'];
			} else {
				$db->query("UPDATE " . DB_PREFIX . "user SET registerToken = '" . $token . "' WHERE id = " . $result->id);
				echo '<div class="enboxed" style="color:green;">' . $lang ['email_resent'] . '</div>';
			}
		}
	}

}

?>
<h2>Registrierungsmail erneut senden</h2>
<?php
foreach ($errors as $error) {
	echo '<div class="enboxed" style="color:red;">' . $error . '</div>';
}
?>
<h3>Um die Aktivierungsmail erneut zu senden, geben sie bitte Ihren angegebenen Usernamen und/oder Ihre angegebene E-Mail-Adresse ein. Accounts, die nach 24h nicht aktiviert wurden, werden wieder freigegeben.</h3>
<form method="post" action="<?php echo $_SERVER['PHP_SELF']; ?>">
	<table>
		<tr>
			<td colspan="2"><input type="hidden" value="1" name="submit" /></td>
		</tr>
		<tr>
			<td>Username:</td>
			<td><input type="text" name="username" /></td>
		</tr>
		<tr>
			<td>E-Mail Adresse:</td>
			<td><input type="text" name="email" /></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="Abschicken!" /></td>
		</tr>
	</table>
</form>

<?php
require_once 'includes/footer.php';
?>