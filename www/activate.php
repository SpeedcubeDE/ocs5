<?php
require_once 'includes/header.php';

@$email = $_GET ['email'];
@$token = $_GET ['token'];

$success = Account::activate($db, $email, $token);
if ($success) {
	header("Location:index.php?activated");
} else {
	header("Location:index.php?activation_failed");
}

require_once 'includes/footer.php';
?>