import org.jgrapht.graph.DefaultWeightedEdge;


public class CustomWeightedEdge extends DefaultWeightedEdge {
	
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
	
	
}
