# Snarl
CS4500 - Software Development Final Project

## How to Play Locally
https://github.com/shalupova-a/Snarl/blob/main/Snarl/src/README.md

## Snarl Rules
Growl, Inc. is a new start-up trying to jump on the bandwagon of nostalgic lo-fi games. Its first product, called Snarl, is planned as a turn-based 2D dungeon crawler with a modular multi-player and multi-AI architecture. The game is meant for 1 to 4 human players. Players are dropped in a dim dungeon, which consists of multiple levels.

A level consists of multiple rooms, connected by hallways. Each room consists of multiple tiles, allowing for rooms of different sizes and configurations. In separate rooms are an exit from the dungeon and a key to unlock that exit.

However, the players are not alone in this dungeon as it is haunted by different adversaries looking to expel the players! The folks at Growl, Inc. are currently thinking ghosts and zombies, but might want to expand the arsenal as the project progresses. What is worse is the adversaries can see the whole dungeon level they occupy, whereas the players can only see a limited area around them. So players must explore the dungeon, avoid being eaten or killed, find the key and get to the exit.

### Goal
The goal of the players is to find the key that unlocks the exit and then have at least one player exit the dungeon.

### Start of the Game
After the map is loaded, the players are placed together in a predetermined room. The enemies are placed throughout the dungeon, in rooms and hallways. The key and exit portal are also placed in different locations of the dungeon.

### Playing One Round
Players act in the order they joined. Once all players have acted, the adversaries can act. Each act consists of two parts: movement and interaction.

A players can move by at most two tiles to an unoccupied tile. A tile is occupied if there is a player or an enemy on the tile. Once the player has chosen their tile, their avatar is moved to that tile. Finally, the player automatically interacts with any object on that tile. A system with movement points is also under discussion.

If the object is a key, the key is removed from the level and the exit is unlocked. If that object is a locked exit, nothing happens. If that object is an unlocked exit, the player leaves the dungeon, represented by the avatar being removed from the level. An inventory system might be also in play.

Once all players have acted, the enemies act. Each enemy chooses an adjacent tile for their destination. Finally, the enemy interacts with the object on that new tile.

If that object is a player, the player is expelled from the game, represented by the avatar being removed from the board and the player marked as expelled. Growl, Inc. is considering a system with health and attack points to make the game a little more interesting.

### Ending the Game

A Snarl level ends when all player avatars are removed - either because they found the exit or they were expelled.
All the players advance to the next level if any player reaches the unlocked exit, including previously expelled players.
All players, including expelled players, are then placed at the start of the next level.

A Snarl game ends if one of the following occurs

1. the enemies expel all players in a given level. In this case, the players lose. 2. any player reaches the unlocked exit of the final level of the dungeon. In this case, the players win.


### Bonus features

#### Multi-game Server
The Snarl server as implemented in Milestone 9 only supports a single game. Extend the server to support multiple Snarl games. Each game should start following the same rules as in Milestone 9 (at least one player, start when four players connect or timeout lapses). For simplicity, you can run games in sequence (a game starts after the previous one finished). If you implement a multi-threaded server, we will give extra points above the allocation specified at the beginning. In addition to the (end-game) message, the server should also send a running leaderboard of players from the games finished so far (the JSON format is up to you). The server should have a mechanism for the administrator to shut it down after any ongoing games finish. This can be via a GUI or by accepting an exit command on standard input in the console.
See https://github.com/shalupova-a/Snarl/blob/main/Snarl/Planning/multi-game.md

#### Hit Point & Combat System
Give players and adversaries hit points, allowing them to engage in combat. That means a player shouldn’t be expelled immediately upon contact with an adversary, but should be able to sustain some damage before being expelled. The player may be be able to hit the adversary back. Make a reasonable choice on the damage delivered by a ghost and a zombie, with respect to the player’s total hit points.

