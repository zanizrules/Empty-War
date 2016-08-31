import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;
/**
 * The GameGui class is the game panel and is responsible for the games input, and output.
 * @author Shane Birdsall
 */
public class GameGui extends JPanel implements ActionListener, KeyListener {
		private static final long serialVersionUID = 1L;
		public final static int PANEL_WIDTH = 800; // size of panel
		public final static int PANEL_HEIGHT = 224;
		private final static int SLEEP = 17; // 60fps
		private static Toolkit toolkit = Toolkit.getDefaultToolkit();
		private static Dimension dimension = toolkit.getScreenSize(); //gets the dimensions for screen width and height to calculate center
		private static int screenHeight = dimension.height;
		private static int screenWidth = dimension.width;
		private static JFrame frame; 
		private Thread detectionThread; // thread used to handle detection
		private Timer timer; // timer used to repaint
		private DrawPanel drawPanel; // draw panel for painting images
		private PlayerCharacter player; // user
		private JButton restartButton, quitButton, menuButton; // 3 buttons to be placed at the bottom of the screen
		private JLabel scoreLbl; // players score
		private Menu menu; // link back to main menu
		private GameDetection detection; // detection class
		
		public GameGui(Menu menu) {
			super(new BorderLayout());
			this.menu = menu;
			// Draw panel for game images
			drawPanel = new DrawPanel();
			drawPanel.setBackground(Color.BLACK);
			drawPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			add(drawPanel,BorderLayout.CENTER);
			
			// Game buttons
			restartButton = new JButton("Restart");
			restartButton.addActionListener(this);
			quitButton = new JButton("Quit");
			quitButton.addActionListener(this);
			menuButton = new JButton("Return to Menu");
			menuButton.addActionListener(this);
			
			// borders
			Border a = BorderFactory.createRaisedBevelBorder();
			Border b = BorderFactory.createLoweredBevelBorder();
			Border c = BorderFactory.createCompoundBorder(a, b);
			
			//Panel for Score Label
			JPanel scorePanel = new JPanel();
			scorePanel.setBackground(Color.RED.darker().darker().darker().darker().darker());
			scorePanel.setBorder(c);
			scoreLbl = new JLabel("Score:");
			try { // Set custom font
				scoreLbl.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("font/arcade.ttf")).deriveFont(Font.BOLD, 23));
			} catch (FontFormatException | IOException e) {
				e.printStackTrace();
			}
			scoreLbl.setForeground(Color.WHITE);
			scorePanel.setPreferredSize(new Dimension(800, 46));
			scorePanel.add(scoreLbl);
			add(scorePanel, BorderLayout.NORTH); // add score at top
			
			// Panel for game buttons
			JPanel panel = new JPanel();
			panel.setBackground(Color.RED.darker().darker().darker().darker().darker());
			panel.setBorder(c);
			panel.add(restartButton);
			panel.add(quitButton);
			panel.add(menuButton);
			add(panel, BorderLayout.SOUTH); // Buttons at the bottom of the screen		
			detection = new GameDetection();
			detectionThread = new Thread(detection); // begin detection algorithm
			setPlayer(new PlayerCharacter()); // create player character
			timer = new Timer(SLEEP, this); // create timer
		}
		private void setPlayer(PlayerCharacter newPlayer) {
			player = newPlayer; // assign player character
		}
		private class DrawPanel extends JPanel { // Used to paint images/icons
			private static final long serialVersionUID = 1L;
			public DrawPanel() {
				super();
				setPreferredSize(new Dimension(PANEL_WIDTH ,PANEL_HEIGHT));
			}
			public void paintComponent(Graphics g) { //can be used to draw whatever I want!
				Graphics2D g2 = (Graphics2D) g;
				super.paintComponent(g2);
				player.drawPlayer(g2); // Draws player + Level + any bullets
			}
		}
		@Override
		public void keyPressed(KeyEvent e) { // handles user input
			if(e.getKeyCode() == KeyEvent.VK_RIGHT) { 
				player.setLeft(false);
			} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
				player.setLeft(true);
			} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
				player.setCurrentState(STATE.CROUCH);
			} else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				player.setCurrentState(STATE.SHOOT);
			} else if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
				player.setCurrentState(STATE.KNIFE);
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() != KeyEvent.VK_DOWN) { 
				player.setCurrentState(STATE.IDLE);
			} // Do nothing if VK_DOWN
		}
		@Override
		public void keyTyped(KeyEvent e) {} // not used
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();

			if(source == timer) { // update animations and score (happens aprox 60 times per second)
				drawPanel.repaint();
				scoreLbl.setText("SCORE: " + player.getScore());
			} else {
				SoundBoard.play(SOUNDS.BTN_SOUND); // play btn sound on btn press
				if(source == quitButton) {   
					System.exit(0); // exit
				} else if(source == restartButton) { 
					restart(); // restart game
				} else if(source == menuButton) {
					stopGame(); // stop game to return to menu
					menu.setVisible(true);
					frame.dispose();
				}
			}
		}
		public synchronized void stopGame() { // stops everything that is happening
			detection.kill(); // stop detection algorithm
			timer.stop(); // stop timer
			for(AbstractEnemyCharacter e : player.getCurrentLevel().getEnemies()) {
				for(AbstractEnemyBullet b : e.bullets) {
					b.stop(); // stop enemy bullets
				}
				e.stop(); // stop enemies
			}
			for(Bullet b : player.bullets) {
				b.stop(); // stop players bullet
			}
			player.stop(); // stop player
		}
		public void restart() { // restart game
			stopGame(); // stop what is happening first
			setPlayer(new PlayerCharacter()); // new player character
			player.getCurrentLevel().startEnemies(); // start the enemies
			timer.start(); // start timer
			new Thread(player).start(); // start player
			detection.alive = true;
			if(!detectionThread.isAlive()) {
				detectionThread.start(); // start detection algorithm
			}
			frame.setFocusable(true);
			frame.requestFocusInWindow(); // allow focus back in game
		}
		public void startGame() {
			if(frame == null) { // if then make new frame. else make existing frame visible
				frame = new JFrame("Shane's Side Scroller GUI");
				frame.setFocusable(true);
				frame.setResizable(false);	
				frame.addKeyListener(this); //add a keylistener		
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(this);
				frame.pack(); //resize frame appropriately for its content
				//positions frame in center of screen
				Point point = new Point((screenWidth/2)-(frame.getWidth()/2), (screenHeight/2)-(frame.getHeight()/2));
				frame.setLocation(point);
				frame.setVisible(true);
				frame.requestFocusInWindow();
			}
			frame.setVisible(true); 
		}
		
		private class GameDetection implements Runnable { // Inner Class that handles detections
			public boolean alive;

			GameDetection() {
				alive = true;
			}
			public synchronized void kill() {
				alive = false; // will cause thread to stop
			}

			@Override
			public void run() {
				while(alive) {
					int distance;

					if(!player.isAlive()) {
						if(player.hasWon() && player.nextLevel() == true) { // handles switching levels
							player.setWin(false); // new level, so no longer "won"
							player.setAlive(true); // player start again
						} else {
							kill(); // stop detection
							try {
								if(menu.getHighScores().checkIfScoreQualifies(player.getScore())) { // handles if player has received high score
									String input = JOptionPane.showInputDialog("Congratulations, you have made it on the High-Score Table!\nPlease enter your name:");
									menu.getHighScores().addNewScore(player.getScore(), input);
								} 
							} catch(NullPointerException e) {} // Database connection is slow for some reason
						}
					} else { // detection happens when player is alive
						for(AbstractEnemyCharacter enemy : player.getCurrentLevel().getEnemies()) { // for each enemy
							// Changes the behavior of enemies
							if(enemy.isAlive()) { // only if alive
								if(player.isAlive()) { 
									distance = enemy.getX() - player.getX(); 
									if(enemy.ENEMY_TYPE == 2) { // melee character needs different values.
										if(distance > -10 && distance < 30) { // > 0 in case player gets past melee enemy
											enemy.setCurrentState(STATE.SHOOT);
										} else if(distance < 1000){
											enemy.setCurrentState(STATE.MOVE);
											if(enemy.getX() < -100) {
												enemy.kill();
												enemy.stop(); // stop enemy if they run off screen
											}
										} else {
											enemy.setCurrentState(STATE.IDLE);
										}	
									} else { // enemies other than melee
										if(distance > 0 && distance < 300) {
											enemy.setCurrentState(STATE.SHOOT);
										} else if(distance < 700){
											enemy.setCurrentState(STATE.MOVE);
											if(enemy.getX() < -200) {
												enemy.kill();
												enemy.stop(); // stop enemy if they run off screen
											}
										} else {
											enemy.setCurrentState(STATE.IDLE);
										}	
									}
								} else {
									enemy.setCurrentState(STATE.IDLE); // otherwise idle state
								}		
							}
							
							// Check if player has knifed an enemy
							if(!enemy.isKilled() && player.isAlive() && player.getCurrentState() == STATE.KNIFE) {
								distance = enemy.getX() - player.getX();
								if(distance < PlayerCharacter.MELEE_DISTANCE) {
									enemy.kill(); // Kills enemy if knife hits them
									player.addToScore();	
									player.addToScore(); // + 200 score for knifing
								}
							}
							// Check if enemy has shot player
							if(!enemy.bullets.isEmpty()) { // only if bullets exist
								for(AbstractEnemyBullet enemyBullet : enemy.bullets) {
									distance = (enemyBullet.getX()+30) - player.getX();
									if(player.isAlive() && enemyBullet.isAlive()) { // only bother checking when player is alive 
										if(distance < 50 && distance > -5) {
											if(!(player.getAllowCrouch() && player.getCurrentState() == STATE.CROUCH)) { // only if player is crouching
												player.kill(); // kill player
												enemyBullet.kill(); // kill bullet
											}
										}
									}
								}
							}
						} // for loop exit
					}

					// Check if player has shot an enemy
					do {
						if(!player.bullets.isEmpty()) { // only if bullets exist
							for(Bullet playersBullet : player.bullets) {
								for(AbstractEnemyCharacter enemy : player.getCurrentLevel().getEnemies()) {
									if(!enemy.isKilled() && playersBullet.isAlive()) { // only bother checking when enemy is alive
										distance = enemy.getX()+55 - playersBullet.getX();
										if(distance < 30) {
											enemy.kill(); // Kill enemy (they were shot)
											playersBullet.kill(); // remove bullet
											player.addToScore();
										}
									}
								}
							}
						}
					} while(alive == false);
				}
				try {
					Thread.sleep(50); // wait a small moment before ending (in case needed to start back up quickly)
				} catch (InterruptedException e) {}
			}
		}
}