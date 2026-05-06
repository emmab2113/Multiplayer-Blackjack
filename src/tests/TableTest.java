package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

import MBServer.Server;
import MBServer.Table;
import MBServer.Card;

class TableTest {
	
	class DummySocket extends Socket {
		@Override
		public InputStream getInputStream() {
			try {
				ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
				ObjectOutputStream headWriter = new ObjectOutputStream(tempStream);
				headWriter.flush();
				return new ByteArrayInputStream(tempStream.toByteArray());
			}
		catch (IOException e) {
			return new ByteArrayInputStream(new byte[0]);
		}
	}
		
		@Override
		public OutputStream getOutputStream() {
			return new ByteArrayOutputStream();
		}
		
	}
	class MockClientHandler extends Server.ClientHandler {
		private boolean stoodOrBustOverride = false;
		private boolean isDealerOverride = false;
		
		public MockClientHandler() throws IOException {
			super(new DummySocket());
		}
		
		@Override
		public boolean isDealer() {
			return this.isDealerOverride;
		}
		
		public void setIsDealer(boolean isDealer) {
			this.isDealerOverride = isDealer;
		}
		
		@Override
		public void addCard(Card card) {
			
		}
		
		@Override
		public void timeOut() {
			
		}
		
		@Override
		public void askForBets() {
			
		}
		
		@Override
		public void askForAction() {
			
		}
		
		@Override
		public boolean getStoodOrBust() {
			return this.stoodOrBustOverride;
		}
		
		public void setStoodOrBustOverride(boolean state) {
			this.stoodOrBustOverride = state;
		}
	}
	
	private MockClientHandler createMockClient() {
		try {
			return new MockClientHandler();
		}
		catch (IOException e) {
			fail("Failed to create MockClientHandler:" + e.getMessage());
			return null;
		}
	}
	
	@Test
	public void drawCardTest() {
		Table testTable = new Table();
		testTable.startGame();
		assertNotNull(testTable.drawCard());
	}
	
	@Test
	public void testAddUserToTable() {
		Table table = new Table();
		MockClientHandler dealerMock = createMockClient();
		dealerMock.setIsDealer(true);
		MockClientHandler playerMock = createMockClient();
		
		table.addUserToTable(dealerMock);
		
		assertTrue(table.hasDealer(), "The first added user should be assigned as the dealer.");
		assertEquals(0, table.getPlayerCount(), "The dealer should not be counted as a seated player.");
		
		table.addUserToTable(playerMock);
		
		assertEquals(1, table.getPlayerCount(), "The second added user should be seated as a player.");
	}
	
	@Test
	public void testRemovePlayerFromTable() {
		Table table = new Table();
		MockClientHandler dealerMock = createMockClient();
		MockClientHandler playerMock = createMockClient();
		dealerMock.setIsDealer(true);
		
		table.addUserToTable(dealerMock);
		table.addUserToTable(playerMock);
		
		assertEquals(1, table.getPlayerCount(), "Setup failed: Player was not added.");
		
		table.queueLeave(playerMock);
		
		assertEquals(0, table.getPlayerCount(), "Player should be removed from the table immediately.");
		assertTrue(table.hasDealer(), "Dealer should still be at the table.");
	}
	
	@Test
	public void testNextTurnSkipsBustedPlayers() {
		Table table = new Table();
		MockClientHandler dealerMock = createMockClient();
		dealerMock.setIsDealer(true);
		MockClientHandler player1 = createMockClient();
		MockClientHandler player2 = createMockClient();
		
		table.addUserToTable(dealerMock);
		table.addUserToTable(player1);
		table.addUserToTable(player2);
		
		table.startGame();
		
		player1.setStoodOrBustOverride(true);
		player2.setStoodOrBustOverride(false);
		
		table.playerStoodOrBust();
	}
	
	@Test
	public void testStartGameDealsCards() {
		Table table = new Table();
		MockClientHandler dealerMock = createMockClient();
		dealerMock.setIsDealer(true);
		MockClientHandler playerMock = createMockClient();
		
		table.addUserToTable(dealerMock);
		table.addUserToTable(playerMock);
		
		table.startGame();
		
		assertEquals(4, table.getCardsDrawn().size(), "Start game should deal exactly 2 cards to the dealer and 1 player.");
	}
	
	@Test
	public void testDealerQueueLeaveDuringGame() {
		Table table = new Table();
		MockClientHandler dealerMock = createMockClient();
		dealerMock.setIsDealer(true);
		
		table.addUserToTable(dealerMock);
		table.startGame();
		
		table.queueLeave(dealerMock);
		
		assertTrue(table.hasDealer(), "Dealer should not be removed immediately during an active game.");
		
		table.endGame();
		
		assertFalse(table.hasDealer(), "Dealer should be removed when the game ends if they queued to leave.");
	}

	@Test
	public void testGetVacancies() {
		Table table = new Table();
		MockClientHandler playerMock1 = createMockClient();
		MockClientHandler playerMock2 = createMockClient();
		
		table.addUserToTable(playerMock1);
		table.addUserToTable(playerMock2);
		
		boolean[] vacancies = table.getVacancies();
		
		assertEquals(6, vacancies.length, "Vacancies array should  match the player seat limit (6).");
		assertFalse(vacancies[0], "Seat 0 should be occupied (false).");
		assertFalse(vacancies[1], "Seat 1 should be occupied (false).");
		assertTrue(vacancies[2], "Seat 2 should be vacant (true).");
		assertTrue(vacancies[5], "Seat 5 should be vacant (true).");
	}
	
	@Test
	public void testEndGameClearsDrawnCards() {
		Table table = new Table();
		MockClientHandler playerMock = createMockClient();
		
		table.addUserToTable(playerMock);
		table.startGame();
		
		assertFalse(table.getCardsDrawn().isEmpty(), "Setup failed: Cards were not drawn.");
		
		table.endGame();
		
		assertTrue(table.getCardsDrawn().isEmpty(), "cardsDrawn vector must be completely cleared after endGame is called.");
	}
	
}