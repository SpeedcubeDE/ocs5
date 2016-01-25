package de.nerogar.ocs.command;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class StopCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {

		for (User disconnectUser : OCSServer.userlist.getUsers()) {
			disconnectUser.logout(false);
		}

		OCSServer.saveAll();

		new Alert(Alert.INFORMATION, true, OCSStrings.getString("system.stop")).broadcast(OCSServer.userlist);

		OCSServer.saveOnlineFile(false);

		//TODO don't use System.exit to stop the server
		System.exit(0);

		return true;
	}

	@Override
	public String getName() {
		return "stop";
	}

	@Override
	public String getHelp() {
		return "/stop";
	}

}
