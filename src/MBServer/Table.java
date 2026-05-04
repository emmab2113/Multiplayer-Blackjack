package MBServer;
import java.util.Vector;

public class Table {
	
	private Server.ClientHandler[] players;
	private Server.ClientHandler dealer;
	private CardDeck shoe;
	private int timer;
	private boolean dealerLeave;
	private boolean gameActive;
	private int playersTurn;
	private Vector<Card> cardsDrawn;
	
	public Table() {
		this.players = new Server.ClientHandler[5];
		
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
		this.playersTurn = 0;
		
		for (Server.ClientHandler player : players) {
			if (player != null) {
				player.askForBets();
			}
		}
	}
	
	public void nextTurn () {
		while (playersTurn < players.length) {
			Server.ClientHandler currentPlayer = players[playersTurn];
			
			if (currentPlayer != null && !currentPlayer.getStoodOrBust()) {
				currentPlayer.askForAction();
				return;
			}
			playersTurn++;
		}
		endGame();
	}
	
	public void endGame() {
		this.shoe.reset();
		this.shoe.shuffle();
		this.cardsDrawn.clear();
		
		this.gameActive = false;
		if (this.dealerLeave && this.dealer != null) {
			removeUserFromTable(this.dealer);
		}
	}
	
	public Card drawCard() {
		Card pulled = this.shoe.pullCard();
		if (pulled != null) {
			this.cardsDrawn.add(pulled);
		}
		return pulled;
	}
	
	public int getPlayerCount() {
		int count = 0;
		for (Server.ClientHandler player : players) {
			if (player != null) {
				count++;
			}
		}
		return count;
	}
	
	public boolean hasDealer() {
		return this.dealer != null;
	}
	
	public void addUserToTable(Server.ClientHandler user) {
		if (this.dealer == null) {
			this.dealer = user;
			return;
		}
		
		for (int i = 0; i < players.length; i++) {
			if (players[i] == null) {
				players[i] = user;
				break;
			}
		}
		
	}
	
	public void queueLeave(Server.ClientHandler user) {
		if (user == this.dealer) {
			if (this.gameActive) {
				this.dealerLeave = true;
			}
			else {
				removeUserFromTable(user);
			}
		}
		else {
			removeUserFromTable(user);
		}
	}
	
	private void removeUserFromTable(Server.ClientHandler user) {
		if (this.dealer == user) {
			this.dealer = null;
			return;
		}
		
		for (int i = 0; i < players.length; i++) {
			if (players[i] == user) {
				players[i] = null;
				break;
			}
		}
	}

	public boolean[] getVacancies() {
		boolean[] vacancies = new boolean[this.players.length];
		for (int i = 0; i < this.players.length; i++) {
			vacancies[i] = (this.players[i] == null);
		}
		return vacancies;
	}
	
	public Vector<Card> getCardsDrawn() {
		return this.cardsDrawn;
	}
	
	public void playerStoodOrBust() {
		this.playersTurn++;
		nextTurn();
	}
}
