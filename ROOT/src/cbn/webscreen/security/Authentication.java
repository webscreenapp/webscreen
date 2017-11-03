package cbn.webscreen.security;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import cbn.webscreen.data.persistance.entity.Login;
import cbn.webscreen.data.persistance.entitymanager.LoginEntityManager;

public class Authentication {
	
	private static byte[] secret = new byte[32];
	
	static {
		new SecureRandom().nextBytes(secret);
	}
	
	private static Logger logger = Logger.getLogger(Authentication.class);
	
	public static class Verified {
		
		private String login = null;
		
		private Verified(String login) {
			this.login = login;
		}
		
		public void session(HttpServletRequest httpServletRequest) {
			httpServletRequest.getSession().setAttribute("login", login);
			logger.debug("authenticated session for login " + login);
		}
		
		public String issueToken() {
			logger.debug("issued token for login " + login);
			try {
				Algorithm algorithm = Algorithm.HMAC256(secret);
			    String token = JWT.create()
			    	.withSubject(login)
			    	.withIssuer("webscreen.server")
			    	.withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12 )) //expires in 12 hours
			        .sign(algorithm);
			    
			    return token;
			    
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
		
	}
		

	public static Verified authenticate(String login, String password) throws AuthenticationException, SQLException {
		
		Login loginEntity = LoginEntityManager.selectByLogin(login);
		
		if (loginEntity != null) {
			if (PasswordHash.checkPassword(password, loginEntity.passwordHash)) {
				return new Verified(loginEntity.login);
			}
		}		
		
		logger.debug("authentication failed for login " + login);
		throw new AuthenticationException("invalid login credentials");
		
	}
	
	
	public static class AuthenticationInfo {
		
		private String login = null;
		
		private boolean verifiedBySession = false;
		private boolean verifiedByToken = false;
		
		public AuthenticationInfo(HttpServletRequest httpServletRequest) {
			
			String sessionLogin = (String) httpServletRequest.getSession().getAttribute("login");
			logger.debug("verified by session for login " + sessionLogin);
			
			String tokenLogin = null;
			String authorizationHeader = httpServletRequest.getHeader("Authorization");
			if (authorizationHeader != null && authorizationHeader.matches("[B|b][E|e][A|a][R|r][E|e][R|r] .+")) {
				
				String token = authorizationHeader.substring("Bearer ".length()).trim();
			
				try {
					Algorithm algorithm = Algorithm.HMAC256(secret);
					JWTVerifier verifier = JWT.require(algorithm).build(); 
					tokenLogin = verifier.verify(token).getSubject();
				} catch (JWTVerificationException e) {
					tokenLogin = null;
				}
			}
			logger.debug("verified by token for login " + tokenLogin);
			
			if (sessionLogin != null & tokenLogin != null) {
				if (!sessionLogin.equals(tokenLogin)) {
					logger.error("verified by both session and token for two different logins. not allowed");
					return;
				}
			}

			if (sessionLogin != null) {
				login = sessionLogin;
			}
			
			if (tokenLogin != null) {
				login = tokenLogin;
			}

			logger.debug("succesfully verified for login " + this.login);
			
			verifiedBySession = sessionLogin != null;
			verifiedByToken = tokenLogin != null;
		}
		
		public boolean isLoggedIn() {
			return login != null;
		}
		
		public String getLogin() throws AuthenticationException {
			if (login == null) {
				throw new AuthenticationException("not logged in");
			}
			return login;
		}
		
		public boolean isVerifiedBySession() {
			return verifiedBySession;
		}
		
		public boolean isVerifiedByToken() {
			return verifiedByToken;
		}
		
	}
	
	public static AuthenticationInfo authenticated(HttpServletRequest httpServletRequest) {
		return new AuthenticationInfo(httpServletRequest);
	}

}
