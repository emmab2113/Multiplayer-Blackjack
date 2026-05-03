package MBServer;
import java.util.Vector;

public class Table {
	
//	private ClientHandler[] players;
//	private ClientHandler dealer;
	private CardDeck shoe;
	private int timer;
	private boolean dealerLeave;
	private boolean gameActive;
	private int playersTurn;
	private Vector<Card> cardsDrawn;
	
	public Table() {
		//this.players = new ClientHandler[5];
		
		this.shoe = new CardDeck(6);
		this.shoe.shuffle();
		this.cardsDrawn = new Vector<>();
		
		this.gameActive = false;
		this.dealerLeave = false;
		this.timer = 0;
		this.playersTurn = 0;
	}
	
	public void startGame() {
		this.gameActive = true;
	}
	
	public void nextTurn () {
		
	}
	
	public void endGame() {
		this.shoe.reset();
		this.shoe.shuffle();
		this.cardsDrawn.clear();
		
//		this.gameActive = false;
//		if (this.dealerLeave && this.dealer != null) {
//			removeUserFromTable(this.dealer);
//		}
	}
	
	public Card drawCard() {
		Card pulled = this.shoe.pullCard();
		if (pulled != null) {
			this.cardsDrawn.add(pulled);
		}
		return pulled;
	}
	
//	public int getPlayerCount() {
//		int count = 0;
//		for (ClientHandler player : players) {
//			if (player != null) {
//				count++;
//			}
//		}
//		return count;
//	}
//	
//	public boolean hasDealer() {
//		return this.dealer != null;
//	}
//	
//	public void addUserToTable(ClientHandler user) {
//		
//	}
//	
//	public void queueLeave(ClientHandler user) {
//		
//	}
//	
//	private void removeUserFromTable(ClientHandler user) {
//		
//	}
//
//	public boolean[] getVacancies() {
//		boolean[] vacancies = new boolean[this.players.length];
//		for (int i = 0; i < this.players.length; i++) {
//			vacancies[i] = (this.players[i] == null);
//		}
//		return vacancies;
//	}
//	
//	public Vector<Card> getCardsDrawn() {
//		return this.cardsDrawn;
//	}
//	
//	public void playerStoodOrBust() {
//		
//	}
}
