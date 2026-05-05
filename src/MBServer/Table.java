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
		this.players = new Server.ClientHandler[6];
		
		this.shoe = new CardDeck(6);
		this.shoe.shuffle();
		this.cardsDrawn = new Vector<>();
		this.shoe.reset();
		this.shoe.shuffle();
		
		this.gameActive = false;
		this.dealerLeave = false;
		this.timer = 0;
		this.playersTurn = 0;
	}
	
	public void startGame() {
		this.gameActive = true;
		for (Server.ClientHandler player : this.players) {
			if (player != null) {
				player.getGameUsers();
				player.addCard(drawCard());
				player.addCard(drawCard());
			}
		}
		this.dealer.getGameUsers();
		this.dealer.addCard(drawCard());
		this.dealer.addCard(drawCard());
		this.timer = 42;
	}
	
	public void nextTurn () {
		int playersStoodOrBust = 0;
		while (playersTurn < players.length) {
			Server.ClientHandler currentPlayer = players[playersTurn];
			
			if (currentPlayer != null && !currentPlayer.getStoodOrBust()) {
				this.timer = 42;
				currentPlayer.askForAction();
				return;
			}
			playersTurn++;
			if (playersTurn == players.length) {
				playersTurn = 0;
			}
		}
		endGame();
	}
	
	public void endGame() {
		this.shoe.reset();
		this.shoe.shuffle();
		this.cardsDrawn.clear();
		this.timer = 42;
		
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
		for (Server.ClientHandler player : this.players) {
			if (player != null) {
				player.getGameCards();
			}
		}
		this.dealer.getGameCards();
		return pulled;
	}
	
	public int getPlayerCount() {
		int count = 0;
		for (Server.ClientHandler player : this.players) {
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
		if (user.isDealer() && this.dealer == null) {
			dealer = user;
			return;
		}
		for (int i = 0; i < this.players.length; i++) {
			if (this.players[i] == null) {
				this.players[i] = user;
				return;
			}
		}
	}
	
	public void queueLeave(Server.ClientHandler user) {
		if (user == dealer && gameActive) {
			dealerLeave = true;
		}
		else {
			removeUserFromTable(user);
		}
	}
	
	private void removeUserFromTable(Server.ClientHandler user) {
		if (user == dealer) {
			this.dealer = null;
			return;
		}
		for (int i = 0; i < this.players.length; i++) {
			if (user == this.players[i]) {
				if (this.gameActive) {
					this.players[i].timeOut();
				}
				this.players[i] = null;
				return;
			}
		}
		for (Server.ClientHandler player : this.players) {
			if (player != null) {
				player.getGameUsers();
			}
		}
		this.dealer.getGameUsers();
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
	
	public Vector<Card>[] getAllUsersHands() {
		Vector<Card>[] allUsersHands = new Vector[7];
		for (int i = 0; i < this.players.length; i++) {
			if (this.players[i] != null) {
				allUsersHands[i] = this.players[i].getHand();
			}
		}
		if (hasDealer()) {
			allUsersHands[6] = this.dealer.getHand();
		}
		return allUsersHands;
	}
	
	public void playerStoodOrBust() {
		this.playersTurn++;
		nextTurn();
	}
	
	public void dealerCancelledGame() {
		gameActive = false;
		for (Server.ClientHandler player: this.players) {
			if (player != null) {
				player.restartGame(false);
				player.removeFromTable();
				removeUserFromTable(player);
			}
		}
		dealer.restartGame();
		dealer.removeFromTable();
		removeUserFromTable(dealer);
	}
}