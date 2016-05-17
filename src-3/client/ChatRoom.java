package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
 * ChatRoom is an object that represents the chat room of Chat Board
 * It stores informations such as user in the chat and conversation history
 */
public class ChatRoom {
	private final String Chatname;
	private final HashMap<String, Integer> users;
	private StringBuilder convs;
	
	/**
	 * Constructor of the chat room 
	 * @param String name of the chat room
	 */
	public ChatRoom(String Name) {
		this.Chatname = Name;
		this.users = new HashMap<String, Integer>();
		this.convs = new StringBuilder();
	}
	
	
	/**
	 * Update chat message of chat room 
	 * @param from, speaker 
	 * @param content, message content
	 */
	public void updateChat(String from, String content){
		convs.append(from+" says : ");
		convs.append(content+"\n");
	}
	
	/**
	 * Update user status 
	 * @param string of user 
	 * @param status of the specified user 
	 */
	public void updateStatus(String user, Integer status){
		users.put(user, status);
	}
	
	/**
	 * Add a new users in the chat room 
	 * @param string of user name
	 * @param Integer , user status 
	 */
	public void addUser(String user, Integer status){
		users.put(user, status);
	}
	
	/**
	 * Delete a user from the chat room
	 * @param username
	 */
	public void deleteUser(String user){
		users.remove(user);
	}
	
	/**
	 * Getter of the ChatRoom name
	 * @return string representation of the name
	 */
	public String getName(){
		return Chatname;
	}
	
	/**
	 * Getter of the chat room message
	 * @return string of message history  
	 */
	public String getMessage(){
		return convs.toString();
	}
	
}
