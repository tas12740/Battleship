package exceptions;

import backend.ShipClass;

public class SunkException extends RuntimeException {
	private static final long serialVersionUID = -1553056363511832401L;
	private String ship;
	private int spaces;

	public SunkException(String ship, int spaces, int numHits, int winCondition) throws WinException {
		super();
		this.ship = ship;
		this.spaces = spaces;
		if (numHits == winCondition) {
			throw new WinException();
		}
	}

	public SunkException(ShipClass ship, int numHits, int winCondition) throws WinException {
		this.ship = ship.getFullName();
		this.spaces = ship.getSpaces();
		if (numHits == winCondition) {
			throw new WinException();
		}
	}

	public String getShip() {
		return this.ship;
	}

	public int getSpaces() {
		return this.spaces;
	}
}
