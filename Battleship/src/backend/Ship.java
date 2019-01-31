package backend;

public enum Ship {
	DESTROYER("Destroyer", 2), CRUISER("Cruiser", 3), SUBMARINE("Submarine", 3), BATTLESHIP("Battleship",
			4), AIRCRAFTCARRIER("Aircraft Carrier", 5);

	private final String name;
	private final int spaces;

	private Ship(String name, int spaces) {
		this.name = name;
		this.spaces = spaces;
	}

	public String getName() {
		return this.name;
	}

	public int getSpaces() {
		return this.spaces;
	}
}
