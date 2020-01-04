package cmet.ac.sockets.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;


/**
 * Represents a client. 
 * Allows user inputs through keyboard and pass them to the server. 
 * Receives responses from the user. 
 * 
 * @author thanuja
 * @version 20.11.2019
 */
public class SimpleClient implements Runnable {

	// reference variable for client socket
	private Socket 					clientSocket;

	// reference variable to store object IO streams, should be used when working with serialized objects.
	private ObjectOutputStream 		output;
	private ObjectInputStream 		input;

	// boolean variable to store stopclient flag.
	private boolean 				stopClient;

	// reference variable for Thread
	private Thread 					clientReader;

	// variables to store Host IP and port number
	private String 					host;
	private int 					port;
	public String				    playerName;
	public String					playerAnswer;
	public String					playerQuestion;
	public String					playerCorrect;
	public String					playerScore;
	
	/**
	 * Constructor, initiates a client, and calls for openConnection.
	 * @param host
	 * @param port
	 * @throws IOException
	 */
	

	
	public SimpleClient(String host, int port) throws IOException {
		this.host = host;
		this.port = port;
		openConnection();
	}
	
	/**
	 * opens a connection to the server
	 * setup Object IO streams for the socket.
	 * 
	 * @throws IOException
	 */
	public void openConnection() throws IOException {

		// Create the sockets and the data streams
		try {

			this.clientSocket = new Socket(this.host, this.port);
			this.output = new ObjectOutputStream(this.clientSocket.getOutputStream());
			this.input = new ObjectInputStream(this.clientSocket.getInputStream());

		} catch (IOException ex) {
			try {
				closeAll();
			} catch (Exception exc) {
				System.err.println("[client: ] error in opening a connection to: " + this.host + " on port: " + this.port);
			}

			throw ex; // Rethrow the exception.
		}
		
		// creates a Thread instance and starts the thread.
		this.clientReader = new Thread(this);
		this.stopClient = false;
		this.clientReader.start();

	}
	
	/**
	 * Handles sending a message to server. In this case, it is a String. 
	 * @param msg
	 * @throws IOException
	 */
	public void sendMessageToServer(String msg) throws IOException {
		if (this.clientSocket == null || this.output == null)
			throw new SocketException("socket does not exist");

		this.output.writeObject(msg);
	}

	/**
	 * Handle message from the server. In this case, simply display them. 
	 * @param msg
	 */
	public void handleMessageFromServer(String msg) {
		display(msg);

	}
	
	public void handleQuestionFromServer(String playerQuestion) {
		display(playerQuestion);

	}
	
	/**
	 * Simply display a String message in the terminal. 
	 * @param message
	 */
	public void display(String message) {
		System.out.println("> " + message);
	}
	
	public void display1(String playerQuestion) {
		System.out.println("> " + playerQuestion);
	}
	
	/**
	 * Close all connections
	 * @throws IOException
	 */
	private void closeAll() throws IOException {
		try {
			// Close the socket
			if (this.clientSocket != null)
				this.clientSocket.close();

			// Close the output stream
			if (this.output != null)
				this.output.close();

			// Close the input stream
			if (this.input != null)
				this.input.close();
			
		} finally {
			// Set the streams and the sockets to NULL no matter what.
			this.output = null;
			this.input = null;
			this.clientSocket = null;
		}
	}
	
	/**
	 * handles user inputs from the terminal. 
	 * This should run as a separate thread. In this case, main thread. 
	 * 
	 */
	
	public void runClient() {
		
		System.out.println("Welcome, Enter your name :");
        try {
        	Scanner playerInfo = new Scanner(System.in);
            playerName = playerInfo.nextLine();
            this.output.writeObject(playerName); 
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } System.out.println("Welcome to The Game, " + playerName);
        
        
		
		try {
			BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
			String message = null;

			while (true) {
				message = fromConsole.readLine();
				handleUserInput(message);
				if(message.equals("over"))
					break;
			}
			
			System.out.println("[client: ] stopping client...");
			this.stopClient = true;
			fromConsole.close();
			//closeAll();
		} catch (Exception ex) {
			System.out.println("[client: ] unexpected error while reading from console!");
		}
		
     
		
		
	}
	

	/**
	 * Can perform any pre-processing or checking of the user input before sending it to server. 
	 * 
	 * @param userResponse
	 */
	public void handleUserInput(String userResponse) {

		if (!this.stopClient) {
			try {
				sendMessageToServer(userResponse);
			} catch (IOException e) {
				System.err.println("[client: ] error when sending message to server: " + e.toString());

				try {
					closeAll();
				} catch (IOException ex) {
					System.err.println("[client: ] error closing the client connections: " + ex.toString());
				}
			}
		}
	}
	
	/**
	 * The thread that communicates with the server. 
	 * receives a message from the server, passes it to handleMessageFromServer(). 
	 * 
	 */
	@Override
	public void run() {

      
		
		
		
		// Loop waiting for data

        try {
			while (!this.stopClient) {
				// Get data from Server and send it to the handler
				// The thread waits indefinitely at the following
				// statement until something is received from the server
				playerQuestion = (String) input.readObject();
				
				// Concrete subclasses do what they want with the
				// msg by implementing the following method
				handleMessageFromServer(playerQuestion);
			}
			System.out.println("[client: ] client stopped..");
		} catch (Exception exception) {
			if (!this.stopClient) {
				try {
					closeAll();
				} catch (Exception ex) {
					System.err.println("[client: ] error in closing the client connection...");
				}
			}
		} finally {
			clientReader = null;
		}
        
        System.out.println(playerQuestion);

        System.out.println("What is your answer?:");
        try {
        	Scanner playerInfo = new Scanner(System.in);
            playerAnswer = playerInfo.nextLine();
            this.output.writeObject(playerAnswer);
            System.out.println("Wait for your next question");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

        
        String msg;
		try {
			while (!this.stopClient) {
				// Get data from Server and send it to the handler
				// The thread waits indefinitely at the following
				// statement until something is received from the server
				msg = (String) input.readObject();

				// Concrete subclasses do what they want with the
				// msg by implementing the following method
				handleMessageFromServer(msg);
			}
			
			System.out.println("[client: ] client stopped..");
		} catch (Exception exception) {
			if (!this.stopClient) {
				try {
					closeAll();
				} catch (Exception ex) {
					System.err.println("[client: ] error in closing the client connection...");
				}
			}
		} finally {
			clientReader = null;
		}
        
		
        
        
		
	}

		// Loop waiting for data

	/**
	 * Main() to initiate the client.
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		// hardcoded server IP and port number. 
		String ip; // = "127.0.0.1";
		int port; // = 7777;
		
		ip = args[0];
		port = Integer.parseInt(args[1]);
		

		SimpleClient chatclient = null;
		
		// thread to communicate with the server starts here.
		try {
			chatclient = new SimpleClient(ip, port);
		} catch (IOException e) {
			System.err.println("[client: ] error in openning the client connection to " + ip + " on port: " + port);
		}
		chatclient.runClient();
	}

}
