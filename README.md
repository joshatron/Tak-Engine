Tak Engine
==========

The tak engine contains the simulator for a tak game.

The main engine is TakEngine.
It is an aggregate engine that uses TakEngineFirstTurns for the first 2 turns
and takEngineMainTurns for the turns after.
They manipulate a TakState, which is the current state of the game.
The engines contain no config, and are stateless.
They used the BGT-Engine as a base.
