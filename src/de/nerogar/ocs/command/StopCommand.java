package de.nerogar.ocs.command;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class StopCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length < 1) return false;
		if (!OCSServer.name.equals(arguments[0])) return false;

		new Alert(Alert.INFORMATION, true, OCSStrings.getString("system.stop")).broadcast(OCSServer.userlist);

		for (User disconnectUser : OCSServer.userlist.getUsers()) {
			disconnectUser.logout(false);
		}

		OCSServer.saveAll();

		OCSServer.saveOnlineFile(false);

		//TODO don't use System.exit to stop the server
		System.exit(OCSServer.EXIT_STOP);

		return true;
	}

	@Override
	public String getName() {
		return "stop";
	}

	@Override
	public String getHelp() {
		return "/stop " + OCSServer.name;
	}

}
