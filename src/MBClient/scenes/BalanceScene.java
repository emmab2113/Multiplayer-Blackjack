package MBClient.scenes;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import MBClient.Client;
import MBClient.GUI;
import MBClient.Scene;

public class BalanceScene implements Scene {
	private GUI gui;
	private Client client;
	
	private JPanel basePanel;
	
	// ====== CONSTRUCTOR ===== //
	
	public BalanceScene(GUI gui, Client client) {
		this.gui = gui;
		this.client = client;
		
		// set base panel layout
		// GridLayout divides container into fixed grid of equal cells
		basePanel.setLayout(new GridLayout(1, 2)); // 1 row, 2 columns
		
		// create panels
		JPanel leftPanel = new JPanel();
		JPanel rightPanel = new JPanel();
		
		// LEFT PANEL
		// balance display
		
		// create text field for balance
		JTextField balanceDisplay = new JTextField(5);
		
		// get current balance from server
		double balance = client.requestBalanceView();
		
		// add components to left panel
		leftPanel.add(balanceDisplay);
		
		// RIGHT PANEL
		// action buttons
		
		// set right panel layout
		// GridLayout divides container into fixed grid of equal cells
		basePanel.setLayout(new GridLayout(2, 1)); // 1 row, 2 columns
		
		// create action buttons
		JButton depositButton = new JButton("deposit");
		JButton withdrawButton = new JButton("withdraw");
		JButton backButton = new JButton("back");
		
		// create action listeners
		depositButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if deposit: 
				// display pop up to get deposit amount
				double deposit = Double.parseDouble(JOptionPane.showInputDialog("enter deposit amount: "));
				// validate
				if (deposit < 0) {
					deposit = Double.parseDouble(JOptionPane.showInputDialog("invalid input. enter deposit amount: "));
				}
				// try deposit
				double updatedBalance = client.requestBalanceDeposit(deposit);
				
				// update balance display
				balanceDisplay.setText(String.valueOf(updatedBalance));
			}
		});
		withdrawButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if withdraw: 
				// get withdraw amount
				double withdraw = Double.parseDouble(JOptionPane.showInputDialog("enter withdraw amount: "));
				// validate
				if (withdraw < 0 || withdraw > balance) {
					withdraw = Double.parseDouble(JOptionPane.showInputDialog("invalid input. enter deposit amount: "));
				}
				// try withdraw
				double updatedBalance = client.requestBalanceWithdrawal(withdraw);
				
				// update balance display
				balanceDisplay.setText(String.valueOf(updatedBalance));
			}
		});
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if back: move back to PlayMainScene
				gui.setScene(new PlayerMainScene(gui, client));
			}
		});
		
		// add components to right panel
		rightPanel.add(depositButton);
		rightPanel.add(withdrawButton);
		rightPanel.add(backButton);
		
		// add left and right panels to base panel
		basePanel.add(leftPanel);
		basePanel.add(rightPanel);
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