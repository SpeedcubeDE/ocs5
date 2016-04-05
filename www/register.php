<?php
require_once 'includes/header.php';

$submit = isset($_POST ['submit']);

$errors = array ();

if ($submit) {
	
	$username = $_POST ['username'];
	$password = $_POST ['password'];
	$password2 = $_POST ['password2'];
	$email = $_POST ['email'];
	$email2 = $_POST ['email2'];
	
	if ($password != $password2) {
		$errors [] = "Die angegebenen Passwörter müssen identisch sein.";
	} else if ($email != $email2) {
		$errors [] = "Die angegebenen E-Mail-Adressen müssen identisch sein.";
	} else {
		
		$error = Account::register($db, $lang, $username, $password, $email);
		
		if ($error == AccountError::NO_ERROR) {
			
			// user input seems fine. Create the User now...
			header("Location:index.php?registered");
		
		} else {
			$errors [] = AccountError::str($error, $lang);
		}
	
	}
}

?>
<h2>Registrierung</h2>
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
			<td><input type="text" name="username" value="<?php echo @$_POST["username"]; ?>" /></td>
		</tr>
		<tr>
			<td>Password:</td>
			<td><input type="password" name="password" /></td>
		</tr>
		<tr>
			<td>Password (wdh.):</td>
			<td><input type="password" name="password2" /></td>
		</tr>
		<tr>
			<td>E-Mail Adresse:</td>
			<td><input type="text" name="email" value="<?php echo @$_POST["email"]; ?>" /></td>
		</tr>
		<tr>
			<td>E-Mail Adresse (wdh.):</td>
			<td><input type="text" name="email2" /></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="Abschicken*!" /> <a href="resendmail.php">Aktivierungsmail erneut senden.</a></td>
		</tr>
	</table>
	<small>*Mit einem Klick auf 'Abschicken' bestätigen sie Ihre Kenntnisnahme, dass die Sicherheit der Daten aus technischen Gründen nicht gewährleistet werden kann.</small>
</form>

<?php
require_once 'includes/footer.php';
?>