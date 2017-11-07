package cbn.webscreen.service.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import cbn.webscreen.message.ErrorResponse;

public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {

	@Override
	public Response toResponse(InvalidFormatException e) {
		ErrorResponse errorResponse = new ErrorResponse();
		
		errorResponse.status = Status.BAD_REQUEST.getStatusCode();
		errorResponse.message = "invalid data format supplied for request";
		
		return Response.status(Status.BAD_REQUEST)
				.entity(errorResponse)
				.build();
	}

}
