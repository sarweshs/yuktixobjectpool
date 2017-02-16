package com.yuktix.objectpool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.yuktix.objectpool.factory.BeanstalkSocket;

public class TCPClient {
	public static void main(String argv[]) throws Exception {
		String hostName = "localhost";
		int portNumber = 9999;
		List<ClientNioSocket> list = new ArrayList<>();
		list.add(new ClientNioSocket(hostName, portNumber,1));
		/*list.add(new ClientNioSocket(hostName, portNumber,2));
		list.add(new ClientNioSocket(hostName, portNumber,3));
		list.add(new ClientNioSocket(hostName, portNumber,4));
		list.add(new ClientNioSocket(hostName, portNumber,5));*/
		int x = 0;
		while(x<1)
		{
			//ClientNioSocket cs = list.get(ThreadLocalRandom.current().nextInt(0 , 4));
			ClientNioSocket cs = list.get(0);
			//System.out.println(cs.write("Hello from " + cs.getId()));
			System.out.println("Still Connected:" + cs.getSocket().isConnected());
			//BeanstalkQueue queue = new BeanstalkQueue(cs.getSocket());
			//queue.put("Go Fetch Data", 1, null);
			BeanstalkSocket bSocket = new BeanstalkSocket(cs.getSocket());
			bSocket.put("Go Fetch Data", 1, null);
			x++;
			Thread.currentThread().sleep(1000);
		}
		
		for(ClientNioSocket cs:list)
		{
			cs.close();
		}
		
	}
	
}
class ClientNioSocket{
	private Socket socket;
	int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	ClientNioSocket(String host, int port, int id) throws UnknownHostException, IOException
	{
		this.socket = new Socket(host,port);
		this.socket.setKeepAlive(true);
		//this.socket.setSoTimeout(50000);
		this.id = id;
	}
	
	void close() throws IOException
	{
		if(this.socket != null && !this.socket.isClosed())
		{
			this.socket.close();
		}
	}
	
	String write(String str) throws IOException
	{
		PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		StringBuffer sBuf = new StringBuffer();
		out.println(str);
		String fromServer;
		while ((fromServer = in.readLine()) != null) {
			sBuf.append(fromServer);
			if(fromServer.equals(str.toUpperCase()))
			{
				break;
			}
		}
		return sBuf.toString();
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
}
