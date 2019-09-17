package com.yangframe.config.advice;

import lombok.Data;

@Data
public class ExceptionEntity {

	private int code;
	private String msg;
	private String path;
	private String error;
	
	public ExceptionEntity(int code, String msg) {
		this.code = code;
		this.msg = msg;
		this.error=msg;
	}

	public ExceptionEntity(ResultEnum enum1) {
		this.code=enum1.getCode();
		this.msg=enum1.getMsg();
		this.error=enum1.getMsg();
	}
	
}
