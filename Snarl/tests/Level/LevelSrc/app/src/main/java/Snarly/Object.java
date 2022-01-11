package Snarly;

public class Object {
    private String type;
    private Integer[] position;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer[] getPosition() {
        return position;
    }

    public void setPosition(Integer[] position) {
        this.position = position;
    }

    public boolean isObjectOnPoint(Integer[] point) {
        return this.position[0].equals(point[0]) && this.position[1].equals(point[1]);
    }
}
