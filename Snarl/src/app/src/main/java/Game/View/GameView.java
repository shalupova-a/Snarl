package Game.View;

import Game.Model.*;
import Game.UI.TextureType;
import Remote.Client;
import asciiPanel.AsciiPanel;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class GameView extends JFrame implements KeyListener {
    protected AsciiPanel gamePanel;
    protected AsciiPanel playerInfoPanel;
    protected AsciiPanel storyPanel;
    protected Integer[][] layout;
    protected List<Item> items;
    protected List<ActorPosition> actors;
    protected Client client;
    protected Integer[] currentPlayerPosn;


    public abstract void beginGameScreen(String name);
    /**
     * An abstract method that must be implemented by the sub class to print the layout
     * Eg : Client view can print the layout for a 5 x 5 square in the world
     *      Observer view can build the whole level with corners and walls
     * Implementing view can choose how to print the layout for their respective views
     * <p>
     * Origin is the coordinate for the room's top left corner. for hallway section it can be the from point or waypoint
     *
     * @param grid:   Integer[][]
     * @param origin: Integer[]
     */
    public abstract void print2dArray(Integer[][] grid, Integer[] origin);


    /**
     * Initialize the panels
     */
    protected void initializeClient() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        ViewPanelFactory viewPanelFactory = new ViewPanelFactory();
        gamePanel = viewPanelFactory.getAsciiPanel("GAME");
        storyPanel = viewPanelFactory.getAsciiPanel("STORY");
        playerInfoPanel = viewPanelFactory.getAsciiPanel("INVENTORY");
        this.layout = new Integer[5][5];
        this.actors = new ArrayList<>();
        this.items = new ArrayList<>();
        this.currentPlayerPosn = new Integer[2];
    }


    /**
     * Set layout of level
     * @param layout
     */
    public void setLayout(Integer[][] layout) {
        this.layout = layout;
    }

    /**
     * Set items of level
     * @param items
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * Set actors of level
     * @param actors
     */
    public void setActors(List<ActorPosition> actors) {
        this.actors = actors;
    }

    /**
     * Set posn of current player
     * @param position
     */
    public void setCurrentPlayerPosn(Integer[] position) {
        this.currentPlayerPosn = position;
    }

    /**
     * To be diaplyed at the beginning of each level
     * @param levelId
     * @throws Exception
     */
    public void welcomeScreen(int levelId) throws Exception {
        gamePanel.clear();
        storyPanel.clear();
        repaint();
        if (levelId == 1) {
            gamePanel.writeCenter(TextureType.BLOCK.getCodePoint() + "Welcome to Snarl!" + TextureType.BLOCK.getCodePoint(), 12, Color.cyan);
        }
        add(gamePanel);
        pack();
        // 13 is the 13 line in the panel
        gamePanel.writeCenter("Starting level " + levelId + " in 5 seconds...", 13);
        storyPanel.repaint();
        gamePanel.repaint();

    }

    /**
     * To be displayed at start of each level/game
     * @param levelId
     * @param gameId
     * @throws Exception
     */
    public void welcomeScreen(int levelId, int gameId) throws Exception {
        gamePanel.clear();
        storyPanel.clear();
        repaint();
        if (levelId == 1) {
            gamePanel.writeCenter(TextureType.BLOCK.getCodePoint() + "Welcome to Snarl!" + TextureType.BLOCK.getCodePoint(), 12, Color.cyan);
        }
        add(gamePanel);
        pack();

        gamePanel.writeCenter("Starting game " + gameId + " level " + levelId + " in 5 seconds", 13);
        storyPanel.repaint();
        gamePanel.repaint();

    }

    /**
     * To be displayed at end of game
     * @param message
     * @param playerScoreList
     * @throws Exception
     */
    public void endGameScreen(String message, JSONArray playerScoreList) throws Exception {
        gamePanel.clear();
        storyPanel.clear();
        repaint();
        gamePanel.writeCenter(message, 10, Color.green);

        for (int i = 0; i < playerScoreList.length(); i++) {
            JSONObject playerScore = playerScoreList.getJSONObject(i);
            updateStoryPanel(playerScore.get("name").toString() + " had " +
                    playerScore.get("exits") + " exits, " + playerScore.get("ejects") + " ejects, and "
                    + playerScore.get("keys") + " keys.", 5 + i * 2);
        }
        pack();
        repaint();
        Thread.sleep(5000);
    }

    /**
     * Show this screen at the end of the level
     * @param key
     * @param exits
     * @param ejects
     * @throws Exception
     */
    public void endLevelScreen(String key, String exits, String ejects) throws Exception {
        gamePanel.clear();
        repaint();
        updateStoryPanel("End of level.");
        gamePanel.writeCenter(key + " had key.", 7, Color.cyan);
        gamePanel.writeCenter(exits + " exited", 9, Color.cyan);
        gamePanel.writeCenter(ejects + " was ejected.", 11, Color.cyan);
        pack();
        repaint();
    }

    /**
     * Helper method to print rooms, Client view cannot use this since it has no concept of rooms
     * @param roomList
     */
    protected void printRooms(List<Room> roomList) {
        for (Room room : roomList) {
            Integer[][] tiles = room.getLayout();
            print2dArray(room.getLayout(), room.getOrigin());
        }
    }

    /**
     * Print hallways
     * @param hallways
     */
    protected void printHallways(List<Hallway> hallways) {
        for (Hallway hallway : hallways) {
            try {
                printRooms(hallway.populateSections());
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
                System.out.println("hallway build exception");
            }
        }
    }

    /**
     * Print items
     * @param items
     */
    protected void placeItems(List<Item> items) {
        for (Item item : items) {
            if (item.getType() == ItemType.key) {
                writeTextureToGamePanel(item.getPosition()[1], item.getPosition()[0], TextureType.KEY);
            } else if (item.getType() == ItemType.exit) {
                writeTextureToGamePanel(item.getPosition()[1], item.getPosition()[0], TextureType.EXIT);
            }
        }
    }

    /**
     * Print actors
     * @param actors
     */
    protected void placeActors(List<ActorPosition> actors) {
        for (ActorPosition actor : actors) {
            if (actor.getType().equals(ActorType.ghost)) {
                writeTextureToGamePanel(actor.getPosition()[1], actor.getPosition()[0], TextureType.GHOST);
                continue;
            } else if (actor.getType().equals(ActorType.zombie)) {
                writeTextureToGamePanel(actor.getPosition()[1], actor.getPosition()[0], TextureType.ZOMBIE);
                continue;
            }

            if (!actor.isActorInPosition(this.currentPlayerPosn)) {
                writeTextureToGamePanel(actor.getPosition()[1], actor.getPosition()[0], TextureType.OTHER_PLAYER);

            } else {
                this.playerInfoPanel.clear();
                this.playerInfoPanel.repaint();
                initPanel(playerInfoPanel, "Player Info");
                this.playerInfoPanel.write("HP:", 2, 4, Color.green);
                this.playerInfoPanel.write(TextureType.HP_BAR_BLOCK.getCodePoint(), 5, 4, Color.red);
                this.playerInfoPanel.write(actor.getHitPoints()+"", 6, 4, Color.green);
                this.playerInfoPanel.repaint();
                writeTextureToGamePanel(actor.getPosition()[1], actor.getPosition()[0], TextureType.PLAYER);
            }
        }
    }

    /**
     * Write to given panel at given x, y
     * @param x
     * @param y
     * @param tt
     * @param asciiPanel
     */
    protected void writeTextureToPanel(int x, int y, TextureType tt, AsciiPanel asciiPanel) {
        asciiPanel.write(tt.getCodePoint(), x, y, tt.getColor());
        asciiPanel.repaint();
    }

    /**
     * Write given color to given panel at given x, y
     * @param x
     * @param y
     * @param tt
     * @param color
     * @param asciiPanel
     */
    protected void writeTextureToPanel(int x, int y, TextureType tt, Color color, AsciiPanel asciiPanel) {
        asciiPanel.write(tt.getCodePoint(), x, y, color != null ? color : tt.getColor());
        asciiPanel.repaint();
    }

    /**
     * Write to gamePanel
     * @param x
     * @param y
     * @param tt
     */
    protected void writeTextureToGamePanel(int x, int y, TextureType tt) {
        gamePanel.write(tt.getCodePoint(), x, y, tt.getColor());
    }

    /**
     * For the purpose of writing text to multiple lines of story panel
     **/
    public void updateStoryPanel(String text, int lineNum) {
        storyPanel.writeCenter(text, lineNum);
        initPanel(storyPanel, "Snarl log");
        storyPanel.repaint();
    }

    /**
     * public method to write stuff to story panel (panel at the bottom of screen) to give feedback to players and build the game story
     **/
    public void updateStoryPanel(String text) {
        storyPanel.clear();
        storyPanel.writeCenter(text, 10);
        initPanel(storyPanel, "Snarl log");
        storyPanel.repaint();
    }

    /**
     * Creates the story panel which is the panel at the bottom that shows what the player should do, ask for moves etc...
     */
    protected void initPanel(AsciiPanel asciiPanel, String panelTitle) {
        int W = asciiPanel.getWidthInCharacters();
        int H = asciiPanel.getHeightInCharacters();
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < H; j++) {
                if (TileUtils.isBorder(i, j, W - 1, H - 1)) {
                    if (i == 0 || i == W - 1) {
                        if (TileUtils.isCornerTile(i, j, W - 1, H - 1)) {
                            if (TileUtils.isRightCorner(i, j, W - 1, H - 1)) {
                                writeTextureToPanel(i, j, TextureType.BOTTOM_LEFT_CORNER, Color.darkGray, asciiPanel);
                            } else if (TileUtils.isLeftCorner(i, j)) {
                                writeTextureToPanel(i, j, TextureType.TOP_LEFT_CORNER, Color.darkGray, asciiPanel);
                            } else if (TileUtils.isBottomLeftCorner(i, j, W - 1, H - 1)) {
                                writeTextureToPanel(i, j, TextureType.TOP_RIGHT_CORNER, Color.darkGray, asciiPanel);

                            } else if (TileUtils.isBottomRightCorner(i, j, W - 1, H - 1)) {
                                writeTextureToPanel(i, j, TextureType.BOTTOM_RIGHT_CORNER, Color.darkGray, asciiPanel);

                            }
                            continue;
                        }
                        writeTextureToPanel(i, j, TextureType.VERTICAL_WALL, asciiPanel);
                    } else {
                        writeTextureToPanel(i, j, TextureType.HORIZONTAL_WALL, asciiPanel);
                    }
                }
            }
        }
        asciiPanel.write(panelTitle, 2, 1);
    }

    public void repaint() {
        gamePanel.repaint();
    }

    /**
     * Stub needs to be added to satisfy contract
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Stub needs to be added to satisfy contract
     * @param e keyevent
     */
    @Override
    public void keyPressed(KeyEvent e) {

    }

    /**
     * Handles key released event from the user
     * Based on if it is one of the cardinal direction, update the player position
     * @param e - key event
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_RIGHT:
                movePlayerBy(0, 1);
                break;
            case KeyEvent.VK_LEFT:
                movePlayerBy(0, -1);
                break;
            case KeyEvent.VK_UP:
                movePlayerBy(-1, 0);
                break;
            case KeyEvent.VK_DOWN:
                movePlayerBy(1, 0);
                break;
            case KeyEvent.VK_ENTER:
                this.client.transitionScreens(keyCode, e.getKeyChar(), e.isActionKey(), this.currentPlayerPosn);
                break;
            default:
                //nothing
                break;
        }

    }

    /**
     * Move player to new posn
     * @param y
     * @param x
     */
    private void movePlayerBy(int y, int x) {
        this.currentPlayerPosn = new Integer[]{this.currentPlayerPosn[0] + y, this.currentPlayerPosn[1] + x};
        System.out.println(Arrays.toString(this.currentPlayerPosn));
    }

}
