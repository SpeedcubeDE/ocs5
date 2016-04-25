package de.nerogar.ocs.scramble;

import java.security.SecureRandom;
import java.util.Random;

public class ScramblerOneByOne implements ScramblerOCS {

	private static String[] moves = { "x", "y", "z" };
	private static String[] modifiers = { "", "'", "2" };

	private Random rand;

	public ScramblerOneByOne() {
		//the SecureRandom generator is really important here
		rand = new SecureRandom();
	}

	@Override
	public String nextScramble() {
		StringBuilder sb = new StringBuilder();

		int lastMoveI = -1;
		for (int i = 0; i < 8; i++) {

			int moveI = -1;
			do {
				moveI = rand.nextInt(moves.length);
			} while (moveI == lastMoveI);
			lastMoveI = moveI;

			int modifierI = rand.nextInt(modifiers.length);

			sb.append(moves[moveI]).append(modifiers[modifierI]).append(' ');
		}

		return sb.substring(0, sb.length() - 1);
	}

}
