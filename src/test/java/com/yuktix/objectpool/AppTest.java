package com.yuktix.objectpool;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.yuktix.objectpool.factory.BeanstalkSocket;
import com.yuktix.objectpool.factory.BeanstalkSocketFactory;

import junit.framework.Assert;

public class AppTest {
	private BeanstalkSocketPool<BeanstalkSocket> pool;
	private AtomicInteger count = new AtomicInteger(0);

	@Before
	public void setUp() throws Exception {

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxIdle(2);
		config.setMaxTotal(4);
		/*---------------------------------------------------------------------+
		|TestOnBorrow=true --> To ensure that we get a valid object from pool  |
		|TestOnReturn=true --> To ensure that valid object is returned to pool |
		+---------------------------------------------------------------------*/
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);
		pool = new BeanstalkSocketPool<BeanstalkSocket>(new BeanstalkSocketFactory(), config);

	}

	@Test
	public void test() {
		try {
			int limit = 10;
			ExecutorService es = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
					new ArrayBlockingQueue<Runnable>(limit));
			for (int i = 0; i < limit; i++) {
				Runnable r = new Runnable() {
					@Override
					public void run() {
						BeanstalkSocket socket = null;
						try {
							socket = pool.borrowObject();
							count.getAndIncrement();
							socket.getSocketCode();
						} catch (Exception e) {
							e.printStackTrace(System.err);
						} finally {
							if (socket != null) {
								pool.returnObject(socket);
							}
						}
					}
				};
				es.submit(r);
			}
			es.shutdown();
			try {
				es.awaitTermination(1, TimeUnit.MINUTES);
			} catch (InterruptedException ignored) {
			}
			System.out.println("Pool Stats:\n Created:[" + pool.getCreatedCount() + "], Borrowed:["
					+ pool.getBorrowedCount() + "]");
			Assert.assertEquals(limit, count.get());
			Assert.assertEquals(count.get(), pool.getBorrowedCount());
			Assert.assertEquals(1, pool.getCreatedCount());
		} catch (Exception ex) {
			fail("Exception:" + ex);
		}
	}

	@After
	public void shutdownServer() {
		pool.close();
		//while(pool.)
	}
}
