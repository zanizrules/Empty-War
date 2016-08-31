import javax.swing.Icon;
/**
 * The BossCharacter class is used to define a boss character with special 
 * characteristics to be used in various levels.
 * @author Shane Birdsall
 */
public class BossCharacter extends AbstractEnemyCharacter {
	public static final int NUMBER_OF_LAUGH_IMG = 5; // 5 images make one full laugh animation
	public static final int BOSS_HEALTH = 10; // Ten hits to kill boss
	public static final Icon[] laughing = new Icon[NUMBER_OF_LAUGH_IMG]; // initialise
	private int currentHealth;
	public BossCharacter(int xPos) { // TYPE 3
		super(68, xPos-80, GameGui.PANEL_HEIGHT-10, 8, 6, 6, 3);
		setUpImages();
		currentHealth = BOSS_HEALTH;
	}
	@Override
	protected void setCurrentImage() {
		setStandardImage("Artwork/Boss/idle.png");
	}
	@Override
	protected void addToImageArrays() {
		addToImageArrays("Artwork/Boss/Running/run", "Artwork/Boss/Shooting/shoot", "Artwork/Boss/Dying/die");
	}
	public void setUpImages() { // Assign images to the arrays
		if(laughing[NUMBER_OF_LAUGH_IMG-1] == null) {
			for(int i = 0; i < NUMBER_OF_LAUGH_IMG; i++) {
				laughing[i] = createImage("Artwork/Boss/Laughing/laugh"+(i+1)+".png");
			}
		}
	}
	private synchronized void unKill() {
		killed = false;
	}
	@Override
	public void run() {
		while(isAlive()) {
			try {
				if(isKilled()) { // Handle boss dying
					currentHealth--;
					unKill();
					if(currentHealth < 1) {
						kill();
						setAlive(false); // Stops thread from continuing 
						SoundBoard.play(SOUNDS.BOSS_DEATH);
						for(int i = 0; i < NUMBER_OF_DIE_IMG; i++) { // Die animation
							updateCurrentImage(dying[i]);
							Thread.sleep(100 - (i*2));
						}
					} else {
						SoundBoard.play(SOUNDS.OUCH); // Plays each time boss is shot
					}
				} else if(getCurrentState() == STATE.MOVE) { // Handles boss moving
					for(int i = 0; i < NUMBER_OF_RUN_IMG; i++) {
						if(getCurrentState() == STATE.MOVE && !isKilled()) { // Check to see if still in MOVE state
							move();
							updateCurrentImage(running[i]);
							Thread.sleep(SLEEP_TIME/2); 
						}
					}
				} else if(getCurrentState() == STATE.SHOOT) { // handle when boss is shooting
					while(getCurrentState() == STATE.SHOOT && !isKilled()) {
						for(int i = 0; i < NUMBER_OF_SHOOT_IMG; i++) {
							if(getCurrentState() == STATE.SHOOT) {
								Thread.sleep(SLEEP_TIME/2);
								updateCurrentImage(shooting[i]);
							}
						}
						if(getCurrentState() == STATE.SHOOT) { // boss shoots bullets in sets of 4
							// Let shot off here
							SoundBoard.play(SOUNDS.BOSS_SHOOT);
							enqueueBullet(new BossBullet(this)); // 1st bullet
							Thread.sleep(SLEEP_TIME/2);
							enqueueBullet(new BossBullet(this)); // 2nd bullet
							Thread.sleep(SLEEP_TIME/2);
							enqueueBullet(new BossBullet(this)); // 3rd bullet 
							Thread.sleep(SLEEP_TIME/2);
							enqueueBullet(new BossBullet(this)); // 4th bullet
							Thread.sleep(SLEEP_TIME/2);
							updateCurrentImage(playerNormal);
						}
						int count = 0;
						while(!isKilled() && getCurrentState() == STATE.SHOOT && count < 25) { // boss laughs before shooting again
							updateCurrentImage(laughing[(count%NUMBER_OF_LAUGH_IMG)]);
							Thread.sleep(SLEEP_TIME*2);
							count++;
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
	class BossBullet extends AbstractEnemyBullet { // bullet specific to the buss character
		public BossBullet(BossCharacter ref) {
			super(ref.getX(), ref.getY()+35, "Artwork/bullets/bossbullet.png", -25, ref);
		}
		public void run() {
			while(isAlive()) {
				try {
					move();
					if(getX() < 0)
						kill(); // kill when off screen
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {}
			}
		}
	}
}
