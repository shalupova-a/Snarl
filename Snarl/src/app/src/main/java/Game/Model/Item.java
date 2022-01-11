package Game.Model;

import java.util.Arrays;

public class Item {
    private String type;
    private Integer[] position;

    public Item() {}

    public Item(String type, Integer[] position) {
        this.type = type;
        this.position = position;
    }

    /**
     * Get type of this item
     * @return ItemType
     */
    public ItemType getType() {
        if(this.type.equals("key")) {
            return ItemType.key;
        } else {
            return ItemType.exit;
        }
    }

    /**
     * Get position of this item
     * @return Integer[]
     */
    public Integer[] getPosition() {
        return position;
    }

    /**
     * Checks if object occupies given position
     * @param point
     * @return boolean
     */
    public boolean isObjectOnPoint(Integer[] point) {
        return Arrays.equals(point, position);
    }
}
