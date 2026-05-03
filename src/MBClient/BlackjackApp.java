package MBClient;

public class BlackjackApp {
	public static void main(String[] args) {
		// create Client and GUI instances
		Client client = new Client();
		GUI gui = new GUI(client);
		
		// connect client to server
		client.connect();
        
		// verify stream connections
		if (client.verifyConnection() != true) {
			// failed connection = don't launch gui
			return;
		}
        
        // run gui
        gui.run();
	}
}