TO: Growl Inc.

FROM: Angelina Shalupova and Sheetal Singh

DATE: February 16, 2021

SUBJECT: Snarl Design Analysis

## GameState Interface
To represent a Snarl state, we will have an Interface called ‘GameState’. A GameState has the following private fields:

* ArrayList<Player> players
  - A list of all the players in the game
* ArrayList<Level> levels
  - A list of all the levels in the game
* int currentLevelId
    - and an int representing the id number of the current level at play. 

We’ve decided that methods in the GameState would include: 

* getAllPlayers() - returns this.players
* getAllRooms() - returns this.rooms
* getAllLevels() - returns this.levels
* validMove(Coordinate from, Coordinate to) - checks if a player’s move is valid, and returns a boolean value
* getActorLocation(String username) - returns a Coordinate representing the location of a player
* getEnemyLocation() - we’ve discussed that getting the location(s) of the adversaries would be helpful, but it is currently unknown if it is necessary since we do not know how adversary code will work
* getCurrentLevel() - returns an int representing the current level at play
* getAlivePlayers() - returns an ArrayList<Player> who are currently alive in the game
* foundExit() - returns a boolean representing if any of the players have found and left through an exit. We thought this would be helpful to have as a method if the game needs to be updated to move all players to the new level where the player who exited is.
* progressLevel(int newLevelId) - changes levels by updating the current level id, player location and list of rooms.
* gameStatus() - returns a boolean to check if all the levels in the dungeon have been completed for the players to win the game, or if all the players have died for the game to be over.

Our GameState has references to other items, which include Player, Coordinate, Level, Room, Object, and Hallway.


### Room class

fields:    
* Coordinate position
* Int height, int width
* HashMap<int, Coordinate> connectedHallways    
  * Int hallway id
  * Coordinate - coordinate of door to hallway
  * We will also have a method to add entries to this hashmap
* Char[][] tiles
* ArrayList<Object> items
* Int roomId


### Player class

fields:
* String username
* int health
* ArrayList<Object> items
* Coordinate position


### Hallway class

fields:
* int hallwayId
* ArrayList<int> roomIds
  * Length of 2 since a hallway only connects 2 rooms
* int length
* ArrayList<Coordinate> waypoints


### Object class

fields:
* Enum type
  * Key or exit
* Coordinate position


### Level class

fields:
* Int levelId
* ArrayList<Room> rooms 
* ArrayList<Hallway> hallways
* Coordinate levelExit
* char[][] tiles
