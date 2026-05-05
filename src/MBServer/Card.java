package MBServer;

public class Card {
	
	// Fields:
	private String rank;
	private String suit;
	
	public Card(String cardRank, String cardSuit) {
		this.rank = cardRank;
		this.suit = cardSuit;
	}
	
	public String getRank() {
		return this.rank;
	}
	
	public String getSuit() {
		return this.suit;
	}

	@Override
	public String toString() {
		return rank + " of " + suit;
	}
}
