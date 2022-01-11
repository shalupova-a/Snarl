# Running the game:
- To run the game go to /local folder 
- `chmod +x localSnarl`
- `./localSnarl`

This will run the game in default mode

you can also do

`./localSnarl --levels FILENAME --players N --start M --observe`

where FILENAME is the absolute path to the file with level data

PLAYERS is num players in the level (max 4)

START is the level to start in 

OBSERVE shows the whole game in observe mode without any fog of war

After that enter the name to register the players and each player will be asked to provide a x and y coordinate to move if it is a valid move

You can choose the option to exit the game at any point by pressing 3. 
The app exits when all players have exited the game or if all players are expelled
