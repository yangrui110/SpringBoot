package com.yangframe.config.advice;

import lombok.Data;

@Data
public class ResultEntity {

	private int code;
	private String msg;
	private int count;
	private Object data;
	public ResultEntity(ResultEnum enum1, Object data,int count) {
		this.code = enum1.getCode();
		this.msg = enum1.getMsg();
		this.data = data;
		this.count=count;
	}
	public ResultEntity(ResultEnum enum1,Object data) {
		this.code = enum1.getCode();
		this.msg = enum1.getMsg();
		this.data = data;
		this.count = 0;
	}
	
	
}
