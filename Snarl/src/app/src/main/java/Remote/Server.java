package Remote;

import Game.Controller.GameManager;
import Game.Model.ActorPosition;
import Game.Model.Level;
import Game.NetworkMessage.MessageType;
import Game.NetworkMessage.NetworkOutMessage;
import Game.NetworkMessage.PlayerMove;
import Game.NetworkMessage.PlayersUpdateMessage;
import Game.View.ObserverView;
import Player.LocalPlayer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    private static final int MAX_PLAYERS = 4;
    private int maxPlayers;
    private ServerSocket serverSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String host;
    private int port;
    private GameManager manager;
    private List<Socket> sockets = new ArrayList<>();
    private List<Level> levels;
    private boolean isObserver;
    private Map<Socket, LocalPlayer> socketMap = new LinkedHashMap<>();
    private JSONObject endLevelJSON;
    private ObjectMapper objectMapper = new ObjectMapper();
    private int levelId;
    private boolean progressLevel = false;
    private boolean progressGame = false;
    private int wait;
    private int numGames;
    private int gameId;
    private Map<String, JSONObject> leaderboard = new HashMap<>();
    private Map<String, JSONObject> playerScoreMap = new HashMap<>();
    private ObserverView gameView;
    private Level currentLevel;

    public Server(String host, int port, int maxPlayers, JSONArray levels, boolean isObserver, int wait, int games) throws Exception {
        this.host = host;
        this.port = port;
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        if (maxPlayers > MAX_PLAYERS) {
            this.maxPlayers = MAX_PLAYERS;
        } else {
            this.maxPlayers = maxPlayers;
        }
        this.levels = objectMapper.readValue(levels.toString(), new TypeReference<List<Level>>() {});
        this.isObserver = isObserver;
        this.wait = wait;
        this.numGames = games;
        this.currentLevel = this.levels.get(0);
    }

    /**
     * Connect to all clients and play the game(s)
     * @throws Exception
     */
    public void startConnection() throws Exception {

        serverSocket = new ServerSocket(port);
        System.out.println("Waiting for " + maxPlayers + " users to register.");

        waitForPlayers(maxPlayers);

        sendMessageToAll(MessageType.WELCOME, "Welcome to the Snarl Game!" +
                "\nAuthors: Angelina Shalupova, Sheetal Singh" +
                "\nGithub Repo: Ilievrad");
        // asks and gets name
        sendMessageToAll(MessageType.NAME, "Please enter your name.");

        if(this.isObserver) {
            gameView = new ObserverView();
        }
        playGames(); // play!

        disconnect(); // disconnect once games have been played
    }

    /**
     * Wait for all the players to connect
     * @param numPlayers
     */
    private void waitForPlayers(int numPlayers) {
        for(int i = 0; i < numPlayers; i++) {
            Socket socket;
            try {
                serverSocket.setSoTimeout(this.wait * 1000);
                socket = serverSocket.accept();
                System.out.println("Connected to: " + socket.getRemoteSocketAddress());
                sockets.add(socket);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    /**
     * Play the game(s)
     * @throws Exception
     */
    private void playGames() throws Exception {
        setItemsCopy(); // if we're playing multiple games, we want to repopulate the key for a level
        for(int i = 0; i < this.numGames; i++) {

            this.gameId = i;

            // start levels over for each game
            for (int j = 0; j < levels.size(); j++) {
                this.currentLevel = levels.get(j);
                this.levelId = j;
                initEndLevelJSON();
                this.manager = new GameManager(levelId, currentLevel, new ArrayList<>(socketMap.values()));

                updateInitialPlayerLocations();

                sendMessageToAll(MessageType.START_LEVEL, "");

                if(this.isObserver) {
                    gameView.beginGameScreen("observer");
                }
                sendPlayerUpdate(""); // send initial player update

                // keep sending moves until game is over
                while (!progressGame) {
                    System.out.println("Sending move request");
                    sendMoveRequest();
                }
                progressGame = false;
                uneject(); // put all expelled players back in

                // determine if players made it to the next level
                if(!progressLevel) {
                    System.out.println("Not progressing level");
                    break;
                }
                progressLevel = false;
            }
            sendMessageToAll(MessageType.END_GAME, ""); // end of game message

            clearPlayerScoreMap(); //clear player score map for next game
        }
    }

    /**
     * Creates JSON to send when level has ended
     */
    private void initEndLevelJSON() {
        this.endLevelJSON = new JSONObject();
        endLevelJSON.put("type", "end-level");
        endLevelJSON.put("key", "");
        endLevelJSON.put("exits", new JSONArray());
        endLevelJSON.put("ejects", new JSONArray());
    }

    /**
     * Send message to all the players
     * @param messageType
     * @param message
     * @throws Exception
     */
    private void sendMessageToAll(MessageType messageType, String message) throws Exception {
        for(Socket socket : this.sockets) {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                JSONObject jsonMsg = new JSONObject();
                jsonMsg.put("type", messageType.getMessageTypeString());

                if(messageType == MessageType.NAME) {
                    jsonMsg.put("info", message);
                    out.println(jsonMsg);
                    String name = in.readLine();

                    // request again if name is taken
                    while (!checkValidName(name, getNames())) {
                        jsonMsg.put("info", "Name already taken. " + message);
                        out.println(jsonMsg);
                        name = in.readLine();
                    }

                    socketMap.put(socket, new LocalPlayer(name));
                    initPlayerScoreMaps(name);

                }
                else if(messageType == MessageType.START_LEVEL) {
                    jsonMsg.put("level", levelId);
                    jsonMsg.put("players", getNames());
                    if(numGames > 1) {
                        jsonMsg.put("game", gameId);
                    }
                    out.println(jsonMsg);

                    if(this.isObserver) {
                        updateObserverStart();
                    }

                    Thread.sleep(5000);
                } else if(messageType == MessageType.END_LEVEL){
                    jsonMsg.put("key", endLevelJSON.get("key"));
                    jsonMsg.put("exits", endLevelJSON.get("exits"));
                    jsonMsg.put("ejects", endLevelJSON.get("ejects"));
                    out.println(jsonMsg);

                    if(this.isObserver) {
                        updateObserverEndLevel();
                    }
                    Thread.sleep(5000);
                }
                else if(messageType == MessageType.END_GAME) {
                    jsonMsg.put("scores", getPlayerScoreArray(playerScoreMap));

                    // if this is the last game, we send the leaderboard and disconnect
                    if(this.gameId + 1 == this.numGames) {
                        jsonMsg.put("leaderboard", getPlayerScoreArray(leaderboard));
                        out.println(jsonMsg);

                        if(this.isObserver) {
                            updateObserverEndGame(true);
                        }
                        Thread.sleep(5000);
                        disconnect();
                    } else {
                        out.println(jsonMsg);
                        if(this.isObserver) {
                            updateObserverEndGame(false);
                        }
                    }

                } else {
                    jsonMsg.put("info", message);
                    out.println(jsonMsg);
                }
            }
            catch(IOException e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    /**
     * Create entry in playerScoreMap and leaderboard for given name
     * @param name
     */
    private void initPlayerScoreMaps(String name) {
        playerScoreMap.put(name, createPlayerScore(name));
        leaderboard.put(name, createPlayerScore(name));
    }

    /**
     * Create a JSONObject to keep track of the given player's score
     * @param name
     * @return
     */
    private JSONObject createPlayerScore(String name) {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("type", "player-score");
        jsonObj.put("name", name);
        jsonObj.put("exits", 0);
        jsonObj.put("ejects", 0);
        jsonObj.put("keys", 0);
        return jsonObj;
    }

    /**
     * Convert playerScoreMap into an array
     * @param playerScoreMap
     * @return JSONArray
     */
    private JSONArray getPlayerScoreArray(Map<String, JSONObject> playerScoreMap) {
        JSONArray playerScoreArray = new JSONArray();
        for(JSONObject jsonObj : playerScoreMap.values()) {
            playerScoreArray.put(jsonObj);
        }
        return playerScoreArray;
    }

    /**
     * Send update to all players with given message
     * @param message
     */
    public void sendPlayerUpdate(String message) throws Exception {
        for (Map.Entry mapElement : socketMap.entrySet()) {
            try {
                Socket socket = (Socket) mapElement.getKey();
                LocalPlayer localPlayer = (LocalPlayer) mapElement.getValue();

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                Integer[][] layout = this.manager.getLayout(localPlayer.getPosition());
                PlayersUpdateMessage playersUpdateMessage = new PlayersUpdateMessage(Game.NetworkMessage.MessageType.PLAYER_UPDATE,
                        layout,
                        localPlayer.getPosition(),
                        this.manager.getNearbyObjects(localPlayer.getPosition()),
                        this.manager.getNearbyActors(localPlayer.getPosition()),
                        message);
                out.println(new JSONObject(objectMapper.writeValueAsString(playersUpdateMessage)));

                if(this.isObserver) {
                    updateObserver(message);
                }

                Thread.sleep(2000);

            } catch(IOException e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    private void updateObserver(String message) {
        gameView.updateGameScreen(this.manager.getState().getLevel(), this.manager.getState().getActors());
        if(!message.equals("")) {
            gameView.updateStoryPanel(message);
        }
    }

    /**
     * Send move request to each player
     * @throws Exception
     */
    public void sendMoveRequest() throws Exception {
        for (Map.Entry mapElement : socketMap.entrySet()) {
            Socket socket = (Socket) mapElement.getKey();
            LocalPlayer localPlayer = (LocalPlayer) mapElement.getValue();
            String response;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                boolean validMove = false;
                int tries = 0;
                // keep getting move as long it's valid or run out of tries
                while (!validMove && tries < 3 && !localPlayer.getExpelled()) {

                    // in case previous adversary move expelled player
                    if (checkExpelled(localPlayer)) {
                        NetworkOutMessage networkOutMessage = this.manager.doCombat(localPlayer.getName(), localPlayer.getPosition());
                        if (networkOutMessage.equals(NetworkOutMessage.EJECTED)) {
                            expel(localPlayer);
                            sendNetworkedMessage(networkOutMessage, localPlayer.getName(), out);
                            break;
                        }
                        sendNetworkedMessage(networkOutMessage, localPlayer.getName(), out);
                    }

                    tries++;
                    response = sendAndGetResponse("move", out); // send move request
                    PlayerMove playerMove = objectMapper.readValue(response, PlayerMove.class);
                    validMove = validateMoveAndSendResponse(localPlayer, playerMove, out); // check if move is valid
                }

            } catch (IOException e) {
                // Player got disconnected
                System.out.println(e);
                handlePlayerDisconnection(localPlayer);
            }
        }

        if(allPlayersExpelled()) {
            System.out.println("all players expelled");
            progressGame = true;
            if(progressLevel) {
                sendMessageToAll(MessageType.END_LEVEL, "");
            } else {
                System.out.println("lost");
            }
        } else {
            this.manager.beckonAdversaryMove();
            if (this.manager.getState().getAdversaries().size() != 0) {
                sendPlayerUpdate("adversaries moved.");
            }
        }
    }

    /**
     * Handling for a player getting disconnected
     * @param localPlayer
     * @throws Exception
     */
    private void handlePlayerDisconnection(LocalPlayer localPlayer) throws Exception {

        // remove socket
        socketMap = socketMap.entrySet()
                .stream()
                .filter(entry -> !checkNameEquality(entry.getValue(), localPlayer.getName()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
        this.manager.getState().removePlayer(localPlayer.getName()); // remove assoc player

        sendPlayerUpdate("player " + localPlayer.getName() + " disconnected.");

        // if there are no more sockets left, end the game
        if(socketMap.isEmpty()) {
            if(isObserver) {
                updateObserver("All clients disconnected");
                Thread.sleep(1000);
            }
            System.exit(0);
        }
    }

    /**
     * Check if a player has been expelled
     * @param player
     * @return boolean
     * @throws Exception
     */
    private boolean checkExpelled(LocalPlayer player) throws Exception {
        return this.manager.ejected(player.getPosition());
    }

    /**
     * Check if a player's move is valid and send message to players
     * @param localPlayer
     * @param playerMove
     * @param out
     * @return boolean
     * @throws Exception
     */
    private boolean validateMoveAndSendResponse(LocalPlayer localPlayer, PlayerMove playerMove, PrintWriter out) throws Exception {
        boolean isValid = true;
        Integer[] to = playerMove.getTo();
        NetworkOutMessage networkOutMessage = this.manager.playerMoveRequest(localPlayer.getPosition(), to, localPlayer);
        // Conditions to not move the player
        if (networkOutMessage == NetworkOutMessage.INVALID) {
            sendNetworkedMessage(networkOutMessage, localPlayer.getName(), out);
            return false;
        } else if (networkOutMessage == NetworkOutMessage.OK_STAYED_PUT) {
            sendNetworkedMessage(networkOutMessage, localPlayer.getName(), out);
            return true;
        } else if (networkOutMessage == NetworkOutMessage.DYNAMIC_DAMAGE_MESSAGE
                || networkOutMessage == NetworkOutMessage.DAMAGE_KILLED_ENEMY) {
            sendNetworkedMessage(networkOutMessage, localPlayer.getName(), out);
            localPlayer.updatePlayerPosition(to);
            updatePlayerLocations();
            return true;
        }

        // Conditions to move the player
        localPlayer.updatePlayerPosition(to);
        updatePlayerLocations();
        if (networkOutMessage == NetworkOutMessage.OK) {
            // nothing yet
        } else if (networkOutMessage == NetworkOutMessage.EJECTED) {
            expel(localPlayer);
        } else if (networkOutMessage == NetworkOutMessage.STEPPED_ON_EXIT) {
            updatePlayerScoreMap(localPlayer.getName(), "exits");
            endLevelJSON.put("exits", endLevelJSON.getJSONArray("exits").put(localPlayer.getName()));
            localPlayer.setExpelled(true);
            progressLevel = true;
        } else if (networkOutMessage == NetworkOutMessage.STEPPED_ON_EXIT_NO_KEY) {
            // nothing yet
        } else if (networkOutMessage == NetworkOutMessage.STEPPED_ON_KEY) {
            updatePlayerScoreMap(localPlayer.getName(), "keys");
            endLevelJSON.put("key", localPlayer.getName());
        } else {
            throw new Exception("invalid network message asked to be sent by game manager");
        }

        sendNetworkedMessage(networkOutMessage, localPlayer.getName(), out);

        return isValid;
    }

    /**
     * Send player update
     * @param networkOutMessage
     * @param playerName
     * @param out
     * @throws Exception
     */
    private void sendNetworkedMessage(NetworkOutMessage networkOutMessage, String playerName, PrintWriter out) throws Exception {
        if (!("").equals(networkOutMessage.getNetworkValue())) {
            out.println(networkOutMessage.getNetworkValue());
        }
        if (!networkOutMessage.getNetworkMessage().equals("")) {
            sendPlayerUpdate(MessageFormat.format(networkOutMessage.getNetworkMessage(), playerName));
        }
    }

    /**
     * Send a request to the player and read in response
     * @param request
     * @param out
     * @return String response
     */
    private String sendAndGetResponse(String request, PrintWriter out) {
        out.println(request);
        try {
            return in.readLine();
        } catch (IOException e) {
            System.out.println("Exception Sending request and response for " + request);
            e.printStackTrace();
        }
        return request;
    }

    /**
     * Check if all players expelled
     * @return boolean
     */
    private boolean allPlayersExpelled() {
        return socketMap.entrySet()
                .stream()
                .allMatch(entry -> entry.getValue().getExpelled());
    }

    /**
     * Set local player expelled
     * @param localPlayer
     */
    private void expel(LocalPlayer localPlayer) {

        updatePlayerScoreMap(localPlayer.getName(), "ejects");
        endLevelJSON.put("ejects", endLevelJSON.getJSONArray("ejects").put(localPlayer.getName()));
        // set player to expelled in game state
        this.manager.getState().removePlayer(localPlayer.getName());
        localPlayer.setExpelled(true);
    }

    /**
     * Updare player score for given name
     * @param name
     * @param key
     */
    private void updatePlayerScoreMap(String name, String key) {
        JSONObject playerScore = playerScoreMap.get(name);
        playerScore.put(key, playerScore.getInt(key) + 1);
        playerScore = leaderboard.get(name);
        playerScore.put(key, playerScore.getInt(key) + 1);
    }

    /**
     * Set all player scores back to 0
     */
    private void clearPlayerScoreMap() {
        playerScoreMap = new HashMap<>();
        for(String name : getNames()) {
            playerScoreMap.put(name, createPlayerScore(name));
        }
    }

    /**
     * Check if player's name is equal to given string
     * @param player
     * @param name
     * @return boolean
     */
    private boolean checkNameEquality(LocalPlayer player, String name) {

        return player.getName().equals(name);
    }

    /**
     * Check given name isnt equal to any given names
     * @param name
     * @param names
     * @return boolean
     */
    private boolean checkValidName(String name, List<String> names) {

        for(String n : names) {
            if(n.equals(name)) {
                System.out.println("Invalid name!");
                return false;
            }
        }

        return true;
    }

    /**
     * Get names of all players
     * @return List<String>
     */
    private List<String> getNames() {
        List<String> names = new ArrayList<>();
        for(LocalPlayer player : socketMap.values()) {
            names.add(player.getName());
        }

        return names;
    }

    /**
     * Set actor's position based on local player's position
     */
    private void updatePlayerLocations() {
        List<ActorPosition> players = this.manager.getState().getPlayers();

        for(LocalPlayer player : socketMap.values()) {
            for(ActorPosition actor : players) {
                if(player.getName().equals(actor.getName())) {
                    actor.setPosition(player.getPosition());
                }
            }
        }
    }

    /**
     * Set local player's position based on corresponding actor
     * @throws Exception
     */
    private void updateInitialPlayerLocations() throws Exception {
        List<ActorPosition> players = this.manager.getState().getPlayers();

        for(LocalPlayer player : socketMap.values()) {
            for(ActorPosition actor : players) {
                if(player.getName().equals(actor.getName())) {
                    player.updatePlayerPosition(actor.getPosition());
                }
            }
        }
    }

    /**
     * Put the player back in the game (no longer expelled)
     */
    private void uneject() {
        for (Map.Entry mapElement : socketMap.entrySet()) {
            LocalPlayer player = (LocalPlayer) mapElement.getValue();
            player.setExpelled(false);
        }
    }

    /**
     * Set items copy list in level.
     * Useful for multiple games, when a key has been picked up
     */
    private void setItemsCopy() {
        for(Level level : this.levels) {
            level.setItemsCopy();
        }
    }

    /**
     * Update the observer when the game starts
     */
    private void updateObserverStart() throws Exception {
        gameView.setState(currentLevel, this.manager.getState().getPlayers(), this.manager.getState().getAdversaries());
        if(numGames > 1) {
            gameView.welcomeScreen(levelId + 1, gameId + 1);
        } else {
            gameView.welcomeScreen(levelId + 1);
        }
    }

    /**
     * Update the observer when the level ends
     */
    private void updateObserverEndLevel() throws Exception {
        gameView.endLevelScreen(endLevelJSON.getString("key"), formatJSONArray(endLevelJSON.getJSONArray("exits")),
                formatJSONArray(endLevelJSON.getJSONArray("ejects")));
    }

    private String formatJSONArray(JSONArray jsonArr) {
        StringBuilder formatString = new StringBuilder();
        for (int i = 0; i < jsonArr.length(); i++) {
            formatString.append(jsonArr.get(i));
            if (i != jsonArr.length() - 1) {
                formatString.append(", ");
            }
        }

        if (formatString.toString().equals("")) {
            formatString = new StringBuilder("no one");
        }

        return formatString.toString();
    }

    /**
     * Update observer when the games(s) end
     * @param lastGame
     * @throws Exception
     */
    private void updateObserverEndGame(boolean lastGame) throws Exception {
        gameView.endGameScreen("End of game.", getPlayerScoreArray(playerScoreMap));

        if(lastGame) {
            gameView.endGameScreen("End of all games. Check out the leaderboard:", getPlayerScoreArray(leaderboard));
        }
    }

    /**
     * Close sockets
     * @throws Exception
     */
    private void disconnect() throws Exception {
        if(isObserver) {
            gameView.setVisible(false);
            gameView.dispose();
        }
        in.close();
        out.close();
        serverSocket.close();
    }
}