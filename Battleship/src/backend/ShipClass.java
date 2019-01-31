package backend;

public class ShipClass {
	private String fullName;
	private String name;
	private int spaces;

	public ShipClass(String fullName, String name, int spaces) {
		if (name.length() > 1 || name.length() < 1 || spaces < 1) {
			throw new IllegalArgumentException();
		}
		this.fullName = fullName;
		this.name = name;
		this.spaces = spaces;
	}

	public String getFullName() {
		return this.fullName;
	}

	public String getName() {
		return this.name;
	}

	public int getSpaces() {
		return this.spaces;
	}

	@Override
	public String toString() {
		return "A ship called " + this.fullName + ", signified by " + this.name + ", of " + this.spaces + " spaces.";
	}
}
