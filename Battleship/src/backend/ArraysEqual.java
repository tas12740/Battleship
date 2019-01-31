package backend;

import java.awt.Color;

public class ArraysEqual {
	public static boolean shipArraysEqual(ShipClass[] one, ShipClass[] two) {
		if (one.length != two.length) {
			return false;
		}

		for (int i = 0; i < one.length; i++) {
			if (!one[i].getFullName().equals(two[i].getFullName()) || !one[i].getName().equals(two[i].getName())
					|| one[i].getSpaces() != two[i].getSpaces()) {
				return false;
			}
		}
		return true;
	}

	public static boolean colorArraysEqual(Color[] one, Color[] two) {
		if (one.length != two.length) {
			return false;
		}

		for (int i = 0; i < one.length; i++) {
			if (one[i].getRed() != two[i].getRed() || one[i].getBlue() != two[i].getBlue()
					|| one[i].getGreen() != two[i].getGreen()) {
				System.out.println(i);
				return false;
			}
		}
		return true;
	}
}
