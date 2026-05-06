package MBClient.scenes;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import MBClient.Client;
import MBClient.GUI;
import MBClient.Scene;

public class PlayerTableScene implements Scene {
	private GUI gui;
	private Client client;
	
	private JPanel basePanel;
	
	// ====== CONSTRUCTOR ===== //
	
	public PlayerTableScene(GUI gui, Client client) {
		this.gui = gui;
		this.client = client;
		
		basePanel = new JPanel();
		
		basePanel.setLayout(new GridLayout(1, 3));
		
		// left panel
		
		JPanel leftPanel = new JPanel();
		
		leftPanel.setLayout(new GridLayout(3, 1));
		
		JPanel otherPlayerPanel1 = new JPanel();
		JPanel otherPlayerPanel2 = new JPanel();
		JPanel buttonPanel = new JPanel();
		
		JButton hitButton = new JButton("hit");
		JButton standButton = new JButton("stand");
		JButton leaveButton = new JButton("leave");
		
		hitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		standButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		leaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
			}
		});
		
		leftPanel.add(otherPlayerPanel1);
		leftPanel.add(otherPlayerPanel2);
		leftPanel.add(buttonPanel);
		
		// center panel
		
		JPanel centerPanel = new JPanel();
		
		centerPanel.setLayout(new GridLayout(2, 1));
		
		JPanel dealerPanel = new JPanel();
		JPanel thisPlayerPanel = new JPanel();
		
		centerPanel.add(dealerPanel);
		centerPanel.add(thisPlayerPanel);
		
		// right panel
		
		JPanel rightPanel = new JPanel();
		
		rightPanel.setLayout(new GridLayout(3, 1));

		JPanel otherPlayerPanel3 = new JPanel();
		JPanel otherPlayerPanel4 = new JPanel();
		JPanel otherPlayerPanel5 = new JPanel();
		
		rightPanel.add(otherPlayerPanel3);
		rightPanel.add(otherPlayerPanel4);
		rightPanel.add(otherPlayerPanel5);
		
		basePanel.add(leftPanel);
		basePanel.add(centerPanel);
		basePanel.add(rightPanel);

	}
	
	// ===== PUBLIC METHODS ==== //
	
	public JPanel getPanel() {
		return basePanel;
	}

}