package Game;

public class Input {
    private State state;
    private String name;
    private Integer[] point;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer[] getPoint() {
        return point;
    }

    public void setPoint(Integer[] point) {
        this.point = point;
    }
}
