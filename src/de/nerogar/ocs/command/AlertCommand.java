package de.nerogar.ocs.command;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class AlertCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length > 0) {
			new Alert(Alert.INFORMATION, true, argumentString).broadcast(OCSServer.userlist);
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "alert";
	}

	@Override
	public String getHelp() {
		return "/alert <text>";
	}

}
