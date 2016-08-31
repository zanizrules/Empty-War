import org.junit.Assert;
import org.junit.Test;

public class CharacterStateTest {
	PlayerCharacter player = new PlayerCharacter();
	@Test
	public void testSetState() {
		Assert.assertEquals(STATE.IDLE, player.getCurrentState());
		player.setCurrentState(STATE.MOVE);
		Assert.assertEquals(STATE.MOVE, player.getCurrentState());
		player.setCurrentState(STATE.SHOOT);
		Assert.assertEquals(STATE.SHOOT, player.getCurrentState());
		player.setCurrentState(STATE.KNIFE);
		Assert.assertEquals(STATE.KNIFE, player.getCurrentState());
		player.setCurrentState(STATE.CROUCH);
		Assert.assertEquals(STATE.CROUCH, player.getCurrentState());
		player.stop();
		Assert.assertEquals(STATE.IDLE, player.getCurrentState());
		// Success indicates player states can switch run, shoot, knife, and crouch correctly. 
	}
}