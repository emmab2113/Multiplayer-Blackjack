// workflow in ClientHandler:

// receive Connected message

// return Connected message with Success status 

// wait for message of type: Login or Register

// if Login message received
// Login message contains: attempted user/pass
// user/pass checked against registry for existence
// Success/Fail status returned depending if exists

// if Register message received
// Register message contains: new user, new pass, and account type (dealer of player)
// account created/added to registry
// Success/Fail status returned depending if account successfully created

/* player logged in */

// wait for message of type: 
// BalanceRequest, DepositRequest, WithdrawRequest, GetTables, LogOut

// if BalanceRequest message received,
// logged in player's current balance returned in BalanceView message

// if DepositRequest message received,
// DepositRequest message contains: deposit amount
// player's balance increased by given amount
// Deposit message returned with Success status containing new balance

// if WithdrawRequest message received,
// WithdrawRequest message contains: withdraw amount
// player's balance decreased by given amount
// if successful, Withdraw message returned with Success status containing new balance
// if unsuccessful, Withdraw message returned with Fail status

// if GetTables message received,
// GetTables message returned containing available tables and the current number of players at table
// possible formatting: "tableID1,numPlayers1,tableID2,numPlayers2"

// if LogOut message received,
// user account logged out
// Success/Fail status returned depending if account successfully logged out

/* dealer logged in */

// wait for message if type: HostTable or LogOut

// if HostTable message received,
// table created
// Success/Fail status returned depending if table successfully created/initialized

// if LogOut message received,
// user account logged out
// Success/Fail status returned depending if account successfully logged out