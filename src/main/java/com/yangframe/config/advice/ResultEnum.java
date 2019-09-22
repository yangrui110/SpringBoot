package com.yangframe.config.advice;

public enum ResultEnum {

	OK(200,"调用成功"),
	USER_NOT_LOGIN(305,"用户未登录"),
	IP_REJECTED(900,"IP禁止登陆"),
	ERROR(500,"系统内部错误");
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	private int code;
	private String msg;
	ResultEnum(int code, String msg){
		this.code=code;
		this.msg=msg;
	}
	
}
