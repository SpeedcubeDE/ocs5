package de.nerogar.ocs;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;

public class OCSStrings {
	private static Map<String, String> strings;

	static {
		strings = new HashMap<String, String>();
		try (Scanner scanner = new Scanner(new FileInputStream("DE.lang"), "UTF-8")) {

			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				String[] split = line.split("=", 2);

				if (line.isEmpty()) continue;

				if (split.length == 2) {
					strings.put(split[0].trim(), split[1].trim());
				} else {
					Logger.log(Logger.ERROR, "Error reading language file, could not parse: \"" + line + "\"");
				}
			}

		} catch (FileNotFoundException e) {
			Logger.log(Logger.ERROR, "Could not load language file");
			e.printStackTrace(Logger.getErrorStream());
		}
	}

	public static String getString(String id) {
		String retString = strings.get(id);
		if (retString != null) {
			return retString;
		} else {
			return id;
		}
	}

	public static String getString(String id, String... values) {
		String retString = strings.get(id);
		if (retString != null) {
			for (int i = 0; i < values.length; i++) {
				retString = retString.replaceAll("\\{" + (i + 1) + "\\}", Matcher.quoteReplacement(values[i]));
			}

			return retString;
		} else {
			return id + " " + Arrays.toString(values);
		}
	}
}
