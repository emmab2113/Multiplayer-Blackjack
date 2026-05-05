package MBServer;
import java.util.List;
import java.util.Vector;
import java.util.Collections;

public class CardDeck {
	private List<Card> Cards;
	private int maxDecks;
	
	public CardDeck(int cardMaxDeck) {
		this.maxDecks = cardMaxDeck;
		this.Cards = new Vector<Card>();
		reset();
	}
	
	public void shuffle() {
		Collections.shuffle(this.Cards);
	}
	
	public Card pullCard() {
		if (!this.Cards.isEmpty()) {
			return this.Cards.remove(this.Cards.size()-1);
		}
		return null;
	}
	
	public void reset() {
		this.Cards.clear();
		
		String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
		String[] ranks = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K","A"};
		
		for (int i = 0; i< this.maxDecks; i++) {
			for (String suit : suits) {
				for (String rank : ranks) {
					this.Cards.add(new Card (rank, suit));
				}
			}
		}
	}
	
	public void addMaxDecks() {
		this.maxDecks++;
		this.reset();
	}

}