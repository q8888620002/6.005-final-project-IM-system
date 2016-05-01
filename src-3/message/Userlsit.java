package message;

import java.util.ArrayList;

import com.google.gson.Gson;

/*
 * UserList represents the current information of user who is in the server.
 *  
 */
public class Userlsit implements ToClientMessage{
	private final ArrayList<String> users;
	private final ToClient type = ToClient.USERLIST;;
	
	/**
	 * Constructor of Userlist 
	 * @param list of string, names of current online users   
	 */
	public Userlsit(ArrayList<String> users) {
		this.users = users;
	}

	
	/**
	 * @return the json string representation of UserList
	 */
	@Override
	public String toJSONString() {
		String json = new Gson().toJson(this);
		return json;
	}

	/**
	 * @return the type of Userlist
	 */
	@Override
	public ToClient getType() {
		return type;
	}

}