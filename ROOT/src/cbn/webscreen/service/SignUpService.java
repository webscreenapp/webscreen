package cbn.webscreen.service;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import cbn.webscreen.data.persistence.entity.Login;
import cbn.webscreen.data.persistence.entitymanager.LoginEntityManager;
import cbn.webscreen.message.SignUpRequest;
import cbn.webscreen.security.PasswordHash;
import cbn.webscreen.util.ResponseFactory;

@Path("/web/signup")
public class SignUpService {
	
	private static Logger logger = Logger.getLogger(SignUpService.class);

	@POST
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response signUp(SignUpRequest request) throws SQLException {
		
		if (request.login == null || request.login.isEmpty()) {
			return ResponseFactory.error("missing login");
		}
		
		if (request.password == null || request.password.isEmpty()) {
			return ResponseFactory.error("misssing password");
		}
		
		if (!request.login.matches("^([a-zA-Z0-9]|_|-){3,}$")) {
			String desc = "login does not meet required criteria:\n"
					+ " - must be atleast three characrets long\n"
					+ " - must contain only characters a-z A-Z 0-9 _ -";
			return ResponseFactory.error("invalid login format", null, desc, null);
		}
		
		if (LoginEntityManager.selectByLowerCaseLogin(request.login.toLowerCase()) != null) {
			return ResponseFactory.error("login already exists");
		}
		
		Login login = new Login();
		
		login.login = request.login;
		login.passwordHash = PasswordHash.hashPassword(request.password);
		
		LoginEntityManager.insert(login);
		
		return ResponseFactory.success();
		
	}

}

