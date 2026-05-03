package MBClient;

import enums.MessageStatus;
import enums.MessageType;

public class GUI {
	private Client client;
	private Scene currentScene;
	
	GUI(Client client) {
		this.client = client;
	}
	
	public void run() {
        /* open screen */
		// display user choices: login or register
		
		// login button action listener:
        // if user choose login,
        // get user/pass
        // send Login message containing user/pass (status: Pending)
        // receive Login message back from server, check status
        // if status Success, move to main menu depending on credentials
        // if status Fail, notify user of failure, re-prompt user/pass
		
		// register button action listener:
        // if user choose register,
        // get credential/user/pass (validate)
        // send Register message containing credential/user/pass
        // receive Register message back from server, check status
        // if status Success, move to main menu depending on credentials
		
        /* player main menu */
        
        // display user choices: balance, play, or logout
        
		// balance button action listener:
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
        
		// play button action listener:
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
        
		// logout button action listener:
        // if user choose logout,
        // send Logout message
        // wait for Logout message back from server, check status
        // if status Success, return to open screen
        
        /* dealer main menu */ 
        
        // display user choices: host table or logout
        
		// host button action listener:
        // if user choose host table,
        // send hostTable message
        // wait for hostTable message back from server, check status
        // if status Success, move to dealer game screen
        // if status Fail, ?
        
		// logout button action listener: 
        // if user choose logout,
        // send Logout message
        // wait for Logout message back from server, check status
        // if status Success, return to open screen
        
        /* player game screen */
        
        
        
        /* dealer game screen */
	}
	
}