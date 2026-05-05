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

public class DealerMainScene implements Scene {
	private GUI gui;
	private Client client;
	
	private JPanel basePanel;
	
	// ====== CONSTRUCTOR ===== //
	
	public DealerMainScene(GUI gui, Client client) {
		this.gui = gui;
		this.client = client;
		
		basePanel = new JPanel();
		
		// set base panel layout
		// GridLayout divides container into fixed grid of equal cells
		basePanel.setLayout(new GridLayout(1, 2)); // 1 row, 2 columns
		
		// create action buttons
		JButton hostButton = new JButton("host");
		JButton logoutButton = new JButton("logout");
		
		// create action listeners
		hostButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if host: add dealer to table, move to DealerTableScene
				if (client.joinTable()) {
					gui.setScene(new DealerTableScene(gui, client));
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
		
		// add components to base panel
		basePanel.add(hostButton);
		basePanel.add(logoutButton);
	}
	
	// ===== PUBLIC METHODS ==== //
	
	public JPanel getPanel() {
		return basePanel;
	}

}