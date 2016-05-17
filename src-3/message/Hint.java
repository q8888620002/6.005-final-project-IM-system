package message;

import com.google.gson.Gson;

public class Hint implements ToClientMessage{

	private final String content;
	private final ToClient type;
	
	
	/**
	 * Constructor of the errorMessage
	 * @param Error ,a String object that represents the message
	 */
	public  Hint(String content) {
		this.content = content;
		this.type = ToClient.HINT;
	}
	
	/**
	 * @return json string representation of the hint message
	 */
	@Override
	public String toJSONString() {
		return  new Gson().toJson(this);
	}

	
	/**
	 * @return the hint message type 
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
