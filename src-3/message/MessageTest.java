package message;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import message.ToServerMessage.ToServer;

public class MessageTest {
	
	/*
	 * Convert a json string to a SignIn Message
	 */
	@Test
	public void SignInConvertion(){
		String Login = "{\"username\":\"Eric\",\"type\":\"SIGNIN\"}";
		GsonBuilder  gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Message.class, new MessageFactory());
		Gson gson = gsonBuilder.create();
		
		ToServerMessage signInAndOut = gson.fromJson(Login, Message.class);
		
		assertEquals(ToServer.SIGNIN, signInAndOut.getType());
		assertEquals("Eric", signInAndOut.getUser());
		assertEquals(Login, signInAndOut.toJSONString());
	}
	
	/*
	 * Convert a json string to a SignIn Message
	 */
	@Test
	public void SignOutConvertion(){
		String LogOut = "{\"username\":\"Eric\",\"type\":\"SIGNOUT\"}";
		GsonBuilder  gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Message.class, new MessageFactory());
		Gson gson = gsonBuilder.create();
		
		ToServerMessage signInAndOut = gson.fromJson(LogOut, Message.class);
		
		assertEquals(ToServer.SIGNOUT, signInAndOut.getType());
		assertEquals("Eric", signInAndOut.getUser());
		assertEquals(LogOut, signInAndOut.toJSONString());
	}
	
	
	/*
	 * Convert a json string to a join-type-ConvOps Message
	 */
	@Test
	public void JoinRoomTest(){
		String join = "{\"username\":\"Lauren\",\"type\":\"JOIN\",\"conversation\":\"happy\"}";
		GsonBuilder  gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Message.class, new MessageFactory());
		Gson gson = gsonBuilder.create();
		
		ToServerMessage convOps = gson.fromJson(join, Message.class);
		
		assertEquals(ToServer.JOIN, convOps.getType());
		assertEquals("Lauren", convOps.getUser());
		assertEquals(join, convOps.toJSONString());
	}
	
	/*
	 * Convert a json string to a join-type-ConvOps Message
	 */
	@Test
	public void LeaveRoomTest(){
		String leave = "{\"username\":\"Lauren\",\"type\":\"LEAVE\",\"conversation\":\"sadness\"}";
		GsonBuilder  gsonBuilder = new GsonBuilder();
		
		gsonBuilder.registerTypeAdapter(Message.class, new MessageFactory());
	
		ToServerMessage convOps = gsonBuilder.create().fromJson(leave, Message.class);
		
		assertEquals(ToServer.LEAVE, convOps.getType());
		assertEquals("Lauren", convOps.getUser());
		assertEquals(leave, convOps.toJSONString());
	}
	
	/*
	 * Convert a json string to a ChatMessage
	 */
	@Test
	public void ChatMessageTest(){
		String message = "{\"from\":\"Eric Lu\","
				+ "\"type\":\"CHAT\","
				+ "\"conversation\":\"piper\","
				+ "\"content\":\" hello world this is my first chat tak to you\"}";
		GsonBuilder  gsonBuilder = new GsonBuilder();
		
		gsonBuilder.registerTypeAdapter(Message.class, new MessageFactory());
	
		ToServerMessage chat = gsonBuilder.create().fromJson(message, Message.class);
		
		assertEquals(ToServer.CHAT, chat.getType());
		assertEquals("Eric Lu", chat.getUser());
		assertEquals(message, chat.toJSONString());
	}
	
}
