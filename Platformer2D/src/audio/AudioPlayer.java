package audio;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
	
	public static int MENU_1 = 0;
	public static int LEVEL_1 = 1;
	public static int LEVEL_2 = 2;
	
	public static int DIE = 0;
	public static int JUMP = 1;
	public static int GAMEOVER = 2;
	public static int LVL_COMPLETED = 3;
	public static int ATTACK_ONE = 4;
	public static int ATTACK_TWO = 5;
	public static int ATTACK_THREE = 6;
	
	private Clip[] songs, effects; // just for practice. It's recommended to use external library for sounds since Clip() has limitation (use only wav format) and might have a delay in sound. Also, many errors might appear using it. 
	private int currentSongId; 
	private float volume = 1f;
	private boolean songMute, effectMute;
	private Random rand = new Random(); // for attack sounds to keep them different. 
	
	public AudioPlayer() {
		
		loadSongs();
		loadEffects();
		playSong(MENU_1);
		
	}
	
	private void loadSongs() {
		
		String[] names = {"menu", "level1", "level2"}; // the name corresponds to the file name in res folder. 
		songs = new Clip[names.length];
		for (int i = 0; i < songs.length; i++)
			songs[i] = getClip(names[i]);
		
	}
	
	private void loadEffects() {
		
		String[] effectNames = {"die", "jump", "gameover", "lvlcompleted", "attack1", "attack2", "attack3"};
		effects = new Clip[effectNames.length];
		for (int i = 0; i < effects.length; i++)
			effects[i] = getClip(effectNames[i]);
		
		updateEffectsVolume(); // if volume gets saved to a file, we want to restart the game with that volume. 
		
	}
	
	private Clip getClip(String name) {
		
		URL url = getClass().getResource("/audio/" + name + ".wav");
		AudioInputStream audio;
		
		try {
			audio = AudioSystem.getAudioInputStream(url);
			Clip c = AudioSystem.getClip();
			c.open(audio);
			
			return c;
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public void setVolume(float volume) { // we will use our audio options class when we change the slider, we want to apply that volume to our music. 
		
		this.volume = volume;
		updateSongVolume();
		updateEffectsVolume();
		
	}
	
	public void stopSong() { // not muting. just stop the song
		
		if (songs[currentSongId].isActive()) 
			songs[currentSongId].stop();
		
	}
	
	public void setLevelSong(int lvlIndex) { // since we have only two songs, the idea is to switch the song between these two every time we move to the next level. 
		
		if (lvlIndex % 2 == 0) 
			playSong(LEVEL_1);
		else 
			playSong(LEVEL_2);
		
	}
	
	public void lvlCompleted() {
		
		stopSong();
		playEffect(LVL_COMPLETED);
		
	}
	
	public void playAttackSound() {
		
		int start = 4; // index for in the String[] effectNames. 
		start += rand.nextInt(3); // nextInt(3) will give 0, 1, or 2. Thus, choosing the effect randomly. 
		playEffect(start);
		
	}
	
	public void playEffect(int effect) {
		
		if (!effects[effect].isRunning()) {
			effects[effect].setMicrosecondPosition(0); // first, reset the effect song so that we can play it from the beginning. 
			effects[effect].start();
		}
	}
	
	public void playSong(int song) {
		
		stopSong();
		
		currentSongId = song; // change song id to the new id.
		updateSongVolume();
		songs[currentSongId].setMicrosecondPosition(0); // start playing from the beginning. 
		songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY); // loop the song without stopping. It must play continuously. 
		
	}
	
	public void toggleSongMute() { // we want to mute everything at the same time. 
		
		this.songMute = !songMute;
		for (Clip c : songs) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(songMute);
		}	
	}
	
	public void toggleEffectMute() {
		
		this.effectMute = !effectMute;
		for (Clip c : effects) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(effectMute);
		}
		// we are going to play an effect as soon as we unmute so that the player can hear the change. 
		if (!effectMute) // meaning the mute is false 
			playEffect(JUMP);
		
	}
	
	private void updateSongVolume() { // update the current song that's being played 
		
		FloatControl gainControl = (FloatControl) songs[currentSongId].getControl(FloatControl.Type.MASTER_GAIN);
		float range = gainControl.getMaximum() - gainControl.getMinimum(); // range of the volume 
		float gain = (range * volume) + gainControl.getMinimum(); // the change (value) we want to set. 
		gainControl.setValue(gain);
		
	}
	
	private void updateEffectsVolume() { // update all effects at the same time. 
		
		for (Clip c : effects) {
			
			FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
			float range = gainControl.getMaximum() - gainControl.getMinimum();
			float gain = (range * volume) + gainControl.getMinimum();
			gainControl.setValue(gain);
			
		}
	}

}
