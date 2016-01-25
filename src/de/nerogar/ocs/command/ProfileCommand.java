package de.nerogar.ocs.command;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class ProfileCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length < 1) return false;
		User targetUser = OCSServer.userPool.getUser(OCSServer.userPool.getUserIDByName(arguments[0]));

		if (targetUser == null) {
			user.sendMessage(chatRoom, OCSStrings.getString("command.userNotFound", arguments[0]));
		} else {
			targetUser.getProfile().sendTo(user);
		}

		return true;
	}

	@Override
	public String getHelp() {
		return "/profile <username>";
	}

	@Override
	public String getName() {
		return "profile";
	}

}
