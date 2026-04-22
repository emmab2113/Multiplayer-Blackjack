package normal;

public class Account {
	private String username;
	private String password;
	private String credentials;
	private double balance;
	private int timeOut;
//	private Vector<Card> cards;
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
		if (balance > this.balance) {
			return false;
		}
		this.balance += balance;
		return true;
	}
	public void setTimeOut(int timer) {
		timeOut = timer;
	}
	public boolean isTimedOut() {
		return (timeOut > 0);
	}
	public boolean setBet(double bet) {
		if (modifyBalance(bet)) {
			activeBet = bet;
			return true;
		}
		return false;
	}
	public void resetCardsAndBet() {
		activeBet = 0;
	}
	/*
	public void receiveCard(Card card) {
		
	}
	
	public Card getCards() {
	
	}
	*/
	public boolean validate(String username, String password, String credentials) {
		return (this.username == username && this.password == password && this.credentials == credentials && !signedIn);
	}
	public double getBalance() {
		return balance;
	}
	public boolean SignOut() {
		signedIn = false;
		return true;
	}

}

