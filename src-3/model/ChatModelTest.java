package model;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

import org.junit.Test;

import server.ChatServer;

public class ChatModelTest {
	
	// test if the model can login properly
	@Test
	public void login() throws IOException, InterruptedException{
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
		chatModel.login("Eric");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		chatModel.outputDebugger(bos);
		Thread.sleep(10);
		// check the output to server
		String expected = "{\"content\":\"Welcome Eric you are login now\",\"type\":\"HINT\"}";
		
		assertEquals(bos.toString(),expected);
		
	}
}
