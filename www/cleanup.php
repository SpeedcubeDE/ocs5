<?php
require_once 'includes/header.php';

$submit = isset($_REQUEST ['submit']);

$logs = array ();

if ($submit) {
	
	$db->query("DELETE FROM " . DB_PREFIX . "user WHERE registerToken != '' AND registerDate < NOW() - INTERVAL " . ACCOUNT_EXPIRATION . " SECOND");
	$logs [] = $db->affected_rows . " abgelaufene, nicht aktivierte Accounts wurden gelöscht";

}

?>
<h2>Cleanup-Routine</h2>
<?php
foreach ($logs as $log) {
	echo '<div class="enboxed" style="color:green;">' . $log . '</div>';
}
?>
Diese Routine löscht überfällige Daten aus der Datenbank (z.B. nicht aktivierte Accounts, zu alte Logs, temporäre Hilfsdaten).
<br />
Sie wird regelmäßig von einem Cronjob ausgeführt.
<br />
<br />

<form action="?" method="get">
	<input type="hidden" name="submit" value="1" /> <input type="submit" value="Routine ausführen" />
</form>

<?php
require_once 'includes/footer.php';
?>