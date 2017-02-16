package com.yuktix.rest.queue;

import java.util.HashMap;

public class BeanstalkErrorCode {

	private static HashMap<String, Integer> codemap = new HashMap<String, Integer> () ;
	
	public static final int OK = 200 ;
	public static final int USING = 201 ;
	public static final int INSERTED = 202 ;
	
	static {
		
		// success for client
		codemap.put("OK", new Integer(200)) ;
		// beanstalkd success codes: 200-300
		codemap.put("USING", new Integer(201)) ;
		codemap.put("INSERTED", new Integer(202)) ;
					
		// beanstalkd server OK but possible error for client
		codemap.put("BURIED", new Integer(301)) ;
								
		// beanstalkd operation errors: 400-500
		codemap.put("NO_RESPONSE", new Integer(401)) ;
		codemap.put("EXPECTED_CRLF", new Integer(402)) ;
		codemap.put("JOB_TOO_BIG", new Integer(403)) ;
		codemap.put("DRAINING", new Integer(404)) ;
					
		// beanstalkd __UNRECOVERABLE__ errors: 500-600
		// close the socket after such errors
		codemap.put("UNKNOWN_EXCEPTION", new Integer(500)) ;
		codemap.put("ERROR_KEY_EXCEPTION", new Integer(501)) ;
		codemap.put("SOCKET_TIMEOUT_EXCEPTION", new Integer(502)) ;
		codemap.put("SOCKET_EXCEPTION", new Integer(503)) ;
		codemap.put("IO_EXCEPTION", new Integer(504)) ;
		
	}
	
	public static int getCode(String key) {
		
		Integer code = codemap.get(key);
		if(code == null) {
			code = codemap.get("ERROR_KEY_EXCEPTION") ;
		}
		
		return code.intValue() ;
	}
}
