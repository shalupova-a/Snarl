package Snarly;

public enum ActorType {
    player("player"), zombie("zombie"), ghost("ghost");

    ActorType(String name) {
        this.name = name;
    }
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
