<?php
require_once 'includes/header.php';

$submit = isset($_POST ['submit']);
@$username = $_REQUEST ['username'] or "";
@$token = $_REQUEST ['token'] or "";

$errors = array ();

if ($submit) {
	
	$new_password = $_POST ['new_password'];
	$new_password2 = $_POST ['new_password2'];
	
	if ($new_password != $new_password2) {
		$errors [] = $lang['passwords_not_identical'];
	}
	
	$error = Account::validatePassword($new_password);
	if ($error != AccountError::NO_ERROR) {
		$errors [] = AccountError::str($error, $lang);
	}
	
	if (count($errors) == 0) {
		
		// check if username and token are valid
		$query = "SELECT id from " . DB_PREFIX . "user WHERE name = '" . escape($db, $username) . "' and resetToken = '" . escape($db, $token) . "' LIMIT 1";
		$result = $db->query($query);
		if ($result->num_rows == 0) {
			$errors[] = $lang["token_incorrect"];
		} else {
			$user = $result->fetch_object();
			$crypted_pw = better_crypt($new_password);
			$db->query("UPDATE " . DB_PREFIX . "user SET password = '" . $crypted_pw . "' WHERE id = $user->id LIMIT 1");
			header("Location:index.php?changedpw");
		}
	}
}

?>
<h2>Passwort zurücksetzen</h2>
<?php
foreach ($errors as $error) {
	echo '<div class="enboxed" style="color:red;">' . $error . '</div>';
}
?>
<form method="post" action="<?php echo $_SERVER['PHP_SELF']; ?>">
	<table>
		<tr>
			<td colspan="2">
				<input type="hidden" value="1" name="submit" />
				<input type="hidden" value="<?php echo htmlspecialchars($username); ?>" name="username" />
				<input type="hidden" value="<?php echo htmlspecialchars($token); ?>" name="token" />
			</td>
		</tr>
		<tr>
			<td>Neues Passwort:</td>
			<td><input type="password" name="new_password" /></td>
		</tr>
		<tr>
			<td>Neues Passwort (wdh.):</td>
			<td><input type="password" name="new_password2" /></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="Passwort setzen!" /></td>
		</tr>
	</table>
</form>

<?php
require_once 'includes/footer.php';
?>