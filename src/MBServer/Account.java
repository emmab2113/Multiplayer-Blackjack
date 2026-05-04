package MBServer;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Account {
	private String username;
	private String password;
	private boolean credentials;
	private double balance;
	private int timeOut;
	private Vector<Card> cards;
	private double activeBet;
	private boolean signedIn;
	
	public Account(String username, String password, boolean credentials) {
		this.username = username;
		this.password = password;
		this.credentials = credentials;
		balance = 0;
		timeOut = 0;
		cards = new Vector<Card>();
		activeBet = 0;
		signedIn = false;
	}
	public Account(String username, String password, boolean credentials, double balance, int timeOut) {
		this.username = username;
		this.password = password;
		this.credentials = credentials;
		this.balance = balance;
		this.timeOut = timeOut;
		cards = new Vector<Card>();
		activeBet = 0;
		signedIn = false;
		
	}
	public boolean modifyBalance(double balance) {
		double USDBalance = (double) (Math.round(balance * 100.0)) / 100.0;
		if (this.balance < USDBalance * -1) {
			return false;
		}
		this.balance += USDBalance;
		return true;
	}
	public void setTimeOut(int timer) {
		timeOut = timer;
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(() -> 
		{
			try {
				while (timeOut > 0) {
					Thread.sleep(1000);
					timeOut--;
				}
			} catch (InterruptedException e) {
				
			}
		});
		executor.shutdown();
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
	public void resetCardsAndBet(boolean payOut) {
		if (payOut) {
			modifyBalance(activeBet * 2);
		}
		activeBet = 0;
		cards.clear();
	}
	
	public void receiveCard(Card card) {
		cards.add(card);
	}
	
	public Vector<Card> getCards() {
		return cards;
	}
	
	public boolean validate(String username, String password, boolean credentials) {
		Boolean isValid = (this.username.compareTo(username) == 0 && this.password.compareTo(password) == 0
				&& this.credentials == credentials && !signedIn);
		if (isValid) {
			signedIn = true;
			return true;
		}
		return false;
	}
	public double getBalance() {
		return balance;
	}
	public boolean signOut() {
		signedIn = false;
		return true;
	}
	public int getTimeOut() {
		return timeOut;
	}
	public String getUsername() {
		return username;
	}
	public boolean getCredentials() {
		return credentials;
	}
}

