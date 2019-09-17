package com.yangframe.config.advice;

public class BaseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExceptionEntity getExceptionEntity() {
		return exceptionEntity;
	}

	
	public BaseException(ExceptionEntity exceptionEntity) {
		this.exceptionEntity = exceptionEntity;
	}


	public void setExceptionEntity(ExceptionEntity exceptionEntity) {
		this.exceptionEntity = exceptionEntity;
	}

	public BaseException(ResultEnum resultEnum) {
		exceptionEntity=new ExceptionEntity(resultEnum);
	}
	
	public BaseException(int code, String msg) {
		exceptionEntity=new ExceptionEntity(code, msg);
	}

	private ExceptionEntity exceptionEntity;
	
	
	
}
