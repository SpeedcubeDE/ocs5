package de.nerogar.ocs.scramble;

import net.gnehzr.tnoodle.scrambles.Puzzle;

public class ScrambleProviderTNoodle extends ScrambleProvider {

	Puzzle puzzle;
	
	public ScrambleProviderTNoodle(Puzzle puzzle) {
		this.puzzle = puzzle;
	}
	
	@Override
	public String genNextScramble() {
		return puzzle.generateScramble();
	}

}
