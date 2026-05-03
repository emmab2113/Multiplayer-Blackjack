import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CardDeckTest {

	@Test
	void testCreatingOneDeck() {
		// Creates one deck
		CardDeck deck = new CardDeck(1);
		
		// List out expected card types
		List<String> expectedCards = new ArrayList<>();
		String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
		String[] ranks = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K","A"};
		
		// Create expected cards
		for (String suit : suits) {
			for (String rank : ranks) {
				expectedCards.add(rank + " " + suit);
			}
		}
		
		// Creates cards being pulled
		int cardsPulled = 0;
		Card pulledCard = deck.pullCard();
		
		// Loops through deck until it's empty
		while (pulledCard != null) {
			cardsPulled++;
			
			String cardIdentity = pulledCard.getRank() + " " + pulledCard.getSuit();
			
			boolean wasInChecklist = expectedCards.remove(cardIdentity);
			assertTrue(wasInChecklist, "Found an unexpected or duplicate card: ");
			
			pulledCard = deck.pullCard();
		}
		
		assertEquals(52, cardsPulled, "52 cards should have been pulled from a 1-deck shoe.");
		
		assertTrue(expectedCards.isEmpty(), "The deck was missing the following cards: " + expectedCards);
	}

}
