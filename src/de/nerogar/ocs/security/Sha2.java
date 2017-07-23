package de.nerogar.ocs.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.nerogar.ocs.Logger;

public class Sha2 {

	public static String hash(String data) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			Logger.log(Logger.ERROR, "MISSING HASH ALGORITHM SHA-256 !!!");
			e.printStackTrace(Logger.getErrorStream());
			return "";
		}
		md.update(data.getBytes());
		return bytesToHex(md.digest());
	}

	public static String bytesToHex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte byt : bytes)
			result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}

	public static String hashPassword(String password, String salt) {
		return hash(salt + password);
	}
}
