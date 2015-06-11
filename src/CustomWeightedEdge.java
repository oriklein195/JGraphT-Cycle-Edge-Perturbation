import java.util.Random;

import org.jgrapht.graph.DefaultWeightedEdge;


public class CustomWeightedEdge extends DefaultWeightedEdge implements Comparable<CustomWeightedEdge> {
	

	private static final long serialVersionUID = 1L;
	private int cycleCount;
	
	public CustomWeightedEdge() {
		cycleCount = 0;
	}
	
	public int getCycleCount() {
		return cycleCount;
	}
	
	public void increaseCycleCount() {
		cycleCount++;
	}
	
	/**
	 * Compares the cycle count of the CustomWeightedEdges
	 */
	@Override
	public int compareTo(CustomWeightedEdge otherEdge) {
		if (this.getCycleCount() < otherEdge.getCycleCount()) {
			return -1;
		} else if (this.getCycleCount() > otherEdge.getCycleCount()) {
			return 1;
		}
		// Choose randomly.
		Random rand = new Random();
		int val = 100 - rand.nextInt(201); // random int between 0 - 200 inclusive
		return val;
	}
	
	@Override
	public String toString() {
		return super.toString() + " - " + cycleCount;
	}
	
}
