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
		// Constructor
		// Used when a user registers
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
		// Constructor
		// Used to create account registry on start up
		this.username = username;
		this.password = password;
		this.credentials = credentials;
		this.balance = balance;
		this.timeOut = timeOut;
		if (this.timeOut > 0) {	// Have timer tick down if user is timed out
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.execute(() ->
			{
				try {
					while (timeOut > 0) {
						Thread.sleep(1000);
						this.timeOut--;
					}
				} catch (InterruptedException e) {
					
				}
			});
			executor.shutdown();
		}
		cards = new Vector<Card>();
		activeBet = 0;
		signedIn = false;
		
	}
	public boolean modifyBalance(double balance) {
		// Change user balance
		double USDBalance = (double) (Math.round(balance * 100.0)) / 100.0; // Standardize input
		if (this.balance < USDBalance * -1) { // Check if input would lead to negative balance
			return false;
		}
		this.balance += USDBalance;
		return true;
	}
	public void setTimeOut(int timer) {
		timeOut = timer; // Set timer
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(() -> // Have timer tick down
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
	
	public boolean isTimedOut() { // Returns if player is on time out
		return (timeOut > 0);
	}
	
	public boolean setBet(double bet) {
		// Set bet
		if (modifyBalance(bet * -1) && bet > 0) { // Check if user can make bet
			activeBet = bet; // Return true if so
			return true;
		}
		return false;
	}
	
	public void resetCardsAndBet() {
		// Reset bet and empty hand
		activeBet = 0;
		cards.clear();
	}
	
	public void resetCardsAndBet(boolean payOut) {
		// Reset bet and empty hand
		// if payOut false, user breaks even on bet
		// if payOut true, get paid 2-1
		modifyBalance(activeBet);
		if (payOut) {
			modifyBalance(activeBet);
		}
		activeBet = 0;
		cards.clear();
	}
	
	public void receiveCard(Card card) { // Add a card to the user's hand
		cards.add(card);
	}
	
	public Vector<Card> getCards() { // Return cards in hand
		return cards;
	}
	
	public boolean validate(String username, String password, boolean credentials) {
		// Validate log in by comparing log in details
		// And checking if user is not already logged in
		// Returns true if user is validated
		Boolean isValid = (this.username.compareTo(username) == 0 && this.password.compareTo(password) == 0
				&& this.credentials == credentials && !signedIn);
		if (isValid) {
			signedIn = true;
			return true;
		}
		return false;
	}
	public double getBalance() { // Returns balance
		return balance;
	}
	public boolean signOut() { // Sign user out of account
		if (!signedIn) {	// Returns true if account was signed in
			return false;
		}
		signedIn = false;
		return true;
	}
	public int getTimeOut() { // Returns remaining time out
		return timeOut;
	}
	public String getUsername() { // Returns username
		return username;
	}
	public boolean getCredentials() { // Returns dealer credentials
		return credentials;
	}
}

