package cbn.webscreen.security;

public class AuthenticationException extends Exception {

	private static final long serialVersionUID = -982115690344635023L;

	public AuthenticationException(String message) {
		super(message);
	}
	
	public AuthenticationException(Throwable cause) {
		super(cause);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}
