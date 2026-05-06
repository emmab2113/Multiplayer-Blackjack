package tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses ({
	AccountTest.class,
	CardDeckTest.class,
	MessageTest.class,
	TableTest.class
})
public class TestSuite {
}