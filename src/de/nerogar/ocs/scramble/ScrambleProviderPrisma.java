package de.nerogar.ocs.scramble;

import com.puzzletimer.scramblers.Scrambler;

public class ScrambleProviderPrisma extends ScrambleProvider {

	private Scrambler scrambler;

	public ScrambleProviderPrisma(Scrambler scrambler) {
		this.scrambler = scrambler;
	}

	@Override
	public String genNextScramble() {
		return scrambler.getNextScramble().getRawSequence();
	}

	@Override
	public String toString() {
		return "scramble provider of " + scrambler.toString();
	}
	
}
