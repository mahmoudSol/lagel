package com.lagel.com.rest;
import java.io.IOException;

public class ApiException extends Exception {
	private static final long serialVersionUID = 719227397L;
	private Error[] errors;
	
	public ApiException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ApiException(String detailMessage, Error[] errors) {
		super(detailMessage);
		this.errors = errors;
	}
	
	public Error[] getErrors() {
		return errors;
	}

	public boolean isNetworkDown() {
		boolean result = false;

		if (getCause() != null && getCause() instanceof RestException
				&& getCause().getCause() != null
				&& getCause().getCause() instanceof IOException) {

			result = true;
		}

		return result;
	}
}
