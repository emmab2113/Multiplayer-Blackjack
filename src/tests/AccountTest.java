package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import MBServer.Account;
import MBServer.Card;

public class AccountTest {
	Account testAccount;
	
	@BeforeEach
	public void testPreparations() {
		testAccount = new Account("Player123","password",false);
	}
	
	@Test
	public void simpleConstructorTest(){
		assertNotNull(testAccount);
	}
	
	@Test
	public void complexConstructorTest1(){
		Account testAccount2 = new Account("Player123","password",false, 100.00, 0);
		assertNotNull(testAccount2);
	}
	
	@Test
	public void getBalanceTest(){
		int balanceInInt = (int) testAccount.getBalance();
		assertEquals(0, balanceInInt);
	}
	
	@Test
	public void modifyBalanceTest1(){
		assertTrue(testAccount.modifyBalance(1));
	}
	
	@Test
	public void modifyBalanceTest2(){
		testAccount.modifyBalance(1);
		int balanceInInt = (int) testAccount.getBalance();
		assertEquals(1, balanceInInt);
	}
	
	@Test
	public void modifyBalanceTest3(){
		testAccount.modifyBalance(1);
		testAccount.modifyBalance(-1);
		int balanceInInt = (int) testAccount.getBalance();
		assertEquals(0, balanceInInt);
	}
	
	@Test
	public void modifyBalanceTest4(){
		assertFalse(testAccount.modifyBalance(-1));
	}
	
	@Test
	public void isTimedOutTest(){
		assertFalse(testAccount.isTimedOut());
	}
	
	@Test
	public void setTimeOutTest1(){
		testAccount.setTimeOut(300);
		assertTrue(testAccount.isTimedOut());
	}
	
	@Test
	public void getTimeOutTest(){
		testAccount.setTimeOut(300);
		assertEquals(300, testAccount.getTimeOut());
	}
	
	@Test
	public void setTimeOutTest2(){
		testAccount.setTimeOut(300);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertNotEquals(300, testAccount.getTimeOut());
	}
	
	@Test
	public void complexConstructorTest2(){
		Account testAccount2 = new Account("Player123","password",false, 100.00, 300);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertNotEquals(300, testAccount.getTimeOut());
	}
	
	@Test
	public void setBetTest1(){
		testAccount.modifyBalance(1);
		assertTrue(testAccount.setBet(1));
	}
	
	@Test
	public void setBetTest2(){
		assertFalse(testAccount.setBet(1));
	}
	
	@Test
	public void getCardsTest(){
		assertTrue(testAccount.getCards().isEmpty());
	}
	
	@Test
	public void receiveCardTest(){
		testAccount.receiveCard(new Card("A", "Clubs"));
		assertFalse(testAccount.getCards().isEmpty());
	}
	
	@Test
	public void resetCardsAndBetTest1(){
		testAccount.receiveCard(new Card("A", "Clubs"));
		testAccount.resetCardsAndBet();
		assertTrue(testAccount.getCards().isEmpty());
	}
	
	@Test
	public void resetCardsAndBetTest2(){
		testAccount.modifyBalance(10);
		testAccount.setBet(5);
		testAccount.receiveCard(new Card("A", "Clubs"));
		testAccount.resetCardsAndBet();
		int balanceInInt = (int) testAccount.getBalance();
		assertEquals(5, balanceInInt);
		assertTrue(testAccount.getCards().isEmpty());
	}
	
	@Test
	public void resetCardsAndBetTest3(){
		testAccount.modifyBalance(10);
		testAccount.setBet(5);
		testAccount.resetCardsAndBet(false);
		int balanceInInt = (int) testAccount.getBalance();
		assertEquals(10, balanceInInt);
	}
	
	@Test
	public void resetCardsAndBetTest4(){
		testAccount.modifyBalance(10);
		testAccount.setBet(5);
		testAccount.resetCardsAndBet(true);
		int balanceInInt = (int) testAccount.getBalance();
		assertEquals(15, balanceInInt);
	}
	
	@Test
	public void validateTest1() {
		assertTrue(testAccount.validate("Player123","password",false));
	}
	
	@Test
	public void validateTest2() {
		assertFalse(testAccount.validate("PlayerEpic","password",false));
	}
	
	@Test
	public void validateTest3() {
		testAccount.validate("Player123","password",false);
		assertFalse(testAccount.validate("Player123","password",false));
	}
	
	@Test
	public void signOutTest1() {
		assertFalse(testAccount.signOut());
	}
	
	@Test
	public void signOutTest2() {
		boolean testSignedIn = testAccount.validate("Player123","password",false);
		assertTrue(testSignedIn);
		
		assertTrue(testAccount.signOut());
	}
	
	@Test
	public void getUsernameTest() {
		String testUsername = "Player123";
		String actualUsername = testAccount.getUsername();
		assertEquals(0, testUsername.compareTo(actualUsername));
	}
	
	@Test
	public void getCredentials() {
		assertFalse(testAccount.getCredentials());
	}
}
