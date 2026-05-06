package MBServer;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import enums.ErrorType;
import message.Message;
import MBClient.Client;

public class Server {
	private static Vector<Table> availableTables;
	private static Vector<Account> accountRegistry;
	private static Vector<Message> messageLog;
	private static ServerSocket server;
	private static int standAt;
	
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Initialize variables
    	availableTables = new Vector<Table>();
    	accountRegistry = new Vector<Account>();
    	messageLog = new Vector<Message>();
    	server = null;
    	standAt = 17;

		try {
			// Initialize accountRegistry
			// By first reading the account registry file
			File registryFile = new File("accounts.txt");
			Scanner registryLoader = new Scanner(registryFile);
			while(registryLoader.hasNextLine()) { // Decipher information fields separated by commas, line by line
				String accountData = registryLoader.nextLine();
				String[] accountDetails = new String[5];
				int detailCounter = 0;
				String detail = "";
				for (int i = 0; i < accountData.length(); i++) { // Record detail
					if (accountData.charAt(i) == ',') { // And prepare to receive new one
						accountDetails[detailCounter] = detail;
						detail = "";
						detailCounter++;
					}
					else {
						detail += accountData.charAt(i);
					}
				}
				accountDetails[detailCounter] = detail;	// Account for last detail
				detail = "";
				detailCounter++;
				// Add account to registry
				accountRegistry.add(new Account(accountDetails[0],accountDetails[1],Boolean.parseBoolean(accountDetails[2]),
						Double.parseDouble(accountDetails[3]),Integer.parseInt(accountDetails[4])));
			}
			
			// Server is listening on port 1234
			server = new ServerSocket(1234);
			server.setReuseAddress(true);

			// Running infinite loop for getting
			// Client request
			while (true) {

				// Socket object to receive incoming client
				// Requests
				Socket client = server.accept();

				// Create a new thread object
				ClientHandler clientSock
					= new ClientHandler(client);

				// This thread will handle the client
				// Separately
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
    	private ObjectOutputStream out = null;
		private ObjectInputStream in = null;
    	private Table seatedAt;

    	// ADD THIS FOR TESTING PURPOSES ONLY
    	protected ClientHandler() {
			this.clientSocket = null;
			// Don't initialize streams or anything else
		}

    	// Constructor to receive client connection
    	public ClientHandler(Socket socket) throws IOException
    	{
    		this.clientSocket = socket;	
    		
    		// get the outputstream and inputstream of client
			OutputStream outBase = clientSocket.getOutputStream();
			InputStream inBase = clientSocket.getInputStream();
			
			// Create object streams so we can read and write Messages to client.
			out = new ObjectOutputStream(outBase);
	        in = new ObjectInputStream(inBase);
	        
	        out.writeObject(new Message("Connected","success","NA"));
    	}

    	// Run on instantiation
    	public void run()
    	{
    		// Initialize variables for client communication
    		try {

    	        // Communicate until client connection is severed
    			Message line;
    			while ((line = (Message) in.readObject()) != null) { // Act based on user input
    				if (line.getType().compareTo("LogOut") == 0) {	// Log user out of account
						account.signOut();
						save();
						account = null;
						writeMessage(new Message("LogOut","success","NA"));
					}
					else if (line.getType().compareTo("text") == 0) {	// Capitalize contents of text Message (Driver fail-safe)
						String capitalInput = line.getText();			// Return capitalized text to client
						capitalInput = capitalInput.toUpperCase();
						writeMessage(new Message("text","success",capitalInput));
						
					}
					else if (line.getType().compareTo("TableJoin") == 0) {	// Look for and join table if not already at one
						if (seatedAt == null) {
							lookForTable();
						}
						else {
							informClientOfError(ErrorType.AlreadyAtTable);	// Inform user they're already seated
						}
					}
					else if (line.getType().compareTo("TableLeave") == 0) {	// Remove user from table
						removeFromTable();
					}
					else if (line.getType().compareTo("LogIn") == 0) {	// Log user into account
						String accountInfo = line.getText();
						String[] accountDetails = new String[3]; // Decipher message contents
						int detailCounter = 0;
						String detail = "";
						for (int i = 0; i < accountInfo.length(); i++) {
							if (accountInfo.charAt(i) == ',') {
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
						if(logIn(accountDetails[0], accountDetails[1], Boolean.parseBoolean(accountDetails[2]))) {
							writeMessage(new Message("LogIn","success","NA")); // Log in if details are correct
						}
						else {
							informClientOfError(ErrorType.InvalidUsernameOrPassword); // Inform client of incorrect details
						}
					}
					else if (line.getType().compareTo("TimeOut") == 0) {	// Time out user
						if (account.isTimedOut()) {
							informClientOfError(ErrorType.TimedOut);
						}
						timeOut();
						writeMessage(new Message("text","success","Time out started"));
					}
					else if (line.getType().compareTo("Register") == 0) {	// Register new account for user
						String accountInfo = line.getText();
						String[] accountDetails = new String[3];
						int detailCounter = 0;
						String detail = "";
						for (int i = 0; i < accountInfo.length(); i++) { // Decipher message contents
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
						if (register(accountDetails[0], accountDetails[1], Boolean.parseBoolean(accountDetails[2]))) {
							writeMessage(new Message("Registered","success","NA")); // Register account if account doesn't yet exist
						}
						else {
							informClientOfError(ErrorType.AccountAlreadyExists); // Inform user account already exists
						}
					}
					else if (line.getType().compareTo("BalanceRequest") == 0) { // Show user their balance
    					String playerBalance = String.valueOf(getPlayerBalance()); // Put balance in string
    					writeMessage(new Message("BalanceView","success", playerBalance)); // Deliver balance to user
    				}
					else if (line.getType().compareTo("DepositRequest") == 0) { // Deposit into user balance
    					if (addToPlayerBalance(Double.parseDouble(line.getText()))) { // Reinterpret message contents and try to deposit it
    						String playerBalance = String.valueOf(getPlayerBalance()); // Put new balance in string
    						writeMessage(new Message("Deposit","success", playerBalance)); // Deliver new balance to user
    					}
    					else {
    						informClientOfError(ErrorType.CannotDeposit); // Inform user they cannot deposit that amount
    					}
    				}
    				else if (line.getType().compareTo("WithdrawRequest") == 0) { // Withdraw from user balance
    					if (chargePlayerBalance(Double.parseDouble(line.getText()))) { // Reinterpret message contents and try to withdraw it
    						String playerBalance = String.valueOf(getPlayerBalance()); // Put new balance in string
    						writeMessage(new Message("Withdraw","success", playerBalance)); // Deliver new balance to user
    					}
    					else {
    						informClientOfError(ErrorType.CannotWithdraw); // Inform user they cannot withdraw that amount
    					}
    				}
    				else if (line.getType().compareTo("RequestBet") == 0) {	// Set bet for user
						askForBets(); // Ask user for bets and set it
						writeMessage(new Message("Bet", "success", "Your wager is set")); // Inform user that bet is set
					}
    				else if (line.getType().compareTo("GameAction") == 0) {	// Ask user to hit or stand
						askForAction(); // Ask user to hit or stand and act accordingly
					}
    				else if (line.getType().compareTo("RenderCard") == 0) {	// Send user info on cards used
						getGameCards();
					}
    				else if (line.getType().compareTo("RenderPlayer") == 0) {	// Send user info on players at table
						getGameUsers();
					}
    				else if (line.getType().compareTo("StartGame") == 0) {	// Start game at table
    					if (seatedAt.hasDealer() && seatedAt.getPlayerCount() > 0) { // Verify game can start
    						seatedAt.startGame();
    					}
    					writeMessage(new Message("StartGame","success", "Game started")); // Inform user that a game has started
					}
    				collectMessage(line); // Log message sent by user
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
    	
    	public synchronized void lookForTable() {
    		// Do not let client look for table if timed out
    		if (account.isTimedOut()) {
    			informClientOfError(ErrorType.TimedOut);
    			return;
    		}
    		
    		// Prepare to look for table
    		boolean foundASeat = false;
    		if (availableTables.size() == 0) { // Add table if none exist
    			makeTable();
    		}
    		
    		if (isDealer()) { // For dealers
    			for (int i = 0; i < availableTables.size(); i++) { // Look for table without dealer
        			if (foundASeat) { // No need to search further
        				break;
        			}
        			seatedAt = availableTables.get(i);
        			if (!seatedAt.hasDealer()) { // Join vacant table
        				seatedAt.addUserToTable(this);
        				foundASeat = true;
        			}
        		}
    			if (!foundASeat) { // Make table to join if no vacancies
    				makeTable();
    				seatedAt = availableTables.elementAt(0);
    				seatedAt.addUserToTable(this);
    				foundASeat = true;
    			}
    		}
    		
    		else { // For players
    			
    			// Prepare to find least populated table(s)
    			int leastTablePopulation = 7;
    			Vector<Integer> tablePopulation = new Vector<Integer>();
    			for (int i = 0; i < availableTables.size(); i++) { // Create list of least populated tables
        			seatedAt = availableTables.get(i);
        			tablePopulation.add(seatedAt.getPlayerCount());
        			if (leastTablePopulation > seatedAt.getPlayerCount()) {
        				leastTablePopulation = seatedAt.getPlayerCount();
        			}
        		}
    			
    			if (leastTablePopulation == 6) { // Make table to join if no vacancies
    				makeTable();
    				leastTablePopulation = 0;
    			}
    			
    			for (int i = 0; i < availableTables.size(); i++) { // Join table from list
    				if (foundASeat) { // No need to search further
        				break;
        			}
    				// Join table if it has room and it is one of the least populated tables
    				if (tablePopulation.get(i) < 6 && tablePopulation.get(i) == leastTablePopulation) {
    					foundASeat = true;
    					seatedAt = availableTables.get(i);
    					seatedAt.addUserToTable(this);
    				}
    			}
    		}
    		writeMessage(new Message ("TableJoin", "success", "NA")); // Tell user they joined table
    	}
    	
    	public synchronized void makeTable() { // Add a new table
    		availableTables.insertElementAt(new Table(), 0);
    	}
    	
    	public void timeOut() { // Time out user for 5 minutes
    		account.setTimeOut(300);
    	}
    	
    	public synchronized boolean register(String username, String password, boolean credentials) { // Register new account for user
    		for (Account existingAccount: accountRegistry) { // Check if account already exists
    			if (existingAccount.getUsername().compareTo(username) == 0) {
    				return false;
    			}
    		}
    		
    		// Prepare to add account to registry
			BufferedWriter registryUpdater = null;
			try {
				registryUpdater = new BufferedWriter(new FileWriter("accounts.txt", true));
				registryUpdater.newLine();
				registryUpdater.append(username + "," + password + "," + credentials + ",0.00,0"); // Account added to file
				
				registryUpdater.close();
				
				Account newAccount = new Account(username, password, credentials); //  Account added to registry mid-exection
	    		accountRegistry.add(newAccount);
	    		account = newAccount;
	    		return newAccount.validate(username, password, credentials); // Sign into account
			} catch (IOException e) { // Exception handling
				e.printStackTrace();
			}

    		return false;
    	}
    	
    	public boolean logIn(String username, String password, boolean credentials) { // Log user into an account
    		for (Account existingAccount: accountRegistry) { // Looks for account within registry
    			if (existingAccount.validate(username, password, credentials)) { // Validates account details
    				account = existingAccount;
    				return true;
    			}
    		}
    		return false; // Details not validated
    	}
    	
    	public double getPlayerBalance() { // Return balance of player's account
    		return account.getBalance();
    	}
    	
    	public boolean chargePlayerBalance(double currency) { // removes from player's balance
    		if (currency > 0) { // Ensures player is being charged
    			return false;
    		}
    		return account.modifyBalance(currency);
    	}
    	
    	public boolean addToPlayerBalance(double currency) { // adds to player's balance
    		if (currency < 0) { // Ensures player is not being charged
    			return false;
    		}
    		return account.modifyBalance(currency);
    	}
    	
    	public void informClientOfError(ErrorType errorType) { // Informs client of error in processing an action
    		if (errorType == ErrorType.AlreadyAtTable) { // TableJoin fail
				writeMessage(new Message("Error","failure","You cannot join another table until you leave your current one"));
			}
    		if (errorType == ErrorType.AccountAlreadyExists) { // Register fail
				writeMessage(new Message("Error","failure","Account already exists in the register"));
			}
    		if (errorType == ErrorType.InvalidUsernameOrPassword) { // LogIn fail
				writeMessage(new Message("Error","failure","Either your username or password was incorrect"));
			}
    		else if (errorType == ErrorType.TimedOut) { // TableJoin fail due to being timed out
    			writeMessage(new Message("Error","failure", "Seconds remaining for timeout: " + account.getTimeOut()));
    		}
    		else if (errorType == ErrorType.CannotDeposit) { // Deposit fail
    			writeMessage(new Message("Error","failure", "You cannot deposit this amount: " + account.getBalance()));
    		}
    		else if (errorType == ErrorType.CannotWithdraw) { // Withdraw fail
    			writeMessage(new Message("Error","failure", "You have insufficient funds: " + account.getBalance()));
    		}
    		else { // Generic fail-safe error
    			writeMessage(new Message("Error","failure","Error: cannot process action"));
    		}
    	}
    	
    	public void askForBets() { // Ask player for bets and attempt to process it
    		try {
    			Message line = new Message(); // Record of player response
				writeMessage(new Message("RequestBet","success","Place your bets")); // Ask for bets
				while ((line = (Message) in.readObject()) != null) { // Repeatedly ask until valid response
					if (line.getType().compareTo("Bet") == 0) {
						if (account.setBet(Double.parseDouble(line.getText()))) { // Reinterpret and set player's bet
							return;
						}
					}
					else {
						informClientOfError(ErrorType.CannotWithdraw); // Inform user their bet is too high
					}
				}
			} catch (IOException e) { // Exception handling
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	
    	public void removeFromTable() { // Remove user from table
    		seatedAt.queueLeave(this); // Have table remove user from it first
    		seatedAt = null; // Sever connection to table
    		writeMessage(new Message ("TableLeave", "success", "NA")); // Tell user they left the table
    	}
    	
    	public void askForAction() { // Ask user for game action and process response
    		try {
    			Message line = new Message(); // Record of player response
				writeMessage(new Message("GameAction","success","Choose Hit or Stand")); // Ask user for game action
				if ((line = (Message) in.readObject()) != null) { // Wait for user to respond
					if (line.getType().compareTo("Stand") == 0) { // Player chooses to stand
						standRequest();
					}
					if (line.getType().compareTo("Hit") == 0) { // Player chooses to hit
						hitRequest();
					}
					if (line.getType().compareTo("Dealer") == 0) { // Dealer's turn is taken automatically
						if (isDealer()) {
							while (checkRanks() < standAt) {
								hitRequest();
							}
							standRequest();
						}
					}
				}
			} catch (IOException e) { // Exception handling
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	
    	public void hitRequest() { // Hit
    		Card card = seatedAt.drawCard(); // Draw card from table
    		addCard(card);
    		checkRanks(); // Update user on their score
    	}
    	
    	public void standRequest() { // Stand
    		stoodOrBust = true;
    		writeMessage(new Message("Hit","success","You have stood")); // Tell user they have stood
    	}
    	
    	public void addCard(Card card) { // Add card to user's hand
    		account.receiveCard(card);
    	}
    	
    	public void restartGame() { // Reset user's hand and bet
    		account.resetCardsAndBet();
    	}
    	
    	public void restartGame(boolean payOut) { // Reset user's hand and bet and gives them winnings
    		account.resetCardsAndBet(payOut);
    	}
    	
    	public void getGameUsers() { // Get all users at a table for GUI to render them
    		boolean[] isSeated = seatedAt.getVacancies(); // Get list of seats not filled at the table
    		String seatedUsers = "";
    		for (boolean vacant: isSeated) { // Invert that list
    			if (vacant) {
    				seatedUsers += "0";
    			}
    			else {
    				seatedUsers += "1";
    			}
    		}
    		writeMessage(new Message("RenderPlayer","success",seatedUsers)); // Tell client to render users
    	}
    	
    	public void getGameCards() { // Get all cards drawn during a game for GUI to render them
    		String allCards = "";
    		Vector<Card>[] drawnCards = seatedAt.getAllUsersHands(); // Get all cards drawn and who drawn them
    		for (int i = 0; i < drawnCards.length; i++) {
    			if (drawnCards[i]!= null) {
    				allCards += ":" + i + ":";
        			for (Card card: drawnCards[i]){
            			allCards += card.getSuit();
            			allCards += card.getRank();
            			allCards += ",";
        			}
    			}
    		}
    		writeMessage(new Message("RenderCard","success",allCards)); // Tell client to render cards
    	}
    	
    	public int checkRanks() { // Check ranks of hand and return score
    		// Prepare the check
    		int aces = 0;
    		int score = 0;
    		String curRank = "";
    		String results = "";
    		Vector<Card> hand = account.getCards();
    		
    		for (Card card: hand) { // Get ranks of each card in hand and record card information
    			curRank = card.getRank();
    			if (curRank == "J" || curRank == "Q" || curRank == "K") { // Face card detected
    				score += 10;
    			}
    			else if (curRank == "A"){ // Ace detected
    				score += 11;
    				aces++;
    			}
    			else { // Regular card detected
    				score += Integer.parseInt(card.getRank());
    			}
    			if (results.length() == 0) { // Format record
    				results += ", ";
    			}
    			results += card.getRank() + card.getSuit();
    		}

    		if (score > 21) { // Check if player busted
				while (aces > 0 && score > 21) { // Adjust based on aces
					score -= 10;
					aces -= 1;
				}
				if (score > 21) {
					stoodOrBust = true;
					results += "\nYou have busted";
				}
			}
    		writeMessage(new Message("Hit","success",results)); // Tell user what cards they have
			return score; // Return score
    	}
    	
    	public synchronized void save() { // Prepare to save account info to registry
    		File registryFile = new File("accounts.txt");
			Scanner registryLoader = null;
			BufferedWriter registryUpdater = null;
			String newRegistry = "";
			try {
				registryLoader = new Scanner(registryFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Read through each entry in registry
			while(registryLoader.hasNextLine()) {
				String accountData = registryLoader.nextLine();
				System.out.println(accountData);
				String[] accountDetails = new String[5];
				int detailCounter = 0;
				String detail = "";
				for (int i = 0; i < accountData.length(); i++) { // Decipher information fields
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
				// When this account is found, update timeOut and balance
				if (account.getUsername().compareTo(accountDetails[0]) == 0) {
					accountData = accountDetails[0] + "," + accountDetails[1] + ',' + accountDetails[2] + ",";
					accountData += account.getBalance() + ",";
					accountData += account.getTimeOut();
				}
				if (registryLoader.hasNextLine()) {
					accountData += "\n";
				}
				newRegistry += accountData; // Record account details
			}
			try {
				registryLoader.close(); // Rewrite account registry file, including updated account
				registryUpdater = new BufferedWriter(new FileWriter("accounts.txt"));
				registryUpdater.write(newRegistry);
				registryUpdater.close();
			} catch (IOException e) { // Exception handling
				e.printStackTrace();
			}
			
    	}
    	
    	public boolean getStoodOrBust() { // Return if user cannot take their turn
    		return stoodOrBust;
    	}
    	
    	public synchronized void collectMessage(Message message) { // Log message
    		messageLog.add(message);
    	}
    	
    	public void writeMessage(Message message) { // write and record messages sent to client
    		try {
				out.writeObject(message);
				collectMessage(message);
			} catch (IOException e) { // Exception handling
				e.printStackTrace();
			}
    	}
    	
    	public boolean isDealer() { // Returns if user is dealer
    		return account.getCredentials();
    	}
    	
    	public void cancelGame() { // Cancel game and remove everyone from the table
    		if (isDealer()) {
    			seatedAt.dealerCancelledGame();
    		}
    	}
    	
    	public Vector<Card> getHand() { // Return user's hand
    		return account.getCards();
    	}
    }
}
