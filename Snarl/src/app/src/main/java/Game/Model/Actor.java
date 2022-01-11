package Game.Model;

public abstract class Actor {

    protected int actorId;
    protected Integer[] position;
    protected ActorType actorType;
    protected int hitPoints;
    protected int attack;
    protected int defense;
    protected int modifier;

    public Actor(int actorId, ActorType actorType, Integer[] position) {
        this.actorId = actorId;
        this.actorType = actorType;
        this.position = position;
    }

    public Actor(int actorId, ActorType actorType, Integer[] position, int hitPoints, int attack, int defense, int modifier) {
        this.actorId = actorId;
        this.position = position;
        this.actorType = actorType;
        this.hitPoints = hitPoints;
        this.attack = attack;
        this.defense = defense;
        this.modifier = modifier;
    }

    /**
     * Get player id
     * @return int
     */
    public int getActorId() {
        return this.actorId;
    }

    /**
     * Get player position
     * @return Coordinate
     */
    public Integer[] getActorPosition() {
        return position;
    }

    /**
     * Get actor type
     * @return ActorType
     */
    public ActorType getActorType() {
        return actorType;
    }

    /**
     * Change player's location to given coordinate
     * @param coordinate: Integer[]
     */
    public void setActorPosition(Integer[] coordinate) {
        this.position = new Integer[]{coordinate[0], coordinate[1]};
    }

    /**
     * Get hp of actor
     * @return int
     */
    public int getHitPoints() {
        return hitPoints;
    }

    /**
     * Set hp
     * @param hitPoints
     */
    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    /**
     * Get attack points
     * @return int
     */
    public int getAttack() {
        return attack;
    }

    /**
     * Get defense points
     * @return int
     */
    public int getDefense() {
        return defense;
    }

    /**
     * Get modifier of points
     * @return int
     */
    public int getModifier() {
        return modifier;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }
}