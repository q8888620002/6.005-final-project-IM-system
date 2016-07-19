package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.ChatModel;

public class LoginView extends JPanel{
	
	private ChatModel model;
	private final JLabel messageLabel;
	private final JLabel usernameLabel;
	private final JTextField username;
	private final JButton loginButton;
	//Rep Invariant: != null
	
	/**
	 * Constructs the LoginView object, initializes the JComponents, defines the layout, and adds the listeners.
	 * @param m The Model for the GUI
	 */
	public LoginView(ChatModel m){
		//initialize model
		this.model = m;
		
		//create the components
		messageLabel = new JLabel("Enter Login.");
		usernameLabel = new JLabel("Username:");
		username = new JTextField();
		loginButton = new JButton("Login");
		
		//define layout
		GroupLayout layout = new GroupLayout(this);
		
        setLayout(layout);
        
        // get some margins around components by default
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        
        // place the components in the layout (which also adds them
        // as children of this view)
        layout.setHorizontalGroup(
        		layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
        				.addComponent(usernameLabel))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(messageLabel)
        				.addComponent(username, 100, 150, Short.MAX_VALUE)
        				.addComponent(loginButton))
        );
        
        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        		.addComponent(messageLabel)
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(usernameLabel)
        				.addComponent(username, GroupLayout.PREFERRED_SIZE, 25,
        				          GroupLayout.PREFERRED_SIZE))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER))
        		.addComponent(loginButton)
        );
        
        // add listeners for user input
        username.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginUser(username.getText());
                }
            }
        });
        
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loginUser(username.getText());
            }
        });
        
        
	}
	
	//send login user to model
	private void loginUser(String username){
		System.out.println("login user");
		model.login(username);
	}
	
}
