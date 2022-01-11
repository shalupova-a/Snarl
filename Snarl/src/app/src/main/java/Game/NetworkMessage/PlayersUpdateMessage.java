package Game.NetworkMessage;

import Game.Model.ActorPosition;
import Game.Model.Item;

import java.util.Collections;
import java.util.List;

public class PlayersUpdateMessage {
    private MessageType type;
    private Integer[][] layout;
    private Integer[] position;
    private List<Item> items;
    private List<ActorPosition> actors;
    private String message;

    public PlayersUpdateMessage() {
    }

    public PlayersUpdateMessage(MessageType type, Integer[][] layout, Integer[] position, List<Item> items, List<ActorPosition> actors, String message) {
        this.type = type;
        this.layout = layout;
        this.position = position;
        this.items = items;
        this.actors = actors;
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Integer[][] getLayout() {
        return layout;
    }

    public void setLayout(Integer[][] layout) {
        this.layout = layout;
    }

    public Integer[] getPosition() {
        return position;
    }

    public void setPosition(Integer[] position) {
        this.position = position;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<ActorPosition> getActors() {
        return Collections.unmodifiableList(this.actors);
    }

    public void setActors(List<ActorPosition> actors) {
        this.actors = actors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
