package de.nerogar.ocs.chat;

import java.util.Random;
import java.util.regex.Matcher;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.OCSStrings;
import de.nerogar.ocs.user.User;

public class MagicShell {

	//TODO don't save this here
	private static final String[] messages = {
			//negative	
			"Nein.",
			"Sicher nicht.",
			"Auf keinen Fall.",

			//neutral
			"Vielleicht.",
			"Unwahrscheinlich.",
			"Lass mich in Ruhe!",
			"Das hast du schon mal gefragt.",
			"Frag {user}.",
			"Bin ich Jesus?",
			"Ich will nicht darüber reden.",
			"Frag doch einfach nochmal.",
			"Mach dir mal selber Gedanken.",

			//positive
			"Ja.",
			"Eines Tages vielleicht.",
			"Ich denke schon. {user}, was sagst du dazu?",
	};

	private static Random random = new Random();

	public static boolean parse(String msg, ChatRoom chatRoom, User user) {

		String shellName = OCSStrings.getString("chat.shell.name");

		String testString = msg.toLowerCase().trim();
		testString = testString.replaceAll("\\s{2,}", " ");
		if (testString.matches("^" + shellName + ".{5,}")) {

			chatRoom.addMessage(false, getResponse(), null);

			return true;
		} else {
			return false;
		}
	}

	private static String getResponse() {
		String response = messages[random.nextInt(messages.length - 1)];
		User randomUser = OCSServer.userlist.getRandomUser();
		response = response.replaceAll("\\{user}", Matcher.quoteReplacement(randomUser == null ? "null" : randomUser.getUsername()));
		return response;
	}
}
