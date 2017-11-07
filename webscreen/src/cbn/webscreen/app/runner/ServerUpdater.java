package cbn.webscreen.app.runner;

import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response;

import cbn.webscreen.app.Data;
import cbn.webscreen.app.RestClient;
import cbn.webscreen.app.message.ErrorResponse;
import cbn.webscreen.app.message.UpdateRequest;
import cbn.webscreen.app.message.UpdateResponse;

public class ServerUpdater implements Runnable {

	private static volatile boolean running = false;
	private static volatile boolean run = false;

	public static synchronized void start() {
		stop();
		run = true;
		new Thread(new ServerUpdater()).start();
	}
	
	public static synchronized void stop() {
		run = false;
		while(running) {
			run = false;
			try { Thread.sleep(8); } catch (InterruptedException e) {}
		}
	}

	@Override
	public void run() {
		running = true;

		while (run && running) {
			
			sendUpdateRequest();
			
			try { Thread.sleep(500); } catch (InterruptedException e) {}
		}
		
		running = false;
	}
	
	public static void sendUpdateRequest() {
		
		UpdateRequest request = new UpdateRequest();
		
		request.lastUpdate = Data.lastUpdate;
		
		request.screenId = Data.screenId;
		
		request.updates = new HashSet<String>(Data.updates);
		
		Data.updates.clear();
		
		Future<Response> futureResponse = RestClient.update(request);
		
		try {
			Response response = futureResponse.get(30L, TimeUnit.SECONDS); // waiting for response ...
			
			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				
				UpdateResponse responseEntity = response.readEntity(UpdateResponse.class);
				
				Data.lastUpdate = responseEntity.lastUpdate;
				
				// handle updates from server
				
				
			} else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
				ErrorResponse responseEntity = response.readEntity(ErrorResponse.class);
				System.err.println(responseEntity.message);
				stop();
			} else {
				Response.Status status = Response.Status.fromStatusCode(response.getStatus());
				System.err.println("status " + status.getStatusCode() +  " - " + status.getReasonPhrase().toLowerCase());
				stop();
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			stop();
		} catch (ExecutionException e) {
			e.printStackTrace();
			stop();
		} catch (TimeoutException e) {
			e.printStackTrace();
			stop();
		}
	}

}
