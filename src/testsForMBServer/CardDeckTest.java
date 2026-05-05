package testsForMBServer;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import MBServer.Card;
import MBServer.CardDeck;

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
	
	@Test
	public void testShuffleChangesOrder() {
		CardDeck unshuffledDeck = new CardDeck(1);
		CardDeck shuffledDeck = new CardDeck(1);
		
		shuffledDeck.shuffle();
		
		List<String> unshuffledOrder = new ArrayList<>();
		List<String> shuffledOrder = new ArrayList<>();
		
		Card uCard= unshuffledDeck.pullCard();
		while (uCard != null) {
			unshuffledOrder.add(uCard.getRank() + " " + uCard.getSuit());
			uCard = unshuffledDeck.pullCard();
		}
		
		Card sCard = shuffledDeck.pullCard();
		while (sCard != null) {
			shuffledOrder.add(sCard.getRank() + " " + sCard.getSuit());
			sCard = shuffledDeck.pullCard();
		}
		
		assertEquals (52, unshuffledOrder.size());
		assertEquals (52, shuffledOrder.size());
		assertNotEquals(unshuffledOrder, shuffledOrder, "The shuffled deck's order should not match the unshuffled deck.");
	}
	
	@Test
	public void testResetRestoresFullDeck() {
		int maxDecks = 2;
		CardDeck deck = new CardDeck(maxDecks);
		
		for (int i = 0; i < 15; i++) {
			deck.pullCard();
		}
		
		deck.reset();
		
		int cardsAfterReset = 0;
		while (deck.pullCard() != null) {
			cardsAfterReset++;
		}
		
		assertEquals(52 * maxDecks, cardsAfterReset, "Resetting a 2-deck shoe should restore it to exactly 104 cards.");
	}
	
	@Test
	public void testAddMaxDecksIncreasesCapacity() {
		CardDeck deck = new CardDeck(1);
		
		deck.addMaxDecks();
		
		int totalCards = 0;
		while (deck.pullCard() != null); {
			totalCards++;
		}
		
		assertEquals(104, totalCards, "Calling addMaxDecks() on a 1-deck shoe should reset it as a 2-deck shoe (104 cards).");
	}

}
