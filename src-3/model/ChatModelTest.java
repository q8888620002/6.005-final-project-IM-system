package model;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

import org.junit.Test;
import server.ChatServer;
import ui.ChatGUI;

public class ChatModelTest {
	
	// test if the model can send the login request  properly
	@Test
	public void loginAndOut() throws IOException, InterruptedException{
		ServerSocket server = new ServerSocket(10000);
		ChatServer chatServer =  new ChatServer(server);
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					chatServer.serve();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
		ChatModel chatModel = new ChatModel("localhost", 10000);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		chatModel.outputDebugger(bos);
		chatModel.start(new ChatGUI());
	
		chatModel.login("EricLu");
		
		
		Thread.sleep(50);
		// check the output to server
		
		// check if the client receive the updated userlist 
		assertEquals( 1,chatModel.getUserNumber());
		
		chatModel.logOut("EricLu");
		Thread.sleep(50);
		
		String expected = "{\"username\":\"EricLu\",\"type\":\"SIGNIN\"}"
				+ "{\"username\":\"EricLu\",\"type\":\"SIGNOUT\"}";
		assertEquals(bos.toString(), expected);
		
		assertEquals(0, chatServer.getUserNumber());
		server.close();
	}
	
	 // test if the user can create a chat room properly 
	@Test
	public void createRoom() throws IOException, InterruptedException {
		ServerSocket server = new ServerSocket(10000);
		ChatServer chatServer =  new ChatServer(server);
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					chatServer.serve();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
		ChatModel chatModel = new ChatModel("localhost", 10000);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		chatModel.outputDebugger(bos);
		chatModel.start(new ChatGUI());
		
		chatModel.login("EricLu");
		chatModel.createConv("league");
		
		String expected = "{\"username\":\"EricLu\",\"type\":\"SIGNIN\"}"
				+ "{\"username\":\"EricLu\",\"type\":\"JOIN\",\"conversation\":\"league\"}";
		assertEquals(bos.toString(), expected);
		Thread.sleep(50);
		assertTrue(chatServer.getConvs().containsKey("league"));
		
		server.close();
	}
	
	// test if user can join an existing chatroom 
	@Test 
	public void joinTest() throws IOException, InterruptedException{
		ServerSocket server = new ServerSocket(10000);
		ChatServer chatServer =  new ChatServer(server);
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					chatServer.serve();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
		ChatModel chatModel = new ChatModel("localhost", 10000);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		chatModel.outputDebugger(bos);
		chatModel.start(new ChatGUI());
		
		chatModel.login("EricLu");
		chatModel.createConv("league");
		
		String expected = "{\"username\":\"EricLu\",\"type\":\"SIGNIN\"}"
				+ "{\"username\":\"EricLu\",\"type\":\"JOIN\",\"conversation\":\"league\"}";
		assertEquals(bos.toString(), expected);
		Thread.sleep(50);
		assertTrue(chatServer.getConvs().containsKey("league"));
		server.close();
	}
	
	// test to see if the client can send message to client
	@Test
	public void leaveRoom() throws IOException, InterruptedException{
		ServerSocket server = new ServerSocket(10000);
		ChatServer chatServer =  new ChatServer(server);
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					chatServer.serve();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
		ChatModel chatModel = new ChatModel("localhost", 10000);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		chatModel.outputDebugger(bos);
		chatModel.start(new ChatGUI());
		
		chatModel.login("EricLu");
		chatModel.createConv("league");
		Thread.sleep(50);
		assertTrue(chatServer.getConvs().containsKey("league"));
		
		chatModel.LeaveConv("league");

		Thread.sleep(50);
		String expected = "{\"username\":\"EricLu\",\"type\":\"SIGNIN\"}"
				+ "{\"username\":\"EricLu\",\"type\":\"JOIN\",\"conversation\":\"league\"}"
				+"{\"username\":\"EricLu\",\"type\":\"LEAVE\",\"conversation\":\"league\"}";
		assertEquals(bos.toString(), expected);
		server.close();
	}
	
	// test to see if user can send the message to server
	@Test
	public void testMessage() throws IOException, InterruptedException{
		ServerSocket server = new ServerSocket(10000);
		ChatServer chatServer =  new ChatServer(server);
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					chatServer.serve();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
		ChatModel chatModel = new ChatModel("localhost", 10000);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		chatModel.outputDebugger(bos);
		chatModel.start(new ChatGUI());
		
		chatModel.login("EricLu");
		chatModel.createConv("league");
		chatModel.updateMessage("EricLu", "league", "Hello world!");
		Thread.sleep(50);
		
		String expected = "{\"username\":\"EricLu\",\"type\":\"SIGNIN\"}"
				+ "{\"username\":\"EricLu\",\"type\":\"JOIN\",\"conversation\":\"league\"}"
				+"{\"from\":\"EricLu\""
				+",\"type\":\"CHATTOSERVER\""
				+ ",\"conversation\":\"league\""
				+ ",\"content\":\"Hello world!\"}";
		assertEquals(bos.toString(), expected);
		server.close();
	}
}
