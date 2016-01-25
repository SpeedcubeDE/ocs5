package de.nerogar.ocs.chat;

import de.nerogar.ocs.user.User;

public class WhisperRoom extends ChatRoom {

	public User partner1;
	public User partner2;

	public WhisperRoom(String name, int ownerID, int minPower, String password, String salt) {
		super(name, ownerID, minPower, password, salt);
		persistant = false;
	}

	@Override
	public void addMessage(boolean me, String chatString, User user) {
		if (!userlist.hasUser(partner1) && partner1.isLoggedIn()) addUser(partner1);
		if (!userlist.hasUser(partner2) && partner2.isLoggedIn()) addUser(partner2);
		super.addMessage(me, chatString, user);
	}

	@Override
	public boolean canEnter(User user) {
		return user.equals(partner1) || user.equals(partner2);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof WhisperRoom) {
			WhisperRoom whisperRoom = (WhisperRoom) o;
			return (whisperRoom.partner1.equals(partner1) && whisperRoom.partner2.equals(partner2)) || (whisperRoom.partner1.equals(partner2) && whisperRoom.partner2.equals(partner1));
		}
		return false;
	}

	public String getType() {
		return "whisper";
	}
}
