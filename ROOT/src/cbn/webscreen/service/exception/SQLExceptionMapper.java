package cbn.webscreen.service.exception;

import java.sql.SQLException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.log4j.Logger;

import cbn.webscreen.message.ErrorResponse;

public class SQLExceptionMapper implements ExceptionMapper<SQLException> {

	private static Logger logger = Logger.getLogger(SQLExceptionMapper.class); 
	
	@Override
	public Response toResponse(SQLException e) {

		logger.error(e.getMessage(), e);
		
		ErrorResponse errorResponse = new ErrorResponse();
		
		errorResponse.status = Status.INTERNAL_SERVER_ERROR.getStatusCode();
		errorResponse.message = "database error";
		
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(errorResponse)
				.build();
	}
}
