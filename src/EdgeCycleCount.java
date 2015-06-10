
public class EdgeCycleCount implements Comparable<EdgeCycleCount> {
	
	private int cycleCount;
	CustomWeightedEdge CustomWeightedEdge;

	public EdgeCycleCount(CustomWeightedEdge CustomWeightedEdge) {
		cycleCount = 0;
		this.CustomWeightedEdge = CustomWeightedEdge;
	}
	
	public void increaseCycleCount() {
		cycleCount++;
	}
	
	public int getCycleCount() {
		return cycleCount;
	}
	
	public CustomWeightedEdge getCustomWeightedEdge() {
		return CustomWeightedEdge;
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
		return CustomWeightedEdge + " - " + cycleCount;
	}
}
