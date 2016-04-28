package message;

import com.google.gson.Gson;

/*
 * ConvOps represents that a user joins or leaves a conversation  
 * 
 * Rep invariant:
 * 		It's a immutable object
 * 		username, type, conversation name are all fianl 
 */

public class ConvOps implements ToServerMessage{
	private final String username;
	private final ToServer type;
	private final String conversation; 
	
	/**
	 * Constructor of ConvOps
	 * @param username , string of username
	 * @param JoinOrNot, whether a user is joining or leaving a conversation 
	 * @param ConvName, name of that conversation to be called
	 */
	public ConvOps(String username,Boolean JoinOrNot,String ConvName ) {
		this.username = username;
		if(JoinOrNot)
			this.type = ToServer.JOIN;
		else
			this.type = ToServer.LEAVE;
		this.conversation = ConvName;
	}
	
	/**
	 * @return name of conversation that the user is joining or leaving
	 */
	public String getConv(){
		return conversation;
	}
	
	/**
	 * @return string of username
	 */
	public String getUser(){
		return username;
	}
	
	@Override
	/**
	 * @return JSON string of ConvOps
	 */
	public String toJSONString() {
		return new Gson().toJson(this);
	}

	/**
	 * @return type of ConvOps either JOIN or LEAVE 
	 */
	@Override
	public ToServer getType() {
		return type;
	}

	/**
	 * allow visitor to visit this object
	 * @throws ErrorTypeException 
	 */
	@Override
	public <T> void accept(ServerMessageVisitor<T> v) throws ErrorTypeException {
		v.visit(this);
	}
	
}
