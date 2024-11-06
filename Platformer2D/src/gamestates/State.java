// SUPERCLASS for all game states. 

package gamestates;

import java.awt.event.MouseEvent;

import audio.AudioPlayer;
import main.Game;
import ui.MenuButton;

public class State {
	
	protected Game game;
	
	public State(Game game) {
		
		this.game = game;
		
	}
	
	public boolean isIn(MouseEvent e, MenuButton mb) { // is the player is pressing inside the button?
		
		return mb.getBounds().contains(e.getX(), e.getY()); // returning a boolean that is going to give us true if our mouse is inside this rectangle. 
		
	}
	
	public Game getGame() {
		return game;
	}
	
	public void setGamesState(Gamestate state) {
		
		switch(state) {
		case MENU -> game.getAudioPlayer().playSong(AudioPlayer.MENU_1);
		case PLAYING -> game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getLevelIndex());
		}
		
		Gamestate.state = state;
		
	}

}
