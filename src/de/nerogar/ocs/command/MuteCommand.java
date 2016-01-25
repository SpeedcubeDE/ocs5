package de.nerogar.ocs.command;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class MuteCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length >= 2) return executeOnUser(arguments, argumentString, user, chatRoom, arguments[0]);
		return false;
	}

	@Override
	protected boolean onExecuteOnUser(String[] arguments, String argumentString, User user, ChatRoom chatRoom, User targetUser) {
		long muteTime = Time.parseTime(arguments[1]);

		new Alert(Alert.WARNING, true, OCSStrings.getString("chat.mute.muted", Time.asStringDelta(muteTime), getArgumentString(argumentString, 2))).sendTo(targetUser);
		targetUser.setMuteTime(muteTime + OCSServer.getTimestamp());
		user.sendMessage(chatRoom, OCSStrings.getString("command.mute.muted", arguments[0], Time.asStringDelta(muteTime)));
		return true;
	}

	@Override
	public String getName() {
		return "mute";
	}

	@Override
	public String getHelp() {
		return "/mute <username> <time> [reason]";
	}

}
