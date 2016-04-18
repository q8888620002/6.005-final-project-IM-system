package tests;

import java.io.IOException;

import org.junit.Test;

import server.ChatServer;

public class ChatServerTest {

	// make sure assertion turned on
	 @Test(expected=AssertionError.class)
	    public void testAssertionsEnabled() {
	        assert false;
	    }
	
	 // Construct a new server.  
	@Test
	public void initServer1() throws IOException{
		ChatServer server = new ChatServer(4444);
	}
}
