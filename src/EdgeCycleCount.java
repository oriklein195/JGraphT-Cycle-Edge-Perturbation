import org.jgrapht.graph.DefaultWeightedEdge;


public class EdgeCycleCount implements Comparable<EdgeCycleCount> {
	
	private int cycleCount;
	DefaultWeightedEdge defaultWeightedEdge;

	public EdgeCycleCount(DefaultWeightedEdge defaultWeightedEdge) {
		cycleCount = 0;
		this.defaultWeightedEdge = defaultWeightedEdge;
	}
	
	public void increaseCycleCount() {
		cycleCount++;
	}
	
	public int getCycleCount() {
		return cycleCount;
	}
	
	public DefaultWeightedEdge getDefaultWeightedEdge() {
		return defaultWeightedEdge;
	}
	
	@Override
	public int compareTo(EdgeCycleCount otherEdge) {
		if (this.getCycleCount() < otherEdge.getCycleCount()) {
			return -1;
		} else {
			return 1;
		}
	}
	
	@Override
	public String toString() {
		return defaultWeightedEdge + " - " + cycleCount;
	}
}
