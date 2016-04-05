<?php

abstract class AccountError {
	const NO_ERROR = 0;
	const NO_SUCH_USER = 1;
	const PASSWORD_INCORRECT = 2;
	const NOT_ACTIVATED = 3;
	
	const USERNAME_INVALID_LENGTH = 4;
	const USERNAME_INVALID_CHARS = 5;
	const USERNAME_OCCUPIED = 6;
	const EMAIL_OCCUPIED = 7;
	const PASSWORD_INVALID_LENGTH = 8;
	const INVALID_EMAIL = 9;
	const EMAIL_SEND_ERROR = 10;
	
	const DATABASE_ERROR = 11;

	public static function str($e, $lang) {
		switch ($e) {
			case self::NO_ERROR :
				return 'no error';
			case self::NO_SUCH_USER :
				return $lang ['no_such_user'];
			case self::PASSWORD_INCORRECT :
				return $lang ['password_incorrect'];
			case self::NOT_ACTIVATED :
				return $lang ['not_activated'];
			
			case self::USERNAME_INVALID_LENGTH :
				return lang_parse($lang ['username_invalid_length'], array (
						Account::USERNAME_MIN_LENGTH,
						Account::USERNAME_MAX_LENGTH 
				));
			case self::USERNAME_INVALID_CHARS :
				return $lang ['username_invalid_chars'];
			case self::USERNAME_OCCUPIED :
				return $lang ['username_occupied'];
			case self::EMAIL_OCCUPIED :
				return $lang ['email_occupied'];
			case self::PASSWORD_INVALID_LENGTH :
				return lang_parse($lang ['password_invalid_length'], array (
						Account::PASSWORD_MIN_LENGTH 
				));
			case self::INVALID_EMAIL :
				return $lang ['invalid_email'];
			case self::EMAIL_SEND_ERROR :
				return $lang ['email_send_error'];
			case self::DATABASE_ERROR :
				return "DATABASE ERROR!! OMGOMGOMG!";
			default :
				return 'undefined error';
		}
	}
}

abstract class Account {
	
	const USERNAME_MIN_LENGTH = 3;
	const USERNAME_MAX_LENGTH = 20;
	const PASSWORD_MIN_LENGTH = 6;
	const DEFAULT_STATUS = 'type /help for help';

	public static function login($db, $username, $password, &$user) {
		
		$query = "SELECT * FROM " . DB_PREFIX . "user WHERE name = '" . escape($db, $username) . "' LIMIT 1";
		$result = $db->query($query);
		
		if ($result->num_rows == 0) {
			return AccountError::NO_SUCH_USER;
		}
		
		$result = $result->fetch_object();
		
		if (crypt($password, $result->password) != $result->password) {
			return AccountError::PASSWORD_INCORRECT;
		}
		
		if ($result->registerToken != '') {
			return AccountError::NOT_ACTIVATED;
		}
		
		// login successfully being have not happened without no error
		$result->loginToken = self::newLoginToken($db, $result->id);
		$user = $result;
		return AccountError::NO_ERROR;
	
	}

	public static function activate($db, $email, $token) {
		
		$db->query("UPDATE " . DB_PREFIX . "user SET registerToken = '' WHERE email = '" . escape($db, $email) . "' AND registerToken = '" . escape($db, $token) . "' LIMIT 1");
		
		return $db->affected_rows > 0;
	
	}

	public static function register($db, $lang, $username, $password, $email) {
		
		Account::cleanup($db);
		
		$result = $db->query("SELECT COUNT(*) FROM " . DB_PREFIX . "user WHERE name = '" . escape($db, $username) . "' LIMIT 1");
		$result = $result->fetch_row();
		
		if ($result [0] != 0) {
			return AccountError::USERNAME_OCCUPIED;
		}
		
		$result = $db->query("SELECT COUNT(*) FROM " . DB_PREFIX . "user WHERE email = '" . escape($db, $email) . "' LIMIT 1");
		$result = $result->fetch_row();
		
		if ($result [0] != 0) {
			return AccountError::EMAIL_OCCUPIED;
		}
		
		if (strlen($username) < self::USERNAME_MIN_LENGTH || strlen($username) > self::USERNAME_MAX_LENGTH) {
			return AccountError::USERNAME_INVALID_LENGTH;
		}
		
		//if (! preg_match("/^[-0-9A-Z_@+.\s]+$/i", $username) || strpos($username, " ") !== false) {
		if (preg_match("/\s/i", $username)) {
			return AccountError::USERNAME_INVALID_CHARS;
		}
		
		if (strlen($password) < 6) {
			return AccountError::PASSWORD_INVALID_LENGTH;
		}
		
		if (filter_var($email, FILTER_VALIDATE_EMAIL) === false) {
			return AccountError::INVALID_EMAIL;
		}
		
		$crypted_pw = better_crypt($password);
		
		$token = "";
		if (EMAIL_VERIFICATION) {
			$token = Account::sendRegisterMail($lang, $email, $username);
			if ($token === false) {
				return AccountError::EMAIL_SEND_ERROR;
			}
		}
		
		$query = "INSERT INTO " . DB_PREFIX . "user (name, password, power, status, nameColor, registerToken, email, registerDate) VALUES ('" . escape($db, $username) . "', '" . $crypted_pw . "', 10, '" . self::DEFAULT_STATUS . "', '" . randomColor() . "', '" . $token . "', '" . escape($db, $email) . "', " . (time() * 1000) . ")";
		$result = $db->query($query);
		
		if ($result === false) {
			echo '<pre>'.$query."\n".$db->error;
			return AccountError::DATABASE_ERROR;
		}
		
		return AccountError::NO_ERROR;
	
	}
	
	// returns the register token
	public static function sendRegisterMail($lang, $email, $username) {
		$token = generateSalt(32);
		$link = $_SERVER ['SERVER_NAME'] . dirname($_SERVER ['SCRIPT_NAME']) . "/activate.php?email=" . urlencode($email) . "&token=" . urlencode($token);
		
		$text = lang_parse($lang ['activatemail_text'], array (
				$username,
				$link 
		));
		
		$header = 'From: noreply@' . $_SERVER ['SERVER_NAME'] . "\r\n" . 'X-Mailer: PHP/' . phpversion();
		
		if (mail($email, $lang ['activatemail_subject'], $text, $header) === false) return false;
		return $token;
	}

	public static function newLoginToken($db, $userid) {
		$token = generateSalt(32);
		$db->query("UPDATE " . DB_PREFIX . "user SET loginToken = '" . $token . "' WHERE id = $userid LIMIT 1");
		return $token;
	}

	public static function cleanup($db) {
		$exptime = (time() - ACCOUNT_EXPIRATION) * 1000;
		$db->query("DELETE FROM " . DB_PREFIX . "user WHERE registerToken != '' AND registerDate < " . $exptime);
	}

}

?>