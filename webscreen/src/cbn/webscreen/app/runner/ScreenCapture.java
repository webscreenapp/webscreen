package cbn.webscreen.app.runner;

import java.awt.AWTException;
import java.awt.Robot;

import cbn.webscreen.app.Data;

public class ScreenCapture implements Runnable{
	
	private static volatile boolean running = false;
	private static volatile boolean run = false;
	
	public static synchronized void start() {
		stop();
		run = true;
		new Thread(new ScreenCapture()).start();
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
		
		try {

			Robot robot = new Robot();
			
			while (run && running) {
				Data.screen.setScreenSize(Data.captureArea.getSize());
				Data.screenImage = robot.createScreenCapture(Data.captureArea);
				Thread.sleep(100);
			}

		} catch (InterruptedException ex) {
			
		} catch (AWTException e) {
			
		}
		
		running = false;
	}
}
