// only static methods, since we don't need to create an object of these class in order to access the methods. 

// How to build a level? we have three options:
// 1. build an editor tab/program, which is the best option, but it takes too long. 
// 2. use int2D array, where each index of the array corresponds to a specific tile. Each index is a position in a level. The value of the index is spriteID. It's simple to do and fast to implement, but really hard to edit.  
// 3. paint the level in the image file. Image file, where each pixel is a position on the level (each pixel is a tile in the game). We get the type of tile depending on the colors of that pixel. 
// Each pixel has three colors: (Red, Green, Blue). It can range from 0 to 255. The color value = spriteID. 
// We will use the third method. Red color for spriteID. Green for enemies. Blue (maybe) for objects. 

package utilz;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import enteties.Crabby;
import main.Game;
import static utilz.Constants.EnemyConstants.CRABBY;

public class LoadSave {
	
	public static final String PLAYER_ATLAS = "updated_player_sprites.png";
	public static final String LEVEL_ATLAS = "outside_sprites.png";
	public static final String MENU_BUTTONS = "button_atlas.png";
	public static final String MENU_BACKGROUND = "menu_background.png";
	public static final String PAUSE_BACKGROUND = "pause_menu.png";
	public static final String SOUND_BUTTONS = "sound_button.png";
	public static final String URM_BUTTONS = "urm_buttons.png"; // unpause - replay - menu =  urm_buttons. 
	public static final String VOLUME_BUTTONS = "volume_buttons.png";
	public static final String MENU_BACKGROUND_IMAGE = "background_menu.png";
	public static final String PLAYING_BG_IMG = "playing_bg_img.png";
	public static final String BIG_CLOUDS = "big_clouds.png";
	public static final String SMALL_CLOUDS = "small_clouds.png";
	public static final String CRABBY_SPRITE = "crabby_sprite.png";
	public static final String STATUS_BAR = "health_power_bar.png";
	public static final String COMPLETED_IMG = "completed_sprite.png";
	public static final String POTION_ATLAS = "potions_sprites.png";
	public static final String CONTAINER_ATLAS = "objects_sprites.png";
	public static final String TRAP_ATLAS = "trap_atlas.png";
	public static final String CANNON_ATLAS = "cannon_atlas.png";
	public static final String CANNON_BALL = "ball.png";
	public static final String DEATH_SCREEN = "death_screen.png";
	public static final String OPTIONS_MENU = "options_background.png";
	
	
	public static BufferedImage GetSpriteAtlas(String fileName) {
		
		BufferedImage img = null;
		
		InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);

		try {
			img = ImageIO.read(is);


		} catch (IOException e) {
			e.printStackTrace();
		} finally { // called no matter what. we need to close the input stream (to free up
					// resources and avoid problems). we use try and catch for that as well just in
					// case if input stream fails.
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return img;
		
	}
	
	public static BufferedImage[] GetAllLevels() {
		
		// create the path to lvls folder
		URL url = LoadSave.class.getResource("/lvls");
		// access the folder and look what's inside 
		File file = null;
		
		try {
			file = new File(url.toURI()); // URL - location, URI - identifier for the resource.
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// now we have the folder where every resource is inside. 
		
		File[] files = file.listFiles(); // the folder will go over all the files inside it and list them inside the "files"
		
//		for(File f: files)
//			System.out.println("file: " + f.getName()); // check if we can see all the levels. 
		// we can see them, but they are unsorted. Will use the simple way to sort them since we don't have much points in the array. 
		
		File[] filesSorted = new File[files.length];
		
		for (int i = 0; i < filesSorted.length; i++)
			for (int j = 0; j < files.length; j++) {
				if(files[j].getName().equals("" + (i + 1) + ".png"))
					filesSorted[i] = files[j];
			}
		
//		// checking. 
//		for(File f: filesSorted)
//			System.out.println("file: " + f.getName()); 
		
		 BufferedImage[] imgs = new BufferedImage[filesSorted.length];
		 
		 for (int i = 0; i < imgs.length; i++)
			try {
				imgs[i] = ImageIO.read(filesSorted[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return imgs;
		
	}
	                    	
}
