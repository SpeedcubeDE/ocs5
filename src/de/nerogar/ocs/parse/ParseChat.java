package de.nerogar.ocs.parse;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.*;
import de.nerogar.ocs.command.Command;
import de.nerogar.ocs.user.User;

public class ParseChat extends Parse {

	//==========
	//structure

	private static Map<String, DataType> chatStructure;
	static {
		chatStructure = new HashMap<String, Parse.DataType>();
		chatStructure.put("msg", DataType.STRING);
		chatStructure.put("roomID", DataType.INTEGER);
	}

	//structure end
	//==========

	private long lastMsgTime;
	private long lastPreventTime;

	public ParseChat(User user) {
		super(user);
	}

	@Override
	public void parse(JSONObject jsonData) {
		if (!validate(jsonData, chatStructure)) return;

		long currentTime = OCSServer.getTimestamp();

		//spam prevention
		if (((currentTime - lastMsgTime) < Config.getValue(Config.MIN_CHAT_MSG_DELAY)) && !getUser().hasPermission(User.SPAM_ALLOWED)) {

			if ((currentTime - lastPreventTime) > OCSServer.get1SecondTimestamp() * 5) {
				new Alert(Alert.INFORMATION, false, OCSStrings.getString("chat.spam.prevent")).sendTo(getUser());
				lastPreventTime = currentTime;
			}
			return;
		}
		lastMsgTime = currentTime;

		//parse
		String chatString = (String) jsonData.get("msg");
		int chatRoomID = ((Long) jsonData.get("roomID")).intValue();

		if (chatString.isEmpty()) return;

		ChatRoom chatRoom = OCSServer.chatRoomManager.getChatRoom(chatRoomID);
		if (chatRoom == null || !chatRoom.hasUser(getUser())) return;

		if (chatString.startsWith("/")) {
			String commandName = chatString.substring(1).split(" ")[0].toLowerCase();

			String argumentString = chatString.substring(commandName.length() + 1, chatString.length()).trim();
			String[] arguments;

			//this prevents an array of length 1 with no arguments
			if (argumentString.equals("")) {
				arguments = new String[0];
			} else {
				arguments = argumentString.split(" ");
			}

			Command command = Command.getCommand(commandName);

			if (command == null) {
				getUser().sendMessage(chatRoom, OCSStrings.getString("command.unknown", commandName));
			} else if (getUser().getPower() < command.getMinPower()) {
				getUser().sendMessage(chatRoom, OCSStrings.getString("command.insufficientPower"));
			} else {
				if (!command.execute(arguments, argumentString, getUser(), chatRoom)) {
					getUser().sendMessage(chatRoom, command.getHelp());
				} else {
					OCSServer.databaseLog.logCommand(getUser(), OCSServer.getTimestamp(), command.getName(), argumentString);
				}
			}

		} else {
			chatRoom.addMessage(false, chatString, getUser());
			MagicShell.parse(chatString, chatRoom, getUser());
		}
	}
}
