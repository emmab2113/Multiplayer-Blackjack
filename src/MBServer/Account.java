package MBServer;

import java.util.Vector;

public class Account {
	private String username;
	private String password;
	private String credentials;
	private double balance;
	private int timeOut;
	private Vector<Card> cards;
	private double activeBet;
	private boolean signedIn;
	
	public Account(String username, String password, String credentials) {
		this.username = username;
		this.password = password;
		this.credentials = credentials;
		balance = 0;
		timeOut = 0;
		activeBet = 0;
		signedIn = false;
	}
	public Account(String username, String password, String credentials, double balance, int timeOut) {
		this.username = username;
		this.password = password;
		this.credentials = credentials;
		this.balance = balance;
		this.timeOut = timeOut;
		activeBet = 0;
		signedIn = false;
		
	}
	public boolean modifyBalance(double balance) {
		double USDBalance = Math.round(balance * 100.0) / 100.0;
		if (USDBalance * -1 > balance) {
			return false;
		}
		balance += USDBalance;
		return true;
	}
	public void setTimeOut(int timer) {
		timeOut = timer;
	}
	public boolean isTimedOut() {
		return (timeOut > 0);
	}
	public boolean setBet(double bet) {
		if (modifyBalance(bet * -1) && bet > 0) {
			activeBet = bet;
			return true;
		}
		return false;
	}
	public void resetCardsAndBet() {
		modifyBalance(activeBet * 2); // Temporary line; pay out should be given by table;
		activeBet = 0;
		cards.clear();
	}
	
	public void receiveCard(Card card) {
		cards.add(card);
	}
	
	public Vector<Card> getCards() {
		return cards;
	}
	
	public boolean validate(String username, String password, String credentials) {
		Boolean isValid = (this.username.compareTo(username) == 0 && this.password.compareTo(password) == 0
				&& this.credentials.compareTo(credentials) == 0 && !signedIn);
		if (isValid) {
			signedIn = true;
			return true;
		}
		return false;
	}
	public double getBalance() {
		return balance;
	}
	public boolean SignOut() {
		signedIn = false;
		return true;
	}
	public int getTimeOut() {
		return timeOut;
	}

}

