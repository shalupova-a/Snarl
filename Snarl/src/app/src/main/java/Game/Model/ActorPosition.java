package Game.Model;

import java.util.Arrays;

public class ActorPosition {

    private ActorType type;
    private String name;
    private Integer[] position;
    private int hitPoints;

    public ActorPosition() {
    }

    public ActorPosition(ActorType type, String name, Integer[] position) {
        this.type = type;
        this.name = name;
        this.position = position;
    }

    public ActorPosition(ActorType type, String name) {
        this.type = type;
        this.name = name;
        this.position = new Integer[2];
    }

    /**
     * Get actor type
     * @return ActorType
     */
    public ActorType getType() {
        return type;
    }

    /**
     * Get actor's name
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Get actor's position
     * @return Integer[]
     */
    public Integer[] getPosition() {
        return position;
    }

    /**
     * Set the actor's position
     * @param position
     */
    public void setPosition(Integer[] position) {
        this.position = position;
    }

    /**
     * Checks if actor is in given position
     * @param p
     * @return boolean
     */
    public boolean isActorInPosition(Integer[] p) {
        return Arrays.equals(p, position);
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }
}
