package cbn.webscreen.data.persistence.entitymanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import cbn.webscreen.data.persistence.entity.Login;
import cbn.webscreen.data.persistence.entity.LoginAccessQueryResult;

public class CustomQuery {

	public static List<LoginAccessQueryResult> selectAllLoginWithAccessByLogin(String login) throws SQLException {
		Connection connection = DatabaseConnectionFactory.createConnection();
		
		try {
			return selectAllLoginWithAccessByLogin(login, connection);
		} finally {
			connection.close();
		}
	}
	
	public static List<LoginAccessQueryResult> selectAllLoginWithAccessByLogin(String login, Transaction transaction) throws SQLException {
		return selectAllLoginWithAccessByLogin(login, transaction.getConnection());
	}
	
	public static List<LoginAccessQueryResult> selectAllLoginWithAccessByLogin(String login, Connection connection) throws SQLException {
		
		List<LoginAccessQueryResult> result = new LinkedList<LoginAccessQueryResult>();
		
		String sql = ""
				+ "SELECT a.id, a.login, "
				+ "CASE WHEN al.accessing_login_id IS NULL "
				+ "  THEN 0 "
				+ "  ELSE 1 "
				+ "END AS has_access "
				+ "FROM login a "
				+ "LEFT OUTER JOIN "
				+ "(SELECT la.accessing_login_id AS accessing_login_id "
				+ "  FROM login_access la "
				+ "  JOIN login l ON (la.login_id = l.id) "
				+ "  WHERE l.login = ?) al "
				+ "ON (a.id = al.accessing_login_id) "
				+ "WHERE NOT a.login = ?"
				+ ";";
		
		PreparedStatement st = connection.prepareStatement(sql);
		
		st.setString(1, login);
		st.setString(2, login);
		
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			LoginAccessQueryResult r = new LoginAccessQueryResult();
			
			r.id = rs.getInt("id");
			r.login = rs.getString("login");
			r.hasAccess = rs.getBoolean("has_access");
			
			result.add(r);
		}
		
		return result;
	}
	
	
}
