package testsForMBServer;

import static org.junit.Assert.assertNotNull;

import 	org.junit.jupiter.api.Test;

import MBServer.Account;

public class AccountTest5 {
	
	@Test
	public void simpleConstructorTest(){
		Account testAccount = new Account("Player123","password","0");
		assertNotNull(testAccount);
	}
}
