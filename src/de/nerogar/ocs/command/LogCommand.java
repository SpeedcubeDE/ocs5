package de.nerogar.ocs.command;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class LogCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length >= 2) return executeOnUser(arguments, argumentString, user, chatRoom, arguments[0]);
		return false;
	}

	@Override
	protected boolean onExecuteOnUser(String[] arguments, String argumentString, User user, ChatRoom chatRoom, User targetUser) {
		int limit = 10;

		if (arguments.length > 2) {
			try {
				limit = Integer.parseInt(arguments[2]);
			} catch (NumberFormatException e) {
				user.sendMessage(chatRoom, OCSStrings.getString("command.numberFormat", arguments[2]));
				return true;
			}
		}

		switch (arguments[1].toLowerCase()) {
		case "login":
			user.sendMessage(chatRoom, OCSServer.databaseLog.getLogLogin(targetUser.getID(), limit));
			break;
		case "command":
			user.sendMessage(chatRoom, OCSServer.databaseLog.getLogCommand(targetUser.getID(), limit));
			break;
		case "duplicate":
			user.sendMessage(chatRoom, OCSServer.databaseLog.getLogDuplicate(targetUser.getID(), limit));
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return "log";
	}

	@Override
	public String getHelp() {
		return "/log <username> <login/command/duplicate> [limit]";
	}
}
