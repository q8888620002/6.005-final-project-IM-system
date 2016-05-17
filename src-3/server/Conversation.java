package server;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import message.ChatToClient;
import message.ChatToServer;
import message.Hint;
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
	private final BlockingQueue<ChatToServer> queue = new LinkedBlockingQueue<ChatToServer>();
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
				
				ChatToServer message = queue.take();
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
	 * Handling the queue, turning it into ChatToClient message
	 * @param message, ADT of the queue
	 */
	private void HandleQueue(ChatToServer message){
		ChatToClient chat = new ChatToClient(message.getConv(),message.getUser(), message.getContent());
		for(ChatHandler user: users.values()){
			try {
				user.updateQueue(chat);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Add a new client to listen to this conversation and notify users who is in the conversation 
	 * This method is synchronized 
	 * @param user , the string representation of user
	 * @param ChatHandler of the client 
	 */
	public synchronized void addClient(String username, ChatHandler handler){
		Hint notification = new Hint(username+" has joined the conversation.");
		for (ChatHandler user : users.values()){
			try {
				user.updateQueue(notification);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
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
		Hint notification = new Hint(username+" has left the conversation.");
		for (ChatHandler user : users.values()){
			try {
				user.updateQueue(notification);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	
	/**
	 * Called by Chat handler who update a queue onto BlockingQueue of the conversation 
	 * @param queue, a Message that contains action to be performed 
	 * @throws InterruptedException 
	 */
	public void updateMessage(ChatToServer message) throws InterruptedException{
		queue.put(message);
	}
	
	/**
	 *  Debugger method of the queue
	 * @return the chat message blocking queue
	 */
	public BlockingQueue<ChatToServer> getqueue(){
		return queue;
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
