package Game.View;

import Game.Model.ActorPosition;
import Game.Model.Level;
import Game.UI.TextureType;

import java.awt.*;
import java.util.List;

public class ObserverView extends GameView {

    private Level level;
    private List<ActorPosition> players;
    private List<ActorPosition> adversaries;

    public ObserverView() {

        this.setTitle("Observer View");
        initializeClient();
        playerInfoPanel.clear();
    }

    /**
     * Set the current level, players, adversaries to display
     * @param level
     * @param players
     * @param adversaries
     */
    public void setState(Level level, List<ActorPosition> players, List<ActorPosition> adversaries) {
        this.level = level;
        this.players = players;
        this.adversaries = adversaries;
    }

    /**
     * To be displayed at beginning of each level/game
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
     * To be displayed at beginning of each level/game
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

        gamePanel.writeCenter("Starting level "  + levelId + " in 5 seconds", 13);
        storyPanel.repaint();
        gamePanel.repaint();
    }

    /**
     * To be displayed at the beginning of each game
     * @param name
     */
    @Override
    public void beginGameScreen(String name) {
        gamePanel.clear();
        storyPanel.clear();
        repaint();
        add(playerInfoPanel, BorderLayout.EAST);
        add(storyPanel, BorderLayout.SOUTH);
        add(gamePanel, BorderLayout.WEST);

        storyPanel.writeCenter("Hello " + name + ", you are in snarl game", 7);
        printHallways(level.getHallways());
        printRooms(level.getRooms());
        placeItems(level.getItems());
        placeActors(players);
        placeActors(adversaries);
        pack();
        gamePanel.repaint();
        storyPanel.repaint();
        initPanel(storyPanel, "Snarl log");
    }

    /**
     * Update current game screen when level/actors change
     * @param level
     * @param actors
     */
    public void updateGameScreen(Level level, List<ActorPosition> actors) {
        gamePanel.clear();
        printHallways(level.getHallways());
        printRooms(level.getRooms());
        placeItems(level.getItems());
        placeActors(actors);
        pack();
        gamePanel.repaint();
    }


    /**
     * Prints out a 2d array grid of valid chars
     * Instead of printing the whole game as a whole, this provides a small array which represents a room
     * Hallways can also be thought of as rooms with width or height set as 1 . so a hallway with 3 waypoints can be broken down as 3 rooms
     * <p>
     * Origin is the coordinate for the room's top left corner. for hallway section it can be the from point or waypoint
     *
     * @param grid:   Integer[][]
     * @param origin: Integer[]
     */
    @Override
    public void print2dArray(Integer[][] grid, Integer[] origin) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                int tileInput = grid[i][j];
                int x = j + origin[1];
                int y = i + origin[0];
                int w = grid.length - 1;
                int h = grid[i].length - 1;

                // BLANK SPACE
                if (tileInput == TextureType.BLANK.getTileInput()) {
                    writeTextureToGamePanel(x, y, TextureType.BLANK);
                    // DOOR
                } else if (tileInput == TextureType.DOOR.getTileInput()) {
                    writeTextureToGamePanel(x, y, TextureType.DOOR);
                    // WALLS, we are checking corners so it has a nice smooth edge!
                } else if (tileInput == TextureType.BLOCK.getTileInput() && TileUtils.isBorder(i, j, grid.length - 1, grid[i].length - 1)) {
                    if (TileUtils.isLeftCorner(i, j)) {
                        writeTextureToGamePanel(x, y, TextureType.TOP_LEFT_CORNER);
                    } else if (TileUtils.isBottomLeftCorner(i, j, w, h)) {
                        writeTextureToGamePanel(x, y, TextureType.BOTTOM_LEFT_CORNER);
                    } else if (TileUtils.isBottomRightCorner(i, j, w, h)) {
                        writeTextureToGamePanel(x, y, TextureType.BOTTOM_RIGHT_CORNER);
                    } else if (TileUtils.isRightCorner(i, j, w, h)) {
                        writeTextureToGamePanel(x, y, TextureType.TOP_RIGHT_CORNER);
                    } else {
                        // vertical border has vertical line and vice versa
                        if (TileUtils.isVerticalBorder(j, h)) {
                            writeTextureToGamePanel(x, y, TextureType.VERTICAL_WALL);
                        } else {
                            writeTextureToGamePanel(x, y, TextureType.HORIZONTAL_WALL);
                        }
                    }
                } else {
                    //System.out.println("Observer debug");
                }
            }
        }
    }
}