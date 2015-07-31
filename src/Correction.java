import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
		// write a helper method which creates a map from an edge to the set of cycles that contain the edge
		Map<CustomWeightedEdge, List<BitSet>> edgeToCyclesMap = generateEdgeToCyclesMap(cycles, integerToEdgeMap);
		
		/*Random r = new Random();
		// Choose a random edge from the graph.
		Set<CustomWeightedEdge> edgeSet = graph.edgeSet();
		CustomWeightedEdge[] edgeArray = edgeSet.toArray(new CustomWeightedEdge[edgeSet.size()]);
		int randomIndex = r.nextInt(edgeArray.length);
		CustomWeightedEdge randomEdge = edgeArray[randomIndex];
		
		System.out.println("Random Edge: " + randomEdge + "\t" + "Edge Number: " + randomIndex);*/
		
		for (int i = 0; i < graph.edgeSet().size(); i++) {
			CustomWeightedEdge chosenEdge = integerToEdgeMap.get(i);
			if (chosenEdge.getCycleCount() == 0) {
				continue;
			}
			//System.out.println("BEFORE EDGE PERTURBATION:");
			double originalEdgeInconsistency = computeEdgeInconsistency(chosenEdge, edgeToCyclesMap.get(chosenEdge), graph,
					integerToEdgeMap, true);
			//System.out.println("AFTER EDGE PERTURBATION:");
			double perturbedEdgeInconsistency = computeEdgeInconsistency(chosenEdge, edgeToCyclesMap.get(chosenEdge), graph,
					integerToEdgeMap, false);
			double deltaEdgeInconsistency = originalEdgeInconsistency - perturbedEdgeInconsistency;
			System.out.println(deltaEdgeInconsistency + "\t" + "Edge " + i);
		}
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
			map.put(edge, cyclesOfEdge);
		}
		return map;
	}
	
	private static double computeEdgeInconsistency(CustomWeightedEdge edge, List<BitSet> cyclesOfEdge, 
			SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph, Map<Integer, CustomWeightedEdge> integerToEdgeMap,
			boolean perturbEdge) {
		double totalInconsistency = 0.0;
		double totalInconsistencyMagnitude = 0.0;
		List<Double> inconsistencies = new ArrayList<Double>();
		
		
		for (BitSet cycle : cyclesOfEdge) {
			double cycleInconsistency = 0.0;
			for (int j = cycle.nextSetBit(0); j >= 0; j = cycle.nextSetBit(j + 1)) {
				CustomWeightedEdge cycleEdge = integerToEdgeMap.get(j); // each edge in the cycle
				double cycleEdgeWeight = graph.getEdgeWeight(cycleEdge);
				cycleInconsistency += cycleEdgeWeight;
			}
			//System.out.println(cycle);
			//System.out.println(cycleInconsistency);
			inconsistencies.add(cycleInconsistency);
			totalInconsistency += cycleInconsistency;
			totalInconsistencyMagnitude += Math.abs(cycleInconsistency);
		}
		//System.out.println("Total Inconsistency: " + totalInconsistency);
		//System.out.println("Total Inconsistency Magnitude: " + totalInconsistencyMagnitude);
		if (perturbEdge) {
			Collections.sort(inconsistencies);
			//System.out.println(inconsistencies);
			double median = getMedianInconsistency(inconsistencies);
			//System.out.println("Median: " + median);
			graph.setEdgeWeight(edge, graph.getEdgeWeight(edge) - median);
		}
		return totalInconsistencyMagnitude;
	}
	
	private static double getMedianInconsistency(List<Double> inconsistencies) {
		double median;
		int size = inconsistencies.size();
		if (inconsistencies.size() % 2 == 0) {
			median = ((double)inconsistencies.get(size / 2) + (double)inconsistencies.get(size / 2 - 1))/2;
		} else {
			median = (double) inconsistencies.get(size / 2);
		}
		return median;
	}
}