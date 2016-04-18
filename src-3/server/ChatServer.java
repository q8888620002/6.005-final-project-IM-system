package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import main.Client;

/**
 * A chat server that handles the client connection and stores conversation between users 
 * 
 * Rep invariant:
 * 		Server sokcet is not null
 * 		
 */
public class ChatServer {
	private  ServerSocket server;
	private ArrayList<Client> onlineUser;
	
	/**
	 * Constructor of the ChatServer that opening a serversocket connection and 
	 * catch an Exception if an I/O error occurred. 
	 * @param port number 
	 * @throws IOException while socket connection error occurred
	 * 
	 */
	public ChatServer(int port)throws IOException{
			this.server = new ServerSocket(port);
			checkRep();
	}
	
	/**
	 * check the rep invariant of ChatServer
	 */
    private void checkRep() {
		assert server != null:" ChatServer error !!!!";
	}
    
    /**
     *  Server start to run and accept socket connection from client
     *    , passing the socket to a new thread afterwards.
     * @throws IOException 
     */
    public void serve() throws IOException{
    	Socket clientSocket = server.accept();
    	
    	// passing the client socket to a new clientHandler thread
    	try {
			new ClientHandler(clientSocket).start();
    		
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    
}
