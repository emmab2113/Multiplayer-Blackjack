package normal;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server {
//	private static Vector<Table> availableTables: Record of tables available for users to join
	private static Vector<Account> accountRegistry;
	private static Vector<TestMessage> messageLog;
	private static ServerSocket server;
	private static int standAt;
	
	public void collectMessage(TestMessage message) {
		messageLog.add(message);
	}
	
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Create a ServerSock on localhost:7777
    	accountRegistry = null;
    	messageLog = null;
    	server = null;
    	standAt = 17;

		try {

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
//    	private Table seatedAt;


    	// Constructor to receive client connection
    	public ClientHandler(Socket socket) throws IOException
    	{
    		this.clientSocket = socket;	
    	}

    	// Run on instantiation
    	public void run()
    	{
    		// Initialize variables for client communication
    		ObjectOutputStream out = null;
    		ObjectInputStream in = null;
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
    	}
    	public void makeTable() {
    		
    	}
    	public void timeOut() {
    		
    	}
    	public boolean register(String username, String password, String credentials) {
    		Account newAccount = new Account(username, password, credentials);
    		return newAccount.validate(username, password, credentials);
    	}
    	public boolean logIn(String username, String password, String credentials) {
    		Account matchingAccount = new Account(username, password, credentials);
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
    		
    	}
    	public void removeFromTable() {
    		
    	}
    	public void askForAction() {
    		
    	}
    	public void hitRequest() {
    		
    	}
    	public void standRequest() {
    		
    	}
    	/*
    	public void addCard(Card card) {
    		
    	}
    	*/
    	public void restartGame() {
    		
    	}
    	public void getGameUsers() {
    		
    	}
    	public void getGameCards() {
    		
    	}
    	public void checkRanks() {
    		
    	}
    	public void save() {
    		
    	}
    	public void getStoodOrBust() {
    		
    	}

    }
}
