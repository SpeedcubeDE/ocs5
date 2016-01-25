package de.nerogar.ocs.command;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class StatusCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (!user.isMutedAndPrevent()) {
			if (argumentString.length() <= Config.getValue(Config.MAX_STATUS_LENGTH)) {
				user.setStatus(argumentString);
			} else {
				user.sendMessage(chatRoom, OCSStrings.getString("command.status.error.tooLong", String.valueOf(Config.getValue(Config.MAX_STATUS_LENGTH))));
			}
		}
		return true;
	}

	@Override
	public String getHelp() {
		return "/status <status>";
	}

	@Override
	public String getName() {
		return "status";
	}

}
