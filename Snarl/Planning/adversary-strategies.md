## Adversary Strategies

Both zombies and ghosts attempt to move as close as possible to the closest player. This is determined by taking the
manhattan distance between the adversary and all the players, and finding the minimum. Adversaries are also able to move
to tiles with items, but they don't have the ability to interact with them.

### Zombie
A zombie is restricted to the moves it can make; it cannot move out of a room or to walls.

**Strategy**: Move as close as possible to a player, while avoiding other adversaries. If there are no players within the room, 
choose a random tile 1 cardinal move away to move to. If there are no possible moves, stay at current location.
#### Examples
1. There is a player in the same room
    * On every turn, the zombie moves as close as possible to this player, following it around
2. There are multiple players in the same room
    * On every turn, the zombie chooses the closest player and attempts to move towards it
3. There are no players in the same room.
    * The zombie chooses a random tile to move to (cannot be a wall or adversary)
4. There are no valid moves
    * Zombie stays put


### Ghosts
A ghost is less restricted, and can move to tiles occupied by adversaries, or to wall tiles. They can also
explore rooms and hallways.

**Strategy**: Move as close as possible to a player, no matter what room they are in. This may result in moving to a wall tile
and getting transported to another room. If there are no possible moves, stay at current location.
####  Examples
1. There is a player in the same room
    * On every turn, the ghost moves as close as possible to this player, following it around
2. There are multiple players in the same room
    *  On every turn, the ghost chooses the closest player and attempts to move towards it
3. The closest player is in a different room
    * The ghost moves as close as possible to this player, and may traverse hallways/different rooms to do so
4. There are no valid moves
    * Ghost stays put




