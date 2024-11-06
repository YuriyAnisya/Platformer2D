// SUPERCLASS
// To store types of values that both player and enemies will use. 

// To change the size of the hitbox, we need to take a look at our sprite. The character is about 20 pixels in width and 28 in height. We need to calculate a few values, the distance from (0,0) to the start of the hitbox. The hitbox starts at (21,4) - this will be by how much the offset will be when we render the player's sprites in the game. 

package enteties;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import main.Game;

public abstract class Entity { // Abstract class is a restricted class that cannot be used to create objects (to access it, it must be inherited from another class)

	protected float x, y; // when protected, only subclasses can use the these. 
	protected int width, height;
	protected Rectangle2D.Float hitbox;
	protected int aniTick, aniIndex;
	protected int state;
	protected float airSpeed; // store the speed which we are traveling through the air. 
	protected boolean inAir = false;
	protected int maxHealth; // maximum health a player has when the game/level starts
	protected int currentHealth; // current health will start at maxHealth. 
	protected Rectangle2D.Float attackBox; // it's an area that whenever the player clicks attack and attack is happening and if the enemy in that box area, then the enemy takes damage or dies.  
	protected float walkSpeed = 1.0f * Game.SCALE;
	
	
	public Entity(float x, float y, int width, int height) { // Constructor
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
	}
	
	protected void drawAttackBox(Graphics g, int xLvlOffset) {
		
		g.setColor(Color.RED);
		g.drawRect((int)(attackBox.x - xLvlOffset), (int)attackBox.y, (int)attackBox.width, (int)attackBox.height);
		
	}
	
	protected void drawHitbox(Graphics g, int xLvlOffset) {
		// for debugging the hitbox
		
		g.setColor(Color.PINK);
		g.drawRect((int)hitbox.x - xLvlOffset, (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
		
	}

	protected void initHitbox(int width, int height) {
		
		hitbox = new Rectangle2D.Float(x, y, (int)(width * Game.SCALE), (int)(height * Game.SCALE));
		
	}
	
//	protected void updateHitbox() { // take new x and y and put it to our hit box. 
//		
//		hitbox.x = (int) x;
//		hitbox.y = (int) y;
//		
//	}
	
	public Rectangle2D.Float getHitbox() {
		
		return hitbox;
		
	}
	
	public int getState() {
		return state;
	}
	
	public int getAniIndex() {
		return aniIndex;
	}
	
	public int getCurrentHealth() {
		return currentHealth;
	}
}
