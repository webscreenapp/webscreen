package cbn.webscreen.service;

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
import cbn.webscreen.message.UpdateRequest;
import cbn.webscreen.message.UpdateResponse;
import cbn.webscreen.security.Authentication;
import cbn.webscreen.security.AuthenticationException;
import cbn.webscreen.util.ResponseFactory;

@Path("/")
public class UpdateService {
	
	private static Logger logger = Logger.getLogger(UpdateService.class);

	@Context
	HttpServletRequest httpServletRequest;
	
	@Path("/web/update")
	@POST
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response webUpdate(UpdateRequest request) throws AuthenticationException {
		
		String login = Authentication.authenticated(httpServletRequest).getLogin();
		
		UpdateResponse updateResponse = new UpdateResponse();
		updateResponse.lastUpdate = Updates.getLastUpdate();
		updateResponse.updates = Updates.getWebUpdates(request.lastUpdate, login, request.screenId);
		
		return Response.ok(updateResponse).build();
	}
	
	@Path("/app/update")
	@POST
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response appUpdate(UpdateRequest request) throws AuthenticationException {
		
		Authentication.authenticated(httpServletRequest).getLogin();
		
		if (request.screenId == null || request.screenId.isEmpty()) {
			return ResponseFactory.error("missing attribute screenId");
		}
		
		Screen screen = ScreenData.screenData.get(request.screenId);
		if (screen == null) {
			return ResponseFactory.error("no screen found with specified screenId");
		}
		
		for (String update : request.updates) {
			if ("screen.pause".equals(update)) {
				ScreenData.screenData.get(request.screenId).paused = true;
				Updates.addScreenWebUpdate(request.screenId, "screen.pause");
			}
			
			if ("screen.resume".equals(update)) {
				ScreenData.screenData.get(request.screenId).paused = false;
				Updates.addScreenWebUpdate(request.screenId, "screen.resume");
			}
			
			if ("screen.stop".equals(update)) {
				Updates.addScreenWebUpdate(request.screenId, "screen.stop");
			}
			
		}
		
		ScreenData.screenData.get(request.screenId).aliveAt = System.currentTimeMillis();
		
		UpdateResponse updateResponse = new UpdateResponse();
		updateResponse.lastUpdate = Updates.getLastUpdate();
		updateResponse.updates = Updates.getAppUpdates(request.lastUpdate, request.screenId);
		
		return Response.ok(updateResponse).build();
	}
	
}
