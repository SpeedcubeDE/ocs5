package de.nerogar.ocs.command;

import java.util.ArrayList;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.*;
import de.nerogar.ocs.user.User;

public class ConfigCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length == 1) {
			String arg1 = arguments[0];
			if (arg1.equalsIgnoreCase("list")) {

				ArrayList<String> configList = new ArrayList<String>();
				for (String configName : Config.configs.keySet()) {
					configList.add(configName + " = " + Config.getValue(configName));
				}
				user.sendMessage(chatRoom, configList);

			} else {
				int value = Config.getValue(arg1);
				user.sendMessage(chatRoom, arguments[0] + " = " + value);
			}
			return true;

		} else if (arguments.length == 2) {
			String name = arguments[0];
			int value = 0;

			try {
				value = Integer.parseInt(arguments[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(chatRoom, OCSStrings.getString("command.numberFormat", arguments[1]));
				return false;
			}

			if (Config.setValue(name, value)) {
				user.sendMessage(chatRoom, OCSStrings.getString("command.config.change", arguments[0], String.valueOf(value)));
			} else {
				user.sendMessage(chatRoom, OCSStrings.getString("command.config.error", arguments[0]));
			}

			return true;
		}

		return false;
	}

	@Override
	public String getName() {
		return "config";
	}

	@Override
	public String getHelp() {
		return "/config <configName/list> [value]";
	}

}
