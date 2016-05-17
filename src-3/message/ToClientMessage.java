package message;

/*
 * An interface that implemented by all server to client message and contains a method
 */
public interface ToClientMessage extends Message{
	
	/**
	 * @return the Message type of to client message ex. USERLIST , CHAT.
	 */
	public ToClient getType();
	
	/**
	 * Enum type of toClient Message.
	 */
	public enum ToClient{
		USERLIST,
		CHATTOCLIENT,
		ERROR,
		HINT
	}
	
	  /**
     * accept method for visitor to visit 
     * @param <T>
     * @throws ErrorTypeException 
     */
    public <T> void accept(ClientMessageVisitor<T> v) throws ErrorTypeException;
}
