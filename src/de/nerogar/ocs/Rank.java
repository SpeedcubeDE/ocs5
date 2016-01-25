package de.nerogar.ocs;

import java.util.ArrayList;
import java.util.List;

import de.nerogar.ocs.sql.DatabaseRank;

public class Rank {

	public static DatabaseRank databaseRank;

	public static ArrayList<Rank> ranks;

	public String name;
	public int power;
	public boolean show;
	public String shortName;

	public Rank(String name, int power, boolean show, String shortName) {
		this.name = name;
		this.power = power;
		this.show = show;
		this.shortName = shortName;
	}

	public static Integer getPower(String name) {
		for (Rank rank : ranks) {
			if (rank.name.equalsIgnoreCase(name)) return rank.power;
		}

		return null;
	}

	public static String getRankString(int power, boolean show) {
		for (Rank rank : ranks) {
			if (rank.power == power && (rank.show || show)) return rank.shortName;
		}

		return "";
	}

	public static List<Rank> getRanks() {
		return ranks;
	}

	public static void init(){
		databaseRank = new DatabaseRank(OCSServer.databaseNew);
		ranks = new ArrayList<Rank>();
		databaseRank.loadRanks(ranks);
	}

}
