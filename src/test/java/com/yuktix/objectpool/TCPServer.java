package com.yuktix.objectpool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	public static void main(String argv[]) throws Exception {
		
		System.out.println("Opening socket server");

		boolean listening = true;
        
        try (ServerSocket serverSocket = new ServerSocket(9999)) { 
            while (listening) {
	            new MutiClientThread(serverSocket.accept()).start();
	        }
	    } catch (IOException e) {
	    	e.printStackTrace();
            System.exit(-1);
        }
	}


}

class MutiClientThread extends Thread {
    private Socket socket = null;

    public MutiClientThread(Socket socket) {
        super("MutiClientThread");
        this.socket = socket;
    }
    
    public void run() {

        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            outputLine = "USING Response from server";
            out.println(outputLine);

            while (true) {
            	inputLine = in.readLine();
            	if(inputLine != null)
            	{
            		System.out.println("Client says:" + inputLine);
            		outputLine =inputLine.toUpperCase();
            		outputLine = "INSERTED " + outputLine + "\r\n";
            		out.println(outputLine);
            	}
            	continue;
                //out.println("done.");
               // break;
                /*if (outputLine.equals("BYE"))
                    break;*/
            }
           // socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
