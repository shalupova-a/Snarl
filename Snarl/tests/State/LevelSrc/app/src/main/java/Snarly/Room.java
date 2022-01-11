package Snarly;

public class Room {

    private String type;
    private Integer[] origin;
    private Bounds bounds;
    private int[][] layout;

    public Room() {
    }

    public Room(String type, Integer[] origin, Bounds bounds, int[][] layout) {
        this.type = type;
        this.origin = origin;
        this.bounds = bounds;
        this.layout = layout;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer[] getOrigin() {
        return origin;
    }

    public void setOrigin(Integer[] origin) {
        this.origin = origin;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public int[][] getLayout() {
        return layout;
    }

    public void setLayout(int[][] layout) {
        this.layout = layout;
    }

    public boolean isPointWithinRoom(Integer[] point) {
        int[] truePoint = {point[0] - origin[0], point[1] - origin[1]};
//        System.out.println(truePoint[0] + "__" + truePoint[1]);
        return !(truePoint[0] < 0 || truePoint[1] < 0 || truePoint[0] + 1 > bounds.getRows() || truePoint[1] + 1 > bounds.getColumns());
    }

    public boolean isTraversable(Integer[] point) {
        if (isPointWithinRoom(point)) {
            int[] truePoint = {point[0] - origin[0], point[1] - origin[1]};
            return this.layout[truePoint[0]][truePoint[1]] == 1 || this.layout[truePoint[0]][truePoint[1]] == 2;
        }
        return false;
    }
}
