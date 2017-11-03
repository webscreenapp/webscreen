package cbn.webscreen.app.message;

import java.util.HashSet;
import java.util.Set;

public class UpdateRequest {

	public Long lastUpdate;
	
	public String screenId;
	
	public Set<String> updates = new HashSet<String>();
	
}
