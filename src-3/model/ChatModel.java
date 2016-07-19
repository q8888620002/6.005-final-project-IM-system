package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import client.ChatRoom;
import message.ChatToClient;
import message.ChatToServer;
import message.ClientMessageVisitor;
import message.ConvOps;
import message.ErrorMessage;
import message.ErrorTypeException;
import message.Hint;
import message.MessageFactory;
import message.SignInAndOut;
import message.ToClientMessage;
import message.ToServerMessage;
import message.Userlsit;
import ui.ChatGUI;

/*
 * ChatModel represents the model of the client chat board,
 * 		receiving the input from server; then translating it.
 *      Finally, the Chat Model stores the information of the online chat server
 *      such as messages between clients ,available conversation that client can join 
 *       ,and the status of clients.
 * 		
 * Rep invariant: 
 * 		hostname and port number are final  
 * 
 */

public class ChatModel implements ClientMessageVisitor<Void>{
		private final String hostname;
		private final Integer port;
		private Socket client;
		private BufferedReader in;
		private PrintWriter out ;
		private Thread listener;
		private  ArrayList<String> users;
		private String username;
		private final HashMap<String, ChatRoom> rooms;
		private ChatGUI gui;
		
		private PrintWriter outputDebugger,inputDebugger,fullDebugger;
		/**
		 * It's a constructor of the chatModel which is the mode of the chatBoard	
		 * @param String of the host name
		 * @param Integer of the port number
		 * @throws IOException 
		 * @throws UnknownHostException 
		 */
		
		public ChatModel(String hostname,Integer portNum) throws IOException {
			/*
			 * init the chatmodel 
			 */
			this.hostname = hostname;
			this.port = portNum;
			this.users = new ArrayList<String>();
			this.rooms = new HashMap<String, ChatRoom >();
			
			
		}
		
		/**
		 * @param ChatGUI of the chat system
		 * Start a new thread to listen to input 
		 */
		public void start(ChatGUI gui){
			
			try {

				/* 
				 * Initiating the connecting  procedure
				 */
				
				client = new Socket(hostname, port);
				System.err.println("Connected successfully");
				out = new PrintWriter(client.getOutputStream(), true);
				
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			}  catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/**
			 * if connecting succeed calling the main board ui and start to run the model
			 */
			this.gui = gui;
			gui.setVisible(true);
			gui.ToLoginView();

			ClientHandler handler  = new ClientHandler(in);
			listener = new Thread(handler);
			listener.start();
			
		}
		
		
		/**
		 * Handler of the server message
		 * @param json string of server message
		 */
		private void parse(String serverMessage){
			GsonBuilder  gsonBuilder = new GsonBuilder();
			
			gsonBuilder.registerTypeAdapter(Message.class, new MessageFactory());
			Gson gson = gsonBuilder.create();
			ToClientMessage Message = gson.fromJson(serverMessage, Message.class);
			
			try {
				
				Message.accept(this);
			} catch (ErrorTypeException e) {
				e.printStackTrace();
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Sending Request to server 
		 * @param json string of request
		 */
		private void sendRequest(String conversation){
			synchronized(out){
				System.err.println("sending "+conversation);
				out.println(conversation);
				out.flush();
			}
			
			if(outputDebugger!=null){
				outputDebugger.print(conversation);
				outputDebugger.flush();
			}
			
			if(fullDebugger!=null){
				synchronized(fullDebugger){
					fullDebugger.print(conversation);
					fullDebugger.flush();
				}
			}
		}
		
		/**
		 * Login method of the chat model 
		 * @param String of the user name 
		 */
		public void login(String username){
			if(checkName(username)){
				SignInAndOut signIn = new SignInAndOut(username, true);
				sendRequest(signIn.toJSONString());
				this.username = username;
			}else{
				System.err.println("name not valid.");

				out.println(new ErrorMessage("this name is not valid. Please enter a new one").toJSONString());
				out.flush();
			}
		}
		
		/**
		 * Log out method of the chat model 
		 * @param String of the user name 
		 */
		public void logOut(String username){
			SignInAndOut signOut = new SignInAndOut(username, false);
			sendRequest(signOut.toJSONString());
		}
		
		/**
		 * Create a new conversation 
		 * @param conversation name 
		 */
		public void createConv(String convName){
			ConvOps create = new ConvOps(username, true, convName);
			sendRequest(create.toJSONString());
		}
		
		/**
		 * Joining a existing conversation 
		 * @param string of a conversation name
		 */
		public void joinConv(String convName){
			ConvOps create = new ConvOps(username, true, convName);
			sendRequest(create.toJSONString());
		}
		
		/**
		 * Leave an existing conversation 
		 * @param string of conversation name
		 */
		public void LeaveConv(String conversation ){
			ConvOps leave = new ConvOps(username, false, conversation);
			sendRequest(leave.toJSONString());
		}
		
		/**
		 * receiving message from server 
		 * @param sender of the message 
		 * @param chat room name of the message 
		 * @param content of the message
		 */
		public void updateMessage(String from, String conv, String content){
			ChatToServer message = new ChatToServer(conv, content, from);
			sendRequest(message.toJSONString());
		}
		
		
		/**
		 * debugger method of the chat model
		 * @param out
		 */
		public void outputDebugger(OutputStream out){
			outputDebugger = new PrintWriter(out,true);
		}
		
		/**
		 * Handler method of the chat message object, updating the message in the chatroom
		 * @param ChatToClient object
		 */
		@Override
		public Void visit(ChatToClient t) {
			System.err.println("update chat message.");
			synchronized (this) {
				rooms.get(t.convName()).updateChat(t.from(), t.content());
			}
			return null;
		}

		@Override
		public Void visit(Hint t) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Void visit(ErrorMessage t) {
			// TODO Auto-generated method stub
			return null;
		}

		
		/**
		 * Handler method of the Userlist object, updateing the userlist of the client model  
		 * @param Userlist 
		 */
		@Override
		public Void visit(Userlsit t) {
			System.err.println("update client user list.");
			// update user list 
			synchronized (this) {
				users = t.getNewUsers();
			}
			return null;
		}
		
		/**
		 * Check whether the given name of client is unique and valid
		 * A username should start with a-z or A-Z or digits and can also contain [-] or [_]
		 * @param username
		 * @return true if the username is valid; otherwise, return false
		 */
		private Boolean checkName(String username){
			final String namePattern = "^[a-zA-Z0-9_-]{5,10}$";
			Matcher matcher = Pattern.compile(namePattern).matcher(username);
			return matcher.matches();
		}
		
		/**
		 * Debugger method of the user number 
		 * @return the number of users at client side   
		 */
		public synchronized int getUserNumber(){
			return users.size();
		}
		
	
		/*
		 * private class that listen to the server input
		 */
		private class ClientHandler implements Runnable{
			private BufferedReader in;
			
			public ClientHandler(BufferedReader in) {
				this.in = in;
			}
			@Override
			public void run() {
				try {
					System.out.println("connecting server");
					
					while(true){
						try {
							
							String line = in.readLine();
							while(line!=null){
								// handle the server input message 
								parse(line);
								line = in.readLine();
							}
						} catch (Exception e) {
							e.printStackTrace();	
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
}
