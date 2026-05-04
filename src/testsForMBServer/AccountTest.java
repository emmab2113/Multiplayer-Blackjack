package testsForMBServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import MBServer.Account;

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
	public void complexConstructorTest(){
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
		System.out.println(balanceInInt);
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
	public void setTimeOutTest(){
		testAccount.setTimeOut(300);
		assertTrue(testAccount.isTimedOut());
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
}
