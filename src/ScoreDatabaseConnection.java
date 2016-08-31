import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * The ScoreDatabaseConnection class handles interactions with the High Score Database.
 * @author Shane Birdsall
 */
public class ScoreDatabaseConnection {
	String url = "jdbc:derby:ScoreDB;create=true"; 
    String username = "shane";
    String password = "password";
    String tableName = "HIGHSCORES";
    Connection conn = null;
    
    public void establishConnection() {
    	try { // Establish connection
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException ex) {
			System.err.println("SQL Exception thrown!");
		}
    }
	public void createTable() {
		try {
			if(doesTableExist(tableName)) {  // only create if it does not exist
				Statement statement = conn.createStatement();
				String sqlCreate= "create table "+tableName+" (NAME varchar(7)," 
						+ "SCORE int)";
				statement.executeUpdate(sqlCreate);
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("SQL Exception thrown!");
		}
	}
	public boolean doesTableExist(String name) {
        try { // use meta data to see if table exists
            DatabaseMetaData dbmd = conn.getMetaData();
            String[] a = {"TABLE"};
            ResultSet rs = dbmd.getTables(null, null, name, a);
            if(!rs.next()) {
                return true;
            } else return false;
        } catch (SQLException e) {
			System.err.println("SQL Exception thrown!");
			e.printStackTrace();
        } 
        return false;
    }
	public void addHighScores(ScoreArrayList scores) {
		try { // add high scores to table
			if(!scores.isEmpty()) {
				Statement statement = conn.createStatement();
				statement.executeUpdate("DELETE FROM "+tableName); // delete old scores
				String sqlInsert = "insert into "+tableName+" values"; // add new scores
				for(int i = 0; i < scores.size(); i++) {
					sqlInsert += "('"+scores.get(i).getName()+"', " 
									+scores.get(i).getScore()+")";
					if(i != scores.size()-1) {
						sqlInsert += ",";
					}
				}  
		       statement.executeUpdate(sqlInsert);
		       statement.close();
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception thrown!");
			e.printStackTrace();
		}
	}
	public ScoreArrayList getAllHighScores() {
		ResultSet results = null;
		ScoreArrayList scores = new ScoreArrayList(HighScorePanel.MAX_AMOUNT_OF_HIGHSCORES);
		try {
			Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String sqlQuery = "select * from HIGHSCORES "; // return all scores
			results = statement.executeQuery(sqlQuery);
			results.beforeFirst();
			while(results.next()) { // get the scores from result set
				scores.add(new Score(results.getString("NAME"), results.getInt("SCORE")));
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception thrown!");
			e.printStackTrace();
		}
		return(scores);  
	}
	public void closeConnections() { // close database connection
		if(conn!=null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.err.println("SQL Exception thrown!");
				e.printStackTrace();
			}
		}
	}
}