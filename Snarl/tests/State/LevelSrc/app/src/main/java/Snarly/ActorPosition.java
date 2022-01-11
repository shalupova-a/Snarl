package Snarly;

import java.util.Arrays;

public class ActorPosition {

    private ActorType type;
    private String name;
    private Integer[] position;

    public ActorType getType() {
        return type;
    }

    public void setType(ActorType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer[] getPosition() {
        return position;
    }

    public void setPosition(Integer[] position) {
        this.position = position;
    }

    public boolean isActorInPosition(Integer[] p) {
        return Arrays.equals(p, position);
    }
}
