// Store data of the level

// How to build a level? we have three options:
// 1. build an editor tab/program, which is the best option, but it takes too long. 
// 2. use int2D array, where each index of the array corresponds to a specific tile. Each index is a position in a level. The value of the index is spriteID. It's simple to do and fast to implement, but really hard to edit.  
// 3. paint the level in the image file. Image file, where each pixel is a position on the level (each pixel is a tile in the game). We get the type of tile depending on the colors of that pixel. 
// Each pixel has three colors: (Red, Green, Blue). It can range from 0 to 255. The color value = spriteID. 
// We will use the third method. Red color for spriteID. Green (maybe) for enemies. Blue (maybe) for objects. 


// How to make a level bigger? when the character moves and level moves with it? 
// The level will move if the player is close enough to the edge of the level. 
// When the player is far enough to the right (or to the left), we have to set "borders". 
// Let's say the borders are at 20% (20 pixels) (20% of game width, 20x) and 80% (80 pixels) (80% of game width, 80x). When the player passes the border, any pixel beyond the border line will be the amount of pixels we need to move the level to the left or right and also the player. 
// Let's say the player is past the border at 85 pixels, meaning 5 pixels over the border line, those 5 pixels will be added to an offset variable and we will have to move everything in the game world by 5 pixels. 
// We will keep the position for everything as is, but we will change how we draw it. We will add an extra variable to everything in the draw call that takes in the offset, so the player still have a position that is 85. 
// But when we draw it, we go 85 - offset, so it looks like the player is at 80. 


package levels;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import enteties.Crabby;
import main.Game;
import objects.Cannon;
import objects.GameContainer;
import objects.Potion;
import objects.Spike;
import utilz.HelpMethods;

import static utilz.HelpMethods.GetLevelData;
import static utilz.HelpMethods.GetCrabs;
import static utilz.HelpMethods.GetPlayerSpawn;

public class Level {
	
	private BufferedImage img;
	private int[][] lvlData; // storing the level data. 
	private ArrayList<Crabby> crabs;
	private ArrayList<Potion> potions;
	private ArrayList<Spike> spikes;
	private ArrayList<GameContainer> containers;
	private ArrayList<Cannon> cannons;
	// store and calculate the levelOffset for each level in this level class
	private int lvlTilesWide; 
	private int maxTilesOffset; 
	private int maxLvlOffsetX;
	private Point playerSpawn;
	
	public Level (BufferedImage img) {
		
		this.img = img;
		createLevelData();
		createEnemies();
		createPotions();
		createContainers();
		createCannons();
		createSpikes();
		calcLvlOffset();
		calcPlayerSpawn();
		
	}
	
	private void createCannons() {
		
		cannons = HelpMethods.GetCannons(img);
		
	}

	private void createSpikes() {
		
		spikes = HelpMethods.GetSpikes(img);
		
	}

	private void createContainers() {
		
		containers = HelpMethods.GetContainers(img);
		
	}

	private void createPotions() {
		
		potions = HelpMethods.GetPotions(img);
		
	}

	private void calcPlayerSpawn() {
		
		playerSpawn = GetPlayerSpawn(img);
		
	}

	private void calcLvlOffset() {
		
		lvlTilesWide = img.getWidth();
		maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
		maxLvlOffsetX = maxTilesOffset * Game.TILES_SIZE;
		
	}

	private void createEnemies() {
		
		crabs = GetCrabs(img);
		
	}

	private void createLevelData() {
		
		lvlData = GetLevelData(img);
		
	}

	public int getSpriteIndex (int x, int y) {// to get the specific index for the sprite array
		return lvlData[y][x];
	}
	
	public int[][] getLevelData() {
		
		return lvlData;
		
	}
	
	public int getLvlOffset() {
		
		return maxLvlOffsetX;
		
	}
	
	public ArrayList<Crabby> getCrabs() {
		
		return crabs;
		
	}
	
	public Point getPlayerSpawn() {
		
		return playerSpawn;
		
	}
	
	public ArrayList<Potion> getPotions() {
		return potions;
	}
	
	public ArrayList<GameContainer> getContainers() {
		return containers;
	}
	
	public ArrayList<Spike> getSpikes() {
		return spikes;
	}
	
	public ArrayList<Cannon> getCannons() {
		return cannons;
	}

}
