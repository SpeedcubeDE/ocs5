package de.nerogar.ocs.parse;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.Alert;
import de.nerogar.ocs.party.Party;
import de.nerogar.ocs.user.User;

public class ParseParty extends Parse {

	//==========
	//structure

	private static Map<String, DataType> partyStructure;

	private static Map<String, DataType> partyStructureCreate;
	private static Map<String, DataType> partyStructureEnter;
	private static Map<String, DataType> partyStructureLeave;
	private static Map<String, DataType> partyStructureStart;
	private static Map<String, DataType> partyStructureRemove;
	private static Map<String, DataType> partyStructureTime;
	private static Map<String, DataType> partyStructureKick;
	static {
		partyStructure = new HashMap<String, Parse.DataType>();
		partyStructure.put("action", DataType.STRING);

		partyStructureCreate = new HashMap<String, Parse.DataType>();
		partyStructureCreate.put("name", DataType.STRING);
		partyStructureCreate.put("rounds", DataType.INTEGER);
		partyStructureCreate.put("cubeType", DataType.STRING);
		partyStructureCreate.put("ranking", DataType.STRING);
		partyStructureCreate.put("mode", DataType.STRING);

		partyStructureEnter = new HashMap<String, Parse.DataType>();
		partyStructureEnter.put("partyID", DataType.INTEGER);

		partyStructureLeave = new HashMap<String, Parse.DataType>();
		partyStructureLeave.put("partyID", DataType.INTEGER);

		partyStructureStart = new HashMap<String, Parse.DataType>();
		partyStructureStart.put("partyID", DataType.INTEGER);

		partyStructureRemove = new HashMap<String, Parse.DataType>();
		partyStructureRemove.put("partyID", DataType.INTEGER);

		partyStructureTime = new HashMap<String, Parse.DataType>();
		partyStructureTime.put("partyID", DataType.INTEGER);
		partyStructureTime.put("round", DataType.INTEGER);
		partyStructureTime.put("time", DataType.INTEGER);

		partyStructureKick = new HashMap<String, Parse.DataType>();
		partyStructureKick.put("partyID", DataType.INTEGER);
		partyStructureKick.put("userID", DataType.INTEGER);
	}

	//structure end
	//==========

	public ParseParty(User user) {
		super(user);
	}

	public void parse(JSONObject jsonData) {
		if (!validate(jsonData, partyStructure)) return;
		String action = (String) jsonData.get("action");

		switch (action) {
			case "create":
				create(jsonData);
				break;

			case "enter":
				enter(jsonData);
				break;

			case "leave":
				leave(jsonData);
				break;

			case "start":
				start(jsonData);
				break;

			case "remove":
				remove(jsonData);
				break;

			case "time":
				time(jsonData);
				break;

			case "kick":
				kick(jsonData);
				break;
		}
	}

	private void create(JSONObject jsonData) {
		if (!validate(jsonData, partyStructureCreate)) return;

		if (getUser().isMutedAndPrevent()) { return; }

		String partyName = (String) jsonData.get("name");
		int rounds = ((Long) jsonData.get("rounds")).intValue();
		String cubeType = (String) jsonData.get("cubeType");
		String ranking = (String) jsonData.get("ranking");
		String mode = (String) jsonData.get("mode");

		if (partyName.equals("")) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("party.error.name")).sendTo(getUser());
			return;
		}

		if (rounds < 1 || rounds > Config.getValue(Config.MAX_PARTY_ROUNDS)) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("party.error.rounds", String.valueOf(Config.getValue(Config.MAX_PARTY_ROUNDS)))).sendTo(getUser());
			return;
		}

		Party party = new Party(partyName, rounds, cubeType, ranking, Party.getModeID(mode, getUser()), getUser());

		if (!OCSServer.partyContainer.addParty(party, getUser())) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("party.error.create")).sendTo(getUser());
		}
	}

	private void enter(JSONObject jsonData) {
		if (!validate(jsonData, partyStructureEnter)) return;

		int partyID = ((Long) jsonData.get("partyID")).intValue();
		Party party = OCSServer.partyContainer.getParty(partyID);

		if (party == null || !party.addUser(getUser())) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("party.error.enter")).sendTo(getUser());
		}
	}

	private void leave(JSONObject jsonData) {
		if (!validate(jsonData, partyStructureLeave)) return;

		int partyID = ((Long) jsonData.get("partyID")).intValue();
		Party party = OCSServer.partyContainer.getParty(partyID);
		
		if (party == null || !OCSServer.partyContainer.getParty(partyID).removeUser(getUser())) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("party.error.leave")).sendTo(getUser());
		}
	}

	private void start(JSONObject jsonData) {
		if (!validate(jsonData, partyStructureStart)) return;

		int partyID = ((Long) jsonData.get("partyID")).intValue();
		Party party = OCSServer.partyContainer.getParty(partyID);
		
		if (party == null || !OCSServer.partyContainer.getParty(partyID).start(getUser())) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("party.error.start")).sendTo(getUser());
		}
	}

	private void remove(JSONObject jsonData) {
		if (!validate(jsonData, partyStructureRemove)) return;

		int partyID = ((Long) jsonData.get("partyID")).intValue();
		
		if (!OCSServer.partyContainer.removeParty(partyID, getUser())) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("party.error.delete")).sendTo(getUser());
		}
	}

	private void time(JSONObject jsonData) {
		if (!validate(jsonData, partyStructureTime)) return;

		int partyID = ((Long) jsonData.get("partyID")).intValue();
		int timeIndex = ((Long) jsonData.get("round")).intValue();
		int time = ((Long) jsonData.get("time")).intValue();

		Party party = OCSServer.partyContainer.getParty(partyID);

		if (!party.setTime(getUser(), timeIndex, time)) {
			new Alert(Alert.ERROR, false, OCSStrings.getString("party.error.time")).sendTo(getUser());
		}
	}

	private void kick(JSONObject jsonData) {
		if (!validate(jsonData, partyStructureKick)) return;

		int partyID = ((Long) jsonData.get("partyID")).intValue();
		int userID = ((Long) jsonData.get("userID")).intValue();

		Party party = OCSServer.partyContainer.getParty(partyID);
		User user = OCSServer.userPool.getUser(userID);

		if (party != null && party.canEdit(getUser())) {
			party.removeUser(user);
		}
	}
}
