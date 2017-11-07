package cbn.webscreen.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
import cbn.webscreen.data.inmemory.Updates;
import cbn.webscreen.data.inmemory.Updates.Update;
import cbn.webscreen.data.persistence.entity.Login;
import cbn.webscreen.data.persistence.entitymanager.LoginEntityManager;

@Path("/")
public class TestService {

	public static class Test {
		public int i;
	}
	
		private static Logger logger = Logger.getLogger(TestService.class);
		
		@Context
		HttpServletRequest httpServletRequest;
	 	
		@Path("/test2")
		@POST
		@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
		public Response test2(Test request){
			return Response.ok(request.i).build();
		}
		
		@Path("/test")
		@POST
		@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
		public Response test(String body) throws SQLException {
			
			logger.info("body:\n" + body);
			
			List<Login> loginList = LoginEntityManager.selectAll();
			
			Object o = new Object() {
				
				public String sessionId = httpServletRequest.getSession().getId();
				
				public String login = (String) httpServletRequest.getSession().getAttribute("login");
				
				public Object nestedObject = new Object() {
					public Object nestedObject2 = new Object() {
						public int[] test = {5,4,3,2,1};
					};
				};

				public  Map<String, List<Update>> screenWebUpdates = Updates.screenWebUpdates;
				
				public List<Login> logins = loginList;
				
				public Map<String, ScreenData.Screen> screens = ScreenData.screenData;
				
			};
			
			return Response.ok(o).build(); 
		}
	
}
