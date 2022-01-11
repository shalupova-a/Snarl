# Multi-Game Server
Our Snarl implementation now supports the ability to play multiple games, running in sequence. 
The amount of games is specified via command line, and the levels from the given file are re-used for each game.

### Modifications

#### App.java and CmdLineOption
These files were modified to take in `--games <int>` via commandline, which indicates the amount of games to be played.
If this is omitted, only 1 game will be played by default. This value is then passed to the server.

#### Server
- This class now takes in a `games` in it's constructor. Also, a for loop was added to loop through all the number of games
based on this `games` field. Within this loop, an entire game is played.
- In addition to the `playerScoresMap` which keeps track of all the player scores within a game, a `leaderboard`
map was added to keep track of player scores across all games. 
- When the `end-game` JSON message is sent to the client, there is a check to see if this is the final game.
If it is, then an additional `leaderboard` key is added to the JSONObject. This leaderboard contains
the scores across all games.

#### Client
- Add ability to accept `leaderboard` and pass it to the view to display
- Send game # to view to display
- added check for enter key press while on the end game screen. This tells the client that the players want to exit 
   (can happen after any game).

#### ClientView
- rewrite `endGameScreen()` to display leaderboard on the screen at the end of all the games
- rewrite `welcomeScreen()` to display the game # on the screen, at the start of each level