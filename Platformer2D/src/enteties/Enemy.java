// Main class all enemies are using. 

package enteties;

import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.*;
import static utilz.Constants.*;
import static utilz.Constants.Directions.*;

import java.awt.geom.Rectangle2D;
import main.Game;

public abstract class Enemy extends Entity {
	
	protected int enemyType;
	protected boolean firstUpdate = true;
	protected int walkDir = LEFT;
	protected int tileY;
	protected float attackDistance = Game.TILES_SIZE;
	protected boolean active = true; // When you start the game, all enemies are active. As soon as they die, the become not active, thus we don't want to update them anymore. 
	protected boolean attackChecked;
	
	public Enemy(float x, float y, int width, int height, int enemyType) {
		
		super(x, y, width, height);
		this.enemyType = enemyType;
		
		maxHealth = GetMaxHealth(enemyType); 
		currentHealth = maxHealth;
		
		walkSpeed = Game.SCALE * 0.35f;
	}
	
	protected void firstUpdateCheck(int[][] lvlData) {
	
		// fall mechanic. Is spawned in air, it falls down on a floor & do not allow it to fall off the edges.  
			if (!IsEntityOnFloor(hitbox, lvlData)) // if returns false, .meaning we are not on a floor...
				inAir = true;
			firstUpdate = false; // to make sure we enter the current if-check only once. 
				
	}
	
	protected void updateInAir(int[][] lvlData) {
		
		if(CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
			hitbox.y += airSpeed;
			airSpeed += GRAVITY;
		} else {
			inAir = false; 
			hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
			tileY = (int)(hitbox.y / Game.TILES_SIZE); // since it's here, it will always stay unchanged. 
		}
		
	}
	
	protected void move(int[][] lvlData) {
		
		float xSpeed = 0; 
		
		if (walkDir == LEFT)
			xSpeed = -walkSpeed;
		else 
			xSpeed = walkSpeed;
		
		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
			if (isFloor(hitbox, xSpeed, lvlData)) {
				hitbox.x += xSpeed;
				return;
			}
		// we will only get here if one of the if-checks above will return false.
		changeWalkDir();
		
	}
	
	protected void turnTowardsPlayer(Player player) {
		
		if (player.hitbox.x > hitbox.x)
			walkDir = RIGHT;
		else 
			walkDir = LEFT;
		
	}
	
	protected boolean canSeePlayer(int[][] lvlData, Player player) {
		
		// First, check if Player and Enemy are on a same tile in Y axis. For Enemies, they'll always be on a same Y axis, meaning not moving down or jumping up. 
		int playerTileY = (int)(player.getHitbox().y / Game.TILES_SIZE);
		if (playerTileY == tileY)
			if (isPlayerInRange(player))
				if (IsSightClear(lvlData, hitbox, player.hitbox, tileY)) // meaning, is there any obstacles in the way? 
					return true;
		
		return false;
	}

	protected boolean isPlayerInRange(Player player) {
		
		// We are going to have two ranges, one for "see range" and another for "attach range"
		int absValue = (int) Math.abs(player.hitbox.x - hitbox.x); 
		
		return absValue <= attackDistance * 5; // returning true if the absValue is less. If it's larger, it's going to return false. 
	}
	
	protected boolean isPlayerCloseForAttack(Player player) {
		
		int absValue = (int) Math.abs(player.hitbox.x - hitbox.x); 
		
		return absValue <= attackDistance;
		
	}

	protected void newState(int enemyState) { // changing one state to another. 
		
		this.state = enemyState;
		// new aniTick and aniIndex so that we don't start at the middle of animation. 
		aniTick = 0; 
		aniIndex = 0;
		
	}
	
	public void hurt(int amount) {
		
		currentHealth -= amount;
		if (currentHealth <= 0)
			newState(DEAD);
		else
			newState(HIT);
	}
	
	protected void checkPlayerHit(Rectangle2D.Float attackBox, Player player) {
		
		if (attackBox.intersects(player.hitbox))
			player.changeHealth(-GetEnemyDmg(enemyType));
		attackChecked = true;
	}
	
	protected void updateAnimationTick() {
		
		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(enemyType, state)) {
				aniIndex = 0;
				switch(state) {
				
				case ATTACK, HIT -> state = IDLE;
				case DEAD -> active = false; 
				
				}
			}
		}
	}
	
	
	protected void changeWalkDir() {
		
		if (walkDir == LEFT)
			walkDir = RIGHT;
		else 
			walkDir = LEFT;
		
	}
	
	public void resetEnemy() {
		
		hitbox.x = x;
		hitbox.y = y;
		firstUpdate = true;
		currentHealth = maxHealth;
		newState(IDLE);
		active = true;
		airSpeed = 0;
		
	}
	
	public boolean isActive() {
		return active;
	}

}
