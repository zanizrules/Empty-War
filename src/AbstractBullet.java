import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;
/**
 * The AbstractBullet class is used to define the common characteristics of all bullets.
 * @author Shane Birdsall
 */
public abstract class AbstractBullet implements Runnable {
	protected Icon bulletImg;
	protected int BULLET_SPEED;
	protected final static int SLEEP_TIME = 30;
	protected boolean alive; 
	protected int x, y; // X and Y co-ordinate of the bullet. (Y is a fixed position)
	protected Character<?> characterRef; // Gives reference to the character who shot the bullet.
	
	public AbstractBullet(int x, int y, String path, int speed, Character<?> ref) {
		bulletImg = new ImageIcon(path);
		alive = true;
		setLocation(x,y);
		BULLET_SPEED = speed;
		characterRef = ref;
	}
	public void drawBullet(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; 
		bulletImg.paintIcon(null, g2, x, y);
	}  
	public synchronized void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public synchronized void move(int amount) {
		x += amount;
	}
	public void move() {
		move(BULLET_SPEED);
	}
	public synchronized boolean isAlive() {
		return alive;
	}
	public synchronized void setAlive(boolean state) {
		alive = state;
	}
	/**
	 * This method does two things: <br>
	 * 1) Kill this character because they were shot. <br>
	 * 2) Remove the bullet that killed the character from the screen. <br>
	 * <b>Example:</b> when EnemyCharacter uses this method it means that characterRef will store a PlayerCharacter. <br>
	 *	 		The enemy will be killed and the players bullet removed from the screen.
	 */
	public synchronized void kill() {
		setAlive(false); 
		characterRef.dequeueBullet(); 
	}
	public synchronized void stop() {
		setAlive(false);
	}
}
