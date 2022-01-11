package Game.Model;

import java.util.ArrayList;
import java.util.List;

public class Player extends Actor {
    private boolean isExpelled;
    private List<Item> inventory;
    private String playerName;
    public Player(int actorId, ActorType type, Integer[] position) {
        super(actorId, type, position);
        this.isExpelled = false;
        this.inventory = new ArrayList<>();
    }

    public Player(int actorId, String name, ActorType type, Integer[] position) {
        super(actorId, type, position);
        this.isExpelled = false;
        this.inventory = new ArrayList<>();
        this.playerName = name;
    }

    public Player(int actorId, String name, ActorType type, Integer[] position, boolean enableHp) {
        super(actorId, type, position, 200, 70, 30, 1);
        this.isExpelled = false;
        this.inventory = new ArrayList<>();
        this.playerName = name;
    }

    /**
     * Get player inventory
     * @return List<Item>
     */
    public List<Item> getInventory() {
        return this.inventory;
    }

    /**
     * Add item to player inventory
     * @param heldItem: Item
     */
    public void addToInventory(Item heldItem) {
        this.inventory.add(heldItem);
    }

    /**
     * Detemine if player is expelled from game
     * @return boolean
     */
    public boolean isExpelled() {
        return isExpelled;
    }

    /**
     * Set isExpelled
     * @param expelled: boolean
     */
    public void setExpelled(boolean expelled) {
        isExpelled = expelled;
    }

    /**
     * Check if player's inventory contains a key item
     * @return boolean
     */
    public boolean hasKey() {
        return inventory.stream().anyMatch(i -> i.getType() == ItemType.key);
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public String getPlayerName() {
        return playerName;
    }
}
