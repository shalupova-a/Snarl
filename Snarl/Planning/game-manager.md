## GameManager Interface

*Note: Server and GameManager are used interchangeably, as well as Player and client.*

### Overview

This will be a server that accepts TCP connections to it from the Player clients. When each player connects to the server, we read in a message with their username. The GameManager creates the player and adds them to the list of players within the model, in order of registration. Once all the necessary players have registered, the GameManager sends a message to the client indicating the game is starting. Then, the GameManager calls the renderLevel function within our view to visualize the first level. Next, the GameManager calls another view function within the GameView, `renderPlayerView(Coordinate)`, which renders the view for each player given their Coordinate. This makes sure we are restricting the view for each player by only showing a 2 grid unit area. This method will also output a coordinate below the view to indicate the Player’s position relative to the origin of the level. We send all view rendering and any other info back to the Player client.

Next, the GameManager will send a message to the Player clients (in order) to enter a number denoting their next action:    
1. Move to a different tile   
2. Stay put   
3. Exit game      

Then, the GameManager will wait for a response from the Player client.
If they chose 1, then the GameManager sends another query to the client to input the coordinate (x, y) they would like to move to.
  * we call a function called `handleMove()` given this coordinate.  
If they chose 2, we do nothing and simply move on to the next player and repeat the process.    
If they chose 3, we call the `stop("You have left the game.")` method and disconnect them from the game. As of now, this does not affect other players (subject to change). 
If they chose something else, we send the same query back but with a message saying that their input was invalid.

Based on the action, we send a message back to the Player client. If the game is not over, we keep looping through all the Players (that are alive) and requesting an action.

Once the game is over, we send a message back to the Player clients and stop the TCP connection.

### Message Format   
We anticipate that messages will be sent between client and server in a JSON format, likely as a JSONObject with the keys `type` and `message`.    
`type` will denote what kind of message is being sent, likely represented by an enum
* INITIALIZE
  * Player sends this with the username to the GameManager
* START
  * GameManager sends this when the game is ready to start
* DISPLAY
  * GameManager sends this with a message of the ascii display.
* STATUS
  * GameManager sends this if the game status has changed and the game is over/player has died.
* QUERY
  * GameManager sends this to ask for an action from the Player
* ACTION
  * Player sends this with the action the user has chosen

### Fields
* `private GameView view`
* `private GameState model`
* `private RuleChecker ruleChecker`
* `private int maxPlayers;`
* `private int minPlayers;`
* All the necessary TCP connection fields
  * Using `ServerSocket` and `Socket` Java libraries
* reader and writer fields (`BufferedReader` and `PrintWriter` Java libraries)
  * Used to send/recieve JSON

### Methods
* `start()` - Used to start the TCP connections with the client(s) and send and receive messages
  * Helper methods:
    1. `private void createPlayer(String username)`: Calls a method within the GameState to create the player and add to the list of players in the model.
    2. `private boolean checkNumPlayers()`: checks that the number of players registered meets the min and max requirements.
    3. `private void handleMove(Coordinate to)`: 
      * Calls `checkValidMove()` in RuleChecker.
        If this returns true, we will call another function called `updateBoard()` which updates the level based on the Player's position. Then, we call `renderPlayerView()` again for each player. 
      * We also need to call methods in the RuleChecker that determine if the player's inventory or HP has changed. If so, we need to update the Model with this info.
      * calls `getWinStatus()` in RuleChecker (if true call `stop("You have won!")`)
      * Then, we call `renderPlayerView()` again for each player. 
    4. `private ? parseJSON(JSONObject jsonObj)`
      * helper method likely needed to parse the JSON messages received from the Player
    5. `private JSONObject createJSON(enum type, String message)`
      * create a JSONObject based on the type and message
* `stop(String message)` - Will stop the TCP connection if a player chooses to disconnect or the game is over

