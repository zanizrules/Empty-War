import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
/**
 * The HighScorePanel class is used to show the high score table.
 * @author Shane Birdsall
 */
public class HighScorePanel extends JPanel implements ActionListener, Runnable {
	private static final long serialVersionUID = 1L;
	private static Icon background = new ImageIcon("Artwork/backgrounds/highScoreBG.png");
	private static JFrame frame;
	private JPanel imagePanel;
	private JTextPane textPane;
	private JButton btnReturn;
	private Menu menu;
	private ScoreArrayList scores;
	public final static int MAX_AMOUNT_OF_HIGHSCORES = 10; // only ten highs cores allowed
	private ScoreDatabaseConnection databaseConn;
	private boolean finished;
	
	public HighScorePanel(Menu menu) {
		super(new BorderLayout());
		finished = false;
		new Thread(this).start();
		this.menu = menu;
		textPane = new JTextPane();
		try { // Set custom font
			textPane.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("font/arcade.ttf")).deriveFont(Font.PLAIN, 14));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		// setup text pane
		textPane.setPreferredSize(new Dimension(295, 300));
		textPane.setEditable(false);
		textPane.setOpaque(false);
		
		// setup image panel
		imagePanel = new DrawPanel();
		imagePanel.add(textPane, BorderLayout.CENTER);
		add(imagePanel, BorderLayout.CENTER);
		btnReturn = new JButton("Return"); 
		btnReturn.addActionListener(this);
		add(btnReturn, BorderLayout.SOUTH); // add return button

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize(); //gets the dimensions for screen width and height to calculate center
		int screenHeight = dimension.height;
		int screenWidth = dimension.width;

		// create frame
		frame = new JFrame("High Scores!");
		frame.setFocusable(true);
		frame.setResizable(false);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.pack(); //resize frame appropriately for its content
		//positions frame in center of screen
		Point point = new Point((screenWidth/2)-(frame.getWidth()/2), (screenHeight/2)-(frame.getHeight()/2));
		frame.setLocation(point);
	}
	public void show() { // show frame
		frame.setVisible(true);
	}
	public boolean isFinished() { // check if database activity is finished
		return finished;
	}
	public void updateScores() {
		if(scores.isEmpty()) { // get scores from DB
			databaseConn.establishConnection();
			scores = databaseConn.getAllHighScores();
			databaseConn.closeConnections();
		}
		String scoreText = "\n";
		for(Score i : scores) {
			scoreText += "\n\t    "+i.getName() +": " +i.getScore();
		}
		textPane.setText(scoreText);  // add scores to text pane
	}
	public void addNewScore(int score, String name) { // add new score to scores and DB
		scores.add(new Score(name, score));
		databaseConn.establishConnection();
		databaseConn.addHighScores(scores);
		databaseConn.closeConnections();
	}
	public boolean checkIfScoreQualifies(int score) { // see if score is good enough 
		if(scores.size() < MAX_AMOUNT_OF_HIGHSCORES || score > scores.get(MAX_AMOUNT_OF_HIGHSCORES-1).getScore()) {
			return true;
		} return false;
	}
	public void setInitialScores() { // used for a completely new/empty score DB
		ScoreArrayList scores = new ScoreArrayList(MAX_AMOUNT_OF_HIGHSCORES);
		for(int i = 0; i < MAX_AMOUNT_OF_HIGHSCORES; i++) {
			scores.add(new Score("USER"+(i+1), 100)); // 10 users with 100 score
		}
		databaseConn.establishConnection();
		databaseConn.addHighScores(scores);
		databaseConn.closeConnections(); // close conn
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == btnReturn) {
			SoundBoard.play(SOUNDS.BTN_SOUND);
			frame.dispose();
			menu.setVisible(true);
		}
	}
	private class DrawPanel extends JPanel { // used to draw background
		private static final long serialVersionUID = 1L;
		public DrawPanel() {
			super();
			setPreferredSize(new Dimension(Menu.PANEL_WIDTH ,Menu.PANEL_HEIGHT));
		}
		public void paintComponent(Graphics g) { //can be used to draw whatever I want!
			Graphics2D g2 = (Graphics2D) g;
			super.paintComponent(g);
			background.paintIcon(null, g2, 0, 0);
		}
	}
	@Override
	public void run() { // establish and connect to database
		databaseConn = new ScoreDatabaseConnection();
		databaseConn.establishConnection();
		databaseConn.createTable();
		scores = databaseConn.getAllHighScores();
		if(scores.isEmpty()) {
			 setInitialScores();
		}
		databaseConn.closeConnections();
		finished = true;
	}	
}
