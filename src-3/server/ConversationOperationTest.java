package server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import message.ConvOps;
import message.ErrorTypeException;
import message.SignInAndOut;
import userInfo.ChatHandler;

public class ConversationOperationTest {
		private static ChatServer server;
		private static Socket one;
		private static ChatHandler onehandler;
		private static Socket client2;
		private static ChatHandler handler2;
		private static Socket client3;
		private static ChatHandler handler3;
		
		@BeforeClass
		public  static void setUp() throws UnknownHostException, IOException, JsonSyntaxException, ErrorTypeException{
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
			
			one = new Socket("localhost", 10000);
			onehandler = new ChatHandler(one, server);

			 client2 = new Socket("localhost", 10000);
			 handler2 = new ChatHandler(client2, server);
			 
			 client3 = new Socket("localhost", 10000);
			 handler3 = new ChatHandler(client3, server);
		}
		
		/*
		 * reset the server after every test
		 */
		@After
		public void Reset() throws IOException{
			    server.getSocket().close();
				pause();
		}
		
		/*
		 * Make the server thread pause
		 */
		private void pause() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/*
		 * Test validation of the username
		 */
		@Test
		public void testNameValidation() throws UnknownHostException, IOException, JsonSyntaxException, ErrorTypeException{
			
			String username1 = "hello1234";
			Socket client = new Socket("localhost", 10000);
			ChatHandler handler = new ChatHandler(client, server);
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
		 * @throws ErrorTypeException 
		 */
		@Test
		public void addConnectionTest() throws UnknownHostException, IOException, ErrorTypeException{
			
			String username1 = "user1";
			SignInAndOut message = new SignInAndOut(username1, true);
			
			assertEquals(0,server.getUserNumber());
			/*
			 * Supposed that client sends a sign-in message
			 */
			onehandler.visit(message);
			
			assertEquals(1, server.getUserNumber());
		}
		
		/**
		 * test add another client that with same id that already exists
		 * @throws IOException 
		 * @throws UnknownHostException 
		 * @throws ErrorTypeException 
		 */
		@Test
		public void ConflictClientTest() throws UnknownHostException, IOException, ErrorTypeException{
			String username1 = "hello12345";
			SignInAndOut message = new SignInAndOut(username1, true);
			
			/*
			 *  user1 is in the server 
			 */
			assertEquals(1,server.getUserNumber());
			/*
			 * Supposed that client sends a sign-in message
			 */
			handler2.visit(message);
			
			assertEquals(2, server.getUserNumber());
			/*
			 *  hello123456 is already in the user list of server
			 */
			assertEquals(2, server.getUserNumber());
			String conflict = "hello12345";
			
			SignInAndOut m = new SignInAndOut(conflict, true);
			
			/*
			 *  hello123456 is already in the user list of server
			 */
			assertEquals(2,server.getUserNumber());
			/*
			 * Supposed that conflictclient sends a sign-in message
			 */
			handler3.visit(m);
			
			assertEquals(2, server.getUserNumber());
		}
		
		/**
		 * Test creating a conversation, joining, and leave the coversations
		 * @throws ErrorTypeException 
		 */
		@Test
		public void CreateAndJoinConv() throws ErrorTypeException{
			assertEquals(0, server.getConvsNumber());
			ConvOps create = new ConvOps("hello12345", true, "happiness");
			onehandler.visit(create);
			assertEquals(1, server.getConvsNumber());
			String convname = "happiness";
			assertEquals(1, server.getConvs().get(convname).getUserNum());
			
			pause();
			pause();
			
			ConvOps user2join = new ConvOps("user2", true, convname);
			handler2.visit(user2join);
			// user2 joined 
			
			ConvOps user3join = new ConvOps("user3", true, convname);
			// user3 joined 
			handler3.visit(user3join);
			
			assertEquals(3, server.getConvs().get(convname).getUserNum());
			

			pause();
			pause();
			
			
			ConvOps user3leave = new ConvOps("user3", false, convname);
			ConvOps user2leave = new ConvOps("user2", false, convname);
			// user 2 , user3 leaving the conversation happiness 
			handler2.visit(user2leave);
			handler3.visit(user3leave);
			
			assertEquals(1, server.getConvs().get(convname).getUserNum());
		}
		
}
