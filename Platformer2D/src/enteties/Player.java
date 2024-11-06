package enteties;

import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;
import static utilz.Constants.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import audio.AudioPlayer;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

public class Player extends Entity {

	private BufferedImage[][] animations; 
	private boolean left, right, jump;
	private boolean moving = false, attacking = false;
	private int[][] lvlData;
	private float xDrawOffset = 21 * Game.SCALE; // must be scaled in case we scale the game.
	private float yDrawOffset = 4 * Game.SCALE;
	
	// Jumping & gravity. 
	private float jumpSpeed = -2.25f * Game.SCALE; // value we apply to airspeed when we are jumping. Negative value because we are going up in y direction. 
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE; // in case we are hitting the roof, we are setting a new value to the airspeed. 
	
	// StatusBar UI
	private BufferedImage statusBarImg;
	// Position and size of the actual bar (image)
	private int statusBarWidth = (int)(192 * Game.SCALE);
	private int statusBarHeight = (int)(58 * Game.SCALE);
	private int statusBarX = (int)(10 * Game.SCALE);
	private int statusBarY = (int)(10 * Game.SCALE);
	// Position and size of actual health bar (it changes the size depending on the health of the player - red bar)
	private int healthBarWidth = (int)(150 * Game.SCALE);
	private int healthBarHeight = (int)(4 * Game.SCALE);
	private int healthBarXStart = (int)(34 * Game.SCALE);
	private int healthBarYStart = (int)(14 * Game.SCALE);
	private int healthWidth = healthBarWidth;
	// Power bar
	private int powerBarWidth = (int)(104 * Game.SCALE);
	private int powerBarHeight = (int)(2 * Game.SCALE);
	private int powerBarXStart = (int)(44 * Game.SCALE);
	private int powerBarYStart = (int)(34 * Game.SCALE);
	private int powerWidth = powerBarWidth;
	private int powerMaxValue = 200;
	private int powerValue = powerMaxValue;
	
	private int flipX = 0;
	private int flipW = 1;
	
	private boolean attackChecked;
	private Playing playing;
	
	private int tileY = 0;
	
	private boolean powerAttackActive; // check if it's active or not. 
	private int powerAttackTick; // increases once every update as long as the attack is active. Once the power attack tick reaches a limit, we are going to stop doing the attack. 
	private int powerGrowSpeed = 15; // to increase power bar over time. 
	private int powerGrowTick;
	
	
	public Player(float x, float y, int width, int height, Playing playing) { // Constructor

		super(x, y, width, height); // pass x, y over to the superclass Entity.
		
		this.playing = playing;
		this.state = IDLE;
		this.maxHealth = 100;
		this.currentHealth = maxHealth;
		this.walkSpeed = Game.SCALE * 1.0f;
		
		loadAnimations();
		initHitbox(20, 27); // The character is about 20 pixels in width and 27 in height
		initAttackBox();
		
	}
	
	public void setSpawn(Point spawn) {
		
		this.x = spawn.x;
		this.y = spawn.y;
		hitbox.x = x;
		hitbox.y = y;
		
	}

	private void initAttackBox() {
		
		attackBox = new Rectangle2D.Float(x, y, (int)(20 * Game.SCALE), (int)(20 * Game.SCALE));
		resetAttackBox();
		
	}

	public void update() {
		
		updateHealthBar();
		updatePowerBar();
		
		if (currentHealth <= 0) {
			
			if (state != DEAD) {
				state = DEAD;
				aniTick = 0;
				aniIndex = 0;
				playing.setPlayerDying(true);
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
			} else if(aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED - 1) { // check for last sprite and last animation tick
				playing.setGameOver(true);
				playing.getGame().getAudioPlayer().stopSong();
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAMEOVER);
			} else 
				updateAnimationTick();
		
			return;
		}
		
		updateAttackBox();
		
		updatePos();
		
		if (moving) {
			
			checkPotionTouched();
			checkSpikesTouched();
			tileY = (int)(hitbox.y / Game.TILES_SIZE);
			if (powerAttackActive) {
				powerAttackTick++;
				if (powerAttackTick >= 35) {
					powerAttackTick = 0;
					powerAttackActive = false;
				}
			}
		}
		
		if (attacking || powerAttackActive)
			checkAttack();
		
		updateAnimationTick();
		setAnimation();

	}

	private void checkSpikesTouched() {
		
		playing.checkSpikesTouched(this);
		
	}

	private void checkPotionTouched() {
		
		playing.checkPotionTouched(hitbox);
		
	}

	private void checkAttack() {
		
		if (attackChecked || aniIndex != 1)
			return;
		attackChecked = true;
		
		if (powerAttackActive)
			attackChecked = false;
		
		playing.checkEnemyHit(attackBox);
		playing.checkObjecthit(attackBox);
		playing.getGame().getAudioPlayer().playAttackSound();
		
	}

	private void updateAttackBox() {
		if (right && left) {
			if (flipW == 1) {
				attackBox.x = hitbox.x + hitbox.width + (int)(Game.SCALE * 10);
			} else {
				attackBox.x = hitbox.x - hitbox.width - (int)(Game.SCALE * 10);
			}
		} else if (right || (powerAttackActive && flipW == 1)) { //   hitbox.x - for correlation. Where the player goes, attack box follows. 
			   		attackBox.x = hitbox.x + hitbox.width + (int)(Game.SCALE * 10);
				} else if (left || (powerAttackActive && flipW == -1)) {
					attackBox.x = hitbox.x - hitbox.width - (int)(Game.SCALE * 10);
				}
		attackBox.y = hitbox.y + (Game.SCALE * 10);
	}

	private void updateHealthBar() {
		
		healthWidth = (int)((currentHealth / (float)maxHealth) * healthBarWidth); // For example, (50/100) * 150px = 75. But, we must have maxHealth as a float. Otherwise, 50/100 will be an integer. 
		
	}
	
	private void updatePowerBar() {
		
		powerWidth = (int)((powerValue / (float)powerMaxValue) * powerBarWidth);
		
		powerGrowTick++;
		if (powerGrowTick >= powerGrowSpeed) {
			powerGrowTick = 0;
			changePower(1);
		}
		
	}

	public void render(Graphics g, int LvlOffset) {

		g.drawImage(animations[state][aniIndex], (int)(hitbox.x - xDrawOffset) - LvlOffset + flipX, (int)(hitbox.y - yDrawOffset), width * flipW, height, null); // img.getSubimage - used to get a specific part of the image.
//		drawHitbox(g);
//		drawAttackBox(g, LvlOffset);
		drawUI(g);
	
	}

	private void drawUI(Graphics g) {
		// Background UI
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
		
		// Health UI
		g.setColor(Color.RED);
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
		
		// Power UI
		g.setColor(Color.YELLOW);
		g.fillRect(powerBarXStart + statusBarX, powerBarYStart + statusBarY, powerWidth, powerBarHeight);
		
	}

	private void updateAnimationTick() { // loop through an array with sprites.

		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(state)) {
				aniIndex = 0;
				attacking = false;
				attackChecked = false;
			}
		}

	}

	private void setAnimation() { // to determine what type of animation we will have.

		int startAni = state;
		
		if (moving)
			state = RUNNING;
		else
			state = IDLE;
		
		if (inAir) {
			if (airSpeed < 0) { // going up
				state = JUMP;
			} else {
				state = FALLING;
			}
		}
		
		if (powerAttackActive) {
			state = ATTACK;
			aniIndex = 1;
			aniTick = 0;
			return;
		}
		
		if (attacking) {
			state = ATTACK;
			if (startAni != ATTACK) {
				aniIndex = 1;
				aniTick = 0;
				return;
			}
		}
		
		if (startAni != state) {  // looking for changes. if there is a change, then we should reset ani tick and ani index. 
			resetAniTick();
		}
		
	}

	private void resetAniTick() {
		
		aniTick = 0;
		aniIndex = 0;
		
	}

	private void updatePos() { // method to increase/decrease x and y deltas.

		moving = false;
		
		if (jump)
			jump();
		
//		if (!left && !right && !inAir) // if we are not holding down any key, then we shoudn't be using the method. 
//			return;
		if (!inAir)
			if (!powerAttackActive)
				if((!left && !right) || (left && right))
					return;
		
		float xSpeed = 0; // temporary storage

		if (left && !right) { // moving left 
			xSpeed -= walkSpeed;
			flipX = width;
			flipW = -1;
		} 
		if (right && !left) { // moving right 
			xSpeed += walkSpeed;
			flipX = 0;
			flipW = 1;
		} 
		
		if (powerAttackActive) {
			if ((!left && !right) || (left && right)) {
				if (flipW == -1)
					xSpeed = -walkSpeed;
				else 
					xSpeed = walkSpeed;
			}
			
			xSpeed *=3;
		}
		
		if (!inAir) { // meaning we are running left or right. In here, we check whether or not we are on a floor. 
			
			if (!IsEntityOnFloor(hitbox, lvlData)) {
				inAir = true;
			}
		}
		
		if (inAir && !powerAttackActive) { // if in the air, we will have to check both x and y directions for collisions. 
			
			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) { // only checking if we can move up or down. 
				// inside this if, we can move up or down. 
				hitbox.y += airSpeed;
				airSpeed += GRAVITY; 
				updateXPos(xSpeed); // checking if we can move left or right. 
			} else { // if we cannot move up or down (meaning we are hitting the roof or the floor).  
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0) // we are going down and hit something. 
					resetInAir();
				else // we hit the roof 
					airSpeed = fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}
			
		} else { // if not, only check the x direction. 
			updateXPos(xSpeed);
		}
		
		moving = true; // if we are not moving left, right, or in air - the first if check will remove us from this method. But if we stay, it means moving is true. 
	}

	private void jump() {
		
		if (inAir) // if we are in the air, we cannot jump again. 
			return;
		playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
		inAir = true;
		airSpeed = jumpSpeed;
		
	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
	}

	private void updateXPos(float xSpeed) {
		
		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
			hitbox.x += xSpeed;
		} else { // if we can't move to the position (colliding with something), there's still space between the player and, for instance, a wall. But, we want the hitbox to be next to the wall, without the space. 
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
			if (powerAttackActive) { // as soon as we hit something (for example, a wall) while in power attack, we must stop. 
				powerAttackActive = false;
				powerAttackTick = 0;
			}
		} 
		
	}
	
	public void changeHealth(int value) {
		
		currentHealth += value;
		
		if (currentHealth <= 0) {
			
			currentHealth = 0;
			// gameOver();
			
		} else if (currentHealth >= maxHealth)
			currentHealth = maxHealth;
		
	}
	
	public void kill() {
		
		currentHealth = 0;
		
	}
	
	public void changePower(int value) {
		
		powerValue += value;
		if (powerValue >= powerMaxValue)
			powerValue = powerMaxValue;
		else if (powerValue <= 0)
			powerValue = 0;
		
	}

	private void loadAnimations() {
		
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

			animations = new BufferedImage[7][8];

			for (int j = 0; j < animations.length; j++) {
				for (int i = 0; i < animations[j].length; i++) {
					animations[j][i] = img.getSubimage(i * 64, j * 40, 64, 40);
				}
			}
			
		statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);

	}
	
	public void loadLvlData (int[][] lvlData) {
		
		this.lvlData = lvlData;
		if (!IsEntityOnFloor(hitbox, lvlData)) {
			inAir = true;
		}
		
	}
	
	public void resetDirBooleans() {
		
		left = false; 
		right = false; 

	}
	
	public void setAttacking(boolean attacking) {
		
		this.attacking = attacking;
		
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}
	
	public void setJump(boolean jump) {
		this.jump = jump;
	}
	
	public int getTileY() {
		return tileY;
	}

	public void resetAll() {
		
		resetDirBooleans();
		inAir = false;
		attacking = false;
		moving = false;
		airSpeed = 0f;
		state = IDLE;
		currentHealth = maxHealth;
		
		hitbox.x = x;
		hitbox.y = y;
		
		resetAttackBox();
		
		if (!IsEntityOnFloor(hitbox, lvlData)) {
			inAir = true;
		}
		
	}
	
	private void resetAttackBox() {
		if (flipW == 1) {
			attackBox.x = hitbox.x + hitbox.width + (int)(Game.SCALE * 10);
		} else {
			attackBox.x = hitbox.x - hitbox.width - (int)(Game.SCALE * 10);
		}
	}

	public void powerAttack() {
		
		if (powerAttackActive)
			return;
		
		if (powerValue >= 60) {
			powerAttackActive = true;
			changePower(-60);
		}
		
	}

}
