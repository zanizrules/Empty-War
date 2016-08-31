/**
 * A Score object represents a players score (High Score), it contains the players name and score.
 * @author Shane Birdsall
 */
public class Score implements Comparable<Score>{
	private String name;
	private int score;
	
	public Score(String n, int s) {
		score = s;
		setName(n);
	}
	private void setName(String n) {
		if(n != null) {
			n = n.trim(); // remove whitespace
			if(n.length() > 7) { // limit string to a length of 7
				n = n.substring(0, 7); // only want first 7 characters
			}
			if(n.isEmpty()) {
				name = "UNKNOWN"; // name is empty after removing whitespace
			} else {
				name = n.toUpperCase(); // make names upper-case
			}
		} else {
			name = "UNKNOWN"; // it was null so now make it unknown
		}
	}
	public int getScore() {
		return score;
	}
	public String getName() {
		return name;
	}
	@Override
	public int compareTo(Score o) {
		return o.getScore() - score;
	}
}