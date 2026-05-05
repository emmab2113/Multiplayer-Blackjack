package MBServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import enums.ErrorType;
import enums.MessageStatus;
import enums.MessageType;
import message.Message;
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
				accountRegistry.add(new Account(accountDetails[0],accountDetails[1],Boolean.parseBoolean(accountDetails[2]),
						Double.parseDouble(accountDetails[3]),Integer.parseInt(accountDetails[4])));
			}
			
			// server is listening on port 1234
			server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress(1234));

			System.out.println("server listening");
			
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
    	private ObjectOutputStream out = null;
		private ObjectInputStream in = null;
    	private Table seatedAt;


    	// Constructor to receive client connection
    	public ClientHandler(Socket socket) throws IOException
    	{
    		this.clientSocket = socket;	
    		
    		// establish both out streams first
			OutputStream outBase = clientSocket.getOutputStream();
			out = new ObjectOutputStream(outBase);
			
			// then establish both in streams
			InputStream inBase = clientSocket.getInputStream();
			in = new ObjectInputStream(inBase);
	        
	        System.out.println("client connected");
    	}

    	// Run on instantiation
    	public void run()
    	{
    		// Initialize variables for client communication
    		try {

    	        // Communicate until client connection is severed
    			Message line;
    			while ((line = (Message) in.readObject()) != null) {
    				if (line.getType() == MessageType.Connected){
    					out.writeObject(new Message(MessageType.Connected, MessageStatus.Success));
    					out.flush();
    				}
    				else if (line.getType() == MessageType.LogOut) {	// Sever client connection on logout
						account.signOut();
						save();
						account = null;
						out.writeObject(new Message(MessageType.LogOut, MessageStatus.Success));
						out.flush();
					}
					else if (line.getType() == MessageType.TableJoin) {	// Capitalize contents of text Message
						if (seatedAt == null) {
							lookForTable();
						}
						else {
							informClientOfError(ErrorType.TypeError);
						}
					}
					else if (line.getType() == MessageType.TableLeave) {	// Capitalize contents of text Message
						removeFromTable();
					}
					else if (line.getType() == MessageType.LogIn) {	// Capitalize contents of text Message
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
						if(logIn(accountDetails[0], accountDetails[1], Boolean.parseBoolean(accountDetails[2]))) {
							out.writeObject(new Message(MessageType.LogIn, MessageStatus.Success));
							out.flush();
						}
						else {
							informClientOfError(ErrorType.TypeError);
						}
					}
					else if (line.getType() == MessageType.TimeOut) {	// Capitalize contents of text Message
						if (account.isTimedOut()) {
							informClientOfError(ErrorType.TimedOut);
						}
						timeOut();
						writeMessage(new Message(MessageType.TimeOut, MessageStatus.Success));
					}
					else if (line.getType() == MessageType.Register) {	// Capitalize contents of text Message
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
						if (register(accountDetails[0], accountDetails[1], Boolean.parseBoolean(accountDetails[2]))) {
							writeMessage(new Message(MessageType.Register, MessageStatus.Success));
						}
						else {
							informClientOfError(ErrorType.TypeError);
						}
					}
					else if (line.getType() == MessageType.BalanceRequest) {
    					String playerBalance = String.valueOf(getPlayerBalance());
    					writeMessage(new Message(MessageType.BalanceView, MessageStatus.Success, playerBalance));
    				}
					else if (line.getType() == MessageType.DepositRequest) {
    					if (addToPlayerBalance(Double.parseDouble(line.getText()))) {
    						String playerBalance = String.valueOf(getPlayerBalance());
    						writeMessage(new Message(MessageType.DepositBalance, MessageStatus.Success, playerBalance));
    					}
    					else {
    						informClientOfError(ErrorType.TypeError);
    					}
    				}
    				else if (line.getType() == MessageType.WithdrawRequest) {
    					if (chargePlayerBalance(Double.parseDouble(line.getText()))) {
    						String playerBalance = String.valueOf(getPlayerBalance());
    						writeMessage(new Message(MessageType.WithdrawBalance, MessageStatus.Success, playerBalance));
    					}
    					else {
    						informClientOfError(ErrorType.TypeError);
    					}
    				}
    				else if (line.getType() == MessageType.RequestBet) {	// Capitalize contents of text Message
						askForBets();
						writeMessage(new Message(MessageType.Bet, MessageStatus.Success));
					}
    				else if (line.getType() == MessageType.GameAction) {	// Capitalize contents of text Message
						askForAction();
					}
    				else if (line.getType() == MessageType.RenderCards) {	// Capitalize contents of text Message
						getGameCards();
					}
    				else if (line.getType() == MessageType.RenderPlayers) {	// Capitalize contents of text Message
						getGameUsers();
					}
    				else if (line.getType() == MessageType.StartGame) {	// Capitalize contents of text Message
    					if (seatedAt.hasDealer() && seatedAt.getPlayerCount() > 0) {
    						seatedAt.startGame();
    					}
    					writeMessage(new Message(MessageType.StartGame, MessageStatus.Success));
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
    	
    	public synchronized void lookForTable() {
    		// Do not let client look for table if timed out
    		if (account.isTimedOut()) {
    			informClientOfError(ErrorType.TimedOut);
    			return;
    		}
    		
    		boolean foundASeat = false;
    		if (availableTables.size() == 0) {
    			makeTable();
    		}
    		if (isDealer()) {
    			for (int i = 0; i < availableTables.size(); i++) {
        			if (foundASeat) {
        				break;
        			}
        			seatedAt = availableTables.get(i);
        			if (!seatedAt.hasDealer()) {
        				seatedAt.addUserToTable(this);
        				foundASeat = true;
        			}
        		}
    			if (!foundASeat) {
    				makeTable();
    				seatedAt = availableTables.elementAt(0);
    				seatedAt.addUserToTable(this);
    				foundASeat = true;
    			}
    		}
    		else {
    			int leastTablePopulation = 7;
    			Vector<Integer> tablePopulation = new Vector<Integer>();
    			for (int i = 0; i < availableTables.size(); i++) {
        			seatedAt = availableTables.get(i);
        			tablePopulation.add(seatedAt.getPlayerCount());
        			if (leastTablePopulation > seatedAt.getPlayerCount()) {
        				leastTablePopulation = seatedAt.getPlayerCount();
        			}
        		}
    			if (leastTablePopulation == 6) {
    				makeTable();
    				leastTablePopulation = 0;
    			}
    			for (int i = 0; i < availableTables.size(); i++) {
    				if (foundASeat) {
        				break;
        			}
    				if (tablePopulation.get(i) < 6 && tablePopulation.get(i) == leastTablePopulation) {
    					foundASeat = true;
    					seatedAt = availableTables.get(i);
    					seatedAt.addUserToTable(this);
    				}
    			}
    		}
    		writeMessage(new Message(MessageType.TableJoin, MessageStatus.Success));
    	}
    	
    	public synchronized void makeTable() {
    		// Add a new table
    		availableTables.insertElementAt(new Table(), 0);
    	}
    	
    	public void timeOut() {
    		account.setTimeOut(300);
    	}
    	
    	public synchronized boolean register(String username, String password, boolean credentials) {
    		for (Account existingAccount: accountRegistry) {
    			if (existingAccount.getUsername().compareTo(username) == 0) {
    				return false;
    			}
    		}
    		
			BufferedWriter registryUpdater = null;
			try {
				registryUpdater = new BufferedWriter(new FileWriter("accounts.txt", true));
				registryUpdater.newLine();
				registryUpdater.append(username + "," + password + "," + credentials + ",0.00,0");
				
				registryUpdater.close();
				
				Account newAccount = new Account(username, password, credentials);
	    		accountRegistry.add(newAccount);
	    		account = newAccount;
	    		return newAccount.validate(username, password, credentials);
			} catch (IOException e) {
				e.printStackTrace();
			}

    		return false;
    	}
    	
    	public boolean logIn(String username, String password, boolean credentials) {
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
				writeMessage(new Message(MessageType.Error, MessageStatus.Fail, "timedOutResponse"));
			}
    		else if (errorType == ErrorType.TimedOut) {
    			writeMessage(new Message(MessageType.Error, MessageStatus.Fail, "Seconds remaining for timeout: " + account.getTimeOut()));
    		}
    		else if (errorType == ErrorType.CannotWithdraw) {
    			writeMessage(new Message(MessageType.Error, MessageStatus.Fail, "You have insufficient funds: " + account.getBalance()));
    		}
    	}
    	
    	public void askForBets() {
    		try {
    			Message line = new Message();
				writeMessage(new Message(MessageType.RequestBet, MessageStatus.Pending));
				while ((line = (Message) in.readObject()) != null) {
					if (line.getType() == MessageType.Bet) {
						if (account.setBet(Double.parseDouble(line.getText()))) {
							return;
						}
					}
					else {
						informClientOfError(ErrorType.CannotWithdraw);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	
    	public void removeFromTable() {
    		seatedAt.queueLeave(this);
    		seatedAt = null;
    		writeMessage(new Message(MessageType.TableLeave, MessageStatus.Success));
    	}
    	
    	public void askForAction() {
    		try {
    			Message line = new Message();
				writeMessage(new Message(MessageType.GameAction, MessageStatus.Pending));
				if ((line = (Message) in.readObject()) != null) {
					if (line.getType() == MessageType.Stand) {
						standRequest();
					}
					if (line.getType() == MessageType.Hit) {
						hitRequest();
					}
					if (line.getType() == MessageType.Dealer) {
						if (isDealer()) {	// Dealers turn is taken automatically
							while (checkRanks() < standAt) {
								hitRequest();
							}
							standRequest();
						}
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
    		//writeMessage(new Message(MessageType.Hit, MessageStatus.Success));
    		
    	}
    	
    	public void addCard(Card card) {
    		account.receiveCard(card);
    	}
    	
    	public void restartGame() {
    		account.resetCardsAndBet();
    	}
    	
    	public void restartGame(boolean payOut) {
    		account.resetCardsAndBet(payOut);
    	}
    	
    	public void getGameUsers() {
    		boolean[] isSeated = seatedAt.getVacancies();
    		String seatedUsers = "";
    		for (boolean vacant: isSeated) {
    			if (vacant) {
    				seatedUsers += "0";
    			}
    			else {
    				seatedUsers += "1";
    			}
    		}
    		writeMessage(new Message(MessageType.RenderPlayers, MessageStatus.Success, seatedUsers));
    	}
    	
    	public void getGameCards() {
    		String allCards = "";
    		Vector<Card>[] drawnCards = seatedAt.getAllUsersHands();
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
    		writeMessage(new Message(MessageType.RenderCards, MessageStatus.Success,allCards));
    	}
    	
    	public int checkRanks() {
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
    			if (results.length() == 0) {
    				results += ", ";
    			}
    			results += card.getRank() + card.getSuit();
    		}

    		if (score > 21) {
				while (aces > 0 && score > 21) {
					score -= 10;
					aces -= 1;
				}
				if (score > 21) {
					stoodOrBust = true;
					results += "\nYou have busted";
				}
			}
    		writeMessage(new Message(MessageType.Hit, MessageStatus.Success, results));
    		//writeMessage(new Message(MessageType.Bust, MessageStatus.Success, results));
			return score;
    	}
    	
    	public synchronized void save() {
    		// Prepare to save account info to registry
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
				// When this account is found, update timeOut and balance
				if (account.getUsername().compareTo(accountDetails[0]) == 0) {
					accountData = accountDetails[0] + "," + accountDetails[1] + ',' + accountDetails[2] + ",";
					accountData += account.getBalance() + ",";
					accountData += account.getTimeOut();
				}
				if (registryLoader.hasNextLine()) {
					accountData += "\n";
				}
				newRegistry += accountData;
			}
			try {
				registryLoader.close();
				registryUpdater = new BufferedWriter(new FileWriter("accounts.txt"));
				registryUpdater.write(newRegistry);
				registryUpdater.close();
			} catch (IOException e) {
				e.printStackTrace();
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
				out.flush();
				collectMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	public boolean isDealer() {
    		return account.getCredentials();
    	}
    	
    	public void cancelGame() {
    		if (isDealer()) {
    			seatedAt.dealerCancelledGame();
    		}
    	}
    	
    	public Vector<Card> getHand() {
    		return account.getCards();
    	}
    }
}
