package MBClient;

import enums.ErrorType;

public class Client {
	private boolean loggedIn;
	private boolean[] seatedPlayers;
	private String IPAddress;
	private String username;
	private Message messageLog; // should be array?
	
	public static void main(String[] args) {
		
	}
	
	public void joinTable() {}
	public void leaveTable() {}
	public void requestRegistry(String username, String password, String credentials) {}
	public void requestLogIn(String username, String password, String credentials) {}
	public void requestBalanceDisplay() {}
	public void requestBalanceDeposit(Double currency) {}
	public void requestBalanceWithdrawal(Double currency) {}
	public void displayError(ErrorType errorType) {}
	public double chooseChips() {}
	public void hit() {}
	public void stand() {}
	public void terminateSession() {}
	public String establishConnection() {}
	public void displayGameUsers() {}
	public void displayGameCards() {}
	public void displayGameStatus() {}
	private void getIPAddress() {}
}