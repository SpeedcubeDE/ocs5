package de.nerogar.ocs.parse;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.chat.WhisperRoom;
import de.nerogar.ocs.user.User;

public class ParseWhisper extends Parse {

	//==========
	//structure

	private static Map<String, DataType> whisperStructure;

	private static Map<String, DataType> whisperStructureStart;
	static {
		whisperStructure = new HashMap<String, Parse.DataType>();
		whisperStructure.put("action", DataType.STRING);

		whisperStructureStart = new HashMap<String, Parse.DataType>();
		whisperStructureStart.put("userID", DataType.INTEGER);
	}

	//structure end
	//==========

	public ParseWhisper(User user) {
		super(user);
	}

	public void parse(JSONObject jsonData) {
		if (!validate(jsonData, whisperStructure)) return;
		String action = (String) jsonData.get("action");

		if (action == null) return;

		switch (action) {
			case "start":
				start(jsonData);
				break;
		}
	}

	private void start(JSONObject jsonData) {
		if (!validate(jsonData, whisperStructureStart)) return;

		if (getUser().isMutedAndPrevent()) return;

		int targetID = ((Long) jsonData.get("userID")).intValue();

		User targetUser = OCSServer.userPool.getUser(targetID);
		if (targetUser != null && targetUser.isLoggedIn()) {
			WhisperRoom whisperRoom = new WhisperRoom(getUser().getUsername() + " : " + targetUser.getUsername(), -1, 0, "", "");
			whisperRoom.partner1 = getUser();
			whisperRoom.partner2 = targetUser;

			if (OCSServer.chatRoomManager.addChatRoom(whisperRoom)) {
				whisperRoom.addUser(getUser());
				whisperRoom.addUser(targetUser);
			} else {
				OCSServer.chatRoomManager.getChatRoom(whisperRoom).addUser(getUser());
			}
		}
	}

}
