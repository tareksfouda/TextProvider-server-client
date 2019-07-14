package com.tarek;

import java.net.*;
import java.io.*;

public class Client{

	static int port;
	String host;
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	BufferedReader stdin;

	public Client(){
		port = 10322;
		host = null;
		clientSocket = null;
		out = null;
		in = null;
		stdin = new BufferedReader(new InputStreamReader(System.in));
	}

	/*
	 * This method initializes the connection
	 */
	public void init() throws IOException{
		System.out.println("Please enter the IP address or hostname of the"
					+ " desired server. Hint: Enter 127.0.0.1 to use localhost.");
		// This will be the host.
		String host = stdin.readLine();

		try{
			// create a client socket with the host entered by the user and the custom port number we have (10322)
			clientSocket = new Socket(host, port);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
										clientSocket.getInputStream()));
		}
		catch(UnknownHostException e){
			System.err.println("There is a problem with the host: " + host);
			System.exit(1);
		}
		catch(IOException e){
			System.err.println("Unable to obtain I/O for connection" + 
								"with " + host);
			System.exit(1);
		}
		System.out.println("SUCCESS: You are now connected to the server. ;)");
		return;
	}

	/*
	 * This method process the user's input and pass it to the server and receiving back data from the Server.
	 */
	public void process() throws IOException{
		String userInput = null;
		String temp = null;

		while((userInput = stdin.readLine()) != null){
			// Pass the user Input to the server
			out.println(userInput);
			
			// If user types quit or shout down break this loop so we can close the client connections
			if(userInput.equals("SHUTDOWN") || userInput.equals("QUIT"))
				break;
			// save whatever the response of the Server
			temp = in.readLine();
			
			// Printing out the response from the server.
			if(!temp.equals("ERR"))
				System.out.println("OK");
			
			System.out.println(temp);
		}
		return;
	}

	/*
	 * This method shuts down the client.
	 */
	public void shutdown() throws IOException{
		in.close();
		out.close();
		stdin.close();
		clientSocket.close();
	}

	public static void main(String[] args) throws IOException{
		Client lc = new Client();
		
		lc.init();
		lc.process();
		lc.shutdown();
	}
}