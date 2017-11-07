package cbn.webscreen.data.persistence.entitymanager;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class DatabaseConnectionFactory {
	
	private static Logger logger = Logger.getLogger(DatabaseConnectionFactory.class);

	private static final String DB_PATH = System.getProperty("server.home") 
			+ File.separator + "data" 
			+ File.separator + "webscreen.db";
	
	static {
		new File(DB_PATH).getParentFile().mkdirs();
		logger.info("Database location: " + DB_PATH);
	}
	
	public static Connection createConnection() throws SQLException{
            // db parameters
            String url = "jdbc:sqlite:" + DB_PATH;
            
            // create a connection to the database
            return DriverManager.getConnection(url);
    }
	
}
