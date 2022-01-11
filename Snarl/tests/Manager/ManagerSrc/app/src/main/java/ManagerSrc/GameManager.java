package Game;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameManager {
    private State gameState;
    private JSONArray actorMoveListList;
    private List managerTrace;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private int maxTurns;

    public GameManager(State gameState, JSONArray actorMoveListList, int maxTurns) {
        this.gameState = gameState;
        this.actorMoveListList = actorMoveListList;
        this.managerTrace = new LinkedList<>();
        this.maxTurns = maxTurns;
    }

    public State getState() {
        return this.gameState;
    }

    public List makeMoves() {
        List<ActorPosition> players = this.gameState.getPlayers();
        List<ActorPosition> playersCopy = new ArrayList(players);
        List<Object> objects = this.gameState.getLevel().getObjects();
        Map<ActorPosition, JSONArray> playerMoves = new HashMap<ActorPosition, JSONArray>();

        for(int i = 0; i < players.size(); i++) {
            JSONArray actorMoveList = (JSONArray) actorMoveListList.get(i);
            playerMoves.put(players.get(i), actorMoveList);
        }

        int moves = 0;
        boolean noTurns = false;
        // while we still have turns left, complete a full turn
        while(moves < maxTurns && !noTurns) {
            List managerTraceEntry = new LinkedList();

            // complete a move for each player
            for (ActorPosition player : players) {
                
                // if player alive
                if(playersCopy
                        .stream()
                        .anyMatch(p -> p.getName().equals(player.getName()))) {

                    updatePlayers(playersCopy);

                    JSONArray actorMoveList = playerMoves.get(player);
                    boolean validMove = false;
                    //keep trying to find valid move
                    while (!validMove && actorMoveList.length() != 0) {
                        JSONObject moveObj = (JSONObject) actorMoveList.get(0);
                        actorMoveList.remove(0);
                        playerMoves.put(player, actorMoveList);
                        Integer[] to = new Integer[2];
                        try {
                            to = objectMapper.readValue(moveObj.get("to").toString(), Integer[].class);
                            // if move is not null, move the player
                            if (to != null) {

                                Integer[] position = player.getPosition();
                                if (checkValidMove(position, to)) {

                                    validMove = true;
                                    player.setPosition(to);

                                    if (foundKey(to)) {

                                        objects.removeIf(o -> o.getType().equals("key"));
                                        this.gameState.getLevel().setObjects(objects);
                                        managerTraceEntry = new LinkedList(Arrays.asList(objectMapper.writeValueAsString(player.getName()),
                                                moveObj, objectMapper.writeValueAsString("Key")));

                                    } else if (!this.gameState.isExitLocked() && exited(to)) {

                                        //System.out.println("Exited");
                                        playersCopy.remove(player);
                                        managerTraceEntry = new LinkedList(Arrays.asList(objectMapper.writeValueAsString(player.getName()),
                                                moveObj, objectMapper.writeValueAsString("Exit")));

                                    } else if (ejected(to)) {

                                        //System.out.println("Ejected");
                                        playersCopy.remove(player);
                                        managerTraceEntry = new LinkedList(Arrays.asList(objectMapper.writeValueAsString(player.getName()),
                                                moveObj, objectMapper.writeValueAsString("Eject")));

                                    } else {

                                        managerTraceEntry = new LinkedList(Arrays.asList(objectMapper.writeValueAsString(player.getName()),
                                                moveObj, objectMapper.writeValueAsString("OK")));
                                    }
                                } else {

                                    managerTraceEntry = new LinkedList(Arrays.asList(objectMapper.writeValueAsString(player.getName()),
                                            moveObj, objectMapper.writeValueAsString("Invalid")));
                                }
                                managerTrace.add(managerTraceEntry);

                            } else {
                                //null move is still a valid move
                                managerTraceEntry = new LinkedList(Arrays.asList(objectMapper.writeValueAsString(player.getName()),
                                        moveObj, objectMapper.writeValueAsString("OK")));
                                managerTrace.add(managerTraceEntry);
                                validMove = true;
                            }
                        } catch (Exception j) {
                            j.printStackTrace();
                        }

                        // if there are no moves left for this player, there are no turns left in the game
                        if (actorMoveList.length() == 0) {
                            noTurns = true;
                            break;
                        }
                    }
                }

            }
            moves+=1;
        }

        updatePlayers(playersCopy);

        // update game state
        this.gameState.setPlayers(playersCopy);
        this.gameState.getLevel().setObjects(objects);

        return managerTrace;
    }

    private boolean checkValidMove(Integer[] from, Integer[] to) {
        return checkCardinalMove(from[0], from[1], to[0], to[1]) && checkUnoccupied(to[0], to[1]) && checkValidTile(to);
    }

    private boolean checkCardinalMove(int fromX, int fromY, int toX, int toY) {

        return (Math.abs(fromX - toX) <= 1 && Math.abs(fromY - toY) <= 1)
                || (Math.abs(fromX - toX) == 2 && Math.abs(fromY - toY) == 0)
                || (Math.abs(fromX - toX) == 0 && Math.abs(fromY - toY) == 2);
    }

    private boolean checkUnoccupied(int x, int y) {
        // can't move to tile if there is another player (adversaries fine for now)

        for(ActorPosition player : this.gameState.getPlayers()) {
            Integer[] playerLocation = player.getPosition();
            if(x == playerLocation[0] && y == playerLocation[1]) {
                return false;
            }
        }
        return true;
    }

    private boolean checkValidTile(Integer[] point) {

        boolean isPointTraversableInRooms = gameState.getLevel().getRooms()
                .stream()
                .anyMatch(s -> s.isTraversable(point));
        boolean isPointTraversable = isPointTraversableInRooms || gameState.getLevel().getHallways().stream().anyMatch(s -> s.isPointInHallway(point));

        return isPointTraversable;
    }

    private boolean foundKey(Integer[] point) {

        boolean isLandedOnKey = gameState.getLevel().getObjects()
                .stream()
                .anyMatch(i -> i.isObjectOnPoint(point) && i.getType().equals("key"));

        if(isLandedOnKey) {
            this.gameState.setExitLocked(false);
        }

        return isLandedOnKey;
    }

    private boolean exited(Integer[] point) {

        boolean isLandedOnExit = gameState.getLevel().getObjects()
                .stream()
                .anyMatch(i -> i.isObjectOnPoint(point) && i.getType().equals("exit"));

        return isLandedOnExit;
    }

    private boolean ejected(Integer[] point) {

        boolean isAdversaryInPoint = this.gameState.getAdversaries().stream().anyMatch(i -> i.isActorInPosition(point));

        return isAdversaryInPoint;
    }

    private List<Integer[]> getTraversableTiles(Integer[] position) {
        List<Integer[]> tiles = new ArrayList();
        for(int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                Integer[] tile = {position[0] + i, position[1] + j};
                tiles.add(tile);
            }
        }

        return tiles;
    }

    private int[][] getLayout(Integer[] position) {

        int[][] layout = new int[5][5];

        for(int i = -2; i <= 2; i++) {
            for(int j = -2; j <= 2; j++) {
                Integer[] tilePosn = {position[0] + i, position[1] + j};
                layout[i + 2][j + 2] = 0;

                for(Room room : this.gameState.getLevel().getRooms()) {

                    if(room.isPointWithinRoom(tilePosn)) {
                        int[] truePoint = {tilePosn[0] - room.getOrigin()[0], tilePosn[1] - room.getOrigin()[1]};
                        int tile = room.getLayout()[truePoint[0]][truePoint[1]];
                        layout[i + 2][j + 2] = tile;
                    }

                }
                for(Hallway hallway : this.gameState.getLevel().getHallways()) {
                    if(hallway.isPointInHallway(tilePosn) && layout[i + 2][j + 2] != 2) {
                        layout[i + 2][j + 2] = 1;
                    }
                }
            }
        }
        return layout;
    }

    private List<Object> getNearbyObjects(List<Integer[]> traversableTiles) {
        Level level = this.gameState.getLevel();
        List<Object> objects = level.getObjects();

        List<Object> nearbyObjects = new ArrayList<>();

        for(Object object : objects) {
            Integer[] objectPosition = object.getPosition();
            for(Integer[] tile : traversableTiles) {
                if (objectPosition[0] == tile[0] && objectPosition[1] == tile[1]) {
                    nearbyObjects.add(object);
                }
            }
        }
        return nearbyObjects;
    }

    private List<ActorPosition> getNearbyActors(List<Integer[]> traversableTiles, String name, List<ActorPosition> players) {
        List<ActorPosition> actors = new ArrayList<>();
        actors.addAll(players);
        actors.addAll(this.gameState.getAdversaries());

        List<ActorPosition> nearbyActors = new ArrayList<>();

        for(ActorPosition actor : actors) {
            Integer[] actorPosition = actor.getPosition();
            for(Integer[] tile : traversableTiles) {
                if (actorPosition[0] == tile[0] && actorPosition[1] == tile[1] && !actor.getName().equals(name)) {
                    nearbyActors.add(actor);
                }
            }
        }
        return nearbyActors;
    }

    private void updatePlayers(List<ActorPosition> playersCopy) {
        // send update to each player
        for(ActorPosition p : playersCopy) {
            JSONObject playerUpdate = new JSONObject().put("type", "player-update");
            List<Integer[]> traversableTiles = getTraversableTiles(p.getPosition());
            playerUpdate.put("layout", getLayout(p.getPosition())); //populate layout
            playerUpdate.put("position", p.getPosition());
            playerUpdate.put("objects", getNearbyObjects(traversableTiles));
            playerUpdate.put("actors", getNearbyActors(traversableTiles, p.getName(), playersCopy));
            try {
                List playerUpdateEntry = new LinkedList(Arrays.asList(objectMapper.writeValueAsString(p.getName()), playerUpdate));
                managerTrace.add(playerUpdateEntry);
            } catch (Exception j) {
                j.printStackTrace();
            }
        }
    }
}