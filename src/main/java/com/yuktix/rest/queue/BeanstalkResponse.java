package com.yuktix.rest.queue;

import java.util.HashMap;

public class BeanstalkResponse {
	
	private int code ;
	private HashMap<String,String> map ;
	
	public BeanstalkResponse () {
		this.code = BeanstalkErrorCode.OK ;
		this.map = new HashMap<String,String>() ;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String get(String key) {
		return map.get(key);
	}

	public void put(String key, String value) {
		this.map.put(key, value) ;
	}
	
	public HashMap<String,String> getMap() {
		return this.map ;
	}
	
}
