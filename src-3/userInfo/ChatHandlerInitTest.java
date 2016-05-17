package userInfo;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import message.ErrorTypeException;
import message.SignInAndOut;
import message.Userlsit;
import server.ChatServer;

/*
 * Test for ChatHandler construction, connection, and multi-thread issue with stimulating server
 */
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
		 * Close the socket after every test execution 
		 */
		@After
		public void close() throws IOException{
			server.close();
			
		}
		
		/*
		 * Stop the current thread 
		 */
		private void pause() {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/*
		 * Setup method of login as EricLu before Test if needed 
		 */
		private void Signin() throws IOException{
			String LogIn = "{\"username\":\"EricLu\",\"type\":\"SIGNIN\"}";
			clientOut.println(LogIn);
			clientOut.flush();
			
		}
		
		/*
		 * Login status check 
		 */
		private void SigninCheck() throws IOException{

			assertTrue(clientIn.ready());
			assertEquals("{\"content\":\"Please enter your name\",\"type\":\"HINT\"}",clientIn.readLine());

			assertTrue(clientIn.ready());
			assertEquals("{\"content\":\"Welcome EricLu you are login now\",\"type\":\"HINT\"}",clientIn.readLine());

			assertTrue(clientIn.ready());
			assertEquals("{\"users\":[\"EricLu\"],\"type\":\"USERLIST\"}",clientIn.readLine());
			
			assertTrue(clientIn.ready());
			assertEquals("{\"content\":\"Receiving Message from Server..\",\"type\":\"HINT\"}",clientIn.readLine());
			
		
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

			assertEquals(0,chatServer.getUserNumber());
			
			handler = new ChatHandler(serverSide, chatServer);
			
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "{\"content\":\"Please enter your name\",\"type\":\"HINT\"}");
			
			
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "{\"content\":\"Welcome EricLu you are login now\",\"type\":\"HINT\"}");
			assertTrue(chatServer.getUsers().containsKey("EricLu"));
			// check server user number 
			assertEquals(1, chatServer.getUserNumber());
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
			assertEquals(clientIn.readLine(), "{\"content\":\"Please enter your name\",\"type\":\"HINT\"}");

			
			String LogIn = "{\"username\":\"EricChang\",\"type\":\"SIGNIN\"}";
			clientOut.println(LogIn);
			clientOut.flush();
			test.join();
			
			
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "{\"content\":\"Welcome EricChang you are login now\",\"type\":\"HINT\"}");
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
			assertEquals(clientIn.readLine(), "{\"content\":\"Please enter your name\",\"type\":\"HINT\"}");

			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "{\"content\":\"Welcome EricLu you are login now\",\"type\":\"HINT\"}");
			
			assertTrue(clientIn.ready());
			assertEquals("{\"users\":[\"EricLu\"],\"type\":\"USERLIST\"}",clientIn.readLine());
			
			
			Thread test = new Thread(handler);
			test.start();
			pause();
			
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "{\"content\":\"Receiving Message from Server..\",\"type\":\"HINT\"}");

		}
		
		/*
		 * tests if the message parser interperets disconnects properly
		 */
		@Test 
		public void TestDisconnect() throws JsonSyntaxException, IOException, ErrorTypeException{
			Signin();
			handler = new ChatHandler(serverSide, chatServer);
			Thread worker = new Thread(handler);
			worker.start();
			pause();
			assertEquals(1, chatServer.getUserNumber());
			
			SigninCheck();
			SignInAndOut logOut = new SignInAndOut("EricLu", false);
			clientOut.println(logOut.toJSONString());
			pause();
			assertEquals(0, chatServer.getUserNumber());
			
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "{\"content\":\"EricLu, see you next time.\",\"type\":\"HINT\"}");
		}
		
		/**
		 * CHatHandler will throw an exception and shut down if the client socket close
		 * @throws UnknownHostException
		 * @throws IOException
		 * @throws JsonSyntaxException
		 * @throws ErrorTypeException
		 */
		@Test
		public void TestDisconnectAtLogin() throws IOException{
			Thread disconnect = new Thread(){
					@Override 
					public void run() {
						try {
							try {
								handler = new ChatHandler(serverSide, chatServer);
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							} catch (ErrorTypeException e) {
								e.printStackTrace();
							}
						} catch (IOException e) {
							handler = null;
							
						}
					}
			};
			
			disconnect.start();
			pause();
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "{\"content\":\"Please enter your name\",\"type\":\"HINT\"}");

			clientSide.close();
			assertEquals(null, handler);
		}
		
		/*
		 * Meet unexpected socket closing after user log in 
		 */
		@Test
		public void TestDisconnectAfterLogin() throws IOException{
			Thread workingTread = new Thread(){
				@Override 
				public void run(){
						try {
							handler = new ChatHandler(serverSide, chatServer);
					
						} catch (JsonSyntaxException e) {
							
							e.printStackTrace();
						} catch (ErrorTypeException e) {
						
							e.printStackTrace();
					} catch (IOException e) {
						  handler = null;
					}	
				}
			};
			
			workingTread.start();
			pause();
			
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "{\"content\":\"Please enter your name\",\"type\":\"HINT\"}");
			pause();
			// login
			
			String LogIn = "{\"username\":\"EricLu\",\"type\":\"SIGNIN\"}";
			clientOut.println(LogIn);
			clientOut.flush();
			pause();
			
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "{\"content\":\"Welcome EricLu you are login now\",\"type\":\"HINT\"}");
			
			clientSide.close();
			
		}
		
		
		/*
		 * Test validation of the username
		 */
		@Test
		public void testNameValidation() throws UnknownHostException, IOException,
													JsonSyntaxException, ErrorTypeException{
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
			assertEquals(clientIn.readLine(), "{\"content\":\"Please enter your name\",\"type\":\"HINT\"}");
			
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "{\"error\":\"this name is not valid. Please enter a new one\",\"type\":\"ERROR\"}");
		}
		
		/*
		 * Test if a user login with another existing name
		 */
		@Test
		public void TestConflictName() throws IOException, JsonSyntaxException, ErrorTypeException{
			Signin();
			handler = new ChatHandler(serverSide,chatServer);
			assertEquals(1,chatServer.getUserNumber());
			Thread worker = new Thread(handler);
			worker.start();
			pause();

			SigninCheck();
			
			Signin();
			pause();
			
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "{\"content\":\"This name already been used\",\"type\":\"HINT\"}");
			assertEquals(1,chatServer.getUserNumber());
		}
		
		/*
		 * Test output thread and update queue function  
		 */
		@Test
		public void TestOutputThread() throws JsonSyntaxException, IOException, ErrorTypeException, InterruptedException {
			Signin();
			handler = new ChatHandler(serverSide,chatServer);
			
			Thread worker = new Thread(handler);
			worker.start();
			pause();
			SigninCheck();
			
			ArrayList<String> names = new ArrayList<String>();
			names.add("Eric");
			names.add("Julie");
			Userlsit list = new Userlsit(names);
			
			
			handler.updateQueue(list);
			
			pause();
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "{\"users\":[\"Eric\",\"Julie\"],\"type\":\"USERLIST\"}");

			assertFalse(clientIn.ready());
		}
		
		/*
		 * Test to see if the output Thread can handle updating queue from multiple threads
		 */
		@Test
		public void TestMultipleThreadUpdating() throws JsonSyntaxException, IOException, ErrorTypeException, InterruptedException {
			Signin();
			handler = new ChatHandler(serverSide,chatServer);
			
			Thread worker = new Thread(handler);
			worker.start();
			pause();
			SigninCheck();
			
			ArrayList<String> names = new ArrayList<String>();
			names.add("Eric");
			names.add("Julie");
			Userlsit list = new Userlsit(names);
			
			Thread[] threadList = new Thread[100];
			for(int i = 0; i < 100; i++)
			{
				threadList[i] = new Thread()
				{
					public void run()
					{
						for(int x = 0; x < 10; x++)
							try {
								handler.updateQueue(list);
							} catch (InterruptedException e) {
								
								e.printStackTrace();
							}
					}
				};
			}
			
			for(Thread t : threadList)
				t.start();
			for(Thread t : threadList)
				t.join();
			pause();
			
			for(int i = 0; i<1000; i++)
			{
				assertTrue(clientIn.ready());
				assertEquals(clientIn.readLine(), "{\"users\":[\"Eric\",\"Julie\"],\"type\":\"USERLIST\"}");
			}
			/*
			 * Make sure no extra queue being generated
			 */
			assertFalse(clientIn.ready());
		}
		
		/*
		 * parseSignMessageTest
		 */
		@Test
		public void parseSignMessageTest(){
			
		}
}
