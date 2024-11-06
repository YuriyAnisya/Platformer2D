package ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import utilz.LoadSave;
import static utilz.Constants.UI.Buttons.*;

public class MenuButton {
	
	private int xPos, yPos, rowIndex, index; // index is set depending on what type of mouse event we are having. 
	private int xOffsetCenter = BUTTON_WIDTH / 2;
	private Gamestate state;
	private BufferedImage[] imgs; 
	private boolean mouseOver, mousePressed;
	private Rectangle bounds; // hitbox of the button. 
	
	public MenuButton (int xPos, int yPos, int rowIndex, Gamestate state) { // x and y where we will draw the button. rowIndex just to choose the correct button in a sprite (we have three rows). Game state is needed because we want to change the state when we press the button. 
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.rowIndex = rowIndex;
		this.state = state;
		
		loadImgs();
		initBounds();
		
	}

	private void initBounds() {
		
		bounds = new Rectangle(xPos - xOffsetCenter, yPos, BUTTON_WIDTH, BUTTON_HEIGHT);
		
	}

	private void loadImgs() {
		
		imgs = new BufferedImage[3];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.MENU_BUTTONS);// button atlas stored in temporary buffered image. 
		
		for (int i = 0; i < imgs.length; i++) {
			imgs[i] = temp.getSubimage(i * BUTTON_WIDTH_DEFAULT, rowIndex * BUTTON_HEIGHT_DEFAULT, BUTTON_WIDTH_DEFAULT, BUTTON_HEIGHT_DEFAULT);
		}
		
	}
	
	public void draw(Graphics g) {
		
		g.drawImage(imgs[index], xPos - xOffsetCenter, yPos, BUTTON_WIDTH, BUTTON_HEIGHT, null);
		
	}
	
	public void update() { // update the button depending on what type of mouse event we're having. 
		
		index = 0;
		
		if (mouseOver)
			index = 1;
		if (mousePressed)
			index = 2;
		
	}

	public boolean isMouseOver() {
		return mouseOver;
	}

	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}

	public boolean isMousePressed() {
		return mousePressed;
	}

	public void setMousePressed(boolean mousePressed) {
		this.mousePressed = mousePressed;
	}
	
	// return the hitbox of the button in order to understand if we are in the button. 
	public Rectangle getBounds() {
		return bounds;
	}
	
	public void applyGamestate() {
		Gamestate.state = state;
	}
	
	public void resetBools () {
		mouseOver = false;
		mousePressed = false;
	}
	
	public Gamestate getState() {
		return state;
	}

}
