package de.nerogar.ocs.command;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class RestartCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {

		new Alert(Alert.INFORMATION, true, OCSStrings.getString("system.restart")).broadcast(OCSServer.userlist);

		for (User disconnectUser : OCSServer.userlist.getUsers()) {
			disconnectUser.logout(false);
		}

		OCSServer.saveAll();

		OCSServer.saveOnlineFile(false);

		//TODO don't use System.exit to stop the server
		System.exit(OCSServer.EXIT_RESTART);

		return true;
	}

	@Override
	public String getName() {
		return "restart";
	}

	@Override
	public String getHelp() {
		return "/restart";
	}

}
