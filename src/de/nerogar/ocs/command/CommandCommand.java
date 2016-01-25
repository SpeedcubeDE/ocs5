package de.nerogar.ocs.command;

import java.util.ArrayList;

import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class CommandCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {

		if (arguments.length == 1) {
			String arg1 = arguments[0];
			if (arg1.equalsIgnoreCase("list")) {

				ArrayList<String> commandList = new ArrayList<String>();
				for (Command command : Command.getCommands()) {
					commandList.add(command.getName() + " : " + command.getMinPowerReal());
				}
				user.sendMessage(chatRoom, commandList);

			} else {
				Command command = Command.getCommand(arg1);
				if (command == null) user.sendMessage(chatRoom, OCSStrings.getString("command.command.error.find", arg1));
				else user.sendMessage(chatRoom, command.getName() + " : " + command.getMinPowerReal());
			}
			return true;

		} else if (arguments.length == 2) {
			String arg0 = arguments[0];
			int newPower;

			try {
				newPower = Integer.parseInt(arguments[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(chatRoom, OCSStrings.getString("command.numberFormat", arguments[1]));
				return false;
			}

			Command command = Command.getCommand(arg0);
			if (command != null) {
				command.setMinPower(newPower);
				user.sendMessage(chatRoom, OCSStrings.getString("command.command.change", arguments[0], String.valueOf(newPower)));
			} else {
				user.sendMessage(chatRoom, OCSStrings.getString("command.command.error.find", arguments[0]));
			}

			return true;
		}

		return false;

	}

	@Override
	public String getHelp() {
		return "/command <commandName/list> [power]";
	}

	@Override
	public String getName() {
		return "command";
	}

}
