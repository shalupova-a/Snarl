package Game.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Adversary extends Actor {

    private Level initLevel;

    public Adversary(int id, ActorType actorType, Integer[] position) {
        super(id, actorType, position);
    }

    public Adversary(int id, ActorType actorType, Integer[] position, boolean useHp) {
        super(id, actorType, position, 100, 50, 30, 1);
        if(actorType.equals(ActorType.zombie)) {
            this.setHitPoints(150);
            this.setAttack(70);
            this.setDefense(0);
            this.setModifier(1);
        } else if(actorType.equals(ActorType.ghost)) {
            this.setHitPoints(60);
            this.setAttack(60);
            this.setDefense(60);
            this.setModifier(1);
        }
    }


    /**
     * Setter for initLevel
     * @param initLevel
     */
    public void setInitLevel(Level initLevel) {
        this.initLevel = initLevel;
    }


    /**
     * Determine tile adversary should move to
     * @param playerCoordinates coordinates of all actors in the game
     * @param adversaryCoordinates coordinates of all actors in the game
     * @return Integer[]
     * @throws Exception
     */
    public Integer[] makeMove(List<Integer[]> playerCoordinates, List<Integer[]> adversaryCoordinates) throws Exception {

        List<Integer[]> possibleMoves;
        List<Integer[]> validMoves = getTraversableTiles(1);

        if(this.actorType == ActorType.zombie) {
            possibleMoves = getZombieMoves(adversaryCoordinates, validMoves);
        } else if(this.actorType == ActorType.ghost) {
            possibleMoves = getGhostMoves(adversaryCoordinates, validMoves);
        } else {
            throw new Exception(actorType + " is not a valid adversary.");
        }

        Integer[] to = moveTowardsPlayer(playerCoordinates, possibleMoves);

        if(Arrays.equals(to, this.position)) {
            if(possibleMoves.size() > 0) {

                Random rand = new Random();
                int index = rand.nextInt(possibleMoves.size());

                to = possibleMoves.get(index);
            }
        }

        if(this.actorType == ActorType.ghost) {
            if(isWall(to)) {
                to = findRandomTile();
            }
        }
        setActorPosition(to);

        return to;
    }

    /**
     * Determines tiles numMoves cardinal moves away from adversary's position
     * @param numMoves
     * @return List<Integer[]>
     */
    private List<Integer[]> getTraversableTiles(int numMoves) {

        List<Integer[]> traversableTiles = new ArrayList<>();

        for(int i = 0; i < numMoves + 1; i++) {
            for(int j = 0; j < numMoves + 1; j++) {
                if (!(i == 0 && j == 0)) {
                    if (i == 0) {
                        traversableTiles.add(new Integer[]{this.position[0], this.position[1] + j});
                        traversableTiles.add(new Integer[]{this.position[0], this.position[1] - j});
                    } else if (j == 0) {
                        traversableTiles.add(new Integer[]{this.position[0] - i, this.position[1]});
                        traversableTiles.add(new Integer[]{this.position[0] + i, this.position[1]});
                    } else {
                        traversableTiles.add(new Integer[]{this.position[0] - i, this.position[1] - j});
                        traversableTiles.add(new Integer[]{this.position[0] + i, this.position[1] - j});
                        traversableTiles.add(new Integer[]{this.position[0] + i, this.position[1] + j});
                        traversableTiles.add(new Integer[]{this.position[0] - i, this.position[1] + j});
                    }
                }
            }
        }
        return traversableTiles;
    }

    /**
     * Get all possible zombie moves
     * @param adversaryCoordinates
     * @param traversableTiles
     * @return List<Integer[]>
     */
    private List<Integer[]> getZombieMoves(List<Integer[]> adversaryCoordinates, List<Integer[]> traversableTiles) {

        List<Integer[]> tiles = new ArrayList<>(traversableTiles);
        // remove tiles that have adversary or is a door
        for(Integer[] tile : tiles) {
            for(Integer[] adversaryCoordinate : adversaryCoordinates) {
                if(Arrays.equals(adversaryCoordinate,tile)) {
                    traversableTiles.remove(tile);
                }
            }
            if(isWall(tile) || isDoor(tile) || outOfBounds(tile)) {

                traversableTiles.remove(tile);
            }
        }

        return traversableTiles;
    }

    /**
     * Checks if given Integer[] corresponds to a door
     * @param tile
     * @return boolean
     */
    private boolean isDoor(Integer[] tile) {
        List<Room> rooms = initLevel.getRooms();

        for(Room room : rooms) {
            if(room.isDoor(tile)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if tile is within bounds of a room
     * @param tile
     * @return boolean
     */
    private boolean outOfBounds(Integer[] tile) {

        return !this.initLevel.checkSameRoom(this.position, tile);
    }

    /**
     * Determines best tiles the Ghost can move to
     * @param playerCoordinates
     * @param traversableTiles
     * @return List<Integer[]>
     */
    private List<Integer[]> getGhostMoves(List<Integer[]> playerCoordinates, List<Integer[]> traversableTiles) {

        List<Integer[]> tiles = new ArrayList<>(traversableTiles);
        for(Integer[] tile : tiles) {
            if(outOfBounds(tile) && !isWall(tile)) {
                traversableTiles.remove(tile);
            }
        }

        return traversableTiles;
    }

    /**
     * Determine if the tile is a wall in a Room
     * @param tile
     * @return boolean
     */
    private boolean isWall(Integer[] tile) {

        List<Room> rooms = this.initLevel.getRooms();

        for(Room room : rooms) {
            Integer[] roomPosn = room.getOrigin();
            if(tile[0] == roomPosn[0] || tile[0] == roomPosn[0] + room.getBounds().getRows() - 1 ||
                    tile[1] == roomPosn[1] || tile[1] == roomPosn[1] + room.getBounds().getColumns() - 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get tile of closest player
     * @return Integer[]
     */
    private Integer[] getClosestTile(List<Integer[]> coordinates, List<Integer[]> traversableTiles) {
        int minDistance = Integer.MAX_VALUE;
        Integer[] closestTile = this.position;

        for(Integer[] tile : coordinates) {

            for(Integer[] traversable : traversableTiles) {
                int manhattanDist = Math.abs(tile[0] - traversable[0])
                        + Math.abs(tile[1] - traversable[1]);
                if(manhattanDist < minDistance) {
                    minDistance = manhattanDist;
                    closestTile = traversable;
                }
            }
        }

        return closestTile;
    }

    /**
     * Find traversable tile closest to player
     * @param playerCoordinates
     * @param traversableTiles
     * @return Integer[]
     */
    private Integer[] moveTowardsPlayer(List<Integer[]> playerCoordinates, List<Integer[]> traversableTiles) {
        Integer[] closestTile = getClosestTile(playerCoordinates, traversableTiles);
        return closestTile;
    }

    /**
     * Find random tile in a random room
     * @return Integer[]
     */
    private Integer[] findRandomTile() {
        Random rand = new Random();

        List<Game.Model.Room> rooms = this.initLevel.getRooms();
        Room nextRoom = rooms.get(rand.nextInt(rooms.size()));

        Integer[] roomPosn = nextRoom.getOrigin();

        boolean notWall = false;
        Integer[] to = new Integer[2];
        while(!notWall) {
            int rand_x = rand.nextInt(nextRoom.getBounds().getRows() - 1) + roomPosn[0];
            int rand_y = rand.nextInt(nextRoom.getBounds().getColumns() - 1) + roomPosn[1];
            to = new Integer[]{rand_x, rand_y};
            notWall = !isWall(to);
        }
        return to;
    }
}
