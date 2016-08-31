import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.Icon;
import javax.swing.ImageIcon;
/**
 * The Character class is used to define a game character.
 * Player characters, enemy characters, bosses, all share these common characteristics.
 * @author Shane Birdsall
 */
public abstract class Character<E extends AbstractBullet> implements Runnable {
	protected Icon currentImage;  // currentImage is used to store the currentImage displayed at any given time.
	protected Icon playerNormal; // Default image, important for idle state
	protected int CHARACTER_HEIGHT; // Image height of character
	protected int NUMBER_OF_RUN_IMG; // Number of running images per running animation
	protected int NUMBER_OF_SHOOT_IMG; // Number of shooting images per running animation
	protected int NUMBER_OF_DIE_IMG; // Number of dying images per running animation
	protected int x, y; // X and Y co-ordinate of the character. 
	protected boolean alive, killed; 
	protected final int SLEEP_TIME = 50; // Time to sleep between animations, and condition statements. Used for my run method.
	protected ConcurrentLinkedQueue<E> bullets; // thread safe
	// animation arrays
	protected Icon[] running; 
	protected Icon[] shooting;
	protected Icon[] dying;
	protected STATE currentState; // every character will have a state at any given time
	
	public Character(int charHeight, int xPos, int yPos, int shootImg, int runImg, int dieImg) {
		NUMBER_OF_RUN_IMG = runImg;
		NUMBER_OF_SHOOT_IMG = shootImg;
		NUMBER_OF_DIE_IMG = dieImg;
		CHARACTER_HEIGHT = charHeight;
		setCurrentState(STATE.IDLE); // start idle
		running = new Icon[NUMBER_OF_RUN_IMG]; // initialise arrays
		shooting = new Icon[NUMBER_OF_SHOOT_IMG];
		dying = new Icon[NUMBER_OF_DIE_IMG];
		setCurrentImage();
		addToImageArrays(); 
		x = xPos;
		y = yPos - CHARACTER_HEIGHT; // account for the characters height
		alive = true; // Character is alive.
		bullets = new ConcurrentLinkedQueue<E>(); // Empty bullet Queue
		//Note: (Thread safe i.e. protected against consumer-producer type race conditions)
	}
	protected synchronized STATE getCurrentState() {
		return currentState;
	}
	protected synchronized void setCurrentState(STATE state) {
		currentState = state;
	}
	protected abstract void setCurrentImage();
	protected void setStandardImage(String imgPath) {
		playerNormal = createImage(imgPath);
		currentImage = playerNormal;
	}
	protected synchronized void updateCurrentImage(Icon img) {
		currentImage = img;
	}
	protected abstract void addToImageArrays();
	protected void addToImageArrays(String runPath, String shootPath, String diePath) {
		for(int i = 0; i < NUMBER_OF_RUN_IMG; i++) {
			running[i] = createImage(runPath+(i+1)+".png");
		}
		for(int i = 0; i < NUMBER_OF_SHOOT_IMG; i++) {
			shooting[i] = createImage(shootPath+(i+1)+".png");
		}
		for(int i = 0; i < NUMBER_OF_DIE_IMG; i++) {
			dying[i] = createImage(diePath+(i+1)+".png");
		}
	}
	public static Icon createImage(String path) {
		return new ImageIcon(path);
	}
	protected void enqueueBullet(E bullet) {
		bullets.offer(bullet);
		new Thread(bullet).start(); // start bullet thread
	}
	protected void dequeueBullet() {
		bullets.poll();
	}
	protected Icon getCurrentImage() {
		return currentImage;
	}
	public synchronized int getX() {
		return x;
	}
	public synchronized int getY() {
		return y;
	}
	protected synchronized boolean isAlive() {
		return alive;
	}
	public synchronized void kill() {
		killed = true;
		setCurrentState(STATE.IDLE); // stops any other actions, such as shooting.
	}
	protected synchronized boolean isKilled() {
		return killed;
	}
	public synchronized void setAlive(boolean state) {
		alive = state;
	}
	public synchronized void addToY(int amount) {
		y += amount;
	}
	public synchronized void setY(int amount) {
		y = amount - CHARACTER_HEIGHT; // account for the characters height
	}
	public synchronized void setX(int amount) {
		x = amount;
	}
	public synchronized void addToX(int amount) {
		x += amount;
	}
	public synchronized void stop() {
		setCurrentState(STATE.IDLE);
		setAlive(false);
	}
	protected synchronized void drawPlayer(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; 
		getCurrentImage().paintIcon(null, g2, x, y);
		for(E i : bullets) {
			i.drawBullet(g2);
		}
	} 
}