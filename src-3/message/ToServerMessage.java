package message;
/*
 * An interface that implemented by all clients to server message and contains a method
 */
public interface ToServerMessage extends Message{
	
		/**
		 * @return the type of ToServer Message
		 */
	    public ToServer getType();
	    
	    /**
		 * @return the owner of ToServer Message
		 */
	    public String getUser();
	    
	    
	    /**
	     * accept method for visitor to visit 
	     * @param <T>
	     * @throws ErrorTypeException 
	     */
	    public <T> void accept(ServerMessageVisitor<T> v) throws ErrorTypeException;
	    /**
	     *  Enum type of ToServer Message
	     */
		public enum ToServer{
			CHAT,
			JOIN,
			LEAVE,
			SIGNIN,
			SIGNOUT
		}
}
