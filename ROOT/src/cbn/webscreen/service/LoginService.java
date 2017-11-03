package cbn.webscreen.service;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import cbn.webscreen.message.ErrorResponse;
import cbn.webscreen.message.LoginInfoResponse;
import cbn.webscreen.message.LoginRequest;
import cbn.webscreen.message.LoginResponse;
import cbn.webscreen.message.SuccessResponse;
import cbn.webscreen.security.Authentication;
import cbn.webscreen.security.AuthenticationException;
import cbn.webscreen.util.ResponseFactory;

@Path("/")
public class LoginService {
	
	private static Logger logger = Logger.getLogger(LoginService.class);
	
	@Context
	HttpServletRequest httpServletRequest;
 	
	@Path("/web/login/info")
	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response loginInfo(LoginRequest request) throws AuthenticationException, SQLException {
		boolean isLoggedIn = Authentication.authenticated(httpServletRequest).isLoggedIn();
		
		LoginInfoResponse loginInfoResponse = new LoginInfoResponse();
		loginInfoResponse.isLoggedIn = isLoggedIn; 
		
		if (isLoggedIn) {
			loginInfoResponse.login = Authentication.authenticated(httpServletRequest).getLogin();
		}

		return Response.ok(loginInfoResponse).build();
	}
	
	
	@Path("/web/login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response weblogin(LoginRequest request) throws AuthenticationException, SQLException {
		
		if (request.login == null || request.login.isEmpty()) {
			throw new AuthenticationException("missing credentials");
		}
		
		if (request.password == null || request.password.isEmpty()) {
			throw new AuthenticationException("missing credencials");
		}
		
		Authentication.authenticate(request.login, request.password).session(httpServletRequest);
		
		return ResponseFactory.success();
		
	}
	
	@Path("/app/login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON +";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
	public Response applogin(LoginRequest request) throws AuthenticationException, SQLException {

		if (request.login == null || request.login.isEmpty()) {
			throw new AuthenticationException("missing credentials");
		}
		
		if (request.password == null || request.password.isEmpty()) {
			throw new AuthenticationException("missing credencials");
		}
		
		LoginResponse response = new LoginResponse();
		
		response.token = Authentication.authenticate(request.login, request.password).issueToken();
		
		return Response.ok(response).build();
			
	}
	
}
