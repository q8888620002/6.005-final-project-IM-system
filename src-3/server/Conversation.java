package server;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import message.ToServerMessage;
import userInfo.ChatHandler;


/*
 * Model of the MVC, a mutable data-type that stores conversations between clients. 
 * Conversation will wait and take the queue from input handler. 
 * ;then, it will copy and put the correspond message queues on user's outputQueue. 
 * 
 * 	Rep invariant:
 * 		name of conversation is final
 */

public class Conversation extends Thread{
	private final HashMap<String, ChatHandler> users = new HashMap<String, ChatHandler>();	
	private final BlockingQueue<ToServerMessage> queue = new LinkedBlockingQueue<ToServerMessage>();
	private final String name;
	
	/**
	 * Constructor of the conversation 
	 * @param String, name of this conversation 
	 * @param BlockingQueue of this conversation shared among threads
	 */
	
	public Conversation(String Name){
		this.name = Name;
	}
	
	/**
	 * Run method for the conversation thread, the conversation will block if blocking queue is empty or full
	 */
	@Override
	public void run(){
		while(true){
			try {
				// take the queue from other thread and deal with it
				
				ToServerMessage message = queue.take();
				try {
					
					HandleQueue(message);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Handling the queue, turning it into action  such as addClient or removeClient
	 * @param message, ADT of the queue
	 */
	private void HandleQueue(ToServerMessage message){
		
	}
	
	/**
	 * Add a new client to listen to this conversation 
	 * This method is synchronized 
	 * @param user , the string representation of user
	 * @param ChatHandler of the client 
	 */
	public synchronized void addClient(String username, ChatHandler handler){
			users.put(username, handler);
	}
	
	/**
	 * remove a existing client who listens to this conversation 
	 * This method is synchronized 
	 * @param user , the string representation of user
	 * @param ChatHandler of the correspond user 
	 * 
	 */
	public synchronized void removeClient(String username, ChatHandler handler){
		users.remove(username, handler);
	}
	
	/**
	 * Called by input handler who update a queue onto BlockingQueue of this conversation 
	 * @param queue, a Message that contains action to be performed 
	 */
	public void updateMessage(ToServerMessage queue){
		
	}
	
	/**
	 * Get the current name of users who are in this room
	 * @return
	 */
	public synchronized HashMap<String, ChatHandler> getUsers(){
		return users;
	}
	
	/**
	 * Get the number of user in this conversation 
	 * This method is synchronized
	 * @return int , number of user who is in this converation 
	 */
	public synchronized int getUserNum(){
		return users.size();
	}
	
	/**
	 * Debugger method for conversation 
	 * @return String, name of this conversation 
	 */
	public String getRoomName(){
		return name;
	}
	
}
