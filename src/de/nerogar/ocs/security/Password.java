package de.nerogar.ocs.security;

public class Password {

	public static boolean isPasswordValid(String password, String hashPassword, String salt) {
		return hashPassword.equals(hashPassword(password, salt));
	}

	public static String hashPassword(String password, String salt) {
		return Sha2.hash(password + salt);
	}

}
