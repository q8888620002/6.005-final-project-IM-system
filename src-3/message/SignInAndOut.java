package message;

import com.google.gson.Gson;

/*
 *  SignInAndOut contains the user name and the login status of user.
 *   
 *  Rep invariant:
 *     username and type are final 
 *     It’s an immutable object
 */		
public class SignInAndOut implements ToServerMessage{
	
	private final String username;
	private final ToServer type;
	
	
	/**
	 * Constructor of SignInAndOut 
	 * 
	 * @param username, name of user string  
	 * @param type, Message type of SignInAndOut either SIGNIN or SIGNOUT
	 */
	public SignInAndOut(String username, Boolean login) {
		this.username = username;
		if(login){
			this.type = ToServer.SIGNIN;
		}else{
			this.type = ToServer.SIGNOUT;
		}
		
	}
	
	/**
	 * @return the type of SignInAndOut either SIGNIN or SIGNOUT
	 */
	@Override
	public ToServer getType() {
		return type;
	}
	
	/**
	 * @return the username of the SignInAndOut
	 */
	@Override
	public String getUser(){ 
		return username;
	}
	
	/**
	 * @return  json string of SignInAndOut
	 */
	@Override
	public String toJSONString() {
		String json = new Gson().toJson(this);
		return json;
	}

	/**
	 * Allow visitor to visit a signinandout object
	 */
	@Override
	public <T> void accept(ServerMessageVisitor<T> v) {
		v.visit(this);
	}


}
