package Game.Model;

public class Room {

    private Integer[] origin;
    private Bounds bounds;
    private Integer[][] layout;

    public Room() {
    }

    public Room(Integer[] origin, Bounds bounds, Integer[][] layout) {
        this.origin = origin;
        this.bounds = bounds;
        this.layout = layout;
    }

    /**
     * Get origin coordinates of room
     * @return Integer[]
     */
    public Integer[] getOrigin() {
        return this.origin;
    }

    /**
     * Set origin
     * @param origin
     */
    public void setOrigin(Integer[] origin) {
        this.origin = origin;
    }

    /**
     * Get bounds of room
     * @return Bounds
     */
    public Bounds getBounds() {
        return this.bounds;
    }

    /**
     * Set bounds
     * @param bounds
     */
    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }


    /**
     * Get layout of room
     * @return Integer[][]
     */
    public Integer[][] getLayout() {
        return this.layout;
    }

    /**
     * Set layout
     * @param layout
     */
    public void setLayout(Integer[][] layout) {
        this.layout = layout;
    }

    /**
     * Checks if point is in this room
     * @param point
     * @return boolean
     */
    public boolean isPointWithinRoom(Integer[] point) {
        int[] truePoint = {point[0] - origin[0], point[1] - origin[1]};
        return !(truePoint[0] < 0 || truePoint[1] < 0 || truePoint[0] + 1 > bounds.getRows() || truePoint[1] + 1 > bounds.getColumns());
    }

    /**
     * Checks if room is either a door or traversable tile
     * @param point
     * @return boolean
     */
    public boolean isPointTraversable(Integer[] point) {
        if (isPointWithinRoom(point)) {
            int[] truePoint = {point[0] - origin[0], point[1] - origin[1]};
            return this.layout[truePoint[0]][truePoint[1]] == 1 || this.layout[truePoint[0]][truePoint[1]] == 2;
        }

        return false;
    }

    /**
     * Check if a point is a door
     * @param point: Integer[]
     * @return boolean
     */
    public boolean isDoor(Integer[] point) {

        if(point[0] >= origin[0] && point[0] < origin[0] + bounds.getRows() &&
                point[1] >= origin[1] && point[1] < origin[1] + bounds.getColumns()) {
            return layout[point[0] - origin[0]][point[1] - origin[1]] == 2;
        }

        return false;
    }
}
