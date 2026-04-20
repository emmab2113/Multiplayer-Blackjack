package normal;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Create a ServerSock on localhost:7777
    	ServerSocket server = null;

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
    }
}
