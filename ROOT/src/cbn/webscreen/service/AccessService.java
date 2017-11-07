package cbn.webscreen.service;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cbn.webscreen.data.persistence.entity.LoginAccess;
import cbn.webscreen.data.persistence.entity.LoginAccessQueryResult;
import cbn.webscreen.data.persistence.entitymanager.CustomQuery;
import cbn.webscreen.data.persistence.entitymanager.LoginAccessEntityManager;
import cbn.webscreen.data.persistence.entitymanager.LoginEntityManager;
import cbn.webscreen.security.Authentication;
import cbn.webscreen.security.AuthenticationException;
import cbn.webscreen.util.ResponseFactory;

@Path("/")
public class AccessService {

	@Context
	private HttpServletRequest httpServletRequest;
	
	@Path("/web/access/list")
	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response accessList() throws AuthenticationException, SQLException {
		
		String login = Authentication.authenticated(httpServletRequest).getLogin();
		
		List<LoginAccessQueryResult> list = CustomQuery.selectAllLoginWithAccessByLogin(login);
		
		return Response.ok(list).build();
		
	}

	@Path("/web/access/set")
	@POST
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response accessSet(List<LoginAccessQueryResult> list) throws AuthenticationException, SQLException {
		
		String login = Authentication.authenticated(httpServletRequest).getLogin();
		
		for (LoginAccessQueryResult la : list) {
			LoginAccess loginAccess = LoginAccessEntityManager.selectByLoginAndAccessLogin(login, la.login);
			if (la.hasAccess) {
				if (loginAccess == null) {
					loginAccess = new LoginAccess();
					loginAccess.loginId = LoginEntityManager.selectByLogin(login).id;
					loginAccess.accessingLoginId = la.id;
					LoginAccessEntityManager.insert(loginAccess);
				}
			} else {
				if (loginAccess != null) {
					LoginAccessEntityManager.delete(loginAccess);
				}
			}
		}
		
		return ResponseFactory.success();
		
	}
	
}
