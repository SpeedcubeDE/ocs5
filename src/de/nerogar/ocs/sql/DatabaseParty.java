package de.nerogar.ocs.sql;

import java.sql.*;

import de.nerogar.ocs.party.Party;
import de.nerogar.ocs.party.PartyUserResult;

public class DatabaseParty extends DatabaseAdapter {

	public DatabaseParty(Database database) {
		super(database);
	}

	public void saveParty(Party party) {
		PreparedStatement psParty = null;
		PreparedStatement psScrambles = null;
		PreparedStatement psTimes = null;

		try {
			//party
			psParty = database.getConnection().prepareStatement("INSERT INTO " + prefix + "party(id, ownerID, cubeType, rounds, startTime, mode) VALUES(?, ?, ?, ?, ?, ?)");

			psParty.setInt(1, party.id);
			psParty.setInt(2, party.ownerID);
			psParty.setString(3, party.scrambleType);
			psParty.setInt(4, party.rounds);
			psParty.setLong(5, party.startTime);
			psParty.setInt(6, party.mode);

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
