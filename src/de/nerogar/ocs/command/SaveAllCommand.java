package de.nerogar.ocs.command;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class SaveAllCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {

		long time = OCSServer.saveAll();
		user.sendMessage(chatRoom, OCSStrings.getString("command.saveAll.saved", Time.asStringDelta(time)));
		return true;
	}

	@Override
	public String getName() {
		return "saveAll";
	}

	@Override
	public String getHelp() {
		return "/saveAll";
	}

}
