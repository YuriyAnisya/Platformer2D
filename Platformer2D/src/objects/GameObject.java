// SUPERCLASS for game objects. 

package objects;

import static utilz.Constants.ANI_SPEED;
import static utilz.Constants.ObjectConstants.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Game;

public class GameObject {

	protected int x, y, objType;
	protected Rectangle2D.Float hitbox;
	protected boolean doAnimation, active = true; // some objects will be animated only when intersected with player's hitbox. active - if we pick up the object or destroy it, it won't be active anymore.  
	protected int aniTick, aniIndex;
	protected int xDrawOffset, yDrawOffset;
	
	public GameObject(int x, int y, int objType) {
		
		this.x = x;
		this.y = y;
		this.objType = objType;
		
	}
	
	protected void updateAnimationTick() {
		
		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(objType)) {
				aniIndex = 0;
				if (objType == BARREL || objType == BOX) {
					doAnimation = false;
					active = false;
				} else if (objType == CANNON_LEFT || objType == CANNON_RIGHT) 
						doAnimation = false;
			}
		}
		
	}
	
	public void reset() { // when we reset the level, we also want to reset the GameObject
		
		aniTick = 0;
		aniIndex = 0;
		active = true;
		
		if (objType == BARREL || objType == BOX || objType == CANNON_LEFT || objType == CANNON_RIGHT) // we don't want to animate them from the beginning. 
			doAnimation = false;
		else
			doAnimation = true; // if a potion, we want to animate from the start
		
	}
	
	protected void initHitbox(int width, int height) {
		
		hitbox = new Rectangle2D.Float(x, y, (int)(width * Game.SCALE), (int)(height * Game.SCALE));
		
	}
	
	public void drawHitbox(Graphics g, int xLvlOffset) {
		// for debugging the hitbox
		
		g.setColor(Color.PINK);
		g.drawRect((int)hitbox.x - xLvlOffset, (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
		
	}

	public int getObjType() {
		return objType;
	}

	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}

	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void setAnimation(boolean doAnimation) {
		this.doAnimation = doAnimation;
	}

	public int getxDrawOffset() {
		return xDrawOffset;
	}

	public int getyDrawOffset() {
		return yDrawOffset;
	}
	
	public int getAniIndex() {
		return aniIndex;
	}
	
	public int getAniTick() {
		return aniTick;
	}
	
}