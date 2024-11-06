package objects;

import main.Game;

public class Cannon extends GameObject {
	
	private int tileY; // to check if player is on a same tile as cannon. In other words, to check if cannon can see the player. 

	public Cannon(int x, int y, int objType) {
		
		super(x, y, objType);
		
		tileY = y / Game.TILES_SIZE;
		initHitbox(40, 26); // we don't care about the hitbox for cannon, we just need the x and y. 
		hitbox.x -= (int)(4 * Game.SCALE); // to make sure that cannon is in the center of the tile we place it on. 
		hitbox.y += (int)(6 * Game.SCALE);
	
	}
	
	public void update() {
		
		if (doAnimation)
			updateAnimationTick();
		
	}
	
	public int getTileY() {
		return tileY;
	}	

}
