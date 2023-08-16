1. Overview. 
	
This project is aimed at creating a dungeon comprised of caves and tunnels that has treasures, weapons and monsters where players can make moves from start to finish.

Description of the game:

A Dungeon is a network of tunnels and caves that are interconnected so that player can explore the entire world by traveling from cave to cave through the tunnels that connect them.
Location in the dungeon can be connected to at most four (4) other locations: one to the north, one to the east, one to the south, and one to the west. 
dungeon locations can "wrap" to the one on the other side of the grid. If the dungeon has a wrap location pair then it's a wrapping dungeon, else its non-wrapping.
In a dungeon, there should be a path from every cave in the dungeon to every other cave. Each dungeon can be constructed with a degree of interconnectivity.
Definition of interconnectivity = 0 is when there is exactly one path from every cave in the dungeon to every other cave in the dungeon. 
Increasing the degree of interconnectivity increases the number of paths between caves.
One cave is randomly selected as the start and one cave is randomly selected to be the end. Minimum path between the start and the end locations is at least of length 5.
Treasures can be placed in a percentage of caves, which the player can pick up when the player is in the location. Tunnels don't have treasure.
Arrows can be placed in caves / tunnels, which the player can pick up when the player is in the location.
Arrows can be shot by specifying the direction and distance to shoot. Distance to shoot is the number of caves that the arrow has to travel and has to be exact for a successful hit.
Monsters can be configured by specifying the difficulty level. They are found only in caves, there is always 1 monster in end cave.
A Monster needs 2 hits to be killed, entering a location with: healthy monster kills the player instantly; injured monster gives 50-50 survival chance.
Caves can have treasure of more than one type.
Game ends when player reaches the end location.

This program by modelling the entities that represents a dungeon as described above and enforcing constraints of the game, allows the user to experience the game.

-------------

2. List of features.

GUI to play the game interactively provides following functionalities:
	
	- all game settings can be accessed by Options->New Game menu in the menu bar. A game can be created anytime using this option.
	- player can move through the dungeon using a mouse click on the screen in addition to the keyboard arrow keys.
	- each location has treasure and weapon it contains displayed at the top left corner of the location.
	- when a monster is 2 locations apart, a stench will be denoted by a green haze on the location.
	- player's treasure and weapon information is always shown at the top pane of the game.
	- player can pick up treasure at a location by pressing "t" on the keyboard.
	- player can pick up an arrow at a location by pressing "a" on the keyboard.
	- player can shoot an arrow by pressing and holding "s" key on the keyboard followed by an arrow key to indicate the direction.
	- shot feedback will be provided by a popup.

The Standalone model Provides APIs for following functionalities:
	
	- placing at least three types of treasure: diamonds, rubies, and sapphires.
	- treasure to be added can be provided as a percentage of caves.
	- placing arrows in the dungeon (number of locations arrows are found works based on the treasure percentage).
	- placing monsters in the dungeon.
	- picking treasure / weapon at the player's location.
	- shooting the arrow by providing direction and distance to shoot.
	- infer the presence of monster by getting the smell at the location.
	- provide a description of the player that includes a description of what treasure the player has collected.
	- provide a description of the player's location that at the includes a description of treasure in the room 
	and the possible moves (north, east, south, west) that the player can make from their current location.
	- player to move from their current location.
	- player to pick up treasure that is located in their location.
	- reset the game and play the same dungeon again from the start.

Console based controller provides API to play game by attaching an input stream to read inputs, and writes to the provided appendable.

-------------

3. How To Run.

To launch GUI, the Jar file should be run without any arguments like,

java -jar pdp_project5_dungeon.jar

To launch consoled based controller, the Jar file can be run as,

java -jar pdp_project5_dungeon.jar [parameter]

[parameter] one of 'predictable', 'random', 'custom'

'predictable' - runs a predictable game automatically without user inputs except for how to proceed the end location (the dungeon only has 1 monster at end).
'random' - generates a random dungeon with preset values for dungeon construction but allows user to play the game interactively.
'custom' - gets arguments to construct dungeon from the user inputs and allows interactive gameplay.

e.g.,   java -jar pdp_project5_dungeon.jar predictable
	java -jar pdp_project5_dungeon.jar random
	java -jar pdp_project5_dungeon.jar custom

-------------

4. How to Use the Program.

To launch a GUI:
Graphic based controller is the point of entry, so an instance of it should be created either passing view or by passing both view and a model.
Once the controller is created, playGame() method should be called.

e.g.,

without a model,

DungeonView view = new DungeonViewImpl();
DungeonControllerFeatures control = new DungeonGraphicController(view);
control.playGame();

with a model,

DungeonView view = new DungeonViewImpl();
Dungeon model = new NonWrappingDungeon("player1", 4, 5,1, 100, 1, null);
DungeonControllerFeatures control = new DungeonGraphicController(view, model);
control.playGame();


To use the model separately:
All the model APIs to play the game are provided by the "Dungeon" interface.

Wrapping dungeon can be created by using "WrappingDungeon" concrete implementation:
	- creates the locations in the dungeon.
	- configures with treasure / arrows / monsters.

Non-wrapping dungeon can be created by using "NonWrappingDungeon" concrete implementation:
	- creates the locations in the dungeon.
	- configures with treasure / arrows / monsters.

After the setup is over, you must use enter() method to enter the dungeon.
Once entering the dungeon, you can use the provided APIs to:
	- check the possible moves from the location.
	- get the location of the player.
	- pick the treasure / arrows at the location.
	- move the player to neighboring location.
	- shoot arrows in a particular direction.
	- check the treasure collected by the player thus far.

Note: 
	- game ends when the player reaches the end location.
	- at each location, player must collect the treasure at the location using collectTreasure API except at the end location 
	(treasure at the end location will be collected automatically if the end location has treasure). 
	for other locations, if the treasure at the location is not collected then the treasure will not be collected.
	
All the model APIs are non-interactive, they expect details as arguments.

e.g., 

Dungeon maze = new NonWrappingDungeon("player", row, col, interConn, treasureP, difficulty);
maze.enter();
Map<PlayerDescription, List<String>> playerD = maze.describePlayer();
Map<LocationDescription, List<String>> locationD = maze.describeLocation();
List<String> possibleMoves = locationD.get(LocationDescription.MOVES);
Map<Treasure, Integer> treasureD = maze.collectTreasure();
maze.move(Direction.NORTH);
...
...
...
maze.reset();


To use console based controller:
All the controller APIs to play the game are provided by the "DungeonController" interface.

Controller should be initialized with an input stream and an appendable. 
After construction, control can be passed by calling the playGame method by passing the model.

The controller takes inputs from the input stream and print results to the appendable.

e.g.,

InputStreamReader input = new InputStreamReader(System.in);
DungeonController control = new DungeonConsoleController(input, System.out);
control.playGame(dungeon);


-------------

5. Description of Examples. 

Screenshots:

"game_settings" - game settings screen.
"start_state" - after creating the dungeon, the player is placed at the start.
"loc_arr_treasure" - view of a location with arrows and treasure.
"after_collecting_arrows_treasure" - view of a location after picking up arrows an treasure.
"weak_stench" - representation of a less pungent smell.
"strong_stench" - representation of a more pungent smell.
"shoot_screen" - interface to select the shoot distance.
"shoot_feedback" - feedback for the shot.
"game_end_healthy_monster" - entering the end location without killing the monster.
"game_end_injured_monster" - entering the end location with an injured monster.
"game_end_killing_monster" - entering the end location after killing the monster.

-------------

6. Design/Model Changes. 

Version 1.0 : class diagram in pdp_project5_original

Changes from project 4 design that modelled the controller and view.

Version 2.0 : class diagram in pdp_project5_final

The design changes were based on how the UI evolved from the initial design.

Change: Made main game panel to have multiple panels in a grid layout.
Reason:
	- When it came to redrawing a location, I didn't have to compute things based on a single panel, the compute to find where to draw images were simpler.
	- This also made tying mouse adapter to the panels, which also saved compute to translate the click coordinates to grid location.

Change: Needed to add methods for setting up menu bar and showing different prompts.
Reason:
	- for things like shoot, user input should be gathered and the feedback must be shown.

-------------

7. Assumptions.

Below are the assumptions made in the GUI implementation:
	- The treasure at the end location is automatically picked, if the player makes it alive.
	- The game window is fixed and cannot be resized.
	- using the graphic controller, player can shoot only between distance 1-5.
	- player picks all/nothing when attempting to pick up arrows/treasure.

-------------

8. Limitations. 

Limitation of the GUI implementation:
	- The focus in the screen doesn't move based on user moves (e.g, when moving to a wrapped location)
	- When creating a dungeon of larger size, the start location might not be in the view, user has to scroll and find the start location.
	- player cannot choose between which treasure to pick / leave at a location.

-------------

9. Citations.

Heavily used following sites for swing related things:
https://docs.oracle.com/
https://stackoverflow.com/
Turtle code from class.

Game settings option pane:
https://stackoverflow.com/questions/6555040/multiple-input-in-joptionpane-showinputdialog/6555051

Player image:
https://www.seekpng.com/ipng/u2q8t4i1e6u2a9o0_skeleton-archer/

-------------
