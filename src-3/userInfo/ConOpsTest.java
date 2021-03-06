package userInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import message.ChatToServer;
import message.ConvOps;
import message.ErrorTypeException;
import message.Hint;
import message.SignInAndOut;
import server.ChatServer;

public class ConOpsTest {
	private ServerSocket server;
	private Socket clientSide ;
	private Socket serverSide;
	private ChatServer chatServer;
	private ChatHandler handler;
	private Thread loginThread;
	private BufferedReader clientIn;
	private PrintWriter clientOut;
	private BufferedReader handlerIn;
	private PrintWriter handlerOut;
	
	private Socket clientSide2;
	private Socket serverSide2;
	private BufferedReader clientIn2 ;
	private PrintWriter clientOut2;
	private ChatHandler handler2;
	private Thread loginThread2;
	
	@Before
	public void setup() throws IOException, JsonSyntaxException, ErrorTypeException{
		
		server = new ServerSocket(10000);
		
		chatServer = new ChatServer(server);
		clientSide = new Socket("localhost", 10000);
		serverSide = server.accept();
		
		clientIn = new BufferedReader(new InputStreamReader(clientSide.getInputStream()));
		clientOut = new PrintWriter(clientSide.getOutputStream(),true);
		handlerIn = new BufferedReader(new InputStreamReader(serverSide.getInputStream()));
		handlerOut = new PrintWriter(clientSide.getOutputStream());
		
		Signin();
		pause();
		SigninCheck(clientIn,"EricLu");
		
	}
	
	/*
	 * Check the thread state and close socket afterwards
	 */
	@After
	public void CheckAndClose() throws IOException, InterruptedException{
		
		assertTrue(loginThread.isAlive());
		assertTrue(chatServer.getUsers().containsKey("EricLu"));
		assertFalse(serverSide.isClosed());
		Hint ckeck = new Hint("message before closing");
		handler.updateQueue(ckeck);
		pause();
		assertTrue(clientIn.ready());
		assertEquals("{\"content\":\"message before closing\",\"type\":\"HINT\"}", clientIn.readLine());
		
		/*
		 * close the socket 
		 */
		server.close();
		
	}
	
	/*
	 * Stop the current thread 
	 */
	private void pause() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Setup method of login as EricLu before Test if needed 
	 */
	private void Signin() throws IOException, JsonSyntaxException, ErrorTypeException{
		String LogIn = "{\"username\":\"EricLu\",\"type\":\"SIGNIN\"}";
		
		clientOut.println(LogIn);
		clientOut.flush();
		handler = new ChatHandler(serverSide, chatServer);
		
		loginThread  = new Thread(handler);
		loginThread.start();
		
	}
	
	/*
	 * Another User Harvey signin 
	 */
	private void SignInAnOtherUser() throws UnknownHostException, IOException, JsonSyntaxException, ErrorTypeException{
		 clientSide2 = new Socket("localhost", 10000);
		 serverSide2 = server.accept();
		 clientIn2 = new BufferedReader(new InputStreamReader(clientSide2.getInputStream()));
		 clientOut2 = new PrintWriter(clientSide2.getOutputStream(),true);
		
		 String LogIn = "{\"username\":\"Harvey\",\"type\":\"SIGNIN\"}";
			
		clientOut2.println(LogIn);
		clientOut2.flush();
		handler2 = new ChatHandler(serverSide2, chatServer);
				
		loginThread2  = new Thread(handler2);
		loginThread2.start();
		 
		pause();
		SigninCheck(clientIn2, "Harvey");
	}
	
	/*
	 * Login status check 
	 */
	private void SigninCheck(BufferedReader clientIn,String name) throws IOException{

		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "{\"content\":\"Please enter your name\",\"type\":\"HINT\"}");

		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "{\"content\":\"Welcome "+name+" you are login now\",\"type\":\"HINT\"}");

		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "{\"content\":\"Receiving Message from Server..\",\"type\":\"HINT\"}");
		
	
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
	
	/**
	 * Test to see if a ChatHandler can create a conversation properly 
	 * @throws IOException 
	 * @throws ErrorTypeException 
	 * @throws JsonSyntaxException 
	 */
	@Test
	public void CreateConv() throws IOException, JsonSyntaxException, ErrorTypeException{
	
		assertEquals(0, chatServer.getConvsNumber());
		String conv = "testRoom1";
		ConvOps createRoom = new ConvOps("EricLu", true, conv);
		
		clientOut.println(createRoom.toJSONString());
		clientOut.flush();
		pause();
		assertTrue(chatServer.getConvs().containsKey(conv));
		assertEquals(1, chatServer.getConvs().get(conv).getUserNum());
		
		// create another room  testRoom
		
		String conv2 = "testRoom2";
		ConvOps create2 = new ConvOps("EricLu", true, conv2);
		clientOut.println(create2.toJSONString());
		clientOut.flush();
		pause();
		assertTrue(chatServer.getConvs().containsKey(conv2));
		assertEquals(1, chatServer.getConvs().get(conv2).getUserNum());
		assertTrue(clientIn.ready());
		assertEquals("{\"content\":\"you have created testRoom1\",\"type\":\"HINT\"}", clientIn.readLine());
		assertTrue(clientIn.ready());
		assertEquals("{\"content\":\"you have created testRoom2\",\"type\":\"HINT\"}", clientIn.readLine());

		
	}
	
	/**
	 * Test if the request fails when the user wants to create an existing conversation
	 * @throws ErrorTypeException 
	 * @throws IOException 
	 * @throws JsonSyntaxException 
	 */
	@Test
	public void CreateAnExitringConv() throws JsonSyntaxException, IOException, ErrorTypeException{
		assertEquals(0, chatServer.getConvsNumber());
		String conv = "why man love to cheat?";
		ConvOps convOps = new ConvOps("EricLu", true, conv);
		clientOut.println(convOps.toJSONString());
		clientOut.flush();
		pause();
		assertEquals(1, chatServer.getConvsNumber());
		assertTrue(chatServer.getConvs().containsKey(conv));
		
		clientOut.println(convOps.toJSONString());
		clientOut.flush();
		pause();
		assertTrue(clientIn.ready());
		assertEquals("{\"content\":\"you have created why man love to cheat?\",\"type\":\"HINT\"}", clientIn.readLine());

		assertTrue(clientIn.ready());
		assertEquals("{\"error\":\"you are already in this room\",\"type\":\"ERROR\"}", clientIn.readLine());
		assertEquals(1, chatServer.getConvsNumber());
		
	}
	
	/**
	 * Test if another user can join an existing conversation 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws ErrorTypeException 
	 * @throws JsonSyntaxException 
	 * @throws InterruptedException 
	 */
	@Test
	public void JoinConv() throws UnknownHostException, IOException, JsonSyntaxException, ErrorTypeException, InterruptedException{
		assertEquals(0, chatServer.getConvsNumber());
		String conv = "league of lengends";
		ConvOps convOps = new ConvOps("EricLu", true, conv);
		clientOut.println(convOps.toJSONString());
		clientOut.flush();
		pause();
		assertEquals(1, chatServer.getConvsNumber());
		assertTrue(chatServer.getConvs().containsKey(conv));
		assertTrue(chatServer.getConvs().get(conv).getUsers().containsKey("EricLu"));
		/*
		 * Harvey signed in 
		 */
		SignInAnOtherUser();
		ConvOps joinConv = new ConvOps("Harvey", true, conv);
		clientOut2.println(joinConv.toJSONString());
		clientOut2.flush();
		pause();
		assertTrue(chatServer.getConvs().get(conv).getUsers().containsKey("Harvey"));
		
		/*
		 * notify EricLu that Harvey joined the conversation 
		 */
		assertTrue(clientIn.ready());
		assertEquals("{\"content\":\"you have created league of lengends\",\"type\":\"HINT\"}", clientIn.readLine());

		assertTrue(clientIn.ready());
		assertEquals("{\"content\":\"Harvey has joined the conversation.\",\"type\":\"HINT\"}", clientIn.readLine());
		assertTrue(loginThread2.isAlive());
		
		assertTrue(chatServer.getUsers().containsKey("Harvey"));
		assertFalse(serverSide2.isClosed());
		Hint ckeck = new Hint("message before closing");
		handler2.updateQueue(ckeck);
		pause();
		assertTrue(clientIn2.ready());
		assertEquals("{\"content\":\"you have joined league of lengends\",\"type\":\"HINT\"}", clientIn2.readLine());

		assertTrue(clientIn2.ready());
		assertEquals("{\"content\":\"message before closing\",\"type\":\"HINT\"}", clientIn2.readLine());
		
	}
	
	/**
	 * Joining  an conversation that the user already-in 
	 * @throws IOException 
	 */
	@Test
	public void JoinNonExistingConv() throws IOException{
		assertEquals(0, chatServer.getConvsNumber());
		String conv = "league";
		ConvOps convOps = new ConvOps("EricLu", true, conv);
		clientOut.println(convOps.toJSONString());
		clientOut.flush();
		pause();
		assertEquals(1, chatServer.getConvsNumber());
		assertTrue(chatServer.getConvs().containsKey(conv));
		assertTrue(chatServer.getConvs().get(conv).getUsers().containsKey("EricLu"));
		
		clientOut.println(convOps.toJSONString());
		clientOut.flush();
		pause();
		assertTrue(clientIn.ready());
		assertEquals("{\"content\":\"you have created league\",\"type\":\"HINT\"}", clientIn.readLine());

		assertTrue(clientIn.ready());
		assertEquals("{\"error\":\"you are already in this room\",\"type\":\"ERROR\"}", clientIn.readLine());
	}
	
	/*
	 * Test if the ChatHandler handle the leave request properly 
	 */
	@Test
	public void LeaveConversation() throws IOException, JsonSyntaxException, ErrorTypeException{
		assertEquals(0, chatServer.getConvsNumber());
		String conv = "weather of day";
		ConvOps convOps = new ConvOps("EricLu", true, conv);
		clientOut.println(convOps.toJSONString());
		clientOut.flush();
		pause();
		assertEquals(1, chatServer.getConvsNumber());
		assertTrue(chatServer.getConvs().containsKey(conv));
		assertTrue(chatServer.getConvs().get(conv).getUsers().containsKey("EricLu"));
		
		/*
		 * another user sign in 
		 */
		SignInAnOtherUser();
		ConvOps joinConv = new ConvOps("Harvey", true, conv);
		clientOut2.println(joinConv.toJSONString());
		clientOut2.flush();
		pause();
		assertTrue(chatServer.getConvs().get(conv).getUsers().containsKey("Harvey"));
		assertTrue(clientIn.ready());
		assertEquals("{\"content\":\"you have created weather of day\",\"type\":\"HINT\"}", clientIn.readLine());

		assertTrue(clientIn.ready());
		assertEquals("{\"content\":\"Harvey has joined the conversation.\",\"type\":\"HINT\"}", clientIn.readLine());
		
		ConvOps leave = new ConvOps("EricLu", false, conv);
		clientOut.println(leave.toJSONString());
		clientOut.flush();
		pause();
		assertEquals(1, chatServer.getConvsNumber());
		assertTrue(chatServer.getConvs().containsKey(conv));
		assertFalse(chatServer.getConvs().get(conv).getUsers().containsKey("EricLu"));
		
		assertTrue(clientIn.ready());
		assertEquals("{\"content\":\"you just left weather of day\",\"type\":\"HINT\"}", clientIn.readLine());
		
		/*
		 * leave message from conversation 
		 */
		assertTrue(clientIn2.ready());
		assertEquals("{\"content\":\"you have joined weather of day\",\"type\":\"HINT\"}", clientIn2.readLine());
		
		assertTrue(clientIn2.ready());
		assertEquals("{\"content\":\"EricLu has left the conversation.\",\"type\":\"HINT\"}",clientIn2.readLine());
	}
	
	/*
	 * Exit a conversation that a user is not in 
	 */
	@Test
	public void LeaveConversationNonExisting() throws IOException{
		assertEquals(0, chatServer.getConvsNumber());
		String conv = "weather of day";

		ConvOps leave = new ConvOps("EricLu", false, conv);
		clientOut.println(leave.toJSONString());
		clientOut.flush();
		pause();
		assertEquals(0, chatServer.getConvsNumber());
		assertFalse(chatServer.getConvs().containsKey(conv));
		
		assertTrue(clientIn.ready());
		assertEquals("{\"error\":\"weather of day does not exist\",\"type\":\"ERROR\"}", clientIn.readLine());
	}
	
	/*
	 * Send a message to users in the coversations
	 */
	@Test
	public void SendMessage() throws IOException, JsonSyntaxException, ErrorTypeException, InterruptedException{
		assertEquals(0, chatServer.getConvsNumber());
		String conv = "league of lengends";
		ConvOps convOps = new ConvOps("EricLu", true, conv);
		clientOut.println(convOps.toJSONString());
		clientOut.flush();
		pause();
		assertTrue(clientIn.ready());
		assertEquals("{\"content\":\"you have created league of lengends\",\"type\":\"HINT\"}", clientIn.readLine());

		assertEquals(1, chatServer.getConvsNumber());
		assertTrue(chatServer.getConvs().containsKey(conv));
		assertTrue(chatServer.getConvs().get(conv).getUsers().containsKey("EricLu"));
		/*
		 * Harvey signed in 
		 */
		SignInAnOtherUser();
		ConvOps joinConv = new ConvOps("Harvey", true, conv);
		clientOut2.println(joinConv.toJSONString());
		clientOut2.flush();
		pause();
		assertTrue(chatServer.getConvs().get(conv).getUsers().containsKey("Harvey"));
		
		/*
		 * notify EricLu that Harvey joined the conversation 
		 */
		assertTrue(clientIn.ready());
		assertEquals("{\"content\":\"Harvey has joined the conversation.\",\"type\":\"HINT\"}", clientIn.readLine());

		// sending out the message 
		ChatToServer message = new ChatToServer(conv, "how are you", "EricLu");
		clientOut.println(message.toJSONString());
		clientOut.flush();
		pause();
		assertTrue(clientIn.ready());
		assertEquals("{\"From\":\"EricLu\",\"content\":\"how are you\",\"type\":\"CHAT\"}", clientIn.readLine());
		assertTrue(clientIn2.ready());
		assertEquals("{\"content\":\"you have joined league of lengends\",\"type\":\"HINT\"}", clientIn2.readLine());
		assertTrue(clientIn2.ready());
		assertEquals("{\"From\":\"EricLu\",\"content\":\"how are you\",\"type\":\"CHAT\"}", clientIn2.readLine());
	}
	
	/*
	 * SignOut Test/ test to see if chat hander can handle the sign out request properly  
	 */
	@Test
	public void SignOutTest() throws JsonSyntaxException, UnknownHostException, IOException, ErrorTypeException, InterruptedException{
		SignInAnOtherUser();
		String conv = "test";
		ConvOps joinConv = new ConvOps("Harvey", true, conv);
		clientOut2.println(joinConv.toJSONString());
		clientOut2.flush();
		pause();
		assertTrue(chatServer.getConvs().get(conv).getUsers().containsKey("Harvey"));
		
		SignInAndOut logOut = new SignInAndOut("Harvey", false);
		clientOut2.println(logOut.toJSONString());
		clientOut2.flush();
		pause();
		
		// check if the user has been removed from conversation 
		assertFalse(chatServer.getConvs().get(conv).getUsers().containsKey("Harvey"));
		// check if the user has been removed from server 
		assertFalse(chatServer.getUsers().containsKey("Harvey"));
		assertFalse(loginThread2.isAlive());
		assertTrue(serverSide2.isClosed());
		
	}
}
