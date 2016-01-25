package de.nerogar.ocs.sql;

import java.sql.*;

import de.nerogar.ocs.user.Profile;

public class DatabaseProfile extends DatabaseAdapter {

	public DatabaseProfile(Database database) {
		super(database);
	}

	public boolean loadUserStatistics(Profile profile) {
		PreparedStatement ps = null;

		try {

			ps = database.getConnection().prepareStatement("SELECT wcaID, forumID  FROM " + prefix + "user WHERE id=? LIMIT 1;");

			ps.setInt(1, profile.getUser().getID());
			ResultSet result = ps.executeQuery();

			if (!result.next()) return false;

			profile.wcaID = result.getString("wcaID");
			profile.forumID = result.getInt("forumID");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}

		return true;
	}

	public void loadPartyResultsOfUser(Profile profile) {
		PreparedStatement ps = null;

		try {

			/*
			 * SELECT MIN(pt.time) as best, SUM(pt.time) as time, COUNT(pt.time) as count, p.cubeType as type, p.mode as mode
			 * FROM " + prefix + "party p, " + prefix + "partytime pt, " + prefix + "user u
			 * WHERE pt.userID = u.id AND u.name = ? AND p.id = pt.partyID AND pt.time > 0
			 * GROUP BY p.cubeType, p.mode;
			 */

			ps = database.getConnection().prepareStatement("SELECT MIN(pt.time) as best, SUM(pt.time) as time, COUNT(pt.time) as count, p.cubeType as type, p.mode as mode FROM " + prefix + "party p, " + prefix + "partytime pt, " + prefix + "user u WHERE pt.userID = u.id AND u.name = ? AND p.id = pt.partyID AND pt.time > 0 GROUP BY p.cubeType, p.mode;");

			ps.setString(1, profile.getUser().getUsername());
			ResultSet result = ps.executeQuery();

			while (result.next()) {
				String type = result.getString("type");
				long time = result.getLong("time");
				long count = result.getLong("count");
				long mode = result.getLong("mode");
				long best = result.getLong("best");

				profile.addCubeTimes(type, time, count, best, mode);
			}

			return;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}

		return;
	}

}
