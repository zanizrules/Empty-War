import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
/**
 * The Flag class is used to define a flag in the wind which symbolises the end of a level.
 * @author Shane Birdsall
 */
public class Flag implements Runnable {
	public static final int NUM_OF_FLAG_IMG = 6; // 6 images per single animation loop
	Icon currentImage;
	Icon[] flags = new Icon[NUM_OF_FLAG_IMG];
	private boolean alive;
	private int x,y;
	
	public Flag(int x, int y) {
		setAlive(true);
		this.x = x;
		this.y = y;
		for(int i = 0; i < NUM_OF_FLAG_IMG; i++) {
			flags[i] = Character.createImage("Artwork/flags/flag"+(i+1)+".png");
		}
		currentImage = flags[0];
		new Thread(this).start();
	}
	public synchronized boolean isAlive() {
		return alive;
	}
	public synchronized void setAlive(boolean state) {
		alive = state;
	}
	public synchronized void move(int amount) {
		x -= amount;
	}
	public void drawFlag(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		currentImage.paintIcon(null, g2, x, y);
	}
	@Override
	public void run() {
		while(isAlive()) {
			for(int i = 0; i < NUM_OF_FLAG_IMG; i++) { // gives the look of flag in the wind
				currentImage = flags[i];
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}		
	}
}
