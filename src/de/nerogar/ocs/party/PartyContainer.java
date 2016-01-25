package de.nerogar.ocs.party;

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.nerogar.ocs.*;
import de.nerogar.ocs.user.User;

public class PartyContainer extends Sendable {
	private HashMap<Integer, Party> parties;

	public PartyContainer() {
		parties = new HashMap<Integer, Party>();
	}

	public boolean addParty(Party party, User user) {
		if ((getPartyCount(user) < Config.getValue(Config.MAX_CREATED_PARTIES) || user.hasPermission(User.CREATE_UNLIMITED_PARTIES)) && user.hasPermission(User.CREATE_PARTY)) {
			parties.put(party.id, party);
			broadcast(OCSServer.userlist);
			return true;
		}

		return false;
	}

	private int getPartyCount(User user) {
		int count = 0;
		for (Party party : parties.values()) {
			if (party.ownerID == user.getID() && !party.hasEnded()) count++;
		}
		return count;
	}

	public List<Party> getParties() {
		List<Party> partyList = new ArrayList<Party>();
		for (Party party : parties.values()) {
			partyList.add(party);
		}

		return partyList;
	}

	public void reactivateUser(User user) {
		for (Party party : getParties()) {
			party.reactivateUser(user);
		}
	}

	public void flushUsers() {
		List<Party> partyList = getParties();
		for (int i = 0; i < partyList.size(); i++) {
			partyList.get(i).flush();
			if (!partyList.get(i).hasOnlineUsers() && partyList.get(i).hasStarted()) removeParty(partyList.get(i).id, null);
		}
	}

	public Party getParty(int id) {
		return parties.get(id);
	}

	public boolean removeParty(int removeID, User user) {
		Party removeParty = getParty(removeID);
		if (removeParty == null) return false;
		if (user == null || removeParty.ownerID == user.getID() || user.hasPermission(User.EDIT_ALL_PARTIES)) {
			//removeParty.kickAll();
			//if (removeParty.hasEnded()) {

			//} else {
			removeParty.close();
			broadcast(OCSServer.userlist);
			parties.remove(removeID);
			return true;
			//}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String send(User user) {
		JSONObject partylistJSON = new JSONObject();

		Collection<Party> parties = this.parties.values();

		JSONArray partiesArray = new JSONArray();
		for (Party listParty : parties) {
			JSONObject partyObject = new JSONObject();

			partyObject.put("id", listParty.id);
			partyObject.put("name", listParty.name);
			partyObject.put("cubeType", listParty.scrambleType);
			partyObject.put("rounds", listParty.rounds);
			partyObject.put("inParty", listParty.hasUser(user));
			partyObject.put("currentRound", listParty.currentRound);
			partyObject.put("closed", listParty.hasEnded());
			partyObject.put("started", listParty.hasStarted());
			partyObject.put("canEdit", listParty.canEdit(user));

			partiesArray.add(partyObject);

		}
		partylistJSON.put("parties", partiesArray);

		partylistJSON.put("type", "partylist");

		return partylistJSON.toJSONString();
	}

}
