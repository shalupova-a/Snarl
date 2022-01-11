package Game.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class State {
    private Level level;
    private List<ActorPosition> players;
    private List<ActorPosition> adversaries;
    // Use this for adversaries for now
    @JsonIgnore
    private List<Adversary> adversaryList = new LinkedList<>();
    @JsonIgnore
    private List<Player> playerList = new LinkedList<>();

    @JsonProperty("exit-locked")
    private boolean exitLocked;

    public State(List<ActorPosition> players, List<ActorPosition> adversaries, Level level, boolean exitLocked) {
        this.players = players;
        this.adversaries = adversaries;
        this.exitLocked = exitLocked;
        this.level = level;
    }

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

    public List<ActorPosition> getActors() {
        List<ActorPosition> actors = new ArrayList<>(players);
        actors.addAll(adversaries);

        return actors;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public boolean isExitLocked() {
        return exitLocked;
    }

    public void setExitLocked(boolean exitLocked) {
        this.exitLocked = exitLocked;
    }

    public List<Adversary> getAdversaryList() {
        return adversaryList;
    }

    public void populateAdversaryList(Level level) {
        this.adversaryList = new ArrayList<>();
        this.adversaryList.addAll(this.adversaries.stream()
                .map(i -> new Adversary(0, i.getType(), new Integer[]{i.getPosition()[0], i.getPosition()[1]}, true))
                .collect(Collectors.toList()));
        this.adversaryList.forEach(i -> i.setInitLevel(level));
    }

    public void setAdversaryList(List<Adversary> adversaryList) {
        this.adversaryList = adversaryList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void populatePlayerList() {
        this.playerList = new ArrayList<>();
        this.playerList.addAll(this.players.stream()
                .map(i -> new Player(0, i.getName(), i.getType(), i.getPosition(), true))
                .collect(Collectors.toList()));
    }

    public void removePlayer(String name) {
        this.players = this.players.stream().filter(player -> !player.getName().equals(name)).collect(Collectors.toList());
        System.out.println("Players size " + this.players.size());
    }
}
