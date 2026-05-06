package MBClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import enums.ErrorType;
import enums.MessageStatus;
import enums.MessageType;
import message.Message;

/*
 * CLIENT CLASS:
 * Acts as handle for a users connectivity to the server. Holds both network information 
 * and user information. Is not a driver, therefore must be activated externally.
 *
 */

public class Client {
	
	// ===== NETWORK INFO ====== //
	
	private int port;
	private String host;
	private Socket socket;
	private OutputStream outputStream;
	private InputStream inputStream;
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;	
	
	// ======= USER INFO ======= //
	
	private String IPAddress; // client IP or server IP?
	private Vector<Message> messageLog; // supposed to be recording every message?
	private boolean loggedIn;
	private String username;
	private boolean[] seatedPlayers;
	
	// ====== CONSTRUCTORS ===== //
	
	public Client() {
		try {
			// initialize port/host with default values
			this.port = 1234;
			this.host = InetAddress.getLocalHost().getHostAddress().trim(); // host default to inet address;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	public Client(int port, String host) {
		this.port = port;
		this.host = host;
	}
	
	// ===== PUBLIC METHODS ==== //
	
	public void connect() {
		try {		
	        // connect to the ServerSocket at host:port
	        socket = new Socket(host, port);
	        System.out.println("\nconnected to " + host + ":" + port);
	        
	        // establish output stream to server
	        // get output stream from connected socket
	        outputStream = socket.getOutputStream();
	        // create an ObjectOutputStream to send an object through outputStream
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.flush();
			
	        // establish input stream from server
	        // get input stream from connected socket
	        inputStream = socket.getInputStream();
	        // create an ObjectInputStream to receive an object through inputStream
	        objectInputStream = new ObjectInputStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean verifyConnection() {
		// send Connected message to server
        send(new Message(MessageType.Connected, MessageStatus.Pending)); 
        
        // read response from server
        Message responseMessage = receive();
        if (responseMessage == null) return false;
        
        // verify successful connection to server
        // (expecting Connected message back with Success status)
        if (responseMessage.getType() == MessageType.Connected
        	&& responseMessage.getStatus() == MessageStatus.Success) {
        	return true;
        }
        else {
        	// connection failed
        	System.out.println("\nobject over socket connection failed");
        	return false;
        }
	}	
	public boolean requestLogIn(String username, String password, String credentials) {
		// send LogIn message to server
    	send(new Message(MessageType.LogIn, MessageStatus.Pending, 
    					 username + "," + password + "," + credentials));
    	
    	// read response from server
    	Message responseMessage = receive();
    	if (responseMessage == null) return false;
    	
    	// interpret response: verify successful login
    	if (responseMessage.getType() == MessageType.LogIn 
    		&& responseMessage.getStatus() == MessageStatus.Success) {
    		this.loggedIn = true;
    		this.username = username;
    		return true;
    	}
    	else {
    		return false;
    	}
	}
	public boolean requestRegistry(String username, String password, String credentials) {
		// send Register message to server
    	send(new Message(MessageType.Register, MessageStatus.Pending, 
    					 username + "," + password + "," + credentials));
    	
    	// read response message from server
    	Message responseMessage = receive();
    	if (responseMessage == null) return false;
    	
    	// interpret response: verify successful registry
    	if (responseMessage.getType() == MessageType.Register 
    		&& responseMessage.getStatus() == MessageStatus.Success) {
    		this.loggedIn = true;
    		this.username = username;
    		return true;
    	}
    	else {
    		return false;
    	}
	}
	public boolean requestLogOut() {
		// send LogOut message to server
    	send(new Message(MessageType.LogOut, MessageStatus.Pending));
    	
    	// read response from server
    	Message responseMessage = receive();
    	if (responseMessage == null) return false;
    	
    	// interpret response: verify successful logout
    	if (responseMessage.getType() == MessageType.LogOut 
    		&& responseMessage.getStatus() == MessageStatus.Success) {
    		this.loggedIn = false;
    		return true;
    	}
    	else {
    		return false;
    	}
	}
	public double requestBalanceView() {
		// send BalanceRequest message to server
		send(new Message(MessageType.BalanceRequest, MessageStatus.Pending));
		
        // read response message from server
		Message responseMessage = receive();
		if (responseMessage == null) throw new IllegalStateException("invalid BalanceRequest response");
		
		// interpret response: verify balance successfully fetched
		if (responseMessage.getType() == MessageType.BalanceView 
			&& responseMessage.getStatus() == MessageStatus.Success) {
			// read and return balance
			return Double.parseDouble(responseMessage.getText());
		}
		
		// throw exception if no balance received
		throw new IllegalStateException("invalid BalanceRequest response");
	}
	public double requestBalanceDeposit(Double currency) {
		// send DepositRequest message to server
		send(new Message(MessageType.DepositRequest, MessageStatus.Pending, 
						 String.valueOf(currency)));
		
        // read response message from server
		Message responseMessage = receive();
		if (responseMessage == null) throw new IllegalStateException("invalid DepositRequest response");
		
		// interpret response: verify balance successfully updated
		if (responseMessage.getType() == MessageType.DepositBalance 
			&& responseMessage.getStatus() == MessageStatus.Success) {
			// read and return updated balance
			return Double.parseDouble(responseMessage.getText());
		}
		
		// throw exception if no balance received
		throw new IllegalStateException("invalid DepositRequest response");	
	}
	public double requestBalanceWithdrawal(Double currency) {
		// send WithdrawRequest message to server
		send(new Message(MessageType.WithdrawRequest, MessageStatus.Pending, 
						 String.valueOf(-currency)));
		
        // read response message from server
		Message responseMessage = receive();
		if (responseMessage == null) throw new IllegalStateException("invalid WithdrawRequest response");
		
		// interpret response: verify balance successfully updated
		if (responseMessage.getType() == MessageType.WithdrawBalance 
			&& responseMessage.getStatus() == MessageStatus.Success) {
			// read and return updated balance
			return Double.parseDouble(responseMessage.getText());
		}
		
		// throw exception if no balance received
		throw new IllegalStateException("invalid WithdrawRequest response");
	}
	public boolean joinTable() {
		// send TableJoin message to server
		send(new Message(MessageType.TableJoin, MessageStatus.Pending));
		
        // read response message from server
		Message responseMessage = receive();
		if (responseMessage == null) return false;
		
		// interpret response: verify table successfully joined
		if (responseMessage.getType() == MessageType.TableJoin 
			&& responseMessage.getStatus() == MessageStatus.Success) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean leaveTable() {
		// send TableLeave message to server
		send(new Message(MessageType.TableLeave, MessageStatus.Pending));
		
        // read response message from server
		Message responseMessage = receive();
		if (responseMessage == null) return false;
		
		// interpret response: verify table successfully left
		if (responseMessage.getType() == MessageType.TableLeave 
			&& responseMessage.getStatus() == MessageStatus.Success) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean chooseBet(double bet) {
		// send Bet message to server
		send(new Message(MessageType.Bet, MessageStatus.Pending, 
						 String.valueOf(bet)));
		
        // read response message from server
		Message responseMessage = receive();
		if (responseMessage == null) return false;
		
		// interpret response: verify bet successfully received
		if (responseMessage.getType() == MessageType.Bet 
			&& responseMessage.getStatus() == MessageStatus.Success) {
			return true;
		}
		else {
			return false;
		}
	}	
	public void hit() {
		// send Hit message to server
		send(new Message(MessageType.Hit, MessageStatus.Pending));
	}
	public void stand() {
		// send Stand message to server
		send(new Message(MessageType.Stand, MessageStatus.Pending));
	}
	public int getGameUsers() {
		// send RenderPlayers message to server
		send(new Message(MessageType.RenderPlayers, MessageStatus.Pending));
		
        // read response message from server
		Message responseMessage = receive();
		if (responseMessage == null) throw new IllegalStateException("invalid getGameUSers response");
		
		// interpret response: verify users returned
		if (responseMessage.getType() == MessageType.RenderPlayers 
			&& responseMessage.getStatus() == MessageStatus.Success) {
			return countUsers(responseMessage.getText());
		}
		else {
			throw new IllegalStateException("invalid getGameUSers response");
		}
	}
	
	// ==== PRIVATE METHODS ==== //
	
	private void send(Message msg) {
		try {
			objectOutputStream.writeObject(msg);
			objectOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private Message receive() {
		try {
			return (Message) objectInputStream.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}	
	private int countUsers(String usersStr) {
		int numUsers = 0;
		for (int i = 0; i < 6; i++) {
			if (usersStr.charAt(i) == 1) {
				numUsers++;
			}
		}
		return numUsers;
	}
	
	
	public void displayGameCards() {}
	public void displayGameStatus() {}
	
	
	
	

}