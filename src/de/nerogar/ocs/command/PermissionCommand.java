package de.nerogar.ocs.command;

import java.util.ArrayList;

import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class PermissionCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length == 1) {
			String arg0 = arguments[0];
			if (arg0.equalsIgnoreCase("list")) {

				ArrayList<String> permissionList = new ArrayList<String>();
				for (String permissionName : User.permissions.keySet()) {
					permissionList.add(permissionName + " = " + User.permissions.get(permissionName));
				}
				user.sendMessage(chatRoom, permissionList.toArray(new String[0]));

			} else {
				Integer value = User.permissions.get(arg0);
				if (value != null) {
					user.sendMessage(chatRoom, arguments[0] + " = " + value);
				} else {
					user.sendMessage(chatRoom, OCSStrings.getString("command.permission.error.find", arguments[0]));
				}
			}
			return true;

		} else if (arguments.length == 2) {
			String name = arguments[0];
			int value = User.permissions.get(User.MAX_POWER);

			try {
				value = Integer.parseInt(arguments[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(chatRoom, OCSStrings.getString("command.numberFormat", arguments[1]));
				return false;
			}

			if (User.permissions.containsKey(name)) {
				User.permissions.put(name, value);
				user.sendMessage(chatRoom, OCSStrings.getString("command.permission.change", arguments[0], String.valueOf(value)));
			} else {
				user.sendMessage(chatRoom, OCSStrings.getString("command.permission.error.find", arguments[0]));
			}

			return true;
		}

		return false;
	}

	@Override
	public String getHelp() {
		return "/permission <permissionName/list> [power]";
	}

	@Override
	public String getName() {
		return "permission";
	}
}
