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
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import message.ChatToServer;
import message.ConvOps;
import message.MessageFactory;
import message.ServerMessageVisitor;
import message.SignInAndOut;
import message.ToServerMessage;
import server.ChatServer;
import server.Conversation;

/*
 * 	A sub-server that handles that handles the multiple client sockets.
 * 	While being created, it will check whether the username of client is available first.
 * 	If username is not valid, the UserInfo object of this connection will not be created. 
 *  Input handler will parse socket’s Input Stream to a Message object
 * 	and put a queue to BlockingQueue of conversation. 
 *  
 * 	Rep invariant:
 * 		client socket is final
 *      server is final 
 * 		
 */
public class InputHandler extends Thread implements ServerMessageVisitor<Void>{
		private final ChatServer server;
	    private final Socket clientSocket;
		private BufferedReader in;
		private HashMap<Integer, Conversation> convs;
		
		/**
		 * Create an InputHandler 
		 * @param client, socket connection of client 
		 * @param ChatServer 
		 */
		public InputHandler(Socket client,ChatServer server){
			this.clientSocket = client;
			this.server = server;
		}
		
		/**
		 *  Handling the incoming input request from client side
		 * @throws IOException 
		 */
		private void handleRequest() throws IOException{
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String input = in.readLine(); 
			while(input!=null){
				createMessage(input);
				in.readLine();
			}
		}
		
		/**
		 * Run method of input handler to handling input from client socket
		 */
		public void Run(){
			try {
				handleRequest();
			} catch (IOException e) {
				// TODO: handle exception
			}
		}
		
		/**
		 * Create a ToServerMessage ADT from socket's input and handle it 
		 * @param json string 
		 */
		private void createMessage(String jsonString){
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
		 */
		@Override
		public Void visit(SignInAndOut s) {
			String username = s.getUser();
			
			
			if(checkName(username)){
				AddUser(username);
			}
			return null;
		}

		@Override
		public Void visit(ConvOps s) {
			// TODO Auto-generated method stub
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
		 * A user will be added to userList of the server 
		 * @param name string of user 
		 */
		private void AddUser(String username){
			if(server.getUsers().containsKey(username)){
				System.err.println("This name already been used");
			}else{
				BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
				OutputHandler OutputHandler = new OutputHandler(clientSocket, queue);
				UserInfo newUser = new UserInfo(this, OutputHandler);
				server.getUsers().put(username, newUser);
			}
		}
		
		/**
		 * Create a conversation with specific id.
		 * The conversation will be added to AllConvs HashMap of server if created successfully
		 * User will also be notified.
		 * @return a Message queue that containing conversation being created.  
		 */
		private Message CreateConv(){
			return null;
		}
		
		/**
		 * Joining a specific conversation in the AllConvs hash map of server
		 * @param integer id of the conversation 
		 * @return a message queue that notifies all users that a user joined.
		 */
		private Message joinConv(Integer id){
			return null;
		}
		
		/**
		 * Leave a conversation that the user current in  
		 * @param id of the conversation that user is going to leave.
		 * @return a Message queue that notifies all users that a user left.
		 */
		private Message LeaveConv(Integer id){
			return null;
		}
		
		/**
		 * Sending Message to all users in the id specified conversation.
		 * @param id, the identifier for the conversation 
		 * @param String content to be sent to all the other users who currently in the conversation.
		 * @return a Message that contains string messages that to be sent to other users
		 */
		private Message SendMessage(Integer id, String content){
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
		
	
}
