package de.nerogar.ocs;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
	private static long[] timeMults = new long[] { 1000L, 60L, 60L, 24L, 365L, 1000L, 1000L, 1000000L };
	private static String[] timeUnits = new String[] { "ms", "s", "m", "h", "d", "a", "ka", "Ma" };

	public static long parseTime(String timeString) {
		long time = 0;
		if (timeString.matches("^(-?\\d*)(ms|s|m|h|d|a)?$")) {
			String value = timeString.replaceAll("^(-?\\d*)(ms|s|m|h|d|a)$", "$1");
			String unit = timeString.replaceAll("^(-?\\d*)(ms|s|m|h|d|a)$", "$2");
			time = Integer.parseInt(value);

			switch (unit) {
			default:
			case "ms":
				time *= 1L;
				break;
			case "s":
				time *= 1000L;
				break;
			case "m":
				time *= 60000L; //60L * 1000L
				break;
			case "h":
				time *= 3600000L; //60L * 60L * 1000L
				break;
			case "d":
				time *= 86400000L; //24L * 60L * 60L * 1000L
				break;
			case "a":
				time *= 31536000000L; //365L * 24L * 60L * 60L * 1000L
				break;
			}
		}

		return time;
	}

	public static String asStringDelta(long time) {

		String timeString = "";

		long tempValue;
		boolean first = true;

		for (int i = 0; i < timeMults.length; i++) {
			tempValue = time % timeMults[i];
			time -= tempValue;
			time /= timeMults[i];

			if (tempValue != 0) {
				timeString = tempValue + timeUnits[i] + (first ? "" : ", ") + timeString;
				first = false;
			}
		}

		return timeString.isEmpty() ? "0ms" : timeString;
	}

	public static String asString(long time) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return sdf.format(date);
	}
}
