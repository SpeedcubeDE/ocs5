package de.nerogar.ocs.command;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.*;
import de.nerogar.ocs.user.User;

public class KickCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length >= 1) return executeOnUser(arguments, argumentString, user, chatRoom, arguments[0]);
		return false;
	}

	@Override
	protected boolean onExecuteOnUser(String[] arguments, String argumentString, User user, ChatRoom chatRoom, User targetUser) {
		new Alert(Alert.WARNING, true, OCSStrings.getString("command.kick.kickedAlert", getArgumentString(argumentString, 1))).sendTo(targetUser);
		targetUser.logout(true);
		new ChatMessage(-1, chatRoom, false, OCSStrings.getString("command.kick.kicked", targetUser.getUsername()), OCSServer.getTimestamp()).broadcast(chatRoom.userlist);
		return true;
	}

	@Override
	public String getName() {
		return "kick";
	}

	@Override
	public String getHelp() {
		return "/kick <username> [reason]";
	}
}
