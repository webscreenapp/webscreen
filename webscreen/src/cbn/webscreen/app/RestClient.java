package cbn.webscreen.app;

import java.util.concurrent.Future;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import cbn.webscreen.app.message.ImageRequest;
import cbn.webscreen.app.message.LoginRequest;
import cbn.webscreen.app.message.ScreenAddRequest;
import cbn.webscreen.app.message.ScreenUpdateRequest;
import cbn.webscreen.app.message.UpdateRequest;

public class RestClient {

	private static String serverUrl = "";

	private static Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
	
	private static String token = "";
	
	public static void setServerUrl(String serverUrl) {
		RestClient.serverUrl = serverUrl;
	}
	
	public static void setToken(String token) {
		RestClient.token = token;
	}
	
	public static Future<Response> login(LoginRequest request) {
		return client
				.target(serverUrl + "/api/app/login")
				.request(MediaType.APPLICATION_JSON)
				.async()
				.post(Entity.entity(request, MediaType.APPLICATION_JSON + ";charset=utf-8"));
	}

	public static Future<Response> image(ImageRequest request, byte[] bytes) {
		
		MultiPart multiPart = new MultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);
		
		FormDataBodyPart imagePart = new FormDataBodyPart("image", request, MediaType.APPLICATION_JSON_TYPE);
		FormDataBodyPart bytesPart = new FormDataBodyPart("bytes", bytes, MediaType.APPLICATION_OCTET_STREAM_TYPE);
		
		multiPart.bodyPart(imagePart);
		multiPart.bodyPart(bytesPart);
		
		return client
				.target(serverUrl + "/api/app/image")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.async()
				.post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE));
	}

	public static Future<Response> screenAdd(ScreenAddRequest request) {
		return client
				.target(serverUrl + "/api/app/screen/add")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.async()
				.post(Entity.entity(request, MediaType.APPLICATION_JSON + ";charset=utf-8"));
	}
	
	public static Future<Response> screenUpdate(ScreenUpdateRequest request) {
		return client
				.target(serverUrl + "/api/app/screen/update")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.async()
				.post(Entity.entity(request, MediaType.APPLICATION_JSON + ";charset=utf-8"));
	}

	public static Future<Response> update(UpdateRequest request) {
		return client
				.target(serverUrl + "/api/app/update")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.async()
				.post(Entity.entity(request, MediaType.APPLICATION_JSON + ";charset=utf-8"));
	}
}
	
