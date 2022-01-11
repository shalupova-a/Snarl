package Snarly;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONPropertyName;

import java.util.List;

public class State {
    private String type;
    private Level level;
    private List<ActorPosition> players;
    private List<ActorPosition> adversaries;

    @JsonProperty("exit-locked")
    private boolean exitLocked;


    public List<ActorPosition> getPlayers() {
        return players;
    }

    public void setPlayers(List<ActorPosition> players) {
        this.players = players;
    }

    public List<ActorPosition> getAdversaries() {
        return adversaries;
    }

    public void setAdversaries(List<ActorPosition> adversaries) {
        this.adversaries = adversaries;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public boolean isExit_locked() {
        return exitLocked;
    }

    public void setExit_locked(boolean exitLocked) {
        this.exitLocked = exitLocked;
    }

}
