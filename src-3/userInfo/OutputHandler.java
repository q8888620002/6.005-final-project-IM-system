package userInfo;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

/*
 * OutputHandler takes output queue from conversation or server, 
 * turning Message ADT into a output stream to client 
 * such as other user leave the conversation or message updating from other user.
 * 
 *  Rep invariant:
 *  		
 */
public class OutputHandler extends Thread{
		private final Socket client;
		private PrintWriter out;
		private BlockingQueue<Message> outputQueue;
		
		
		/**
		 * Create a output handler object with 
		 * @param client socket connection 
		 * @param blocking queue containing message to be sent to client from appropriate conversation 
		 * 
		 */
		public OutputHandler(Socket client,BlockingQueue<Message> queue){
			this.client = client;
			this.outputQueue = queue;
		}
		
		/**
		 * Run method for output handeler wait for incoming queue
		 * 
		 */
		private void Run(){
			
		}
		
		/**
		 * Handling the action correspond to output queue
		 * @param Message queue  from outputqueue
		 */
		private void handle(Message queue){
			
		}
		
		/**
		 * Create a output stream to notify user that a new conversation was created
		 * @return a string representation of notification
		 */
		private String createConv(){
			return null;
		}
		
		/**
		 *  Create a output stream to notify the client that the other user just left the conversation.
		 * @return String of a leave message
		 */
		private String leaveConv(){
			return null;
		}
		
		/**
		 * Update the latest message to users in the conversation 
		 * 
		 * @return String representation of message content
		 */
		private String sendMessage(){
			return null;
		}
		
		/**
		 * Put a new queue onto output handler's output queue
		 * This method can be called by the conversation or the current server
		 * @param message
		 */
		public void udpateQueue(Message message){
			
		}
		
}
