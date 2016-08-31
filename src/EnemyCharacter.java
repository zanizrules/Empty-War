/**
 * The EnemyCharacter class is used to define the default enemy character.
 * @author Shane Birdsall
 */
public class EnemyCharacter extends AbstractEnemyCharacter {
	public EnemyCharacter(int xPos) { // TYPE 1
		super(47, xPos, GameGui.PANEL_HEIGHT-15, 10, 12, 17,1);
	}
	@Override
	protected void addToImageArrays() {
		addToImageArrays("Artwork/enemy/running/enemyRun", "Artwork/enemy/shooting/enemyShoot", "Artwork/enemy/dying/enemyDie");
	}
	@Override
	protected void setCurrentImage() {
		setStandardImage("Artwork/enemy/shooting/enemyShoot1.png");
	}
	@Override
	public void run() {
		while(isAlive()) {
			try {
				if(isKilled()) { // handles dying
					setAlive(false); // Stops thread from continuing 
					addToY(7); // Adjustment for different picture sizes
					SoundBoard.play(SOUNDS.ENEMY_DEATH); // plays upon death
					for(int i = 0; i < NUMBER_OF_DIE_IMG; i++) { // Die animation
						updateCurrentImage(dying[i]);
						Thread.sleep(100 - (i*2));
					}
				} else if(getCurrentState() == STATE.MOVE) { // handles moving
					for(int i = 0; i < NUMBER_OF_RUN_IMG; i++) {
						if(getCurrentState() == STATE.MOVE && !isKilled()) { // Check to see if still in MOVE state
							move();
							updateCurrentImage(running[i]);
							Thread.sleep(SLEEP_TIME);
						}
					}
				} else if(getCurrentState() == STATE.SHOOT) { // handles shooting
					for(int i = 0; i < 4; i++) { // Raise the gun
						if(!isKilled() && getCurrentState() == STATE.SHOOT) {
							updateCurrentImage(shooting[i]);
							Thread.sleep(SLEEP_TIME);
						}
					}
					while(getCurrentState() == STATE.SHOOT && !isKilled()) {
						for(int i = 0; i < 6; i++) {
							if(getCurrentState() == STATE.SHOOT) {
								Thread.sleep(SLEEP_TIME);
								updateCurrentImage(shooting[4+i]);
							}
						}
						if(getCurrentState() == STATE.SHOOT) {
							// Let shot off here
							SoundBoard.play(SOUNDS.PISTOL2); 
							enqueueBullet(new EnemyBullet(this));
							Thread.sleep(SLEEP_TIME/2);
							updateCurrentImage(shooting[4]);
							Thread.sleep(SLEEP_TIME/2);
						}
						int count = 0;
						while(!isKilled() && getCurrentState() == STATE.SHOOT && count < 25) {
							Thread.sleep(SLEEP_TIME); // wait, as to not shot too often
							count++; // counting ensures the wait time, but still allows exiting the loop if things change.
						} 
					}
					if(!isKilled()) {
						for(int i = 0; i < 3; i++) { //lower gun
							updateCurrentImage(shooting[3-i]);
							Thread.sleep(SLEEP_TIME);
						}
					}
				}  else {
					// I only want enemies to move, shoot, and be killed. Otherwise they MUST be in an IDLE state.
					setCurrentState(STATE.IDLE);
					updateCurrentImage(playerNormal); // shooting[0] is also playerNormal
				}
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {}
		}
	}
}