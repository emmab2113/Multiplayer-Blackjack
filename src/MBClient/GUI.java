package MBClient;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import MBClient.scenes.OpenScene;
import enums.MessageStatus;
import enums.MessageType;

public class GUI {
	private Client client;
	private Scene currentScene;
	
	private JFrame mainFrame;
	
	GUI(Client client) {
		this.client = client;
	}
	
	public void run() {
		// create main frame
		mainFrame = new JFrame("Multiplayer Blackjack");
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(500, 250); // width, height
		mainFrame.setResizable(false); // fix window size
		mainFrame.setLocationRelativeTo(null); // center window on screen
		
		// display OpenScene
		setScene(new OpenScene(this, client));
			
		mainFrame.setVisible(true);		
	}
	
	public void setScene(Scene newScene) {
		// check if there is a scene open already
		if (currentScene != null) {
			mainFrame.remove(currentScene.getPanel());
		}
		
		currentScene = newScene;
		
		mainFrame.add(currentScene.getPanel());
		
		mainFrame.revalidate();
		mainFrame.repaint();
	
	}
	
}