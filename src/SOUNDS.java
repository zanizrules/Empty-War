/**
 * The SOUNDS ENUM stores every game sound, and connects the file name of the .WAV file to a descriptor.
 * @author Shane Birdsall
 */
public enum SOUNDS {
	BTN_SOUND("buttonClick"), BOSS_DEATH("bossDeath"), BOSS_SHOOT("bossFire"), ENEMY_DEATH("enemyDeath"), ENEMY_SCREAM("kamikaze"),
	KNIFE("knife"), MISSION_COMPLETE("missionComplete"), OUCH("ouch"), PISTOL1("pistolFire"), PISTOL2("pistolFire2"), 
	PLAYER_DEATH("playerDeath"), SCREAM("scream"), BOSS_LAUGH("bossLaugh");
	final String filename;
	SOUNDS(String file) {
		filename = file; // assign filename
	}
}