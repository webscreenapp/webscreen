package cbn.webscreen.service.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParseException;

import cbn.webscreen.message.ErrorResponse;

@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {

	@Override
	public Response toResponse(JsonParseException e) {
		
		ErrorResponse errorResponse = new ErrorResponse();
		
		errorResponse.status = Status.BAD_REQUEST.getStatusCode();
		errorResponse.message = "invalid data supplied for request.";
		
		return Response.status(Status.BAD_REQUEST)
				.entity(errorResponse)
				.build();
	}
}