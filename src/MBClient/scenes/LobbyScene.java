package MBClient.scenes;

import javax.swing.JPanel;

import MBClient.Client;
import MBClient.GUI;
import MBClient.Scene;

public class LobbyScene implements Scene {
	private GUI gui;
	private Client client;
	
	private JPanel basePanel;
	
	// ====== CONSTRUCTOR ===== //
	
	public LobbyScene(GUI gui, Client client) {
		this.gui = gui;
		this.client = client;
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