package de.nerogar.ocs.command;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.*;
import de.nerogar.ocs.user.User;

public class BanCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length >= 1) return executeOnUser(arguments, argumentString, user, chatRoom, arguments[0]);
		return false;
	}

	@Override
	protected boolean onExecuteOnUser(String[] arguments, String argumentString, User user, ChatRoom chatRoom, User targetUser) {
		targetUser.setBanReason(getArgumentString(argumentString, 1));
		targetUser.setPower(-1);
		new Alert(Alert.WARNING, true, OCSStrings.getString("command.ban.bannedAlert", targetUser.getBanReason())).sendTo(targetUser);
		targetUser.logout(true);
		targetUser.resetLoginToken();

		new ChatMessage(-1, chatRoom, false, OCSStrings.getString("command.ban.banned", arguments[0], targetUser.getBanReason()), OCSServer.getTimestamp()).broadcast(chatRoom.userlist);

		return true;
	}

	@Override
	public String getName() {
		return "ban";
	}

	@Override
	public String getHelp() {
		return "/ban <username> [reason]";
	}

}
