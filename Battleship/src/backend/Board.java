package backend;

import java.util.concurrent.ThreadLocalRandom;
import exceptions.*;

public class Board {
	// - for blank, H for hit, M for miss
	// ships - aircraft carrier (A), battleship (B), submarine (S), cruiser (C),
	// destroyer (D)
	// X for sunk
	private String[][] board;
	private int numHits;
	private int[] shipHits;
	private Pair[] shipStarts;
	private boolean[] orientation;
	private ShipClass[] ships;
	private int rows;
	private int cols;
	private int winCondition;

	public Board() {
		numHits = 0;
		shipHits = new int[5];
		shipStarts = new Pair[5];
		orientation = new boolean[5];
		ships = new ShipClass[] { new ShipClass("Destroyer", "D", 2), new ShipClass("Cruiser", "C", 3),
				new ShipClass("Submarine", "S", 3), new ShipClass("Battleship", "B", 4),
				new ShipClass("Aircraft Carrier", "A", 5) };
		for (int i = 0; i < ships.length; i++) {
			this.shipHits[i] = ships[i].getSpaces();
		}
		this.rows = 10;
		this.cols = 10;
		resetBoard();
		winCondition = winCondition();
	}

	public Board(Pair[] shipStarts, boolean[] orientation) {
		this(10, 10,
				new ShipClass[] { new ShipClass("Destroyer", "D", 2), new ShipClass("Cruiser", "C", 3),
						new ShipClass("Submarine", "S", 3), new ShipClass("Battleship", "B", 4),
						new ShipClass("Aircraft Carrier", "A", 5) },
				shipStarts, orientation);
		winCondition = winCondition();
	}

	public Board(int rows, int cols, ShipClass[] ships, Pair[] shipStarts, boolean[] orientation) {
		this.board = new String[rows][cols];
		numHits = 0;
		this.shipStarts = shipStarts;
		this.orientation = orientation;
		this.rows = rows;
		this.cols = cols;
		this.ships = ships;
		this.shipHits = new int[ships.length];
		for (int i = 0; i < ships.length; i++) {
			this.shipHits[i] = ships[i].getSpaces();
		}

		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				board[row][col] = "-";
			}
		}
		for (int i = 0; i < ships.length; i++) {
			Pair p = shipStarts[i];
			int spaces = ships[i].getSpaces();
			String s = ships[i].getName();
			for (int k = 0; k < spaces; k++) {
				if (!board[p.getRow()][p.getCol()].equals("-")) {
					throw new IllegalArgumentException();
				}
				board[p.getRow()][p.getCol()] = s;
				if (orientation[i]) {
					p = new Pair(p.getRow(), p.getCol() + 1);
				} else {
					p = new Pair(p.getRow() + 1, p.getCol());
				}
			}
		}
		winCondition = winCondition();
	}

	public Board(int rows, int cols, ShipClass[] ships) {
		this.board = new String[rows][cols];
		numHits = 0;
		this.shipStarts = new Pair[ships.length];
		this.orientation = new boolean[ships.length];
		this.rows = rows;
		this.cols = cols;
		this.ships = ships;
		this.shipHits = new int[ships.length];
		for (int i = 0; i < ships.length; i++) {
			this.shipHits[i] = ships[i].getSpaces();
		}

		resetBoard();
		winCondition = winCondition();
	}

	private int winCondition() {
		int w = 0;
		for (int s = 0; s < ships.length; s++) {
			w += ships[s].getSpaces();
		}
		return w;
	}

	public int[] getShips() {
		return this.shipHits;
	}

	public int getRows() {
		return this.rows;
	}

	public int getCols() {
		return this.cols;
	}

	public ShipClass[] getShipClasses() {
		return this.ships;
	}

	public boolean hit(int row, int col) throws WinException {
		String s = board[row][col];
		if (s.equals("-")) {
			board[row][col] = "M";
			return false;
		} else if (s.equals("M")) {
			throw new AlreadyMissException();
		} else if (s.equals("H") || s.equals("X")) {
			throw new AlreadyHitException();
		} else {
			int index = -1;
			for (int i = 0; i < ships.length; i++) {
				if (ships[i].getName().equals(s)) {
					index = i;
					break;
				}
			}
			board[row][col] = "H";
			numHits++;
			shipHits[index]--;
			if (shipHits[index] == 0) {
				Pair p = shipStarts[index];
				for (int i = 0; i < ships[index].getSpaces(); i++) {
					board[p.getRow()][p.getCol()] = "X";
					if (!orientation[index]) {
						p = new Pair(p.getRow() + 1, p.getCol());
					} else {
						p = new Pair(p.getRow(), p.getCol() + 1);
					}
				}
				throw new SunkException(ships[index].getFullName(), ships[index].getSpaces(), numHits, winCondition);
			}
			if (numHits == winCondition) {
				throw new WinException();
			}
			return true;
		}
	}

	public void fillBoard() {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				board[r][c] = "-";
			}
		}
	}

	public void resetBoard() {
		fillBoard();
		int t = 0, tot = 0;
		for (int s = 0; s < ships.length; s++) {
			ShipClass sc = ships[s];
			int spaces = sc.getSpaces();
			t = 0;
			while (true) {
				int rowStart, colStart;
				int orientation = ThreadLocalRandom.current().nextInt(0, 2);
				if (orientation == 1) {
					int colBound = cols - spaces + 1;
					if (colBound == 0) {
						colBound = 1;
					}
					rowStart = ThreadLocalRandom.current().nextInt(0, rows);
					colStart = ThreadLocalRandom.current().nextInt(0, colBound);
				} else {
					int rowBound = rows - spaces + 1;
					if (rowBound == 0) {
						rowBound = 1;
					}
					rowStart = ThreadLocalRandom.current().nextInt(0, rowBound);
					colStart = ThreadLocalRandom.current().nextInt(0, cols);
				}
				boolean restart = false;
				int row = rowStart, col = colStart;
				for (int space = 0; space < spaces; space++) {
					if (row >= rows || col >= cols) {
						restart = true;
						break;
					}
					if (!board[row][col].equals("-")) {
						restart = true;
						break;
					} else {
						if (orientation == 1) {
							col++;
						} else {
							row++;
						}
					}
				}
				if (restart) {
					t++;
					if (t == 100) {
						s = 0;
						tot++;
						if (tot == 100) {
							throw new IllegalArgumentException();
						}
						fillBoard();
						break;
					}
					continue;
				}

				shipStarts[s] = new Pair(rowStart, colStart);
				this.orientation[s] = (orientation == 1);

				row = rowStart;
				col = colStart;
				for (int space = 0; space < spaces; space++) {
					board[row][col] = sc.getName();
					if (orientation == 1) {
						col++;
					} else {
						row++;
					}
				}
				break;
			}
		}
		numHits = 0;
	}

	public String space(int row, int col) {
		return board[row][col];
	}

	public int getNumHits() {
		return this.numHits;
	}

	public void printBoard() {
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				System.out.print(board[row][col] + " ");
			}
			System.out.print("\n");
		}
	}
}
