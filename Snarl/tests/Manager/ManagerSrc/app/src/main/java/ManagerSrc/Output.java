package Game;

import java.util.HashSet;
import java.util.Set;

public class Output {
    private boolean traversable;
    private String object;
    private String type;
    private Set<Integer[]> reachable = new HashSet<>();

    public Output() {
    }

    public Output(boolean traversable, String object, String type) {
        this.traversable = traversable;
        this.object = object;
        this.type = type;
    }

    public Output(boolean traversable, String object, String type, Set<Integer[]> reachable) {
        this.traversable = traversable;
        this.object = object;
        this.type = type;
        this.reachable = reachable;
    }

    public boolean isTraversable() {
        return traversable;
    }

    public void setTraversable(boolean traversable) {
        this.traversable = traversable;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<Integer[]> getReachable() {
        return reachable;
    }

    public void setReachable(Set<Integer[]> reachable) {
        this.reachable = reachable;
    }
}
