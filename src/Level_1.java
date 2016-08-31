import java.util.ArrayList;
/**
 * The Level_1 class is a Game Level of LEVEL_NUM '1', it is the first level.
 * @author Shane Birdsall
 */
public class Level_1 extends Level {
	public Level_1() {
		super("level1",1, 3560, 6, 60, GameGui.PANEL_HEIGHT-8);
	}
	@Override
	public void initialiseEnemies() { // set up enemies, how many and where to spawn
		ArrayList<AbstractEnemyCharacter> enemyList = new ArrayList<AbstractEnemyCharacter>();
		for(int i = 0; i < NUM_OF_ENEMIES; i++) {
			if(i%2 == 0) { // add melee character
				enemyList.add(new MeleeEnemyCharacter((END_POINT- GameGui.PANEL_WIDTH) - ((i+1)*350))); // Not random
			} else // add shooting character
				enemyList.add(new EnemyCharacter((END_POINT- GameGui.PANEL_WIDTH) - ((i+1)*350))); // Not random
		}
		enemyList.add(new BossCharacter(END_POINT- GameGui.PANEL_WIDTH)); // add boss at end of level
		initialiseEnemies(enemyList);
	}
}