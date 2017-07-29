package de.nerogar.ocs.command;

import java.util.List;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.user.User;

public class SetRankCommand extends Command {

	@Override
	public boolean execute(String[] arguments, String argumentString, User user, ChatRoom chatRoom) {
		if (arguments.length == 2) return executeOnUser(arguments, argumentString, user, chatRoom, arguments[0]);
		return false;
	}

	@Override
	protected boolean onExecuteOnUser(String[] arguments, String argumentString, User user, ChatRoom chatRoom, User targetUser) {
		String newRank = arguments[1];

		Integer newPower = Rank.getPower(newRank);
		if (newPower == null) {
			user.sendMessage(chatRoom, OCSStrings.getString("command.setRank.error.rank", newRank));
			return true;
		}

		if (newPower < user.getPower() || user.hasPermission(User.MAX_POWER)) {
			targetUser.setPower(newPower);
			user.sendMessage(chatRoom, OCSStrings.getString("command.setRank.change", targetUser.getUsername(), Rank.getRankString(targetUser.getPower(), true)));
			new Alert(Alert.INFORMATION, true, OCSStrings.getString("command.setRank.changeOwn", Rank.getRankString(targetUser.getPower(), true))).sendTo(targetUser);
		} else {
			user.sendMessage(chatRoom, OCSStrings.getString("command.setRank.error"));
		}

		return true;
	}

	@Override
	public String getName() {
		return "setRank";
	}

	@Override
	public String getHelp() {
		StringBuilder ranksString = new StringBuilder();
		List<Rank> ranks = Rank.getRanks();

		for (int i = 0; i < ranks.size(); i++) {
			ranksString.append(ranks.get(i).name);
			if (i < ranks.size() - 1) ranksString.append("|");
		}

		return "/setRank <username> <" + ranksString.toString() + ">";
	}

}
