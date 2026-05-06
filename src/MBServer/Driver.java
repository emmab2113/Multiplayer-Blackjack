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
import message.Message;
import MBServer.Server;
/*
READ FIRST

1. type SignIn
2. type TimeOut
3. type TableJoin
*/

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
	        Message serverCom;
	        boolean login = false;
	        boolean expectMessage = false;
	        
	        // Send login message and await response
	        objectOutputStream.writeObject(new Message("login", "pending", "Undefined"));
	        serverCom = ((Message) objectInputStream.readObject());
	        
	        // Communicate until server connection is severed
	        while(true) {     	
	        	System.out.println(serverCom.getType() + ": " + serverCom.getStatus());
        		clientCom = sc.nextLine();	// Type contents of text message to server (or request to logout)
        		expectMessage = false;
        		if (clientCom.compareTo("LogOut") == 0) {	// Logout, sever server connection, and exit program
        			objectOutputStream.writeObject(new Message("LogOut","pending","Undefined"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("TableJoin") == 0) {
        			objectOutputStream.writeObject(new Message("TableJoin","pending","Undefined"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("TableLeave") == 0) {
        			objectOutputStream.writeObject(new Message("TableLeave","pending","Undefined"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("LogIn") == 0) {
        			objectOutputStream.writeObject(new Message("LogIn","pending","playerEpic,password,0"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("Register") == 0) {
        			objectOutputStream.writeObject(new Message("Register","pending","playerEpic,password,0"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("TimeOut") == 0) {
        			objectOutputStream.writeObject(new Message("TimeOut","pending","NA"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("BalanceRequest") == 0) {
        			objectOutputStream.writeObject(new Message("BalanceRequest","pending","NA"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("DepositRequest") == 0) {
        			objectOutputStream.writeObject(new Message("DepositRequest","pending","25.00"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("WithdrawRequest") == 0) {
        			objectOutputStream.writeObject(new Message("WithdrawRequest","pending","-25.00"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("RequestBet") == 0) {
        			objectOutputStream.writeObject(new Message("RequestBet","pending","NA"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("GameAction") == 0) {
        			objectOutputStream.writeObject(new Message("GameAction","pending","NA"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("RenderCard") == 0) {
        			objectOutputStream.writeObject(new Message("RenderCard","pending","NA"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("RenderPlayer") == 0) {
        			objectOutputStream.writeObject(new Message("RenderPlayer","pending","NA"));
        			expectMessage = true;
        		}
        		else if (clientCom.compareTo("StartGame") == 0) {
        			objectOutputStream.writeObject(new Message("StartGame","pending","NA"));
        			expectMessage = true;
        		}
        		else {	// Sent text message to server
        			objectOutputStream.writeObject(new Message("text","pending",clientCom));
        			expectMessage = true;
        		}
        		
        		
        		if (expectMessage) {
        			serverCom = ((Message) objectInputStream.readObject());	// Await response
	        		if (serverCom.getType().compareTo("text") == 0 && serverCom.getStatus().compareTo("success") == 0) {
	        			System.out.println(serverCom.getText());	// Print capitalized text
	        		}
	        		
	        		else if (serverCom.getType().compareTo("GameAction") == 0){
	        			System.out.println(serverCom.getText());
	        			clientCom = sc.nextLine();
	        			if (clientCom.compareTo("Hit") == 0) {
		        			objectOutputStream.writeObject(new Message("Hit","pending","Undefined"));
		        		}
	        			else if (clientCom.compareTo("Stand") == 0) {
	        				objectOutputStream.writeObject(new Message("Stand","pending","Undefined"));
	        			}
	        			else if (clientCom.compareTo("Dealer") == 0) {
	        				objectOutputStream.writeObject(new Message("Dealer","pending","Undefined"));
	        			}
	        			serverCom = ((Message) objectInputStream.readObject());
	        			System.out.println(serverCom.getText());
	        		}
	        		else if (serverCom.getType().compareTo("TableJoin") == 0){
	        			System.out.println(serverCom.getText());
	        		}
	        		else if (serverCom.getType().compareTo("TableLeave") == 0){
	        			System.out.println(serverCom.getText());
	        		}
	        		else if (serverCom.getType().compareTo("Registered") == 0){
	        			System.out.println(serverCom.getText());
	        		}
	        		else if (serverCom.getType().compareTo("BalanceView") == 0){
	        			System.out.println(serverCom.getText());
	        		}
	        		else if (serverCom.getType().compareTo("Deposit") == 0){
	        			System.out.println(serverCom.getText());
	        		}
	        		else if (serverCom.getType().compareTo("Withdraw") == 0){
	        			System.out.println(serverCom.getText());
	        		}
	        		else if (serverCom.getType().compareTo("LogOut") == 0){
	        			System.out.println(serverCom.getText());
	        		}
	        		else if (serverCom.getType().compareTo("RequestBet") == 0){
	        			do {
	        				System.out.println(serverCom.getText());
	        				clientCom = sc.nextLine();
	        				objectOutputStream.writeObject(new Message("Bet","pending",clientCom));
	        				serverCom = ((Message) objectInputStream.readObject());	// Await response
	        			}
	        			while(serverCom.getStatus().compareTo("failure") == 0);
	        			System.out.println(serverCom.getText());
	        		}
	        		else if (serverCom.getType().compareTo("Error") == 0){
	        			System.out.println(serverCom.getText());	// Print capitalized text
	        		}
	        		else if (serverCom.getType().compareTo("RenderCard") == 0){
	        			System.out.println(serverCom.getText());
	        		}
	        		else if (serverCom.getType().compareTo("RenderPlayer") == 0){
	        			System.out.println(serverCom.getText());
	        		}
	        		else if (serverCom.getType().compareTo("StartGame") == 0){
	        			System.out.println(serverCom.getText());
	        		}
        		}
	        }
		}
        catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}