package com.yuktix.rest.queue.commands;

import com.yuktix.rest.queue.BeanstalkException;
import com.yuktix.rest.queue.BeanstalkResponse;

public interface IBeanstalkCommand {

	BeanstalkResponse process(String[] lines) throws BeanstalkException;
}
