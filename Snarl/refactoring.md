# Milestone 6 - Refactoring Report

**Team members:**
- Angelina Shalupova
- Sheetal Singh

**Github team/repo:**
Ilievrad


## Plan
- Flesh out unit tests and include tests from testing tasks to be used as unit tests
- Add interface for UI rendering and extract from `GameView` so we can plug in different UI depending on our needs
- Move checks like `withinRoom()`, `withinHallway()` to be inside the respective classes so we can do room.isWithinRoom Or maybe move into rulechecker
- Move MessageType class to model
- Add separate folder for interfaces or place them outside source so it is organized better
- Move checking cardinal moves to `RuleChecker`, since it would be better to keep the `PlayerClient` thin as possible for actions of the player
- Add exceptions and more validity checks
  - Bugs:
     - Player can move off grid
     - Players can be on the same tile
     - roomId of Player doesn't get updated when they move to new room
     - Players can sometime move to wall tiles
     - Game doesn't end when one player exits
- Hallway `withinBounds()` method is unimplemented, may require design consideration
- Go through entire codebase and delete unnecessary fields/methods
- Add JavaDoc
- Try to decouple MVC as much as possible

## Changes

- Added JavaDoc to all methods
- Updated roomId of player location when they move to new room/hallway within `handlePlayerMove()` in `GameManager`
- Fixed logic in `checkValidPlayerMove() `
    - Fixed bug where players can be on the same tile
- Implemented `withinBounds()` in `Hallway` class
    - Required a design change of `Hallway`
        - Added `initHorizontal` and `tiles` fields, as well as setters/getters
    - Added methods to `GameState` to set these new `Hallway` fields
        - `setFromRoomHallways`
        - `setHallwayTiles`
    - Simplified `GameView` method `drawHallway` to draw based on new `tiles` field
    - This fixed the bug where the player can move off grid
- Simplified code for `getTraversableTiles()` and moved this to `RuleChecker`
- Deleted unnecessary methods/fields in `GameState`
- Added exception if `start()` within `GameManager` is called with no players registered
- Fixed bug: game is won when key is found and player reaches exit
- Added junit tests and included some checks from existing testing task 
- Decouple model MessageType from controller in its own entity folder isntead of model folder


## Future Work
- Explore Swing, AsciiPanel or other gui libraries to switch from plain ascii text game.
    - Explore stuff like player camera so the player is zoomed in on their surroundings rather than the whole board in addition to the fog of war effect.

- Create our own exceptions instead of throwing the general `Exception`

## Conclusion

We were able to fix many of the bugs present in our code as well as reorganize and decouple some of our code.
There is likely more decoupling to be done, which we will do along the way if we notice something could be designed better.
There is also the opportunity to better our game visually, and throw more detailed exceptions to the user.
