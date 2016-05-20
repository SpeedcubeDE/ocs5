<?php

$lang ['no_such_user'] = 'User existiert nicht.';
$lang ['password_incorrect'] = 'Passwort ist inkorrekt.';
$lang ['token_incorrect'] = 'Token ist inkorrekt.';
$lang ['not_activated'] = 'Der Account ist noch nicht aktiviert.';
$lang ['no_data_submitted'] = 'Keine Anmeldedaten übergeben.';

$lang ['username_invalid_length'] = 'Der Username muss zwischen {1} und {2} Zeichen lang sein!';
$lang ['username_invalid_chars'] = 'Der Username darf keine Leerzeichen enthalten!';
//$lang ['username_invalid_chars'] = 'Der Username enthält ungültige Zeichen!';
$lang ['username_occupied'] = 'Der Username ist bereits vergeben!';
$lang ['email_occupied'] = 'Die E-Mail-Adresse ist bereits vergeben!';
$lang ['passwords_not_identical'] = 'Die angegebenen Passwörter sind nicht identisch!';
$lang ['password_invalid_length'] = 'Das Passwort muss zwischen {1} und {2} Zeichen lang sein.';
$lang ['invalid_email'] = 'Die E-Mail Adresse ist ungültig.';
$lang ['email_send_error'] = 'Serverfehler: E-Mail konnte nicht versendet werden.';

$lang ['activatemail_subject'] = 'OCS - Account aktivieren';
$lang ['activatemail_text'] = "Willkommen im Online-Cubing-System, {1}! \n\nUm deinen Account zu aktivieren, klicke bitte hier: {2} \n\n Wenn der Link nicht funktioniert, kopiere ihn bitte in deine URL-Leiste.";

$lang ['registered'] = 'Du hast dich erfolgreich registriert! Der Account ist nun aktiviert.';
$lang ['registered_email'] = 'Du hast dich erfolgreich registriert! Klicke nun bitte auf den Aktivierungslink in der E-Mail, die innerhalb weniger Minuten ankommen sollte.<br />Ist der Account nicht innerhalb von 24h aktiviert worden, wird er wieder freigegeben.<br /><a href="resendmail.php">E-Mail erneut senden!</a>';
$lang ['activated'] = 'Dein Account wurde erfolgreich aktiviert. Du kannst dich nun einloggen.';
$lang ['activation_failed'] = 'Accountaktivierung fehlgeschlagen!';
$lang ['activation_obsolete'] = 'Account ist bereits aktiviert!';
$lang ['changed_pw'] = 'Dein Passwort wurde erfolgreich geändert!';
$lang ['sent_reset_mail'] = 'Es wurde eine E-Mail zum Zurücksetzen des Passwortes an die E-Mail Adresse des Accounts verschickt.';

$lang ['email_resent'] = 'E-Mail wurde erneut versendet!';
$lang ['resend_no_match'] = 'Keine Übereinstimmungen mit den übermittelten Daten gefunden!';

$lang ['resetpwmail_subject'] = 'OCS - Passwort zurücksetzen.';
$lang ['resetpwmail_text'] = "Hallo, {1}! \n\nFür deinen OCS-Account unter dieser E-Mail-Adresse wurde angefordert, das Passwort zurückzusetzen. Um dies zu tun, klicke bitte hier: {2} \n\n Wenn der Link nicht funktioniert, kopiere ihn bitte in deine URL-Leiste.";


function lang_parse($str, $args) {
	for($i = 0; $i < count($args); $i ++) {
		$str = str_replace('{' . ($i + 1) . '}', $args [$i], $str);
	}
	return $str;
}

?>