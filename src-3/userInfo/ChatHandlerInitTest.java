package userInfo;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import message.ErrorTypeException;
import server.ChatServer;

public class ChatHandlerInitTest {
		
		private ServerSocket server;
		private Socket clientSide ;
		private Socket serverSide;
		private ChatServer chatServer;
		private ChatHandler handler;
		private BufferedReader clientIn;
		private PrintWriter clientOut;
		private BufferedReader handlerIn;
		private PrintWriter handlerOut;
		
		@Before
		public void setup() throws IOException{
			
			server = new ServerSocket(10000);
			
			chatServer = new ChatServer(server);
			clientSide = new Socket("localhost", 10000);
			serverSide = server.accept();
			
			
			clientIn = new BufferedReader(new InputStreamReader(clientSide.getInputStream()));
			clientOut = new PrintWriter(clientSide.getOutputStream(),true);
			handlerIn = new BufferedReader(new InputStreamReader(serverSide.getInputStream()));
			handlerOut = new PrintWriter(clientSide.getOutputStream());
			
		}
		
		/*
		 * Close the socket 
		 */
		@After
		public void kill() throws IOException{
			server.close();
			
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
		
		@Test
		public void testSetup()
		{
			assertFalse(serverSide == null);
			assertFalse(clientOut == null);
			assertFalse(clientIn == null);
			assertFalse(handlerOut == null);
			assertFalse(handlerIn == null);
		}
		
		/*
		 * Test ChatHandler init with single thread
		 */
		@Test
		public void convstructorTest() throws IOException, JsonSyntaxException, ErrorTypeException{
			String LogIn = "{\"username\":\"EricLu\",\"type\":\"SIGNIN\"}";
			clientOut.println(LogIn);
			clientOut.flush();
			
			handler = new ChatHandler(serverSide, chatServer);
			
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "Please enter your name");
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "Welcome EricLu your name is available.");
			assertTrue(chatServer.getUsers().containsKey("EricLu"));
		}
		
		
		/*
		 * Test initialize a ChatHandler within another thread
		 */
		@Test
		public void multiThreadTest() throws JsonSyntaxException, IOException, InterruptedException{
			
			// start a chatHandler in another thread 
			Thread test = new Thread(){
				@Override
				public void run() {
					try {
						handler = new  ChatHandler(serverSide, chatServer);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ErrorTypeException e) {
						e.printStackTrace();
					}
					
				}
			};
			
			test.start();
			pause();
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "Please enter your name");
			
			String LogIn = "{\"username\":\"EricChang\",\"type\":\"SIGNIN\"}";
			clientOut.println(LogIn);
			clientOut.flush();
			test.join();
			
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "Welcome EricChang your name is available.");
			assertTrue(chatServer.getUsers().containsKey("EricChang"));
		}
		
		
		/**
		 * Test if the ChatHandler can start working properly
		 * @throws ErrorTypeException 
		 * @throws IOException 
		 * @throws JsonSyntaxException 
		 */
		
		@Test
		public void TetsWorking() throws JsonSyntaxException, IOException, ErrorTypeException{
			String LogIn = "{\"username\":\"EricLu\",\"type\":\"SIGNIN\"}";
			clientOut.println(LogIn);
			clientOut.flush();
			handler = new ChatHandler(serverSide, chatServer);
			
			pause();
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "Please enter your name");
			

			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "Welcome EricLu your name is available.");
			
			Thread test = new Thread(handler);
			test.start();
			pause();
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "Receiving Message from Server..");
		}
		
		/*
		 * Test validation of the username
		 */
		@Test
		public void testNameValidation() throws UnknownHostException, IOException, JsonSyntaxException, ErrorTypeException{
		
			/*
			 * name that is less than 5 character
			 */
			String username3 = "hi";
			/*
			 * name exceeds upper limit
			 */
			String LogIn = "{\"username\":\"12hello1234123123sdfsa\",\"type\":\"SIGNIN\"}";
			clientOut.println(LogIn);
			clientOut.flush();
			handler = new ChatHandler(serverSide, chatServer);
			
			pause();
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "Please enter your name");
			
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "this name is not valid. Please enter a new one");
		}
}
