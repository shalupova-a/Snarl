TO: Growl Inc.  
FROM: Angelina Shalupova and Sheetal Singh  
DATE: February 8, 2021  
SUBJECT: Snarl Project Analysis  


Snarl is made up of a dungeon with players that can move through the dungeon. The dungeon has multiple levels, all which have rooms/hallways. Along with the rooms, levels must keep track of what room the key is in. Rooms/hallways can contain adversaries, players, items, and an exit. They must also keep track of which tiles are unoccupied and occupied, and by what they are occupied. Rooms must keep track of the location of the hallway. A hallway is essentially a room, but it cannot contain an exit, and it keeps track of the 2 rooms that it connects. Players, adversaries, and items need to store their location. A player may have an inventory to keep track of all the items they have collected (i.e. key). The player also has a username, given by the user at the start of the game. The player also needs to keep track of if they have been killed or harmed by an adversary. Since adversaries can be either ghosts or zombies, we may need to be able to distinguish them as well in case they have different abilities.
### Milestones
For this project, we propose having four milestones. Milestone 1 involves setting up the game as well as the connection between the client and server. This includes prompting the user for its username and number of players, and setting up the dungeon with levels, hallways, and rooms on the server side. Then, the client should display clean output for the user to start playing the game.


Milestone 2 is focused towards the players and adversaries movements. We will allow players to move around rooms and between rooms/hallways. In addition, we will integrate the code for adversaries to move around the room and hallways. For now, if a player attempts to leave a room or level, they are able to exit.


Milestone 3 involves incorporating the limited visibility that players have at their location. In addition, we propose to also enable interactions between players and items (for now, keys) between users. This also includes, once a player has found a key, not only will that player be able to exit, but any other player would also be able to exit the room/level. 


For our final milestone, we plan to integrate adversaries to interact (kill, maim, etc.) players. We also hope to wrap up the game by determining if the player(s) have won or lost the game, and display this. Also, if any user(s) attempt to quit the game, the game is discontinued and a start menu is displayed.
