package cbn.webscreen.service.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.databind.JsonMappingException;

import cbn.webscreen.message.ErrorResponse;

public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

	@Override
	public Response toResponse(JsonMappingException e) {
		
		ErrorResponse errorResponse = new ErrorResponse();
		
		errorResponse.status = Status.BAD_REQUEST.getStatusCode();
		errorResponse.message = "invalid json format supplied for request";
		
		return Response.status(Status.BAD_REQUEST)
				.entity(errorResponse)
				.build();
	}
}
