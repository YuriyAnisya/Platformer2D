package objects;

import static utilz.Constants.ObjectConstants.*;

import main.Game;

public class GameContainer extends GameObject{

	public GameContainer(int x, int y, int objType) {
		
		super(x, y, objType);
		createHitbox(); // we will have two different sizes of hitbox depending on type of object we have, so we need a method
		
	}

	private void createHitbox() {
		
		if (objType == BOX) {
			
			initHitbox(25, 18);
			xDrawOffset = (int)(7 * Game.SCALE);
			yDrawOffset = (int)(12 * Game.SCALE);
			
		} else {
			
			initHitbox(23, 25);
			xDrawOffset = (int)(8 * Game.SCALE);
			yDrawOffset = (int)(5 * Game.SCALE);
		}
		
		// we need to place our objects on a ground, so that they touch the floor. The objects are all drawn from the top left corner, so the difference between the bottom of the sprite to the floor is something we need to increase our hitbox. 
		// in other words, we need to increase or add to our hitbox.y and even x a little bit so we get barrel and box to touch the floor. That will actually be the same for both of them since both of them are placed on a bottom of the sprite. 
		hitbox.y += yDrawOffset + (int)(2 * Game.SCALE); // First, we move them down by the offset we get from our yDrawOffset because we are drawing it from the top left corner of our hitbox, not the tile. Our hitbox is not the same as the tile. We get the 2 from the difference of the sprite to the tile. The sprite is 30 pixels in height and the tile is 32. 
		hitbox.x += xDrawOffset / 2; // to the center.  
	}
	
	public void update() {
		
		if (doAnimation)
			updateAnimationTick();
	
	}

}
