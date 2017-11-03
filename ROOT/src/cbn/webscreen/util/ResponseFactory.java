package cbn.webscreen.util;

import javax.ws.rs.core.Response;

import cbn.webscreen.message.ErrorResponse;
import cbn.webscreen.message.SuccessResponse;

public class ResponseFactory {
	
	public static Response success() {
		SuccessResponse successResponse = new SuccessResponse();
		successResponse.success = true;
		
		return Response.ok(successResponse).build();
	}

	public static Response error(String message) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.status = Response.Status.BAD_REQUEST.getStatusCode();
		errorResponse.message = message;
		
		return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
	}

	/**
	 * @param message
	 * @param errorCode - excluded if null
	 * @param description - excluded if null
	 * @param moreInfo - excluded if null
	 * @return
	 */
	public static Response error(String message, String errorCode, String description, String moreInfo) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.status = Response.Status.BAD_REQUEST.getStatusCode();
		errorResponse.message = message;
		errorResponse.errorCode = errorCode;
		errorResponse.description = description;
		errorResponse.moreInfo = moreInfo;
		
		return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
	}

	public static Response serverError(String message) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
		errorResponse.message = message;
		
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
	}
	
	public static Response notFound() {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.status = Response.Status.NOT_FOUND.getStatusCode();
		errorResponse.message = "not found";
		
		return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
	}
	
	public static Response noAccess(String message) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.status = Response.Status.UNAUTHORIZED.getStatusCode();
		errorResponse.message = message;
		
		return Response.status(Response.Status.UNAUTHORIZED).entity(errorResponse).build();
	}
}
