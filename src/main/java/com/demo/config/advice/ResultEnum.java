package com.demo.config.advice;

public enum ResultEnum {

	OK(200,"调用成功"),
	ID_NOT_EXIST(201,"Id不能为空"),
	GROUPID_EMPTY(202,"聊天组Id不能为空"),
	MEMBER_ID_EMPTY(203,"成员的Id不能为空"),
	NOT_EMPTY(204,"不能为空"),
	NOT_ENOUGH_CAR(205,"战斗车不足"),
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
