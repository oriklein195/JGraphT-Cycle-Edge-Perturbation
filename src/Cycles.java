import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;


public class Cycles {

	private Map<DefaultWeightedEdge, Integer> edgeToIntegerMap;
	private Map<Integer, DefaultWeightedEdge> integerToEdgeMap;
	private SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph;
	private PriorityQueue<EdgeCycleCount> pq;
	
	public Cycles(SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph) {
		edgeToIntegerMap = new HashMap<DefaultWeightedEdge, Integer>();
		integerToEdgeMap = new HashMap<Integer, DefaultWeightedEdge>();
		pq = new PriorityQueue<EdgeCycleCount>();
		this.graph = graph;
	}
	
	public List<BitSet> getCycles() {
		return new ArrayList<BitSet>();
	}
	
	public void populateEdgeToIntegerMap() {
		int edgeInteger = 0;
		for (DefaultWeightedEdge edge : graph.edgeSet()) {
			edgeToIntegerMap.put(edge, edgeInteger);
			integerToEdgeMap.put(edgeInteger, edge);
			EdgeCycleCount edgeCycle = new EdgeCycleCount(edge);
			pq.add(edgeCycle);
			edgeInteger++;
		}
		System.out.println(pq);
		//System.out.println(edgeToIntegerMap);
		//System.out.println(integerToEdgeMap);
	}
	
	public BitSet getCycleIteration() {
		BitSet output = new BitSet(graph.edgeSet().size()); // this is the number of bits in the BitSet
		Set<Integer> uDiscovered = new HashSet<Integer>(); // nodes that have been discovered while searching BFS from u
		Set<Integer> vDiscovered = new HashSet<Integer>(); // nodes that have been discovered while searching BFS from v
		
		DefaultWeightedEdge edgePQ = pq.poll().getDefaultWeightedEdge(); // edge with the min number of cycles at this point
		Integer u = graph.getEdgeSource(edgePQ);
		uDiscovered.add(u);
		Integer v = graph.getEdgeTarget(edgePQ);
		vDiscovered.add(v);
		boolean foundCycle = false;
		while (foundCycle == false) {
			// get edges incident on u
			Set<DefaultWeightedEdge> uNeighbors = graph.outgoingEdgesOf(u);
			// get the next edge to traverse with BFS (and thus also the next node)
			
			// get edges incident on v
			Set<DefaultWeightedEdge> vNeighbors = graph.outgoingEdgesOf(v);
		}
		// Once we've found the cycle, mark all the edges that are in this cycle in the BitSet output as true (1). All the 
		// other bits are false (0).
		
		return output;
	}
	
	/**
	 * Helper method that picks the best neighbor to explore in the next iteration of BFS. Finds the edge with the lowest 
	 * cycle count whose target node also hasn't been discovered yet. If the target node belongs to the discovered set
	 * of the opposite side (u when considering v, or v when considering u), then this edge is preferred.
	 */
	private DefaultWeightedEdge pickBestNeighbor(boolean isU, Set<DefaultWeightedEdge> uNeighbors, 
			Set<DefaultWeightedEdge> vNeighbors, Set<Integer> uDiscovered, Set<Integer> vDiscovered) {
		if (isU) {
			// iterate through uNeighbors
			for (DefaultWeightedEdge edge : uNeighbors) {
				// if the target of the edge is in vNeighbors
				Integer edgeTarget = graph.getEdgeTarget(edge);
				if (vDiscovered.contains(edgeTarget)) { // if this is an edge connecting to the opposite side
					// we've found a cycle, now need to gather the edges that are in the cycle
				} else {
					// return edge with the lowest cycle count
					int minCycleCount = 10000000;
					
				}
			}
		} else {
			
		}
	}
}
