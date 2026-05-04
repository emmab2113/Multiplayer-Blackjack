package MBClient.scenes;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import MBClient.Client;
import MBClient.GUI;
import MBClient.Scene;

public class LoginScene implements Scene {
	private GUI gui;
	private Client client;
	
	private JPanel basePanel;
	
	// ====== CONSTRUCTOR ===== //
	
	public LoginScene(GUI gui, Client client) {
		this.gui = gui;
		this.client = client;
		
		// set base panel layout
		// GridLayout divides container into fixed grid of equal cells
		basePanel.setLayout(new GridLayout(4, 1)); // 3 rows, 1 columns
		
		// drop down menu for credential selection
		String[] credentials = {"player", "dealer"};
		JComboBox<String> credDropdown = new JComboBox<>(credentials);
		
		// create username/password labels/text fields
		JLabel userLabel = new JLabel("username:");
		JTextField userField = new JTextField(10); // 10 = preferred field size
		JLabel passLabel = new JLabel("password:");
		JTextField passField = new JTextField(10); // 10 = preferred field size
		
		// create username row panel, set layout
		// FlowLayout lines components in line, one after another
		// FlowLayout.LEFT makes components left-aligned
		// 5 = horizontal space between components
		// 1 = vertical space above/below components
		JPanel userRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 1));
		
		// add rating label/text field to rating row
		userRow.add(userLabel);
		userRow.add(userField);
		
		// create password row panel, set layout
		// FlowLayout lines components in line, one after another
		// FlowLayout.LEFT makes components left-aligned
		// 5 = horizontal space between components
		// 1 = vertical space above/below components
		JPanel passRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 1));
		
		// add time label/text field to time row
		passRow.add(passLabel);
		passRow.add(passField);
		
		// create login button
		JButton loginButton = new JButton("login");
		
		// create action listener
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// get credential from drop down menu
				String cred = (String) credDropdown.getSelectedItem();
				
				// get user/pass from text fields
				String user = userField.getText();
				String pass = new String(passField.getText());
				
				// try login
				if (client.requestLogIn(user, pass, cred)) {
					// if success, move to main menu depending on credentials
					if (cred == "player") {
						gui.setScene(new PlayerMainScene(gui, client));
					}
					else if (cred == "dealer") {
						gui.setScene(new DealerMainScene(gui, client));
					}
				}
				else {
					// if fail, notify of error
					JOptionPane.showMessageDialog(null, "login failed. try again");
					
					// clear fields
					userField.setText("");
					passField.setText("");
					
					// wait for another button press
				}
			}
		});
		
		// add parts to panel
		basePanel.add(credDropdown);
		basePanel.add(userRow);
		basePanel.add(passRow);
		basePanel.add(loginButton);
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