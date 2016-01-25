package de.nerogar.ocs.command;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.chat.Sound;
import de.nerogar.ocs.user.User;

public class SoundCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		Sound sound;

		if (arguments.length >= 1) {
			float volume = 1f;
			if (arguments.length >= 2) {
				try {
					volume = Float.parseFloat(arguments[1]);
				} catch (NumberFormatException e) {
					user.sendMessage(chatRoom, OCSStrings.getString("command.numberFormat", arguments[1]));
				}
			}
			sound = new Sound(arguments[0], volume);

			if (arguments.length >= 3) {
				User target = OCSServer.userlist.getUser(OCSServer.userPool.getUserIDByName(arguments[2]));
				if (target != null && target.isLoggedIn()) {
					sound.sendTo(target);
					user.sendMessage(chatRoom, OCSStrings.getString("command.sound.userPlayed", arguments[0], target.getUsername()));
				} else {
					user.sendMessage(chatRoom, OCSStrings.getString("command.userNotFound", arguments[2]));
				}
			} else {
				sound.broadcast(OCSServer.userlist);
				user.sendMessage(chatRoom, OCSStrings.getString("command.sound.played", arguments[0]));
			}
			return true;
		}

		return false;
	}

	@Override
	public String getName() {
		return "sound";
	}

	@Override
	public String getHelp() {
		return "/sound <soundFile> [volume] [username]";
	}

}
