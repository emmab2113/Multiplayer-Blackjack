package MBServer;

import MBClient.Client;

public class Table{
	private Server.ClientHandler dealer;
	private Server.ClientHandler[] players;
	private Card[] cardHistory;
	private static int counter = 0;
	
	public Table(){
		players = new Server.ClientHandler[6];
		cardHistory = new Card[6];
		
		for (int i = 0; i < 6; i++) {
			cardHistory[i] = new Card("A","Clubs");
		}
	}
	
	public Card drawCard() {
		Card card = cardHistory[counter];
		counter++;
		return card;
	}
	
	public void addUserToTable(Server.ClientHandler user) {
		players[0] = user;
		user.askForAction();
	}
}