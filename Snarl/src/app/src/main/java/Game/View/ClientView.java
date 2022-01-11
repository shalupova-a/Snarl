package Game.View;

import Game.UI.*;
import Remote.Client;
import java.awt.*;

public class ClientView extends GameView {


    public ClientView(Client client, String player) {
        this.setTitle(player + " view");
        this.client = client;
        initializeClient();
    }

    /**
     * To be displayed at the beginning of each game
     * @param name
     */
    @Override
    public void beginGameScreen(String name) {
        gamePanel.clear();
        repaint();
        add(playerInfoPanel, BorderLayout.EAST);
        add(storyPanel, BorderLayout.SOUTH);
        add(gamePanel, BorderLayout.WEST);
        storyPanel.writeCenter("Hello " + name + ", this is the color of your player.", 7, TextureType.PLAYER.getColor());
        print2dArray(layout, null);
        placeItems(items);
        placeActors(actors);
        pack();
        initPanel(storyPanel, "Snarl log");
        initPanel(playerInfoPanel, "Player Info");
        repaint();
    }

    /**
     * Updates game screen
     */
    public void updateGameScreen() {
        gamePanel.clear();
        print2dArray(layout, null);
        placeItems(items);
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
     * @param grid:   char[][]
     * @param origin: Integer[]
     */
    @Override
    public void print2dArray(Integer[][] grid, Integer[] origin) {

        origin = new Integer[2];

        origin[0] = Math.max(0, currentPlayerPosn[0] - 2);
        origin[1] = Math.max(0, currentPlayerPosn[1] - 2);

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                int tileInput = grid[i][j];
                int x = j + origin[1];
                int y = i + origin[0];

                // BLANK SPACE
                if (tileInput == TextureType.BLANK.getTileInput()) {
                    writeTextureToGamePanel(x, y, TextureType.BLANK);
                    // DOOR
                } else if (tileInput == TextureType.DOOR.getTileInput()) {
                    writeTextureToGamePanel(x, y, TextureType.DOOR);
                    // WALLS, we are checking corners so it has a nice smooth edge!
                } else if (tileInput == TextureType.BLOCK.getTileInput()) {
                    writeTextureToGamePanel(x, y, TextureType.BLOCK);
                } else {
                    //System.out.println("DEBUG " + tileInput);
                }
            }
        }
    }

}
