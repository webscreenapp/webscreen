package cbn.webscreen.service.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import cbn.webscreen.message.ErrorResponse;
import cbn.webscreen.security.AuthenticationException;

public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

	@Override
	public Response toResponse(AuthenticationException e) {

		ErrorResponse errorResponse = new ErrorResponse();
		
		errorResponse.status = Status.UNAUTHORIZED.getStatusCode();
		errorResponse.message = e.getMessage();
		
		return Response.status(Status.UNAUTHORIZED)
				.entity(errorResponse)
				.build();
	}
}
