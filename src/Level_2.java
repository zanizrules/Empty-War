import java.util.ArrayList;
/**
 * The Level_2 class is a Game Level of LEVEL_NUM '2', it is the second level.
 * @author Shane Birdsall
 */
public class Level_2 extends Level {
	public Level_2() {
		super("level2",2, 5895, 15, 80,GameGui.PANEL_HEIGHT-40);
	}
	@Override
	public void initialiseEnemies() {
		ArrayList<AbstractEnemyCharacter> enemyList = new ArrayList<AbstractEnemyCharacter>();
		AbstractEnemyCharacter e;
		int a;
		for(int i = 0; i < NUM_OF_ENEMIES; i++) { // x locations rely on i
			a = rand.nextInt(1000); // randomise what enemy/enemies should appear
			if(a < 400) { // single shooter
				e = new EnemyCharacter((END_POINT- GameGui.PANEL_WIDTH) -(i*300));
				e.addToY(-35);
				enemyList.add(e);
			} else if(a < 600) { // group of three shooters
				for(int j = 0; j < 3; j++) {
					e = new EnemyCharacter((END_POINT- GameGui.PANEL_WIDTH) -(i*300)- (j* 50));
					e.addToY(-35);
					enemyList.add(e);
				}
			} else { // single melee 
				e = new MeleeEnemyCharacter((END_POINT- GameGui.PANEL_WIDTH) -(i*300));
				e.addToY(-35);
				enemyList.add(e);
			}
			
		}
		e = new BossCharacter(END_POINT- GameGui.PANEL_WIDTH); // add boss at end
		e.addToY(-35);
		enemyList.add(e);
		for (int i = 0; i < 4; i++) { // add melee enemies around/before boss (adds difficulty)
			e = new MeleeEnemyCharacter(END_POINT- GameGui.PANEL_WIDTH - 100 * (1+i));
			e.addToY(-35);
			enemyList.add(e);
		}
		initialiseEnemies(enemyList);
	}
}