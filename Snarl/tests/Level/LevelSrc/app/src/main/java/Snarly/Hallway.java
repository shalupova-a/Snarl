package Snarly;

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

    public Room sectionCreator(Integer[] start, Integer[] end) throws JsonProcessingException {
        ObjectMapper o = new ObjectMapper();
//        System.out.println(o.writeValueAsString(end));
//        System.out.println(o.writeValueAsString(start));
        if (start[0].equals(end[0])) {
//            System.out.println(start[1]+"____"+ end[1]);
            if(start[1] > end[1]) {
                Integer[] temp = start;
                start = end;
                end = temp;
            }
//            System.out.println(o.writeValueAsString(end));
//            System.out.println(o.writeValueAsString(start));
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

    public List<Room> buildSections(Integer[] point) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Room> sections = new LinkedList<>();
        sections.add(sectionCreator(from, waypoints[0]));
        for (int i = 0; i < waypoints.length - 1; i++) {
            sections.add(sectionCreator(waypoints[i], waypoints[i + 1]));
        }
        sections.add(sectionCreator(waypoints[waypoints.length - 1], to));
//        System.out.println(objectMapper.writeValueAsString(sections));
        Room r = sections.stream().filter(i -> i.isPointWithinRoom(point)).findFirst().orElse(null);
//        System.out.println(objectMapper.writeValueAsString(r));
        return sections;
    }

    public boolean isPointInHallway(Integer[] point) throws Exception {
//        for()
        if(waypoints == null) {
           sections.add(sectionCreator(from,to));
        } else {
           sections = buildSections(point);
        }

        return sections.stream().anyMatch(i -> i.isPointWithinRoom(point));
    }

    public boolean isTraversable(Integer[] point) {
        // hallways are always traversable (so far)
        return true;
    }
}
