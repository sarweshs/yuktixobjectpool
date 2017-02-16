package com.yuktix.objectpool.factory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;


public class BeanstalkSocketFactory extends BasePooledObjectFactory<BeanstalkSocket> {
    @Override
    public BeanstalkSocket create() throws Exception {
        return new BeanstalkSocket("localhost", 9999);
    }
    @Override
    public PooledObject<BeanstalkSocket> wrap(BeanstalkSocket parser) {
        return new DefaultPooledObject<BeanstalkSocket>(parser);
    }
    @Override
    public void passivateObject(PooledObject<BeanstalkSocket> parser) throws Exception {
        //parser.getObject().reset();
    }
    @Override
    public boolean validateObject(PooledObject<BeanstalkSocket> parser) {
        return  parser.getObject().isValid();
    }
	
}