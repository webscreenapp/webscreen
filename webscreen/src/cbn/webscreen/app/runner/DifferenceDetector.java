package cbn.webscreen.app.runner;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import cbn.webscreen.app.Data;

public class DifferenceDetector implements Runnable{
	
	private static volatile boolean running = false;
	private static volatile boolean run = false;
	
	public static synchronized void start() {
		stop();
		run = true;
		new Thread(new DifferenceDetector()).start();
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
		
		// wait for capture
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
		}
		
		Dimension screenSize = Data.screen.getScreenSize();

		ArrayList<Integer> pixels = new ArrayList<Integer>();
		for (int i = 0; i < screenSize.width * screenSize.height; i++) {
			pixels.add(i);
		}

		Collections.shuffle(pixels);

		BufferedImage checkingImage = copyImage(Data.screenImage);
		BufferedImage previousImage = copyImage(Data.screenImage);

		while (run && running) {
			
			boolean[] uv = new boolean[Data.screen.getNumOfSegments()];
			Arrays.fill(uv, false);
			
			int c = 0;
			int pixelCount = pixels.size();
			
			for (Integer pixel : pixels) {

				c++;
				
				int index = Data.screen.getSegmentIndexByPixel(pixel);

				int x = pixel % screenSize.width;
				int y = pixel / screenSize.width;

				try {
					if (checkingImage.getRGB(x, y) != previousImage.getRGB(x, y)) {
						uv[index] = true;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
				}

				if (0 == c % (pixelCount / 10)) { // every 10%
					for (int i = 0; i < uv.length; i++) {
						
						Integer value = Data.currentVersions.get(i);
						if (value == null) {
							Data.currentVersions.put(i, 0);
						} else {
							if(uv[i]) {
								Data.currentVersions.put(i, (value+1));
							}
						}
					}
				}
			}
			
			for (int i = 0; i < uv.length; i++) {
				
				Integer value = Data.currentVersions.get(i);
				if (value == null) {
					Data.currentVersions.put(i, 0);
				} else {
					if(uv[i]) {
						Data.currentVersions.put(i, (value+1));
					}
				}
			}
			
			previousImage = copyImage(checkingImage);
			checkingImage = copyImage(Data.screenImage);
		}
		
		running = false;
	}
	
	private static BufferedImage copyImage(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

}
