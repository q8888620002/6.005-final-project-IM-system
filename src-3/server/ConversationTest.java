package server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.BeforeClass;
import org.junit.Test;

import message.ConvOps;
import message.ErrorTypeException;
import message.SignInAndOut;
import userInfo.ChatHandler;


public class ConversationTest {
		static ChatServer server;
		static Socket connection1;
		static ChatHandler handler1;
		static String username1;
		static Socket connection2;
		static ChatHandler handler2;
		static String username2;
	
			
		
		/*
		 * Set up the serve and make client connect to it
		 */
		@BeforeClass
		public static void setUp() throws UnknownHostException, IOException, ErrorTypeException{
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
			
			// make a user name eloworld connect to the server 
			
		    username1 = "eloworld";
		   
		    connection1 = new Socket("localhost", 10000);
		    handler1 = new ChatHandler(connection1, server);
			SignInAndOut signin1 = new SignInAndOut(username1, true);
			// user1 login
			handler1.visit(signin1);
		
			String conversation = "happiness";
			
			username2 = "helloworld";
			connection2 = new Socket("localhost", 10000);
			handler2 = new ChatHandler(connection2, server);
			SignInAndOut signin2 = new SignInAndOut(username2, true);
			// user2 login
			handler2.visit(signin2);
			
			
			assertEquals(2, server.getUserNumber());
			
			ConvOps create = new ConvOps(username1, true, conversation);
			assertEquals(0, server.getConvsNumber());

			// user1 create a conversation with name "happiness"
			
			handler1.visit(create);
			assertEquals(1, server.getConvsNumber());
			
			// user1 is in the "happiness" conversation now
			
			assertEquals(1, server.getConvs().get(conversation).getUserNum());
		}
		
		/*
		 * Test if the conversation can be joined by a new user properly  
		 */
		@Test
		public void ConversationConstruct() throws ErrorTypeException{
			String conversation = "happiness";
			ConvOps join = new ConvOps(username2, true, conversation);
			
			String conversations = "happinesss";
			ConvOps join2 = new ConvOps(username2, true, conversations);
			// user2 join a existing conversation "happiness"
			handler2.visit(join2);
			
			assertEquals(2, server.getConvs().size());
		}
			
}
