import static org.junit.Assert.*;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;

public class DatabaseConnectionTest {
	ScoreDatabaseConnection ScoreDB = new ScoreDatabaseConnection();
	@Test
	public void testEstablishConnection() {
		ScoreDB.establishConnection();
		Assert.assertNotNull(ScoreDB.conn);
		ScoreDB.closeConnections();
		try {
			Assert.assertTrue(ScoreDB.conn.isClosed());
		} catch (SQLException e) {
			fail("SQL Exception");
		}
	} // Success indicates database connection is working correctly
}
