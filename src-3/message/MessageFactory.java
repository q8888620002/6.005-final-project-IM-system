package message;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/*
 * It's a factory of converting input JSON string to correspond Message Object. 
 */
public class MessageFactory implements JsonDeserializer<Message>{

	@Override
	public Message deserialize(JsonElement json, Type typeOf, JsonDeserializationContext content)
			throws JsonParseException {
		
		final JsonObject jsonObject = json.getAsJsonObject();
		
		final JsonElement jsontype = jsonObject.get("type");
	    
		final String type = jsontype.getAsString();
	    
		System.err.println(type);
		
	    switch (type) {
	    case"SIGNIN":
			return new SignInAndOut(
					jsonObject.get("username").getAsString(),
					true
					);
	    case"SIGNOUT":
			return new SignInAndOut(
					jsonObject.get("username").getAsString(),
					false
					);
		case "CHATTOSERVER":
			return new ChatToServer(
					jsonObject.get("conversation").getAsString(), 
					jsonObject.get("content").getAsString(),
					jsonObject.get("from").getAsString());
		case"JOIN":
			return new ConvOps(
					jsonObject.get("username").getAsString(),
					true,
					jsonObject.get("conversation").getAsString()
					);
		case"LEAVE":
			return new ConvOps(
					jsonObject.get("username").getAsString(),
					false,
					jsonObject.get("conversation").getAsString()
					);
	// to client message	
		case "USERLIST":
			ArrayList<String> users = new Gson().fromJson(jsonObject.get("users")
					, new TypeToken<ArrayList<String>>(){}.getType());
			return new Userlsit(users);
		case "CHATTOCLIENT":
			return new ChatToClient(
					jsonObject.get("conversation").getAsString(), 
					jsonObject.get("from").getAsString(),
					jsonObject.get("content").getAsString());
		case"HINT":
			return new Hint(jsonObject.get("content").getAsString());
		case"ERROR":
			return new Hint(jsonObject.get("content").getAsString());
		default:
			/**
			 * return an error message if user sending an unformatted string object 
			 */
			return null;
		}
	}
		
}
