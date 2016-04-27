package server;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import userInfo.OutputHandler;

/*
 * Model of the MVC, a mutable data-type that stores conversations between clients. 
 * Conversation will wait and take the queue from input handler. 
 * ;then, it will copy and put the correspond message queues on user's outputQueue. 
 * 
 * 	Rep invariant:
 * 		name of conversation is final
 */

public class Conversation extends Thread{
	private HashMap<String, OutputHandler> users = new HashMap<String, OutputHandler>();	
	private BlockingQueue<Message> queue;
	private final String name;
	
	/**
	 * Create a conversation 
	 */
	
	public Conversation(String Name){
		this.name = Name;
		
	}
	
	/**
	 * Run method for the conversation thread, wait and take the blockigng queue
	 */
	private void Run(){
		
	}
	
	/**
	 * Handling the queue, turning it into action  such as addClient or removeClient
	 * @param message, ADT of the queue
	 */
	private void HandleQueue(Message message){
		
	}
	
	/**
	 * Add a new client to listen to this conversation 
	 * @param user , the string representation of user
	 */
	private void addClient(String username){
		
	}
	
	/**
	 * remove a existing client who listens to this conversation 
	 * @param user , the string representation of user
	 */
	private void removeClient(String username){
		
	}
	
	/**
	 * Called by input handler who update a queue onto BlockingQueue of this conversation 
	 * @param queue, a Message that contains action to be performed 
	 */
	public void updateMessage(Message queue){
		
	}
	
	/**
	 * Get the current name of users who are in this room
	 * @return
	 */
	private String getUsers(){
		return null;
	}
	
	
}
