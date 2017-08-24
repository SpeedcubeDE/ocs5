package de.nerogar.ocs.parse;

import de.nerogar.ocs.Logger;
import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.user.Profile;
import de.nerogar.ocs.user.User;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ParseProfile extends Parse {

	//==========
	//structure

	private static Map<String, DataType> profileStructure;

	private static Map<String, DataType> profileStructureGet;
	private static Map<String, DataType> profileStructureHistory;

	static {
		profileStructure = new HashMap<String, Parse.DataType>();
		profileStructure.put("action", DataType.STRING);

		profileStructureGet = new HashMap<String, Parse.DataType>();
		profileStructureGet.put("userID", DataType.INTEGER);

		profileStructureHistory = new HashMap<String, Parse.DataType>();
		profileStructureHistory.put("userID", DataType.INTEGER);
		profileStructureHistory.put("cubeType", DataType.STRING);
		profileStructureHistory.put("start", DataType.INTEGER);
		profileStructureHistory.put("end", DataType.INTEGER);
	}

	//structure end
	//==========

	public ParseProfile(User user) {
		super(user);
	}

	public void parse(JSONObject jsonData) {
		if (!validate(jsonData, profileStructure)) return;
		String action = (String) jsonData.get("action");

		if (action == null) return;

		switch (action) {
			case "get":
				get(jsonData);
				break;
			case "history":
				history(jsonData);
				break;
		}
	}

	private void get(JSONObject jsonData) {
		if (!validate(jsonData, profileStructureGet)) return;
		int userID = ((Long) jsonData.get("userID")).intValue();

		User user = OCSServer.userPool.getUser(userID);
		if (user == null) {
			new Alert(Alert.ERROR, true, OCSStrings.getString("user.error.findID", String.valueOf(userID))).sendTo(getUser());
			return;
		}

		Profile profile = user.getProfile();

		if (profile != null) profile.sendTo(getUser());
	}

	private void history(JSONObject jsonData) {
		// TODO: remove permission check
		if (!getUser().hasPermission(User.DEBUG)) return;

		if (!validate(jsonData, profileStructureHistory)) return;
		int userID = ((Long) jsonData.get("userID")).intValue();
		String cubeType = (String) jsonData.get("cubeType");
		int start = ((Long) jsonData.get("start")).intValue();
		int end = ((Long) jsonData.get("end")).intValue();

		User user = OCSServer.userPool.getUser(userID);
		if (user == null) {
			new Alert(Alert.ERROR, true, OCSStrings.getString("user.error.findID", String.valueOf(userID))).sendTo(getUser());
			return;
		}

		Logger.log(Logger.DEBUG, "profile history packet");

	}

}
