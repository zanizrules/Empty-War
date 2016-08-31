/**
 * The EnemyBullet class is used to define a bullet shot by the default enemy character. 
 * @author Shane Birdsall
 */
public class EnemyBullet extends AbstractEnemyBullet {
	public EnemyBullet(EnemyCharacter ref) {
		super(ref.getX()+24, ref.getY()+24, "Artwork/bullets/enemyBullet.png", -15, ref);
	}
	public void run() {
		while(isAlive()) {
			try {
				move(); // Travel, then check to see if off screen. Hit-detection is handled elsewhere.
				if(getX() < 0)
					kill();
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {}
		}
	}
}
