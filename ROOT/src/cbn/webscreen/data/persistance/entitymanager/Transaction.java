package cbn.webscreen.data.persistance.entitymanager;

import java.sql.Connection;
import java.sql.SQLException;

public class Transaction {

	Connection connection = null;

	public void begin() throws SQLException {
		connection = DatabaseConnectionFactory.createConnection();
		connection.setAutoCommit(false);

	}

	public void commit() throws SQLException {
		if (connection != null) {
			connection.commit();
			connection.close();
		}
	}

	public Connection getConnection() {
		return connection;
	}
	

}
