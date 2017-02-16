package com.yuktix.rest.queue.commands;

import org.apache.commons.lang3.StringUtils;

import com.yuktix.rest.queue.BeanstalkErrorCode;
import com.yuktix.rest.queue.BeanstalkException;
import com.yuktix.rest.queue.BeanstalkResponse;

public class BeanstalkUseCommand implements IBeanstalkCommand {

	private String tube ;
	
	public BeanstalkUseCommand(String tube) {
		this.tube = tube ;
	}
	
	public BeanstalkResponse process(String[] lines) throws BeanstalkException {
		
		BeanstalkResponse response = new BeanstalkResponse() ;
		// use only expects one line
		// remove chars <= 32
		String line1 = StringUtils.trim(lines[0]) ;
		String[] tokens = StringUtils.split(line1, DataConstants.SPACE) ;
		
		// find code
		int code = BeanstalkErrorCode.getCode(tokens[0]) ;
		if(code != BeanstalkErrorCode.USING) {
			throw new BeanstalkException(code, line1) ;
		}
		
		response.setCode(code);
		// found USING
		response.put("code", "" +code);
		response.put("tube", tube) ;
		response.put("response", lines[0]) ;
		return response;
		
	}

}
