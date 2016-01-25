package de.nerogar.ocs.command;

import java.util.ArrayList;

import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class HelpCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		Command[] commands = Command.getCommands();

		ArrayList<String> helpString = new ArrayList<String>();

		for (Command command : commands) {
			if (command.getMinPower() <= user.getPower()) {
				helpString.add(command.getHelp());
			}
		}
		user.sendMessage(chatRoom, helpString.toArray(new String[0]));

		return true;
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getHelp() {
		return "/help";
	}

}
