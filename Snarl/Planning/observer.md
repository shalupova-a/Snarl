## Overall goal:
We are trying to expose the game API as a view only format to observers of the game and provide a centralized way for information to be transmitted to multiple viewers of the game. The viewers will have a view-only state of the game without input to change the game state. We will also reuse the GameManager class from our current codebase to register the users and update any state change that will affect the game view. The viewers will implement the Observer interface. As the viewers join the game, (we assume there is only one ongoing game), they will be added to the list of Observers within the GameManager. When the game state changes, the GameManager will call `publishStateToObservers`, rendering the updated GameView and printing out what has changed since the previous state (player move, adversary move, etc).

## Observer Interface:
We have an interface which will be implemented as by actual viewers of the game.

#### Methods:
        public void renderView(GameState gamestate)


- interface method to be implemented by classes that use the observer
- Has `renderView` which takes the gamestate item and uses it to render the view of the game 
- We assume that the observer will be implemented by viewer (maybe adversary too)

## ViewerObserver 
The ViewerObserver implements the Observer interface and implements its `renderView` method.

#### Fields
    private GameState gamestate

#### Methods:
    @Override
    public void renderView(GameState gameState)
    public void display2DAsciiView(GameState gameState)

The `renderView` parses the state data and gets it into a state such that it can call the `display2DAsciiView` with Levels. It can also be replaced by any other display methods likes `displayWithGUI` in the future based on how our project evolves.

## GameSubject Interface:
Following the observer pattern, we design a GameSubject interface that will have the methods to notify, register and unregister observers for the game

#### Methods:
    public void registerObserver(Observer o)
    public void unregisterObserver(Observer o)
    public void publishStateToObservers(GameState gameState)
- The `register` and `unregister` observer methods take an Object that implements the Observer interface and adds them to a list of observers
- `publishStateToObservers` will be used by the GameManager, the actual implementor of this GameSubject interface, and will provide the argument for this method.
        - It will likely iterate through the list of observers and call the `renderView()` method with the GameState as argument

## GameManager 
 The GameManger already exists in our design, but now it will be implementing the GameSubject interface and therefore implementing all its methods
 
     GameManager implements GameSubject

#### Fields
     private void List<Observer> observersSubscribed 

#### Methods
    @Override
    public void registerObserver(Observer o)
    @Override
    public void unregisterObserver(Observer o)
    @Override
    public void publishStateToObservers(GameState gameState)

The `observesSubscribed` list will be updated when observers are registered and unregistered

