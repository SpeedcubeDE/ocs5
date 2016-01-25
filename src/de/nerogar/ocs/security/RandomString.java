package de.nerogar.ocs.security;

import java.util.Random;

public class RandomString {

	public static String getNew(int length) {
		String s = "";

		Random r = new Random();
		for (int i = 0; i < length; i++) {
			s += (char) (r.nextFloat() * 93 + 33);
		}

		return s;
	}

}
