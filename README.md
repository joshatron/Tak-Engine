Tak Engine
==========

The tak engine contains the simulator for a tak game.

The main class to interface with it is GameState.
You can initialize the board, check legality of turns, make moves, and check for win states.

The engine also provides an interface for playing games.
The Games class runs through a specified number of games.
You can implement GameHooks that allows you to run code before and after each turn.
You can also implement players that can either be implementations of AI players or human ones.

Todo
----

 * Add exporting and importing games to files
 * Add scoring games
 * Add getting the number of board positions each player owns
 * Add way to incrementally check for some win conditions to speed up win checking
