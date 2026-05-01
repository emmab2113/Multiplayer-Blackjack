package MBClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

import enums.ErrorType;

public class Client {
	private boolean loggedIn;
	private boolean[] seatedPlayers;
	private String IPAddress;
	private String username;
	private Vector<Message> messageLog;
	
	public static void main(String[] args) {
		try {
			// initialize port/host with default values
			int port = 1234;
			String host = InetAddress.getLocalHost().getHostAddress(); // host default to inet address
			
	        // connect to the ServerSocket at host:port
	        Socket socket = new Socket(host, port);
	        System.out.println("\nConnected to " + host + ":" + port);
	        
	        // establish output stream to server
	        // get output stream from connected socket
	        OutputStream outputStream = socket.getOutputStream();
	        // create an ObjectOutputStream to send an object through outputStream
	        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
	        
	        // establish input stream from server
	        // get input stream from connected socket
	        InputStream inputStream = socket.getInputStream();
	        // create an ObjectInputStream to receive an object through inputStream
	        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
	        
	        // send Connected message
	        
	        // receive Connected message back with Success status
	        
	        // user choices: login or register
	        
	        // if user choose login,
	        // get user/pass
	        // send Login message containing user/pass (status: Pending)
	        // receive Login message back from server, check status
	        // if status Success, move to main menu depending on credentials
	        // if status Fail, notify user of failure, re-prompt user/pass
	        
	        // if user choose register,
	        // get credential/user/pass (validate)
	        // send Register message containing credential/user/pass
	        // receive Register message back from server, check status
	        // if status Success, move to main menu depending on credentials
	        
	        /* player main menu */
	        
	        // user choices: balance, play, or logout
	        
	        // if user choose balance,
	        // send BalanceRequest message
	        // wait for BalanceView message received containing balance
	        // display balance
	        // user choices: deposit, withdraw, or back
	        // if choose deposit, 
	        //		get deposit amount (validate)
	        //		send DepositRequest message containing deposit amount
	        // 		wait for Deposit message received with Success status containing new balance
	        //		update balance display
	        // if choose withdraw,
	        //		get withdraw amount (validate)
	        //		send WithdrawRequest message containing withdraw amount 	
	        // 		wait for Withdraw message received containing new balance, check status
	        //		if status Success, update balance display
	        // 		if status Fail, notify user of failure, do not update balance display
	        // if choose back,
	        // 		return to main menu
	        
	        // if user choose play,
	        // move to lobby screen (available tables loading)
	        // send GetTables message to server with Pending status
	        // wait for GetTables message back from server with Success status
	        // read available tables from message into list
	        // display available tables in clickable list on lobby screen
	        // user choices: choose table or back
	        // if choose table,
	        // 		send TableJoin message containing desired table
	        //		wait for TableJoin message received back, check status
	        //		if status Success, 
	        // 			save tableID
	        //			move to player game screen
	        //		if status Fail, 
	        //			notify user of failure 
	        //			remove table from list, update display
	        // 			prompt for a different table
	        // 			restart from "send TableJoin message"
	        // if choose back,
	        //		return to main menu
	        
	        // if user choose logout,
	        // send Logout message
	        // wait for Logout message back from server, check status
	        // if status Success, return to open screen
	        
	        /* dealer main menu */ 
	        
	        // user choices: host table or logout
	        
	        // if user choose host table,
	        // send hostTable message
	        // wait for hostTable message back from server, check status
	        // if status Success, move to dealer game screen
	        // if status Fail, ?
	        
	        // if user choose logout,
	        // send Logout message
	        // wait for Logout message back from server, check status
	        // if status Success, return to open screen
	        
	        /* player game screen */
	        
	        
	        
	        /* dealer game screen */
	        
	        
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void joinTable() {}
	public void leaveTable() {}
	public void requestRegistry(String username, String password, String credentials) {}
	public void requestLogIn(String username, String password, String credentials) {}
	public void requestBalanceDisplay() {}
	public void requestBalanceDeposit(Double currency) {}
	public void requestBalanceWithdrawal(Double currency) {}
	public void displayError(ErrorType errorType) {}
	public double chooseChips() {}
	public void hit() {}
	public void stand() {}
	public void terminateSession() {}
	public void establishConnection() {}
	public void displayGameUsers() {}
	public void displayGameCards() {}
	public void displayGameStatus() {}
	private void getIPAddress() {}
}