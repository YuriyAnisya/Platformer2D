// Crab Behavior: 
// Game starts -> inAir or Fall? -> on floor? (if no, return to "inAir or Fall?") ->
// -> Idle -> Running -> is Player in sight? (if no, return to "Running") -> 
// -> Move towards player -> is Player in Range? (if no, return to "is Player in sight?") ->
// -> Attacking -> is AttackOver? -> Idle -> Running ->... 

package enteties;

import static utilz.Constants.EnemyConstants.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import main.Game;
import static utilz.Constants.Directions.*;

public class Crabby extends Enemy {
	
	// AttackBox
	private int attackBoxOffsetX;

	public Crabby(float x, float y) {
		
		super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
		
		initHitbox(22, 19);
		initAttackBox(); 
	
	}
	
	private void initAttackBox() {
		
		attackBox = new Rectangle2D.Float(x, y, (int)(82 * Game.SCALE), (int)(19 * Game.SCALE));
		attackBoxOffsetX = (int)(Game.SCALE * 30); // it's going to be 30 on a left side and 30 on a right side in width, and then 22 in the middle. 
		
	}

	public void update(int[][] lvlData, Player player) {
		
		updateBehavior(lvlData, player);
		updateAnimationTick();
		updateAttackBox();
		
	}
	
	private void updateAttackBox() {
		
		attackBox.x = hitbox.x - attackBoxOffsetX;
		attackBox.y = hitbox.y;
		
	}

	public void updateBehavior(int[][] lvlData, Player player) {
	
		if(firstUpdate)  // doing something for the first time we ever update.  
			firstUpdateCheck(lvlData);
		
		if (inAir) 
			updateInAir(lvlData);
		else {
			switch (state) {
			case IDLE:
				newState(RUNNING);
				break;
			case RUNNING:
				if (canSeePlayer(lvlData, player)) {
					turnTowardsPlayer(player);
					if (isPlayerCloseForAttack(player))
						newState(ATTACK);
				}
				move(lvlData);
				break;
			case ATTACK:
				if (aniIndex == 0)
					attackChecked = false; // to make sure that every time we restart the animation, attackChecked is false. 
				
				if (aniIndex == 3 && !attackChecked) // checking if player gets hit (hurt) at specific index of the crabby animation. attackCheked - to make sure we only do one check per animation
					checkPlayerHit(attackBox, player);
				break;
			case HIT:
				break;
			}
		}
	}
	
	public int flipX() {
		
		if (walkDir == RIGHT)
			return width;
		else 
			return 0;
	}
	
	public int flipW() {
		
		if (walkDir == RIGHT)
			return -1;
		else 
			return 1;
		
	}

}