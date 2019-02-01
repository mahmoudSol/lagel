package com.lagel.com.rest;



public class RestException extends Exception
{
	private static final long serialVersionUID = 27L;
	private int statusCode;

	public RestException(String detailMessage, Throwable throwable)
	{
		super(detailMessage, throwable);
	}

	public RestException(String detailMessage, int statusCode)
	{
		super(detailMessage);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
