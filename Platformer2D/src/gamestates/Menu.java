package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import main.Game;
import ui.MenuButton;
import utilz.LoadSave;

public class Menu extends State implements Statemethods{
	
	private MenuButton[] buttons = new MenuButton[3];
	private BufferedImage backgroundImg, backgroundImgPink;
	private int menuX, menuY, menuWidth, menuHeight;

	public Menu(Game game) {
		
		super(game);
		loadButtons();
		loadBackground();
		
		backgroundImgPink = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMAGE);
		
	}

	private void loadBackground() {
		
		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
		menuWidth = (int)(backgroundImg.getWidth() * Game.SCALE); 
		menuHeight = (int)(backgroundImg.getHeight() * Game.SCALE);
		menuX = Game.GAME_WIDTH / 2 - menuWidth / 2; // (in a center) - (half the width)
		menuY = (int)(45 * Game.SCALE);
		
	}

	private void loadButtons() {
		
		buttons[0] = new MenuButton(Game.GAME_WIDTH / 2, (int)(150 * Game.SCALE), 0, Gamestate.PLAYING); // we want to access the game, thus Gamestate.PLAYING. 
		buttons[1] = new MenuButton(Game.GAME_WIDTH / 2, (int)(220 * Game.SCALE), 1, Gamestate.OPTIONS);
		buttons[2] = new MenuButton(Game.GAME_WIDTH / 2, (int)(290 * Game.SCALE), 2, Gamestate.QUIT);
		
	}

	@Override
	public void update() {
		
		for (MenuButton mb : buttons) {
			mb.update();
		}
		
	}

	@Override
	public void draw(Graphics g) {
		
		g.drawImage(backgroundImgPink, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		
		g.drawImage(backgroundImg, menuX, menuY, menuWidth, menuHeight, null);
		
		
		for (MenuButton mb : buttons) {
			mb.draw(g);
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		for (MenuButton mb : buttons) { // we want to check if we are pressing inside the button. If we are, we want to set this button to MousePressed is equal to true. 
			if (isIn(e, mb)) {
				mb.setMousePressed(true);
				break;
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) { // check if we are releasing our mouse, are we inside a button? then we want to execute whatever the button says. 
		
		for (MenuButton mb : buttons) {
			if (isIn(e, mb)) { // if we are inside the button... 
				if (mb.isMousePressed()) { // we got to make sure we actually pressed it. Did we press it? (If we didn't press the button, for instance we pressed outside and then dragged our mouse over the button and then released, it means we didn't press the button). 
					mb.applyGamestate(); // if true, we change the game state. 
				if (mb.getState() == Gamestate.PLAYING)
					game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getLevelIndex());
					break;
				}
			}
		}
		resetButtons(); // just in case if there is still a button that is not reset. 
	}

	private void resetButtons() {
		
		for (MenuButton mb : buttons)
			mb.resetBools();
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		for (MenuButton mb : buttons)
			mb.setMouseOver(false); // to make sure that between each update we are resetting the mouseOver boolean because we might move away from one button. 
		
		for (MenuButton mb : buttons) // whether or not we are hovering over any type of button 
			if (isIn(e, mb)) {
				mb.setMouseOver(true);
				break;
			}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			Gamestate.state = Gamestate.PLAYING;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
}
