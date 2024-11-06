package objects;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import enteties.Player;
import gamestates.Playing;
import levels.Level;
import main.Game;
import utilz.LoadSave;
import static utilz.Constants.ObjectConstants.*;
import static utilz.HelpMethods.CanCannonSeePlayer;
import static utilz.HelpMethods.IsProjectileHittingLevel;
import static utilz.Constants.Projectiles.*;

public class ObjectManager {
	
	private Playing playing;
	private BufferedImage[][] potionImgs, containerImgs;
	private BufferedImage[] cannonImg;
	private BufferedImage spikeImg, cannonBallImg;
	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> containers;
	private ArrayList<Spike> spikes;
	private ArrayList<Cannon> cannons;
	private ArrayList<Projectile> projectiles = new ArrayList<>();
	
	public ObjectManager(Playing playing) {
		
		this.playing = playing;
		
		loadImgs();
		
	}
	
	public void checkSpikesTouched(Player p) {
		
		for (Spike s : spikes)
			if (s.getHitbox().intersects(p.getHitbox()))
				p.kill(); // kill the player if he touches the spikes
				
	}
	
	public void checkObjectTouched(Rectangle2D.Float hitbox) { // intersection of the player's hitbox with potion hitbox. 
		
		for (Potion p : potions)
			if (p.isActive()) {
				if (hitbox.intersects(p.getHitbox())) { // player hitbox intersects with potion hitbox
					p.setActive(false);
					applyEffectToPlayer(p);
				}
			}
		
	}
	
	public void applyEffectToPlayer(Potion p) {
		
		if (p.getObjType() == RED_POTION)
			playing.getPlayer().changeHealth(RED_POTION_VALUE);
		else 
			playing.getPlayer().changePower(BLUE_POTION_VALUE);
		
	}
	
	public void checkObjectHit(Rectangle2D.Float attackbox) { // if the object (barrel or box) gets hit. 
		
		for (GameContainer gc : containers)
			if (gc.isActive() && !gc.doAnimation) {
				if (gc.getHitbox().intersects(attackbox)) { // if player's attack box hits the container
					gc.setAnimation(true);
					
					int type = 0;
					if (gc.getObjType() == BARREL)
						type = 1;
					
					potions.add(new Potion((int)(gc.getHitbox().x + gc.getHitbox().width / 2), (int)(gc.getHitbox().y - gc.getHitbox().height / 2), type));
					return;
					
				}
			}
		
	}
	
	public void loadObjects(Level newLevel) {
		
		potions = new ArrayList<>(newLevel.getPotions());
		containers = new ArrayList<>(newLevel.getContainers());
		spikes = newLevel.getSpikes();
		cannons = newLevel.getCannons();
		projectiles.clear(); // every time we load a new level or load a start level, we want to clear the current array list. 
		
	}

	private void loadImgs() {
		
		BufferedImage potionSprites = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
		potionImgs = new BufferedImage[2][7];
		
		for (int j = 0; j < potionImgs.length; j++)
			for (int i = 0; i < potionImgs[j].length; i++) {
				potionImgs[j][i] = potionSprites.getSubimage(12 * i, 16 * j, 12, 16); 
			}
		
		
		BufferedImage containerSprites = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
		containerImgs = new BufferedImage[2][8];
		
		for (int j = 0; j < containerImgs.length; j++)
			for (int i = 0; i < containerImgs[j].length; i++) {
				containerImgs[j][i] = containerSprites.getSubimage(40 * i, 30 * j, 40, 30); 
			}
		
		
		spikeImg = LoadSave.GetSpriteAtlas(LoadSave.TRAP_ATLAS);
		
		
		cannonImg = new BufferedImage[7];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CANNON_ATLAS);
		
		for (int i = 0; i < cannonImg.length; i++)
			cannonImg[i] = temp.getSubimage(i * 40, 0, 40, 26);
		
		
		cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL);
		
	}

	public void update(int[][] lvlData, Player player) {
		
		for (Potion p : potions)
			if (p.isActive())
				p.update();
		
		for (GameContainer gc : containers)
			if (gc.isActive())
				gc.update();
		
		updateCannons(lvlData, player);
		updateProjectiles(lvlData, player);
		
	}
	
	private void updateProjectiles(int[][] lvlData, Player player) {
		
		for (Projectile p : projectiles)
			if (p.isActive()) {
				
				p.updatePos();
				
				if(p.getHitbox().intersects(player.getHitbox())) {
					player.changeHealth(-25);
					p.setActive(false);
				} else if (IsProjectileHittingLevel(p, lvlData)) {
					p.setActive(false);
				}
					
			}
	}

	private boolean isPlayerInRange(Cannon c, Player player) {
		
		int absValue = (int) Math.abs(player.getHitbox().x - c.getHitbox().x); 
		
		return absValue <= Game.TILES_SIZE * 5;
	}
	
	private boolean isPlayerInfrontOfCannon(Cannon c, Player player) {
		
		if (c.getObjType() == CANNON_LEFT) {
			if (c.getHitbox().x > player.getHitbox().x) // meaning that the player is on a left side of the hitbox. 
				return true;
		} else if (c.getHitbox().x < player.getHitbox().x)
			return true;
		
		return false;
	}
	
	private void updateCannons(int[][] lvlData, Player player) {
		
		for (Cannon c : cannons) {
			if (!c.doAnimation)
				if (c.getTileY() == player.getTileY())
					if (isPlayerInRange(c, player))
						if (isPlayerInfrontOfCannon(c, player))
							if (CanCannonSeePlayer(lvlData, player.getHitbox(), c.getHitbox(), c.getTileY())) 
								c.setAnimation(true);
								
			c.update();
			if (c.getAniIndex() == 4 && c.getAniTick() == 0)
				shootCannon(c);
			
		}
		
		/*
		 * If the cannon is not animating:
		 * First, we are checking if they are on a same tile Y. 
		 * If that's the case, then we check if Player is in range. 
		 * If that's the case, then we check is player in front of cannon.
		 * If that's the case, then we check line of sight or loss. 
		 * If all of that is true, then - shoot the cannon. 
		 */
		
	}
	
	private void shootCannon(Cannon c) {
		
		int dir = 1;
		if (c.getObjType() == CANNON_LEFT)
			dir = -1;
		projectiles.add(new Projectile((int)(c.getHitbox().x), (int)(c.getHitbox().y), dir));
		
	}

	public void draw(Graphics g, int xLvlOffset) {
		
		drawPotions(g, xLvlOffset);
		drawContainers(g, xLvlOffset);
		drawTraps(g, xLvlOffset);
		drawCannons(g, xLvlOffset);
		drawProjectiles(g, xLvlOffset);
		
	}

	private void drawProjectiles(Graphics g, int xLvlOffset) {
		
		for (Projectile p : projectiles)
			if(p.isActive())
				g.drawImage(cannonBallImg, (int)(p.getHitbox().x - xLvlOffset), (int)(p.getHitbox().y), CANNON_BALL_WIDTH, CANNON_BALL_HEIGHT, null);
		
	}

	private void drawCannons(Graphics g, int xLvlOffset) {
		
		for (Cannon c : cannons) {
			
			int x = (int)(c.getHitbox().x - xLvlOffset);
			int width = CANNON_WIDTH;
			
			if (c.getObjType() == CANNON_RIGHT) {
				x += width;
				width *= -1;
			}
				
			g.drawImage(cannonImg[c.getAniIndex()], x, (int)(c.getHitbox().y), width, CANNON_HEIGHT, null);
			
		}
	}

	private void drawTraps(Graphics g, int xLvlOffset) {
		
		for (Spike s: spikes)
			g.drawImage(spikeImg, (int)(s.getHitbox().x - xLvlOffset), (int)(s.getHitbox().y - s.getyDrawOffset()), SPIKE_WIDTH, SPIKE_HEIGHT, null);
		
	}

	private void drawContainers(Graphics g, int xLvlOffset) {
		
		for (GameContainer gc : containers)
			if (gc.isActive()) {
				
				int type = 0;
				if (gc.getObjType() == BARREL) {
					type = 1;
				} 
				
				g.drawImage(containerImgs[type][gc.getAniIndex()], (int)(gc.getHitbox().x - gc.getxDrawOffset() - xLvlOffset), (int)(gc.getHitbox().y - gc.getyDrawOffset()), CONTAINER_WIDTH, CONTAINER_HEIGHT, null);
				
			}
		
	}

	private void drawPotions(Graphics g, int xLvlOffset) {
		
		for (Potion p : potions)
			if (p.isActive()) {
				
				int type = 0;
				if (p.getObjType() == RED_POTION) {
					type = 1;
				} 
				
				g.drawImage(potionImgs[type][p.getAniIndex()], (int)(p.getHitbox().x - p.getxDrawOffset() - xLvlOffset), (int)(p.getHitbox().y - p.getyDrawOffset()), POTION_WIDTH, POTION_HEIGHT, null);
				
			}
	}
	
	public void resetAllObjects() {
		
		loadObjects(playing.getLevelManager().getCurrentLevel());
		
		for (Potion p : potions)
			p.reset();
		
		for (GameContainer gc : containers)
			gc.reset();
		
		for (Cannon c : cannons)
			c.reset();
		
	}
	
}
