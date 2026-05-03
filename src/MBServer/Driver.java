package MBServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Vector;

import enums.ErrorType;
import enums.TestMessage;
import MBServer.Server;


public class Driver {
	public static void main(String[] args) {
		try {
			Scanner sc = new Scanner(System.in); //System.in is a standard input stream.
	        int port = 1234;
	        String host;
			host = InetAddress.getLocalHost().getHostAddress().trim();
	
	        // Automatically connects to the ServerSocket at host:port if it's active
	        Socket socket = new Socket(host, port);
	
	        // Input and output stream sockets.
	        InputStream inputStream = socket.getInputStream();
	        OutputStream outputStream = socket.getOutputStream();
	
	        // Create object streams so we can read and write messages to server.
	        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
	        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
	        
	        // Initialize variables for communicating with server
	        String clientCom;
	        TestMessage serverCom;
	        boolean login = false;
	        boolean expectMessage = false;
	        
	        // Send login message and await response
	        objectOutputStream.writeObject(new TestMessage("login", "pending", "Undefined"));
	        serverCom = ((TestMessage) objectInputStream.readObject());
	        
	        // Communicate until server connection is severed
	        while(true) {     	
	        	if (login) {	// Connected to server
	        		System.out.println("Send a message to the server to capitalize (type 'logout' to exit): ");
	        		clientCom = sc.nextLine();	// Type contents of text message to server (or request to logout)
	        		expectMessage = false;
	        		if (clientCom.compareTo("logout") == 0) {	// Logout, sever server connection, and exit program
	        			objectOutputStream.writeObject(new TestMessage("logout","pending","Undefined"));
	        			expectMessage = true;
	        		}
	        		else if (clientCom.compareTo("TableJoin") == 0) {
	        			objectOutputStream.writeObject(new TestMessage("TableJoin","pending","Undefined"));
	        		}
	        		else if (clientCom.compareTo("TableLeave") == 0) {
	        			objectOutputStream.writeObject(new TestMessage("TableLeave","pending","Undefined"));
	        		}
	        		else if (clientCom.compareTo("MakeTable") == 0) {
	        			objectOutputStream.writeObject(new TestMessage("MakeTable","pending","Undefined"));
	        		}
	        		else if (clientCom.compareTo("SignIn") == 0) {
	        			objectOutputStream.writeObject(new TestMessage("SignIn","pending","player123,password,0"));
	        			expectMessage = true;
	        		}
	        		else {	// Sent text message to server
	        			objectOutputStream.writeObject(new TestMessage("text","pending",clientCom));
	        			expectMessage = true;
	        		}
	        		if (expectMessage) {
	        			serverCom = ((TestMessage) objectInputStream.readObject());	// Await response
		        		if (serverCom.getType().compareTo("text") == 0 && serverCom.getStatus().compareTo("success") == 0) {
		        			System.out.println(serverCom.getText());	// Print capitalized text
		        		}
		        		else if (serverCom.getType().compareTo("logout") == 0 && serverCom.getStatus().compareTo("success") == 0){
		        			login = false;
		        			return;	// Exit after logging out program
		        		}
		        		else if (serverCom.getType().compareTo("Error") == 0){
		        			System.out.println(serverCom.getText());	// Print capitalized text
		        		}
	        		}
	        	}
	        	else {
	        		if (serverCom.getType().compareTo("login") == 0 && serverCom.getStatus().compareTo("success") == 0) {
	        			login = true;	// On successful connect, enable text and logout messages
	        		}
	        		else {
	        			objectOutputStream.writeObject(new TestMessage("login", "pending", "Undefined"));
	        	        serverCom = ((TestMessage) objectInputStream.readObject());	// Repeatedly attempt to connect until successful
	        		}
	        	}
	        }
		}
        catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}