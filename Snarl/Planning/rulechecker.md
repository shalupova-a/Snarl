TO: Growl Inc.    
FROM: Angelina Shalupova and Sheetal Singh    
DATE: February 24, 2021    
SUBJECT: Rule Checker Interface Design    

Outlined below is our design for the RuleChecker interface, which handles movements and interactions. It also updates the GameState's current level and game status (if the game has been won).


# Interface RuleChecker
	
Fields
  * Private Coordinate currentPosn
  * Private Coordinate toPosn
  * Private int currentLevelId
  * Private ArrayList<Level> levels
  * Private boolean winStatus
  * Private Character character
  * Private GameState state
  
  
Methods
  * Boolean checkValidMove(currentPosn, toPosn)
    * Returns true if move is valid, false if it is not. Throws an exception if move is invalid
  * Boolean reachableTile(currentPosn, toPosn)
    * Helper function of checkValidMove
    * Checks if the toPosn tile is 2 cardinal moves away from the currentPosn tile
      * valid moves may change for adversaries, so we may need to check for Character type here
  * Boolean freeTile(toPosn)
    * Helper function of checkValidMove
    * If the Character is a PlChecks if the posn the character wants to move to is free (doesn’t have Character, isnt a wall)
    * If the Character is an Adversary, we may not need this check depending on the adversaries abilities.
  * Boolean playerInteractions()
    * Check that the tile is free for a character (doesn't have Character, isnt a wall)
  * Boolean adversaryInteractions()
    * Check that the tile is free for an adversary (no other adversaries, no items)
      * may need to allow different types of movements here such as wall climbing
    * If tile has a Player, move is valid and we need to "kill" this Player
  * void killPlayer(username)
    * Helper function to adversaryInteractions()
    * kills/maims the player by decreasing it's health value. 
  * Boolean foundObject(toPosn)
    * Helper function of freeTile
    * Checks if toPosn is has an exit or a key
      * If it has an exit AND the Character has a key, we update the currentLevelId
      * If it has a key (or some other item), we update the Character’s inventory
        * This may only be applicable to Player (not sure if Adversary’s can interact with Objects)
  * Void updateLevel(currentLevelId)
    * Updates currentLevelId to next level. If there is no next level, update winStatus to true
  * Void updateInventory(item)
    * Add this item (most likely a key) to the character’s inventory 
  * Boolean getWinStatus()
    * Returns boolean declaring is the players have won yet


All these methods will also be responsible for updating the GameState state field. If a Player has found an item, then we must update the corresponding player’s inventory in the GameState. If the level has changed/game is won, then we must update that information in the GameState as well.
If a Player is killed, we need to update the corresponding player's health in the GameState.


