package message;

import javax.swing.text.AbstractDocument.Content;

import org.junit.experimental.theories.FromDataPoints;

import com.google.gson.Gson;
import com.sun.org.apache.regexp.internal.recompile;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

import jdk.nashorn.internal.ir.annotations.Reference;

/**
 * ChatToClient represent the message that another user sends to other in conversation 
 * Rep invariant:
 *      From ,type ,and content are final and private
 *
 */
public class ChatToClient implements ToClientMessage{
	private final String from;
	private final String content;
	private final String conversation;
	private final ToClient type;
	
	/**
	 * constructor of the ChatToClient
	 * @param from
	 * @param contnet
	 */
	public ChatToClient(String conversation, String from, String content) {
		this.from = from;
		this.content = content;
		this.conversation = conversation;
		this.type = ToClient.CHATTOCLIENT;
	}
	
	/**
	 * @return the json strign representation of ChatToClient
	 */
	@Override
	public String toJSONString() {
		return new Gson().toJson(this);
	}

	/**
	 * @return the ToClient.CHAT
	 */
	@Override
	public ToClient getType() {
		return type;
	}
	
	/**
	 * @return name of the conversation 
	 */
	public String convName(){
		return conversation;
	}
	
	/**
	 * @return name of the speaker
	 */
	public String from(){
		return from;
	}
	
	/**
	 * @return the content of the message
	 */
	public String content(){
		return content;
	}

	@Override
	public <T> void accept(ClientMessageVisitor<T> v) throws ErrorTypeException {
		v.visit(this);
	}

}
