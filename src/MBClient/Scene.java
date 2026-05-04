package MBClient;

public interface Scene {
	void construct();
	void destruct();	
	void render();
	void update();
	void handleInput();

}
