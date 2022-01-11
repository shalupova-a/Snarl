package Remote;

import Game.NetworkMessage.MessageType;
import Game.NetworkMessage.NetworkOutMessage;
import Game.NetworkMessage.PlayersUpdateMessage;
import Game.UI.GameViewType;
import Game.View.ClientView;
import Player.LocalPlayer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner = new Scanner(System.in);
    private ClientView clientView;
    private LocalPlayer player;
    private PlayersUpdateMessage playersUpdateMessage;
    private GameViewType currentViewType;
    StringBuilder userNameBuilder = new StringBuilder();
    private ObjectMapper objectMapper = new ObjectMapper();
    private int levelId;
    private Integer[] currentPlayerPosn;
    private boolean ejected = false;

    public Client(String address, int port) throws Exception {
        this.currentViewType = GameViewType.START_SCREEN;
        clientSocket = new Socket(address, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        playersUpdateMessage = new PlayersUpdateMessage();
        this.player = null;
        this.clientView = null;
        this.levelId = 0;
        currentPlayerPosn = new Integer[2];
    }

    /**
     * Starts client connection to get network messages
     * Messages are of type Message type and NetworkOutType
     * NetworkOutType are type of messages sent standalone using out stream
     * MessageType are messages that are sent as part of json
     * @throws Exception
     */
    public void startConnection() throws Exception {

        String inputLine = in.readLine();
        System.out.println(inputLine); // welcome message

        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
            // Player level messages that define player movement
            // MOVE
            if (inputLine.equals(MessageType.MOVE.getMessageTypeString())) {
                updateClientView();
                clientView.updateStoryPanel("Use arrow keys to make move and press enter when you are done!");
                clientView.setFocusable(true);
            // OK Message
            } else if (inputLine.equals(NetworkOutMessage.OK.getNetworkValue())) {
                player.updatePlayerPosition(currentPlayerPosn);
                updateClientView();
            // INVALID move message
            } else if (inputLine.equals(NetworkOutMessage.INVALID.getNetworkValue())) {
                currentPlayerPosn = player.getPosition();
                clientView.updateStoryPanel("Invalid move.");
                Thread.sleep(2000);
            // Player STEPPED_ON_KEY
            } else if (inputLine.equals(NetworkOutMessage.STEPPED_ON_KEY.getNetworkValue())) {
                clientView.updateStoryPanel("Found key");
            // Player STEPPED_ON_EXIT
            } else if (inputLine.equals(NetworkOutMessage.STEPPED_ON_EXIT.getNetworkValue())) {
                currentViewType = GameViewType.LEVEL_END_SCREEN;
            // Player is EJECTED
            } else if (inputLine.equals(NetworkOutMessage.EJECTED.getNetworkValue())) {
                clientView.updateStoryPanel("You were ejected.");
                this.ejected = true;
            } else {
                // JSON messages sent to all players
                JSONObject jsonObject = new JSONObject(inputLine);
                String type = (String) jsonObject.get("type");
                // Ask for name to all players
                if (type.equals(MessageType.NAME.getMessageTypeString())) {
                    askForName();
                // START_LEVEL
                } else if (type.equals(MessageType.START_LEVEL.getMessageTypeString())) {
                    handleStartLevelMessage(jsonObject);
                // PLAYER Update message
                } else if (type.equals(MessageType.PLAYER_UPDATE.getMessageTypeString())) {
                    handlePlayerUpdateMessage(jsonObject);
                // GAME END Message
                } else if (type.equals(MessageType.END_GAME.getMessageTypeString())) {
                    handleEndGameMessage(jsonObject);
                // LEVEL END Message
                } else if (type.equals(MessageType.END_LEVEL.getMessageTypeString())) {
                    handleEndLevelMessage(jsonObject);
                } else {
                // Should not happen
                    System.out.println("Unknown");
                }
            }
        }
    }

    /**
     * Ask for player name in console
     * Send name to server and create a local plauer instance with the name
     * Build Client view for the player with name
     */
    private void askForName() {
        System.out.println("Please enter your name.");
        String name = scanner.next();

        this.player = new LocalPlayer(name);
        out.println(name);
        this.clientView = new ClientView(this, player.getName());
    }

    /**
     * Start level json from server is parsed
     * Set game screen and start the game with appropriate level id
     * @param jsonObject - json object containing start game per snarl remote protocol
     * @throws Exception
     */
    private void handleStartLevelMessage(JSONObject jsonObject) throws Exception {
        currentViewType = GameViewType.START_SCREEN;

        this.levelId = jsonObject.getInt("level");
        if (jsonObject.has("game")) {
            start(levelId + 1,  jsonObject.getInt("game") + 1);
        } else {
            start(levelId + 1);
        }
    }

    /**
     *  handles plauer update message
     *  contains information regarding the player and their surroundings
     *  update client view with the current information
     * @param jsonObject - optional message to show on screen from server
     * @throws Exception
     */
    private void handlePlayerUpdateMessage(JSONObject jsonObject) throws Exception {
        String message = jsonObject.get("message").toString();
        parsePlayerUpdate(jsonObject);
        if (currentViewType == GameViewType.START_SCREEN) {
            currentViewType = GameViewType.GAME_PLAY_SCREEN;
            updateClientView();
            clientView.beginGameScreen(player.getName());
        } else if (currentViewType == GameViewType.LEVEL_END_SCREEN) {
            updateClientView();
            clientView.updateGameScreen();
            clientView.updateStoryPanel("Found exit. You have passed the level!");
        } else {
            updateClientView();
            clientView.updateGameScreen();
            if (message != null) {
                clientView.updateStoryPanel(message);
            }
        }

        if (ejected) {
            clientView.updateStoryPanel("You are dead.", 11);
        }
    }

    /**
     * Handle end game message from server
     * Disconnect and show leaderboard if present
     * @param jsonObject - leaderboard json
     * @throws Exception
     */
    private void handleEndGameMessage(JSONObject jsonObject) throws Exception {
        ejected = false;
        currentViewType = GameViewType.END_GAME_SCREEN;

        // if there are no more games, disconnect
        if (jsonObject.has("leaderboard")) {
            System.out.println(jsonObject.getJSONArray("leaderboard"));
            clientView.endGameScreen("End of game. Hit enter if you want to exit.",
                    jsonObject.getJSONArray("scores"));
            clientView.endGameScreen("End of all games. Check out the leaderboard:",
                    jsonObject.getJSONArray("leaderboard"));
            disconnect();
        } else {
            clientView.endGameScreen("End of game. Hit enter if you want to exit.",
                    jsonObject.getJSONArray("scores"));
        }
    }

    /**
     * handle end level server message
     * @param jsonObject - has  information on end of level including keys, exit and ejects
     * @throws Exception
     */
    private void handleEndLevelMessage(JSONObject jsonObject) throws Exception {
        clientView.endLevelScreen(jsonObject.getString("key"), formatJSONArray(jsonObject.getJSONArray("exits")),
                formatJSONArray(jsonObject.getJSONArray("ejects")));
        ejected = false;
    }

    /**
     * Format JSONArray to String for the view
     * @param jsonArr
     * @return
     */
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
     * Update player position
     * @param playerUpdateJson
     * @throws Exception
     */
    private void parsePlayerUpdate(JSONObject playerUpdateJson) throws Exception {
        PlayersUpdateMessage playersUpdateMessage = objectMapper.readValue(playerUpdateJson.toString(), PlayersUpdateMessage.class);
        this.playersUpdateMessage = playersUpdateMessage;
        player.updatePlayerPosition(playersUpdateMessage.getPosition());
    }

    /**
     * Renders initial view with levelid.
     */
    private void start(int levelId) throws Exception {
        clientView.welcomeScreen(levelId);
    }

    /**
     * Renders initial view with levelId gameId
     * @param levelId
     * @param gameId
     * @throws Exception
     */
    private void start(int levelId, int gameId) throws Exception {
        clientView.welcomeScreen(levelId, gameId);
    }

    /**
     * Updates view with each player update
     */
    private void updateClientView() {
        clientView.setCurrentPlayerPosn(player.getPosition());
        clientView.setLayout(playersUpdateMessage.getLayout());
        clientView.setActors(playersUpdateMessage.getActors());
        clientView.setItems(playersUpdateMessage.getItems());
    }

    /**
     * Transition screens based on key input
     * @param keyCode
     * @param keyChar
     * @param isActionKey
     * @param playerPosn
     */
    public void transitionScreens(int keyCode, char keyChar, boolean isActionKey, Integer[] playerPosn) {
        if (currentViewType.equals(GameViewType.GAME_PLAY_SCREEN) && keyCode == KeyEvent.VK_ENTER) {
            try {
                JSONObject playerMove = new JSONObject();
                playerMove.put("type", "move");

                Integer[] position = player.getPosition();

                playerMove.put("to", "");
                if (position[0] != playerPosn[0] || position[1] != playerPosn[1]) {
                    playerMove.put("to", playerPosn);
                }
                currentPlayerPosn = playerPosn;
                out.println(playerMove);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (currentViewType.equals(GameViewType.MENU_SCREEN)) {
            // TODO: CLEANUP
            // i love this view which lets the user type thier name on the gui. but lets keep this in the cmd line for now till the network stuff is sorted out
            if (keyCode == KeyEvent.VK_ENTER) {
                //clientView.beginGameScreen();
            } else if (!isActionKey) {
                if (userNameBuilder.length() > 0 && keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_DELETE) {
                    userNameBuilder.setLength(userNameBuilder.length() - 1);
                } else {
                    userNameBuilder.append(keyChar);
                }
                //clientView.gameMenuScreen(userNameBuilder.toString());
            }
        } else if (currentViewType.equals(GameViewType.END_GAME_SCREEN) && keyCode == KeyEvent.VK_ENTER) {
            // shutting down game
            try {
                disconnect();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            // random keys being pressed, do nothing
        }
    }

    /**
     * Disconnect client
     * @throws Exception
     */
    private void disconnect() throws Exception {
        clientView.setVisible(false);
        clientView.dispose();
        in.close();
        out.close();
        clientSocket.close();
    }
}
