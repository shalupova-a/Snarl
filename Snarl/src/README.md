## Playing the game over the network

- Open two terminals

### Option 1
- Do 
```./snarlClient1 --port 12345 --address localhost```
in one terminal for the client (can do multiple clients as well)

- Run ```./snarlServer1 --address localhost --port 12345 --clients <int>``` on the other terminal for the server
- Answer the question in the client terminal when it asks for the username
- The game should load now in a separate panel GUI . (Run the terminal with -X if running in khoury)
- Add optional ```--observe``` flag to server to watch the whole game
- add ```--games <int>``` to indicate how many games to play. 

USER INPUT:
- User can make input by pressing the arrow keys and then enter
- Follow prompts on the screen and keep moving to find the key and then exit

SHUT DOWN: 
Any client can exit by exiting out of the GUI window. Also, at the end of each game, the user can hit
the enter key to stop all games.
