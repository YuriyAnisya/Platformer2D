package objects;

import main.Game;

public class Potion extends GameObject {
	
	private float hoverOffset; // meaning the current offset that we are playing to the hitbox, so that there is some difference. 
	private int maxHoverOffset, hoverDir = 1; // hoverDir = 1 so that it goes down first. 
	
	public Potion(int x, int y, int objType) {
		
		super(x, y, objType);
		doAnimation = true;
		initHitbox(7, 14);
		xDrawOffset = (int)(3 * Game.SCALE); // offset from sprites. 3 on x, and 2 on y. 
		yDrawOffset = (int)(2 * Game.SCALE);
		
		maxHoverOffset = (int)(10 * Game.SCALE);
	}
	
	public void update() {
		
		updateAnimationTick();
		updateHover();
		
	}

	private void updateHover() {
		
		hoverOffset += (0.075F * Game.SCALE * hoverDir);
		
		if (hoverOffset >= maxHoverOffset)
			hoverDir = -1;
		else if (hoverOffset <= 0)
			hoverDir = 1;
		
		hitbox.y = y + hoverOffset;
	}
	
	

}
