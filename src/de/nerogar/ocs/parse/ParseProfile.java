package de.nerogar.ocs.parse;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.user.Profile;
import de.nerogar.ocs.user.User;

public class ParseProfile extends Parse {

	//==========
	//structure

	private static Map<String, DataType> profileStructure;

	private static Map<String, DataType> profileStructureGet;
	static {
		profileStructure = new HashMap<String, Parse.DataType>();
		profileStructure.put("action", DataType.STRING);

		profileStructureGet = new HashMap<String, Parse.DataType>();
		profileStructureGet.put("userID", DataType.INTEGER);
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
			case "get": get(jsonData);
				break;
		}
	}

	private void get(JSONObject jsonData) {
		if (!validate(jsonData, profileStructureGet)) return;
		int userID = ((Long) jsonData.get("userID")).intValue();

		User user = OCSServer.userPool.getUser(userID);
		if (getUser() == null) {
			new Alert(Alert.ERROR, true, OCSStrings.getString("user.error.findID", String.valueOf(userID))).sendTo(getUser());
			return;
		}

		Profile profile = user.getProfile();

		if (profile != null) profile.sendTo(getUser());
	}

}
