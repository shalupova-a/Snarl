package Game.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Hallway {

    private Integer[] from;
    private Integer[] to;
    private Integer[][] waypoints;

    @JsonIgnore
    private List<Room> sections = new LinkedList<>();

    public Hallway() {
    }

    public Hallway(Integer[] from, Integer[] to, Integer[][] waypoints) {
        this.from = from;
        this.to = to;
        this.waypoints = waypoints;
    }

    /**
     * Get starting point of this hallway
     * @return Integer[]
     */
    public Integer[] getFrom() {
        return this.from;
    }

    /**
     * Set from
     * @param from
     */
    public void setFrom(Integer[] from) {
        this.from = from;
    }

    /**
     * Get ending point of this hallway
     * @return Integer[]
     */
    public Integer[] getTo() {
        return this.to;
    }

    /**
     * Set to
     * @param to
     */
    public void setTo(Integer[] to) {
        this.to = to;
    }

    /**
     * Get waypoints of this hallway
     * @return Integer[][]
     */
    public Integer[][] getWaypoints() {
        return this.waypoints;
    }

    /**
     * Set waypoints
     * @param waypoints
     */
    public void setWaypoints(Integer[][] waypoints) {
        this.waypoints = waypoints;
    }

    /**
     * Split hallway into sectioned rooms
     * @return List<Room>
     * @throws Exception
     */
    public List<Room> populateSections() {

        if(waypoints.length == 0) {
            sections.add(createSection(from, to));
        } else{
            sections.add(createSection(from, waypoints[0]));
            for(int i = 0; i < waypoints.length - 1; i++) {
                sections.add(createSection(waypoints[i], waypoints[i + 1]));
            }
            sections.add(createSection(waypoints[waypoints.length - 1], to));
        }
        return sections;
    }

    /**
     * Populate room section
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
    private Room createSection(Integer[] from, Integer[] to){

        // horizontal hallway
        if(from[0] == to[0]) {
            Integer[][] tiles = new Integer[1][Math.abs(from[1] - to[1])];
            Arrays.stream(tiles).forEach(a -> Arrays.fill(a, 1));
            Integer[] origin = from[1] > to[1] ? to : from;
            return new Room(origin /** to is the origin since lesser point**/,
                    new Bounds(1, Math.abs(from[1] - to[1]))/** hallways are one width if horizontal**/, tiles);
        } else {
            Integer[][] tiles = new Integer[Math.abs(from[0] - to[0])][1];
            Arrays.stream(tiles).forEach(a -> Arrays.fill(a, 1));
            Integer[] origin = from[0] > to[0] ? to : from;
            return new Room(origin /** to is the origin since lesser point**/
                    , new Bounds(/** hallways are one width if horizontal**/
                    Math.abs(from[0] - to[0]), 1),
                    tiles);
        }
    }

    /**
     * Checks if a point is in this hallway
     * @param point
     * @return boolean
     */
    public boolean isPointInHallway(Integer[] point) {
        sections = populateSections();
        return sections.stream().anyMatch(i -> i.isPointWithinRoom(point));
    }
}
