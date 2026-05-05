package MBClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import enums.MessageStatus;
import enums.MessageType;
import message.Message;

public class TestServer {
	private static ServerSocket server;
	
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Create a ServerSock on localhost:7777
    	server = null;

		try {
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
    	private ObjectOutputStream out = null;
		private ObjectInputStream in = null;


    	// Constructor to receive client connection
    	public ClientHandler(Socket socket) throws IOException
    	{
    		this.clientSocket = socket;	
    		
			OutputStream outBase = clientSocket.getOutputStream();
			out = new ObjectOutputStream(outBase);
			
			InputStream inBase = clientSocket.getInputStream();
	        in = new ObjectInputStream(inBase);
	        
	        System.out.println("client connected");
    	}

    	// Run on instantiation
    	public void run()
    	{
    		// Initialize variables for client communication
    		try {
    			//boolean disconnect = false;

    	        // Communicate until client connection is severed
    			Message line;
    			while ((line = (Message) in.readObject())  != null) {
    				if (line.getType() == MessageType.Connected){
    					out.writeObject(new Message(MessageType.Connected, MessageStatus.Success));
    					out.flush();
    				}
    				else if (line.getType() == MessageType.LogOut) {	// Sever client connection on logout
						out.writeObject(new Message(MessageType.LogOut, MessageStatus.Success));
    					out.flush();
    					//disconnect = true;
					}
					else if (line.getType() == MessageType.TableJoin) {
						out.writeObject(new Message(MessageType.TableJoin, MessageStatus.Success));
    					out.flush();
					}
					else if (line.getType() == MessageType.TableLeave) {
						out.writeObject(new Message(MessageType.TableLeave, MessageStatus.Success));
    					out.flush();
					}
					else if (line.getType() == MessageType.LogIn) {
						out.writeObject(new Message(MessageType.LogIn, MessageStatus.Success));
    					out.flush();
					}
					else if (line.getType() == MessageType.TimeOut) {
						out.writeObject(new Message(MessageType.TimeOut, MessageStatus.Success));
    					out.flush();
					}
					else if (line.getType() == MessageType.Register) {	
						out.writeObject(new Message(MessageType.Register, MessageStatus.Success));
    					out.flush();
					}
					else if (line.getType() == MessageType.BalanceRequest) {
						out.writeObject(new Message(MessageType.BalanceView, MessageStatus.Success, "12.34"));
    					out.flush();
    				}
					else if (line.getType() == MessageType.DepositRequest) {
						out.writeObject(new Message(MessageType.DepositBalance, MessageStatus.Success, "13.34"));
    					out.flush();
    				}
    				else if (line.getType() == MessageType.WithdrawRequest) {
    					out.writeObject(new Message(MessageType.WithdrawBalance, MessageStatus.Success, "11.34"));
    					out.flush();
    				}
    				else if (line.getType() == MessageType.RequestBet) {
    					out.writeObject(new Message(MessageType.RequestBet, MessageStatus.Success));
    					out.flush();
					}
    				else if (line.getType() == MessageType.GameAction) {
    					out.writeObject(new Message(MessageType.GameAction, MessageStatus.Success));
    					out.flush();
					}
    				else if (line.getType() == MessageType.RenderCards) {
    					out.writeObject(new Message(MessageType.RenderCards, MessageStatus.Success));
    					out.flush();
					}
    				else if (line.getType() == MessageType.RenderPlayers) {
    					out.writeObject(new Message(MessageType.RenderPlayers, MessageStatus.Success, "011000"));
    					out.flush();
					}
    				else if (line.getType() == MessageType.StartGame) {
    					out.writeObject(new Message(MessageType.StartGame, MessageStatus.Success));
    					out.flush();
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
    	
 