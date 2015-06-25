import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;


public class Cycles {

	private Map<CustomWeightedEdge, Integer> edgeToIntegerMap;
	private Map<Integer, CustomWeightedEdge> integerToEdgeMap;
	private SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph;
	private PriorityQueue<CustomWeightedEdge> pq;
	private Map<Integer, BitSet> nodeToBitSet;
	private List<BitSet> cycles;
	private List<CustomWeightedEdge> removedEdges;
	private int numRepeatedCycles;
	private static long startTime = System.currentTimeMillis();
	
	// Constructor for Cycles class.
	public Cycles(Graph graph) {
		edgeToIntegerMap = new HashMap<CustomWeightedEdge, Integer>();
		integerToEdgeMap = new TreeMap<Integer, CustomWeightedEdge>();
		pq = new PriorityQueue<CustomWeightedEdge>();
		nodeToBitSet = new HashMap<Integer, BitSet>();
		this.graph = graph.getGraph();
		cycles = new ArrayList<BitSet>();
		removedEdges = new ArrayList<CustomWeightedEdge>();
		numRepeatedCycles = 0;
		populateEdgeToIntegerMap();
	}
	
	public List<BitSet> getCycles(int minCycleCount) {
		int iteration = 0;
		//System.out.println("integerToEdgeMap: " + integerToEdgeMap);
		while (pq.peek().getCycleCount() < minCycleCount) {
			iteration++;
			/*if (iteration == 2000) {
				break;
			}*/
			//System.out.println("ITERATION: " + iteration);
			// poll from the PQ
			CustomWeightedEdge edgePQ = pq.poll(); // edge with the min number of cycles at this point
			//System.out.println("edgePQ: " + edgePQ);
			if (edgePQ.getStuckCount() > 100000) {
				// Don't find the cycle and remove the edge
				//System.out.println("Removed edgePQ from PQ b/c stuckCount > 10.");
				//System.out.println();
				removedEdges.add(edgePQ);
				continue;
			}
			BitSet newCycle = getOneCycle(edgePQ);
			// add back edgePQ to the PQ.
			pq.add(edgePQ);
			//System.out.println("Priority Queue: " + pq);
			//System.out.println("cycles: " + cycles.size());
			if (newCycle.isEmpty()) {
				//System.out.println("Got stuck in a corner. Surrounded by discovered nodes.");
			} else if (!cycles.contains(newCycle)) { // if this newly found cycle isn't already in the list of cycles
				cycles.add(newCycle);
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println("It took " + ((endTime - startTime) / 1000L) + " seconds");
		System.out.println("M: " + minCycleCount);
		System.out.println("Number of Iterations: " + iteration);
		//System.out.println("Cycles: " + cycles.size() + " - " + cycles);
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
		System.out.println("Encountered " + numRepeatedCycles + " repeated cycles." + " Algorithm continued to find larger "
				+ "cycles.");
		//System.out.println("Priority Queue: " + pq);
		System.out.println("Removed " + removedEdges.size() + " edges: " + removedEdges);
		System.out.println();
		return cycles;
	}
	
	public void printCycleLengthHistogram(List<BitSet> cycles) {
		Map<Integer, Integer> histogram = new TreeMap<Integer, Integer>();
		for (BitSet cycle : cycles) {
			int cycleLength = cycle.cardinality();
			if (histogram.containsKey(cycleLength)) {
				int numCyclesWithLength = histogram.get(cycleLength);
				histogram.put(cycleLength, numCyclesWithLength + 1);
			} else {
				histogram.put(cycleLength, 1);
			}
		}
		for (Integer cycleLength : histogram.keySet()) {
			System.out.println(cycleLength + "\t" + histogram.get(cycleLength));
		}
	}
	
	public void printHistogram() {
		// process PQ
		// process removed edges
		// first make sure that they add up to 1308. **
		Map<Integer, Integer> histogram = new TreeMap<Integer, Integer>();
		System.out.println(pq.size());
		System.out.println(removedEdges.size());
		for (CustomWeightedEdge edge : pq) {
			int cycleCount = edge.getCycleCount();
			if (!histogram.containsKey(cycleCount)) {
				// create a new key with value 1
				histogram.put(cycleCount, 1);
			} else {
				// add 1 to the value of the existing key
				int existingValue = histogram.get(cycleCount);
				histogram.put(cycleCount, existingValue + 1);
			}
		}
		for (CustomWeightedEdge removedEdge : removedEdges) {
			int cycleCount = removedEdge.getCycleCount();
			if (!histogram.containsKey(cycleCount)) {
				// create a new key with value 1
				histogram.put(cycleCount, 1);
			} else {
				// add 1 to the value of the existing key
				int existingValue = histogram.get(cycleCount);
				histogram.put(cycleCount, existingValue + 1);
			}
		}	
		System.out.println(histogram);
		System.out.println("Cycle Count:     " + "# Edges with this Cycle Count");
		// then use a for loop and add them to a mapping from Integer to Integer. From cycleCount to number of edges
		// with that cycleCount.
		for (Integer key : histogram.keySet()) {
			System.out.println(key + "\t" + histogram.get(key));
		}
		
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
			edge.incrementCycleCount();
			if (pq.remove(edge)) { // remove, update, and add to the priority queue each edge in the discovered cycle
				pq.add(edge);
			}
		}
	}
	
	public BitSet getOneCycle(CustomWeightedEdge edgePQ) {
		BitSet output = new BitSet(graph.edgeSet().size()); // this is the number of bits in the BitSet
		Set<Integer> uDiscovered = new HashSet<Integer>(); // nodes that have been discovered while searching BFS from u
		Set<Integer> vDiscovered = new HashSet<Integer>(); // nodes that have been discovered while searching BFS from v
		
		BitSet uBitSet = new BitSet(graph.edgeSet().size()); // set of edges traversed on u's side
		BitSet vBitSet = new BitSet(graph.edgeSet().size()); // set of edges traversed on v's side
		
		Integer u = graph.getEdgeSource(edgePQ);
		uDiscovered.add(u);
		nodeToBitSet.put(u, (BitSet) uBitSet.clone());
		Integer v = graph.getEdgeTarget(edgePQ);
		vDiscovered.add(v);
		nodeToBitSet.put(v, (BitSet) vBitSet.clone());
		Integer originalU = graph.getEdgeSource(edgePQ);
		Integer originalV = graph.getEdgeTarget(edgePQ);
		
		//System.out.println("uDiscovered: " + uDiscovered);
		//System.out.println("vDiscovered: " + vDiscovered);
		//System.out.println("nodeToBitSet: " + nodeToBitSet);
		
		while (true) {
			// get edges incident on u
			Set<CustomWeightedEdge> uNeighbors = graph.incomingEdgesOf(u);
			// get edges incident on v
			Set<CustomWeightedEdge> vNeighbors = graph.outgoingEdgesOf(v);
			
			// keep track of the problem edges so that they are not part of the random lottery at the end
			// problem edges include edges that are part of a duplicate cycle, or are the edge from v->u
			Set<CustomWeightedEdge> goodUNeighbors = new HashSet<CustomWeightedEdge>();
			Set<CustomWeightedEdge> goodVNeighbors = new HashSet<CustomWeightedEdge>();

			// PICK BEST NEIGHBOR FOR U. (Look at incoming edges)
			int uMinCycleCount = 10000000;
			List<CustomWeightedEdge> uMinCycleCountEdges = new ArrayList<CustomWeightedEdge>();
			for (CustomWeightedEdge edge : uNeighbors) {
				/*System.out.println("u: " + edge);
				System.out.println("uDiscovered: " + uDiscovered);
				System.out.println("vDiscovered: " + vDiscovered);*/
				Integer edgeSource = graph.getEdgeSource(edge);
				
				// If the edge source is the original v and the edge target is the original u, then DEFINITELY skip this.
				if (graph.getEdgeSource(edge) == originalV && graph.getEdgeTarget(edge) == originalU) {
					//System.out.println("Found the mistake. Not this time.");
					continue;
				}
				
				if (uDiscovered.contains(edgeSource)) { // if we can close the cycle on the same side
					//System.out.println("Edge has been discovered already by u.");
					
					//output.set(edgeToIntegerMap.get(edge));
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
							/*System.out.println("Found a cycle for u with length 3 or greater!");
							System.out.println("Cycle: " + output);
							System.out.println("uBitSet: " + uBitSet);
							System.out.println("edgeTarget: " + nodeToBitSet.get(edgeSource));
							System.out.println("edgePQ: " + edgeToIntegerMap.get(edgePQ));
							System.out.println("edge: " + edgeToIntegerMap.get(edge));*/
							updatePQCycleCount(output);
							return output; // exit the while loop
						} else { // if cycle has already been found
							numRepeatedCycles++;
							//System.out.println("Duplicate cycle. Continuing.");
							output.clear();
							continue;
						}
					} else { // if we found a cycle of length 2 (as in the first case), then we clear the output BitSet
						// and continue searching
						output.clear();
					}
				} else {
					// the edges that made it here are goodUNeighbors
					goodUNeighbors.add(edge);
					
					// return edge with the lowest cycle count. If there's a tie, *RANDOMLY* choose an edge.
					if (edge.getCycleCount() < uMinCycleCount && !removedEdges.contains(edge)) { // edge hasn't been removed
						// reset the uMinCycleCountEdges, update uMinCycleCount
						uMinCycleCount = edge.getCycleCount();
						uMinCycleCountEdges.clear();
						uMinCycleCountEdges.add(edge);
					} else if (edge.getCycleCount() == uMinCycleCount) {
						uMinCycleCountEdges.add(edge);
					}
				}
			}
			
			// probability Beta = 80% that the edge is chosen from the uMinCycleCountEdges
			// probability 1 - Beta = 20% that the edge is chosen from all the uNeighbors
			
			if (uMinCycleCountEdges.size() > 0) { // if there exists at least one edge in uMinCycleCountEdges
				CustomWeightedEdge randomlyChosenEdge = new CustomWeightedEdge();
				double randomDouble = new Random().nextDouble(); // between 0.0 and 1.0
				if (randomDouble < .7) {
					// choose edge randomly from uMinCycleCountEdges
					int uRandomIndex = new Random().nextInt(uMinCycleCountEdges.size()); // shit. Forgot to check that
					// the randomly chosen minCycleCountEdge is not already discovered
					randomlyChosenEdge = uMinCycleCountEdges.get(uRandomIndex);
				} else {
					// choose edge randomly from all the uNeighbors given that:
					// - edge hasn't been discovered
					// hasn't been removed from PQ
					// - edge target doesn't create a duplicate cycle
					// for each edge in uProblemEdges, remove it from uNeighbors so it doesn't have a chance to be chosen
					randomlyChosenEdge = chooseRandomEdgeFromAll(true, goodUNeighbors, uDiscovered);
				}
				// check that the source of this edge is not already discovered
				if (!uDiscovered.contains(graph.getEdgeSource(randomlyChosenEdge))) {
					Integer uDiscoveredNode = graph.getEdgeSource(randomlyChosenEdge);
					//System.out.println("Chosen u edge: " + randomlyChosenEdge);
					uDiscovered.add(uDiscoveredNode);
					uBitSet.set(edgeToIntegerMap.get(randomlyChosenEdge));
					u = uDiscoveredNode;
					nodeToBitSet.put(u, (BitSet) uBitSet.clone());
				} else {
					//System.out.println("target edge u has already been discovered");
					edgePQ.incrementStuckCount();
					return new BitSet();
				}
			} else { // if there are no valid neighbors left
				edgePQ.incrementStuckCount();
				return new BitSet(); // return an empty BitSet, which will be ignored in getCycles().
			}
			
			//System.out.println("v: " + v);
			// PICK BEST NEIGHBOR FOR V. (Look at outgoing edges)
			int vMinCycleCount = 10000000;
			List<CustomWeightedEdge> vMinCycleCountEdges = new ArrayList<CustomWeightedEdge>();
			for (CustomWeightedEdge edge : vNeighbors) {
				/*System.out.println("v: " + edge);
				System.out.println("uDiscovered: " + uDiscovered);
				System.out.println("vDiscovered: " + vDiscovered);*/
				Integer edgeTarget = graph.getEdgeTarget(edge);
				if (vDiscovered.contains(edgeTarget)) { // OR if the edgeTarget was the original u.
					//System.out.println("Edge has been discovered already by v.");
					continue;
				}
				// If the edge source is the original v and the edge target is the original u, then DEFINITELY skip this.
				if (graph.getEdgeSource(edge) == originalV && graph.getEdgeTarget(edge) == originalU) {
					//System.out.println("Found the mistake. Not this time.");
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
					if (output.cardinality() > 2) {
						if (!cycles.contains(output)) { // AND cycle hasn't been found already
							/*System.out.println("Found a cycle for v with length 3 or greater!");
							System.out.println("Cycle: " + output);
							System.out.println("vBitSet: " + vBitSet);
							System.out.println("edgeTarget: " + nodeToBitSet.get(edgeTarget));
							System.out.println("edgePQ: " + edgeToIntegerMap.get(edgePQ));
							System.out.println("edge: " + edgeToIntegerMap.get(edge));*/
							updatePQCycleCount(output);
							return output; // exit the while loop
						} else { // if cycle has already been found
							numRepeatedCycles++;
							//System.out.println("Duplicate cycle. Continuing.");
							output.clear(); // reset the output
							continue;
						}
					} else {
						output.clear(); // reset the output
					}
				} else {
					// the edges that made it here are goodVNeighbors
					goodVNeighbors.add(edge);
					
					// return edge with the lowest cycle count
					if (edge.getCycleCount() < vMinCycleCount && !removedEdges.contains(edge)) { 
						// reset the vMinCycleCountEdges, update vMinCycleCount
						vMinCycleCount = edge.getCycleCount();
						vMinCycleCountEdges.clear();
						vMinCycleCountEdges.add(edge);
					} else if (edge.getCycleCount() == vMinCycleCount) {
						vMinCycleCountEdges.add(edge);
					}
				}
			}
			
			// probability Beta = 80% that the edge is chosen from the uMinCycleCountEdges
			// probability 1 - Beta = 20% that the edge is chosen from all the uNeighbors
			if (vMinCycleCountEdges.size() > 0) { // if there exists at least one edge in vMinCycleCountEdges
				CustomWeightedEdge randomlyChosenEdge = new CustomWeightedEdge();
				double randomDouble = new Random().nextDouble(); // between 0.0 and 1.0
				if (randomDouble < .7) {
					// choose edge randomly from vMinCycleCountEdges
					int vRandomIndex = new Random().nextInt(vMinCycleCountEdges.size());
					randomlyChosenEdge = vMinCycleCountEdges.get(vRandomIndex);
				} else {
					// choose edge randomly from all the vNeighbors
					randomlyChosenEdge = chooseRandomEdgeFromAll(false, goodVNeighbors, vDiscovered);
				}
				// check that the target of this edge is not already discovered
				if (!vDiscovered.contains(graph.getEdgeTarget(randomlyChosenEdge))) {
					Integer vDiscoveredNode = graph.getEdgeTarget(randomlyChosenEdge);
					//System.out.println("Chosen v edge: " + randomlyChosenEdge);
					vDiscovered.add(vDiscoveredNode);
					vBitSet.set(edgeToIntegerMap.get(randomlyChosenEdge));
					v = vDiscoveredNode;
					nodeToBitSet.put(v, (BitSet) vBitSet.clone());
				} else {
					//System.out.println("target edge v has already been discovered");
					edgePQ.incrementStuckCount();
					return new BitSet();
				}
			} else { // if there are no valid neighbors left
				edgePQ.incrementStuckCount();
				return new BitSet(); // return an empty BitSet, which will be ignored in getCycles().
			}
		}
	}
	
	private CustomWeightedEdge chooseRandomEdgeFromAll(boolean isU, Set<CustomWeightedEdge> neighbors, 
			Set<Integer> discovered) {
		int item = new Random().nextInt(neighbors.size()); //integer between 0 (inclusive) and size (exclusive)
		int i = 0;
		CustomWeightedEdge selectedEdge = new CustomWeightedEdge();
		for(CustomWeightedEdge edge : neighbors)
		{
		    if (i == item)
		        selectedEdge = edge;
		    i = i + 1;
		}
		// if the edge's target has been discovered already
		if (isU) {
			// we look at the incoming edges (edge source)
			if (discovered.contains(graph.getEdgeSource(selectedEdge))) {
				return chooseRandomEdgeFromAll(isU, neighbors, discovered);
			}
		} else {
			// we look at the outgoing edges (edge target)
			if (discovered.contains(graph.getEdgeTarget(selectedEdge))) {
				return chooseRandomEdgeFromAll(isU, neighbors, discovered);
			}
		}
		//System.out.println("Randomly selected edge: " + selectedEdge);
		return selectedEdge;
	}
	
	public boolean verifyCycles(List<BitSet> cycles) {
		for (BitSet cycle : cycles) {
			BitSet sources = new BitSet(graph.vertexSet().size());
			BitSet targets = new BitSet(graph.vertexSet().size());
			for (int i = cycle.nextSetBit(0); i >= 0; i = cycle.nextSetBit(i + 1)) {
				CustomWeightedEdge edge = integerToEdgeMap.get(i);
				int source = graph.getEdgeSource(edge);
				int target = graph.getEdgeTarget(edge);
				
				if (sources.get(source) == true || targets.get(target) == true) {
					System.out.println("Not a valid cycle. Repeat source or target.");
					System.out.println(edge);
					return false;
				}
				sources.set(graph.getEdgeSource(edge));
				targets.set(graph.getEdgeTarget(edge));
				System.out.println(edge);
			}
			if (!sources.equals(targets)) {
				System.out.println("Not a valid cycle.");
				return false;
			}
			System.out.println();
		}
		System.out.println("All cycles are valid.");
		return true;
	}
	
	public void printIntegerToEdgeMap() {
		for (Integer key : integerToEdgeMap.keySet()) {
			System.out.println(key + "\t" + integerToEdgeMap.get(key));
		}
	}
	
	public List<BitSet> getCyclesWithEdge(Integer sourceNode, Integer targetNode, List<BitSet> cycles) {
		CustomWeightedEdge edge = graph.getEdge(sourceNode, targetNode);
		Integer edgeNumber = edgeToIntegerMap.get(edge);
		List<BitSet> cyclesWithEdge = new ArrayList<BitSet>();
		for (BitSet cycle : cycles) {
			if (cycle.get(edgeNumber)) { // if this cycle contains the edge we're looking for
				cyclesWithEdge.add(cycle);
				System.out.println(cycle);
				for (int i = cycle.nextSetBit(0); i >= 0; i = cycle.nextSetBit(i + 1)) {
					List<CustomWeightedEdge> edges = new ArrayList<CustomWeightedEdge>();
					edges.add(integerToEdgeMap.get(i));
					System.out.println(edges);
				}
			}
		}
		System.out.println("Cycles with edge " + edge + " - " + cyclesWithEdge);
		return cyclesWithEdge;
	}
}
