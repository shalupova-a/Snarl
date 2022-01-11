package Game.Model;

public enum ActorType {
    player("player"), zombie("zombie"), ghost("ghost");

    private String name;

    ActorType(String name) {
        this.name = name;
    }

    /**
     * Get name of ActorType
     * @return
     */
    public String getName() {
        return this.name;
    }
}