package cbn.webscreen.data.persistence.entitymanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import cbn.webscreen.data.persistence.entity.Login;

public class LoginEntityManager {
	
	public static void createTable() throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();

		String sql = 
				"CREATE TABLE IF NOT EXISTS login (" + 
				"  id integer PRIMARY KEY AUTOINCREMENT, " + 
				"  login text NOT NULL UNIQUE, " + 
				"  password_hash text NOT NULL " + 
				");";
				
		Statement st = connection.createStatement();
		st.execute(sql);
		
		connection.close();
	}

	private static Login map(ResultSet resultSet) throws SQLException {
		Login login = new Login();
		
		login.id = resultSet.getInt("id");
		login.login = resultSet.getString("login");
		login.passwordHash = resultSet.getString("password_hash");
		
		return login;
	}
	
	public static void insert(Login login) throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		insert(login, connection);
		connection.close();
	}

	public static void insert(Login login, Transaction transaction) throws SQLException {
		insert(login, transaction.getConnection());
	}
	
	public static void insert(Login login, Connection connection) throws SQLException {
		
		String sql = "INSERT INTO login (login, password_hash) VALUES (?, ?);";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		st.setString(1, login.login);
		st.setString(2, login.passwordHash);

		st.executeUpdate();
		
		ResultSet rs = st.getGeneratedKeys();
		
		while (rs.next()) {
			login.id = rs.getInt(1);
			return;
		}
	}

	public static void update(Login login) throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		update(login, connection);
		connection.close();
	}
	
	public static void update(Login login, Transaction transaction) throws SQLException {
		update(login, transaction.getConnection());
	}
	
	public static void update(Login login, Connection connection) throws SQLException {
		
		String sql = "UPDATE login SET login = ?, password_hash = ? WHERE id = ?;";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		st.setString(1, login.login);
		st.setString(2, login.passwordHash);
		st.setInt(3, login.id);
		
		st.execute();
	}

	public static Login selectById(int id) throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		
		try {
			return selectById(id, connection);
		} finally {
			connection.close();
		}
	}
	
	public static Login selectById(int id, Transaction transaction) throws SQLException {
		return selectById(id, transaction.getConnection());
	}
	
	public static Login selectById(int id, Connection connection) throws SQLException {
		
		String sql = "SELECT id, login, password_hash FROM login WHERE id = ?;";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		st.setInt(1, id);
		
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			return map(rs);
		}
		
		return null;
	}

	public static Login selectByLogin(String login) throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		
		try {
			return selectByLogin(login, connection);
		} finally {
			connection.close();
		}
	}
	
	public static Login selectByLogin(String login, Transaction transaction) throws SQLException {
		return selectByLogin(login, transaction.getConnection());
	}
	
	public static Login selectByLogin(String login, Connection connection) throws SQLException {
		
		String sql = "SELECT id, login, password_hash FROM login WHERE login = ?;";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		st.setString(1, login);
		
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			return map(rs);
		}
		
		return null;
	}

	public static Login selectByLowerCaseLogin(String lowerCaseLogin) throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		
		try {
			return selectByLowerCaseLogin(lowerCaseLogin, connection);
		} finally {
			connection.close();
		}
	}
	
	public static Login selectByLowerCaseLogin(String lowerCaseLogin, Transaction transaction) throws SQLException {
		return selectByLowerCaseLogin(lowerCaseLogin, transaction.getConnection());
	}
	
	public static Login selectByLowerCaseLogin(String lowerCaseLogin, Connection connection) throws SQLException {
		
		String sql = "SELECT id, login, password_hash FROM login WHERE LOWER(login) = ?;";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		st.setString(1, lowerCaseLogin);
		
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			return map(rs);
		}
		
		return null;
	}
	
	public static List<Login> selectAll() throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		
		try {
			return selectAll(connection);
		} finally {
			connection.close();
		}
	}
	
	public static List<Login> selectAll(Transaction transaction) throws SQLException {
		return selectAll(transaction.getConnection());
	}
	
	public static List<Login> selectAll(Connection connection) throws SQLException {
		
		List<Login> result = new LinkedList<Login>();
		
		String sql = "SELECT id, login, password_hash FROM login;";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			result.add(map(rs));
		}
		
		return result;
	}
	
}
