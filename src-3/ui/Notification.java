package ui;

import javax.swing.JOptionPane;

/*
 * This is a abstract class that contains static method that notify user about errors or other message
 */
public abstract class Notification {
	
	/**
	 * @return an alert gui that tells connection error happened
	 */
	public static void connectionFailedError(){
		JOptionPane.showMessageDialog(null,"Connection Failed.",
			    "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	
	/**
	 * @return an alert message that tells user about host number error
	 */
	public static void hostPortFormatError(){
		JOptionPane.showMessageDialog(null,"Hostname is incorrect.",
			    "Error", JOptionPane.ERROR_MESSAGE);
	}
}
