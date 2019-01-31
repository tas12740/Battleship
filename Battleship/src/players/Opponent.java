package players;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import backend.*;

public class Opponent {
	private boolean huntMode;

	private Board board;
	private Board opp;

	public Opponent(Board opp) {
		board = new Board(opp.getRows(), opp.getCols(), opp.getShipClasses());
		huntMode = true;

		this.opp = opp;
	}

	public Board getOpp() {
		return opp;
	}

	public Board getBoard() {
		return board;
	}

	public boolean getHuntMode() {
		return this.huntMode;
	}

	public void hunt() {
		int[] ships = opp.getShips();
		this.huntMode = true;
		for (int i = 0; i < ships.length; i++) {
			if (ships[i] > 0 && ships[i] < opp.getShipClasses()[i].getSpaces()) {
				huntMode = false;
			}
		}
	}

	public Pair nextGuess() {
		ArrayList<Pair> possibles = max(buildProbabilityDensity());
		Pair guess = possibles.get(ThreadLocalRandom.current().nextInt(0, possibles.size()));
		return guess;
	}

	public int[][] buildProbabilityDensity() {
		int[][] d = new int[opp.getRows()][opp.getCols()];
		if (huntMode) {
			int[] ships = opp.getShips();
			ShipClass[] shipClasses = opp.getShipClasses();
			ShipClass ship;
			for (int row = 0; row < opp.getRows(); row++) {
				for (int col = 0; col < opp.getCols(); col++) {
					if (!opp.space(row, col).equals("M") && !opp.space(row, col).equals("H")
							&& !opp.space(row, col).equals("X")) {
						for (int s = 0; s < shipClasses.length; s++) {
							ship = shipClasses[s];
							if (ships[s] > 0) {
								for (int colStart = col - ship.getSpaces() + 1; colStart <= col; colStart++) {
									boolean canPlace = true;
									if (colStart >= 0 && colStart <= opp.getCols() - ship.getSpaces()) {
										for (int c = colStart; c < colStart + ship.getSpaces(); c++) {
											if (opp.space(row, c).equals("M") || opp.space(row, c).equals("X")) {
												canPlace = false;
											}
										}
										if (canPlace) {
											d[row][col]++;
										}
									}
								}
								for (int rowStart = row - ship.getSpaces() + 1; rowStart <= row; rowStart++) {
									boolean canPlace = true;
									if (rowStart >= 0 && rowStart <= opp.getRows() - ship.getSpaces()) {
										for (int r = rowStart; r < rowStart + ship.getSpaces(); r++) {
											if (opp.space(r, col).equals("M") || opp.space(r, col).equals("X")) {
												canPlace = false;
											}
										}
										if (canPlace) {
											d[row][col]++;
										}
									}
								}
							}
						}
					} else {
						d[row][col] = 0;
					}
				}
			}
		} else {
			// target mode
			int[] ships = opp.getShips();
			ShipClass[] shipClasses = opp.getShipClasses();
			ShipClass ship;
			for (int row = 0; row < opp.getRows(); row++) {
				for (int col = 0; col < opp.getCols(); col++) {
					if (!opp.space(row, col).equals("M") && !opp.space(row, col).equals("H")
							&& !opp.space(row, col).equals("X")) {
						for (int s = 0; s < shipClasses.length; s++) {
							ship = shipClasses[s];
							if (ships[s] > 0) {
								for (int colStart = col - ship.getSpaces() + 1; colStart <= col; colStart++) {
									boolean canPlace = true;
									boolean heavy = false;
									if (colStart >= 0 && colStart <= opp.getCols() - ship.getSpaces()) {
										for (int c = colStart; c < colStart + ship.getSpaces(); c++) {
											if (opp.space(row, c).equals("M") || opp.space(row, c).equals("X")) {
												canPlace = false;
											}
											if (opp.space(row, c).equals("H")) {
												heavy = true;
											}
										}
										if (canPlace) {
											if (heavy) {
												d[row][col] += 10;
											} else {
												d[row][col]++;
											}
										}
									}
								}
								for (int rowStart = row - ship.getSpaces() + 1; rowStart <= row; rowStart++) {
									boolean canPlace = true;
									boolean heavy = false;
									if (rowStart >= 0 && rowStart <= opp.getRows() - ship.getSpaces()) {
										for (int r = rowStart; r < rowStart + ship.getSpaces(); r++) {
											if (opp.space(r, col).equals("M") || opp.space(r, col).equals("X")) {
												canPlace = false;
											}
											if (opp.space(r, col).equals("H")) {
												heavy = true;
											}
										}
										if (canPlace) {
											if (heavy) {
												d[row][col] += 10;
											} else {
												d[row][col]++;
											}
										}
									}
								}
							}
						}
					} else {
						d[row][col] = 0;
					}
				}
			}
		}
		return d;
	}

	private ArrayList<Pair> max(int[][] d) {
		int max = -1;
		for (int row = 0; row < d.length; row++) {
			for (int col = 0; col < d[row].length; col++) {
				if (d[row][col] > max) {
					max = d[row][col];
				}
			}
		}
		ArrayList<Pair> p = new ArrayList<>();
		for (int row = 0; row < d.length; row++) {
			for (int col = 0; col < d[row].length; col++) {
				if (d[row][col] == max) {
					p.add(new Pair(row, col));
				}
			}
		}
		return p;
	}
}
