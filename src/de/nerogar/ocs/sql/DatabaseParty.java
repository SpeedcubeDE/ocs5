package de.nerogar.ocs.sql;

import de.nerogar.ocs.party.Party;
import de.nerogar.ocs.party.PartyContainer;
import de.nerogar.ocs.party.PartyUserResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseParty extends DatabaseAdapter {

	public DatabaseParty(Database database) {
		super(database);
	}

	public void loadParties(PartyContainer partyContainer) {
		PreparedStatement ps = null;

		Map<Integer, Party> parties = new HashMap<>();
		Map<Integer, List<PartyUserResult>> results = new HashMap<>();

		try {
			ps = database.getConnection().prepareStatement("SELECT * FROM " + prefix + "party");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				int ownerID = result.getInt("ownerID");
				String scrambleType = result.getString("cubeType");
				String ranking = result.getString("ranking");
				int rounds = result.getInt("rounds");
				long startTime = result.getLong("startTime");
				int mode = result.getInt("mode");

				Party party = new Party(id, name, ownerID, scrambleType, ranking, rounds, startTime, mode);
				parties.put(id, party);
				partyContainer.loadParty(party);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}

		try {
			ps = database.getConnection().prepareStatement("SELECT * FROM " + prefix + "partyRound");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				int partyID = result.getInt("partyID");
				int round = result.getInt("round");
				String scramble = result.getString("scramble");

				parties.get(partyID).scrambles[round] = scramble;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}

		try {
			ps = database.getConnection().prepareStatement("SELECT * FROM " + prefix + "partytime");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				int partyID = result.getInt("partyID");
				int userID = result.getInt("userID");
				int round = result.getInt("round");
				int time = result.getInt("time");

				boolean inserted = false;
				List<PartyUserResult> r = results.computeIfAbsent(partyID, k -> new ArrayList<>());
				for (PartyUserResult partyUserResult : r) {
					if (partyUserResult.userID == userID) {
						partyUserResult.setTime(parties.get(partyID).rounds - 1, round, time);
						inserted = true;
						break;
					}
				}
				if (!inserted) {
					r.add(new PartyUserResult(userID, parties.get(partyID).rounds, parties.get(partyID).ranking));
					r.get(r.size() - 1).setTime(parties.get(partyID).rounds - 1, round, time);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}

		for (Map.Entry<Integer,List<PartyUserResult>> resultEntry : results.entrySet()) {
			List<PartyUserResult> partyUserResults = resultEntry.getValue();
			PartyUserResult[] result = new PartyUserResult[partyUserResults.size()];

			for (int i = 0; i < partyUserResults.size(); i++) {
				result[i] = partyUserResults.get(i);
			}

			parties.get(resultEntry.getKey()).loadData(result);
		}

		for (Map.Entry<Integer, Party> partyEntry : parties.entrySet()) {
			if(partyEntry.getValue().results==null){
				partyEntry.getValue().loadData(new PartyUserResult[0]);
			}
		}

	}

	public void saveParty(Party party) {
		if (party.saved) return;
		party.saved = true;

		PreparedStatement psParty = null;
		PreparedStatement psScrambles = null;
		PreparedStatement psTimes = null;

		try {
			//party
			psParty = database.getConnection().prepareStatement("INSERT INTO " + prefix + "party(id, name, ownerID, cubeType, ranking, rounds, startTime, mode) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");

			psParty.setInt(1, party.id);
			psParty.setString(2, party.name);
			psParty.setInt(3, party.ownerID);
			psParty.setString(4, party.scrambleType);
			psParty.setString(5, party.ranking);
			psParty.setInt(6, party.rounds);
			psParty.setLong(7, party.startTime);
			psParty.setInt(8, party.mode);

			psParty.execute();

			//scrambles
			psScrambles = database.getConnection().prepareStatement("INSERT INTO " + prefix + "partyRound(partyID, round, scramble) VALUES(?, ?, ?)");

			int count = 0;
			int batchSize = 1000;

			for (int i = 0; i < party.scrambles.length; i++) {

				psScrambles.setInt(1, party.id);
				psScrambles.setInt(2, i);
				psScrambles.setString(3, party.scrambles[i]);
				psScrambles.addBatch();

				count++;

				if (count % batchSize == 0) {
					psScrambles.executeBatch();
				}
			}

			psScrambles.executeBatch();

			//times
			psTimes = database.getConnection().prepareStatement("INSERT INTO " + prefix + "partytime(partyID, userID, round, time) VALUES(?, ?, ?, ?)");

			count = 0;
			batchSize = 1000;

			for (PartyUserResult pur : party.results) {
				for (int i = 0; i < pur.times.length; i++) {

					psTimes.setInt(1, party.id);
					psTimes.setInt(2, pur.userID);
					psTimes.setInt(3, i);
					psTimes.setInt(4, pur.times[i]);
					psTimes.addBatch();

					count++;

					if (count % batchSize == 0) {
						psTimes.executeBatch();
					}
				}
			}

			psTimes.executeBatch();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(psParty);
			closeOrFail(psScrambles);
			closeOrFail(psTimes);
		}
	}

	public int getNextPartyID() {
		PreparedStatement ps;

		try {
			ps = database.getConnection().prepareStatement("SELECT Auto_increment FROM information_schema.tables WHERE table_name='" + prefix + "party'");
			ResultSet result = ps.executeQuery();

			result.next();
			return result.getInt("Auto_increment");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

}
