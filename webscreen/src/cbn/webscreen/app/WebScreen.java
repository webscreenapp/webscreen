package cbn.webscreen.app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response;

import cbn.webscreen.app.LoginWindow.LoginAction;
import cbn.webscreen.app.message.ErrorResponse;
import cbn.webscreen.app.message.LoginRequest;
import cbn.webscreen.app.message.LoginResponse;
import cbn.webscreen.app.message.ScreenAddRequest;
import cbn.webscreen.app.message.ScreenAddResponse;
import cbn.webscreen.app.message.ScreenUpdateRequest;
import cbn.webscreen.app.message.SuccessResponse;
import cbn.webscreen.app.runner.DifferenceDetector;
import cbn.webscreen.app.runner.ImageQueuer;
import cbn.webscreen.app.runner.ImageUploader;
import cbn.webscreen.app.runner.ScreenCapture;
import cbn.webscreen.app.runner.ServerUpdater;
import cbn.webscreen.app.screen.ShowCapture;

public class WebScreen {
	
	public static MainWindow mainWindow = new MainWindow();

	public static LoginWindow loginWindow = new LoginWindow();
	
	public static boolean running = false;

	public static boolean paused = false;
	
	public static void main(String[] args) {
		
		loginWindow.setVisible(true);
		
		loginWindow.addLoginListener(new LoginAction() {
			
			@Override
			public void login() {
				
				WebScreen.login();

			}
		});
		
		mainWindow.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				stop();
				super.windowClosed(e);
			}
		});
		
		mainWindow.setResizeListener(new MainWindow.ResizeListener() {
			
			@Override
			public void resizeStop() {
				Data.captureArea = mainWindow.getCaptureArea();
				Data.screen.setScreenSize(Data.captureArea.getSize());
				if (running) {
					DifferenceDetector.start();
					
					if (Data.screenId != null && !Data.screenId.isEmpty()) {
						updateScreen();
					}
				}
			}
			
			@Override
			public void resizeStart() {
				DifferenceDetector.stop();
			}
		});
		
		mainWindow.setMoveListener(new MainWindow.MoveListener() {
			
			@Override
			public void moved() {
				Data.captureArea = mainWindow.getCaptureArea();
				Data.screen.setScreenSize(Data.captureArea.getSize());
			}
		});
	}
	
	private static void login() {
		RestClient.setServerUrl(loginWindow.getServer());
		
		LoginRequest request = new LoginRequest();
		request.login = loginWindow.getLogin();
		request.password = loginWindow.getPassword();
		loginWindow.setStatusText("logging in ...");
		
		Future<Response> futureResponse = RestClient.login(request);
		
		Response response = null;
		
		try {
			
			response = futureResponse.get(30L, TimeUnit.SECONDS); // waiting for response ...
			
			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				LoginResponse responseEntity = response.readEntity(LoginResponse.class);
				
				RestClient.setToken(responseEntity.token);
				
				loginWindow.dispose();
				
				mainWindow.setVisible(true);
				
				createToggleListener();

			} else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
				ErrorResponse responseEntity = response.readEntity(ErrorResponse.class);
				loginWindow.setStatusText(responseEntity.message);
			} else {
				Response.Status status = Response.Status.fromStatusCode(response.getStatus());
				loginWindow.setStatusText("status " + status.getStatusCode() +  " - " + status.getReasonPhrase().toLowerCase());
			}
			
			
		} catch (InterruptedException e) {
			loginWindow.setStatusText("login failed");
		} catch (ExecutionException e) {
			e.printStackTrace();
			Throwable lastCause = e;
			while (lastCause.getCause() != null) {
				lastCause = lastCause.getCause();
			}
			if (lastCause instanceof UnknownHostException) {
				loginWindow.setStatusText("unknown host");
			} else {
				loginWindow.setStatusText(lastCause.getMessage().toLowerCase());
			}
			
		} catch (TimeoutException e) {
			loginWindow.setStatusText("login timeout");
		}
	}
	
	private static void createScreen() {
		ScreenAddRequest request = new ScreenAddRequest();
		
		request.screenWidth = Data.screen.getScreenSize().width;
		request.screenHeight = Data.screen.getScreenSize().height;
		request.segmentWidth = Data.screen.getSegmentSize().width;
		request.segmentHeight = Data.screen.getSegmentSize().height;
		
		Future<Response> futureResponse = RestClient.screenAdd(request);
		
		try {
			Response response = futureResponse.get(30L, TimeUnit.SECONDS); // waiting for response ...
			
			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				ScreenAddResponse responseEntity = response.readEntity(ScreenAddResponse.class);

				Data.screenId = responseEntity.screenId;
				
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
	private static void createToggleListener() {
		mainWindow.setToggleListener(new MainWindow.ToggleListener() {
			
			@Override
			public void toggle() {

				if (!running) {
					running = true;

					Data.captureArea = mainWindow.getCaptureArea();
					Data.screen.setScreenSize(Data.captureArea.getSize());

					createScreen();
					
					start();
					mainWindow.setToggleButtonText("pause");
				} else {
					if (!paused) {
						paused = true;
						ScreenCapture.stop();
						mainWindow.setToggleButtonText("resume");
						Data.updates.remove("screen.resume");
						Data.updates.add("screen.pause");
					} else {
						paused = false;
						ScreenCapture.start();
						mainWindow.setToggleButtonText("pause");
						Data.updates.remove("screen.pause");
						Data.updates.add("screen.resume");
					}
				}
				
			}
		});
		
	}

	private static void updateScreen() {
		ScreenUpdateRequest request = new ScreenUpdateRequest();
		
		request.screenId = Data.screenId;
		
		request.screenWidth = Data.screen.getScreenSize().width;
		request.screenHeight = Data.screen.getScreenSize().height;
		request.segmentWidth = Data.screen.getSegmentSize().width;
		request.segmentHeight = Data.screen.getSegmentSize().height;
		
		Future<Response> futureResponse = RestClient.screenUpdate(request);
		
		try {
			Response response = futureResponse.get(30L, TimeUnit.SECONDS); // waiting for response ...
			
			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
//				SuccessResponse responseEntity = response.readEntity(SuccessResponse.class);
				
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
	
	public static void start() {
		ScreenCapture.start();
		DifferenceDetector.start();
		ImageQueuer.start();
		ImageUploader.start();
		ServerUpdater.start();
	}
	
	public static void stop() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("stoping all");
				
				ScreenCapture.stop();
				DifferenceDetector.stop();
				ImageQueuer.stop();
				ImageUploader.stop();
				ServerUpdater.stop();
				
				System.out.println("stopped all");
				
				System.exit(0);
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try { Thread.sleep(1000); } catch (InterruptedException e) {}
				System.exit(0);
			}
		}).start();
	}
	
}
