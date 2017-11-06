package cbn.webscreen.service;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;


import cbn.webscreen.data.inmemory.ScreenData;
import cbn.webscreen.data.inmemory.ScreenData.Screen;
import cbn.webscreen.data.persistance.entity.Login;
import cbn.webscreen.data.persistance.entity.LoginAccess;
import cbn.webscreen.data.persistance.entitymanager.LoginAccessEntityManager;
import cbn.webscreen.data.persistance.entitymanager.LoginEntityManager;
import cbn.webscreen.message.ImageRequest;
import cbn.webscreen.message.ImageVersionRequest;
import cbn.webscreen.security.Authentication;
import cbn.webscreen.security.AuthenticationException;
import cbn.webscreen.util.ResponseFactory;
import cbn.webscreen.util.ScreenParameters;

@Path("/")
public class ImageService {

	Logger logger = Logger.getLogger(ImageService.class);
	
	@Context
	HttpServletRequest httpServletRequest;
	
	@Path("/web/image/{screenId}/{index}/{time}")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getImage(
			@PathParam("screenId") String screenId,	
			@PathParam("index")	int index,
			@PathParam("time") long time 
		) throws AuthenticationException, SQLException {
		
		String login = Authentication.authenticated(httpServletRequest).getLogin();
		
		if (screenId == null || screenId.isEmpty()) {
			return ResponseFactory.notFound();
		}
		
		ScreenData.Screen screen = ScreenData.screenData.get(screenId);
		if (screen == null) {
			return ResponseFactory.notFound();
		}
		
		// check if own screen
		if (!login.equals(screen.login)) {
			
			// check if have access
			LoginAccess loginAccess = LoginAccessEntityManager.selectByLoginAndAccessLogin(screen.login, login);
			if (loginAccess == null) {
				return ResponseFactory.noAccess("no access");
			}
		}
		
		byte[] bytes = screen.images.get(index);
		
		return Response.ok(bytes).build();
		
	}
	
	@Path("/app/image")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response putImage(
		@FormDataParam("image") ImageRequest request,
		@FormDataParam("bytes") byte[] bytes) {
		
		// TODO: login check
		
		if (bytes == null || bytes.length == 0) {
			return ResponseFactory.error("no data provided");
		}
		
		if (request.screenId == null || request.screenId.isEmpty()) {
			return ResponseFactory.error("missing attribute screenId");
		}

		ScreenData.Screen screen = ScreenData.screenData.get(request.screenId);
		if (screen == null) {
			return ResponseFactory.error("no screen found with specified screenId");
		}
		
		//TODO: check access
		
		screen.images.put(request.index, bytes);
		screen.versions.put(request.index, request.version);
		
		return ResponseFactory.success();
	}

	@Path("/web/image/version")
	@POST
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response imageVersion(ImageVersionRequest request) throws AuthenticationException, SQLException {
		
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

		List<Integer> versions = new LinkedList<Integer>();
		
		ScreenParameters screenParameters = new ScreenParameters(screen.screenSize, screen.segmentSize);
		for (int i = 0; i < screenParameters.getNumOfSegments(); i++) {
			Integer version = screen.versions.get(i);
			if (version != null) {
				versions.add(version);
			} else {
				versions.add(0);
			}
		}
		
		return Response.ok(versions).build();
	}
	
}