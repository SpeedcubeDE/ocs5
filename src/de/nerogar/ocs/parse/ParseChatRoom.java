package de.nerogar.ocs.parse;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.security.Password;
import de.nerogar.ocs.security.RandomString;
import de.nerogar.ocs.user.User;

public class ParseChatRoom extends Parse {

	//==========
	//structure

	private static Map<String, DataType> chatRoomStructure;

	private static Map<String, DataType> chatRoomStructureCreate;
	private static Map<String, DataType> chatRoomStructureEnter;
	private static Map<String, DataType> chatRoomStructureLeave;
	private static Map<String, DataType> chatRoomStructureRemove;
	static {
		chatRoomStructure = new HashMap<String, Parse.DataType>();
		chatRoomStructure.put("action", DataType.STRING);

		chatRoomStructureCreate = new HashMap<String, Parse.DataType>();
		chatRoomStructureCreate.put("roomName", DataType.STRING);
		chatRoomStructureCreate.put("password", DataType.STRING);
		chatRoomStructureCreate.put("minPower", DataType.INTEGER);

		chatRoomStructureEnter = new HashMap<String, Parse.DataType>();
		chatRoomStructureEnter.put("roomID", DataType.INTEGER);
		chatRoomStructureEnter.put("password", DataType.STRING);

		chatRoomStructureLeave = new HashMap<String, Parse.DataType>();
		chatRoomStructureLeave.put("roomID", DataType.INTEGER);

		chatRoomStructureRemove = new HashMap<String, Parse.DataType>();
		chatRoomStructureRemove.put("roomID", DataType.INTEGER);
	}

	//structure end
	//==========

	public ParseChatRoom(User user) {
		super(user);
	}

	public void parse(JSONObject jsonData) {
		if (!validate(jsonData, chatRoomStructure)) return;
		String action = (String) jsonData.get("action");

		switch (action) {
			case "create":
				create(jsonData);
				break;

			case "enter":
				enter(jsonData);
				break;

			case "leave":
				leave(jsonData);
				break;

			case "remove":
				remove(jsonData);
				break;
		}
	}

	private void create(JSONObject jsonData) {
		if (!validate(jsonData, chatRoomStructureCreate)) return;

		String roomName = (String) jsonData.get("roomName");
		String password = (String) jsonData.get("password");

		if (getUser().isMutedAndPrevent()) return;

		if (!getUser().hasPermission(User.CREATE_CHAT_ROOM)) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("chat.room.error.create")).sendTo(getUser());
			return;
		}

		if (roomName.length() > 32) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("chat.room.error.nameLength")).sendTo(getUser());
			return;
		}

		int minPower = 0;
		if (jsonData.containsKey("minPower")) {
			minPower = ((Long) jsonData.get("minPower")).intValue();
		}
		ChatRoom newChatRoom;

		if (password.equals("")) {
			newChatRoom = new ChatRoom(roomName, getUser().getID(), minPower, "", "");
		} else {
			String pwSalt = RandomString.getNew(16);
			String pwHash = Password.hashPassword(password, pwSalt);

			newChatRoom = new ChatRoom(roomName, getUser().getID(), minPower, pwHash, pwSalt);
		}

		if (!OCSServer.chatRoomManager.addChatRoom(newChatRoom)) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("chat.room.error.create")).sendTo(getUser());
		}
	}

	private void enter(JSONObject jsonData) {
		if (!validate(jsonData, chatRoomStructureEnter)) return;

		int enterID = ((Long) jsonData.get("roomID")).intValue();

		if (!OCSServer.chatRoomManager.enterChatRoom(getUser(), enterID, (String) jsonData.get("password"))) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("chat.room.error.enter")).sendTo(getUser());
		}
	}

	private void leave(JSONObject jsonData) {
		if (!validate(jsonData, chatRoomStructureLeave)) return;

		int leaveID = ((Long) jsonData.get("roomID")).intValue();

		if (!OCSServer.chatRoomManager.leaveChatRoom(getUser(), leaveID)) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("chat.room.error.leave")).sendTo(getUser());
		}
	}

	private void remove(JSONObject jsonData) {
		if (!validate(jsonData, chatRoomStructureRemove)) return;

		int removeID = ((Long) jsonData.get("roomID")).intValue();

		if (!OCSServer.chatRoomManager.removeChatRoom(removeID, getUser())) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("chat.room.error.delete")).sendTo(getUser());
		}
	}

}
