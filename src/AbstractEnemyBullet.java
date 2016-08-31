/**
 * The AbstractEnemyBullet class is used to specifically define ENEMY bullets.
 * @author Shane Birdsall
 */
public abstract class AbstractEnemyBullet extends AbstractBullet {
	public AbstractEnemyBullet(int x, int y, String path, int speed, AbstractEnemyCharacter ref) {
		super(x, y, path, speed, ref);
	}
}
