package userInfo;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.BeforeClass;
import org.junit.Test;


import message.SignInAndOut;
import server.ChatServer;

public class HandlerFuncTest {
		static ChatServer server;
	
		@BeforeClass
		public static void setUp(){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						server = new ChatServer(10000);
						server.serve();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	
	
		/*
		 * Test validation of the username
		 */
		@Test
		public void testNameValidation() throws UnknownHostException, IOException{
			
			String username1 = "hello1234";
			Socket client = new Socket("localhost", 10000);
			InputHandler handler = new InputHandler(client, server);
			assertTrue(handler.checkUserName(username1));
			/*
			 * User name exceeds upper limit
			 */
			String username2 = "12hello1234123123sdfsa";
			assertFalse(handler.checkUserName(username2));
			/*
			 * name that is less than 5 character
			 */
			String username3 = "hi";
			assertFalse(handler.checkUserName(username3));
			
		}
		
		/**
		 * test addUser function 
		 * @throws IOException 
		 * @throws UnknownHostException 
		 */
		@Test
		public void addClientTest() throws UnknownHostException, IOException{
			
			String username1 = "hello12345";
			Socket client = new Socket("localhost", 10000);
			InputHandler handler = new InputHandler(client, server);
			
			SignInAndOut message = new SignInAndOut(username1, true);
			
			assertEquals(0,server.getUserNumber());
			/*
			 * Supposed that client sends a sign-in message
			 */
			handler.visit(message);
			
			assertEquals(1, server.getUserNumber());
		}
		
}
