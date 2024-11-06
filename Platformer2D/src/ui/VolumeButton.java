package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import utilz.LoadSave;
import static utilz.Constants.UI.VolumeButtons.*;


public class VolumeButton extends PauseButton{
	
	private BufferedImage[] imgs;
	private BufferedImage slider;
	private int index = 0;
	private boolean mouseOver, mousePressed;
	private int buttonX; // the actual button for slider. 
	private int minX, maxX; // min x for the button & max x the button can have
	private float floatValue = 0f;

	public VolumeButton(int x, int y, int width, int height) {
		
		// giving values for the button 
		super(x + width / 2, y, VOLUME_WIDTH, height); // The hitbox is only for the button and it's going to start at the center of slider. x + width/2 put the button on the middle of the slider. 
		
		bounds.x -= VOLUME_WIDTH / 2;
		
		// giving a new buttonX
		buttonX = x + width / 2;
		// and then, we give the x and the width for the actual slider
		this.x = x; 
		this.width = width;
		
		minX = x + VOLUME_WIDTH / 2;
		maxX = x + width - VOLUME_WIDTH / 2;
		
		loadImgs();
		
	}
	
	private void loadImgs() {
		
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.VOLUME_BUTTONS);
		imgs = new BufferedImage[3];
		
		for (int i = 0; i < imgs.length; i++)
			imgs[i] = temp.getSubimage(i * VOLUME_DEFAULT_WIDTH, 0, VOLUME_DEFAULT_WIDTH, VOLUME_DEFAULT_HEIGHT);
		
		slider = temp.getSubimage(3 * VOLUME_DEFAULT_WIDTH, 0, SLIDER_DEFAULT_WIDTH, VOLUME_DEFAULT_HEIGHT); // 3 * VOLUME_DEFAULT_WIDTH because we have three volume buttons and then the slider. 
		
		
	}

	public void update() {
		
		index = 0;
		if (mouseOver)
			index = 1;
		if (mousePressed)
			index = 2;
		
	}
	
	public void draw(Graphics g) {
		
		g.drawImage(slider, x, y, width, height, null); 
		g.drawImage(imgs[index], buttonX - VOLUME_WIDTH / 2, y, VOLUME_WIDTH, height, null); // buttonX - VOLUME_WIDTH / 2 it makes our mouse to be at the center of the button, not on a left side. 
		
	}
	
	public void changeX(int x) {
		
		if(x < minX)
			buttonX = minX;
		else if (x > maxX)
			buttonX = maxX;
		else 
			buttonX = x; // if it's inside the bounds
		
		updateFloatValue();
		
		// make the hitbox follow the mouse. 
		bounds.x = buttonX - VOLUME_WIDTH / 2;
		
	}
	
	private void updateFloatValue() {
		
		float range = maxX - minX;
		float value = buttonX - minX;
		floatValue = value / range;
		
	}

	public void resetBools() {
		
		mouseOver = false;
		mousePressed = false;
		
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
	
	public float getFloatValue() {
		return floatValue;
	}

}
