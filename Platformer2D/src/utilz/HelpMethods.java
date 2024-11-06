package utilz;

import static utilz.Constants.EnemyConstants.CRABBY;
import static utilz.Constants.ObjectConstants.*;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import enteties.Crabby;
import main.Game;
import objects.Cannon;
import objects.GameContainer;
import objects.Potion;
import objects.Projectile;
import objects.Spike;

public class HelpMethods {
	
	public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
		// we are going to test the top left and the bottom right first. when we go diagonal, it's going to be one of these two that will be inside a tile (if there is a tile).  
		
		if (!IsSolid(x, y, lvlData)) // Checking TOP LEFT. We want it false, meaning we have not found a solid one yet. 
			if (!IsSolid(x + width, y + height, lvlData)) // Checking BOTTOM RIGHT
				if (!IsSolid(x + width, y, lvlData)) // Checking TOP RIGHT
					if (!IsSolid(x, y + height, lvlData)) // Checking BOTTOM LEFT
						return true;
				
		return false;
	}
	
	private static boolean IsSolid (float x, float y, int[][] lvlData) { // checking whether or not it is a tile & if the position is inside the game window. 
		
		// Before, we had: if (x < 0 || x >= Game.GAME_WIDTH). We were making sure that the player is inside the window. But, for player x will increase more than the Game.GAME_WIDTH, and we don't know how big the actual level is. We only know how big the visible is. Thus:
		int maxWidth = lvlData[0].length * Game.TILES_SIZE;
		if (x < 0 || x >= maxWidth) 
			return true;
		if (y < 0 || y >= Game.GAME_HEIGHT) 
			return true;
		
		// if none of those above fails (meaning we are inside the game window), we need to figure out where we are in the level data. 
		float xIndex = x / Game.TILES_SIZE;
		float yIndex = y / Game.TILES_SIZE;
		
		return IsTileSolid((int)xIndex, (int)yIndex, lvlData);
	}
	
	public static boolean IsProjectileHittingLevel(Projectile p, int[][] lvlData) {
		
		return IsSolid(p.getHitbox().x + p.getHitbox().width / 2, p.getHitbox().y + p.getHitbox().height / 2, lvlData);
		
	}
	
	private static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData) {
		
		int value = lvlData[yTile][xTile];
		// we will check if that value is a tile. 
		if (value >= 48 || value < 0 || value != 11) // 11 is a transparent tile, can't be solid. 
			return true;
		
		return false;
	}
	
	public static float GetEntityXPosNextToWall (Rectangle2D.Float hitbox, float xSpeed) {
		// we need to calculate what current tile we are on in the x position. It's going to be either colliding with the tile to the left (meaning current tile - 1), or to the right (meaning current tile + 1).
		int currentTile = (int)(hitbox.x / Game.TILES_SIZE);
		// first we check where the collision is, to the left or to the right. xSpeed can't be 0 because we would have no collision. 
		if (xSpeed > 0) { // to the right
			
			int tileXPos = currentTile * Game.TILES_SIZE; // position in pixels 
			// to place player next to the tile on the right, we need to know the offset between the size of the player and the size of the tile. 
			int xOffset = (int)(Game.TILES_SIZE - hitbox.width);
			
			// we return tileXPos (current position of the tile) + xOffset and give it to the player, then the right side of the hitbox will be next to the wall. We add -1 so that the edge of the hitbox won't be on tile.  
			return tileXPos + xOffset - 1;
			
		} else { // to the left
			return currentTile * Game.TILES_SIZE;
		}	
	}
	
	public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
		
		int currentTile = (int)(hitbox.y / Game.TILES_SIZE);
		if (airSpeed > 0) { // Falling - touching floor
			int tileYPos = currentTile * Game.TILES_SIZE;
			int yOffset = (int)(Game.TILES_SIZE - hitbox.height);
			return tileYPos + yOffset - 1;
			
		} else { // Jumping 
			return currentTile * Game.TILES_SIZE;
		}
	}

	public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
		// Check the pixel bellow bottom left and bottom right corners. If both of these are not solid, then we are in the air. 
		if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData)) { // bottom left
			if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData)) { // bottom right
				return false;
			}
		}
		
		return true;
	}
	
	/**
	* We just check the bottomleft of the enemy here +/- the xSpeed. We never check
	* bottom right in case the enemy is going to the right. It would be more
	* correct checking the bottomleft for left direction and bottomright for the
	* right direction. But it wont have big effect in the game. The enemy will
	* simply change direction sooner when there is an edge on the right side of the
	* enemy, when its going right.
	*/
	public static boolean isFloor (Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
		
		if (xSpeed > 0)
			return IsSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
		
		return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
		
	}
	
	public static boolean CanCannonSeePlayer(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile) {
		
		int firstXTile = (int)(firstHitbox.x / Game.TILES_SIZE);
		int secondXTile = (int)(secondHitbox.x / Game.TILES_SIZE);
		
		if (firstXTile > secondXTile) 
			return IsAllTilesClear(secondXTile, firstXTile, yTile, lvlData); 
		else 
			return IsAllTilesClear(firstXTile, secondXTile, yTile, lvlData);
		
	}
	
	public static boolean IsAllTilesClear(int xStart, int xEnd, int y,  int[][] lvlData) {
		
		for(int i = 0; i < xEnd - xStart; i++) 
			if (IsTileSolid(xStart + i, y, lvlData))
				return false;
		
		return true;
		
	}
	
	public static boolean IsAllTileWalkable(int xStart, int xEnd, int y,  int[][] lvlData) {
		
			if (IsAllTilesClear(xStart, xEnd, y, lvlData))
				for(int i = 0; i < xEnd - xStart; i++) {
					if (!IsTileSolid(xStart + i, y + 1, lvlData))
						return false;
			}
		
		return true;
	}
	
	public static boolean IsSightClear (int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile) {
		
		// What X tile the first and second hitbox are on? 
		int firstXTile = (int)(firstHitbox.x / Game.TILES_SIZE);
		int secondXTile = (int)(secondHitbox.x / Game.TILES_SIZE);
		
		// check for any type of obstacle in between the hitboxes. We will check the X tiles. 
		if (firstXTile > secondXTile) // check to make sure that the loop works correctly. If one tile is bigger than another, we might get errors since we will gave to decrease, not increase, and etc.
			return IsAllTileWalkable(secondXTile, firstXTile, yTile, lvlData); 
		else 
			return IsAllTileWalkable(firstXTile, secondXTile, yTile, lvlData); 

	}
	
	public static int[][] GetLevelData(BufferedImage img) { // the size of this array will match the size of our game window, in terms of tiles per width and height.
//		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA); // level image
		
		int[][] lvlData = new int[img.getHeight()][img.getWidth()];
		
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color (img.getRGB(i, j)); // we need to get the color. 
				int value = color.getRed();
				if (value >= 48) // in case we are more or equal than 48, we set it to zero since that index doesn't exist in sprite atlas. 
					value = 0;
				lvlData[j][i] = value; // Red color for spriteID. Whatever the value of red is, it's going to be the index (later on) for that sprite. 
			}
		}
		return lvlData;	
	}
	
	public static ArrayList<Crabby> GetCrabs(BufferedImage img) {
//		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
		
		ArrayList<Crabby> list = new ArrayList<>();
		
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color (img.getRGB(i, j)); // we need to get the color. 
				int value = color.getGreen();
				if (value == CRABBY)
					list.add(new Crabby(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
			}
		}
		return list;
	}
	
	public static Point GetPlayerSpawn(BufferedImage img) { // Point is a position with two integer values. 
		
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color (img.getRGB(i, j)); // we need to get the color. 
				int value = color.getGreen();
				if (value == 100)
					return new Point(i * Game.TILES_SIZE, j * Game.TILES_SIZE);
			}
		}
		
		return new Point(1 * Game.TILES_SIZE, 1 * Game.TILES_SIZE);
	}
	
	public static ArrayList<Potion> GetPotions(BufferedImage img) {
		
		ArrayList<Potion> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++)
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == RED_POTION || value == BLUE_POTION)
					list.add(new Potion(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
			}

		return list;
	}
	
	public static ArrayList<GameContainer> GetContainers(BufferedImage img) {
		
		ArrayList<GameContainer> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++)
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == BOX || value == BARREL)
					list.add(new GameContainer(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
			}

		return list;
	}

	public static ArrayList<Spike> GetSpikes(BufferedImage img) {
		
		ArrayList<Spike> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++)
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == SPIKE)
					list.add(new Spike(i * Game.TILES_SIZE, j * Game.TILES_SIZE, SPIKE));
			}

		return list;
		
	}
	
	public static ArrayList<Cannon> GetCannons(BufferedImage img) {
		
		ArrayList<Cannon> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++)
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == CANNON_LEFT || value == CANNON_RIGHT)
					list.add(new Cannon(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
			}

		return list;
		
	}
		
}
