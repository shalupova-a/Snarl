package Game.Controller;

import Game.Model.*;

import java.util.ArrayList;
import java.util.List;

public class RuleChecker {

    private State gameState;

    private static final int NUM_MOVES = 2;

    public RuleChecker(State model) {
        this.gameState = model;
    }

    /**
     * Checks that given tile is a valid inital location for an entity
     * @param to: Coordinate representing tile
     * @return boolean
     */
    public boolean validInitialLocation(Integer[] to) {
        return checkValidTile(to) && checkUnoccupiedByItem(to)
                && checkUnoccupiedByActor(to, this.gameState.getActors());
    }

    /**
     * Check that tile is not a wall or exit
     * @param to: Coordinate representing tile
     * @return boolean
     */
    private boolean validSpace(Integer[] to) {
        Level level = this.gameState.getLevel();
        List<Room> rooms = level.getRooms();
        List<Hallway> hallways = level.getHallways();

        boolean withinRoomBounds = false;
        boolean withinHallwayBounds = false;

        for (Room room : rooms) {
            if(room.isPointWithinRoom(to)) {
                withinRoomBounds = true;
            }
        }

        for (Hallway hallway : hallways) {

            if(hallway.isPointInHallway(to)) {
                withinHallwayBounds = true;
            }
        }

        return withinRoomBounds || withinHallwayBounds || !checkUnoccupiedByItem(to);
    }

    /**
     * Check if there is an item at this tile
     * @param to: Coordinate representing tile
     * @return boolean
     */
    public boolean checkUnoccupiedByItem(Integer[] to) {
        return !this.gameState.getLevel().getItems().stream().anyMatch(i -> i.isObjectOnPoint(to));
    }

    /**
     * Check if exit is locked in the level
     * @return boolean
     */
    public boolean isExitLocked() {
        return this.gameState.isExitLocked();
    }

    /**
     * Check that the given to tile is reachable, valid, and not occupied by a player
     * @param from: tile player is moving from
     * @param to: tile player is moving to
     * @return boolean: if a player can move to this tile
     */
    public boolean checkValidPlayerMove(Integer[] from, Integer[] to) {
        return checkCardinalMove(from[0], from[1], to[0], to[1]) && checkUnoccupiedByActor(to, this.gameState.getPlayers())
                && checkValidTile(to);
    }

    /**
     * Check that the given move is 2 cardinal moves away
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @return boolean
     */
    private boolean checkCardinalMove(int fromX, int fromY, int toX, int toY) {

        return (Math.abs(fromX - toX) <= 1 && Math.abs(fromY - toY) <= 1)
                || (Math.abs(fromX - toX) == 2 && Math.abs(fromY - toY) == 0)
                || (Math.abs(fromX - toX) == 0 && Math.abs(fromY - toY) == 2);
    }


    /**
     * Check that there isn't another actor at this tile
     * @param to: Coordinate representing tile
     * @param actors: actors we want to check against
     * @return boolean
     */
    private boolean checkUnoccupiedByActor(Integer[] to, List<ActorPosition> actors) {
        // can't move to tile if there is another player

        return !actors.stream().anyMatch(p -> p.isActorInPosition(to));
    }


    /**
     * Check that tile is traversable in a room or hallway
     * @param point
     * @return boolean
     */
    private boolean checkValidTile(Integer[] point) {

        boolean isPointTraversableInRooms = gameState.getLevel().getRooms()
                .stream()
                .anyMatch(s -> s.isPointTraversable(point));
        boolean isPointTraversable = isPointTraversableInRooms || gameState.getLevel().getHallways().stream().anyMatch(s -> s.isPointInHallway(point));

        return isPointTraversable;
    }


    /**
     * Check if player has found the key, if so set exit to locked
     * @param point
     * @return
     */
    public boolean foundKey(Integer[] point) {

        boolean isLandedOnKey = gameState.getLevel().getItems()
                .stream()
                .anyMatch(i -> i.isObjectOnPoint(point) && i.getType() == ItemType.key);

        if(isLandedOnKey) {
            this.gameState.setExitLocked(false);
        }

        return isLandedOnKey;
    }

    /**
     * Remove the key from the level
     */
    public void removeKey() {
        List<Item> items = this.gameState.getLevel().getItems();
        items.removeIf(o -> o.getType() == ItemType.key);
    }

    /**
     * Check if the given point is an exit
     * @param point
     * @return boolean
     */
    public boolean exited(Integer[] point) {

        return gameState.getLevel().getItems()
                .stream()
                .anyMatch(i -> i.isObjectOnPoint(point) && i.getType() == ItemType.exit);
    }

    /**
     * Check if there are any adversaries on given point
     * @param point
     * @return boolean
     */
    public boolean ejected(Integer[] point) {

        return this.gameState.getAdversaries().stream().anyMatch(i -> i.isActorInPosition(point));
    }


    /**
     * Determines all Coordinates NUM_MOVES cardinal moves away from given position
     * @param position: Integer[]
     * @return List<Integer[]>
     */
    public List<Integer[]> getTraversableTiles(Integer[] position) {
        List<Integer[]> tiles = new ArrayList();
        for(int i = -NUM_MOVES; i <= NUM_MOVES; i++) {
            for (int j = -NUM_MOVES; j <= NUM_MOVES; j++) {
                Integer[] tile = {position[0] + i, position[1] + j};
                tiles.add(tile);
            }
        }

        return tiles;
    }
}