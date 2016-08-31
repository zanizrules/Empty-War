import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.ImageIcon;
/**
 * The Level class is used to define the common characteristics of a Game Level.
 * @author Shane Birdsall
 */
public abstract class Level {
	private final int LEVEL_NUM; // used to recognise the level number. E.g. the first level is Level 1.
	private Icon background; // levels background
	protected Flag finishFlag; // levels flag
	public int x, y; // x, y co-ordinates of background
	public static final int MOVE_DISTANCE = 5; // how much to move by
	public final int PLAYER_Y;
	public final int END_POINT; // x point that represents the end of the level
	public final int NUM_OF_ENEMIES; // number of enemies
	private ArrayList<AbstractEnemyCharacter> enemies;
	protected static Random rand = new Random(); // can be used in deciding locations to spawn enemies, or what enemie type to spawn.
	
	public Level(String path,int i, int end, int num, int flagOffSet, int playerY) {
		PLAYER_Y = playerY;
		LEVEL_NUM = i;
		x = 0;
		y = 0;
		END_POINT = end;
		NUM_OF_ENEMIES = num;
		background = new ImageIcon("Artwork/backgrounds/" + path +".png");
		initialiseEnemies();
		finishFlag = new Flag((END_POINT - ((2*GameGui.PANEL_WIDTH)/3)-50), (GameGui.PANEL_HEIGHT - flagOffSet));
	}
	public boolean moveBackground(boolean left) {
		if(left) { // if player is moving left then move background left
			if(getX() > MOVE_DISTANCE) {
				moveBackground(-MOVE_DISTANCE);
			}
			return true;
		} else {
			if(getX() <= -END_POINT + GameGui.PANEL_WIDTH) {
				return false; // cause player to move rather than background
			} else { // if player is moving right then move background right
				moveBackground(MOVE_DISTANCE);
				return true;
			}
		}
	}
	public int getLevelNum() {
		return LEVEL_NUM;
	}
	public void moveBackground(int amount) {
		moveX(amount);	// Move background to the left
		finishFlag.move(amount); // Move flag to the left to keep its original position on the background
		for(AbstractEnemyCharacter i : enemies) {
			i.move(amount); // Move enemy to the left to keep its original position on the background
			for(AbstractEnemyBullet e : i.bullets) {
				e.move(-amount); // Move enemy bullet to the left to keep its original position on the background
			}
		}
	}
	public synchronized void moveX(int amount) {
		x -= amount;
	}
	public synchronized int getX() {
		return x;
	}
	public void drawLevel(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// Draw background first
		background.paintIcon(null, g2, x, y);
		finishFlag.drawFlag(g2); // draw flag
		for(AbstractEnemyCharacter i : enemies)
			i.drawPlayer(g2); // draw enemies
	}  
	public ArrayList<AbstractEnemyCharacter> getEnemies() {
		return enemies;
	}
	public abstract void initialiseEnemies();
	public void initialiseEnemies(ArrayList<AbstractEnemyCharacter> characters) {
		enemies = characters;
	}
	public void startEnemies() {
		for(AbstractEnemyCharacter i : enemies)
			new Thread(i).start(); //start enemie threads
	}
}