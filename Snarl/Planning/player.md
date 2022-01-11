## Player Interface

*Note: Server and GameManager are used interchangeably, as well as Player and client.*

The Player acts as a client in relation to the GameManager, which is the server. The Player will connect to the GameManager via a TCP connection. Once the Player has connected, it will query the user for a username which it will send to the server. Then, it should receive a welcome message from the server once all the players have registered (number stored in GameManager), which the Player will output. Then, the GameManager will send back an ascii display of their current position (probably the origin of the level) with limited view of their position as well as their health status, which the Player will display for the user. Then, The GameManager will be send a request for an action (make a move, stay put, leave the game). The Player will allow the user to input their action and send it back to the GameManager. If the GameManager determines that the user choose to make a move, they will send a query back to the Player for a coordinate. The Player will then allow for the user to enter a coordinate. This coordinate will be sent back to the GameManager. The GameManager will then send back an updated view with their new location (if the move is valid), or an error message asking them to input a valid coordinate. It is also possible that their health status will be updated or a win/lose screen depending on what interactions occured. This will be repeated once all the other players have made moves or until the game ends.
Then the player will be disconnected by the server. 


We anticipate that messages will be sent between client and server in a JSON format, likely as a JSONObject with the keys `type` and `message`.    
`type` will denote what kind of message is being sent
* INITIALIZE
  * Player sends this with the username to the GameManager
* START
  * GameManager sends this when the game is ready to start
* DISPLAY
  * GameManager sends this with a message of the ascii display.
* STATUS
  * GameManager sends this if the game status has changed and the game is over.
* QUERY
  * GameManager sends this to ask for an action from the Player
* ACTION
  * Player sends this with the action the user has chosen

### Fields
* `private Socket clientSocket`
* `private BufferedReader in`
* `private PrintWriter out`

### Methods
* `public void startConnection(String ip, int port)`
  * connects to the server (GameManager) via a TCP connection.
  * prompts user for username
  * sends username to server
  * waits for message from GameManager indicating game is ready to start
  * call `handleMoves()`
* `private void handleMoves()`
  * read message from GameManager of view display, print on screen
  * read message from GameManager querying user for an action and collect user input
  * send back user response
  * read message from GameManager with updated view, display for user (repeat in a loop until we receive another query for an action)
  * once another action message is received, repeat the process, or stop until a message denoting the game has ended is received. 
* `private void stopConnection(String message)`
  * Ends the TCP connection and prints out the message from the server
* `private void parseJSON(JSONObject jsonObj)`
  * helper method likely needed to parse the JSON messages received from the GameManager
* `private JSONObject createJSONObj(enum type, String message)`
  * helper method that creates a JSONObject based on type and message
  
