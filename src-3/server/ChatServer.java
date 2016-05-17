package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.sun.javafx.collections.MappingChange.Map;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import com.sun.swing.internal.plaf.synth.resources.synth;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import userInfo.ChatHandler;
import userInfo.UserInfo;

/**
 * A chat server that handles the client connection and stores conversation between users 
 * 
 * Rep invariant:
 * 		Server sokcet is not null
 * 		
 */
public class ChatServer {
	private final ServerSocket server;
	private final HashMap<String, UserInfo> users;
	private final HashMap<String, Conversation> allConvs;
	
	/**
	 * Constructor of the ChatServer that opening a serversocket connection and 
	 * catch an Exception if an I/O error occurred. 
	 * @param port number 
	 * @throws IOException while socket connection error occurred
	 * 
	 */
	public ChatServer(int port)throws IOException{
			this.server = new ServerSocket(port);
			this.users  = new HashMap<String,UserInfo>();
			this.allConvs = new HashMap<String,Conversation>();
			checkRep();
	}
	
	/**
	 * Constructor of ChatServer  
	 * @param socket, Server socket if provided
	 * @throws IOException
	 */
	public ChatServer(ServerSocket socket)throws IOException{
		this.server = socket;
		this.users  = new HashMap<String,UserInfo>();
		this.allConvs = new HashMap<String,Conversation>();
		checkRep();
}
	
	/**
	 * check the rep invariant of ChatServer
	 */
    private void checkRep() {
		assert server != null:" ChatServer error !!!!";
	}
    
    /**
     *  The server will block and wait for connection and accept socket connection from client
     *    ,finally passing the socket to a new thread.
     * @throws IOException 
     */
    public void serve() throws IOException{
    	System.err.println("server starts!");
    	while (true) {
    		try {
    			System.err.println("server is waiting");
    	    	
    			Socket clientSocket = server.accept();
    	    	// passing the client socket to a new clientHandler thread
    	    	try {
    	    		System.err.println("handling incoming connection");
    	    		new Thread(new ChatHandler(clientSocket, this)).start();
    	    		
    			} catch (Exception e) {
    				e.printStackTrace();		
    				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
    }
    
    /**
     *  Testing method for ChatServer, closing server socket
     */
    public ServerSocket getSocket() {
       
		return server;
    }
    
    /**
     * debugger method for onlineUser hashMap
     * @return a string representation of onlineUser
     */
    public synchronized HashMap<String, UserInfo> getUsers(){
    	return users;
    }
    
    /**
     * getter of the user name list 
     * @return a Arraylist of string of user name 
     */
    public synchronized ArrayList<String> getUserList(){
    	ArrayList<String> names = new ArrayList<String>();

    	for(String name: users.keySet()){
    		names.add(name);
    	}
    	return names;
    }
    
    
    /**
     * get the total number of online users
     * @return an Integer of user number
     */
    public synchronized int getUserNumber(){
    	return users.size();
    }
    
    /**
     * get the total number of accessible conversations 
     * @return an Integer of user number
     */
    public synchronized int getConvsNumber(){
    	return allConvs.size();
    }
    
    /**
     * debugger method of conversation hashmap in the erver
     * @return a string representation of AllConvs
     */ 
    public synchronized HashMap<String, Conversation> getConvs(){
    	return allConvs;
    }
    
}
