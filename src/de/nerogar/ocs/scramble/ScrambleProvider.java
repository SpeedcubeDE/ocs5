package de.nerogar.ocs.scramble;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.puzzletimer.scramblers.ScramblerProvider;

import puzzle.*;

public abstract class ScrambleProvider {

	public abstract String genNextScramble();

	public static ScrambleProvider getScrambleProvider(String scrambleType) {
		if (scrambleProvider.containsKey(scrambleType)) {
			return scrambleProvider.get(scrambleType);
		} else {
			throw new IllegalArgumentException("could not find scrambler " + scrambleType);
		}
	}

	//TODO load definitions from databse
	private static Map<String, ScrambleProvider> scrambleProvider = new HashMap<>();

	public static void initScrambler() {

		Map<String, String> prismaTypes = new HashMap<String, String>();
		//prismaTypes.put("2x2", "2x2x2-CUBE-RANDOM");
		//prismaTypes.put("3x3", "RUBIKS-CUBE-RANDOM");
		//prismaTypes.put("4x4", "4x4x4-CUBE-RANDOM");
		prismaTypes.put("5x5", "5x5x5-CUBE-RANDOM");
		prismaTypes.put("6x6", "6x6x6-CUBE-RANDOM");
		prismaTypes.put("7x7", "7x7x7-CUBE-RANDOM");

		prismaTypes.put("Clock", "RUBIKS-CLOCK-RANDOM");
		prismaTypes.put("Megaminx", "MEGAMINX-RANDOM");
		prismaTypes.put("Pyraminx", "PYRAMINX-RANDOM");
		prismaTypes.put("Square-1", "SQUARE-1-RANDOM");
		prismaTypes.put("Floppy", "FLOPPY-CUBE-RANDOM");
		prismaTypes.put("Skewb", "SKEWB-RANDOM");

		prismaTypes.put("2x2 URF", "2x2x2-CUBE-URF");
		prismaTypes.put("3x3 easy cross", "RUBIKS-CUBE-EASY-CROSS");
		prismaTypes.put("3x3 CLL", "RUBIKS-CUBE-CLL-TRAINING");
		prismaTypes.put("3x3 ELL", "RUBIKS-CUBE-ELL-TRAINING");
		prismaTypes.put("3x3 F2L", "RUBIKS-CUBE-FRIDRICH-F2L-TRAINING");
		prismaTypes.put("3x3 OLL", "RUBIKS-CUBE-FRIDRICH-OLL-TRAINING");
		prismaTypes.put("3x3 PLL", "RUBIKS-CUBE-FRIDRICH-PLL-TRAINING");
		prismaTypes.put("Tower", "TOWER-CUBE-RANDOM");
		prismaTypes.put("Rubik's Tower", "RUBIKS-TOWER-RANDOM");
		prismaTypes.put("Rubik's Domino", "RUBIKS-DOMINO-RANDOM");

		ScramblerProvider prismaScramblerProvider = new ScramblerProvider();
		for (Entry<String, String> e : prismaTypes.entrySet()) {
			scrambleProvider.put(e.getKey(), new ScrambleProviderPrisma(prismaScramblerProvider.get(e.getValue())));
		}

		scrambleProvider.put("2x2", new ScrambleProviderTNoodle(new TwoByTwoCubePuzzle()));
		scrambleProvider.put("3x3", new ScrambleProviderTNoodle(new ThreeByThreeCubePuzzle()));
		scrambleProvider.put("4x4", new ScrambleProviderTNoodle(new FourByFourCubePuzzle()));

		//needed to initialize some of the algorithms
		for (ScrambleProvider s : scrambleProvider.values()) {
			s.genNextScramble();
		}

	}

}
