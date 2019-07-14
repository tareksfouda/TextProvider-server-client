package com.tarek;

import java.util.*;
import java.net.*;
import java.io.*;

public class Server{

	static int port;
	Socket clientSocket;
	ServerSocket serverSocket;
	PrintWriter out;
	BufferedReader in;

	public Server(){
		port = 10322;
		serverSocket = null;
		clientSocket = null;
		out = null;
		in = null;
	}

	public static void main(String[] args) throws IOException{
//		if(args.length != 1){
//			System.err.println("Please specify ONE file to serve.");
//			System.exit(-1);
//		}

		Server ls = new Server();

		ls.initServer();

//		String fileName = args[0];
//		ls.preProcessFile(fileName);

		ls.preProcessFile("/home/tarek/eclipse-workspace/TextProvider/src/com/tarek/text.txt");

		ls.processRequests();

	}

	public void initServer() throws IOException{

		try{
			serverSocket = new ServerSocket(port);
		}
		catch(IOException e){
			System.err.println("Could not listen on port " + port);
			System.exit(-1);
		}

		try{
			clientSocket = serverSocket.accept();
		}
		catch(IOException e){
			System.err.println("failed: port " + port);
			System.exit(-1);
		}

		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(
									clientSocket.getInputStream()));
	}

	public void processRequests() throws IOException{
		String inputLine = null;
		while(true){
			try{
				// Gets the value of input passed from the client and printing it
				inputLine = in.readLine();
				System.out.println(inputLine);
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
			// If Quit is passed then close the connection with the client.
			if(inputLine.equals("QUIT")){
				in.close();
				out.close();
				clientSocket.close();
				try{
					clientSocket = serverSocket.accept();
				}
				catch(IOException e){
					System.err.println("accept() failed: port " + port);
					System.exit(-1);
				}
				clientSocket.getOutputStream().flush();
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(
									clientSocket.getInputStream()));
				continue;
			}
			
			// If shutdown was passed then go to shutdown function (which shutdowns the server)
			if(inputLine.equals("SHUTDOWN")){
				shutdown();
				break;
			}
			
			// Else parse the input to return back the data.
			out.println(parseInput(inputLine));
		}
	}

	/*
	 * This method shuts down the server and closes it's connection with clients.
	 */
	public void shutdown() throws IOException{
		in.close();
		out.close();
		clientSocket.close();
		serverSocket.close();
	}

	/*
	 * This method parses the input and returns the corresponding line/msg to the client.
	 */
	public String parseInput(String str) throws IOException{
		if(str.length() < 4 || !str.substring(0,4).equals("GET ")){
			return "ERR";
		}

		int lineNumber = Integer.parseInt(str.substring(4,str.length()));
		if(lineNumber < 0)
			return "ERR";

		FileReader freader = null;
		try{
			freader = new FileReader("lines/" + lineNumber + ".tmp"); 
		}
		catch(IOException e){
			return "ERR";
		}
		BufferedReader br = new BufferedReader(freader);

		String ret = null;
		try{
			ret = br.readLine();
		}
		catch(IOException e){
			return "ERR";
		}
		return ret;
	}

	public void preProcessFile(String fileName) throws IOException{
		FileReader freader = null;
		try{
			freader = new FileReader(fileName);
		}
		catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
		BufferedReader br = new BufferedReader(freader);

		String currLine = null;
		FileWriter fwriter = null;
		int lineNumber = 1;

		File tempDir = new File("lines/");
		boolean b = tempDir.mkdir();
		if(!b){
			System.err.println("error making lines/ dir");
			System.exit(-1);
		}

		while((currLine = br.readLine()) != null){
			try{
				fwriter = new FileWriter("lines/" + lineNumber + ".tmp");
				fwriter.write(currLine + "\n");
				fwriter.close();
				lineNumber++;
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}