package de.nerogar.ocs.user;

import java.util.*;

import de.nerogar.ocs.Config;
import de.nerogar.ocs.OCSServer;

public class UserPool {
	private static HashMap<Integer, User> userMap;
	private static LinkedList<User> users;

	public UserPool() {
		userMap = new HashMap<Integer, User>();
		users = new LinkedList<User>();
	}

	public User getUser(int userID) {
		User user = userMap.get(userID);

		if (user == null) {
			user = OCSServer.databaseUser.getUserByID(userID);
			if (user == null) return null;

			user.setProfile(new Profile(user));

			addUserToCache(user);
		} else {
			if (user.getProfile().isDirty()) {
				user.getProfile().loadFromDatabase();
			}
		}

		cleanOldUser();

		return user;
	}

	private void addUserToCache(User user) {
		userMap.put(user.getID(), user);
		users.addLast(user);

		cleanOldUser();
	}

	/**
	 * this will try to clean as many users as possible to get to the MAX_USER_POOL_SIZE config
	 */
	private void cleanOldUser() {
		boolean userRemoved;

		do {
			userRemoved = false;

			if (users.size() > Config.getValue(Config.MAX_USER_POOL_SIZE)) {
				User oldestUser = users.getFirst();
				if (!oldestUser.isLoggedIn()) {
					OCSServer.databaseUser.saveUser(oldestUser);
					users.removeFirst();
					userMap.remove(oldestUser.getID());

					userRemoved = true;
				}
			}

		} while (userRemoved);

	}

	/**
	 * this will clean all offline users
	 */
	public void cleanAllOfflineusers() {
		for (int i = users.size() - 1; i >= 0; i--) {

			User removeUser = users.get(i);
			if (!removeUser.isLoggedIn()) {
				OCSServer.databaseUser.saveUser(removeUser);
				users.remove(i);
				userMap.remove(removeUser.getID());
			}
		}
	}

	public void setProfileDirty(int userID) {
		User user = userMap.get(userID);
		if (user != null) user.getProfile().setDirty();
	}

	public int getUserIDByToken(String authToken) {
		return OCSServer.databaseUser.getUserIDByToken(authToken);
	}

	public int getUserIDByName(String username) {
		for (User user : users) {
			if (user.getUsername().equals(username)) return user.getID();
		}
		return OCSServer.databaseUser.getUserIDByName(username);
	}

	public List<User> getAllUsers() {
		ArrayList<User> returnList = new ArrayList<User>();
		for (User u : users) {
			returnList.add(u);
		}
		return returnList;
	}

	public void saveAll() {
		for (User user : users) {
			OCSServer.databaseUser.saveUser(user);
		}
	}

}
