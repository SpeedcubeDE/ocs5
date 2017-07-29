package de.nerogar.ocs.command;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class SetPowerCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length == 2) return executeOnUser(arguments, argumentString, user, chatRoom, arguments[0]);
		return false;
	}

	@Override
	protected boolean onExecuteOnUser(String[] arguments, String argumentString, User user, ChatRoom chatRoom, User targetUser) {
		int newPower;

		try {
			newPower = Integer.parseInt(arguments[1]);
		} catch (NumberFormatException e) {
			user.sendMessage(chatRoom, OCSStrings.getString("command.numberFormat", arguments[1]));
			return true;
		}

		if (newPower < user.getPower() || user.hasPermission(User.MAX_POWER)) {
			targetUser.setPower(newPower);
			user.sendMessage(chatRoom, OCSStrings.getString("command.setPower.change", targetUser.getUsername(), String.valueOf(targetUser.getPower())));
			new Alert(Alert.INFORMATION, true, OCSStrings.getString("command.setPower.changeOwn",String.valueOf(targetUser.getPower()))).sendTo(targetUser);
		} else {
			user.sendMessage(chatRoom, OCSStrings.getString("command.setPower.error"));
		}

		return true;
	}

	@Override
	public String getName() {
		return "setPower";
	}

	@Override
	public String getHelp() {
		return "/setPower <username> <power>";
	}

}
