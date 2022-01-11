# Adversary Class

This class implements the Actor interface

Fields:
* `private ActorType actorType`
    * initialized to `ActorType.Adversary` in constructor
* `private AdversaryType adversaryType`
    * either type ghost or type zombie
* `private int id`
    * unique id assigned by GameManager when adversaries are constructed
* `private Coordinate location`
    * global location of adversary
* `private Level initLevel`
    * full level information, set at the beginning of each level

Methods:
* getters for `actorType`, `adversaryType`, `id`, `location`, `initLevel`
* `public void setInitLevel(Level level)`
    * Called by the GameManager at the start of each level
* `public Coordinate makeMove(List<Coordinate> actorCoordinates)`
    * The adversary determines their next move based on the given player and adversary locations
        * We don't know yet what kind of moves are valid for an adversary, 
            but we assume that an adversary would attempt to move to the nearest player and avoid nearby adversaries
    * Called by GameManager anytime it's the adversary's turn
* `private void setLocation(Coordinate to)`
    * Updates the addversary's location
    * Called by GameManager once it validates that move returned from makeMove is valid

## ActorType

This will be an enum of `Player` and `Adversary`

## AdversaryType

This will be an enum of `Zombie` and `Ghost` (more types can be easily added in the future)

# Actor Interface

* `public ActorType getActorType()`
* `public int getActorId()`
* `public Coordinate getActorLocation()`


*Note: our Player class does not currently implement an Actor interface. 
After noticing the similarities between players and adversaries, we have decided to make this design change moving on.*