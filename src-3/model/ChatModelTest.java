package model;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

import org.junit.Test;

import message.SignInAndOut;
import server.ChatServer;

public class ChatModelTest {
	
	// test if the model can send the login request  properly
	@Test
	public void loginAndOut() throws IOException, InterruptedException{
		ServerSocket server = new ServerSocket(10000);
		ChatServer chatServer =  new ChatServer(server);
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					chatServer.serve();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
		ChatModel chatModel = new ChatModel("localhost", 10000);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		chatModel.outputDebugger(bos);
		chatModel.start();
	
		chatModel.login("EricLu");
		
		
		Thread.sleep(50);
		// check the output to server
		String expected = "{\"username\":\"EricLu\",\"type\":\"SIGNIN\"}";
		assertEquals(bos.toString(), expected);
		// check if the client receive the updated userlist 
		assertEquals( 1,chatModel.getUserNumber());
		
		chatModel.logOut("EricLu");
		Thread.sleep(50);
		assertEquals( 0, chatServer.getUserNumber());

	}

}
