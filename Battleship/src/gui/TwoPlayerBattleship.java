package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import backend.Board;
import backend.Pair;
import backend.ShipClass;
import exceptions.AlreadyHitException;
import exceptions.AlreadyMissException;
import exceptions.SunkException;
import exceptions.WinException;

public class TwoPlayerBattleship extends JFrame {
	private static final long serialVersionUID = 2420989566824276145L;
	// Backend
	private JFrame menu;
	private String playerOneName;
	private String playerTwoName;

	private int rows;
	private int cols;
	private Color[] colorHits;

	private Board playerOneBoard;
	private Board playerTwoBoard;

	private String[] colLetters;
	private Integer[] rowNumbers;

	// Player One Display
	private JFrame playerOneFrame;
	private JPanel[][] playerOnePanels;
	private JPanel playerOneTop;
	private JPanel playerOneBottom;
	private JComboBox<Integer> playerOneRow;
	private JComboBox<String> playerOneCol;
	private JButton playerOneGuess;

	// Player Two Display
	private JFrame playerTwoFrame;
	private JPanel[][] playerTwoPanels;
	private JPanel playerTwoTop;
	private JPanel playerTwoBottom;
	private JComboBox<Integer> playerTwoRow;
	private JComboBox<String> playerTwoCol;
	private JButton playerTwoGuess;

	// Menu Bar
	private JMenuBar menuBar;
	private JMenu navigation;
	private JMenuItem goToMenu;

	// Set-Up Panel
	private JTextField playerOneNameField;
	private JTextField playerTwoNameField;
	private JButton start;

	// ship choosing
	private Chooser chooserOne;
	private Chooser chooserTwo;

	public TwoPlayerBattleship(JFrame menu, int rows, int cols, ShipClass[] ships, Color[] colorHits) {
		this.menu = menu;
		this.rows = rows;
		this.cols = cols;
		this.colorHits = colorHits;

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		navigation = new JMenu("Navigation");
		menuBar.add(navigation);
		goToMenu = new JMenuItem("Go to Menu");
		navigation.add(goToMenu);
		goToMenu.addActionListener(new ReturnToMenu());

		this.setLayout(new GridLayout(3, 2));

		this.add(new JLabel("Player One Name: "));
		playerOneNameField = new JTextField(10);
		playerOneNameField.setText("Player One");
		this.add(playerOneNameField);

		this.add(new JLabel("Player Two Name: "));
		playerTwoNameField = new JTextField(10);
		playerTwoNameField.setText("Player Two");
		this.add(playerTwoNameField);

		this.add(new JLabel());
		start = new JButton("Start");
		start.setFocusPainted(false);
		this.add(start);
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playerOneName = playerOneNameField.getText();
				playerTwoName = playerTwoNameField.getText();
				chooserOne = new Chooser(menu, rows, cols, ships, playerOneNameField.getText());
				chooserTwo = new Chooser(menu, rows, cols, ships, playerTwoNameField.getText());
				chooserOne.setLocation(0, 0);
				chooserTwo.setLocation(chooserOne.getWidth(), 0);
				dispose();
			}
		});

		this.pack();
		this.validate();
		this.repaint();

		try {
			BufferedImage img = ImageIO.read(OnePlayerBattleship.class.getResource("/battleship.png"));
			this.setIconImage(img);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {

			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				menu.setVisible(true);
				dispose();
				if (chooserOne != null) {
					chooserOne.dispose();
				}
				if (chooserTwo != null) {
					chooserTwo.dispose();
				}
				if (playerOneFrame != null) {
					playerOneFrame.dispose();
				}
				if (playerTwoFrame != null) {
					playerTwoFrame.dispose();
				}
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {

			}

			@Override
			public void windowOpened(WindowEvent arg0) {

			}

		});

		this.setVisible(true);
		this.setResizable(false);
		this.setTitle("Two Player Battleship Set-Up");
	}

	private void buildFrames() {
		colLetters = new String[cols];
		for (int i = 0; i < colLetters.length; i++) {
			colLetters[i] = "" + ((char) (65 + i));
		}
		rowNumbers = new Integer[rows];
		for (int i = 0; i < rowNumbers.length; i++) {
			rowNumbers[i] = (i + 1);
		}

		Border line = BorderFactory.createLineBorder(Color.black);

		playerOneFrame = new JFrame();
		playerOneFrame.setLayout(new BorderLayout());

		playerOneTop = new JPanel();
		playerOneTop.setLayout(new GridLayout(rows + 1, cols + 1));
		playerOneTop.add(new JPanel());
		for (int c = 0; c < colLetters.length; c++) {
			JLabel l = new JLabel("" + colLetters[c]);
			l.setHorizontalAlignment(JLabel.CENTER);
			playerOneTop.add(l);
		}

		playerOnePanels = new JPanel[rows][cols];
		for (int r = 0; r < rowNumbers.length; r++) {
			JLabel l = new JLabel("" + rowNumbers[r]);
			l.setHorizontalAlignment(JLabel.CENTER);
			playerOneTop.add(l);
			for (int c = 0; c < colLetters.length; c++) {
				JPanel panel = new JPanel();
				panel.setPreferredSize(new Dimension(30, 30));
				panel.setBackground(Color.gray);
				panel.setBorder(line);
				playerOnePanels[r][c] = panel;
				playerOneTop.add(panel);
			}
		}

		playerOneFrame.add(playerOneTop, BorderLayout.CENTER);

		playerOneBottom = new JPanel();
		playerOneBottom.setLayout(new GridLayout(1, 0, 1, 0));

		JLabel colLabel = new JLabel("Column: ");
		colLabel.setHorizontalAlignment(JLabel.CENTER);
		playerOneBottom.add(colLabel);

		this.playerOneCol = new JComboBox<>(colLetters);
		playerOneBottom.add(playerOneCol);

		JLabel rowLabel = new JLabel("Row: ");
		rowLabel.setHorizontalAlignment(JLabel.CENTER);
		playerOneBottom.add(rowLabel);

		this.playerOneRow = new JComboBox<>(rowNumbers);
		playerOneBottom.add(playerOneRow);

		playerOneGuess = new JButton("Guess");
		playerOneGuess.setFocusPainted(false);
		playerOneBottom.add(playerOneGuess);

		playerOneFrame.add(playerOneBottom, BorderLayout.SOUTH);

		playerTwoFrame = new JFrame();
		playerTwoFrame.setLayout(new BorderLayout());

		playerTwoTop = new JPanel();
		playerTwoTop.setLayout(new GridLayout(rows + 1, cols + 1));
		playerTwoTop.add(new JPanel());
		for (int c = 0; c < colLetters.length; c++) {
			JLabel l = new JLabel("" + colLetters[c]);
			l.setHorizontalAlignment(JLabel.CENTER);
			playerTwoTop.add(l);
		}

		playerTwoPanels = new JPanel[rows][cols];
		for (int r = 0; r < rowNumbers.length; r++) {
			JLabel l = new JLabel("" + rowNumbers[r]);
			l.setHorizontalAlignment(JLabel.CENTER);
			playerTwoTop.add(l);
			for (int c = 0; c < colLetters.length; c++) {
				JPanel panel = new JPanel();
				panel.setPreferredSize(new Dimension(30, 30));
				panel.setBackground(Color.gray);
				panel.setBorder(line);
				playerTwoPanels[r][c] = panel;
				playerTwoTop.add(panel);
			}
		}

		playerTwoFrame.add(playerTwoTop, BorderLayout.CENTER);

		playerTwoBottom = new JPanel();
		playerTwoBottom.setLayout(new GridLayout(1, 0, 1, 0));

		JLabel colLabelTwo = new JLabel("Column: ");
		colLabelTwo.setHorizontalAlignment(JLabel.CENTER);
		playerTwoBottom.add(colLabelTwo);

		this.playerTwoCol = new JComboBox<>(colLetters);
		playerTwoBottom.add(playerTwoCol);

		JLabel rowLabelTwo = new JLabel("Row: ");
		rowLabelTwo.setHorizontalAlignment(JLabel.CENTER);
		playerTwoBottom.add(rowLabelTwo);

		this.playerTwoRow = new JComboBox<>(rowNumbers);
		playerTwoBottom.add(playerTwoRow);

		playerTwoGuess = new JButton("Guess");
		playerTwoGuess.setFocusPainted(false);
		playerTwoBottom.add(playerTwoGuess);

		playerTwoFrame.add(playerTwoBottom, BorderLayout.SOUTH);

		playerOneGuess.addActionListener(new GuessPlayerOne());
		playerTwoGuess.addActionListener(new GuessPlayerTwo());

		playerOneFrame.pack();
		playerOneFrame.validate();
		playerOneFrame.setVisible(true);
		playerOneFrame.setLocation(0, 0);
		playerOneFrame.setTitle(playerOneName + "'s Guesses");

		playerTwoFrame.pack();
		playerTwoFrame.validate();
		playerTwoFrame.setVisible(true);
		playerTwoFrame.setTitle(playerTwoName + "'s Guesses");
		playerTwoFrame.setLocation(playerOneFrame.getWidth(), 0);

		playerOneFrame.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {

			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				menu.setVisible(true);
				dispose();
				if (chooserOne != null) {
					chooserOne.dispose();
				}
				if (chooserTwo != null) {
					chooserTwo.dispose();
				}
				if (playerOneFrame != null) {
					playerOneFrame.dispose();
				}
				if (playerTwoFrame != null) {
					playerTwoFrame.dispose();
				}
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {

			}

			@Override
			public void windowOpened(WindowEvent arg0) {

			}

		});

		playerTwoFrame.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {

			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				menu.setVisible(true);
				dispose();
				if (chooserOne != null) {
					chooserOne.dispose();
				}
				if (chooserTwo != null) {
					chooserTwo.dispose();
				}
				if (playerOneFrame != null) {
					playerOneFrame.dispose();
				}
				if (playerTwoFrame != null) {
					playerTwoFrame.dispose();
				}
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {

			}

			@Override
			public void windowOpened(WindowEvent arg0) {

			}

		});

		if (ThreadLocalRandom.current().nextInt(0, 2) == 0) {
			JOptionPane.showMessageDialog(playerOneFrame, playerOneName + " goes first.");
			enablePlayerOne();
			disablePlayerTwo();
		} else {
			JOptionPane.showMessageDialog(playerTwoFrame, playerTwoName + " goes first.");
			enablePlayerTwo();
			disablePlayerOne();
		}
	}

	private void enablePlayerOne() {
		playerOneGuess.setEnabled(true);
		if (this.playerOneTop.getMouseListeners().length == 0) {
			playerOneTop.addMouseListener(new PlayerOneMouse());
		}
	}

	private void enablePlayerTwo() {
		playerTwoGuess.setEnabled(true);
		if (this.playerTwoTop.getMouseListeners().length == 0) {
			playerTwoTop.addMouseListener(new PlayerTwoMouse());
		}
	}

	private void disablePlayerOne() {
		playerOneGuess.setEnabled(false);
		if (playerOneTop.getMouseListeners().length > 0) {
			playerOneTop.removeMouseListener(playerOneTop.getMouseListeners()[0]);
		}
	}

	private void disablePlayerTwo() {
		playerTwoGuess.setEnabled(false);
		if (playerTwoTop.getMouseListeners().length > 0) {
			playerTwoTop.removeMouseListener(playerTwoTop.getMouseListeners()[0]);
		}
	}

	private class GuessPlayerOne implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int row = playerOneRow.getSelectedIndex(), col = playerOneCol.getSelectedIndex();
			try {
				if (playerTwoBoard.hit(row, col)) {
					playerOnePanels[row][col].setBackground(colorHits[0]);
				} else {
					playerOnePanels[row][col].setBackground(colorHits[1]);
				}
				enablePlayerTwo();
				disablePlayerOne();
			} catch (SunkException exc) {
				playerOnePanels[row][col].setBackground(colorHits[0]);
				JOptionPane.showMessageDialog(playerOneTop,
						"You have sunk the " + exc.getShip() + ", which has " + exc.getSpaces() + " spaces.");
				enablePlayerTwo();
				disablePlayerOne();
			} catch (AlreadyHitException exc) {
				JOptionPane.showMessageDialog(playerOneTop, "You've already hit that space.");
			} catch (AlreadyMissException exc) {
				JOptionPane.showMessageDialog(playerOneTop, "You've already missed at that space.");
			} catch (WinException exc) {
				playerOnePanels[row][col].setBackground(colorHits[0]);
				JOptionPane.showMessageDialog(playerOneTop, "You won in " + 17 + " guesses!");
				disablePlayerOne();
				disablePlayerTwo();
			}
		}
	}

	private class GuessPlayerTwo implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int row = playerTwoRow.getSelectedIndex(), col = playerTwoCol.getSelectedIndex();
			try {
				if (playerOneBoard.hit(row, col)) {
					playerTwoPanels[row][col].setBackground(colorHits[0]);
				} else {
					playerTwoPanels[row][col].setBackground(colorHits[1]);
				}
				enablePlayerOne();
				disablePlayerTwo();
			} catch (SunkException exc) {
				playerTwoPanels[row][col].setBackground(colorHits[0]);
				JOptionPane.showMessageDialog(playerTwoTop,
						"You have sunk the " + exc.getShip() + ", which has " + exc.getSpaces() + " spaces.");
				enablePlayerOne();
				disablePlayerTwo();
			} catch (AlreadyHitException exc) {
				JOptionPane.showMessageDialog(playerTwoTop, "You've already hit that space.");
			} catch (AlreadyMissException exc) {
				JOptionPane.showMessageDialog(playerTwoTop, "You've already missed at that space.");
			} catch (WinException exc) {
				playerTwoPanels[row][col].setBackground(colorHits[0]);
				JOptionPane.showMessageDialog(playerTwoTop, "You won in " + 17 + " guesses!");
				disablePlayerOne();
				disablePlayerTwo();
			}
		}
	}

	private class PlayerOneMouse implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			int x = e.getX(), y = e.getY();
			int dividerY = playerOnePanels[0][0].getHeight(), dividerX = playerOnePanels[0][0].getWidth();
			if (x > dividerX && y > dividerY) {
				int row = (y / dividerY) - 1, col = (x / dividerX) - 1;
				try {
					if (playerTwoBoard.hit(row, col)) {
						playerOnePanels[row][col].setBackground(colorHits[0]);
					} else {
						playerOnePanels[row][col].setBackground(colorHits[1]);
					}
					playerOneRow.setSelectedIndex(row);
					playerOneCol.setSelectedIndex(col);
					enablePlayerTwo();
					disablePlayerOne();
				} catch (SunkException exc) {
					playerOnePanels[row][col].setBackground(colorHits[0]);
					playerOneRow.setSelectedIndex(row);
					playerOneCol.setSelectedIndex(col);
					JOptionPane.showMessageDialog(playerOneTop,
							"You have sunk the " + exc.getShip() + ", which has " + exc.getSpaces() + " spaces.");
					enablePlayerTwo();
					disablePlayerOne();
				} catch (AlreadyHitException exc) {
					playerOneRow.setSelectedIndex(row);
					playerOneCol.setSelectedIndex(col);
					JOptionPane.showMessageDialog(playerOneTop, "You've already hit that space.");
				} catch (AlreadyMissException exc) {
					playerOneRow.setSelectedIndex(row);
					playerOneCol.setSelectedIndex(col);
					JOptionPane.showMessageDialog(playerOneTop, "You've already missed at that space.");
				} catch (WinException exc) {
					playerOnePanels[row][col].setBackground(colorHits[0]);
					playerOneRow.setSelectedIndex(row);
					playerOneCol.setSelectedIndex(col);
					JOptionPane.showMessageDialog(playerOneTop, "You won in " + 17 + " guesses!");
					disablePlayerOne();
					disablePlayerTwo();
				}
			}

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}

		@Override
		public void mouseExited(MouseEvent arg0) {

		}

		@Override
		public void mousePressed(MouseEvent arg0) {

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {

		}

	}

	private class PlayerTwoMouse implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			int x = e.getX(), y = e.getY();
			int dividerY = playerTwoPanels[0][0].getHeight(), dividerX = playerTwoPanels[0][0].getWidth();
			if (x > dividerX && y > dividerY) {
				int row = (y / dividerY) - 1, col = (x / dividerX) - 1;
				try {
					if (playerOneBoard.hit(row, col)) {
						playerTwoPanels[row][col].setBackground(colorHits[0]);
					} else {
						playerTwoPanels[row][col].setBackground(colorHits[1]);
					}
					playerTwoRow.setSelectedIndex(row);
					playerTwoCol.setSelectedIndex(col);
					enablePlayerOne();
					disablePlayerTwo();
				} catch (SunkException exc) {
					playerTwoPanels[row][col].setBackground(colorHits[0]);
					playerTwoRow.setSelectedIndex(row);
					playerTwoCol.setSelectedIndex(col);
					JOptionPane.showMessageDialog(playerTwoTop,
							"You have sunk the " + exc.getShip() + ", which has " + exc.getSpaces() + " spaces.");
					enablePlayerOne();
					disablePlayerTwo();
				} catch (AlreadyHitException exc) {
					playerTwoRow.setSelectedIndex(row);
					playerTwoCol.setSelectedIndex(col);
					JOptionPane.showMessageDialog(playerTwoTop, "You've already hit that space.");
				} catch (AlreadyMissException exc) {
					playerTwoRow.setSelectedIndex(row);
					playerTwoCol.setSelectedIndex(col);
					JOptionPane.showMessageDialog(playerTwoTop, "You've already missed at that space.");
				} catch (WinException exc) {
					playerTwoRow.setSelectedIndex(row);
					playerTwoCol.setSelectedIndex(col);
					playerTwoPanels[row][col].setBackground(colorHits[0]);
					JOptionPane.showMessageDialog(playerTwoTop, "You won in " + 17 + " guesses!");
					disablePlayerOne();
					disablePlayerTwo();
				}
			}

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}

		@Override
		public void mouseExited(MouseEvent arg0) {

		}

		@Override
		public void mousePressed(MouseEvent arg0) {

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {

		}
	}

	private class ReturnToMenu implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			menu.setVisible(true);
			dispose();
			if (chooserOne != null) {
				chooserOne.dispose();
			}
			if (chooserTwo != null) {
				chooserTwo.dispose();
			}
			if (playerOneFrame != null) {
				playerOneFrame.dispose();
			}
			if (playerTwoFrame != null) {
				playerTwoFrame.dispose();
			}
		}
	}

	private class Chooser extends JFrame {
		private static final long serialVersionUID = -1383316892405308615L;
		
		private JFrame menu;
		private String name;

		private JMenuBar menuBar;
		private JMenu actions;
		private JMenu goToMenu;
		private JMenu information;
		private JMenuItem play;
		private JMenuItem reset;
		private JMenuItem menuItem;
		private JMenuItem help;

		// selection panel
		private JPanel shipsDisplay;
		private JPanel choices;
		private JPanel labelsPanel;

		private String[] colLetters;
		private Integer[] rowNumbers;
		private final String[] ORIENT = { "Horizontal", "Vertical" };

		private int r, c;
		private ShipClass[] ships;
		private ArrayList<JComboBox<Integer>> rows;
		private ArrayList<JComboBox<String>> cols;
		private ArrayList<JComboBox<String>> orients;

		private ArrayList<JLabel> labels;
		private ArrayList<ColorButton> choosers;
		private ArrayList<Color> colors;

		// display panel
		private JPanel display;
		private JPanel[][] grid;

		Chooser(JFrame menu, int rows, int cols, ShipClass[] ships, String name) {
			this.r = rows;
			this.c = cols;
			this.ships = ships;
			this.rowNumbers = new Integer[r];
			this.name = name;
			for (int i = 0; i < rowNumbers.length; i++) {
				rowNumbers[i] = i + 1;
			}
			this.colLetters = new String[c];
			for (int i = 0; i < colLetters.length; i++) {
				colLetters[i] = "" + ((char) (65 + i));
			}

			this.setLayout(new BorderLayout());

			this.menu = menu;

			this.menuBar = new JMenuBar();
			this.setJMenuBar(menuBar);

			this.actions = new JMenu("Actions");
			menuBar.add(actions);
			this.play = new JMenuItem("Save Configuration");
			actions.add(play);
			this.reset = new JMenuItem("Reset");
			actions.add(reset);

			this.goToMenu = new JMenu("Menu");
			menuItem = new JMenuItem("Go to Menu");
			goToMenu.add(menuItem);
			menuItem.addActionListener(new Menu());
			menuBar.add(goToMenu);

			this.information = new JMenu("Information");
			this.help = new JMenuItem("Help");
			information.add(help);
			help.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String s = "";
					s += "Help\n=================================================================================================\n";
					s += "Use the combo boxes below to select the starting positions for the ships and their orientations.";
					s += "\nIn order to distinguish each individual ship you can change its color using the buttons below.";
					s += "\nA space will be marked red if there is a conflict or if the position and orientation you have selected will run the ship off the board";
					s += "\nPress Actions>Save Configuration in order to use your configuration against your opponent or press Actions>Reset to reset the board.";
					s += "\nPress Menu>Go to Menu in order to return to the menu.";
					JOptionPane.showMessageDialog(menuBar, s);
				}
			});
			menuBar.add(information);

			play.addActionListener(new PlayListener());
			reset.addActionListener(new Reset());

			buildDisplay();

			buildShipsDisplay();
			buildLabels();
			buildChoices();

			repaintGrid();

			reset();

			try {
				BufferedImage img = ImageIO.read(OnePlayerBattleship.class.getResource("/battleship.png"));
				this.setIconImage(img);
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.addWindowListener(new WindowListener() {

				@Override
				public void windowActivated(WindowEvent e) {

				}

				@Override
				public void windowClosed(WindowEvent e) {

				}

				@Override
				public void windowClosing(WindowEvent e) {
					menu.setVisible(true);
					dispose();
					if (chooserOne != null) {
						chooserOne.dispose();
					}
					if (chooserTwo != null) {
						chooserTwo.dispose();
					}
					if (playerOneFrame != null) {
						playerOneFrame.dispose();
					}
					if (playerTwoFrame != null) {
						playerTwoFrame.dispose();
					}
				}

				@Override
				public void windowDeactivated(WindowEvent e) {

				}

				@Override
				public void windowDeiconified(WindowEvent e) {

				}

				@Override
				public void windowIconified(WindowEvent e) {

				}

				@Override
				public void windowOpened(WindowEvent e) {

				}

			});

			JPanel lower = new JPanel();
			lower.setLayout(new BorderLayout());
			lower.add(shipsDisplay, BorderLayout.CENTER);
			lower.add(labelsPanel, BorderLayout.WEST);
			lower.add(choices, BorderLayout.EAST);

			this.add(lower, BorderLayout.CENTER);
			this.add(display, BorderLayout.NORTH);
			this.setTitle(name + "'s Ship Placement");

			this.validate();
			this.pack();
			this.repaint();
			this.setResizable(false);
			this.setVisible(true);
		}

		private void reset() {
			boolean hor = (c >= r);
			int rowS = 0, colS = 0;
			for (int s = 0; s < ships.length; s++) {
				rows.get(s).setSelectedIndex(rowS);
				cols.get(s).setSelectedIndex(colS);
				orients.get(s).setSelectedIndex((hor == true) ? 0 : 1);
				if (hor) {
					rowS++;
					if (rowS == r) {
						rowS = r - 1;
					}
				} else {
					colS++;
					if (colS == c) {
						colS = c - 1;
					}
				}
			}
		}

		private void buildShipsDisplay() {
			shipsDisplay = new JPanel();
			int maxSpaces = -1;
			for (int i = 0; i < ships.length; i++) {
				if (ships[i].getSpaces() > maxSpaces) {
					maxSpaces = ships[i].getSpaces();
				}
			}
			shipsDisplay.setLayout(new GridLayout(ships.length, maxSpaces));
			Border line = BorderFactory.createLineBorder(Color.black);

			for (int ship = 0; ship < ships.length; ship++) {
				int spaces = ships[ship].getSpaces();
				for (int s = 0; s < spaces; s++) {
					JPanel panel = new JPanel();
					panel.setPreferredSize(new Dimension(25, 25));
					panel.setBackground(Color.darkGray);
					panel.setBorder(line);
					shipsDisplay.add(panel);
				}
				for (int s = 0; s < maxSpaces - spaces; s++) {
					JPanel panel = new JPanel();
					panel.setPreferredSize(new Dimension(25, 25));
					panel.setBackground(Color.gray);
					panel.setBorder(line);
					shipsDisplay.add(panel);
				}
			}

		}

		private void buildLabels() {
			labelsPanel = new JPanel();
			choosers = new ArrayList<>();
			labelsPanel.setLayout(new GridLayout(ships.length, 1));

			labels = new ArrayList<>();
			choosers = new ArrayList<>();
			colors = new ArrayList<>();

			for (int k = 0; k < ships.length; k++) {
				Color rand = new Color(ThreadLocalRandom.current().nextInt(0, 256),
						ThreadLocalRandom.current().nextInt(0, 256), ThreadLocalRandom.current().nextInt(0, 256));

				JPanel curr = new JPanel();
				JLabel l = new JLabel(ships[k].getFullName() + ": ");
				l.setForeground(rand);
				labels.add(l);

				ColorButton b = new ColorButton("Select Color", k);
				b.setBackground(rand);
				b.setForeground(Color.WHITE);
				b.setFocusPainted(false);
				choosers.add(b);

				colors.add(rand);

				curr.add(labels.get(k));
				curr.add(choosers.get(k));
				labelsPanel.add(curr);
			}
		}

		private void buildChoices() {
			choices = new JPanel();
			choices.setLayout(new GridLayout(ships.length, 1));
			rows = new ArrayList<JComboBox<Integer>>();
			cols = new ArrayList<JComboBox<String>>();
			orients = new ArrayList<JComboBox<String>>();

			for (int s = 0; s < ships.length; s++) {
				JPanel curr = new JPanel();
				curr.setLayout(new GridLayout(1, 6));

				JLabel col = new JLabel("Column: ");
				col.setHorizontalAlignment(JLabel.CENTER);
				curr.add(col);
				cols.add(new JComboBox<String>(colLetters));
				curr.add(cols.get(s));
				JLabel row = new JLabel("Row: ");
				row.setHorizontalAlignment(JLabel.CENTER);
				curr.add(row);
				rows.add(new JComboBox<Integer>(rowNumbers));
				curr.add(rows.get(s));
				orients.add(new JComboBox<String>(ORIENT));
				curr.add(orients.get(s));

				rows.get(s).addItemListener(new ComboListener());
				cols.get(s).addItemListener(new ComboListener());
				orients.get(s).addItemListener(new ComboListener());
				choices.add(curr);
			}
		}

		private void buildDisplay() {
			display = new JPanel();
			display.setLayout(new GridLayout(r + 1, c + 1));

			Border line = BorderFactory.createLineBorder(Color.black);

			display.add(new JPanel());
			for (int i = 0; i < colLetters.length; i++) {
				JLabel label = new JLabel(colLetters[i]);
				label.setHorizontalAlignment(JLabel.CENTER);
				display.add(label);
			}

			grid = new JPanel[r][c];
			for (int row = 0; row < r; row++) {
				JLabel label = new JLabel("" + rowNumbers[row]);
				label.setHorizontalAlignment(JLabel.CENTER);
				display.add(label);
				for (int col = 0; col < c; col++) {
					JPanel newPanel = new JPanel();
					newPanel.setPreferredSize(new Dimension(30, 30));
					newPanel.setBackground(Color.gray);
					newPanel.setBorder(line);
					grid[row][col] = newPanel;
					display.add(newPanel);
				}
			}
		}

		private void repaintGrid() {
			boolean canPlay = true;
			for (int row = 0; row < grid.length; row++) {
				for (int col = 0; col < grid[row].length; col++) {
					grid[row][col].setBackground(Color.gray);
				}
			}
			Pair[] starts = buildStartPairs();
			boolean[] orientations = buildOrientations();
			Color col;

			for (int s = 0; s < ships.length; s++) {
				if ((starts[s].getRow() > r - ships[s].getSpaces() && !orientations[s])
						|| (starts[s].getCol() > c - ships[s].getSpaces() && orientations[s])) {
					col = Color.red;
				} else {
					col = colors.get(s);
				}
				if (!paintSpaces(starts[s], ships[s].getSpaces(), col, orientations[s])) {
					canPlay = false;
				}
			}

			actions.setEnabled(canPlay);
		}

		private boolean paintSpaces(Pair start, int spaces, Color c, boolean orientation) {
			Pair p = start;
			boolean canPlay = true;
			for (int i = 0; i < spaces; i++) {
				if (p.getRow() < this.r && p.getCol() < this.c) {
					if (grid[p.getRow()][p.getCol()].getBackground().equals(Color.gray)) {
						grid[p.getRow()][p.getCol()].setBackground(c);
					} else {
						canPlay = false;
						grid[p.getRow()][p.getCol()].setBackground(Color.red);
					}
					if (orientation) {
						p = new Pair(p.getRow(), p.getCol() + 1);
					} else {
						p = new Pair(p.getRow() + 1, p.getCol());
					}
				} else {
					canPlay = false;
					break;
				}
			}
			return canPlay;
		}

		private Pair[] buildStartPairs() {
			Pair[] p = new Pair[ships.length];
			for (int s = 0; s < ships.length; s++) {
				p[s] = new Pair(rows.get(s).getSelectedIndex(), cols.get(s).getSelectedIndex());
			}

			return p;
		}

		private boolean[] buildOrientations() {
			boolean[] b = new boolean[ships.length];
			for (int s = 0; s < ships.length; s++) {
				b[s] = (orients.get(s).getSelectedIndex() == 0);
			}

			return b;
		}

		private class ComboListener implements ItemListener {
			@Override
			public void itemStateChanged(ItemEvent e) {
				repaintGrid();
			}
		}

		private class PlayListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(play)) {
					if (name.equals(playerOneName)) {
						dispose();
						playerOneBoard = new Board(r, c, ships, buildStartPairs(), buildOrientations());
						if (playerTwoBoard != null) {
							buildFrames();
						}
					} else if (name.equals(playerTwoName)) {
						dispose();
						playerTwoBoard = new Board(r, c, ships, buildStartPairs(), buildOrientations());
						if (playerOneBoard != null) {
							buildFrames();
						}
					}
				}

			}
		}

		private class Reset implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(reset)) {
					reset();
				}
			}
		}

		private class Menu implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				menu.setVisible(true);
				dispose();
				if (chooserOne != null) {
					chooserOne.dispose();
				}
				if (chooserTwo != null) {
					chooserTwo.dispose();
				}
				if (playerOneFrame != null) {
					playerOneFrame.dispose();
				}
				if (playerTwoFrame != null) {
					playerTwoFrame.dispose();
				}
			}
		}

		private class ColorButton extends JButton {
			private static final long serialVersionUID = -1170113828784313311L;
			private int index;

			ColorButton(String name, int index) {
				super(name);
				this.index = index;
				this.addActionListener(new ColorChoose());
			}

			int getIndex() {
				return this.index;
			}

			private class ColorChoose implements ActionListener {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					ColorButton cb = (ColorButton) arg0.getSource();
					int i = cb.getIndex();
					Color c = JColorChooser.showDialog(labelsPanel, "Choose Color", colors.get(i));
					labels.get(i).setForeground(c);
					choosers.get(i).setBackground(c);
					colors.set(i, c);
					repaintGrid();
				}

			}
		}
	}
}
