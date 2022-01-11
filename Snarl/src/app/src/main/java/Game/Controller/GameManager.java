package Game.Controller;

import Game.Model.*;
import Game.NetworkMessage.NetworkOutMessage;
import Player.LocalPlayer;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
    private State gameState;
    private RuleChecker ruleChecker;
    private static final boolean ENABLE_COMBAT_SYSTEM = true;

    public GameManager(int levelId, Level currentLevel, List<LocalPlayer> localPlayers) {
        currentLevel.setItems();
        this.gameState = createInitialState(levelId, currentLevel, localPlayers);
        this.ruleChecker = new RuleChecker(gameState);
        updatePlayerLocations();
        updateAdversaryLocations();
        this.gameState.populatePlayerList();
        this.gameState.populateAdversaryList(this.gameState.getLevel());
    }

    //*******************************************************************************************************
    //*******************************************************************************************************
    //**************************    INIT GAME STATE OR EACH LEVEL     ***************************************
    //*******************************************************************************************************
    //*******************************************************************************************************
    private State createInitialState(int levelId, Level currentLevel, List<LocalPlayer> localPlayers) {
        return new State(initPlayers(localPlayers), initAdversaries(levelId), currentLevel, true);
    }

    private List<ActorPosition> initPlayers(List<LocalPlayer> localPlayers) {
        List<ActorPosition> players = new LinkedList<>();
        for (LocalPlayer player : localPlayers) {
            // random zombie and ghost
            players.add(new ActorPosition(ActorType.player, player.getName()));
        }
        return players;
    }

    private List<ActorPosition> initAdversaries(int levelId) {
        List<ActorPosition> adversaries = new LinkedList<>();
        for (int i = 0; i < levelId + 2; i++) {
            // random zombie and ghost
            ActorType actorType = i % 2 == 0 ? ActorType.ghost : ActorType.zombie;
            adversaries.add(new ActorPosition(actorType, i + ""));
        }
        return adversaries;
    }

    private JSONObject createScoreMap(String name) {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("type", "player-score");
        jsonObj.put("name", name);
        jsonObj.put("exits", 0);
        jsonObj.put("ejects", 0);
        jsonObj.put("keys", 0);

        return jsonObj;
    }

    //*******************************************************************************************************

    //*******************************************************************************************************
    //*************************************   MOVEMENTS   ***************************************************
    //*******************************************************************************************************
    public NetworkOutMessage playerMoveRequest(Integer[] from, Integer[] to, LocalPlayer localPlayer) {
        // stayed put
        if(to == null) {
            return NetworkOutMessage.OK_STAYED_PUT;
        } else if (this.ruleChecker.checkValidPlayerMove(from, to)) {
            if (this.ruleChecker.foundKey(to)) {
                this.ruleChecker.removeKey();
                return NetworkOutMessage.STEPPED_ON_KEY;
            }
            else if (!this.ruleChecker.isExitLocked() && this.ruleChecker.exited(to)) {
                this.getState().removePlayer(localPlayer.getName());
                return NetworkOutMessage.STEPPED_ON_EXIT;
            } else if (this.ruleChecker.ejected(to)) {
                return doCombat(localPlayer.getName(), to);
            }
            return NetworkOutMessage.OK;
        } else {
            return NetworkOutMessage.INVALID;
        }
    }
    //*******************************************************************************************************

    public NetworkOutMessage doCombat(String localPlayername, Integer[] to) {
        if (!ENABLE_COMBAT_SYSTEM) {
            return NetworkOutMessage.EJECTED;
        }
        System.out.println("doing combat");
        Player player = this.getState().getPlayerList()
                .stream()
                .filter(i -> i.getPlayerName().equals(localPlayername))
                .findAny()
                .get();
        ActorPosition playerToSend = this.getState().getPlayers()
                .stream()
                .filter(i -> i.getName().equals(localPlayername))
                .findAny()
                .get();
        Adversary adversary = this.getState().getAdversaryList()
                .stream()
                .filter(i -> i.getActorPosition()[0].equals(to[0]) && i.getActorPosition()[1].equals(to[1]))
                .findAny().get();
        CombatSystem.getInstance().duel(player, adversary);

        System.out.println(player.getHitPoints());
        playerToSend.setHitPoints(player.getHitPoints());
        if (adversary.getHitPoints() == 0) {
            // Remove adversary from adversarylist and actor position list
            // we remove enemy by filtering the enemy out of the list and setting the list again
            this.getState().setAdversaryList(this.getState().getAdversaryList()
                    .stream()
                    .filter(i -> !(i.getActorPosition()[0] + "__" + i.getActorPosition()[1]).equals(to[0] + "__" + to[1]))
                    .collect(Collectors.toList()));
            this.getState().setAdversaries(this.getState().getAdversaries()
                    .stream()
                    .filter(i -> !(i.getPosition()[0] + "__" + i.getPosition()[1]).equals(to[0] + "__" + to[1]))
                    .collect(Collectors.toList()));
            // Send message for enemy kill
            NetworkOutMessage.DYNAMIC_DAMAGE_MESSAGE
                    .setNetworkMessage(MessageFormat.format(NetworkOutMessage.DAMAGE_KILLED_ENEMY.getNetworkMessage(), localPlayername, adversary.getActorType().name()));
            return NetworkOutMessage.DYNAMIC_DAMAGE_MESSAGE;
        } else if (player.getHitPoints() == 0) {
            // player hit points go to 0 and is ejected
            return NetworkOutMessage.EJECTED;
        } else {
            // player deals some damage
            NetworkOutMessage.DYNAMIC_DAMAGE_MESSAGE
                    .setNetworkMessage(
                            MessageFormat.format(NetworkOutMessage.DAMAGE_MESSAGE.getNetworkMessage(), localPlayername, adversary.getActorType().name(), adversary.getHitPoints(), playerToSend.getHitPoints()));
            return NetworkOutMessage.DYNAMIC_DAMAGE_MESSAGE;
        }
    }

    private void updatePlayerLocations() {
        List<ActorPosition> players =  this.gameState.getPlayers();
        for(ActorPosition player : players) {
            player.setPosition(generateRandomLocation(false));
            player.setHitPoints(200);
        }

    }

    private void updateAdversaryLocations() {
        List<ActorPosition> adversaries =  this.gameState.getAdversaries();
        for(ActorPosition adversary : adversaries) {
            adversary.setPosition(generateRandomLocation(true));
            adversary.setHitPoints(200);
        }
    }

    public State getState() {
        return this.gameState;
    }

    public boolean ejected(Integer[] point) {
        return this.ruleChecker.ejected(point);
    }

    public void beckonAdversaryMove() {

        List<Integer[]> playerCoordinates = this.gameState.getPlayers().stream().map(ActorPosition::getPosition).collect(Collectors.toList());
        List<Integer[]> playerCoords = playerCoordinates.stream().map(i -> new Integer[]{i[0], i[1]}).collect(Collectors.toList());

        Map<String, Integer[]> oldPositionToNewPosition = new HashMap<>();

        this.gameState.getAdversaryList().forEach(i -> {

            try {
                String key = i.getActorPosition()[0]+"__"+i.getActorPosition()[1];
                Integer[] movingTo =
                        i.makeMove(playerCoords, this.gameState.getAdversaryList().stream().map(Actor::getActorPosition).collect(Collectors.toList()));

                oldPositionToNewPosition.put(key, movingTo);

            } catch (Exception e) {
                System.out.println("Exception for adversaryMove");
                e.printStackTrace();
            }
        });
        this.gameState.getAdversaries()
                .forEach(i -> {
                    i.setPosition(oldPositionToNewPosition.get(i.getPosition()[0] + "__" + i.getPosition()[1]));
                });

    }

    public Integer[] generateRandomLocation(boolean adversary) {
        // TODO: allow location to be in hallway
        Random rand = new Random();
        Integer[] randLocation;

        List<Room> rooms = this.gameState.getLevel().getRooms();
        // need to change rand number generation since 0 was giving NPE
        int rand_room = rand.nextInt(rooms.size());
        Room room = rooms.get(rand_room);

        Integer[] origin = room.getOrigin();

        boolean foundTile = false;
        Integer[] point = new Integer[2];
        while (!foundTile) {
            Bounds roomBounds = room.getBounds();

            int x = rand.nextInt(roomBounds.getColumns() - 1);
            int y = rand.nextInt(roomBounds.getRows() - 1);
            point = new Integer[]{y + origin[0], x + origin[1]};

            try {
                if(room.isPointTraversable(point) && this.ruleChecker.validInitialLocation(point)) {
                    foundTile = true;

                }
            } catch (Exception e) {
                // we dont care
            }
        }
        return point;
    }


    public Integer[][] getLayout(Integer[] position) {

        Integer[][] layout = new Integer[5][5];

        int starti = -2, endi = 2;
        int startj = -2, endj = 2;
        // Edge case when it is at the edge of the box
        if(position[0] + starti == -1) {
            starti = -1; endi = 3;
        }
        // Edge case when it is at the edge of the box
        if(position[1] + startj == -1) {
            startj = -1; endj = 3;
        }

        for(int i = starti; i <= endi; i++) {
            for(int j = startj; j <= endj; j++) {
                Integer[] tilePosn = {position[0] + i, position[1] + j};
                layout[i + (-1 * starti)][j + (-1 * startj)] = 0;

                for(Room room : this.gameState.getLevel().getRooms()) {
                    if(room.isPointWithinRoom(tilePosn)) {
                        int[] truePoint = {tilePosn[0] - room.getOrigin()[0], tilePosn[1] - room.getOrigin()[1]};
                        int tile = room.getLayout()[truePoint[0]][truePoint[1]];
                        layout[i + (-1 * starti)][j + (-1 * startj)] = tile;
                    }
                }
                for(Hallway hallway : this.gameState.getLevel().getHallways()) {
                    if(hallway.isPointInHallway(tilePosn) && layout[i + (-1 * starti)][j + (-1 * startj)] != 2) {
                        layout[i + (-1 * starti)][j + (-1 * startj)] = 1;
                    }
                }
            }
        }
        return layout;
    }

    public List<Item> getNearbyObjects(Integer[] position) {
        return getNearbyObjects(this.ruleChecker.getTraversableTiles(position));
    }

    private List<Item> getNearbyObjects(List<Integer[]> traversableTiles) {
        Level level = this.gameState.getLevel();
        List<Item> items = level.getItems();

        List<Item> nearbyItems = new ArrayList<>();

        for(Item item : items) {

            for(Integer[] tile : traversableTiles) {
                if (item.isObjectOnPoint(tile)) {
                    nearbyItems.add(item);
                }
            }
        }
        return nearbyItems;
    }

    public List<ActorPosition> getNearbyActors(Integer[] position) {
        return getNearbyActors(this.ruleChecker.getTraversableTiles(position), this.gameState.getActors());
    }

    private List<ActorPosition> getNearbyActors(List<Integer[]> traversableTiles, List<ActorPosition> actors) {

        List<ActorPosition> nearbyActors = new ArrayList<>();

        for(ActorPosition actor : actors) {
            for(Integer[] tile : traversableTiles) {
                if (actor.isActorInPosition(tile)) {
                    nearbyActors.add(actor);
                }
            }
        }
        return nearbyActors;
    }
}