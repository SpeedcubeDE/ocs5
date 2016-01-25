package de.nerogar.ocs.parse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.nerogar.ocs.Logger;

public class ParseLogin {

	public static String parseLogin(String jsonText) {
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonData = (JSONObject) jsonParser.parse(jsonText);

			if (!jsonData.containsKey("type")) return null;
			String typeString = (String) jsonData.get("type");

			if (typeString.equals("login")) {
				String key = (String) jsonData.get("key");
				return key;
			}
		} catch (ParseException e) {
			e.printStackTrace(Logger.getErrorWriter());
		}

		return null;
	}

}
