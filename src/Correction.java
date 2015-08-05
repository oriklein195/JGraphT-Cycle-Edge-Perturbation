import java.util.ArrayList;
import java.util.BitSet;
<<<<<<< Updated upstream
import java.util.Collections;
=======
>>>>>>> Stashed changes
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
		
		while (totalCycleInconsistency > 0.001) {
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
	
<<<<<<< Updated upstream
	public static void simulatedAnnealing(SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph, List<BitSet> cycles,
			Map<Integer, CustomWeightedEdge> integerToEdgeMap, Map<CustomWeightedEdge, Integer> edgeToIntegerMap) {
		// write a helper method which creates a map from an edge to the set of cycles that contain the edge
		Map<CustomWeightedEdge, List<BitSet>> edgeToCyclesMap = generateEdgeToCyclesMap(cycles, integerToEdgeMap);
		
		Random r = new Random();
		// Choose a random edge from the graph.
		Set<CustomWeightedEdge> edgeSet = graph.edgeSet();
		CustomWeightedEdge[] edgeArray = edgeSet.toArray(new CustomWeightedEdge[edgeSet.size()]);
		
		System.out.println("Total Inconsistency Magnitude: " + getTotalInconsistency(cycles, integerToEdgeMap, graph));
		System.out.println();
		System.out.println("Perturb Amounts:");
		System.out.println();
		// Here is the meat of the algorithm.
		for (int i = 0; i < 10000; i++) {
			System.out.println("ITERATION " + i);
			int randomIndex = r.nextInt(edgeArray.length);
			CustomWeightedEdge randomEdge = edgeArray[randomIndex];
			
			//System.out.println("Random Edge: " + randomEdge + "\t" + "Edge Number: " + randomIndex);
			
			if (randomEdge.getCycleCount() == 0) {
				System.out.println();
				continue;
			}
			//System.out.println("BEFORE EDGE PERTURBATION:");
			double originalEdgeInconsistency = computeEdgeInconsistency(randomEdge, edgeToCyclesMap.get(randomEdge), graph,
					integerToEdgeMap);
			
			//System.out.println("AFTER EDGE PERTURBATION:");
			double randomPerturbation = -1 + 2 * r.nextDouble();
			graph.setEdgeWeight(randomEdge, graph.getEdgeWeight(randomEdge) + randomPerturbation);
			double perturbedEdgeInconsistency = computeEdgeInconsistency(randomEdge, edgeToCyclesMap.get(randomEdge), graph,
					integerToEdgeMap);
			
			double deltaEdgeInconsistency = perturbedEdgeInconsistency - originalEdgeInconsistency;
			System.out.println("Delta: " + deltaEdgeInconsistency);
			if (deltaEdgeInconsistency < 0) { // if inconsistency was not decreased
				// Do nothing. Perturbation was already applied.
				System.out.println("Delta < 0. Accepted perturbation.");
			} else if (getProbability(i, deltaEdgeInconsistency)) {
				// Do nothing. Perturbation was already applied.
				System.out.println("Delta > 0. Probability passed. Accepted perturbation.");
			} else {
				// Undo the edge perturbation.
				graph.setEdgeWeight(randomEdge, graph.getEdgeWeight(randomEdge) - randomPerturbation);
				System.out.println("Delta > 0. Probability failed. Rejected perturbation. Subtracted median inconsistency instead.");
				// ****What if we set the edge weight to the median inconsistency here instead?******
				/*double medianInconsistency = getMedianInconsistency(randomEdge, edgeToCyclesMap.get(randomEdge), graph, 
						integerToEdgeMap);
				graph.setEdgeWeight(randomEdge, graph.getEdgeWeight(randomEdge) - medianInconsistency);
				perturbedEdgeInconsistency = computeEdgeInconsistency(randomEdge, edgeToCyclesMap.get(randomEdge), graph,
						integerToEdgeMap);
				deltaEdgeInconsistency = perturbedEdgeInconsistency - originalEdgeInconsistency;
				System.out.println("***delta: " + deltaEdgeInconsistency + "*** (should be negative value)");*/
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Total Inconsistency Magnitude: " + getTotalInconsistency(cycles, integerToEdgeMap, graph));
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
			SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph, Map<Integer, CustomWeightedEdge> integerToEdgeMap) {
		double totalInconsistency = 0.0;
		double totalInconsistencyMagnitude = 0.0;
		
		
		for (BitSet cycle : cyclesOfEdge) {
			double cycleInconsistency = 0.0;
			for (int j = cycle.nextSetBit(0); j >= 0; j = cycle.nextSetBit(j + 1)) {
				CustomWeightedEdge cycleEdge = integerToEdgeMap.get(j); // each edge in the cycle
				double cycleEdgeWeight = graph.getEdgeWeight(cycleEdge);
				cycleInconsistency += cycleEdgeWeight;
			}
			//System.out.println(cycle);
			//System.out.println(cycleInconsistency);
			totalInconsistency += cycleInconsistency;
			totalInconsistencyMagnitude += Math.abs(cycleInconsistency);
		}
		//System.out.println("Total Inconsistency: " + totalInconsistency);
		//System.out.println("Total Inconsistency Magnitude: " + totalInconsistencyMagnitude);
		return totalInconsistencyMagnitude;
	}
	
	private static double getMedianInconsistency(CustomWeightedEdge edge, List<BitSet> cyclesOfEdge, SimpleDirectedWeightedGraph<Integer, 
			CustomWeightedEdge> graph, Map<Integer, CustomWeightedEdge> integerToEdgeMap) {
		List<Double> inconsistencies = new ArrayList<Double>();
		
		for (BitSet cycle : cyclesOfEdge) {
			double cycleInconsistency = 0.0;
			for (int j = cycle.nextSetBit(0); j >= 0; j = cycle.nextSetBit(j + 1)) {
				CustomWeightedEdge cycleEdge = integerToEdgeMap.get(j); // each edge in the cycle
				double cycleEdgeWeight = graph.getEdgeWeight(cycleEdge);
				cycleInconsistency += cycleEdgeWeight;
			}
			inconsistencies.add(cycleInconsistency);
		}
		Collections.sort(inconsistencies);
		double medianInconsistency;
		int size = inconsistencies.size();
		if (inconsistencies.size() % 2 == 0) {
			medianInconsistency = ((double)inconsistencies.get(size / 2) + (double)inconsistencies.get(size / 2 - 1))/2;
		} else {
			medianInconsistency = (double) inconsistencies.get(size / 2);
		}
		return medianInconsistency;
	}
	
	private static double getTotalInconsistency(List<BitSet> cycles, Map<Integer, CustomWeightedEdge> integerToEdgeMap,
			SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph) {
=======
	public static void simulatedAnnealing(int maxNumIterations, SimpleDirectedWeightedGraph<Integer, 
			CustomWeightedEdge> graph, List<BitSet> cycles, Map<CustomWeightedEdge, Integer> edgeToIntegerMap,
			Map<Integer, CustomWeightedEdge> integerToEdgeMap) {
		
		Map<CustomWeightedEdge, List<BitSet>> edgeToCyclesMap = generateEdgeToCyclesMap(graph, cycles, 
				edgeToIntegerMap);
		Set<CustomWeightedEdge> edgeSet = graph.edgeSet();
		CustomWeightedEdge[] edgeSetArray = edgeSet.toArray(new CustomWeightedEdge[edgeSet.size()]);
		
		double originalTotalInconsistency = calculateTotalInconsistency(graph, cycles, integerToEdgeMap);
		
		for (int i = 0; i < maxNumIterations; i++) {
			System.out.println("ITERATION: " + i);
			// Choose a random edge.
			Random r = new Random();
			int randomIndex = r.nextInt(edgeSetArray.length);
			CustomWeightedEdge edge = edgeSetArray[randomIndex];
			System.out.println(edge);
			
			double originalEdgeWeight = graph.getEdgeWeight(edge);
			System.out.println("original edge weight: " + originalEdgeWeight);
			// Get total sum of magnitude of inconsistencies for this randomly chosen edge.
			double originalTotalEdgeCyclesInconsistency = calculateEdgeCyclesInconsistency(graph, edge, 
					edgeToCyclesMap, integerToEdgeMap);
			System.out.println("original inconsistency: " + originalTotalEdgeCyclesInconsistency);
			System.out.println();
			
			// Randomly perturb the edge uniformly between -1.0 and 1.0.
			/*double perturbAmount = -1.0 + 2.0 * r.nextDouble();
			graph.setEdgeWeight(edge, originalEdgeWeight + perturbAmount);
			System.out.println("perturbed edge weight: " + (originalEdgeWeight + perturbAmount));
			double perturbedTotalEdgeCyclesInconsistency = calculateEdgeCyclesInconsistency(graph, edge, 
					edgeToCyclesMap, integerToEdgeMap);
			System.out.println("perturbed inconsistency: " + perturbedTotalEdgeCyclesInconsistency);
			
			if (perturbedTotalEdgeCyclesInconsistency < originalTotalEdgeCyclesInconsistency) {
				System.out.println("***Replaced original edge with perturbed edge***");
			} else {
				// set the edge weight back to its original weight
				graph.setEdgeWeight(edge, originalEdgeWeight);
			}*/
			
			for (int j = 0; j < 20; j++) {
				// increment the edge weight by 0.1
				double currentEdgeWeight = graph.getEdgeWeight(edge);
				System.out.println("Current edge weight: " + currentEdgeWeight);
				graph.setEdgeWeight(edge, currentEdgeWeight + 0.1);
				double totalEdgeCyclesInconsistency = calculateEdgeCyclesInconsistency(graph, edge, 
						edgeToCyclesMap, integerToEdgeMap);
				System.out.println(totalEdgeCyclesInconsistency);
			}
			System.out.println("----------------------------------------------");
			// Generate a random "neighbor". Perturb the edge uniformly randomly between (-1, 1).
		}
		System.out.println();
		// Print out the original total inconsistency.
		System.out.println("Original total inconsistency: " + originalTotalInconsistency);
				
		// Print out the final total inconsistency.
		double finalTotalInconsistency = calculateTotalInconsistency(graph, cycles, integerToEdgeMap);
		System.out.println("Final total inconsistency: " + finalTotalInconsistency);
	}
	
	/**
	 * Helper function that computes the total inconsistency for all the cycles of a particular edge.
	 */
	public static double calculateEdgeCyclesInconsistency(SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph,
			CustomWeightedEdge edge, Map<CustomWeightedEdge, List<BitSet>> edgeToCyclesMap, 
			Map<Integer, CustomWeightedEdge> integerToEdgeMap) {
		List<BitSet> cyclesOfEdge = edgeToCyclesMap.get(edge);
		double totalEdgeCyclesInconsistencyMagnitude = 0.0;
		for (BitSet cycle : cyclesOfEdge) {
			double cycleSum = 0.0;
			// calculate cycle sum
			for (int j = cycle.nextSetBit(0); j >= 0; j = cycle.nextSetBit(j + 1)) {
				CustomWeightedEdge edgeInCycle = integerToEdgeMap.get(j); // each edge in the cycle
				cycleSum += graph.getEdgeWeight(edgeInCycle);
			}
			totalEdgeCyclesInconsistencyMagnitude += Math.abs(cycleSum);
		}
		return totalEdgeCyclesInconsistencyMagnitude;
	}
	
	/**
	 * Helper function that returns the total magnitude inconsistency for all of the cycles in the graph
	 */
	public static double calculateTotalInconsistency(SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph,
			List<BitSet> cycles, Map<Integer, CustomWeightedEdge> integerToEdgeMap) {
>>>>>>> Stashed changes
		double totalCycleInconsistency = 0.0;
		for (BitSet cycle : cycles) {
			double cycleSum = 0.0;
			// calculate cycle sum
			for (int i = cycle.nextSetBit(0); i >= 0; i = cycle.nextSetBit(i + 1)) {
				CustomWeightedEdge edge = integerToEdgeMap.get(i); // each edge in the cycle
				cycleSum += graph.getEdgeWeight(edge);
			}
<<<<<<< Updated upstream
=======
			//System.out.println("cycle sum: " + cycleSum);
>>>>>>> Stashed changes
			totalCycleInconsistency += Math.abs(cycleSum);
		}
		return totalCycleInconsistency;
	}
	
<<<<<<< Updated upstream
	public static double getTemperature(int iteration, double initialTemp, double lambda) {
		double temperature = initialTemp * Math.exp(-1.0 * lambda * iteration);
		return temperature;
	}
	
	// z is the original consistency of an edge
	// z' is the new consistency of the edge *after* the perturbation
	// delta is z' - z
	// If delta < 0, we always accept because the inconsistency was lowered and thus improved.
	// If delta > 0, we accept with the probability that e ^ (-delta / temperature) > random(0, 1)
	// If temperature is higher, then the probability is greater
	// If temperature is lower, then the probability is lower
	// If delta is greater, then the probability is lower
	// If delta is lower, then the probability is greater
	public static boolean getProbability(int iteration, double delta) {
		// getTemperature(int iteration, double initialTemp, double lambda)
		double temperature = getTemperature(iteration, 10.0, 0.0005);
		Random r = new Random();
		double randomUniformDouble = r.nextDouble(); // randomly generates a number uniformly between 0 and 1
		double probability = Math.exp(-0.5 * delta / temperature);
		System.out.println("Probability: " + probability);
		//System.out.println("temperature: " + temperature + "\t" + "probability: " + probability);
		return probability > randomUniformDouble;
=======
	// want some helper method that generates a Map from edge to the Set<BitSet> cycles that the edge is a part of
	// Then, we can use this Map to save a lot of time when computing our objective function at each iteration
	public static Map<CustomWeightedEdge, List<BitSet>> generateEdgeToCyclesMap(SimpleDirectedWeightedGraph<Integer,
			CustomWeightedEdge> graph, List<BitSet> cycles, Map<CustomWeightedEdge, Integer> edgeToIntegerMap) {
		
		Map<CustomWeightedEdge, List<BitSet>> map = new HashMap<CustomWeightedEdge, List<BitSet>>();
		
		for (CustomWeightedEdge edge : graph.edgeSet()) {
			List<BitSet> cyclesOfEdge = new ArrayList<BitSet>();
			for (BitSet cycle : cycles) {
				int i = edgeToIntegerMap.get(edge);
				if (cycle.get(i)) { // if this cycle contains the current edge of the outer for loop
					cyclesOfEdge.add(cycle);
				}
			}
			map.put(edge, cyclesOfEdge);
			//System.out.println(edge + " - " + map.get(edge).size());
		}
		
		
		return map;
	}
	
	public static void printVotedInconsistencies(SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph, 
			List<BitSet> cycles, Map<Integer, CustomWeightedEdge> integerToEdgeMap) {
		for (BitSet cycle : cycles) {
			int cycleLength = cycle.cardinality();
			double cycleSum = 0.0;
			// calculate cycle sum
			for (int i = cycle.nextSetBit(0); i >= 0; i = cycle.nextSetBit(i + 1)) {
				CustomWeightedEdge edge = integerToEdgeMap.get(i); // each edge in the cycle
				cycleSum += graph.getEdgeWeight(edge);
			}
			double edgePerturbation = Math.abs(cycleSum / (double) cycleLength); // epsilon = 3.0
			//System.out.println("edge perturbation: " + edgePerturbation);
			// now that we have the cycle sum, need to distribute the negative of it equally to the edges
			for (int j = cycle.nextSetBit(0); j >= 0; j = cycle.nextSetBit(j + 1)) {
				CustomWeightedEdge edge = integerToEdgeMap.get(j); // each edge in the cycle
				edge.addPerturbation(edgePerturbation); // ADD THE ABSOLUTE VALUE INSTEAD
			}
		}
		System.out.println();
		// for each edge in the graph
		for (CustomWeightedEdge edge : graph.edgeSet()) {
			System.out.println(edge.getTotalPerturbation());
			if (edge.getCycleCount() == 0) { // ignore any edges that aren't in cycles
				continue;
			}
			//System.out.println(edge.getTotalPerturbation());
			//System.out.println(edge.getCycleCount());
			double averageEdgePerturbation = edge.getTotalPerturbation() / (double) edge.getCycleCount();
			//System.out.println(averageEdgePerturbation);
			double previousEdgeWeight = graph.getEdgeWeight(edge);
			graph.setEdgeWeight(edge, previousEdgeWeight + averageEdgePerturbation);
			edge.resetTotalPerturbation();
		}
>>>>>>> Stashed changes
	}
}
