package cbn.webscreen.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class LoginInfoResponse {

	public boolean isLoggedIn;
	
	@JsonInclude(Include.NON_EMPTY)
	public String login;
	
}
