package message;

import com.google.gson.Gson;

/*
 * ChatMessage represents a new message content in a conversation 
 * and sender name of new incoming message.
 * 
 * Rep invariant:
 * 		It's immutable
 * 		
 */
public class ChatToServer implements ToServerMessage{
	private final String from;
	private final ToServer type; 
	private final String conversation;
	private final String content;
	
	/**
	 * Create a new ChatMessage message
	 * @param conversation
	 * @param content
	 * @param From
	 */
	public ChatToServer(String conversation, String content, String From) {
		this.content = content; 
		this.conversation = conversation;
		this.from = From;
		this.type = ToServer.CHATTOSERVER;
	}
	
	
	/**
	 * @return the content of this message
	 */
	public String getContent(){
		return content;
	}
	
	/**
	 * @return the name of sender 
	 */
	@Override
	public String getUser(){
		return from;
	}
	
	/**
	 * @return the name of conversation
	 */
	public String getConv(){
		return conversation;
	}
	
	/**
	 * @return the json string of ChatMessage
	 */
	@Override
	public String toJSONString() {
		return new Gson().toJson(this);
	}

	/**
	 * @return the type of chatmessage
	 */
	@Override
	public ToServer getType() {
		return type;
	}

	
	/**
	 *  accept a visitor to handle this message 
	 * @throws ErrorTypeException 
	 */
	@Override
	public <T> void accept(ServerMessageVisitor<T> v) throws ErrorTypeException {
		v.visit(this);
	}


		
}
