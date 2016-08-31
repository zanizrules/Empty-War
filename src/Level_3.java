import java.util.ArrayList;
/**
 * The Level_3 class is a Game Level of LEVEL_NUM '3', it is the third level.
 * @author Shane Birdsall
 */
public class Level_3 extends Level {
	public Level_3() {
		super("level3",3, 5280, 25, 90, GameGui.PANEL_HEIGHT-43);
	}
	@Override
	public void initialiseEnemies() {
		ArrayList<AbstractEnemyCharacter> enemyList = new ArrayList<AbstractEnemyCharacter>();
		AbstractEnemyCharacter e;
		for(int i = 0; i < NUM_OF_ENEMIES; i++) { // add shooters
			e = new EnemyCharacter(END_POINT- GameGui.PANEL_WIDTH -(i*150));
			e.addToY(-35);
			enemyList.add(e);
		}
		e = new BossCharacter((END_POINT- GameGui.PANEL_WIDTH)/2); // add mid-way boss
		e.addToY(-35);
		enemyList.add(e);
		for (int i = 0; i < 12; i++) { // add huge swarm of melee characters around boss
			e = new MeleeEnemyCharacter(((END_POINT- GameGui.PANEL_WIDTH)/2) + 100*(i+1));
			e.addToY(-35);
			enemyList.add(e);
		}
		e = new BossCharacter(END_POINT- GameGui.PANEL_WIDTH); // add boss at end of level
		e.addToY(-35);
		enemyList.add(e);
		initialiseEnemies(enemyList);
	}
}