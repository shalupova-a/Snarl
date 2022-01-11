package Game.View;

public class TileUtils {
    /**
     *
     * @param xCoord - x coord of the tile
     * @param yCoord - y coord of the tile
     * @param width - width of the layout
     * @param height - height of the layout
     * @return boolean is the tile is the corner tile in a layout of tiles
     */
    public static boolean isCornerTile(int xCoord, int yCoord, int width, int height) {
        return isLeftCorner(xCoord, yCoord)
                || isRightCorner(xCoord, yCoord, width, height)
                || isBottomLeftCorner(xCoord, yCoord, width, height)
                || isBottomRightCorner(xCoord, yCoord, width, height);
    }
    /**
     *
     * @param xCoord - x coord of the tile
     * @param yCoord - y coord of the tile
     * @param width - width of the layout
     * @param height - height of the layout
     * @return boolean To check if the tile is in the border of the layout
     */
    public static boolean isBorder(int xCoord, int yCoord, int width, int height) {
        return isVerticalBorder(yCoord, height) || isHorizontalBorder(xCoord, width);
    }

    /**
     * Tells if a tile is in the horizontal border of a layout
     * @param x - x coord of the tile
     * @param width - width of the layout
     * @return boolean to tell if it is horizontal border
     */
    public static boolean isHorizontalBorder(int x, int width) {
        return x == 0 || x == width;
    }

    /**
     *  Tells if a tile is in the vertical border of the layout
     * @param y - y coord of the tile
     * @param height - heigh of the layout
     * @return boolean
     */
    public static boolean isVerticalBorder(int y, int height) {
        return y == 0 || y == height;
    }

    /**
     *  Tells if it is the left corner
     *  This util is useful for designing corners properly
     *  left corner and right corners have different shaped ascii values
     * @param x - x coord of the tile
     * @param y - y coord of the tile
     * @return boolean
     */
    public static boolean isLeftCorner(int x, int y) {
        return x == 0 && y == 0;
    }
    /**
     *  Tells if it is the right corner
     *  This util is useful for designing corners properly
     *  left corner and right corners have different shaped ascii values
     * @param x - x coord of the tile
     * @param y - y coord of the tile
     * @return boolean
     */
    public static boolean isRightCorner(int x, int y,int width, int height) {
        return x == 0 && y == height;
    }
    /**
     *  Tells if it is the Bottom Left  corner
     *  This util is useful for designing corners properly
     *  left corner and right corners have different shaped ascii values
     * @param x - x coord of the tile
     * @param y - y coord of the tile
     * @param width - width of the layout
     * @return boolean
     */
    public static boolean isBottomLeftCorner(int x, int y, int width, int height) {
        return x == width && y == 0;
    }

    /**
     *  Tells if it is the Bottom Right corner
     *  This util is useful for designing corners properly
     *  left corner and right corners have different shaped ascii values
     * @param x - x coord of the tile
     * @param y - y coord of the tile
     * @param width - width of the layout
     * @param height - height of the layout
     * @return boolean
     */
    public static boolean isBottomRightCorner(int x, int y, int width, int height) {
        return x == width && y == height;
    }
}
