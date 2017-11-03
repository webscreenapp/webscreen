package cbn.webscreen.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ErrorResponse {

	public int status;
	
	public String message;
	
	@JsonInclude(Include.NON_NULL)
	public String errorCode;
	
	@JsonInclude(Include.NON_NULL)
	public String description;

	@JsonInclude(Include.NON_NULL)
	public String moreInfo;
	
}
