package userInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import message.ChatToServer;
import message.ConvOps;
import message.ErrorTypeException;
import message.MessageFactory;
import message.ServerMessageVisitor;
import message.SignInAndOut;
import message.ToServerMessage;
import message.ToServerMessage.ToServer;
import server.ChatServer;
import server.Conversation;

/*
 * 	A sub-server that handles that handles the multiple client sockets.
 * 	While being created, it will check whether the username of client is available first.
 * 	If username is not valid, the UserInfo object of this connection will not be created. 
 *  Input handler will parse socket’s Input Stream to a Message object
 * 	and put a queue to BlockingQueue of conversation. 
 * 
 *  ChatHandler also receive output queue from conversations and server 
 *  and parse the output queue, sending it back to clients side.
 * 	Rep invariant:
 * 		client socket is final
 *      server is final 
 * 		
 */
public class ChatHandler extends Thread implements ServerMessageVisitor<Void>{
		private final ChatServer server;
	    private final Socket clientSocket;
		private BufferedReader in;
		private final HashMap<String, Conversation> convs = new HashMap<String, Conversation>();
		private final BlockingQueue<Message> outputqueue = new LinkedBlockingQueue<Message>();
		
		
		/**
		 * Create an InputHandler 
		 * @param client, socket connection of client 
		 * @param ChatServer 
		 */
		public ChatHandler(Socket client,ChatServer server){
			this.clientSocket = client;
			this.server = server;
		}
		
		/**
		 *  Handling the incoming input request from client side
		 * @throws IOException 
		 * @throws ErrorTypeException 
		 * @throws JsonSyntaxException 
		 */
		private void handleRequest() throws IOException, JsonSyntaxException, ErrorTypeException{
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String input = in.readLine(); 
			while(input!=null){
				createMessage(input);
				in.readLine();
			}
		}
		
		/**
		 * Run method of input handler to handling input from client socket
		 * @throws ErrorTypeException 
		 * @throws JsonSyntaxException 
		 */
		public void Run() throws JsonSyntaxException, ErrorTypeException{
			try {
				handleRequest();
				} catch (IOException e) {
			}
		}
		
		/**
		 * Create a ToServerMessage ADT from socket's input and handle it 
		 * @param json string 
		 * @throws ErrorTypeException 
		 * @throws JsonSyntaxException 
		 */
		private void createMessage(String jsonString) throws ErrorTypeException, JsonSyntaxException{
			GsonBuilder  gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Message.class, new MessageFactory());
			ToServerMessage Message = gsonBuilder.create().fromJson(jsonString, Message.class);
			/*
			 * Make InputHandler to handle the message
			 */
			Message.accept(this);
		}
		
		/**
		 * Visit method for InputHandler to handle with SignInAndOut
		 * @throws ErrorTypeException 
		 */
		@Override
		public Void visit(SignInAndOut s) throws ErrorTypeException {
			String username = s.getUser();
			ToServer log =  s.getType();
			
			if(log == ToServer.SIGNIN){
				// check if the user name is valid
				if(checkName(username)){
					AddUser(username);
				}else{
					System.err.println("this name is not valid.");
				}
			}else if(log == ToServer.SIGNOUT){
				
				if(server.getUsers().containsKey(username)){
					RemoveUser(username);
				}else{
					System.err.println("You cannot log out before logging in.");
				}
				
			}else{
				// shouldn't get here
				throw new ErrorTypeException("this type doesn't exist.");
			}
		
			return null;
		}

		/**
		 * Visitor method of Conversation operations
		 * @throws ErrorTypeException 
		 */
		@Override
		public Void visit(ConvOps s) throws ErrorTypeException {
			String convName = s.getConv();
			ToServer type = s.getType();
			String username = s.getUser();
			
			if(type == ToServer.JOIN){
				
				 // check if the conversation already exists
			
				synchronized (this) {
					if(server.getConvs().containsKey(convName)){ 
						// add user to this conversation 
						Conversation conv = server.getConvs().get(convName);
						conv.addClient(username,this);
						System.err.println(username+" joined room "+ convName);
					}else{
						// if it doesn't exists, just create one and join in the conversation 
						CreateConv(convName);
						
						// add user to this conversation 
						System.err.println(username +" created room "+ convName);
						
						Conversation conv = server.getConvs().get(convName);
						conv.addClient(username, this );
					}
					// should not get here	
				}
			}else if (type == ToServer.LEAVE) {
				
					if(convs.containsKey(convName)){
						convs.get(convName).removeClient(username, this);
					}else{
						
						System.err.println("you are not in that room");
					}
				
			}else{
				// should not get here
				throw new ErrorTypeException("this type doesn't exist.");
				
			}
			return null;
		}

		@Override
		public Void visit(ChatToServer s) {
			// TODO Auto-generated method stub
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
		 * A new UserInfo of current client will be initialized and added to userList of the server
		 *    only if this user name is valid. 
		 *    This method is synchronized. Users cannot be added concurrently
		 * @param name string of user 
		 */
		private void AddUser(String username){
			synchronized (this) {
				if(server.getUsers().containsKey(username)){
					System.err.println("This name already been used");
				}else{

					UserInfo newUser = new UserInfo(this);
					server.getUsers().put(username, newUser);
				}
			}
		}
		
		/**
		 * Remove a existing user from user list of server
		 * @param name string of user 
		 */
		private void RemoveUser(String username){
			synchronized (this) {
				if(server.getUsers().containsKey(username)){
					server.getUsers().remove(username);
				}
			}
		}
		
		
		/**
		 * Create a conversation with specific id.
		 * The conversation will be added to AllConvs HashMap of server if created successfully
		 * All user will also be notified. 
		 * This method is synchronized, conversation cannot be create simultaneously 
		 * @param String, name of the conversation  
		 */
		private void CreateConv(String ConvName){
			synchronized(this){
				try {
					Conversation conversation = new Conversation(ConvName);
					server.getConvs().put(ConvName, conversation);
					conversation.start();
				} catch (Exception e) {
					System.err.println("conversation "+ConvName+" creation failed.");
				}
				
			}
		}
		
		/**
		 * Joining a specific conversation in the AllConvs hash map of server
		 * @param integer id of the conversation 
		 * @return a message queue that notifies all users that a user joined.
		 */
		private void joinConv(String roomname){
			
		}
		
		/**
		 * Leave a conversation that the user current in  
		 * @param id of the conversation that user is going to leave.
		 * @return a Message queue that notifies all users that a user left.
		 */
		private Message LeaveConv(String roomname){
			return null;
		}
		
		/**
		 * Sending Message to all users in the id specified conversation.
		 * @param id, the identifier for the conversation 
		 * @param String content to be sent to all the other users who currently in the conversation.
		 * @return a Message that contains string messages that to be sent to other users
		 */
		private Message SendMessage(String roomname, String content){
			return null;
		}
		
		/**
		 * debugger method of CheckName
		 * @param username 
		 * @return boolean of user 
		 */
		public Boolean checkUserName(String username){
			return checkName(username);
		}
		
		/**
		 * Update a queue onto this ChatHandler's blockingQueue
		 * @param queue
		 * @throws InterruptedException
		 */
		
		public void updateQueue(Message queue) throws InterruptedException {
			outputqueue.put(queue);
		}
	
}
