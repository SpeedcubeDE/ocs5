package de.nerogar.ocs;

import de.nerogar.ocs.user.User;

public abstract class Sendable {

	public abstract String send(User user);

	public void sendTo(User user) {
		if (user != null) {
			user.sendRawMessage(send(user));
		}
	}

	public void broadcast(Userlist userlist) {
		for (User user : userlist.getUsers()) {
			user.sendRawMessage(send(user));
		}
	}
}
