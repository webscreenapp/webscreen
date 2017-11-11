package cbn.webscreen.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import cbn.webscreen.app.screen.Screen;

public class Data {
	
	public static String screenId = null;
	
	public static Queue<Integer> queue = new ConcurrentLinkedQueue<Integer>();

	public static long lastUpdate = 0;
	public static Set<String> updates = Collections.synchronizedSet(new HashSet<String>());
	
	public static Rectangle captureArea = new Rectangle(0, 0, 256, 128);
	
	public static Screen screen = new Screen(captureArea.getSize(), new Dimension(256, 128));

	public static Map<Integer, Integer> currentVersions = Collections.synchronizedMap(new HashMap<Integer, Integer>());

	public static Map<Integer, Integer> uploadedVersions = Collections.synchronizedMap(new HashMap<Integer, Integer>());

	public static BufferedImage screenImage = null;
	
	static{
		Data.screenImage = new BufferedImage(screen.getWidth(), screen.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics gSi = Data.screenImage.getGraphics();
		gSi.setColor(Color.BLACK);
		gSi.fillRect(0, 0, screen.getWidth(), screen.getHeight());
	}
	
}
