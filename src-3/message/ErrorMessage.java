package message;

import com.google.gson.Gson;

/*
 * ErrorMessage represents the error 
 */
public class ErrorMessage implements ToClientMessage{
	private final String error;
	private final ToClient type;
	
	
	/**
	 * Constructor of the errorMessage
	 * @param Error ,a String object that represents the message
	 */
	public ErrorMessage(String error) {
		this.error = error;
		this.type = ToClient.ERROR;
	}
	
	/**
	 * @return json string representation of the Error message
	 */
	@Override
	public String toJSONString() {
		return  new Gson().toJson(this);
	}

	
	/**
	 * @return the error message type 
	 */
	@Override
	public ToClient getType() {
		return type;
	}

	@Override
	public <T> void accept(ClientMessageVisitor<T> v) throws ErrorTypeException {
		v.visit(this);
	}

}
