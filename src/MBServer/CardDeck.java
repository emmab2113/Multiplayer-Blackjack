package MBServer;

import java.util.List;
import java.util.ArrayList;

public class CardDeck {
	private List<Card> Cards;
	private int maxDeck;
	
	public CardDeck(int cardMaxDeck) {
		this.maxDeck = cardMaxDeck;
	}
	
	public void shuffle() {
		
	}
	
	public Card pullCard() {
		return Cards.getFirst();
	}
	
	public void reset() {
		
	}
	
	public void addDecks() {
		
	}

}
