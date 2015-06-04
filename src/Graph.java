import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

/**
 * @author Christopher Kao
 * 6/3/15
 */
public class Graph {

	private SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph;
	
	/**
	 * Constructor that takes an input .txt file and builds the graph using JGraphT's SimpleDirectedGraph class.
	 * @param fileName
	 */
	public Graph(String fileName) {
		graph = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		if (fileName == null || fileName.length() == 0) {
			throw new IllegalArgumentException();
		}
		int startNode; // Nodes can also be represented as Strings, or whichever class we decide to implement.
		int endNode;
		double weight;
		// first pass just adds the vertices, doesn't care about the edge weights
		try {
			String line;
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("\\s+");
				startNode = Integer.parseInt(tokens[0]);
				endNode = Integer.parseInt(tokens[1]);
				//weight = Double.parseDouble(tokens[2]);
				graph.addVertex(startNode);
				graph.addVertex(endNode);
			}
		} catch (IOException e) {
			System.out.println("IO Exception.");
		}
	
		// second pass adds the edges with their weights, since vertices are all guaranteed to be in the graph
		try {
			String line;
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("\\s+");
				startNode = Integer.parseInt(tokens[0]);
				endNode = Integer.parseInt(tokens[1]);
				weight = Double.parseDouble(tokens[2]);
				addEdge(startNode, endNode, weight);
				// want to add edges
			}
		} catch (IOException e) {
			System.out.println("IO Exception.");
		}
	}
	
	public List<List> findJohnsonCycles() {
		JohnsonSimpleCycles johnsonCycles = new JohnsonSimpleCycles(graph);
		List<List> cycles = johnsonCycles.findSimpleCycles();
		List<List> output = new ArrayList<List>();
		System.out.println("Johnson cycles:");
		// ignore all the cycles of length 2 (these trivially sum up to 0)
		for (List cycle : cycles) {
			if (cycle.size() > 2) {
				output.add(cycle);
			}
		}
		System.out.println(output);
		return output;
	}
	
	public List<List> findSzwarcfiterLauerCycles() {
		SzwarcfiterLauerSimpleCycles slCycles = new SzwarcfiterLauerSimpleCycles(graph);
		List<List> cycles = slCycles.findSimpleCycles();
		List<List> output = new ArrayList<List>();
		System.out.println("Szwarcfiter Lauer cycles:");
		// ignore all the cycles of length 2 (these trivially sum up to 0)
		for (List cycle : cycles) {
			if (cycle.size() > 2) {
				output.add(cycle);
			}
		}
		System.out.println(output);
		return output;
	}
	
	public void perturbEdges() {
		// before each iteration, need to reset the totalEdgePerturbationMap
		// edgeNumCyclesMap can stay the same since the topology of the graph doesn't change.
		List<List> cycles = findSzwarcfiterLauerCycles();
		Map<Integer, Map<Integer, Integer>> edgeNumCyclesMap = createEdgeNumCyclesMap(cycles);
		double totalCycleInconsistency = 1.0; // any number greater than the threshold works
		int iteration = 0;
		while (totalCycleInconsistency > 0.001) {
			iteration++;
			System.out.println("---------------------------------------------");
			System.out.println("ITERATION: " + iteration);
			totalCycleInconsistency = perturbIteration(cycles, edgeNumCyclesMap);
			print();
			System.out.println();
			System.out.println("total cycle inconsistency: " + totalCycleInconsistency);
		}
		System.out.println("---------------------------------------------");
		System.out.println("Completed in " + iteration + " iterations.");
	}
	
	public double perturbIteration(List<List> cycles, Map<Integer, Map<Integer, Integer>> edgeNumCyclesMap) {
		Map<Integer, Map<Integer, Double>> totalEdgePerturbationMap = new HashMap<Integer, Map<Integer, Double>>();
		double totalCycleInconsistency = 0.0;
		for (List<Integer> cycle : cycles) {
			// need to know the length of the cycle
			// need to outline the individual edges of the cycle
			// need to calculate the sum of the edge weights of the cycle
			// divide the negative of the sum by the length of the cycle and distribute among the edges
			// IMPORTANT: keep track of how many cycles each edge is in, to find the average of the added perturbation
			//  could have 2 other JGraphT graphs
			//    - one keeps track of the sum of the perturbations
			//    - the other keeps track of the number of cycles that an edge is in
			// lastly, epsilon is added in to vary how much each edge should be perturbed with each iteration
			
			int cycleLength = cycle.size();
			double cycleSum = 0.0;
			//System.out.println(); // just to make it neater between cycles
			//System.out.println(cycle);
			DefaultWeightedEdge edge;
			// 1. Calculate the sum of the cycle edge weights.
			for (int i = 0; i < cycleLength; i++) { // remember, i is the index, not the node value in the list
				if (i == cycleLength - 1) {
					// edge from i to 0 (wraps around)
					edge = graph.getEdge(cycle.get(i), cycle.get(0));
				} else {
					// edge from i to i + 1
					edge = graph.getEdge(cycle.get(i), cycle.get(i + 1));
				}
				cycleSum += graph.getEdgeWeight(edge);
			}
			System.out.println("cycle sum: " + cycleSum);
			totalCycleInconsistency += Math.abs(cycleSum);
			
			double averageEdgePerturbation = -1.0 * cycleSum / cycleLength; // MULTIPLIED BY SOME EPSILON
			//System.out.println("average edge perturbation: " + averageEdgePerturbation);
			// 2. Add this averageEdgePerturbation to each edge in the cycle.
			for (int j = 0; j < cycleLength; j++) {
				if (j == cycleLength - 1) {
					edge = graph.getEdge(cycle.get(j), cycle.get(0));
					//System.out.println(edge);
				} else {
					edge = graph.getEdge(cycle.get(j), cycle.get(j + 1));
					//System.out.println(edge);
				}
				Integer edgeSource = graph.getEdgeSource(edge);
				Integer edgeTarget = graph.getEdgeTarget(edge);
				totalEdgePerturbationMap = storeEdgePerturbation(totalEdgePerturbationMap, edgeSource, 
						edgeTarget, averageEdgePerturbation);
				//System.out.println("totalEdgePerturbationMap: " + totalEdgePerturbationMap);
			}
		}
		// Update the original graph by perturbing each edge. Add the totalEdgePerturbationMap / edgeMapCyclesMap
		// to every edge in the graph.
		for (DefaultWeightedEdge edge : graph.edgeSet()) {
			double originalEdgeWeight = graph.getEdgeWeight(edge);
			Integer edgeSource = graph.getEdgeSource(edge);
			Integer edgeTarget = graph.getEdgeTarget(edge);
			double totalEdgePerturbation = totalEdgePerturbationMap.get(edgeSource).get(edgeTarget);
			int edgeNumCycles = edgeNumCyclesMap.get(edgeSource).get(edgeTarget);
			
			double averagedEdgePerturbation = totalEdgePerturbation / edgeNumCycles;
			graph.setEdgeWeight(edge, originalEdgeWeight + averagedEdgePerturbation);
		}
		return totalCycleInconsistency; // actually double what the actual cycle inconsistency is, since the 
		// algorithm is double-counting the cycles
	}
	
	/**
	 * Helper method that stores the edge perturbation into the totalEdgePerturbationMap.
	 */
	private Map<Integer, Map<Integer, Double>> storeEdgePerturbation(Map<Integer, Map<Integer, Double>> map, 
			Integer edgeSource, Integer edgeTarget, double averageEdgePerturbation) {
		if (map.containsKey(edgeSource)) {
			double existingEdgePerturbation = 0.0;
			if (map.get(edgeSource).containsKey(edgeTarget)) {
				existingEdgePerturbation = map.get(edgeSource).get(edgeTarget);
			}
			map.get(edgeSource).put(edgeTarget, existingEdgePerturbation + averageEdgePerturbation);
		} else {
			Map<Integer, Double> targetNodes = new HashMap<Integer, Double>();
			targetNodes.put(edgeTarget, averageEdgePerturbation);
			map.put(edgeSource, targetNodes);
		}
		return map;
	}
	
	private Map<Integer, Map<Integer, Integer>> createEdgeNumCyclesMap(List<List> cycles) {
		Map<Integer, Map<Integer, Integer>> edgeNumCyclesMap = new HashMap<Integer, Map<Integer, Integer>>();
		
		for (List<Integer> cycle : cycles) {
			DefaultWeightedEdge edge;
			int cycleLength = cycle.size();
			for (int j = 0; j < cycleLength; j++) {
				if (j == cycleLength - 1) {
					edge = graph.getEdge(cycle.get(j), cycle.get(0));
				} else {
					edge = graph.getEdge(cycle.get(j), cycle.get(j + 1));
				}
				Integer edgeSource = graph.getEdgeSource(edge);
				Integer edgeTarget = graph.getEdgeTarget(edge);
				edgeNumCyclesMap = storeEdgeNumCycles(edgeNumCyclesMap, edgeSource, edgeTarget);
			}
		}
		return edgeNumCyclesMap;
	}
	
	/**
	 * Helper method that stores the number of cycles that an edge is a part of.
	 */
	private Map<Integer, Map<Integer, Integer>> storeEdgeNumCycles(Map<Integer, Map<Integer, Integer>> map,
			Integer edgeSource, Integer edgeTarget) {
		if (map.containsKey(edgeSource)) {
			Integer existingNumCycles = 0;
			if (map.get(edgeSource).containsKey(edgeTarget)) {
				existingNumCycles = map.get(edgeSource).get(edgeTarget);
			}
			map.get(edgeSource).put(edgeTarget, existingNumCycles + 1);
		} else {
			Map<Integer, Integer> targetNodes = new HashMap<Integer, Integer>();
			targetNodes.put(edgeTarget, 1);
			map.put(edgeSource, targetNodes);
		}
		return map;
	}
	
	public double getTotalInconsistency() {
		return 0.0;
	}
	
	public void generateRandomClique() {
		// want to see how long it takes to find cycles in graphs of size, say 100 or 500.
	}
	
	/**
	 * Helper method which not only adds the edge literally from startNode to endNode, but also adds a negative
	 * edge weight from endNode to startNode.
	 */
	public void addEdge(int startNode, int endNode, double weight) {
		DefaultWeightedEdge forwardEdge = graph.addEdge(startNode, endNode);
		graph.setEdgeWeight(forwardEdge, weight);
		DefaultWeightedEdge backwardEdge = graph.addEdge(endNode, startNode);
		graph.setEdgeWeight(backwardEdge, -1.0 * weight);
	}
	
	public void print() {
		//System.out.println("Vertices: " + graph.vertexSet());
		System.out.println();
		printEdges();
	}
	
	public void printEdges() {
		Set<DefaultWeightedEdge> edges = graph.edgeSet();
		System.out.println(" Edges:    Weights:");
		for (DefaultWeightedEdge edge : edges) {
			double weight = graph.getEdgeWeight(edge);
			if (weight < 0.0) {
				System.out.println(edge + "     " + weight);
			} else {
				System.out.println(edge + "      " + weight);
			}
		}
	}
}
