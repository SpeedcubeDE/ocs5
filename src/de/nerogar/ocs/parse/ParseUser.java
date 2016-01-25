package de.nerogar.ocs.parse;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.user.User;

public class ParseUser extends Parse {

	//==========
	//structure

	private static Map<String, DataType> userStructure;

	private static Map<String, DataType> userStructureFetch;
	static {
		userStructure = new HashMap<String, Parse.DataType>();
		userStructure.put("action", DataType.STRING);

		userStructureFetch = new HashMap<String, Parse.DataType>();
		userStructureFetch.put("userID", DataType.INTEGER);
	}

	//structure end
	//==========

	public ParseUser(User user) {
		super(user);
	}

	public void parse(JSONObject jsonData) {
		if (!validate(jsonData, userStructure)) return;
		String action = (String) jsonData.get("action");

		if (action == null) return;

		switch (action) {
			case "get":
				get(jsonData);
				break;
		}
	}

	private void get(JSONObject jsonData) {
		if (!validate(jsonData, userStructureFetch)) return;
		int userID = ((Long) jsonData.get("userID")).intValue();

		User targetUser = OCSServer.userPool.getUser(userID);
		if (targetUser != null) {
			targetUser.sendTo(getUser());
		} else {
			new Alert(Alert.ERROR, true, OCSStrings.getString("user.error.findID", String.valueOf(userID))).sendTo(getUser());
		}
	}
}
