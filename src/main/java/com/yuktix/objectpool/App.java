package com.yuktix.objectpool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.yuktix.objectpool.factory.BeanstalkSocket;
import com.yuktix.objectpool.factory.BeanstalkSocketFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	 GenericObjectPoolConfig config = new GenericObjectPoolConfig();
         config.setMaxIdle(2);
         config.setMaxTotal(4);
         /*
         ---------------------------------------------------------------------+
         |TestOnBorrow=true --> To ensure that we get a valid object from pool  |
         |TestOnReturn=true --> To ensure that valid object is returned to pool |
         +---------------------------------------------------------------------
         */
         config.setTestOnBorrow(true);
         config.setTestOnReturn(true);
         BeanstalkSocketPool<BeanstalkSocket> pool = new BeanstalkSocketPool<BeanstalkSocket>(new BeanstalkSocketFactory(), config);
         
         for(int i=0; i<5; i++)
         {
         	PoolTest pt = new PoolTest(pool);
         	Thread t1 = new Thread(pt,"Thread-" + i);
         	t1.start();
         	
         }
    }
    
}

class PoolTest implements Runnable{
	
	GenericObjectPool<BeanstalkSocket> pool;
	public PoolTest(GenericObjectPool<BeanstalkSocket> pool)
	{
		this.pool = pool;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		BeanstalkSocket connection;
		try {
			connection = (BeanstalkSocket) pool.borrowObject();
			System.out.println(connection);
			pool.returnObject(connection);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}


