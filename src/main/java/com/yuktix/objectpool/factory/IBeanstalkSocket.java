package com.yuktix.objectpool.factory;

import java.io.IOException;

import com.yuktix.rest.queue.BeanstalkException;
import com.yuktix.rest.queue.BeanstalkResponse;
import com.yuktix.rest.queue.commands.IBeanstalkCommand;

public interface IBeanstalkSocket {
	
	public BeanstalkResponse put(String tube, int timeToRun, byte[] message) throws BeanstalkException;
	
	public BeanstalkResponse sendCommand(byte[] command, byte[] payload, IBeanstalkCommand callback)  throws BeanstalkException;
	
	public BeanstalkResponse processResponse(String response, IBeanstalkCommand callback) 
			throws BeanstalkException;
	
	public void close() throws IOException;
	
	public boolean isValid();
	
	public boolean isOpen();

}
