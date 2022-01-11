package Game.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Level {
    private List<Room> rooms;
    private List<Hallway> hallways;
    private List<Item> items;

    public Level() {}

    public Level(List<Room> rooms, List<Hallway> hallways, List<Item> items) {
        this.rooms = rooms;
        this.hallways = hallways;
        this.items = items;
    }
    @JsonIgnore
    private List<Item> itemsCopy;

    /**
     * Get all items in this level
     * @return List<Item>
     */
    public List<Item> getItems() {
        return this.items;
    }

    /**
     * Set items
     * @param items
     */
    public void setItems() {
        this.items = new ArrayList<>(this.itemsCopy);
    }

    public void setItemsCopy() {
        this.itemsCopy = new ArrayList<>(items);
    }

    /**
     * Get all rooms in this level
     * @return List<Room>
     */
    public List<Room> getRooms() {
        return Collections.unmodifiableList(this.rooms);
    }

    /**
     * Set rooms
     * @param rooms
     */
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * Get all hallways in this level
     * @return List<Hallway>
     */
    public List<Hallway> getHallways() {
        return Collections.unmodifiableList(this.hallways);
    }

    /**
     * Set hallways
     * @param hallways
     */
    public void setHallways(List<Hallway> hallways) {
        this.hallways = hallways;
    }

    /**
     * Check that tiles belong to the same room in a level
     * @param tile1
     * @param tile2
     * @return boolean
     */
    public boolean checkSameRoom(Integer[] tile1, Integer[] tile2) {
        for (Room room : this.rooms) {

            if(room.isPointWithinRoom(tile1)) {

                return room.isPointWithinRoom(tile2);
            }
        }
        return false;
    }
}