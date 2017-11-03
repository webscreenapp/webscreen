package cbn.webscreen.application;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import cbn.webscreen.service.exception.AuthenticationExceptionMapper;
import cbn.webscreen.service.exception.JsonParseExceptionMapper;
import cbn.webscreen.service.exception.SQLExceptionMapper;

@ApplicationPath("/api")
public class Application extends ResourceConfig {

	public Application() {
		packages("cbn.webscreen.service");
		packages("cbn.webscreen.service.exception");
		
		register(JacksonFeature.class);
		register(MultiPartFeature.class);
		
		register(JsonParseExceptionMapper.class);
		register(AuthenticationExceptionMapper.class);
		register(SQLExceptionMapper.class);
	}
	
}
