package cbn.webscreen.app.runner;

import java.util.Collections;
import java.util.LinkedList;

import cbn.webscreen.app.Data;

public class ImageQueuer implements Runnable {

	private static volatile boolean running = false;
	private static volatile boolean run = false;

	public static synchronized void start() {
		stop();
		run = true;
		new Thread(new ImageQueuer()).start();
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
		
		// wait for capture and detector
		try {
			Thread.sleep(250);
		} catch (InterruptedException e1) {
		}

		while (run && running) {
			
			LinkedList<Integer> indexesToQueue = new LinkedList<Integer>();
			
			for (Integer index : Data.currentVersions.keySet()) {
				Integer curVer = Data.currentVersions.get(index); 
				Integer uplVer = Data.uploadedVersions.get(index);
				
				if (!Data.inQueue.contains(index)) {
					if (uplVer == null) {
						indexesToQueue.add(index);
					} else if (curVer > uplVer) {
						indexesToQueue.add(index);
					}
				}
				
			}
			
			Collections.shuffle(indexesToQueue);
			
			for (Integer index : indexesToQueue) {
				Data.queue.add(index);
				Data.inQueue.add(index);
			}
			
			try { Thread.sleep(32); } catch (InterruptedException e) {}
		}
		
		running = false;
	}

}
