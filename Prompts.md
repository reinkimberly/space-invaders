Prompt 1: I'm building Space Invaders in Java using Swing, split into three files: GameModel.java, GameView.java, and GameController.java. GameView should extend JPanel and be hosted in a JFrame. GameController should have the main method and wire the three classes together. GameModel must have no Swing imports. For now, just create the three class shells with placeholder comments describing what each class will do. The program should compile and open a blank window.

Produced: It creates three basic Java classes following the MVC structure: GameModel , GameView, and GameController. When you run it, the program compiles and opens a blank window, but no game elements appear yet since everything is just placeholders.


Prompt 2: Fill in GameModel.java. The model should track: the player's horizontal position, the alien formation (5 rows of 11), the player's bullet (one at a time), alien bullets, the score, and lives remaining (start with 3). Add logic to: move the player left and right, fire a player bullet if one isn't already in flight, advance the player's bullet each tick, move the alien formation right until the edge then down and reverse, fire alien bullets at random intervals, and detect collisions between bullets and aliens or the player. No Swing imports.

Produced: It builds GameModel so it fully manages the game state, tracking the player position, alien grid, bullets, score, and lives. It also adds the game logic like movement, shooting, alien behavior, random enemy fire, and collision detection with no Swing imports.


Prompt 3: Fill in GameView.java. It should take a reference to the model and draw everything the player sees: the player, the alien formation, both sets of bullets, the score, and remaining lives. Show a centered game-over message when the game ends. The view should only read from the model — it must never change game state.

Produced: It completes GameView so it draws all visible game elements, player, aliens, bullets, score, and lives—based on the data in the model. It also displays a centered game-over message when needed, and strictly acts as a read only layer that never modifies the game state.


Prompt 4: Fill in GameController.java. Add keyboard controls so the player can move left and right with the arrow keys and fire with the spacebar. Add a game loop using a Swing timer that updates the model each tick and redraws the view. Stop the loop when the game is over.

Produced: It completes GameController by adding keyboard input (arrow keys to move, spacebar to shoot) and a game loop using a Swing timer. Each tick updates the model and repaints the view, and the loop stops automatically when the game is over.


Prompt 5: Create a separate file called ModelTester.java with a main method. It should create a GameModel, call its methods directly, and print PASS or FAIL for each check. Write tests for at least five behaviors: the player cannot move past the left or right edge, firing while a bullet is already in flight does nothing, a bullet that reaches the top is removed, destroying an alien increases the score, and losing all lives triggers the game-over state. No testing libraries — just plain Java.

Produced: It adds a ModelTester.java file with a main method that manually tests the GameModel without any testing frameworks. It creates a model instance, directly calls its methods, and prints simple PASS/FAIL results for at least five behaviors: boundary limits for player movement, single active player bullet restriction, bullet removal at the top of the screen, score increase when an alien is destroyed, and game-over triggering when lives reach zero.


Prompt 6: In GameModel.java, add a list of shield rectangles positioned between the player and the alien formation. Reduce a shield's health when hit by a bullet from either side. Remove the shield when health reaches zero. No Swing imports.

Produced: It extends GameModel by adding a set of shield objects (stored as simple rectangles or custom data structures) placed between the player and aliens. Each shield has health that decreases when hit by either player or alien bullets, and the shield is removed from the game once its health reaches zero. All of this is handled purely in the model, with no Swing imports, since it’s still responsible only for game state and logic.


Prompt 7: In GameView.java's paintComponent method only, draw the shields from the model's shield list. Use the shield's health value to choose a color from full green to dim red. Do not call any model mutating methods.

Produced: It updates only the paintComponent method in GameView so that it iterates through the model’s shield list and draws each shield on screen. Each shield’s color is determined by its remaining health, blending from bright green (full health) to dim red (low health). The view strictly remains read-only, only accessing shield data from the model and never calling any methods that change game state.


Prompt 8: In GameModel.java, increase the alien movement speed each time an alien is destroyed. Expose a method the Controller can call to get the current recommended timer interval. Do not touch the View.

Produced: It modifies GameModel so that every time an alien is destroyed, the alien movement speed increases (making the game progressively harder). It also adds a method that the controller can use to adjust the Swing timer delay dynamically based on the current speed. The View is not changed at all and continues to only render whatever state the model provides.

Prompt 9: In GameView.java can you add a high score tracked accross games. Can you add this to the upper left-hand corner.

Produced: A highscore displayed in the top left corner of the screen.

Prompt 10: Add levels up to level 10. Each level gets faster and more difficult. The next level starts after the ladt one ends. Make sure when the new level starts everything resets and the screen displays the Level.

Produced: This produced a great view of what level we are on, but it never actually moved to the next level. 

Trouble Shooting: Once the aliens are off of the screen, reset the game and move onto the next level. But I still wanted changes. I also then got this error code:
Error: Main method not found in class GameModel, please define the main method as:
   public static void main(String[] args)
or a JavaFX application class must extend javafx.application.Application (I asked AI to fix this and it did.)
I want to pause the game when you get to the next level, at this pause I want a screen to pop up that says Next Level: with what level you will be on. I want you to be able to continue to the next level when you press c. This prompt fixed my issues and I was very happy with the outcome. The game now has ten levels and I want it to say Congratulations You Beat the Aliens! after the tenth level.
When you get done with the tenth level, stop the game and show Congratulations You Beat the Aliens! on the game screen. Make sure all words are centered and seen within the screen.


