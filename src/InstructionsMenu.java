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
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
/**
 * The Menu class is used to show the main menu (entry point of the game).
 * @author Shane Birdsall
 */
public class InstructionsMenu extends JPanel implements ActionListener, Runnable {
	private static final long serialVersionUID = 1L;
	private Menu menu; // reference to main menu
	private static Icon background = new ImageIcon("Artwork/backgrounds/instruction.png");
	private DrawPanel imagePanel;
	private JButton btnReturn;
	private static JFrame frame;
	private Icon playerNormal = new ImageIcon("Artwork/player/playerNormal.png");
	protected Icon[] running = new ImageIcon[5]; // running images
	protected Icon[] shooting = new ImageIcon[7]; // shooting images
	protected Icon[] knifing = PlayerCharacter.getMeleeImages(); // knife images
	private Icon currentRun, currentShoot, currentCrouch, currentKnife;
	private boolean alive;
	private Timer timer;
	// classes to be run as threads
	private Running run = new Running();
	private Shooting shoot = new Shooting();
	private Knifing knife = new Knifing();
	private JLabel lbl;
	
	public InstructionsMenu(Menu menu) {
		super(new BorderLayout());
		currentRun = currentShoot = currentCrouch = currentKnife = playerNormal; // set all images to idle
		if(running[running.length-1] == null) { // set up running images
			for(int i = 0; i < running.length; i++) {
				running[i] = new ImageIcon("Artwork/player/running/runright"+(i+1)+".png");
			}
		}
		if(shooting[shooting.length-1] == null) { // set up shooting images
			for(int i = 0; i < shooting.length; i++) {
				shooting[i] = new ImageIcon("Artwork/player/shooting/shoot"+(i+1)+".png");
			}
		}
		this.menu = menu;
		alive = true;
		imagePanel = new DrawPanel(); // panel for drawing icons
		lbl = new JLabel(" How To Play ");
		lbl.setOpaque(false);
		lbl.setForeground(Color.RED.darker().darker().darker());
		try { // Set custom font
			lbl.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("font/blood.ttf")).deriveFont(Font.BOLD, 70));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		imagePanel.add(lbl);
		add(imagePanel, BorderLayout.CENTER);
		btnReturn = new JButton("Return");
		btnReturn.addActionListener(this);
		add(btnReturn, BorderLayout.SOUTH);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize(); //gets the dimensions for screen width and height to calculate center
		int screenHeight = dimension.height;
		int screenWidth = dimension.width;
		// create frame
		frame = new JFrame("INSTRUCTIONS");
		frame.setFocusable(true);
		frame.setResizable(false);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.pack(); //resize frame appropriately for its content
		//positions frame in center of screen
		Point point = new Point((screenWidth/2)-(frame.getWidth()/2), (screenHeight/2)-(frame.getHeight()/2));
		frame.setLocation(point);
		// start timer
		timer = new Timer(17, this);
		timer.start();
	}
	public void show() { // show instructions (start animations)
		frame.setVisible(true);
		startAnimations();
	}
	public void startAnimations() {
		this.alive = true;
		new Thread(this).start(); // start crouching
		run.alive = true;
		new Thread(run).start(); // start running
		shoot.alive = true;
		new Thread(shoot).start(); // start shooting
		knife.alive = true;
		new Thread(knife).start(); // start knifing
	}
	public void stopAnimations() {
		run.stop();
		knife.stop(); // stop all 4 animations
		shoot.stop();
		this.alive = false;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == timer) {
			imagePanel.repaint();
		} else if(source == btnReturn) {
			SoundBoard.play(SOUNDS.BTN_SOUND);
			frame.dispose();
			stopAnimations(); // stop animations, as returning to menu
			menu.setVisible(true);
		}
	}
	private class DrawPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		public DrawPanel() {
			super();
			setPreferredSize(new Dimension(Menu.PANEL_WIDTH, Menu.PANEL_HEIGHT));
		}
		public void paintComponent(Graphics g) { //can be used to draw whatever I want!
			Graphics2D g2 = (Graphics2D) g;
			super.paintComponent(g);
			// paint background, followed by 4 player characters doing different actions
			background.paintIcon(null, g2, 0, 0); 
			currentCrouch.paintIcon(null, g2, 87, 245);
			currentRun.paintIcon(null, g2, 87, 87);
			currentShoot.paintIcon(null, g2, 215, 85);
			currentKnife.paintIcon(null, g2, 215, 245);
		}
	}
	@Override
	public void run() { // used to animate crouching
		while(alive) {
			try {
				currentCrouch = playerNormal;
				Thread.sleep(500);
				currentCrouch = PlayerCharacter.crouch;
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
	}	
	private class Running implements Runnable { // used to animate running
		private boolean alive;
		public Running() {
			alive = true;
		}
		public void stop() {
			alive = false;
		}
		@Override
		public void run() {
			while(alive) {
				try {
					for(int i = 0; i < running.length; i++) {
						currentRun = running[i];
						Thread.sleep(75);
					}
				} catch (InterruptedException e) {}
			}
		}
	}
	private class Knifing implements Runnable { // used to animate knifing
		private boolean alive;
		public Knifing() {
			alive = true;
		}
		public void stop() {
			alive = false;
		}
		@Override
		public void run() {
			while(alive) {
				try {
					for(int i = 0; i < knifing.length; i++) {
						currentKnife = knifing[i];
						Thread.sleep(150);
					}
				} catch (InterruptedException e) {}
			}
		}
	}
	private class Shooting implements Runnable { // used to animate shooting
		private boolean alive;
		public Shooting() {
			alive = true;
		}
		public void stop() {
			alive = false;
		}
		@Override
		public void run() {
			while(alive) {
				try {
					for(int i = 0; i < shooting.length; i++) {
						currentShoot = shooting[i];
						Thread.sleep(80);
					}
				} catch (InterruptedException e) {}
			}
		}
	}
}