import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
/**
 * The Bullet class is used to define a bullet shot by the player. 
 * @author Shane Birdsall
 */
public class Bullet extends AbstractBullet {
	private boolean left; // if the bullet should travel left
	public Bullet(PlayerCharacter ref) {
		super(ref.getX()+49,ref.getY()+12, "Artwork/bullets/bullet.png", 20, ref);
		left = ref.getLeft();
		if(left) {
			move(-55); // adjust if left
		}
	}
	@Override
	public synchronized void move() {
		if(left) 
			move(-BULLET_SPEED); // move x pixels to the left
		else 
			move(BULLET_SPEED); // move x pixels to the right
	}
	@Override
	public void drawBullet(Graphics g) {
		if(left) { // Reverse image rather than getting a new/different image for left facing
			Graphics2D g2 = (Graphics2D) g; 
			ImageIcon ic = (ImageIcon)bulletImg;
			g2.drawImage(ic.getImage(), x+ic.getIconWidth(), y, -ic.getIconWidth(), ic.getIconHeight(), null);
		} else // draw bullet normally
			super.drawBullet(g);
	} 
	public void run() {
		while(isAlive()) {
			try {
				move(); // Travel, then check to see if off screen. Hit-detection is handled elsewhere.
				if(getX() > GameGui.PANEL_WIDTH || getX() < 0) { // Kill bullet after it leaves the screen 
					kill();
				}
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {}
		}
	}
}
