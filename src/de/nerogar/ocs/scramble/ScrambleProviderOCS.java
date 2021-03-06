package de.nerogar.ocs.scramble;

public class ScrambleProviderOCS extends ScrambleProvider {

	private ScramblerOCS scrambler;

	public ScrambleProviderOCS(ScramblerOCS scrambler) {
		this.scrambler = scrambler;
	}

	@Override
	public String genNextScramble() {
		return scrambler.nextScramble();
	}

	@Override
	public String toString() {
		return "scramble provider of " + scrambler.toString();
	}

}
