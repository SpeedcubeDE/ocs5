package de.nerogar.ocs.command;

import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class MeCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length > 0) {
			chatRoom.addMessage(true, argumentString, user);

			return true;
		}

		return false;
	}

	@Override
	public String getName() {
		return "me";
	}

	@Override
	public String getHelp() {
		return "/me <text>";
	}

}
