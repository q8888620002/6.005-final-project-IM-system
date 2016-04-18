package main;

import java.io.IOException;

import server.ChatServer;

public class Main {
	
	/**
     * Start to run a chat server with port number specified within 0 - 65535
	 * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
    		ChatServer chatServer;
    		try {
    			if(args.length == 1){
    				int port = Integer.parseInt(args[0]);
    				chatServer = new ChatServer(port);
    			}
    			
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.err.println("port number not specified.");
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				System.err.println(args[0] + " is out of range 0 - 65535.");
			} 
    }
}
