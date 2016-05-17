package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import model.ChatModel;

public class ChatGUI extends JFrame{
		private ConnectView connectView;
		private LobbyView lobby;
		private ChatModel model;
		
		/**
		 * Constructor of the ChatGUI, initiating the connection view 
		 */
		public ChatGUI(){
			setTitle("IM Client");
			setLayout(new BorderLayout());
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setPreferredSize(new Dimension(250,200));
			setMinimumSize(new Dimension(250,200));
	        connectView = new ConnectView(this);
	        add(connectView, BorderLayout.CENTER);
	        pack(); 
		}
}
