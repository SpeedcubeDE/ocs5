package de.nerogar.ocs.command;

import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class NameColorCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length > 0) {
			String newColor = arguments[0];
			if (newColor.matches("^[a-fA-F0-9]{6}$")) {
				user.setNameColor(newColor);
			} else {
				user.sendMessage(chatRoom, OCSStrings.getString("command.nameColor.noColor", newColor));
			}
		} else {
			user.sendMessage(chatRoom, OCSStrings.getString("command.nameColor.color", user.getNameColor()));
		}

		return true;

	}

	@Override
	public String getHelp() {
		return "/nameColor [color]";
	}

	@Override
	public String getName() {
		return "nameColor";
	}

}
