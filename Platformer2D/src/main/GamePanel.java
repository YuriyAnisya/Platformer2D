// JPanel is generally used to contain different elements that you want to display in a given layout.
// Animation:
// One of the simplest ones is to store all the sprites of an animation in an array. Every index of the array is one sprite. As the game goes on, index ticker is increased every update and once that ticker have reached the desired value, the index of the array is increased by 1 and the next image (sprite) is drawn. Basically, we create a loop that moves through the array. 

package main;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import inputs.KeyboardInputs;
import inputs.MouseInputs;
import static main.Game.GAME_WIDTH;
import static main.Game.GAME_HEIGHT;

public class GamePanel extends JPanel {

	private MouseInputs mouseInputs;
	private Game game;
	
	public GamePanel(Game game) { // Constructor

		mouseInputs = new MouseInputs(this);
		this.game = game;

		setPanelSize();
		addKeyListener(new KeyboardInputs(this)); // keyboard inputs. pass gamePanel to keyboards constructor using
													// "this" keyword.
		addMouseListener(mouseInputs); // mouse inputs.
		addMouseMotionListener(mouseInputs);
	}

	private void setPanelSize() {

		Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
		setPreferredSize(size);
	}
	
	public void updateGame() {
		
		
	}

	public void paintComponent(Graphics g) { // We never call this method directly. paintComponent gets called whenever
												// we start the program (it's like a constructor, it gets called
												// automatically whenever we create a GamePanel object). JPanel has all
												// the code needed to take care of whatever we made, but we can only
												// draw something if we create a method paintComponent. JPanel cannot
												// draw by itself, it need a graphic object for that.

		super.paintComponent(g); // call paintComponent from superclass. needs to be first to avoid picture bugs.
		
		game.render(g);
		
	}

	public Game getGame() {
		return game;
	}
}
