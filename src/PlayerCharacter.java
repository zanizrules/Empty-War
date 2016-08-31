import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.ImageIcon;
/**
 * The PlayerCharacter class is used to define a user character (character that player controls).
 * @author Shane Birdsall
 */
public class PlayerCharacter extends Character<Bullet> { 
	public static final int STARTING_X_POS = 75; // where player starts, X pixels in.
	public static final int MELEE_DISTANCE = 5; // distance that knife works
	public static final int NUMBER_OF_WIN_IMG = 5; // number of winning images per animation loop
	public static final int NUMBER_OF_KNIFE_IMG = 5;// number of knifing images per animation loop
	public static final Icon[] winning = new Icon[NUMBER_OF_WIN_IMG]; // set up arrays
	public static final Icon[]	melee = new Icon[NUMBER_OF_KNIFE_IMG];
	public static final Icon crouch = createImage("Artwork/player/crouch/crouch1"+".png");
	private Level currentLevel; // stores current level, E.g. first level, second level etc.
	private boolean automaticPistol, won, allowCrouch, moveBackground, left; // boolean values for checks
	private int score; // players score

	public PlayerCharacter() {
		super(41, STARTING_X_POS, 0, 11, 12, 8);
		currentLevel = new Level_1();
		setY(currentLevel.PLAYER_Y);
		automaticPistol = true;
		allowCrouch = true;
		moveBackground = false;
		left = false;
		setUpImages();
		score = 0;
	}
	public synchronized void move() {
		if(left) { // move player to the left
			if(x > -Level.MOVE_DISTANCE) {
				x -= Level.MOVE_DISTANCE;
			}
		} else // move player to the right
			x += Level.MOVE_DISTANCE;
		if(x > GameGui.PANEL_WIDTH/3) {
			setMoveBackground(true); // move background next, rather than player
		}
	}
	public synchronized boolean nextLevel() { // switches the current level
		if(currentLevel.getLevelNum() == 1) { // go to level 2
			currentLevel = new Level_2();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			setY(currentLevel.PLAYER_Y); // set location
			setX(STARTING_X_POS);
			currentLevel.startEnemies(); // start enemies
		} else if(currentLevel.getLevelNum() == 2) { // go to level 3
			currentLevel = new Level_3();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			setY(currentLevel.PLAYER_Y); // set location
			setX(STARTING_X_POS);
			currentLevel.startEnemies(); // start enemies
		} else {
			return false;
		}
		return true;	
	}
	public void setUpImages() { // add images to arrays
		if(winning[NUMBER_OF_WIN_IMG-1] == null) {
			for(int i = 0; i < NUMBER_OF_WIN_IMG; i++) {
				winning[i] = createImage("Artwork/player/winning/win"+(i+1)+".png");
			}
		}
		getMeleeImages();
	}
	public static Icon[] getMeleeImages() {// add melee images to array
		if(melee[NUMBER_OF_KNIFE_IMG-1] == null) {
			for(int i = 0; i < NUMBER_OF_KNIFE_IMG; i++) {
				melee[i] = createImage("Artwork/player/knife/knife"+(i+1)+".png");
			}
		}
		return melee;
	}
	@Override
	protected void setCurrentImage() {
		setStandardImage("Artwork/player/playerNormal.png");
	} 
	public Level getCurrentLevel() {
		return currentLevel;
	}
	public synchronized boolean getAllowCrouch() {
		return allowCrouch;
	}
	private synchronized void setAllowCrouch(boolean bol) {
		allowCrouch = bol;
	}
	private synchronized void setMoveBackground(boolean bol) {
		moveBackground = bol;
	}
	public synchronized void setAutomaticPistol(boolean state) {
		automaticPistol = state;
	}
	public synchronized boolean isAutomaticPistol() {
		return automaticPistol;
	}
	public void drawPlayer(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		currentLevel.drawLevel(g2);
		if(left) {
			ImageIcon ic = (ImageIcon)getCurrentImage();
			g2.drawImage(ic.getImage(), x+41, y, -ic.getIconWidth(), CHARACTER_HEIGHT, null);
			for(Bullet i : bullets) {
				i.drawBullet(g2); // draw player bullets
			}
		} else {
			super.drawPlayer(g2); // draw character
		}
	}
	@Override
	protected void addToImageArrays() {
		addToImageArrays("Artwork/player/running/runright", "Artwork/player/shooting/shoot", "Artwork/player/dying/die");
	}
	public synchronized void setWin(boolean win) {
		setCurrentState(STATE.IDLE);
		won = win;
	}
	public synchronized void setLeft(boolean bol) { // switch between moving left and right
		setCurrentState(STATE.MOVE);
		left = bol;
		if(left) {
			setMoveBackground(false);
		}
	}
	public synchronized boolean getLeft() {
		return left;
	}
	public boolean hasWon() {
		return won;
	}
	private synchronized boolean moveBackground() {
		if(x <= GameGui.PANEL_WIDTH/3) { 
			setMoveBackground(false); // player should now move rather than background
		}
		if(currentLevel.moveBackground(left)) {
			int distance = Level.MOVE_DISTANCE;;
			if(!left) {
				distance = -distance; // invert
			}
			for(Bullet i : bullets) {
				i.move(distance); // adjust bullets
			}
			return true;
		} else
			return false;
	}
	public void addToScore() {
		score += EnemyCharacter.POINTS_FOR_KILLING; // points added (call this method after getting a kill)
	}
	public int getScore() {
		return score;
	}
	public void run() {
		while(isAlive()) {
			try {
				if(isKilled()) { // handle dying
					setAlive(false); // Stop thread
					setLeft(false);
					if(!left) {
						addToY(-9); // adjust for different picture size
					} else {
						addToX(-10); // Adjust for left facing death
					} 
					SoundBoard.play(SOUNDS.PLAYER_DEATH);
					for(int i = 0; i < NUMBER_OF_DIE_IMG; i++) { // Die animation
						updateCurrentImage(dying[i]);
						Thread.sleep(SLEEP_TIME*3);
					}
					while(!bullets.isEmpty()) { // Wait until all bullets have finished
						Thread.sleep(SLEEP_TIME*2);
					}
					for(AbstractEnemyCharacter i : this.getCurrentLevel().getEnemies()) {
						i.stop(); // Stop enemies
					}
				} else if(getCurrentState() == STATE.MOVE) { // handles moving
					while(getCurrentState() == STATE.MOVE && !isKilled()) {
						for(int i = 0; i < NUMBER_OF_RUN_IMG/2; i++) { //1st half of run animation
							if(getCurrentState() == STATE.MOVE && !isKilled()) {
								if(moveBackground) { // if player should move or bnackground should
									if(moveBackground()) { // if background is able to move
										updateCurrentImage(running[i]);
										Thread.sleep(SLEEP_TIME-10);
									} else setWin(true); // reached end 
								} else { // move player
									move();
									updateCurrentImage(running[i]);
									Thread.sleep(SLEEP_TIME-10);
								}
							}
						}
						if(getCurrentState() == STATE.MOVE && !isKilled()) {
							for(int i = NUMBER_OF_RUN_IMG/2; i < NUMBER_OF_RUN_IMG; i++) { //2nd half of run animation
								if(getCurrentState() == STATE.MOVE && !isKilled()) {
									if(moveBackground) { // same checks as before
										if(moveBackground()) { // repeated twice for faster response of change
											updateCurrentImage(running[i]);
											Thread.sleep(SLEEP_TIME-10);
										} else setWin(true);
									} else { // move player
										move();
										updateCurrentImage(running[i]);
										Thread.sleep(SLEEP_TIME-10);
									}
								}
							}
						}
					}
				} else if(getCurrentState() == STATE.SHOOT) { // handle shooting
					for(int i = 0; i < 3; i++) { // Raise gun 
						if(!isKilled() && getCurrentState() == STATE.SHOOT) {
							updateCurrentImage(shooting[9-i]);
							Thread.sleep(SLEEP_TIME);
						}
					}
					do {
						setAllowCrouch(false); // Don't allow player to crouch
						if(!isKilled()) {
							updateCurrentImage(shooting[0]);
							Thread.sleep(SLEEP_TIME);
						}
						for(int i = 1; i < 4; i++) {
							if(!isKilled()) {
								updateCurrentImage(shooting[i]);
								Thread.sleep(SLEEP_TIME);
							}
						}
						if(!isKilled()) {
							// Let shot off here
							SoundBoard.play(SOUNDS.PISTOL1);
							enqueueBullet(new Bullet(this));
							Thread.sleep(SLEEP_TIME/3);
						}
						for(int i = 0; i < 3; i++) {
							if(!isKilled() && getCurrentState() != STATE.CROUCH) {
								updateCurrentImage(shooting[4+i]);
								Thread.sleep(SLEEP_TIME+(50*i));
							}
						}
					} while(getCurrentState() == STATE.SHOOT && isAutomaticPistol() && !isKilled());
					setAllowCrouch(true); // player can crouch again
					for(int i = 0; i < 3; i++) { // lower gun
						if(!isKilled() && getCurrentState() == STATE.SHOOT) {
							updateCurrentImage(shooting[7+i]);
							Thread.sleep(SLEEP_TIME);
						}
					}
					updateCurrentImage(playerNormal);
				} else if(getCurrentState() == STATE.KNIFE) { // handles knifing
					setAllowCrouch(false); // no crouching allowed
					SoundBoard.play(SOUNDS.KNIFE); // slash sound
					for(int i = 0; i < NUMBER_OF_KNIFE_IMG; i++) { // Knife animation
						if(!isKilled()) {
							updateCurrentImage(melee[i]);
							Thread.sleep(SLEEP_TIME*2);
						}
					}
					setAllowCrouch(true); // crouching allowed
					updateCurrentImage(playerNormal);
				} else if(getCurrentState() == STATE.CROUCH) { // handles crouching
					if(!isKilled()) {
						updateCurrentImage(crouch);
					}
					while(getCurrentState() == STATE.CROUCH && !isKilled()) {
						Thread.sleep(SLEEP_TIME); // wait until no longer crouching
					}
					updateCurrentImage(playerNormal);
				} else {
					// If the player is not doing any of the above states I want to ensure the state is IDLE.
					setCurrentState(STATE.IDLE); 
					updateCurrentImage(playerNormal);
				}
				if(hasWon()) {
					addToY(-9); // adjust for different picture size
					addToX(-9);
					SoundBoard.play(SOUNDS.MISSION_COMPLETE); // plays during win animation
					for(int i = 0; i < 15; i++) { // times to repeat animation 
						for(int j = 0; j < NUMBER_OF_WIN_IMG; j++) { // win animation
							Thread.sleep(75);
							updateCurrentImage(winning[j]);
						}
					}
					setAlive(false); // Stop thread
					Thread.sleep(SLEEP_TIME);
					if(!hasWon()) {
						updateCurrentImage(playerNormal);
						addToY(9); // reverse adjustments
						addToX(9);
					} else {
						updateCurrentImage(winning[NUMBER_OF_WIN_IMG-1]);
					}
					//Thread.sleep(SLEEP_TIME*3);
				}
				Thread.sleep(SLEEP_TIME/2);
			} catch (InterruptedException e) {}
		} 
	}
}