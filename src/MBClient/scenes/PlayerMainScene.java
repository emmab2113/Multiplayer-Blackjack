package MBClient.scenes;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import MBClient.Client;
import MBClient.GUI;
import MBClient.Scene;

public class PlayerMainScene implements Scene {
	private GUI gui;
	private Client client;
	
	private JPanel basePanel;
	
	// ====== CONSTRUCTOR ===== //
	
	public PlayerMainScene(GUI gui, Client client) {
		this.gui = gui;
		this.client = client;
		
		basePanel = new JPanel();
		
		// set base panel layout
		// GridLayout divides container into fixed grid of equal cells
		basePanel.setLayout(new GridLayout(1, 3)); // 1 row, 3 columns
		
		// create action buttons
		JButton balanceButton = new JButton("balance");
		JButton playButton = new JButton("play");
		JButton logoutButton = new JButton("logout");
		
		// create action listeners
		// perform action depending on button clicked
		balanceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if balance: move to BalanceScene
				gui.setScene(new BalanceScene(gui, client));
			}
		});
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if play: add player to table, move to PlayerTableScene
				if (client.joinTable()) {
					gui.setScene(new PlayerTableScene(gui, client));
				}
				else {
					JOptionPane.showMessageDialog(null, "table join failed.");
				}
			}
		});
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if logout: logout and move back to OpenScene
				if (client.requestLogOut()) {
					// if success, move back to OpenScene
					gui.setScene(new OpenScene(gui, client));
				}
				else {
					JOptionPane.showMessageDialog(null, "logout failed.");
				}
			}
		});
		
		// add buttons to panel
		basePanel.add(balanceButton);
		basePanel.add(playButton);
		basePanel.add(logoutButton);
	}
	
	// ===== PUBLIC METHODS ==== //
	
	public JPanel getPanel() {
		return basePanel;
	}

}