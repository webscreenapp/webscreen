package cbn.webscreen.data.persistance.entitymanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import cbn.webscreen.data.persistance.entity.LoginAccess;

public class LoginAccessEntityManager {

	public static void createTable() throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();

		String sql = 
				"CREATE TABLE IF NOT EXISTS login_access (" + 
				"  login_id integer NOT NULL, " + 
				"  access_login_id integer NOT NULL, " + 
				"  PRIMARY KEY (login_id, access_login_id)," +
				"  FOREIGN KEY (login_id) REFERENCES login(id) ON DELETE CASCADE," + 
				"  FOREIGN KEY (access_login_id) REFERENCES login(id) ON DELETE CASCADE" + 
				");";
				
		Statement st = connection.createStatement();
		st.execute(sql);
		
		connection.close();
	}

	private static LoginAccess map(ResultSet resultSet) throws SQLException {
		LoginAccess loginAccess = new LoginAccess();
		
		loginAccess.loginId = resultSet.getInt("login_id");
		loginAccess.accessLoginId = resultSet.getInt("access_login_id");
		
		return loginAccess;
	}
	
	public static void insert(LoginAccess loginAccess) throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		insert(loginAccess, connection);
		connection.close();
	}

	public static void insert(LoginAccess loginAccess, Transaction transaction) throws SQLException {
		insert(loginAccess, transaction.getConnection());
	}
	
	public static void insert(LoginAccess loginAccess, Connection connection) throws SQLException {
		
		String sql = "INSERT INTO login_access (login_id, access_login_id) VALUES (?, ?);";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		st.setInt(1, loginAccess.loginId);
		st.setInt(2, loginAccess.accessLoginId);

		st.executeUpdate();
	}

	public static void delete(LoginAccess loginAccess) throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		delete(loginAccess, connection);
		connection.close();
	}
	
	public static void delete(LoginAccess loginAccess, Transaction transaction) throws SQLException {
		delete(loginAccess, transaction.getConnection());
	}
	
	public static void delete(LoginAccess loginAccess, Connection connection) throws SQLException {
		
		String sql = "DELETE FROM login_access WHERE login_id = ? AND access_login_id = ?;";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		st.setInt(1, loginAccess.loginId);
		st.setInt(2, loginAccess.accessLoginId);
		
		st.executeUpdate();
	}
	
	public static LoginAccess selectByLoginIdAndAccessLoginId(int loginId, int accessLoginId) throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		
		try {
			return selectByLoginIdAndAccessLoginId(loginId, accessLoginId, connection);
		} finally {
			connection.close();
		}
	}
	
	public static LoginAccess selectByLoginIdAndAccessLoginId(int loginId, int accessLoginId, Transaction transaction) throws SQLException {
		return selectByLoginIdAndAccessLoginId(loginId, accessLoginId, transaction.getConnection());
	}
	
	public static LoginAccess selectByLoginIdAndAccessLoginId(int loginId, int accessLoginId, Connection connection) throws SQLException {
		
		String sql = "SELECT login_id, access_login_id FROM login_access WHERE login_id = ? AND access_login_id = ?;";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		st.setInt(1, loginId);
		st.setInt(2, accessLoginId);
		
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			return map(rs);
		}
		
		return null;
	}
	
	public static LoginAccess selectByLoginAndAccessLogin(String login, String accessLogin) throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		
		try {
			return selectByLoginAndAccessLogin(login, accessLogin, connection);
		} finally {
			connection.close();
		}
	}
	
	public static LoginAccess selectByLoginAndAccessLogin(String login, String accessLogin, Transaction transaction) throws SQLException {
		return selectByLoginAndAccessLogin(login, accessLogin, transaction.getConnection());
	}
	
	public static LoginAccess selectByLoginAndAccessLogin(String login, String accessLogin, Connection connection) throws SQLException {
		
		String sql = ""
				+ "SELECT login_id, access_login_id "
				+ "FROM login_access la "
				+ "JOIN login l ON (la.login_id = l.id) "
				+ "JOIN login a ON (la.access_login_id = a.id) "
				+ "WHERE l.login = ? AND a.login = ?;";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		st.setString(1, login);
		st.setString(2, accessLogin);
		
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			return map(rs);
		}
		
		return null;
	}
	
	public static List<LoginAccess> selectAll() throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		
		try {
			return selectAll(connection);
		} finally {
			connection.close();
		}
	}
	
	public static List<LoginAccess> selectAll(Transaction transaction) throws SQLException {
		return selectAll(transaction.getConnection());
	}
	
	public static List<LoginAccess> selectAll(Connection connection) throws SQLException {
		
		List<LoginAccess> result = new LinkedList<LoginAccess>();
		
		String sql = "SELECT login_id, access_login_id FROM login_access;";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			result.add(map(rs));
		}
		
		return result;
	}

	public static List<LoginAccess> selectAllByLoginId(int loginId) throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		
		try {
			return selectAllByLoginId(loginId, connection);
		} finally {
			connection.close();
		}
	}
	
	public static List<LoginAccess> selectAllByLoginId(int loginId, Transaction transaction) throws SQLException {
		return selectAllByLoginId(loginId, transaction.getConnection());
	}
	
	public static List<LoginAccess> selectAllByLoginId(int loginId, Connection connection) throws SQLException {
		
		List<LoginAccess> result = new LinkedList<LoginAccess>();
		
		String sql = "SELECT login_id, access_login_id FROM login_access WHERE login_id = ?;";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		st.setInt(1, loginId);
		
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			result.add(map(rs));
		}
		
		return result;
	}
	
}
