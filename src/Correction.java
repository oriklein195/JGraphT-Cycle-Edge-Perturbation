import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;


public class Correction {
	
	// takes in a graph and a list of cycles (NOT the bitmap)
	// make this iterative later, stopping when the total error is below some threshold
	public static void correctEdges(SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph, 
			List<BitSet> cycles, Map<Integer, CustomWeightedEdge> integerToEdgeMap) {
		int iteration = 0;
		double totalCycleInconsistency = 1.0;
		
		while (totalCycleInconsistency > 0.000001) {
			iteration++;
			System.out.println("---------------------------------------------");
			System.out.println("ITERATION: " + iteration);
			totalCycleInconsistency = correctEdgesIteration(graph, cycles, integerToEdgeMap);
			System.out.println("total cycle inconsistency: " + totalCycleInconsistency);
			
		}
	}
	
	public static double correctEdgesIteration(SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph, 
			List<BitSet> cycles, Map<Integer, CustomWeightedEdge> integerToEdgeMap) {
		double totalCycleInconsistency = 0.0;
		for (BitSet cycle : cycles) {
			int cycleLength = cycle.cardinality();
			double cycleSum = 0.0;
			// calculate cycle sum
			for (int i = cycle.nextSetBit(0); i >= 0; i = cycle.nextSetBit(i + 1)) {
				CustomWeightedEdge edge = integerToEdgeMap.get(i); // each edge in the cycle
				cycleSum += graph.getEdgeWeight(edge);
			}
			//System.out.println("cycle sum: " + cycleSum);
			totalCycleInconsistency += Math.abs(cycleSum);
			double edgePerturbation = -1.0 * cycleSum / (double) cycleLength / 10.0; // epsilon = 3.0
			//System.out.println("edge perturbation: " + edgePerturbation);
			// now that we have the cycle sum, need to distribute the negative of it equally to the edges
			for (int j = cycle.nextSetBit(0); j >= 0; j = cycle.nextSetBit(j + 1)) {
				CustomWeightedEdge edge = integerToEdgeMap.get(j); // each edge in the cycle
				edge.addPerturbation(edgePerturbation);
			}
		}
		System.out.println();
		// for each edge in the graph
		for (CustomWeightedEdge edge : graph.edgeSet()) {
			if (edge.getCycleCount() == 0) { // ignore any edges that aren't in cycles
				continue;
			}
			double averageEdgePerturbation = edge.getTotalPerturbation() / (double) edge.getCycleCount();
			//System.out.println(averageEdgePerturbation);
			double previousEdgeWeight = graph.getEdgeWeight(edge);
			graph.setEdgeWeight(edge, previousEdgeWeight + averageEdgePerturbation);
			//System.out.println(edge + "        " + averageEdgePerturbation + "           " + graph.getEdgeWeight(edge));
			// at the end, reset totalPerturbation for each edge in the graph.
			edge.resetTotalPerturbation();
		}
		return totalCycleInconsistency;
	}
	
	public static void simulatedAnnealing(SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph, List<BitSet> cycles,
			Map<Integer, CustomWeightedEdge> integerToEdgeMap, Map<CustomWeightedEdge, Integer> edgeToIntegerMap) {
		// write a helper method that computes the inconsistency of all the cycles that a single edge partakes in
		Map<CustomWeightedEdge, List<BitSet>> edgeToCyclesMap = generateEdgeToCyclesMap(cycles, integerToEdgeMap);
		// write a helper method which creates a map from an edge to the set of cycles that contain the edge
	}
	
	private static Map<CustomWeightedEdge, List<BitSet>> generateEdgeToCyclesMap(List<BitSet> cycles, Map<Integer, 
			CustomWeightedEdge> integerToEdgeMap) {
		
		Map<CustomWeightedEdge, List<BitSet>> map = new HashMap<CustomWeightedEdge, List<BitSet>>();
		for (int i = 0; i < integerToEdgeMap.size(); i++) {
			List<BitSet> cyclesOfEdge = new ArrayList<BitSet>();
			for (BitSet cycle : cycles) {
				if (cycle.get(i)) { // if this cycle contains this edge
					cyclesOfEdge.add(cycle);
				}
			}
			CustomWeightedEdge edge = integerToEdgeMap.get(i);
			System.out.println(edge + " --- " + cyclesOfEdge.size());
			map.put(edge, cyclesOfEdge);
		}
		return map;
	}
	
	private static double computeEdgeInconsistency(CustomWeightedEdge edge) {
		return 0.0;
	}
}
