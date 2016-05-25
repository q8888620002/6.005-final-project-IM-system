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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import client.ChatRoom;
import message.ChatToClient;
import message.ClientMessageVisitor;
import message.ErrorMessage;
import message.ErrorTypeException;
import message.Hint;
import message.MessageFactory;
import message.SignInAndOut;
import message.ToClientMessage;
import message.ToServerMessage;
import message.Userlsit;

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
		private final HashMap<String, ChatRoom> rooms;
		
		private PrintWriter outputDebugger,inputDebugger,fullDebugger;
		/**
		 * It's a constructor of the chatModel which is the mode of the chatBoard	
		 * @param String of the host name
		 * @param Integer of the port number
		 * @throws IOException 
		 * @throws UnknownHostException 
		 */
		
		public ChatModel(String hostname,Integer portNum) throws IOException {
			this.hostname = hostname;
			this.port = portNum;
			this.users = new ArrayList<String>();
			this.rooms = new HashMap<String, ChatRoom >();
			
			/* 
			 * Initiating the connecting  procedure
			 */
			
			try {
				client = new Socket(hostname, port);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			
			/**
			 * if connecting succeed calling the main board ui and start to run the model
			 */
		}
		
		/**
		 * Start a new thread to listen to input 
		 */
		public void start(){
			try {
				
				out = new PrintWriter(client.getOutputStream(), true);
				
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
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
			SignInAndOut signIn = new SignInAndOut(username, true);
			sendRequest(signIn.toJSONString());
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
		 * Joining a existing conversation 
		 * @param string of a conversation name
		 */
		public void joinConv(){
			
		}
		
		
		/**
		 * Leave an existing conversation 
		 * @param string of conversation name
		 */
		public void LeaveConv(String conversation ){
			
		}
		
		/**
		 * receiving message from server 
		 * @param sender of the message 
		 * @param chat room name of the message 
		 * @param content of the message
		 */
		public void updateMessage(String from, String conv, String content){
			
		}
		
		
		/**
		 * debugger method of the chat model
		 * @param out
		 */
		public void outputDebugger(OutputStream out){
			outputDebugger = new PrintWriter(out,true);
		}

		@Override
		public Void visit(ChatToClient t) {
			// TODO Auto-generated method stub
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
