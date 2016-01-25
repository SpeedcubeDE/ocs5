package de.nerogar.ocs.command;

import java.util.List;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class UserpoolCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length == 1) {
			switch (arguments[0].toLowerCase()) {
				case "dump":
					dumpUserPool(user, chatRoom);
					break;
				case "clear":
					int oldPoolSize = OCSServer.userPool.getAllUsers().size();
					OCSServer.userPool.cleanAllOfflineusers();
					int newPoolSize = OCSServer.userPool.getAllUsers().size();

					user.sendMessage(chatRoom, OCSStrings.getString("command.userpool.clear.cleared",
							String.valueOf(oldPoolSize),
							String.valueOf(newPoolSize),
							String.valueOf(oldPoolSize - newPoolSize)));
					break;
				default:
					return false;
			}

			return true;
		} else {
			return false;
		}
	}

	private void dumpUserPool(User user, ChatRoom chatRoom) {
		List<User> userlist = OCSServer.userPool.getAllUsers();
		String[] sendList = new String[userlist.size()];

		for (int i = 0; i < userlist.size(); i++) {
			sendList[i] = (i + 1) + ": " + userlist.get(i).getUsername();
		}

		user.sendMessage(chatRoom, sendList);
	}

	@Override
	public String getName() {
		return "userpool";
	}

	@Override
	public String getHelp() {
		return "/userpool <dump/clear>";
	}

}
