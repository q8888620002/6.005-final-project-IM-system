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
		private final ArrayList<String> users;
		private final HashMap<String, ChatRoom> rooms;
		
		private PrintWriter outputTranscript,inputTranscript,fullTranscript;
		/**
		 * It's a constructor of the chatModel which is the mode of the chatBoard	
		 * @param String of the host name
		 * @param Integer of the port number
		 * @throws IOException 
		 * @throws UnknownHostException 
		 */
		
		public ChatModel(String hostname,Integer portNum) throws UnknownHostException, IOException {
			this.hostname = hostname;
			this.port = portNum;
			this.users = new ArrayList<String>();
			this.rooms = new HashMap<String, ChatRoom >();
		}
		
		/**
		 * start a socket connection with chat server 
		 */
		public void start(){
			try {
				client = new Socket(hostname, port);
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				out = new PrintWriter(client.getOutputStream(), true);
				
				while(true){
					try {
						// read the input message 
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
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		/**
		 * Handler of the server message
		 */
		public void parse(String serverMessage){
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
		 * Login method of the chat model 
		 * @param String of the user name 
		 */
		public void login(String username){
			SignInAndOut signIn = new SignInAndOut(username, true);
			
		}
		
		/**
		 * Log out method of the chat model 
		 * @param String of the user name 
		 */
		public void logOut(String username){
			
		}
		
		/**
		 * update userlist
		 * @param new array list of string of online users
		 */
		public void updateUsers(ArrayList<String> String){
			
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
		 * Sending Request to server 
		 * @param json string of request
		 */
		public void sendRequest(String conversation){
			
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
			outputTranscript = new PrintWriter(out,true);
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

		@Override
		public Void visit(Userlsit t) {
			// TODO Auto-generated method stub
			return null;
		}

		
}
