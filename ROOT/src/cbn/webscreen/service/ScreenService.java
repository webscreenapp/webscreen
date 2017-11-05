package cbn.webscreen.service;

import java.awt.Dimension;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import cbn.webscreen.data.inmemory.ScreenData;
import cbn.webscreen.data.inmemory.ScreenData.Screen;
import cbn.webscreen.data.inmemory.Updates;
import cbn.webscreen.data.persistance.entity.Login;
import cbn.webscreen.data.persistance.entity.LoginAccess;
import cbn.webscreen.data.persistance.entitymanager.LoginAccessEntityManager;
import cbn.webscreen.data.persistance.entitymanager.LoginEntityManager;
import cbn.webscreen.message.ScreenAddRequest;
import cbn.webscreen.message.ScreenAddResponse;
import cbn.webscreen.message.ScreenInfoRequest;
import cbn.webscreen.message.ScreenInfoResponse;
import cbn.webscreen.message.ScreenResponse;
import cbn.webscreen.message.ScreenUpdateRequest;
import cbn.webscreen.security.Authentication;
import cbn.webscreen.security.AuthenticationException;
import cbn.webscreen.util.ResponseFactory;
import cbn.webscreen.util.ScreenIdFactory;

@Path("/")
public class ScreenService {
	
	private static Logger logger = Logger.getLogger(ScreenService.class);
	
	@Context
	HttpServletRequest httpServletRequest;

	@POST
	@Path("/web/screen/list")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response screenList() throws AuthenticationException, SQLException {

		String login = Authentication.authenticated(httpServletRequest).getLogin();

		List<ScreenResponse> screens = new LinkedList<ScreenResponse>();
		
		for (String screenId : ScreenData.screenData.keySet()) {
			Screen screen = ScreenData.screenData.get(screenId);

			ScreenResponse screenInfo = new ScreenResponse();
			screenInfo.screenId = screenId;
			screenInfo.owner = screen.login;
			
			// check if own screen
			if (login.equals(screen.login)) {
				screenInfo.isMyScreen = true;
				screenInfo.hasAccess = true;
			} else {
				screenInfo.isMyScreen = false;
				// check if have access
				LoginAccess loginAccess = LoginAccessEntityManager.selectByLoginAndAccessLogin(screen.login, login);
				if (loginAccess != null) {
					screenInfo.hasAccess = true;
				} else {
					screenInfo.hasAccess = false;
				}
			}
			
			screens.add(screenInfo);
		}

		return Response.ok(screens).build();
		
	}
	
	@POST
	@Path("/web/screen/info")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response screenInfo(ScreenInfoRequest request) throws AuthenticationException, SQLException {
		
		String login = Authentication.authenticated(httpServletRequest).getLogin();
		
		if (request.screenId == null || request.screenId.isEmpty()) {
			return ResponseFactory.error("missing attribute screenId");
		}
		
		Screen screen = ScreenData.screenData.get(request.screenId);
		if (screen == null) {
			return ResponseFactory.error("no screen found with specified screenId");
		}

		// check if own screen
		if (!login.equals(screen.login)) {
			
			// check if have access
			LoginAccess loginAccess = LoginAccessEntityManager.selectByLoginAndAccessLogin(screen.login, login);
			if (loginAccess == null) {
				return ResponseFactory.noAccess("no access");
			}
		}
		
		ScreenInfoResponse response = new ScreenInfoResponse();
		response.screenWidth = screen.screenSize.width;
		response.screenHeight = screen.screenSize.height;
		response.segmentWidth = screen.segmentSize.width;
		response.segmentHeight = screen.segmentSize.height;
		
		return Response.ok(response).build();
	}

	@POST
	@Path("/app/screen/add")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response screenAdd(ScreenAddRequest request) throws AuthenticationException {
		
		String login = Authentication.authenticated(httpServletRequest).getLogin();
		
		String screenId = ScreenIdFactory.generate();

		ScreenData.Screen screen = new ScreenData.Screen();
		screen.login = login;
		screen.screenSize = new Dimension(request.screenWidth, request.screenHeight);
		screen.segmentSize = new Dimension(request.segmentWidth, request.segmentHeight);
		screen.aliveAt = System.currentTimeMillis();
		
		ScreenData.screenData.put(screenId, screen);

		Updates.addGlobalWebUpdate("screen.new");
		
		ScreenAddResponse response = new ScreenAddResponse();
		response.screenId = screenId;
		
		return Response.ok(response).build();
	}

	@POST
	@Path("/app/screen/update")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response screenUpdate(ScreenUpdateRequest request) throws AuthenticationException {
		
		String login = Authentication.authenticated(httpServletRequest).getLogin();

		ScreenData.Screen screen = ScreenData.screenData.get(request.screenId);
		
		if (screen == null) {
			return ResponseFactory.error("no screen found with specified screenId");
		}
		
		if (!login.equals(screen.login)) {
			return ResponseFactory.noAccess("request denied: different owner");
		}
		
		screen.screenSize = new Dimension(request.screenWidth, request.screenHeight);
		screen.segmentSize = new Dimension(request.segmentWidth, request.segmentHeight);

		Updates.addScreenWebUpdate(request.screenId, "screen.update");
		
		return ResponseFactory.success();
	}
	
}
