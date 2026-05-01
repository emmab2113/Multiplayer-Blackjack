package testsForMBServer;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({AccountTest.class, ServerTest.class, ClientHandlerTest.class})
public class MBServerTestSuite {

}
