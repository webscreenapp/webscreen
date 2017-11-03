package cbn.webscreen.app.runner;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

import cbn.webscreen.app.Data;
import cbn.webscreen.app.RestClient;
import cbn.webscreen.app.WebScreen;
import cbn.webscreen.app.message.ErrorResponse;
import cbn.webscreen.app.message.ImageRequest;
import cbn.webscreen.app.message.LoginResponse;
import cbn.webscreen.app.message.SuccessResponse;
import cbn.webscreen.app.screen.PngImageCodec;
import cbn.webscreen.app.screen.ShowCapture;

public class ImageUploader implements Runnable {
	
	public static final int MAX_CONCURRENT_UPLOADS = 3;

	private static Future<Response>[] futureResponses = new Future[MAX_CONCURRENT_UPLOADS];
	
	private static volatile boolean running = false;
	private static volatile boolean run = false;

	public static synchronized void start() {
		stop();
		run = true;
		new Thread(new ImageUploader()).start();
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
			
			if (!Data.queue.isEmpty()) {

				for (int i = 0; i < futureResponses.length; i++) {
					
					if (futureResponses[i] == null) {
						
						if (!Data.queue.isEmpty()) {
							
							int index = Data.queue.poll();
							Data.inQueue.remove(index);
							
							int version = Data.currentVersions.get(index);
							
							ImageRequest imageRequest = new ImageRequest();
							imageRequest.index = index;
							imageRequest.screenId = Data.screenId;
							imageRequest.version = version;
							
							Rectangle rct = Data.screen.getSegmentRectangle(index);
							
							BufferedImage img = Data.screenImage.getSubimage(rct.x, rct.y, rct.width, rct.height);
							
							futureResponses[i] = RestClient.image(imageRequest, PngImageCodec.encodeImage(img));
							
							Data.uploadedVersions.put(index, version);
						}
						
					} else if (futureResponses[i].isDone()) {
						
						try {
							Response response = futureResponses[i].get();
							if (response.getStatus() == Response.Status.OK.getStatusCode()) {
								//SuccessResponse responseEntity = response.readEntity(SuccessResponse.class);
								futureResponses[i] = null;
								
							} else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
								ErrorResponse responseEntity = response.readEntity(ErrorResponse.class);
								System.err.println(responseEntity.message);
								WebScreen.stop();
							} else if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
								ErrorResponse responseEntity = response.readEntity(ErrorResponse.class);
								System.err.println(responseEntity.message);
								WebScreen.stop();
							} else {
								Response.Status status = Response.Status.fromStatusCode(response.getStatus());
								System.err.println("status " + status.getStatusCode() +  " - " + status.getReasonPhrase().toLowerCase());
								WebScreen.stop();
							}
							
						} catch (InterruptedException e) {
							e.printStackTrace();
							WebScreen.stop();
						} catch (ExecutionException e) {
							e.printStackTrace();
							WebScreen.stop();
						}
						
					}
					
					
				}

			}
			
			try { Thread.sleep(32); } catch (InterruptedException e) {}
		}
		
		running = false;
	}
	
}
