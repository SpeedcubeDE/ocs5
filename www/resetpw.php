<?php
require_once 'includes/header.php';

$submit = isset($_POST ['submit']);

$errors = array ();

if ($submit) {
	
	$username = $_POST ['username'];
	$email = $_POST ['email'];
		
	$error = Account::sendResetMail($db, $lang, $username);
	
	if ($error == AccountError::NO_ERROR) {
		header("Location:index.php?resetpw");
	} else {
		$errors [] = AccountError::str($error, $lang);
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
			<td colspan="2"><input type="hidden" value="1" name="submit" /></td>
		</tr>
		<tr>
			<td>Username:</td>
			<td><input type="text" name="username" /></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="E-Mail anfordern!" /></td>
		</tr>
	</table>
</form>

<?php
require_once 'includes/footer.php';
?>
