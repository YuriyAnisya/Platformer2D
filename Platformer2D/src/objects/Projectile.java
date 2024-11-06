package objects;

import java.awt.geom.Rectangle2D;

import main.Game;

import static utilz.Constants.Projectiles.*;

public class Projectile {
	
	private Rectangle2D.Float hitbox;
	private int dir; // cannon balls can only go left or right. dir = -1 is to the left. dir = 1 is to the right.
	private boolean active = true;
	
	
	public Projectile(int x, int y, int dir) {
		
		int xOffset = (int)(-3 * Game.SCALE); // default for facing to the left. 
		int yOffset = (int)(5 * Game.SCALE); // default for both left and right. 
		
		if (dir == 1)
			xOffset = (int)(29 * Game.SCALE);
		
		hitbox = new Rectangle2D.Float(x + xOffset, y + yOffset, CANNON_BALL_WIDTH, CANNON_BALL_HEIGHT);
		this.dir = dir;
		
	}
	
	public void updatePos() {
		
		hitbox.x += dir * SPEED;
		
	}
	
	public void setPos(int x, int y) {
		
		// we need a correct spawn position of the ball. Without adjustment, it always spawns on a top left of the sprite. 
		hitbox.x = x;
		hitbox.y = y;
		
	}
	
	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return active;
	}

}
