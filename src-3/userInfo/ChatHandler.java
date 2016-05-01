package userInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
import message.ErrorMessage;
import message.ErrorTypeException;
import message.Hint;
import message.MessageFactory;
import message.ServerMessageVisitor;
import message.SignInAndOut;
import message.ToClientMessage;
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
public class ChatHandler implements Runnable ,ServerMessageVisitor<Void>{
		private final ChatServer server;
	    private final Socket clientSocket;
		private final BufferedReader in;
		private final HashMap<String, Conversation> convs = new HashMap<String, Conversation>();
		private final BlockingQueue<ToClientMessage> outputqueue = new LinkedBlockingQueue<ToClientMessage>();
		private final PrintWriter out;
		private final Thread outputThread;
		
		/**
		 * Create an InputHandler 
		 * @param client, socket connection of client 
		 * @param ChatServer 
		 * @throws IOException 
		 * @throws ErrorTypeException 
		 * @throws JsonSyntaxException 
		 */
		public ChatHandler(Socket client,ChatServer server) throws IOException, JsonSyntaxException, ErrorTypeException{
			this.clientSocket = client;
			this.server = server;
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			
			outputThread = new Thread(){
				@Override
				public void run() {
					while(true){
						try {
							ToClientMessage message = outputqueue.take();
						
							//System.out.println("Server writing output to port "
			              //         + ": " + message.toJSONString());
							try {
								out.println(message.toJSONString());
								out.flush();
							} catch (Exception e) {
								e.printStackTrace();
								System.err.println("client connection error");
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			
			
			out.println(new Hint("Please enter your name").toJSONString());
			out.flush();
			
			String loginRequest = in.readLine(); 
			
			/*
			 * loginRequest would be null if the client socket is closed
			 */
			if(loginRequest != null)
				parseMessage(loginRequest);
			else 
				throw new IOException("Client disconnected");
			
		}

		/**
		 * Run method of input handler to handling input request from client socket
		 */
		@Override
		public void run(){
			out.println(new Hint("Receiving Message from Server..").toJSONString());
			/*
			 * Start the Output thread and input handler thread 
			 */
			outputThread.start();
			
			while (true) {
				try {
						String input = in.readLine(); 	
						while(input!=null){
							System.err.println(input);
							parseMessage(input);
							input = in.readLine();
						}
					} catch (IOException e) {
						
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					} catch (ErrorTypeException e) {
						e.printStackTrace();
					}
			}
		}
		
		/**
		 * Create a ToServerMessage ADT from socket's input and handle it 
		 * @param json string 
		 * @throws ErrorTypeException 
		 * @throws JsonSyntaxException 
		 */
		private void parseMessage(String jsonString) throws ErrorTypeException, JsonSyntaxException{
			
			GsonBuilder  gsonBuilder = new GsonBuilder();
			
			gsonBuilder.registerTypeAdapter(Message.class, new MessageFactory());
			Gson gson = gsonBuilder.create();
			
			ToServerMessage Message = gson.fromJson(jsonString, Message.class);
			
			/*
			 * Make InputHandler to handle the message
			 */
		
			Message.accept(this);
		}
		
		/**
		 * Visit method for InputHandler to handle with SignInAndOut
		 */
		@Override
		public Void visit(SignInAndOut s) {
			String username = s.getUser();
			ToServer log =  s.getType();
		
			if(log == ToServer.SIGNIN){
				
				// check if the user name is valid
				if(checkName(username)){
					
					AddUser(username);
				}else{
					
					out.println(new ErrorMessage("this name is not valid. Please enter a new one").toJSONString());
					out.flush();
				}
			}else if(log == ToServer.SIGNOUT){
				
				if(server.getUsers().containsKey(username)){
					
					RemoveUser(username);
				}else{
					
					ErrorMessage error = new ErrorMessage("You're currently offline. You cannot sign out.");
					try {
						this.updateQueue(error);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}else{
				
				System.err.println("Unknown message type: " + log);
				
				ErrorMessage errorQue = new ErrorMessage("Unknown input message. please try again.");
				try {
					this.updateQueue(errorQue);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		
			return null;
		}

		/**
		 * Visitor method of Conversation operations
		 */
		@Override
		public Void visit(ConvOps s) {
			
			String convName = s.getConv();
			ToServer type = s.getType();
			String username = s.getUser();
			
			if(type == ToServer.JOIN){
				
				 // check if the conversation already exists
			
				synchronized (this) {
					
					if(server.getConvs().containsKey(convName)){ 
						// add user to this conversation 
						Conversation conv = server.getConvs().get(convName);
						
						/*
						 * check if the user is in the conversation already
						 */
						
						if(!conv.getUsers().containsKey(username)){
							
							// add conversation to this chat handler 
							
							conv.addClient(username,this);
							System.err.println(username+" joined room "+ convName);
						}else{
							try {
								ErrorMessage e = new ErrorMessage("you are already in this room");
								this.updateQueue(e);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						
					}else{
						// if it doesn't exists, just create one and join in the conversation 
						CreateConv(convName);
						
						// add user to this conversation 
						System.err.println(username +" created room "+ convName);
						
						Conversation conv = server.getConvs().get(convName);
						
						convs.put(convName, conv);

						conv.addClient(username, this);
						
						System.err.println(username+" joined room "+ convName);
						
					}
					// should not get here	
				}
			}else if (type == ToServer.LEAVE) {
				synchronized (this) {
					
						if(convs.containsKey(convName)){
							
							convs.get(convName).removeClient(username, this);
							
							System.err.println(username+" just leave the conversation "+convName);
							try {
								this.updateQueue(new Hint("you just left "+convName));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else{
							ErrorMessage errorMessage = new ErrorMessage(convName+" does not exist");
							try {
								this.updateQueue(errorMessage);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
				}
			}else{
				System.err.println("Unknown message type: " + type);
				
				ErrorMessage errorQue = new ErrorMessage("Unknown input message. please try again.");
				try {
					this.updateQueue(errorQue);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
					out.println(new Hint("This name already been used").toJSONString());
					out.flush();
				}else{
					UserInfo newUser = new UserInfo(this);
					server.getUsers().put(username, newUser);
					
					out.println(new Hint("Welcome "+username+" you are login now").toJSONString());
					out.flush();
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
					Hint goodbye = new Hint(username+", see you next time.");
					try {
						this.updateQueue(goodbye);
					} catch (Exception e) {
						e.printStackTrace();
					}
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
		 * Update a queue onto this ChatHandler's output blockingQueue
		 * @param String, output queue
		 * @throws InterruptedException
		 */
		
		public void updateQueue(ToClientMessage queue) throws InterruptedException {
			outputqueue.put(queue);
		}

		
}
