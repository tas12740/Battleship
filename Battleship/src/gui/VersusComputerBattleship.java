package gui;

import backend.*;
import exceptions.*;
import players.*;

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

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class VersusComputerBattleship extends JFrame {
	private static final long serialVersionUID = -5511752143221574082L;

	private JFrame menu;
	private JMenuBar menuBar;
	private JMenu menuItem;
	private JMenuItem goToMenu;
	private JMenu information;
	private JMenuItem help;

	private Color[] colors;

	// Backend
	private Opponent computer;
	private String[] colLetters;
	private Integer[] rowNumbers;
	private Board yourBoard;
	private int guesses;

	// here the terminology refers to the board being guessed on
	// Human panel
	private JPanel hPanel;
	private JPanel[][] hGrid;
	private JComboBox<String> col;
	private JComboBox<Integer> row;
	private JButton guess;
	private JButton show;
	private JPanel gridPanel;

	// Computer panel
	private JPanel cPanel;
	private JPanel[][] cGrid;

	public VersusComputerBattleship(Board user, JFrame menu, Color[] colors) {
		super();

		this.menu = menu;
		this.menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		this.menuItem = new JMenu("Navigation");
		menuBar.add(menuItem);
		this.goToMenu = new JMenuItem("Go to Menu");
		this.menuItem.add(goToMenu);
		this.goToMenu.addActionListener(new GoToMenu());

		information = new JMenu("Information");
		help = new JMenuItem("Help");
		information.add(help);
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = "";
				s += "Help\n=========================================";
				s += "\nUse the drop-down boxes below to select the space to guess. Then press the Guess button to guess the space.";
				s += "\nYou can also click a space to guess it.";
				s += "\nUse the Show/Hide button to show/hide the locations of your ships.";
				s += "\nPress Navigation>Go to Menu to return to the menu.";

				JOptionPane.showMessageDialog(menuBar, s);
			}
		});
		menuBar.add(information);

		this.colors = colors;

		computer = new Opponent(user);
		this.yourBoard = user;

		colLetters = new String[user.getCols()];
		for (int i = 0; i < colLetters.length; i++) {
			colLetters[i] = "" + ((char) (65 + i));
		}
		rowNumbers = new Integer[user.getRows()];
		for (int i = 0; i < rowNumbers.length; i++) {
			rowNumbers[i] = (i + 1);
		}

		setUpHuman();
		setUpComputer();

		this.setLayout(new BorderLayout());
		this.add(hPanel, BorderLayout.CENTER);
		this.add(cPanel, BorderLayout.EAST);

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
				dispose();
				menu.setVisible(true);
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

		this.setResizable(false);
		this.setTitle("Battleship");
		this.validate();
		this.pack();
		this.repaint();
		this.setVisible(true);
	}

	private void setUpHuman() {
		hPanel = new JPanel();
		hPanel.setLayout(new BorderLayout());

		gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(yourBoard.getRows() + 1, yourBoard.getCols() + 1));

		gridPanel.add(new JPanel());
		for (int i = 0; i < colLetters.length; i++) {
			JLabel label = new JLabel(colLetters[i]);
			label.setHorizontalAlignment(JLabel.CENTER);
			gridPanel.add(label);
		}

		Border line = BorderFactory.createLineBorder(Color.black);

		hGrid = new JPanel[yourBoard.getRows()][yourBoard.getCols()];
		for (int row = 0; row < hGrid.length; row++) {
			JLabel label = new JLabel("" + rowNumbers[row]);
			label.setHorizontalAlignment(JLabel.CENTER);
			gridPanel.add(label);
			for (int col = 0; col < hGrid[row].length; col++) {
				JPanel panel = new JPanel();
				panel.setPreferredSize(new Dimension(30, 30));
				panel.setBackground(Color.gray);
				panel.setBorder(line);
				hGrid[row][col] = panel;
				gridPanel.add(panel);
			}
		}
		gridPanel.addMouseListener(new Mouse());

		hPanel.add(gridPanel, BorderLayout.CENTER);

		JPanel low = new JPanel();
		low.setLayout(new GridLayout(1, 6, 1, 0));

		low.add(new JLabel("Column: "));

		col = new JComboBox<>(colLetters);
		col.addItemListener(new ComboListener());
		low.add(col);

		low.add(new JLabel("Row: "));

		row = new JComboBox<>(rowNumbers);
		row.addItemListener(new ComboListener());
		low.add(row);

		guess = new JButton("Guess");
		guess.addActionListener(new GuessButton());
		guess.setFocusPainted(false);
		low.add(guess);

		show = new JButton("Show");
		show.addActionListener(new ShowButton());
		show.setFocusPainted(false);
		low.add(show);

		hPanel.add(low, BorderLayout.SOUTH);
	}

	private void setUpComputer() {
		cPanel = new JPanel();
		cPanel.setLayout(new BorderLayout());

		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(yourBoard.getRows() + 1, yourBoard.getCols() + 1));

		gridPanel.add(new JPanel());
		for (int i = 0; i < colLetters.length; i++) {
			JLabel label = new JLabel(colLetters[i]);
			label.setHorizontalAlignment(JLabel.CENTER);
			gridPanel.add(label);
		}

		Border line = BorderFactory.createLineBorder(Color.black);

		cGrid = new JPanel[yourBoard.getRows()][yourBoard.getCols()];
		for (int row = 0; row < cGrid.length; row++) {
			JLabel label = new JLabel("" + rowNumbers[row]);
			label.setHorizontalAlignment(JLabel.CENTER);
			gridPanel.add(label);
			for (int col = 0; col < cGrid[row].length; col++) {
				JPanel panel = new JPanel();
				panel.setPreferredSize(new Dimension(30, 30));
				panel.setBackground(Color.gray);
				panel.setBorder(line);
				cGrid[row][col] = panel;
				gridPanel.add(panel);
			}
		}

		cPanel.add(gridPanel, BorderLayout.CENTER);
	}

	private class GuessButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(guess)) {
				int r = row.getSelectedIndex(), c = col.getSelectedIndex();
				try {
					if (computer.getBoard().hit(r, c)) {
						hGrid[r][c].setBackground(colors[0]);
						guesses++;
					} else {
						hGrid[r][c].setBackground(colors[1]);
						guesses++;
					}
				} catch (SunkException exc) {
					hGrid[r][c].setBackground(colors[0]);
					JOptionPane.showMessageDialog(hPanel,
							"You have sunk the " + exc.getShip() + ", which has " + exc.getSpaces() + " spaces.");
				} catch (AlreadyHitException exc) {
					JOptionPane.showMessageDialog(hPanel, "You've already hit that space.");
					return;
				} catch (AlreadyMissException exc) {
					JOptionPane.showMessageDialog(hPanel, "You've already missed at that space.");
					return;
				} catch (WinException exc) {
					hGrid[r][c].setBackground(colors[0]);
					for (int row = 0; row < cGrid.length; row++) {
						for (int col = 0; col < cGrid[row].length; col++) {
							String s = yourBoard.space(row, col);
							if (!s.equals("M") && !s.equals("H") && !s.equals("X") && !s.equals("-")) {
								cGrid[row][col].setBackground(Color.green);
							}
						}
					}
					JOptionPane.showMessageDialog(hPanel, "You won in " + guesses + " guesses!");
					gridPanel.removeMouseListener(gridPanel.getMouseListeners()[0]);
					guess.setEnabled(false);
					return;
				}

				// now move onto the computer's turn
				Pair cp = computer.nextGuess();
				try {
					if (yourBoard.hit(cp.getRow(), cp.getCol())) {
						computer.hunt();
						cGrid[cp.getRow()][cp.getCol()].setBackground(colors[0]);
					} else {
						computer.hunt();
						cGrid[cp.getRow()][cp.getCol()].setBackground(colors[1]);
					}
				} catch (SunkException exc) {
					computer.hunt();
					cGrid[cp.getRow()][cp.getCol()].setBackground(colors[0]);
					JOptionPane.showMessageDialog(cPanel, "The computer has sunk your " + exc.getShip() + ", which has "
							+ exc.getSpaces() + " spaces.");
				} catch (AlreadyHitException exc) {
					JOptionPane.showMessageDialog(cPanel, "The computer has already hit that space.");
				} catch (AlreadyMissException exc) {
					JOptionPane.showMessageDialog(cPanel, "The computer has already missed at that space.");
				} catch (WinException exc) {
					cGrid[cp.getRow()][cp.getCol()].setBackground(colors[0]);
					for (int row = 0; row < hGrid.length; row++) {
						for (int col = 0; col < hGrid[row].length; col++) {
							String s = computer.getBoard().space(row, col);
							if (!s.equals("M") && !s.equals("H") && !s.equals("X") && !s.equals("-")) {
								hGrid[row][col].setBackground(Color.green);
							}
						}
					}
					JOptionPane.showMessageDialog(hPanel, "The computer won in " + guesses + " guesses!");
					gridPanel.removeMouseListener(gridPanel.getMouseListeners()[0]);
					guess.setEnabled(false);
				}
				guess.setEnabled(false);
			}
		}
	}

	private class ShowButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(show)) {
				if (show.getText().equals("Show")) {
					for (int row = 0; row < cGrid.length; row++) {
						for (int col = 0; col < cGrid[row].length; col++) {
							if (cGrid[row][col].getBackground().equals(Color.gray)) {
								if (!yourBoard.space(row, col).equals("-")) {
									cGrid[row][col].setBackground(Color.green);
								}
							}
						}
					}
					show.setText("Hide");
				} else {
					for (int row = 0; row < cGrid.length; row++) {
						for (int col = 0; col < cGrid[row].length; col++) {
							if (cGrid[row][col].getBackground().equals(Color.green)) {
								cGrid[row][col].setBackground(Color.gray);
							}
						}
					}
					show.setText("Show");
				}
			}

		}

	}

	private class ComboListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			int r = row.getSelectedIndex(), c = col.getSelectedIndex();
			if (computer.getBoard().space(r, c).equals("H") || computer.getBoard().space(r, c).equals("M")
					|| computer.getBoard().space(r, c).equals("X")) {
				guess.setEnabled(false);
			} else {
				guess.setEnabled(true);
			}

		}
	}

	private class GoToMenu implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(goToMenu)) {
				dispose();
				menu.setVisible(true);
			}
		}
	}

	private class Mouse implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			int x = e.getX(), y = e.getY();
			int dividerY = hGrid[0][0].getHeight(), dividerX = hGrid[0][0].getWidth();
			if (x > dividerX && y > dividerY) {
				int r = (y / dividerY) - 1, c = (x / dividerX) - 1;
				try {
					if (computer.getBoard().hit(r, c)) {
						hGrid[r][c].setBackground(colors[0]);
						guesses++;
					} else {
						hGrid[r][c].setBackground(colors[1]);
						guesses++;
					}
					col.setSelectedIndex(c);
					row.setSelectedIndex(r);
				} catch (SunkException exc) {
					hGrid[r][c].setBackground(colors[0]);
					JOptionPane.showMessageDialog(hPanel,
							"You have sunk the " + exc.getShip() + ", which has " + exc.getSpaces() + " spaces.");
					col.setSelectedIndex(c);
					row.setSelectedIndex(r);
				} catch (AlreadyHitException exc) {
					JOptionPane.showMessageDialog(hPanel, "You've already hit that space.");
					col.setSelectedIndex(c);
					row.setSelectedIndex(r);
					return;
				} catch (AlreadyMissException exc) {
					JOptionPane.showMessageDialog(hPanel, "You've already missed at that space.");
					col.setSelectedIndex(c);
					row.setSelectedIndex(r);
					return;
				} catch (WinException exc) {
					hGrid[r][c].setBackground(colors[0]);
					for (int row = 0; row < cGrid.length; row++) {
						for (int col = 0; col < cGrid[row].length; col++) {
							String s = yourBoard.space(row, col);
							if (!s.equals("M") && !s.equals("H") && !s.equals("X") && !s.equals("-")) {
								cGrid[row][col].setBackground(Color.green);
							}
						}
					}
					JOptionPane.showMessageDialog(hPanel, "You won in " + guesses + " guesses!");
					col.setSelectedIndex(c);
					row.setSelectedIndex(r);
					guess.setEnabled(false);
					gridPanel.removeMouseListener(gridPanel.getMouseListeners()[0]);
					return;
				}

				// now move onto the computer's turn
				Pair cp = computer.nextGuess();
				try {
					if (yourBoard.hit(cp.getRow(), cp.getCol())) {
						computer.hunt();
						cGrid[cp.getRow()][cp.getCol()].setBackground(colors[0]);
					} else {
						computer.hunt();
						cGrid[cp.getRow()][cp.getCol()].setBackground(colors[1]);
					}
				} catch (SunkException exc) {
					computer.hunt();
					cGrid[cp.getRow()][cp.getCol()].setBackground(colors[0]);
					JOptionPane.showMessageDialog(cPanel, "The computer has sunk your " + exc.getShip() + ", which has "
							+ exc.getSpaces() + " spaces.");
				} catch (AlreadyHitException exc) {
					JOptionPane.showMessageDialog(cPanel, "The computer has already hit that space.");
				} catch (AlreadyMissException exc) {
					JOptionPane.showMessageDialog(cPanel, "The computer has already missed at that space.");
				} catch (WinException exc) {
					cGrid[cp.getRow()][cp.getCol()].setBackground(colors[0]);
					for (int row = 0; row < hGrid.length; row++) {
						for (int col = 0; col < hGrid[row].length; col++) {
							String s = computer.getBoard().space(row, col);
							if (!s.equals("M") && !s.equals("H") && !s.equals("X") && !s.equals("-")) {
								hGrid[row][col].setBackground(Color.green);
							}
						}
					}
					JOptionPane.showMessageDialog(hPanel, "The computer won in " + guesses + " guesses!");
					gridPanel.removeMouseListener(gridPanel.getMouseListeners()[0]);
					guess.setEnabled(false);
				}
				guess.setEnabled(false);

			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

	}
}
