package cbn.webscreen.data.inmemory;

import java.awt.Dimension;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ScreenData {
	
	public static Map<String, Screen> screenData = Collections.synchronizedMap(new HashMap<String, Screen>());
	
	public static class Screen {

		public String login = null;
		
		public long aliveAt = 0;
		
		public boolean paused = false;
		
		public Dimension screenSize = null;
		public Dimension segmentSize = null;
		
		@JsonIgnore
		public Map<Integer, byte[]> images = Collections.synchronizedMap(new HashMap<Integer, byte[]>());
		
		public Map<Integer, Integer> versions = Collections.synchronizedMap(new HashMap<Integer, Integer>());
	}
}
