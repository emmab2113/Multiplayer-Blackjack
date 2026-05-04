package MBClient.scenes;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import MBClient.Client;
import MBClient.GUI;
import MBClient.Scene;

public class OpenScene implements Scene {
	private GUI gui;
	private Client client;
	
	private JPanel basePanel;
	
	// ====== CONSTRUCTOR ====== //
	
	public OpenScene(GUI gui, Client client) {
		this.gui = gui;
		this.client = client;
		
		// set base panel layout
		// GridLayout divides container into fixed grid of equal cells
		basePanel.setLayout(new GridLayout(1, 2)); // 1 row, 2 columns
		
		// create action buttons
		JButton loginButton = new JButton("login");
		JButton registerButton = new JButton("register");
		
		// create action listeners
		// perform action depending on button clicked
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if login: move to LoginScene
				gui.setScene(new LoginScene(gui, client));
			}
		});
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if register: move to RegisterScene
				gui.setScene(new RegisterScene(gui, client));
			}
		});
		
		// add buttons to panel
		basePanel.add(loginButton);
		basePanel.add(registerButton);
	}
	
	// ===== PUBLIC METHODS ==== //
	
	public void construct() {
		
	}
	public void destruct() {	
		
	}
	public JPanel getPanel() {
		return basePanel;
	}

}