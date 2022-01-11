package Player;

import Game.Common.Player;

import java.util.Scanner;

public class LocalPlayer implements Player {
    private final Scanner scanner = new Scanner(System.in);
    private final int id;
    private final String name;
    private Integer[] position;
    private boolean expelled;

    public LocalPlayer(String name) {
        this.name = name;
        this.id = 0;
        this.position = null;
        this.expelled = false;
    }

    public LocalPlayer(int id, String name) throws Exception {
        this.id = id;
        this.name = name;
        this.position = null;
    }

    public int getId() {
        return id;
    }

    public String getName() { return this.name; }

    @Override
    public void updatePlayerPosition(Integer[] playerPosn) throws Exception {
        this.position = playerPosn;
    }

    public Integer[] getPosition() {
        return this.position;
    }

    public void setExpelled(boolean expelled) {
        this.expelled = expelled;
    }

    public boolean getExpelled() {
        return this.expelled;
    }

    /**
     * Prompt user for move and handle user input
     */
    @Override
    public void promptPlayerMove() {

    }

}
