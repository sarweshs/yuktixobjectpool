package com.yuktix.rest.queue;

public class BeanstalkException extends Exception {

	private static final long serialVersionUID = 1L;
	private int code ;
	private String command ;
	
	public BeanstalkException(int code, String message) {
		super(message);
		this.setCode(code) ;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}
