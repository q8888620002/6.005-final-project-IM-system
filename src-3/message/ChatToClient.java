package message;

import com.google.gson.Gson;

/**
 * ChatToClient represent the message that another user sends to other in conversation 
 * Rep invariant:
 *      From ,type ,and content are final and private
 *
 */
public class ChatToClient implements ToClientMessage{
	private final String From;
	private final String content;
	private final ToClient type;
	
	/**
	 * constructor of the ChatToClient
	 * @param from
	 * @param contnet
	 */
	public ChatToClient(String from, String content) {
		this.From = from;
		this.content = content;
		this.type = ToClient.CHAT;
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

}
