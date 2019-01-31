package gui;

import exceptions.*;
import backend.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class OnePlayerBattleship extends JFrame {
	private static final long serialVersionUID = 966142265845861312L;

	private JFrame menu;
	private JMenuBar menuBar;
	private JMenu navigation;
	private JMenuItem goToMenu;
	private JMenu information;
	private JMenuItem help;
	// Backend
	private Board board;
	private int guesses;
	private Color[] colors;

	// Top panel
	private JPanel[][] panels;
	private JPanel topPanel;

	// Bottom panel
	private String[] colLetters;
	private Integer[] rowNumbers;
	private JPanel bottomPanel;
	private JComboBox<String> columnChoice;
	private JComboBox<Integer> rowChoice;
	private JButton guessButton;
	private JButton resetButton;

	public OnePlayerBattleship(int r, int c, ShipClass[] ships, JFrame menu, Color[] colors) {
		this.menu = menu;
		this.colors = colors;
		this.menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		navigation = new JMenu("Navigation");
		menuBar.add(navigation);
		goToMenu = new JMenuItem("Go to Menu");
		navigation.add(goToMenu);
		goToMenu.addActionListener(new GoToMenu());

		information = new JMenu("Information");
		help = new JMenuItem("Help");
		information.add(help);
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = "";
				s += "Help\n=========================================";
				s += "\nUse the drop-down boxes below to select the space to guess. Then press the Guess button to guess the space.";
				s += "\nYou can also click a space to guess it.";
				s += "\nPress Navigation>Go to Menu to return to the menu.";
				JOptionPane.showMessageDialog(menuBar, s);
			}
		});
		menuBar.add(information);

		board = new Board(r, c, ships);
		guesses = 0;

		colLetters = new String[board.getCols()];
		for (int i = 0; i < colLetters.length; i++) {
			colLetters[i] = "" + ((char) (65 + i));
		}
		rowNumbers = new Integer[board.getRows()];
		for (int i = 0; i < rowNumbers.length; i++) {
			rowNumbers[i] = (i + 1);
		}

		setUpTopPanel();
		setUpBottomPanel();

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
				setVisible(false);
				menu.setVisible(true);
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

		this.setLayout(new BorderLayout());
		this.add(topPanel, BorderLayout.CENTER);
		topPanel.addMouseListener(new Mouse());
		this.add(bottomPanel, BorderLayout.SOUTH);
		this.setTitle("Battleship");
		this.setResizable(false);
		this.validate();
		this.pack();
		this.repaint();
		this.setVisible(true);
	}

	public int getRows() {
		return this.board.getRows();
	}

	public int getCols() {
		return this.board.getCols();
	}

	public ShipClass[] getShips() {
		return this.board.getShipClasses();
	}

	public Color[] getColors() {
		return this.colors;
	}

	private void setUpTopPanel() {
		topPanel = new JPanel();
		panels = new JPanel[board.getRows()][board.getCols()];

		Border lineBorder = BorderFactory.createLineBorder(Color.black);
		for (int row = 0; row < panels.length; row++) {
			for (int col = 0; col < panels[row].length; col++) {
				panels[row][col] = new JPanel();
				panels[row][col].setPreferredSize(new Dimension(30, 30));
				panels[row][col].setBackground(Color.gray);
				panels[row][col].setBorder(lineBorder);
			}
		}

		topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(board.getRows() + 1, board.getCols() + 1));
		topPanel.add(new JPanel());
		for (int i = 0; i < colLetters.length; i++) {
			JLabel col = new JLabel(colLetters[i]);
			col.setHorizontalAlignment(JLabel.CENTER);
			col.setVerticalAlignment(JLabel.CENTER);
			topPanel.add(col);
		}
		for (int row = 0; row < board.getRows(); row++) {
			JLabel r = new JLabel("" + rowNumbers[row]);
			r.setHorizontalAlignment(JLabel.CENTER);
			r.setVerticalAlignment(JLabel.CENTER);
			topPanel.add(r);
			for (int col = 0; col < board.getCols(); col++) {
				topPanel.add(panels[row][col]);
			}
		}

	}

	private void setUpBottomPanel() {
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(1, 0, 1, 0));

		JLabel colLabel = new JLabel("Column:");
		colLabel.setHorizontalAlignment(JLabel.CENTER);
		bottomPanel.add(colLabel);

		columnChoice = new JComboBox<>(colLetters);
		bottomPanel.add(columnChoice);

		JLabel rowLabel = new JLabel("Row:");
		rowLabel.setHorizontalAlignment(JLabel.CENTER);
		bottomPanel.add(rowLabel);

		rowChoice = new JComboBox<>(rowNumbers);
		bottomPanel.add(rowChoice);

		guessButton = new JButton("Guess");
		guessButton.setFocusPainted(false);
		guessButton.addActionListener(new GuessButton());
		bottomPanel.add(guessButton);

		resetButton = new JButton("Reset");
		resetButton.setFocusPainted(false);
		resetButton.addActionListener(new ResetButton());
		bottomPanel.add(resetButton);
	}

	private class GuessButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(guessButton)) {
				guesses++;
				int row = rowChoice.getSelectedIndex(), col = columnChoice.getSelectedIndex();
				try {
					if (board.hit(row, col)) {
						panels[row][col].setBackground(colors[0]);
					} else {
						panels[row][col].setBackground(colors[1]);
					}
				} catch (SunkException exc) {
					panels[row][col].setBackground(colors[0]);
					JOptionPane.showMessageDialog(topPanel,
							"You have sunk the " + exc.getShip() + ", which has " + exc.getSpaces() + " spaces.");
				} catch (AlreadyHitException exc) {
					JOptionPane.showMessageDialog(topPanel, "You've already hit that space.");
				} catch (AlreadyMissException exc) {
					JOptionPane.showMessageDialog(topPanel, "You've already missed at that space.");
				} catch (WinException exc) {
					panels[row][col].setBackground(colors[0]);
					JOptionPane.showMessageDialog(topPanel, "You won in " + guesses + " guesses!");
					guessButton.setEnabled(false);
				}
			}
		}
	}

	private class ResetButton implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(resetButton)) {
				board.resetBoard();
				for (int row = 0; row < panels.length; row++) {
					for (int col = 0; col < panels[row].length; col++) {
						panels[row][col].setBackground(Color.gray);
					}
				}
				if (topPanel.getMouseListeners().length > 0) {
					topPanel.addMouseListener(new Mouse());
				}
				guessButton.setEnabled(true);
				guesses = 0;
			}
		}
	}

	private class GoToMenu implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(goToMenu)) {
				setVisible(false);
				menu.setVisible(true);
			}
		}
	}

	private class Mouse implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getSource().equals(topPanel)) {
				int x = e.getX(), y = e.getY();
				int dividerY = panels[0][0].getHeight(), dividerX = panels[0][0].getWidth();
				if (x > dividerX && y > dividerY) {
					guesses++;

					int row = (y / dividerY) - 1, col = (x / dividerX) - 1;
					try {
						if (board.hit(row, col)) {
							panels[row][col].setBackground(colors[0]);
						} else {
							panels[row][col].setBackground(colors[1]);
						}
						rowChoice.setSelectedIndex(row);
						columnChoice.setSelectedIndex(col);
					} catch (SunkException exc) {
						panels[row][col].setBackground(colors[0]);
						rowChoice.setSelectedIndex(row);
						columnChoice.setSelectedIndex(col);
						JOptionPane.showMessageDialog(topPanel,
								"You have sunk the " + exc.getShip() + ", which has " + exc.getSpaces() + " spaces.");
					} catch (AlreadyHitException exc) {
						JOptionPane.showMessageDialog(topPanel, "You've already hit that space.");
					} catch (AlreadyMissException exc) {
						JOptionPane.showMessageDialog(topPanel, "You've already missed at that space.");
					} catch (WinException exc) {
						panels[row][col].setBackground(colors[0]);
						rowChoice.setSelectedIndex(row);
						columnChoice.setSelectedIndex(col);
						JOptionPane.showMessageDialog(topPanel, "You won in " + guesses + " guesses!");
						topPanel.removeMouseListener(topPanel.getMouseListeners()[0]);
						guessButton.setEnabled(false);
					}
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
}
