import java.util.ArrayList;
import java.util.Collections;
/**
 * The ScoreArrayList class defines an ArrayList with a set size.
 * @author Shane Birdsall
 */
public class ScoreArrayList extends ArrayList<Score> {
	private static final long serialVersionUID = 1L;
	public final int ARRAY_SIZE;
	public ScoreArrayList(int size) {
		super();
		ARRAY_SIZE = size; // Max amount of scores
	}
	public boolean add(Score e) {
		boolean bol = false;
		if(this.size() < ARRAY_SIZE) { // fine to add (not full)
			bol = super.add(e);
			Collections.sort(this);
		} else if(e.getScore() > get(ARRAY_SIZE-1).getScore()) { // full but score qualifies
			remove(size()-1); // remove last score
			bol = add(e);
			Collections.sort(this); // sort after adding new score
		}
		return bol;
	}
}