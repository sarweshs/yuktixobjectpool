package com.yuktix.objectpool.factory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.yuktix.rest.queue.BeanstalkErrorCode;
import com.yuktix.rest.queue.BeanstalkException;
import com.yuktix.rest.queue.BeanstalkResponse;
import com.yuktix.rest.queue.commands.BeanstalkPutCommand;
import com.yuktix.rest.queue.commands.BeanstalkUseCommand;
import com.yuktix.rest.queue.commands.IBeanstalkCommand;

public class BeanstalkSocket implements IBeanstalkSocket{

	private static final Logger Log = Logger.getLogger(BeanstalkSocket.class.getName());
	private Socket socket;
	final byte[] CRLF = { '\r', '\n' };
	final char SPACE = ' ' ;
	private int socketCode ;
	
	public BeanstalkSocket(String host, int port) throws BeanstalkException {
		
		try { 
			
			this.socket = new Socket();
			this.socket.connect(new InetSocketAddress(host,port), 1000);
			this.socket.setSoTimeout(1000);
		} catch(Exception ex) {
			
			Log.error(ex);
			this.socket = null ;
			int code = this.mapExceptionCode(ex) ;
			this.socketCode  = code ;
			String xmsg = "unable to contact beanstalkd service" ;
			throw new BeanstalkException(code, xmsg);
		}
		
	}
	
public BeanstalkSocket(Socket socket) throws BeanstalkException {
		
		try { 
			
			this.socket = socket;						
		} catch(Exception ex) {
			
			Log.error(ex);
			this.socket = null ;
			int code = this.mapExceptionCode(ex) ;
			this.socketCode  = code ;
			String xmsg = "unable to contact beanstalkd service" ;
			throw new BeanstalkException(code, xmsg);
		}
		
	}
	
	public int getSocketCode() {
		return socketCode;
	}

	private int mapExceptionCode(Exception ex) {
		
		int code = 0 ;
		
		if (ex instanceof java.net.SocketTimeoutException) {
			// wrong configuration: check host + port
			code = BeanstalkErrorCode.getCode("SOCKET_TIMEOUT_EXCEPTION");
			return  code;
			
		}  else if (ex instanceof java.net.SocketException) {
			// beanstalkd not listening on host
			// wrong port, bind error
			code = BeanstalkErrorCode.getCode("SOCKET_EXCEPTION");
			return code;
		
		} else  if (ex instanceof java.io.IOException) {
			// unexpected I/O error
			code = BeanstalkErrorCode.getCode("IO_EXCEPTION");
			return code ;
			
		} else { 
			// unknown error
			code = BeanstalkErrorCode.getCode("UNKNOWN_EXCEPTION");
			return code ;
		}
		
	}

	// from within GFv4 : either use JCA adaper or directly
	// direct connection:  open socket to beanstalkd 
	// and close the socket after use
			
	public BeanstalkResponse put(String tube, int timeToRun, byte[] message) throws BeanstalkException {

		String command = String.format("use %s", tube);
		sendCommand(command.getBytes(), null, new BeanstalkUseCommand(tube));
		if(message == null)
		{
			command = String.format("put 1 1 %d %d", timeToRun, 0);
		}
		else
		{
			command = String.format("put 1 1 %d %d", timeToRun, message.length);
		}
		BeanstalkResponse br = sendCommand(command.getBytes(), message, new BeanstalkPutCommand());
		return br ;
		
	}

	public BeanstalkResponse sendCommand(byte[] command, byte[] payload, IBeanstalkCommand callback)  throws BeanstalkException {

		BeanstalkResponse br = null ;
		
		try { 
			
			InputStream is = null;
			OutputStream os = null;
			int ch = 0;
			int lastch = 0;
			String response = null;
			
			if(this.socket == null) {
				String xmsg = "No socket for beanstalk communication" ;
				throw new BeanstalkException(this.socketCode, xmsg);
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ByteArrayOutputStream bais = new ByteArrayOutputStream();
	
			// wait 1100 ms for socket to reply
			socket.setSoTimeout(1100);
	
			os = socket.getOutputStream();
			baos.write(command);
			baos.write(CRLF);
	
			if (payload != null) {
				baos.write(payload);
				baos.write(CRLF);
			}
	
			os = socket.getOutputStream();
			os.write(baos.toByteArray());
			os.flush();
			baos.close();
	
			is = socket.getInputStream();
	
			while ((ch = is.read()) != -1) {
	
				bais.write((byte) ch);
				if (lastch == '\r' && ch == '\n') {
					// time to leave!
					break;
				}
	
				lastch = ch;
	
			}
	
			// remove trailing CRLF from string
			response = new String(bais.toByteArray(), 0, (bais.size() - 2));
			bais.close();
			
			br = processResponse(response, callback) ;
			
		} catch(BeanstalkException ex1) {
			this.socketCode = ex1.getCode() ;
			throw ex1 ;
			
		} catch(Exception ex2) {
			
			Log.error(ex2);
			int code = this.mapExceptionCode(ex2) ;
			this.socketCode = code ;
			String xmsg = "unable to contact beanstalkd service" ;
			throw new BeanstalkException(code, xmsg);
		}
		
		return br;
		
	}
	
	public BeanstalkResponse processResponse(String response, IBeanstalkCommand callback) 
			throws BeanstalkException {
		
		BeanstalkResponse br = null ;
		String[] lines = StringUtils.split(response, "\r\n");
		
		if (lines == null || (lines.length == 0 )) {
			socketCode = BeanstalkErrorCode.getCode("NO_RESPONSE");
			throw new BeanstalkException(socketCode, "No response from beanstalkd server");
		}
		
		br = callback.process(lines) ;
		
		// @debug
		for (Map.Entry<String, String> entry : br.getMap().entrySet()) {
		    System.out.println(entry.getKey() + "/" + entry.getValue());
		}
		
		return  br;
		
	}
	
	public void close() throws IOException
	{
		this.socket.close();
	}
	
	public boolean isValid()
	{
		return this.socket.isConnected();
	}
	
	public boolean isOpen()
	{
		return !this.socket.isClosed();
	}
}
