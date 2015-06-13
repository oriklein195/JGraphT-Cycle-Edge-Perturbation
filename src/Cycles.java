import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;


public class Cycles {

	private Map<CustomWeightedEdge, Integer> edgeToIntegerMap;
	private Map<Integer, CustomWeightedEdge> integerToEdgeMap;
	private SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph;
	private PriorityQueue<CustomWeightedEdge> pq;
	private Map<Integer, BitSet> nodeToBitSet;
	private List<BitSet> cycles;
	
	// Constructor for Cycles class.
	public Cycles(SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph) {
		edgeToIntegerMap = new HashMap<CustomWeightedEdge, Integer>();
		integerToEdgeMap = new HashMap<Integer, CustomWeightedEdge>();
		pq = new PriorityQueue<CustomWeightedEdge>();
		nodeToBitSet = new HashMap<Integer, BitSet>();
		this.graph = graph;
		cycles = new ArrayList<BitSet>();
		populateEdgeToIntegerMap();
	}
	
	public List<BitSet> getCycles(int minCycleCount) {
		int iteration = 0;
		int numRepeatedCycles = 0;
		System.out.println("integerToEdgeMap: " + integerToEdgeMap);
		System.out.println();
		while (pq.peek().getCycleCount() < minCycleCount) {
			iteration++;
			/*if (iteration == 20) {
				break;
			}*/
			System.out.println("ITERATION: " + iteration);
			BitSet newCycle = getOneCycle();
			System.out.println("Priority Queue: " + pq);
			System.out.println("cycles: " + cycles.size());
			if (newCycle.isEmpty()) {
				System.out.println("Got stuck in a corner. Surrounded by discovered nodes.");
			} else if (!cycles.contains(newCycle)) { // if this newly found cycle isn't already in the list of cycles
				cycles.add(newCycle);
			} else {
				System.out.println("Ignored repeat cycle.");
				numRepeatedCycles++;
			}
			System.out.println();
		}
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println("Priority Queue: " + pq);
		System.out.println("Cycles: " + cycles);
		System.out.println("Each edge is part of at least " + minCycleCount + " cycles.");
		int cycleSum = 0;
		int maxCycleLength = 0;
		for (BitSet cycle : cycles) {
			cycleSum += cycle.cardinality();
			if (cycle.cardinality() > maxCycleLength) {
				maxCycleLength = cycle.cardinality();
			}
		}
		double averageCycleLength = (double) cycleSum / (double) cycles.size();
		System.out.println("The average cycle length is " + averageCycleLength + ".");
		System.out.println("The max cycle length is " + maxCycleLength);
		System.out.println("We found " + cycles.size() + " cycles.");
		System.out.println("There were " + numRepeatedCycles + " repeated cycles.");
		return cycles;
	}
	
	// Private helper method for the constructor, maps each edge to its assigned edge number
	private void populateEdgeToIntegerMap() {
		int edgeInteger = 0;
		for (CustomWeightedEdge edge : graph.edgeSet()) {
			edgeToIntegerMap.put(edge, edgeInteger);
			integerToEdgeMap.put(edgeInteger, edge);
			pq.add(edge);
			edgeInteger++;
		}
	}
	
	// Once we've found the cycle, for each bit set to true in the output, increment the corresponding edge's 
	// cycle count *IF* it is a new cycle.
	private void updatePQCycleCount(BitSet cycle) { // already checked that cycle is not already in cycles
		for (int i = cycle.nextSetBit(0); i >= 0; i = cycle.nextSetBit(i + 1)) {
			CustomWeightedEdge edge = integerToEdgeMap.get(i);
			pq.remove(edge); // remove, update, and add to the priority queue each edge in the discovered cycle
			edge.increaseCycleCount();
			pq.add(edge);
		}
	}
	
	public BitSet getOneCycle() {
		BitSet output = new BitSet(graph.edgeSet().size()); // this is the number of bits in the BitSet
		Set<Integer> uDiscovered = new HashSet<Integer>(); // nodes that have been discovered while searching BFS from u
		Set<Integer> vDiscovered = new HashSet<Integer>(); // nodes that have been discovered while searching BFS from v
		
		CustomWeightedEdge edgePQ = pq.peek(); // edge with the min number of cycles at this point
		System.out.println("edgePQ: " + edgePQ);
		
		BitSet uBitSet = new BitSet(graph.edgeSet().size()); // set of edges traversed on u's side
		BitSet vBitSet = new BitSet(graph.edgeSet().size()); // set of edges traversed on v's side
		
		Integer u = graph.getEdgeSource(edgePQ);
		System.out.println("u: " + u);
		uDiscovered.add(u);
		nodeToBitSet.put(u, (BitSet) uBitSet.clone());
		Integer v = graph.getEdgeTarget(edgePQ);
		System.out.println("v: " + v);
		vDiscovered.add(v);
		nodeToBitSet.put(v, (BitSet) vBitSet.clone());
		System.out.println("uDiscovered: " + uDiscovered);
		System.out.println("vDiscovered: " + vDiscovered);
		System.out.println("nodeToBitSet: " + nodeToBitSet);
		
		while (true) {
			// get edges incident on u
			Set<CustomWeightedEdge> uNeighbors = graph.incomingEdgesOf(u);
			// get edges incident on v
			Set<CustomWeightedEdge> vNeighbors = graph.outgoingEdgesOf(v);

			// PICK BEST NEIGHBOR FOR U. (Look at incoming edges)
			int uMinCycleCount = 10000000;
			List<CustomWeightedEdge> uMinCycleCountEdges = new ArrayList<CustomWeightedEdge>();
			for (CustomWeightedEdge edge : uNeighbors) {
				Integer edgeSource = graph.getEdgeSource(edge);
				System.out.println("u: " + edge);
				if (uDiscovered.contains(edgeSource) || graph.inDegreeOf(edgeSource) == 1) { // OR if edgeSource inDegree = 1
					System.out.println("Edge has been discovered already by u.");
					continue;
				}
				// if the target of the edge is in uNeighbors
				if (vDiscovered.contains(edgeSource)) { // if this is an edge connecting to the opposite side
					// we've found a cycle, now need to gather the edges that are in the cycles
					// OR the bitsets for each side + edgeInteger for edgePQ + edgeInteger for crossEdge edge
					output.or(uBitSet);
					output.or(nodeToBitSet.get(edgeSource));
					output.set(edgeToIntegerMap.get(edgePQ));
					output.set(edgeToIntegerMap.get(edge));
					if (output.cardinality() > 2) { 
						if (!cycles.contains(output)) { // AND cycle hasn't been found already
							System.out.println("Found a cycle for u with length 3 or greater!");
							System.out.println("Cycle: " + output);
							/*System.out.println("uBitSet: " + uBitSet);
							System.out.println("edgeTarget: " + nodeToBitSet.get(edgeSource));
							System.out.println("edgePQ: " + edgeToIntegerMap.get(edgePQ));
							System.out.println("edge: " + edgeToIntegerMap.get(edge));*/
							updatePQCycleCount(output);
							return output; // exit the while loop
						} else { // if cycle has already been found
							System.out.println("Duplicate cycle. Continuing.");
							output.clear();
							continue;
						}
					} else { // if we found a cycle of length 2 (as in the first case), then we clear the output BitSet
						// and continue searching
						output.clear();
					}
				} else {
					// return edge with the lowest cycle count. If there's a tie, *RANDOMLY* choose an edge.
					if (edge.getCycleCount() < uMinCycleCount) { // reset the uMinCycleCountEdges, update uMinCycleCount
						uMinCycleCount = edge.getCycleCount();
						uMinCycleCountEdges.clear();
						uMinCycleCountEdges.add(edge);
					} else if (edge.getCycleCount() == uMinCycleCount) {
						uMinCycleCountEdges.add(edge);
					}
				}
			}
			//if (graph.getEdgeSource(uMinCycleCountEdge) != null && graph.getEdgeTarget(uMinCycleCountEdge) != null) {'
			if (uMinCycleCountEdges.size() > 0) { // if there exists at least one edge in uMinCycleCountEdges
				// randomly choose one of the edges in uMinCycleCountEdges
				int uRandomIndex = new Random().nextInt(uMinCycleCountEdges.size());
				CustomWeightedEdge uMinCycleCountEdge = uMinCycleCountEdges.get(uRandomIndex);
				Integer uDiscoveredNode = graph.getEdgeSource(uMinCycleCountEdge);
				System.out.println("Chosen u edge: " + uMinCycleCountEdge);
				uDiscovered.add(uDiscoveredNode);
				uBitSet.set(edgeToIntegerMap.get(uMinCycleCountEdge));
				u = uDiscoveredNode;
				nodeToBitSet.put(u, (BitSet) uBitSet.clone());
			} else {
				//System.out.println("Edge is null");
				return new BitSet(); // return an empty BitSet, which will be ignored in getCycles().
			}
			
			//System.out.println("v: " + v);
			// PICK BEST NEIGHBOR FOR V. (Look at outgoing edges)
			int vMinCycleCount = 10000000;
			List<CustomWeightedEdge> vMinCycleCountEdges = new ArrayList<CustomWeightedEdge>();
			for (CustomWeightedEdge edge : vNeighbors) {
				System.out.println("v: " + edge);
				Integer edgeTarget = graph.getEdgeTarget(edge);
				if (vDiscovered.contains(edgeTarget) || graph.inDegreeOf(edgeTarget) == 1) {
					System.out.println("Edge has been discovered already by v.");
					continue;
				}
				// if the target of the edge is in uNeighbors
				if (uDiscovered.contains(edgeTarget)) { // if this is an edge connecting to the opposite side
					// we've found a cycle, now need to gather the edges that are in the cycles
					// OR the bitsets for each side + edgeInteger for edgePQ + edgeInteger for crossEdge edge
					output.or(vBitSet);
					output.or(nodeToBitSet.get(edgeTarget));
					output.set(edgeToIntegerMap.get(edgePQ));
					output.set(edgeToIntegerMap.get(edge)); // I *think* this is the problem. 
					//System.out.println("output: " + output);
					if (output.cardinality() > 2) {
						if (!cycles.contains(output)) { // AND cycle hasn't been found already
							System.out.println("Found a cycle for v with length 3 or greater!");
							System.out.println("Cycle: " + output);
							/*System.out.println("vBitSet: " + vBitSet);
							System.out.println("edgeTarget: " + nodeToBitSet.get(edgeTarget));
							System.out.println("edgePQ: " + edgeToIntegerMap.get(edgePQ));
							System.out.println("edge: " + edgeToIntegerMap.get(edge));*/
							updatePQCycleCount(output);
							return output; // exit the while loop
						} else { // if cycle has already been found
							System.out.println("Duplicate cycle. Continuing.");
							output.clear(); // reset the output
							continue;
						}
					} else {
						output.clear(); // reset the output
						//System.out.println("output: " + output);
					}
				} else {
					// return edge with the lowest cycle count
					if (edge.getCycleCount() < vMinCycleCount) { // reset the vMinCycleCountEdges, update vMinCycleCount
						vMinCycleCount = edge.getCycleCount();
						vMinCycleCountEdges.clear();
						vMinCycleCountEdges.add(edge);
					} else if (edge.getCycleCount() == vMinCycleCount) {
						vMinCycleCountEdges.add(edge);
					}
				}
			}
			//if (graph.getEdgeSource(vMinCycleCountEdge) != null && graph.getEdgeTarget(vMinCycleCountEdge) != null) {
			if (vMinCycleCountEdges.size() > 0) { // if there exists at least one edge in vMinCycleCountEdges
				// randomly choose one of the edges in vMinCycleCountEdges
				int vRandomIndex = new Random().nextInt(vMinCycleCountEdges.size());
				CustomWeightedEdge vMinCycleCountEdge = vMinCycleCountEdges.get(vRandomIndex);
				Integer vDiscoveredNode = graph.getEdgeTarget(vMinCycleCountEdge);
				System.out.println("Chosen v edge: " + vMinCycleCountEdge);
				vDiscovered.add(vDiscoveredNode);
				vBitSet.set(edgeToIntegerMap.get(vMinCycleCountEdge));
				v = vDiscoveredNode;
				nodeToBitSet.put(v, (BitSet) vBitSet.clone());
			} else {
				//System.out.println("Edge is null.");
				return new BitSet(); // return an empty BitSet, which will be ignored in getCycles().
			}
			
			System.out.println("uDiscovered: " + uDiscovered);
			System.out.println("vDiscovered: " + vDiscovered);
			System.out.println("nodeToBitSet: " + nodeToBitSet);
		}
	}
	
	public boolean verifyCycles(List<BitSet> cycles) {
		for (BitSet cycle : cycles) {
			for (int i = cycle.nextSetBit(0); i >= 0; i = cycle.nextSetBit(i + 1)) {
				CustomWeightedEdge edge = integerToEdgeMap.get(i);
				System.out.println(edge);
			}
			System.out.println();
		}
		return true;
	}
}
