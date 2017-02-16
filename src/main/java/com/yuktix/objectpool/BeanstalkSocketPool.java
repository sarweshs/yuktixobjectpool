package com.yuktix.objectpool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class BeanstalkSocketPool<T> extends GenericObjectPool<T> {

	public BeanstalkSocketPool(PooledObjectFactory<T> factory, GenericObjectPoolConfig config) {
		super(factory, config);
		// TODO Auto-generated constructor stub
	}
	
	public BeanstalkSocketPool(PooledObjectFactory<T> factory) {
		super(factory);
		// TODO Auto-generated constructor stub
	}

}
