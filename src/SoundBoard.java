import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
/**
 * The SoundBoard class handles the games audio.
 * @author Shane Birdsall
 */
public class SoundBoard implements LineListener {
	private File soundFile; 
	private Clip clip;
	private Line line;
    
	private SoundBoard(String file) {
		soundFile = new File("Sounds/"+file+".wav"); // create new file
	    try {
			Line.Info linfo = new Line.Info(Clip.class);
			line = AudioSystem.getLine(linfo);
			clip = (Clip) line;
			clip.addLineListener(this);
			AudioInputStream input = AudioSystem.getAudioInputStream(soundFile);
			clip.open(input);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	public static void play(SOUNDS sound) { //static method used to play a sound
		SoundBoard a = new SoundBoard(sound.filename);
		a.clip.start(); // play sound
	}
	@Override
	public void update(LineEvent e) {
		 LineEvent.Type type = e.getType();
		 if (type == LineEvent.Type.STOP) 
			  clip.close(); // close clip after it has played
	}
}
