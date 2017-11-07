package cbn.webscreen.service.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import cbn.webscreen.message.ErrorResponse;

public class UnrecognizedPropertyExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException>{

	@Override
	public Response toResponse(UnrecognizedPropertyException e) {
		ErrorResponse errorResponse = new ErrorResponse();
		
		errorResponse.status = Status.BAD_REQUEST.getStatusCode();
		errorResponse.message = "invalid data supplied for request";
		
		return Response.status(Status.BAD_REQUEST)
				.entity(errorResponse)
				.build();
	}

}
