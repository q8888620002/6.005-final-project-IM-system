package clientMain;

import java.lang.reflect.InvocationTargetException;


import ui.ChatGUI;

/**
 * GUI chat client runner.
 */
public class Client {

    /**
     * Start a GUI chat client.
     * @throws InterruptedException 
     * @throws InvocationTargetException 
     */
    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
    			try {
    				ChatGUI chatBoard = new ChatGUI();
    				chatBoard.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
    }
}
