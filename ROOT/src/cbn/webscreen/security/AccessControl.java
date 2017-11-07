package cbn.webscreen.security;

import java.sql.SQLException;

import cbn.webscreen.data.persistence.entity.LoginAccess;
import cbn.webscreen.data.persistence.entitymanager.LoginAccessEntityManager;

public class AccessControl {

	public static boolean hasAccess(String login, String accessedLogin) throws SQLException {
		
		if (login.equals(accessedLogin)) {
			return true;
		}
			
		LoginAccess loginAccess = LoginAccessEntityManager.selectByLoginAndAccessLogin(accessedLogin, login);

		return loginAccess != null;
		
	}
}
