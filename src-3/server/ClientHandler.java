package server;
/*
 * Clienthandler handle socket connection of client , invoke the ChatBoard 
 * Rep invariant:
 * 		client is not null 
 */

import java.net.Socket;

public class ClientHandler extends Thread{
		private Socket client;
		private 
		
		/**
		 * Constructor of clientHandler 
		 * @param client socket
		 */
		public ClientHandler(Socket client){
			this.client = client;
		}
}
