package de.nerogar.ocs.party;

import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerProvider;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.Sound;
import de.nerogar.ocs.user.User;

public class Party extends Sendable {

	ScramblerProvider scramblerProvider = new ScramblerProvider();
	private static int MAX_ID;

	public static final int MODE_NORMAL = 0;
	public static final int MODE_COMP = 1;
	public static final int MODE_MODERATED = 2;

	public int id;
	public String name;
	public int ownerID;
	public String scrambleType;
	public String ranking;
	public int rounds;
	public long startTime;
	public int mode;

	private Userlist userlist;
	public PartyUserResult[] results;
	public String[] scrambles;
	private Scrambler scrambler;
	private long lastScrambleTime;

	public int currentRound;

	private boolean ended;
	public boolean saved; //saved in the database

	public Party(String name, int rounds, String scrambleType, String ranking, int mode, User user) {
		id = getNewID();
		this.name = name;
		ownerID = user.getID();
		userlist = new Userlist();
		this.scrambleType = scrambleType;
		this.rounds = rounds;
		this.mode = mode;

		this.ranking = ranking;
	}

	public boolean canEdit(User user) {
		return user.getID() == ownerID || user.hasPermission(User.EDIT_ALL_PARTIES);
	}

	public boolean addUser(User user) {
		if (!userlist.hasUser(user) && !hasStarted() && !hasEnded()) {
			userlist.addUser(user);
			OCSServer.partyContainer.broadcast(OCSServer.userlist);
			broadcast(OCSServer.userlist);

			return true;
		}
		return false;
	}

	public void reactivateUser(User user) {
		if (isRunning()) {
			for (PartyUserResult pur : results) {
				if (pur.userID == user.getID()) {
					userlist.addUser(user);
					OCSServer.partyContainer.broadcast(userlist);
					broadcast(OCSServer.userlist);
				}
			}
		}
	}

	public boolean removeUser(User user) {
		if (userlist.hasUser(user)) {
			userlist.removeUser(user);
			if (hasStarted()) broadcast(OCSServer.userlist);

			if (isRoundDone()) {
				startNextRound();
			}

			OCSServer.partyContainer.broadcast(OCSServer.userlist);
			broadcast(OCSServer.userlist);

			return true;
		}
		return false;
	}

	public boolean start(User user) {
		if (hasStarted()) return false;
		Sound.PARTY_NEW_ROUND.broadcast(userlist);

		if (canEdit(user)) {
			results = new PartyUserResult[userlist.userCount()];
			scrambles = new String[rounds];
			startTime = OCSServer.getTimestamp();

			scrambler = scramblerProvider.get(ScrambleTypes.types.get(scrambleType));
			genScramble(0);

			List<User> userCollection = userlist.getUsers();

			int index = 0;

			for (User partyUser : userCollection) {
				results[index] = new PartyUserResult(partyUser.getID(), rounds, ranking);
				index++;
			}

			broadcast(OCSServer.userlist);
			OCSServer.partyContainer.broadcast(OCSServer.userlist);
			return true;
		}
		return false;
	}

	public boolean isRunning() {
		return hasStarted() && !hasEnded();
	}

	public boolean hasUser(User user) {
		return userlist.hasUser(user);
	}

	public boolean setTime(User user, int index, int time) {
		if (!hasStarted() || hasEnded()) return false;

		if (hasUser(user)) {
			for (PartyUserResult pur : results) {
				if (pur.userID == user.getID()) {
					if (pur.setTime(currentRound, index, time)) {
						if (isRoundDone()) {
							startNextRound();
						}

						Arrays.sort(results);
						broadcast(OCSServer.userlist);

						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean isRoundDone() {
		if (hasStarted()) {
			boolean onlineUsersDone = true;
			for (PartyUserResult pur : results) {
				if (userlist.hasUser(pur.userID) && !pur.timeSet[currentRound]) onlineUsersDone = false;
			}

			if (onlineUsersDone) {
				for (PartyUserResult pur : results) {
					if (!userlist.hasUser(pur.userID) && !pur.timeSet[currentRound]) pur.setTime(currentRound, currentRound, PartyUserResult.DNS);
				}
			}

			for (PartyUserResult pur : results) {
				if (!pur.timeSet[currentRound]) return false;
			}
			return true;
		}
		return false;
	}

	private void startNextRound() {
		Sound.PARTY_NEW_ROUND.broadcast(userlist);

		if (currentRound == rounds - 1) {

			for (User user : userlist.getUsers()) {
				user.getProfile().setDirty();
				//OCSServer.userPool.setProfileDirty(user.id);
			}

			OCSServer.databaseParty.saveParty(this);
			OCSServer.partyContainer.removeParty(id, null);
		} else {
			currentRound++;
			genScramble(currentRound);
		}

		OCSServer.partyContainer.broadcast(OCSServer.userlist);
	}

	private void genScramble(int index) {

		long currentTime = OCSServer.getTimestamp();
		if (currentTime > lastScrambleTime + OCSServer.get1SecondTimestamp()) {
			scrambles[index] = (scrambler == null) ? "" : scrambler.getNextScramble().getRawSequence();
		} else {
			scrambles[index] = OCSStrings.getString("party.error.scrambleLimit");
		}
		lastScrambleTime = currentTime;
	}

	public boolean hasStarted() {
		return results != null;
	}

	public boolean hasEnded() {
		return ended;
	}

	public boolean hasOnlineUsers() {
		for (User user : userlist.getUsers()) {
			if (user.isLoggedIn()) return true;
		}

		return false;
	}

	public void close() {
		ended = true;
		broadcast(OCSServer.userlist);
	}

	public void kickAll() {
		userlist.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String send(User user) {
		JSONObject partyJSON = new JSONObject();

		JSONArray resultArray = new JSONArray();

		if (hasStarted()) {
			for (int i = 0; i < results.length; i++) {
				PartyUserResult result = results[i];

				JSONObject resultObject = new JSONObject();
				JSONArray timesArray = new JSONArray();

				for (int time : result.times) {
					timesArray.add(time);
				}

				resultObject.put("userID", result.userID);
				resultObject.put("times", timesArray);
				resultObject.put("avg", result.avg);
				resultObject.put("mean", result.mean);

				JSONArray avgArray = new JSONArray();

				for (Average avg : result.averages) {
					avgArray.add(getAverageObject(avg));
				}

				resultObject.put("avgs", avgArray);

				resultObject.put("place", i);

				resultObject.put("inParty", userlist.hasUser(result.userID));

				resultArray.add(resultObject);
			}
			partyJSON.put("result", resultArray);

			JSONArray scrambleArray = new JSONArray();
			for (int i = 0; i <= currentRound; i++) {
				scrambleArray.add(scrambles[i]);
			}

			partyJSON.put("scrambles", scrambleArray);

		} else {
			int place = 0;
			for (User partyUser : userlist.getUsers()) {

				JSONObject resultObject = new JSONObject();

				resultObject.put("userID", partyUser.getID());

				resultObject.put("place", place);
				place++;

				resultObject.put("inParty", true);

				resultArray.add(resultObject);
			}
			partyJSON.put("result", resultArray);

		}

		partyJSON.put("ranking", ranking);

		partyJSON.put("id", id);
		partyJSON.put("type", "party");

		return partyJSON.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private JSONObject getAverageObject(Average avg) {
		JSONObject avgJSON = new JSONObject();
		avgJSON.put("name", avg.name);
		avgJSON.put("time", avg.time);
		avgJSON.put("start", avg.startIndex);
		avgJSON.put("length", avg.length);
		return avgJSON;
	}

	public static int getModeID(String mode, User user) {
		if (user.hasPermission(User.CREATE_PARTY_WITH_MODE)) {

			switch (mode) {
				case "normal":
					return MODE_NORMAL;
				case "comp":
					return MODE_COMP;
				case "moderated":
					return MODE_MODERATED;
				default:
					return MODE_NORMAL;
			}
		} else {
			return MODE_NORMAL;
		}
	}

	public static int getNewID() {
		MAX_ID++;
		return MAX_ID - 1;
	}

	static {
		MAX_ID = OCSServer.databaseParty.getNextPartyID();
	}

	public void flush() {
		for (User user : userlist.getUsers()) {
			if (!user.isLoggedIn()) {
				removeUser(user);
			}
		}
	}

}
