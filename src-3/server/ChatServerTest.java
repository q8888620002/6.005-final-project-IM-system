package server;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

public class ChatServerTest {
	ChatServer server;
	BufferedReader in;
	Thread runServer;
	
	/**
	 * Set up the server It also test to see if the server can be constructor properly
	 */
	@Before
	public void setUp(){
		
		PipedOutputStream pipeOut = new PipedOutputStream();
		PipedInputStream pipeIn;
		try
		{
			pipeIn = new PipedInputStream(pipeOut);
			System.setErr(new PrintStream(pipeOut));
			in = new BufferedReader(new InputStreamReader(pipeIn));
		}
		catch (IOException e)
		{
			fail();
		}
		
		/*
		 * Start the server 
		 */
		 runServer = new Thread(new Runnable() {	
			@Override
			public void run() {
				try {
					server.serve();
				} catch (IOException e) {	
					e.printStackTrace();
				}
			}
		});
	}
	
	/*
	 * Make the server thread pause
	 */
	private void pause() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		
	/*
	 * Test if the server can initialized properly and make a single socket connection
	 * 
	 */
	@Test
	public void testSingleConnection() throws UnknownHostException, IOException, InterruptedException{
		server = new ChatServer(5000);
		runServer.start();
		assertEquals(in.readLine(), "server starts!");
		assertEquals(in.readLine(), "server is waiting");
		Socket newsocket = new Socket("localhost", 5000);
		assertEquals(in.readLine(), "handling incoming connection");
	}
	/*
	 * Test for multiple sockets connection at the same time without blocking each other 
	 */
	@Test
	public void testConnection() throws UnknownHostException, IOException, InterruptedException{
		server = new ChatServer(10000);
		runServer.start();
		assertEquals(in.readLine(), "server starts!");
		pause();
		assertEquals(in.readLine(), "server is waiting");
		Socket newsocket1= new Socket("localhost", 10000);
		Socket newsocket2 = new Socket("localhost", 10000);
		
		assertEquals(in.readLine(), "handling incoming connection");
		assertEquals(in.readLine(), "server is waiting");
		assertEquals(in.readLine(), "handling incoming connection");
		
	}
	
	
}
