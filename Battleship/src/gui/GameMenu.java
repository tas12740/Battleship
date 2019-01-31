package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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

import backend.ArraysEqual;
import backend.ShipClass;

public class GameMenu extends JFrame {
	private static final long serialVersionUID = -1125209530618809345L;
	private JMenuBar menuBar;
	private JMenu options;
	private JMenuItem settings;
	private JMenu information;
	private JMenuItem help;

	private JButton playSolo;
	private JButton playComp;
	private JButton playTwo;

	private static GameMenu menu;
	private PlayerChooseShips chooserMenu;
	private OnePlayerBattleship solo;

	private ShipClass[] customShips;
	private int rows, cols;

	private Color[] colors;

	public static void main(String[] args) {
		createMenu();
	}

	public static GameMenu createMenu() {
		if (menu == null) {
			menu = new GameMenu();
			return menu;
		} else {
			return menu;
		}
	}

	private GameMenu() {
		rows = 10;
		cols = 10;
		colors = new Color[2];
		colors[0] = Color.red;
		colors[1] = Color.blue;
		this.customShips = new ShipClass[] { new ShipClass("Destroyer", "D", 2), new ShipClass("Cruiser", "C", 3),
				new ShipClass("Submarine", "S", 3), new ShipClass("Battleship", "B", 4),
				new ShipClass("Aircraft Carrier", "A", 5) };

		this.setLayout(new BorderLayout());

		this.menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		options = new JMenu("Options");
		menuBar.add(options);

		settings = new JMenuItem("Settings");
		options.add(settings);
		settings.addActionListener(new Settings());

		information = new JMenu("Information");
		help = new JMenuItem("Help");
		information.add(help);
		help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = "";
				s += "Help\n=====================================";
				s += "\nPress Options>Settings to set up custom ships or a custom board size.";
				s += "\nPress the buttons to play the associated version of Battleship.";
				JOptionPane.showMessageDialog(menuBar, s);
			}
		});
		menuBar.add(information);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));

		playSolo = new JButton("Play Solo");
		playSolo.addActionListener(new PlayListener());
		playSolo.setFocusPainted(false);
		panel.add(playSolo);

		playComp = new JButton("Play Against Computer");
		playComp.addActionListener(new PlayListener());
		playComp.setFocusPainted(false);
		panel.add(playComp);

		playTwo = new JButton("Versus Play");
		playTwo.addActionListener(new PlayListener());
		playTwo.setFocusPainted(false);
		panel.add(playTwo);

		try {
			BufferedImage img = ImageIO.read(OnePlayerBattleship.class.getResource("/battleshipTitle.png"));
			this.add(new JLabel(new ImageIcon(img)), BorderLayout.CENTER);
			this.setIconImage(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.add(panel, BorderLayout.SOUTH);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int) screenSize.getWidth() / 3, (int) screenSize.getHeight() / 3);

		this.setResizable(false);
		this.setTitle("Battleship");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.validate();
		this.pack();
		this.setVisible(true);
	}

	private class PlayListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(playSolo)) {
				setVisible(false);
				if (solo == null) {
					OnePlayerBattleship f = new OnePlayerBattleship(rows, cols, customShips, createMenu(), colors);
					solo = f;
					f.setLocation(getX(), getY());
				} else {
					if (rows != solo.getRows() || cols != solo.getCols()
							|| !ArraysEqual.shipArraysEqual(customShips, solo.getShips())
							|| !ArraysEqual.colorArraysEqual(colors, solo.getColors())) {
						solo.dispose();
						OnePlayerBattleship f = new OnePlayerBattleship(rows, cols, customShips, createMenu(), colors);
						solo = f;
						f.setLocation(getX(), getY());
					} else {
						solo.setVisible(true);
					}
				}
			} else if (e.getSource().equals(playComp)) {
				setVisible(false);
				if (chooserMenu == null) {
					PlayerChooseShips f = new PlayerChooseShips(createMenu(), rows, cols, customShips, colors);
					chooserMenu = f;
					f.setLocation(getX(), getY());
				} else {
					if (rows != chooserMenu.getRows() || cols != chooserMenu.getCols()
							|| !ArraysEqual.shipArraysEqual(customShips, chooserMenu.getShips())
							|| !ArraysEqual.colorArraysEqual(colors, chooserMenu.getColors())) {
						chooserMenu.dispose();
						PlayerChooseShips f = new PlayerChooseShips(createMenu(), rows, cols, customShips, colors);
						chooserMenu = f;
						f.setLocation(getX(), getY());
					} else {
						chooserMenu.setVisible(true);
					}
				}
			} else if (e.getSource().equals(playTwo)) {
				setVisible(false);
				JFrame f = new TwoPlayerBattleship(createMenu(), rows, cols, customShips, colors);
				f.setLocation(getX(), getY());
			}
		}
	}

	private class Settings implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			SettingsPage s = new SettingsPage();
			s.setVisible(true);
			options.setEnabled(false);
			playSolo.setEnabled(false);
			playComp.setEnabled(false);
		}
	}

	private class SettingsPage extends JFrame {
		private static final long serialVersionUID = -9165762178883471864L;
		private JComboBox<Integer> numRows;
		private JComboBox<Integer> numCols;
		private ArrayList<ShipPanel> panels;
		private JPanel ships;
		private JButton save;
		private JButton add;

		private JButton hitColor;
		private JButton missColor;

		private final Integer[] SPACECHOICES = { 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };

		SettingsPage() {
			this.panels = new ArrayList<>();
			save = new JButton("Save");
			save.setFocusPainted(false);
			add = new JButton("Add");
			add.setFocusPainted(false);

			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout());

			panel.add(new JLabel("Number of rows: "));

			numRows = new JComboBox<>(SPACECHOICES);
			panel.add(numRows);
			numRows.addItemListener(new ComboListener());

			panel.add(new JLabel("Number of columns: "));

			numCols = new JComboBox<>(SPACECHOICES);
			panel.add(numCols);
			numCols.addItemListener(new ComboListener());

			numRows.setSelectedIndex(5);
			numCols.setSelectedIndex(5);

			ships = new JPanel();
			ships.setLayout(new GridLayout(0, 1));
			panels.add(new ShipPanel(new ShipClass("Destroyer", "D", 2), 10, 0));
			panels.add(new ShipPanel(new ShipClass("Cruiser", "C", 3), 10, 1));
			panels.add(new ShipPanel(new ShipClass("Submarine", "S", 3), 10, 2));
			panels.add(new ShipPanel(new ShipClass("Battleship", "B", 4), 10, 3));
			panels.add(new ShipPanel(new ShipClass("Aircraft Carrier", "A", 5), 10, 4));
			for (int i = 0; i < panels.size(); i++) {
				ships.add(panels.get(i));
			}

			JPanel bottomPanel = new JPanel();
			bottomPanel.setLayout(new GridLayout(2, 1));

			JPanel topBottomPanel = new JPanel();
			topBottomPanel.setLayout(new FlowLayout());
			topBottomPanel.add(save);
			topBottomPanel.add(add);
			add.addActionListener(new AddShipAction());
			save.addActionListener(new SaveAction());
			bottomPanel.add(topBottomPanel);

			JPanel bottomBottomPanel = new JPanel();
			bottomBottomPanel.setLayout(new FlowLayout());
			hitColor = new JButton("Color for Hit");
			hitColor.setBackground(colors[0]);
			hitColor.setFocusPainted(false);
			missColor = new JButton("Color for Miss");
			missColor.setBackground(colors[1]);
			missColor.setFocusPainted(false);
			bottomBottomPanel.add(hitColor);
			bottomBottomPanel.add(missColor);
			hitColor.addActionListener(new ColorButton());
			missColor.addActionListener(new ColorButton());

			bottomPanel.add(bottomBottomPanel);
			bottomPanel.add(topBottomPanel);

			this.addWindowListener(new WindowListener() {
				@Override
				public void windowClosed(WindowEvent e) {

				}

				@Override
				public void windowActivated(WindowEvent arg0) {

				}

				@Override
				public void windowClosing(WindowEvent arg0) {
					options.setEnabled(true);
					playSolo.setEnabled(true);
					playComp.setEnabled(true);
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
			this.add(panel, BorderLayout.NORTH);
			this.add(ships, BorderLayout.CENTER);
			this.add(bottomPanel, BorderLayout.SOUTH);
			this.pack();
			this.validate();
			this.repaint();
		}

		private void removeShip(int number) {
			if (!(panels.size() < 2)) {
				ShipPanel s = panels.get(number);
				panels.remove(number);
				ships.remove(s);
				for (int p = number; p < panels.size(); p++) {
					panels.get(p).decrementNumber();
				}
				validate();
				pack();
				repaint();
			}
		}

		private void addShip() {
			ShipPanel s = new ShipPanel(new ShipClass("Placeholder Name", "A", 2),
					Math.min((Integer) numRows.getSelectedItem(), (Integer) numCols.getSelectedItem()), panels.size());
			panels.add(s);
			ships.add(s);
			validate();
			pack();
			repaint();
		}

		private int numSpaces() {
			int n = 0;
			for (ShipPanel s : panels) {
				n += s.getNumSpaces();
			}
			return n;
		}

		private boolean lettersUnique() {
			ArrayList<String> letters = new ArrayList<>();
			for (int s = 0; s < panels.size(); s++) {
				String str = panels.get(s).getLetter();
				if (letters.contains(str)) {
					return false;
				}
				letters.add(str);
			}
			return true;
		}

		private boolean canPlace() {
			int boardSize = ((Integer) numRows.getSelectedItem()) * ((Integer) numCols.getSelectedItem());
			return (numSpaces() <= boardSize) && lettersUnique();
		}

		private class ShipPanel extends JPanel {
			private static final long serialVersionUID = -7512019668195296075L;
			private JTextField fullName;
			private final String[] letters = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
					"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
			private int number;
			private JComboBox<String> name;
			private JComboBox<Integer> numSpaces;
			private RemoveButton remove;

			ShipPanel(ShipClass ship, int maxSpaces, int number) {
				this.number = number;

				this.setLayout(new FlowLayout());

				this.add(new JLabel("Ship Name:"));
				fullName = new JTextField(15);
				fullName.setText(ship.getFullName());
				this.add(fullName);
				this.add(new JLabel("Ship Letter:"));
				name = new JComboBox<>(letters);
				name.setSelectedItem(ship.getName());
				name.addItemListener(new SpaceListener());
				this.add(name);
				this.add(new JLabel("Number of spaces:"));

				Integer[] spaces = new Integer[maxSpaces];
				for (int i = 0; i < spaces.length; i++) {
					spaces[i] = i + 1;
				}
				numSpaces = new JComboBox<>(spaces);
				numSpaces.addItemListener(new SpaceListener());
				numSpaces.setSelectedIndex(ship.getSpaces() - 1);
				this.add(numSpaces);

				remove = new RemoveButton("Remove", this);
				remove.setFocusPainted(false);
				remove.addActionListener(new RemoveShipAction());
				this.add(remove);
			}

			String getFullName() {
				return fullName.getText();
			}

			String getLetter() {
				return (String) name.getSelectedItem();
			}

			int getNumSpaces() {
				return (Integer) numSpaces.getSelectedItem();
			}

			void setMaxSpaces(int max) {
				Integer[] spaces = new Integer[max];
				Integer currSpaces = (Integer) numSpaces.getSelectedItem();
				if (currSpaces > max) {
					currSpaces = max;
				}
				for (int i = 0; i < spaces.length; i++) {
					spaces[i] = i + 1;
				}
				numSpaces.removeItemListener(numSpaces.getItemListeners()[0]);
				numSpaces.removeAllItems();
				for (Integer i : spaces) {
					numSpaces.addItem(i);
				}
				numSpaces.addItemListener(new SpaceListener());
				numSpaces.setSelectedIndex(currSpaces - 1);
			}

			int getNumber() {
				return this.number;
			}

			void decrementNumber() {
				this.number--;
			}
		}

		private class RemoveButton extends JButton {
			private static final long serialVersionUID = -2720384576217982564L;
			private ShipPanel panel;

			RemoveButton(String name, ShipPanel panel) {
				super(name);
				this.panel = panel;
			}

			ShipPanel getPanel() {
				return this.panel;
			}
		}

		private class RemoveShipAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShipPanel s = ((RemoveButton) e.getSource()).getPanel();
				removeShip(s.getNumber());
				add.setEnabled(canPlace());
				save.setEnabled(canPlace());
			}
		}

		private class AddShipAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				addShip();
				add.setEnabled(canPlace());
				save.setEnabled(canPlace());
			}
		}

		private class ComboListener implements ItemListener {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.DESELECTED) {
					if (arg0.getSource().equals(numRows) || arg0.getSource().equals(numCols)) {
						int newSize = Math.max((Integer) numRows.getSelectedItem(),
								(Integer) numCols.getSelectedItem());
						for (int i = 0; i < panels.size(); i++) {
							panels.get(i).setMaxSpaces(newSize);
						}
						add.setEnabled(canPlace());
						save.setEnabled(canPlace());
						validate();
						pack();
						repaint();
					}
				}
			}
		}

		private class SpaceListener implements ItemListener {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.DESELECTED) {
					add.setEnabled(canPlace());
					save.setEnabled(canPlace());
				}
			}
		}

		private class SaveAction implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (canPlace()) {
					customShips = new ShipClass[panels.size()];
					for (int i = 0; i < panels.size(); i++) {
						ShipPanel s = panels.get(i);
						customShips[i] = new ShipClass(s.getFullName().trim(), s.getLetter(), s.getNumSpaces());
					}
					rows = (Integer) numRows.getSelectedItem();
					cols = (Integer) numCols.getSelectedItem();
					dispose();
					options.setEnabled(true);
					playSolo.setEnabled(true);
					playComp.setEnabled(true);
				} else {
					JOptionPane.showMessageDialog(settings, "Too many spaces.");
				}
			}
		}

		private class ColorButton implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(hitColor)) {
					Color c = JColorChooser.showDialog(settings, "Choose Hit Color", hitColor.getBackground());
					hitColor.setBackground(c);
					colors[0] = c;
				} else if (e.getSource().equals(missColor)) {
					Color c = JColorChooser.showDialog(settings, "Choose Miss Color", hitColor.getBackground());
					missColor.setBackground(c);
					colors[1] = c;
				}
			}

		}
	}
}
