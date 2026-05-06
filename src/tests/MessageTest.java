package tests;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import message.Message;
import enums.*;

public class MessageTest {
	@Test
	public void defaultConstructorTest() {
		Message msg = new Message();
		Assertions.assertAll (
			() -> Assertions.assertEquals(MessageType.Test, msg.getType()),
			() -> Assertions.assertEquals(MessageStatus.Test, msg.getStatus()),
			() -> Assertions.assertEquals("", msg.getText())
		);	
	}
	
	@Test
	public void twoArgConstructorTest() {
		Message msg = new Message(MessageType.Hit, MessageStatus.Pending);
		Assertions.assertAll (
			() -> Assertions.assertEquals(MessageType.Hit, msg.getType()),
			() -> Assertions.assertEquals(MessageStatus.Pending, msg.getStatus()),
			() -> Assertions.assertEquals("", msg.getText())
		);	
	}
	
	@Test
	public void threeArgConstructorTest() {
		Message msg = new Message(MessageType.BalanceView, MessageStatus.Success, "123");
		Assertions.assertAll (
			() -> Assertions.assertEquals(MessageType.BalanceView, msg.getType()),
			() -> Assertions.assertEquals(MessageStatus.Success, msg.getStatus()),
			() -> Assertions.assertEquals("123", msg.getText())
		);	
	}
	
	@Test
	public void emptyStringTest() {
		Message msg = new Message(MessageType.Test, MessageStatus.Test, "");
		assertEquals("", msg.getText());
	}
	
	@Test
	public void nullStringTest() {
		Message msg = new Message(MessageType.Test, MessageStatus.Test, null);
		assertNull(msg.getText());
	}
	
	
}