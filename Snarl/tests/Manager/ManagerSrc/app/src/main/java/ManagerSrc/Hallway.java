package Game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedList;
import java.util.List;

public class Hallway {

    private String type;
    private Integer[] from;
    private Integer[] to;
    private Integer[][] waypoints;

    @JsonIgnore
    private List<Room> sections;

    public Hallway() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer[] getFrom() {
        return from;
    }

    public void setFrom(Integer[] from) {
        this.from = from;
    }

    public Integer[] getTo() {
        return to;
    }

    public void setTo(Integer[] to) {
        this.to = to;
    }

    public Integer[][] getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(Integer[][] waypoints) {
        this.waypoints = waypoints;
    }

    public Room sectionCreator(Integer[] start, Integer[] end) {
        ObjectMapper o = new ObjectMapper();
        if (start[0].equals(end[0])) {
            if(start[1] > end[1]) {
                Integer[] temp = start;
                start = end;
                end = temp;
            }

            return new Room("room", start, new Bounds(1, (end[1] - start[1]) + 1), null);
        } else if (start[1].equals(end[1])) {
            if(start[0] > end[0]) {
                Integer[] temp = start;
                start = end;
                end = temp;
            }
            return new Room("room", start, new Bounds((end[0] - start[0]) + 1, 1), null);
        }
        return null;
    }

    public List<Room> buildSections() {
        List<Room> sections = new LinkedList<>();
        sections.add(sectionCreator(from, waypoints[0]));
        for (int i = 0; i < waypoints.length - 1; i++) {
            sections.add(sectionCreator(waypoints[i], waypoints[i + 1]));
        }
        sections.add(sectionCreator(waypoints[waypoints.length - 1], to));
        return sections;
    }

    public boolean isPointInHallway(Integer[] point) {
        sections = buildSections();
        return sections.stream().anyMatch(i -> i.isPointWithinRoom(point));
    }

    public boolean isTraversable(Integer[] point) {
        // hallways are always traversable (so far)
        return true;
    }
}
