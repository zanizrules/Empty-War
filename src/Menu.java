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
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
/**
 * The Menu class is used to show the main menu (entry point of the game).
 * @author Shane Birdsall
 */
public class Menu extends JFrame implements ActionListener, Runnable {
	private static final long serialVersionUID = 1L;
	public final static int PANEL_WIDTH = 600; // define size of window
	public final static int PANEL_HEIGHT = 375;
	private static Icon background = new ImageIcon("Artwork/backgrounds/menuBackground.png");
	private JPanel buttonPanel, imagePanel;
	private JButton quitBtn, startBtn, highScoreBtn, helpBtn;
	private JLabel gameLabel; // game name label
	private GameGui game; // reference to game window
	private HighScorePanel highScores; // reference to high score window
	private InstructionsMenu help; // reference to instructions window
	
	public Menu() {
		super("Shane's Side Scrolling Shooter");
		new Thread(this).start();
		// Game buttons
		startBtn = new JButton("Start");
		startBtn.addActionListener(this);
		quitBtn = new JButton("Quit");
		quitBtn.addActionListener(this);
		highScoreBtn = new JButton("High Scores");
		highScoreBtn.addActionListener(this);
		helpBtn = new JButton("Instructions");
		helpBtn.addActionListener(this);
		// add label
		gameLabel = new JLabel(" Shane's Game! ");
		gameLabel.setOpaque(false);
		gameLabel.setForeground(Color.RED.darker().darker().darker());
		try { // Set custom font
			gameLabel.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("font/blood.ttf")).deriveFont(Font.BOLD, 70));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}

		//Borders 
		Border a = BorderFactory.createRaisedBevelBorder();
		Border b = BorderFactory.createLoweredBevelBorder();
		Border c = BorderFactory.createCompoundBorder(a, b);
		// set up buttons on a panel
		buttonPanel = new JPanel();
		buttonPanel.setBorder(c);
		buttonPanel.setBackground(Color.RED.darker().darker().darker().darker().darker());
		buttonPanel.add(startBtn);
		buttonPanel.add(helpBtn);
		buttonPanel.add(highScoreBtn);
		buttonPanel.add(quitBtn);
		
		imagePanel = new DrawPanel(); // set up panel for background
		imagePanel.add(gameLabel);
		getContentPane().add(imagePanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		setFocusable(true);
		setResizable(false);	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack(); //resize frame appropriately for its content
		//positions frame in center of screen
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize(); //gets the dimensions for screen width and height to calculate center
		int screenHeight = dimension.height;
		int screenWidth = dimension.width;
		Point point = new Point((screenWidth/2)-(getWidth()/2), (screenHeight/2)-(getHeight()/2));
		setLocation(point);
		setVisible(true);
	}
	private class DrawPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		public DrawPanel() {
			super();
			setBackground(Color.BLACK); // looks better incase of any flickers
			setPreferredSize(new Dimension(PANEL_WIDTH ,PANEL_HEIGHT));
		}
		public void paintComponent(Graphics g) { //can be used to draw whatever I want!
			Graphics2D g2 = (Graphics2D) g;
			super.paintComponent(g);
			background.paintIcon(null, g2, 0, 0);
		}
	}	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		SoundBoard.play(SOUNDS.BTN_SOUND); // button clicked sound
		if(source == quitBtn) {   
			System.exit(0); // exit
		} else if(source == startBtn) { 
			setVisible(false); // hide menu screen
			if(game == null) {
				game = new GameGui(this); // create game
			}
			game.startGame(); // start game
			game.restart(); // starts up everything (detection, timers, etc)
		} else if(source == highScoreBtn) {
			if(highScores == null || !highScores.isFinished()) { // show wait message (my way of handling errors)
				JOptionPane.showMessageDialog(this, "Sorry but the HighScore table is still loading!\nPlease check back soon...");
			} else { // show high score table
				setVisible(false);
				highScores.updateScores();
				highScores.show();
			}
		} else if(source == helpBtn) { // show instructions
			setVisible(false);
			help.show();
		}
	}
	public HighScorePanel getHighScores() {
		return highScores;
	}
	public static void main(String[] args) {
		// very short main, all that needs to happen is a menu object be created.
		// From then on with the help of the user everything else will be handled from class constructors & methods etc.
				new Menu();
	}
	@Override
	public void run() { // create instructions and highScores
		help = new InstructionsMenu(this);
		highScores = new HighScorePanel(this);
	}
}