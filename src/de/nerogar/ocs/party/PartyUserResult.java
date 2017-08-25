package de.nerogar.ocs.party;

import java.util.ArrayList;

public class PartyUserResult implements Comparable<PartyUserResult> {

	public static final int DNF = -1;
	public static final int DNS = -2;

	public static final String AVG  = "avg";
	public static final String MEAN = "mean";
	public static final String BEST = "best";

	public int[]     times;
	public boolean[] timeSet;
	public int       userID;
	public boolean   ended;

	private String ranking;
	public  int    avg;
	public  int    mean;
	public  int    best;
	public  int    worst;

	public ArrayList<Average> averages;

	public PartyUserResult(int userID, int rounds, String ranking) {
		this.userID = userID;
		this.ranking = ranking;

		times = new int[rounds];
		timeSet = new boolean[rounds];
		averages = new ArrayList<Average>();
	}

	public boolean setTime(int currentRound, int index, int time) {
		if (index >= 0 && index < times.length) {
			times[index] = time;
			timeSet[index] = true;
			if (index == times.length - 1) ended = true;

			calcRankTimes(currentRound);
			return true;
		}

		return false;
	}

	public void calcRankTimes(int round) {
		best = Integer.MAX_VALUE;
		worst = 0;
		int sum = 0;
		int dnfCount = 0;

		for (int i = 0; i <= round; i++) {
			if (times[i] >= 0) {
				if (times[i] < best) best = times[i];
				if (times[i] > worst && worst >= 0) worst = times[i];
				sum += times[i];
			} else {
				dnfCount++;
				worst = DNF;
			}
		}

		if (dnfCount == 0) {
			mean = sum / (round + 1);
		} else {
			mean = DNF;
		}

		sum -= best;
		sum -= worst >= 0 ? worst : 0;

		if (dnfCount <= 1 && round >= 2) {
			avg = sum / (round - 1); //round 4 -> 5th round ((4+1)-2)
		} else {
			avg = DNF;
		}

		//averages
		averages.clear();

		addAveragesOf(round, 5);
		addAveragesOf(round, 12);

	}

	private void addAveragesOf(int round, int length) {
		if (round >= length) {
			int rollingAverage = getAverage(times, round - (length - 1), round);

			int bestAverage = Integer.MAX_VALUE;
			int baStartIndex = 0;

			for (int i = 0; i <= round - (length - 1); i++) {
				int tempTime = getAverage(times, i, i + (length - 1));
				if (tempTime < bestAverage && tempTime >= 0) {
					bestAverage = tempTime;
					baStartIndex = i;
				}
			}
			if (bestAverage == Integer.MAX_VALUE) bestAverage = DNF;

			averages.add(new Average("ra", rollingAverage, round - (length - 1), length));
			averages.add(new Average("ba", bestAverage, baStartIndex, length));

		}
	}

	private int getAverage(int[] times, int firstRound, int lastRound) {
		int avgBest = Integer.MAX_VALUE;
		int avgWorst = 0;
		int sum = 0;
		int dnfCount = 0;
		int avg;
		int rounds = lastRound - firstRound + 1;

		for (int i = firstRound; i <= lastRound; i++) {
			int time = times[i];
			if (time >= 0) {
				if (time < avgBest) avgBest = time;
				if (time > avgWorst && avgWorst >= 0) avgWorst = time;
				sum += time;
			} else {
				dnfCount++;
				avgWorst = DNF;
			}
		}

		sum -= avgBest;
		sum -= avgWorst >= 0 ? avgWorst : 0;

		if (dnfCount <= 1 && rounds > 2) {
			avg = sum / (rounds - 2);
		} else {
			avg = DNF;
		}

		return avg;
	}

	@Override
	public int compareTo(PartyUserResult pur) {
		switch (ranking) {
			case AVG:
				return compare(avg, pur.avg, userID, pur.userID);
			case MEAN:
				return compare(mean, pur.mean, userID, pur.userID);
			case BEST:
				return compare(best, pur.best, userID, pur.userID);
			default:
				return 0;
		}
	}

	private int compare(int val1, int val2, int userID1, int userID2) {
		if (val1 < 0 && val2 < 0) return userID1 - userID2;
		else if (val2 < 0) return -1;
		else if (val1 < 0) return 1;
		else return val1 - val2;
	}
}
