package cbn.webscreen.data.inmemory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class Updates {

	private static Logger logger = Logger.getLogger(Updates.class);
	
	public static class Update {
		
		public Long index;
		
		public String update;
		
		public Long timestamp;
		
		@Override
		public boolean equals(Object o) {
			return ((Update) o).index == index;
		}
		
	}
	
	private static long lastUpdate = 0L;
	
	public static List<Update> globalWebUpdates = Collections.synchronizedList(new LinkedList<Update>());
	public static Map<String, List<Update>> screenWebUpdates = Collections.synchronizedMap(new HashMap<String, List<Update>>());
	public static Map<String, List<Update>> loginWebUpdates = Collections.synchronizedMap(new HashMap<String, List<Update>>());
	public static Map<String, List<Update>> screenAppUpdates = Collections.synchronizedMap(new HashMap<String, List<Update>>());

	private static synchronized long getNewUpdateIndex() {
		return ++lastUpdate;
	}
	
	public static synchronized long getLastUpdate() {
		return lastUpdate;
	}
	
	public static Set<String> getWebUpdates(long lastUpdate, String login, String screenId) {
		
		Set<String> updates = new HashSet<String>();
		
		for (Update update : globalWebUpdates) {
			if (update.index > lastUpdate) {
				updates.add(update.update);
			}
		}
		
		if (login != null) {
			List<Update> loginWebUpdateList = loginWebUpdates.get(login);
			if (loginWebUpdateList != null) {
				for (Update update : loginWebUpdateList) {
					if (update.index > lastUpdate) {
						updates.add(update.update);
					}
				}
			}
		}
		
		if (screenId != null) {
			// TODO: check screen access
			List<Update> screenWebUpdateList = screenWebUpdates.get(screenId);
			if (screenWebUpdateList != null) {
				for (Update update : screenWebUpdateList) {
					if (update.index > lastUpdate) {
						updates.add(update.update);
					}
				}
			}
		}
		
		return updates;
	}
	
	public static Set<String> getAppUpdates(long lastUpdate, String screenId) {
		
		Set<String> updates = new HashSet<String>();
		
		if (screenId != null) {
			List<Update> screenAppUpdateList = screenAppUpdates.get(screenId);
			if (screenAppUpdateList != null) {
				for (Update update : screenAppUpdateList) {
					if (update.index > lastUpdate) {
						updates.add(update.update);
					}
				}
			}
		}
		
		return updates;
	}
	
	public static void addScreenAppUpdate(String screenId, String update) {
		Update upd = new Update();
		upd.index = getNewUpdateIndex();
		upd.update = update;
		upd.timestamp = System.currentTimeMillis();
		
		List<Update> updates = screenAppUpdates.get(screenId);
		if (updates == null) {
			updates = Collections.synchronizedList(new LinkedList<Update>());
			screenAppUpdates.put(screenId, updates);
		}
		updates.add(upd);
	}
	
	public static void addGlobalWebUpdate(String update) {
		Update upd = new Update();
		upd.index = getNewUpdateIndex();
		upd.update = update;
		upd.timestamp = System.currentTimeMillis();
		globalWebUpdates.add(upd);
	}
	
	public static void addScreenWebUpdate(String screenId, String update) {
		Update upd = new Update();
		upd.index = getNewUpdateIndex();
		upd.update = update;
		upd.timestamp = System.currentTimeMillis();
		
		List<Update> updates = screenWebUpdates.get(screenId);
		if (updates == null) {
			updates = Collections.synchronizedList(new LinkedList<Update>());
			screenWebUpdates.put(screenId, updates);
		}
		updates.add(upd);
	}
	
	public static void addLoginWebUpdate(String login, String update) {
		Update upd = new Update();
		upd.index = getNewUpdateIndex();
		upd.update = update;
		upd.timestamp = System.currentTimeMillis();
		
		List<Update> updates = loginWebUpdates.get(login);
		if (updates == null) {
			updates = Collections.synchronizedList(new LinkedList<Update>());
			loginWebUpdates.put(login, updates);
		}
		updates.add(upd);
	}

}
