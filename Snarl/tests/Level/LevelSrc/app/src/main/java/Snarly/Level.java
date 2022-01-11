package Snarly;

import java.util.List;

public class Level {
    String type;
    List<Room> rooms;
    List<Hallway> hallways;
    List<Object> objects;

    public List<Object> getObjects() {
        return objects;
    }

    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Hallway> getHallways() {
        return hallways;
    }

    public void setHallways(List<Hallway> hallways) {
        this.hallways = hallways;
    }
}
