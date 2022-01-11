  ```
  +---------+                        +---------+                    +---------+
  |   User  |                        |  Client |                    |  Server |
  +---+-----+                        +----+----+                    +---------+
      |           launch game             |      set up connection        |
      |    +----------------------->      |    +------------------->      |
      |                                   |                               |
      |                                   |                               |
      |    welcome message & inquire info |      response: welcome message|
      |    <-----------------------+      |    <--------------------+     |
      |                                   |                               |
      |                                   |                               |
      |                                   |                               |
      |    enter username/# of players    | send info/start game request  |
      |    +----------------------->      |      +------------------->    |
      |                                   |                               |
      | display 1st level board           | response: start board data    |
      |      <-----------------------+    |     <--------------------+    |
      |                                   |                               |
      | request: player move + interaction|                               |
      |                                   |      send request             |
      |    +----------------------->      |    +------------------->      | checks: valid move
      |                                   |                               |
      |                                   |                               |
      |  prints/displays: updated board   |       response: updated board |
      |    <--------------------------+   |    <------------------------+ |
      |                                   |                               |
      |       request: player exit        |        send request           |
      |   +--------------------------->   |    +---------------------->   |   checks: valid exit
      |                                   |                               |
      |         display updated board     |   response: updated board     |
      |         or display win/lose screen|     or win/lose screen        |
      |       <-----------------------+   |    <----------------------+   |
      |         exit command              |         disconnection request |
      |    +----------------------->      |    +------------------->      |
      |                                   |                               |
