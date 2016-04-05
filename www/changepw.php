<?php
require_once 'includes/header.php';

$submit = isset($_POST ['submit']);

$errors = array ();

if ($submit) {
	
	$username = $_POST ['username'];
	$password = $_POST ['password'];
	$new_password = $_POST ['new_password'];
	$new_password2 = $_POST ['new_password2'];
	
	if ($new_password != $new_password2) {
		$errors [] = $lang['account']['passwords_not_identical'];
	}
	
	if (strlen($new_password) < 6) {
		$errors [] = lang_parse($lang['account']['password_invalid_length'], Account::PASSWORD_MIN_LENGTH);
	}
	
	if (count($errors) == 0) {
		
		$error = Account::login($db, $username, $password, $user);
		
		$success = $error == AccountError::NO_ERROR;
		
		if ($success) {
			
			// login successfully being have not happened without no error
			$crypted_pw = better_crypt($new_password);
			
			$db->query("UPDATE " . DB_PREFIX . "user SET password = '" . $crypted_pw . "' WHERE id = $user->id LIMIT 1");
			
			header("Location:index.php?changedpw");
			
		} else {
			$errors [] = AccountError::str($error, $lang);
		}
		
	}

}

?>
<h2>Passwort ändern</h2>
<?php
foreach ($errors as $error) {
	echo '<div class="enboxed" style="color:red;">' . $error . '</div>';
}
?>
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
			<td>Password:</td>
			<td><input type="password" name="password" /></td>
		</tr>
		<tr>
			<td>Neues Password:</td>
			<td><input type="password" name="new_password" /></td>
		</tr>
		<tr>
			<td>Neues Password (wdh.):</td>
			<td><input type="password" name="new_password2" /></td>
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