// UPS vs FPS: UPS takes care of logic (playermove etc., it checks for updates and applies them), while FPS takes care of rendering of everything (the level, player, enemies, etc). FPS can be changed, while UPS must be the same on every PC. 

package main;

import java.awt.Graphics;

import audio.AudioPlayer;
import gamestates.GameOptions;
import gamestates.Gamestate;
import gamestates.Menu;
import gamestates.Playing;
import ui.AudioOptions;
import utilz.LoadSave;

public class Game implements Runnable {

	private GamePanel gamePanel;
	private Thread gameThread;
	private final int FPS_SET = 120; // set amount of frames per second. no more than that.
	private final int UPS_SET = 200;
	
	private Playing playing;
	private Menu menu;
	private GameOptions gameOptions;
	private AudioOptions audioOptions;
	private AudioPlayer audioPlayer;
	
	// When a variable is declared static in Java programming, it means that the variable belongs to the class itself rather than to any specific instance of the class. This means that there is only one copy of the variable in memory, regardless of how many instances of the class are created. In Java, a final variable is a variable that can be assigned a value once and cannot further be altered or modified once assigned.
	public final static int TILES_DEFAULT_SIZE = 32; // tile size is 32x32 pixels. 
	public final static float SCALE = 2f; // How much should we scale everything? 
	public final static int TILES_IN_WIDTH = 26; // How many tiles in width do we want? // update: we will think of this as visible tiles, not total tiles. 
	public final static int TILES_IN_HEIGHT = 14; // How many tiles in height do we want? // update: we will think of this as visible tiles, not total tiles.
	public final static int TILES_SIZE = (int)(TILES_DEFAULT_SIZE * SCALE); // try to make sure its a round number. 
	public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
	public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;
	
	public Game() { // Constructor

		initClasses(); // to initialize player, enemies, handlers, etc. 
		
		gamePanel = new GamePanel(this); // pass game into game panel
		new GameWindow(gamePanel);
		gamePanel.setFocusable(true); // make sure all inputs work for everyone. 
		gamePanel.requestFocus(); // we must tell that input focus to the JPanel
		
		startGameLoop();
		
		System.out.println(GAME_WIDTH + ":" + GAME_HEIGHT);

	}

	private void initClasses() {
		
		audioOptions = new AudioOptions(this);
		audioPlayer = new AudioPlayer();
		menu = new Menu(this);
		playing = new Playing(this);
		gameOptions = new GameOptions(this);
		
	}

	private void startGameLoop() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	private void update() {
		
		switch (Gamestate.state) {
		case MENU:
			menu.update();
			break;
		case PLAYING:
			playing.update();
			break;
		case OPTIONS:
			gameOptions.update();
			break;
		case QUIT: 
		default:
			System.exit(0); // terminate the program. Depending on integer we pass to it, we can get a different type of exit log or smt similar.  
			break;
		}
		
	}
	
	public void render (Graphics g) {
		
		switch (Gamestate.state) {
		case MENU:
			menu.draw(g);
			break;
		case PLAYING:
			playing.draw(g);
			break;
		case OPTIONS:
			gameOptions.draw(g);
			break;
		default:
			break;
		}
		
	}

	// GAME ENGINE
	@Override
	public void run() { // method in runnable that we must fill. we pass Runnable to Thread in order to
						// run this "run" method.

		double timePerFrame = 1000000000.0 / FPS_SET; // how long each frame should last. will use nanoseconds. 1 second = 1 billion nanoseconds.
		double timePerUpdate = 1000000000.0 / UPS_SET; // the time in between updates
		
		long previousTime = System.nanoTime();

		int frames = 0;
		int updates = 0;
		long lastCheck = 0; // the time at the last check.
		
		double deltaU = 0;
		double deltaF = 0;

		while (true) { // checking from the previous frame till now if the tamePerFrame has passed. If
						// so, next frames comes (we do refresh of the game screen).

			long currentTime = System.nanoTime();
			
			// The way to check if it's time to update the game is to check if deltaU is more or equal to 1. DeltaU gets increased by taking minus previousTime and dividing the result with time per update. 
			deltaU += (currentTime - previousTime) / timePerUpdate; // deltaU will be 1.0 or more when the duration since last update is equal or more that timePerUpdate. 
			deltaF += (currentTime - previousTime) / timePerFrame;
			previousTime = currentTime;
			
			if (deltaU >= 1) {
				update();
				updates++;
				deltaU--;
			}
			
			if (deltaF >= 1) { // if more that one, we update (render)
				gamePanel.repaint();
				frames++;
				deltaF--;
			}

			// FPS check. currentTimeMillis() returns the time in milliseconds, 1 second =
			// 1000 milliseconds.
			if (System.currentTimeMillis() - lastCheck >= 1000) { // if one second have passed since the last fps check, we do a new fps check. Save the new fps cehck as the last fps check and repeat.

				lastCheck = System.currentTimeMillis();
				System.out.println("FPS: " + frames + " | UPS: " + updates);

				frames = 0; // reset frames
				updates = 0; // reset updates
			}

		}

	}
	
	public void windowFocusLost() {
		
		if (Gamestate.state == Gamestate.PLAYING) {
			playing.getPlayer().resetDirBooleans();
		}
		
	}
	
	public Menu getMenu() {
		return menu;
	}
	
	public Playing getPlaying() {
		return playing;
	}
	
	public GameOptions getGameOptions() {
		return gameOptions;
	}
	
	public AudioOptions getAudioOptions() {
		return audioOptions;
	}
	
	public AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}

}
