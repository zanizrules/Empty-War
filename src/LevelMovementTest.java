import static org.junit.Assert.*;
import org.junit.Test;

public class LevelMovementTest {
	Level exampleLevel = new Level_1();
	@Test
	public void testMoveBackground() {
		assertEquals(0, exampleLevel.getX());
		exampleLevel.moveBackground(false);
		assertEquals(-Level.MOVE_DISTANCE, exampleLevel.getX());
		exampleLevel.moveBackground(50);
		assertEquals(-Level.MOVE_DISTANCE-50, exampleLevel.getX());
		
		exampleLevel.moveX(exampleLevel.END_POINT - GameGui.PANEL_WIDTH);
		assertFalse(exampleLevel.moveBackground(false));
		exampleLevel.moveX(-exampleLevel.END_POINT + GameGui.PANEL_WIDTH);
		assertTrue(exampleLevel.moveBackground(false));
	} // success indicates level movement works fine
}