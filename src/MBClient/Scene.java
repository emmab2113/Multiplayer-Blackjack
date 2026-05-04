package MBClient;

import javax.swing.JPanel;

public interface Scene {
	void construct();
	void destruct();	
	JPanel getPanel();
}
