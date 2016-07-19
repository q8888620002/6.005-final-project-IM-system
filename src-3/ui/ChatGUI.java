package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import model.ChatModel;

public class ChatGUI extends JFrame{
		private ConnectView connectView;
		private LobbyView lobby;
		private FriendList friends;
		private ChatModel model;
		private LoginView loginView;
		/**
		 * Constructor of the ChatGUI, initiating the connection view 
		 */
		public ChatGUI(){
			setTitle("New Client");
			setLayout(new BorderLayout());
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setPreferredSize(new Dimension(250,200));
			setMinimumSize(new Dimension(250,200));
	        connectView = new ConnectView(this);
	        add(connectView, BorderLayout.CENTER);
	        pack(); 
		}
		
		
		
		
		/**
		 * Switches the JPanel inside the ChatGUI from the ConnectView to the LoginView after the server
		 * has connected to the client and the model has been created.
		 */
		public void ToLoginView(){
			System.out.println("switch to login view from connect view");
			
			setVisible(false);
			getContentPane().remove(connectView);
			model = connectView.getModel();
			loginView = new LoginView(model);
			getContentPane().add(loginView, BorderLayout.CENTER);
			setPreferredSize(new Dimension(240,200));
			setMinimumSize(new Dimension(215,200));
			
			getContentPane().validate();
			getContentPane().repaint();
			setVisible(true);
		}
}
