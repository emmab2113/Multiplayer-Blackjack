package MBServer;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class Server {
	private static Vector<Table> availableTables;
	private static Vector<Account> accountRegistry;
	private static Vector<TestMessage> messageLog;
	private static ServerSocket server;
	private static int standAt;
	
	public void collectMessage(TestMessage message) {
		messageLog.add(message);
	}
	
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Create a ServerSock on localhost:7777
    	accountRegistry = new Vector<Account>();
    	messageLog = new Vector<TestMessage>();
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
    
    private static class ClientHandler implements Runnable {
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
    	}

    	// Run on instantiation
    	public void run()
    	{
    		// Initialize variables for client communication
    		boolean login = false;
    		try {
    				
    			// get the outputstream and inputstream of client
    			OutputStream outBase = clientSocket.getOutputStream();
    			InputStream inBase = clientSocket.getInputStream();
    			
    			// Create object streams so we can read and write TestMessages to client.
    			out = new ObjectOutputStream(outBase);
    	        in = new ObjectInputStream(inBase);

    	        // Communicate until client connection is severed
    			TestMessage line;
    			while ((line = (TestMessage) in.readObject()) != null) {
    				if (login) {
    					if (line.getType().compareTo("logout") == 0) {	// Sever client connection on logout
    						login = false;
    						out.writeObject(new TestMessage("logout","success","success"));
    						clientSocket.close();
    						return;
    					}
    					else if (line.getType().compareTo("text") == 0) {	// Capitalize contents of text TestMessage
    						String capitalInput = line.getText();			// Return new text to client
    						capitalInput = capitalInput.toUpperCase();
    						out.writeObject(new TestMessage("text","success",capitalInput));
    						
    					}
    				}
    				else {	// Only listen for login TestMessages if client is not logged in
    					if (line.getType().compareTo("login") == 0) {
    						login = true;
    						out.writeObject(new TestMessage("login","success","success"));
    					}
    				}
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
    		for (int i = 0; i < availableTables.size(); i++) {
    			seatedAt = availableTables.get(i);
    		}
    	}
    	public void makeTable() {
    		availableTables.add(null);
    	}
    	public void timeOut() {
    		account.setTimeOut(300);
    	}
    	public boolean register(String username, String password, String credentials) {
    		Account newAccount = new Account(username, password, credentials);
    		accountRegistry.add(newAccount);
    		account = newAccount;
    		return newAccount.validate(username, password, credentials);
    	}
    	public boolean logIn(String username, String password, String credentials) {
    		Account matchingAccount = new Account(username, password, credentials);
    		account = matchingAccount;
    		return matchingAccount.validate(username, password, credentials);
    	}
    	public double getPlayerBalance() {
    		return account.getBalance();
    	}
    	public double chargePlayerBalance(double currency) {
    		account.modifyBalance(currency);
    		return account.getBalance();
    	}
    	public double addToPlayerBalance(double currency) {
    		account.modifyBalance(currency);
    		return account.getBalance();
    	}
    	/*
    	public void informClientOfError(ErrorType errorType) {
    		
    	}
    	*/
    	public void askForBets() {
    		try {
    			TestMessage line = new TestMessage();
				out.writeObject(new TestMessage("Bet","success","NA"));
				if ((line = (TestMessage) in.readObject()) != null) {
					account.setBet(42);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	public void removeFromTable() {
    		seatedAt = null;
    	}
    	public void askForAction() {
    		try {
    			TestMessage line = new TestMessage();
				out.writeObject(new TestMessage("GameAction","success","NA"));
				if ((line = (TestMessage) in.readObject()) != null) {
					if (line.getType().compareTo("stand") == 0) {
						standRequest();
					}
					if (line.getType().compareTo("hit") == 0) {
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
    		
    	}
    	public void standRequest() {
    		
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
				out.writeObject(new TestMessage("RenderPlayer","success",seatedUsers));
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
				out.writeObject(new TestMessage("RenderCard","success",allRanks));
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	public void checkRanks() {
    		int aces = 0;
    		int score = 0;
    		String curRank = "";
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
    				score += Integer.parseInt(card.getRank());;
    			}
    			if (score > 21) {
    				while (aces > 0 && score > 21) {
    					score -= 10;
    					aces -= 1;
    				}
    				if (score > 21) {
    					stoodOrBust = true;
    				}
    			}
    		}
    	}
    	public void save() {
    		for (Account registered: accountRegistry) {
    			if (account == registered) {
    				File registryFile = new File("accounts.txt");
    				Scanner registryLoader = null;
					try {
						registryLoader = new Scanner(registryFile);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
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
    			}
    		}
    	}
    	public boolean getStoodOrBust() {
    		return stoodOrBust;
    	}

    }
}
