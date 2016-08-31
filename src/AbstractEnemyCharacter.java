/**
 * The AbstractEnemyCharacter class is used to specifically define ENEMY characters.
 * @author Shane Birdsall
 */
public abstract class AbstractEnemyCharacter extends Character<AbstractEnemyBullet> {
	private static final int ENEMY_SPEED = 5;
	public static final int POINTS_FOR_KILLING = 100;
	public final int ENEMY_TYPE;
	public AbstractEnemyCharacter(int charHeight, int xPos, int yPos, int shootImg, int runImg, int dieImg, int type) {
		super(charHeight, xPos, yPos, shootImg, runImg, dieImg);
		ENEMY_TYPE = type;
	}
	protected void move() {
		move(ENEMY_SPEED);
	}
	protected void move(int distance) {
		x -= distance;
	}
}