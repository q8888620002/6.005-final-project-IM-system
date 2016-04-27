package message;

/*
 * An interface that implemented by all server to client message and contains a method
 */
public interface ToClientMessage {
	
	/**
	 * @return the Message type of to client message ex. USERLIST , CHAT.
	 */
	public ToClient getType();
	
	/**
	 * Enum type of toClient Message.
	 */
	public enum ToClient{
		USERLIST,
		CHAT
	}
}
