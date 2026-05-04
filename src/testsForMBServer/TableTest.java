package testsForMBServer;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import MBServer.Table;

public class TableTest {
	@Test
	public void drawCardTest() {
		Table testTable = new Table();
		testTable.startGame();
		assertNotNull(testTable.drawCard());
	}
}
