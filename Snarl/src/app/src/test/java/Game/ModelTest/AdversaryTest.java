/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Game.ModelTest;

import Game.Model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class AdversaryTest {

    private Level initLevel() throws Exception {
        Integer[][] tilesRoom1 = {{0, 0, 0, 0, 0,},
                {0, 1, 1, 1, 0},
                {0, 1, 1, 1, 2},
                {0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0}};

        Integer[][] tilesRoom2 = {{0, 2, 0, 0},
                {0, 1, 1, 0},
                {0, 1, 1, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 0,}};

        Room room1 = new Room(new Integer[]{0, 0}, new Bounds(5, 5), tilesRoom1);
        Room room2 = new Room(new Integer[]{9, 7}, new Bounds(4, 5), tilesRoom2);
        Integer[][] waypoints = new Integer[][]{{10, 2}};
        Hallway hallway = new Hallway(new Integer[]{4, 2}, new Integer[]{10, 7}, waypoints);

        List<Room> rooms = new ArrayList<Room>();
        List<Hallway> hallways = new ArrayList<Hallway>();
        List<Item> items = new ArrayList<Item>();
        Item exit = new Item("exit", new Integer[]{10, 11});
        Item key = new Item("key", new Integer[]{11, 9});
        rooms.add(room1);
        rooms.add(room2);
        hallways.add(hallway);
        items.add(exit);
        items.add(key);
        Level level = new Level(rooms, hallways, items);

        return level;
    }

    private Level initLevel2() throws Exception {
        /**
         * X X X     X X X X
         * X _ ] _ _ ] _ _ X
         * X X X     X _ _ X
         *           X ] X X
         *
         *
         *
         *
         */
        Integer[][] tilesRoom1 = {{0, 0, 0},
                {0, 1, 2},
                {0, 0, 0}};

        Integer[][] tilesRoom2 = {{0, 0, 0, 0},
                {2, 1, 1, 0},
                {0, 1, 1, 0},
                {0, 1, 1, 0},
                {0, 2, 0, 0}};

        Integer[][] tilesRoom3 = {{0, 2, 0, 0},
                {0, 1, 1, 0},
                {0, 1, 1, 0},
                {0, 1, 1, 2},
                {0, 0, 0, 0}};

        Integer[][] tilesRoom4 = {{0, 2, 0, 0},
                {0, 1, 1, 0},
                {2, 1, 1, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 0}};

        Room room1 = new Room(new Integer[]{0, 0}, new Bounds(3, 3), tilesRoom1);
        Room room2 = new Room(new Integer[]{5, 0}, new Bounds(4, 5), tilesRoom2);
        Room room3 = new Room(new Integer[]{5, 7}, new Bounds(4, 5), tilesRoom3);
        Room room4 = new Room(new Integer[]{12, 10}, new Bounds(4, 5), tilesRoom4);
        Hallway hallway1 = new Hallway(new Integer[]{2, 1}, new Integer[]{5, 1}, null);
        Hallway hallway2 = new Hallway(new Integer[]{6, 4}, new Integer[]{6, 7}, null);
        Hallway hallway3 = new Hallway(new Integer[]{8, 10}, new Integer[]{12, 12}, null);

        List<Room> rooms = new ArrayList<>();
        List<Hallway> hallways = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        Item exit = new Item("exit", new Integer[]{5, 2});
        Item key = new Item("key", new Integer[]{7, 1});

        rooms.add(room1);
        rooms.add(room2);
        rooms.add(room3);
        rooms.add(room4);
        hallways.add(hallway1);
        hallways.add(hallway2);
        hallways.add(hallway3);
        items.add(exit);
        items.add(key);

        Level level = new Level(rooms, hallways, items);

        return level;
    }


    @Test public void makeMoveZombie() throws Exception {
        Integer[] zombiePosn = new Integer[]{1, 1};

        Adversary zombie = new Adversary(1234, ActorType.zombie, zombiePosn);
        zombie.setInitLevel(initLevel());

        Player p1 = new Player(1, ActorType.player, new Integer[]{1, 3});
        Player p2 = new Player(2, ActorType.player, new Integer[]{11, 10});
        List<Integer[]> playerPosns = new ArrayList<>(Arrays.asList(p1.getActorPosition(), p2.getActorPosition()));

        Integer[] move1 = zombie.makeMove(playerPosns, new ArrayList<>());

        assertTrue(Arrays.equals(move1, new Integer[]{1, 2}));

        Integer[] move2 = zombie.makeMove(playerPosns, new ArrayList<>());
        assertTrue(Arrays.equals(move2, (new Integer[]{1, 3}))); // moved to player tile, remove player

        playerPosns.remove(0);

        Integer[] move3 = zombie.makeMove(playerPosns, new ArrayList<>());
        assertFalse(Arrays.equals(move3, move2));
    }

    @Test public void makeMoveGhost() throws Exception {

        Integer[] ghostPosn = new Integer[]{1, 1};

        Adversary ghost = new Adversary(1, ActorType.ghost, ghostPosn);
        ghost.setInitLevel(initLevel());

        Player p1 = new Player(1, ActorType.player, new Integer[]{1, 3});
        Player p2 = new Player(2, ActorType.player, new Integer[]{11, 10});
        List<Integer[]> playerPosns = new ArrayList<>(Arrays.asList(p1.getActorPosition(), p2.getActorPosition()));

        Integer[] move1 = ghost.makeMove(playerPosns, new ArrayList<>());
        assertTrue(Arrays.equals(move1, new Integer[]{1, 2}));

        Integer[] move2 = ghost.makeMove(playerPosns, new ArrayList<>());
        assertTrue(Arrays.equals(move2, new Integer[]{1, 3})); // moved to player tile, remove player
        playerPosns.remove(0);

        Integer[] move3 = ghost.makeMove(playerPosns, new ArrayList<>());
        assertFalse(Arrays.equals(move3, move2));

    }

}
