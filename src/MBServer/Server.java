package MBServer;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import enums.ErrorType;
import enums.Message;
import MBClient.Client;

public class Server {
	private static Vector<Table> availableTables;
	private static Vector<Account> accountRegistry;
	private static Vector<Message> messageLog;
	private static ServerSocket server;
	private static int standAt;
	
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Create a ServerSock on localhost:7777
    	availableTables = new Vector<Table>();
    	accountRegistry = new Vector<Account>();
    	messageLog = new Vector<Message>();
    	server = null;
    	standAt = 17;

		try {
			File registryFile = new File("accounts.txt");
			Scanner registryLoader = new Scanner(registryFile);
			while(registryLoader.hasNextLine()) {
				String accountData = registryLoader.nextLine();
				String[] accountDetails = new String[5];
				int detailCounter = 0;
				String detail = "";
				for (int i = 0; i < accountData.length(); i++) {
					if (accountData.charAt(i) == ',') {
						accountDetails[detailCounter] = detail;
						detail = "";
						detailCounter++;
					}
					else {
						detail += accountData.charAt(i);
					}
				}
				accountDetails[detailCounter] = detail;
				detail = "";
				detailCounter++;
				accountRegistry.add(new Account(accountDetails[0],accountDetails[1],accountDetails[2],
						Double.parseDouble(accountDetails[3]),Integer.parseInt(accountDetails[4])));
			}
			
			// server is listening on port 1234
			server = new ServerSocket(1234);
			server.setReuseAddress(true);

			// running infinite loop for getting
			// client request
			while (true) {

				// socket object to receive incoming client
				// requests
				Socket client = server.accept();

				// create a new thread object
				ClientHandler clientSock
					= new ClientHandler(client);

				// This thread will handle the client
				// separately
				new Thread(clientSock).start();
			}
		}
		catch (IOException e) {	// Exception handling
			e.printStackTrace();
		}
		finally {	// Stop listening
			if (server != null) {
				try {
					server.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
    
    public static class ClientHandler implements Runnable {
    	private final Socket clientSocket;
    	private Account account;
    	private boolean stoodOrBust;
    	ObjectOutputStream out = null;
		ObjectInputStream in = null;
    	private Table seatedAt;


    	// Constructor to receive client connection
    	public ClientHandler(Socket socket) throws IOException
    	{
    		this.clientSocket = socket;	
    		
    		// get the outputstream and inputstream of client
			OutputStream outBase = clientSocket.getOutputStream();
			InputStream inBase = clientSocket.getInputStream();
			
			// Create object streams so we can read and write TestMessages to client.
			out = new ObjectOutputStream(outBase);
	        in = new ObjectInputStream(inBase);
	        
	        out.writeObject(new Message("Connected","success","NA"));
    	}

    	// Run on instantiation
    	public void run()
    	{
    		// Initialize variables for client communication
    		boolean login = false;
    		try {

    	        // Communicate until client connection is severed
    			Message line;
    			while ((line = (Message) in.readObject()) != null) {
    				if (login) {
    					if (line.getType().compareTo("LogOut") == 0) {	// Sever client connection on logout
    						login = false;
    						out.writeObject(new Message("Disconnected","success","NA"));
    						clientSocket.close();
    						return;
    					}
    					else if (line.getType().compareTo("text") == 0) {	// Capitalize contents of text Message
    						String capitalInput = line.getText();			// Return new text to client
    						capitalInput = capitalInput.toUpperCase();
    						out.writeObject(new Message("text","success",capitalInput));
    						
    					}
    					else if (line.getType().compareTo("TableJoin") == 0) {	// Capitalize contents of text Message
    						lookForTable();
    					}
    					else if (line.getType().compareTo("TableLeave") == 0) {	// Capitalize contents of text Message
    						removeFromTable();
    					}
    					else if (line.getType().compareTo("MakeTable") == 0) {	// Capitalize contents of text Message
    						makeTable();
    					}
    					else if (line.getType().compareTo("SignIn") == 0) {	// Capitalize contents of text Message
    						String accountInfo = line.getText();
    						String[] accountDetails = new String[3];
    						int detailCounter = 0;
    						String detail = "";
    						for (int i = 0; i < accountInfo.length(); i++) {
    							if (accountInfo.charAt(i) == ',') {
    								if (detailCounter == 2) {
    									break;
    								}
    								accountDetails[detailCounter] = detail;
    								detail = "";
    								detailCounter++;
    							}
    							else {
    								detail += accountInfo.charAt(i);
    							}
    						}
    						accountDetails[detailCounter] = detail;
    						detail = "";
    						detailCounter++;
    						if(logIn(accountDetails[0], accountDetails[1], accountDetails[2])) {
    							out.writeObject(new Message("SignIn","success","NA"));
    						}
    						else {
    							informClientOfError(ErrorType.TypeError);
    						}
    					}
    					else if (line.getType().compareTo("SignOut") == 0) {	// Capitalize contents of text Message
    						account.signOut();
    						out.writeObject(new Message("SignOut","success","NA"));
    					}
    					else if (line.getType().compareTo("Register") == 0) {	// Capitalize contents of text Message
    						String accountInfo = line.getText();
    						String[] accountDetails = new String[3];
    						int detailCounter = 0;
    						String detail = "";
    						for (int i = 0; i < accountInfo.length(); i++) {
    							if (accountInfo.charAt(i) == ',') {
    								if (detailCounter == 2) {
    									break;
    								}
    								accountDetails[detailCounter] = detail;
    								detail = "";
    								detailCounter++;
    							}
    							else {
    								detail += accountInfo.charAt(i);
    							}
    						}
    						accountDetails[detailCounter] = detail;
    						detail = "";
    						detailCounter++;
    						if(logIn(accountDetails[0], accountDetails[1], accountDetails[2])) {
    							out.writeObject(new Message("SignIn","success","NA"));
    						}
    						else {
    							informClientOfError(ErrorType.TypeError);
    						}
    						if (register(accountDetails[0], accountDetails[1], accountDetails[2])) {
    							out.writeObject(new Message("Registered","success","NA"));
    						}
    						else {
    							informClientOfError(ErrorType.TypeError);
    						}
    					}
    					else if (line.getType().compareTo("BalanceRequest") == 0) {
	    					String playerBalance = String.valueOf(getPlayerBalance());
	    					out.writeObject(new Message("BalanceView","success", playerBalance));
	    				}
    					else if (line.getType().compareTo("DepositRequest") == 0) {
	    					if (addToPlayerBalance(Double.parseDouble(line.getText()))) {
	    						String playerBalance = String.valueOf(getPlayerBalance());
	        					out.writeObject(new Message("Deposit","success", playerBalance));
	    					}
	    					informClientOfError(ErrorType.TypeError);
	    				}
	    				else if (line.getType().compareTo("WithdrawRequest") == 0) {
	    					if (addToPlayerBalance(Double.parseDouble(line.getText()))) {
	    						String playerBalance = String.valueOf(getPlayerBalance());
	        					out.writeObject(new Message("Withdraw","success", playerBalance));
	    					}
	    					informClientOfError(ErrorType.TypeError);
	    				}
    				}
    				else {	// Only listen for login TestMessages if client is not logged in
    					if (line.getType().compareTo("login") == 0) {
    						login = true;
    						out.writeObject(new Message("login","success","success"));
    					}
    				}
    				collectMessage(line);
    			}
    		}
    		catch (IOException | ClassNotFoundException e) {	// Exception handling
    			e.printStackTrace();
    		}
    		finally {	// Attempt to close client connection
    			try {
    				if (out != null) {
    					out.close();
    				}
    				if (in != null) {
    					in.close();
    					clientSocket.close();
    				}
    			}
    			catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	public void lookForTable() {
    		// Do not let client look for table if timed out
    		if (account.isTimedOut()) {
    			informClientOfError(ErrorType.TypeError);
    			return;
    		}
    		
    		// Join table
//    		writeMessage(new Message("GetTable","success","table info"));
    		seatedAt = availableTables.get(0);
    		seatedAt.addUserToTable(this);
    	}
    	public void makeTable() {
    		// Add a new table
    		availableTables.add(new Table());
    	}
    	public void timeOut() {
    		account.setTimeOut(300);
    	}
    	public boolean register(String username, String password, String credentials) {
    		for (Account existingAccount: accountRegistry) {
    			if (existingAccount.validate(username, password, credentials)) {
    				existingAccount.signOut();
    				return false;
    			}
    		}
    		Account newAccount = new Account(username, password, credentials);
    		accountRegistry.add(newAccount);
    		account = newAccount;
    		return newAccount.validate(username, password, credentials);
    	}
    	public boolean logIn(String username, String password, String credentials) {
    		for (Account existingAccount: accountRegistry) {
    			if (existingAccount.validate(username, password, credentials)) {
    				account = existingAccount;
    				return true;
    			}
    		}
    		return false;
    	}
    	public double getPlayerBalance() {
    		return account.getBalance();
    	}
    	public boolean chargePlayerBalance(double currency) {
    		if (currency > 0) {
    			return false;
    		}
    		return account.modifyBalance(currency);
    	}
    	public boolean addToPlayerBalance(double currency) {
    		if (currency < 0) {
    			return false;
    		}
    		return account.modifyBalance(currency);
    	}
    	
    	public void informClientOfError(ErrorType errorType) {
    		if (errorType == ErrorType.TypeError) {
				writeMessage(new Message("Error","failure","timedOutResponse"));
			}
    	}
    	
    	public void askForBets() {
    		try {
    			Message line = new Message();
				out.writeObject(new Message("RequestBet","success","NA"));
				while ((line = (Message) in.readObject()) != null) {
					if (line.getType().compareTo("Bet") == 0) {
						if (account.setBet(Double.parseDouble(line.getText()))) {
							return;
						}
					}
					else {
						informClientOfError(ErrorType.TypeError);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	public void removeFromTable() {
    		seatedAt = null;
    		if (true) {
    			timeOut();
    		}
    	}
    	public void askForAction() {
    		try {
    			Message line = new Message();
				out.writeObject(new Message("GameAction","success","Choose Hit or Stand"));
				if ((line = (Message) in.readObject()) != null) {
					if (line.getType().compareTo("Stand") == 0) {
						standRequest();
					}
					if (line.getType().compareTo("Hit") == 0) {
						hitRequest();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	public void hitRequest() {
    		Card card = seatedAt.drawCard();
    		account.receiveCard(card);
    		checkRanks();
    	}
    	public void standRequest() {
    		stoodOrBust = true;
    		writeMessage(new Message("Hit","success","You have stood"));
    	}
    	public void addCard(Card card) {
    		account.receiveCard(card);
    	}
    	public void restartGame() {
    		account.resetCardsAndBet();
    	}
    	public void getGameUsers() {
    		Boolean[] isSeated = new Boolean[6];
    		String seatedUsers = "";
    		for (Boolean seat: isSeated) {
    			if (seat) {
    				seatedUsers += "1";
    			}
    			seatedUsers += "0";
    		}
    		try {
				out.writeObject(new Message("RenderPlayer","success",seatedUsers));
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	public void getGameCards() {
    		String allRanks = "";
    		Vector<Card> hand = account.getCards(); // Temporary, should pull from table
    		for (Card card: hand) {
    			allRanks += card.getSuit();
    			allRanks += card.getRank();
    		}
    		try {
				out.writeObject(new Message("RenderCard","success",allRanks));
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	public void checkRanks() {
    		int aces = 0;
    		int score = 0;
    		String curRank = "";
    		String results = "";
    		Vector<Card> hand = account.getCards();
    		for (Card card: hand) {
    			curRank = card.getRank();
    			if (curRank == "J" || curRank == "Q" || curRank == "K") {
    				score += 10;
    			}
    			else if (curRank == "A"){
    				score += 11;
    				aces++;
    			}
    			else {
    				score += Integer.parseInt(card.getRank());
    			}
    		}
    		results += "Your hand has a score of " + score + "\n";
    		results += "You have " + aces + " in your hand\n";
    		if (score > 21) {
				while (aces > 0 && score > 21) {
					score -= 10;
					aces -= 1;
				}
				if (score > 21) {
					stoodOrBust = true;
					results += "You have busted";
				}
			}
    		writeMessage(new Message("Hit","success",results));
    	}
    	public void save() { // Non-functioning at this moment
    		File registryFile = new File("accounts.txt");
			Scanner registryLoader = null;
			BufferedWriter registryUpdater = null;
			Boolean foundAccount = false;
			try {
				registryLoader = new Scanner(registryFile);
				registryUpdater = new BufferedWriter(new FileWriter("accounts.txt"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			while(registryLoader.hasNextLine()) {
				String accountData = registryLoader.nextLine();
				if (foundAccount) {
					break;
				}
				String[] accountDetails = new String[3];
				int detailCounter = 0;
				String detail = "";
				for (int i = 0; i < accountData.length(); i++) {
					if (accountData.charAt(i) == ',') {
						accountDetails[detailCounter] = detail;
						detail = "";
						detailCounter++;
					}
					else {
						detail += accountData.charAt(i);
					}
				}
				accountDetails[detailCounter] = detail;
				detail = "";
				detailCounter++;
				if(account.validate(accountDetails[0], 
						accountDetails[1], accountDetails[2])) {
					accountData = accountDetails[0] + "," + accountDetails[1] + ',' + accountDetails[2] + ",";
					accountData += account.getBalance() + ",";
					accountData += account.getTimeOut();
				}
				try {
					registryUpdater.write(accountData);
					registryUpdater.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}
    	public boolean getStoodOrBust() {
    		return stoodOrBust;
    	}
    	public synchronized void collectMessage(Message message) {
    		messageLog.add(message);
    	}
    	public void writeMessage(Message message) {
    		// write and record messages sent to client
    		try {
				out.writeObject(message);
				collectMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
}
