package Game.UI;

import java.util.Random;
import java.awt.*;
// https://en.wikipedia.org/wiki/Code_page_437 check this for mappings for the ascii values to symbols
// we are using code 437 tileset similar to dwarf fortress games
public enum TextureType {
    /** empty spaces and doors which are passable **/
    BLANK((char) 250, Color.decode("#0f49c6"), 1),
    DOOR((char) 'x', Color.red, 2),

    /** these represent walls, their corners and impassable terrains **/
    BLOCK((char) 177, Color.lightGray, 0),
    BOTTOM_LEFT_CORNER((char)200, Color.white, 0),
    TOP_LEFT_CORNER((char)201, Color.white, 0),
    TOP_RIGHT_CORNER((char)187, Color.white, 0),
    BOTTOM_RIGHT_CORNER((char)188, Color.white, 0),
    VERTICAL_WALL((char)186, Color.white, 0),
    HORIZONTAL_WALL((char)205, Color.white, 0),

    /** Items **/
    KEY('K', Color.yellow, 3),
    EXIT('E', Color.cyan, 3),

    /** Actors **/
    OTHER_PLAYER((char)1, Color.pink, 4),
    PLAYER((char)1, Color.magenta, 4),
    ZOMBIE((char)2, Color.green, 4),
    GHOST((char)2, Color.white, 4),

    /** Player Info **/
    HP_BAR_BLOCK((char)3, Color.green, 5);

    private final Character codePoint;
    private final Color color;
    private final int tileInput;

    public Character getCodePoint() {
        return codePoint;
    }

    public Color getColor() {
        return color;
    }

    public int getTileInput() {
        return tileInput;
    }

    TextureType(Character codePoint, Color color, int tileInput) {
        this.codePoint = codePoint;
        this.color = color;
        this.tileInput = tileInput;
    }
}