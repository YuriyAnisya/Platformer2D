package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Gamestate;
import main.Game;
import utilz.LoadSave;

public class LevelManager {
	
	private Game game;
	private BufferedImage[] levelSprite;
	private ArrayList<Level> levels;
	private int lvlIndex = 0;
	
	public LevelManager (Game game) {
		
		this.game = game;
		importOutsideSprites();
		levels = new ArrayList<>();
		buildAllLevels();

	}
	
	public void loadNextLevel() {
		
		lvlIndex ++;
		if (lvlIndex >= levels.size()) { // when we complete all levels, we start over. I can add gameCompletedOverlay, but later. 
			lvlIndex = 0;
			System.out.println("NO MORE LEVELS! GAME COMPLETED");
			Gamestate.state = Gamestate.MENU;
		}
		
		Level newLevel = levels.get(lvlIndex);
		game.getPlaying().getEnemyManager().loadEnemies(newLevel);
		game.getPlaying().getPlayer().loadLvlData(newLevel.getLevelData());
		game.getPlaying().setMaxLvlOffset(newLevel.getLvlOffset());
		game.getPlaying().getObjectManager().loadObjects(newLevel);
		
	}
	
	private void buildAllLevels() {
		
		BufferedImage[] allLevels = LoadSave.GetAllLevels();
		
		for (BufferedImage img : allLevels)
			levels.add(new Level(img));
		
	}

	private void importOutsideSprites() {
		
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
		levelSprite = new BufferedImage[48]; // 4 sprite in height, 12 in width. 4*12 = 48. 
		for (int j = 0; j < 4; j++) { // <4 because only 4 in height, so 4 rows. 
			for (int i = 0; i < 12; i++) {
				int index = j * 12 + i;
				levelSprite[index] = img.getSubimage(i * 32, j * 32, 32, 32);
			}
		}
		
	}

	public void draw (Graphics g, int LvlOffset) {
		
		for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
			for (int i = 0; i < levels.get(lvlIndex).getLevelData()[0].length; i++) {
				int index = levels.get(lvlIndex).getSpriteIndex(i, j);
				g.drawImage(levelSprite[index], i * Game.TILES_SIZE - LvlOffset, j * Game.TILES_SIZE, Game.TILES_SIZE, Game.TILES_SIZE, null);
			}
		}
	}
	
	public void update () {
		
		
		
	}
	
	public Level getCurrentLevel() {
		
		return levels.get(lvlIndex);
		
	}
	
	public int getAmountOfLevels() {
		
		return levels.size();
		
	}
	
	public int getLevelIndex() {
		return lvlIndex;
	}

}
