/**
 * MeleeEnemyCharacter defines a enemy character who specialises in sword-play.
 * @author Shane Birdsall
 */
public class MeleeEnemyCharacter extends AbstractEnemyCharacter {
	public MeleeEnemyCharacter(int xPos) { // TYPE 2
		super(59, xPos, GameGui.PANEL_HEIGHT-10, 5, 12, 20, 2);
	}
	@Override
	protected void setCurrentImage() { // default
		setStandardImage("Artwork/meleeEnemy/Slash/1.png");
	}
	@Override
	protected void addToImageArrays() {
		addToImageArrays("Artwork/meleeEnemy/Run/run", "Artwork/meleeEnemy/Slash/", "Artwork/meleeEnemy/Die/");
	}
	@Override
	public void run() {
		while(isAlive()) {
			try {
				if(isKilled()) { // handles dying
					setAlive(false); // Stops thread from continuing 
					SoundBoard.play(SOUNDS.SCREAM);
					for(int i = 0; i < NUMBER_OF_DIE_IMG; i++) { // Die animation
						updateCurrentImage(dying[i]);
						Thread.sleep(SLEEP_TIME*2);
					}
				} else if(getCurrentState() == STATE.MOVE) { // handles moving
					for(int i = 0; i < NUMBER_OF_RUN_IMG; i++) {
						if(getCurrentState() == STATE.MOVE && !isKilled()) { // Check to see if still in MOVE state
							move();
							updateCurrentImage(running[i]);
							Thread.sleep(SLEEP_TIME/2);
						}
					}
				} else if(getCurrentState() == STATE.SHOOT) { // handles shooting
					while(getCurrentState() == STATE.SHOOT && !isKilled()) {
						for(int i = 0; i < NUMBER_OF_SHOOT_IMG; i++) {
							if(getCurrentState() == STATE.SHOOT) {
								Thread.sleep(SLEEP_TIME/2);
								updateCurrentImage(shooting[i]);
							}
						}
						if(getCurrentState() == STATE.SHOOT) {
							// Let shot off here
							SoundBoard.play(SOUNDS.KNIFE); // slash sound
							enqueueBullet(new Slash(this)); // treat melee slash as a bullet for detection purposes
							Thread.sleep(SLEEP_TIME/2);
							updateCurrentImage(playerNormal);
						}
						if(!isKilled() && getCurrentState() == STATE.SHOOT) {
							Thread.sleep(SLEEP_TIME*4); // wait
						}
					}
				}  else {
					// I only want enemies to move, shoot, and be killed. Otherwise they MUST be in an IDLE state.
					setCurrentState(STATE.IDLE);
					updateCurrentImage(playerNormal);
				}
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {}
		}
	}
	class Slash extends AbstractEnemyBullet { // "Bullet" used for MeleeEnemyCharacter
		public Slash(MeleeEnemyCharacter ref) {
			super(ref.getX()-10, 0, "", 0, ref);
		}
		public void run() {
			try {
				Thread.sleep(SLEEP_TIME); // never move just sleep before ending
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}