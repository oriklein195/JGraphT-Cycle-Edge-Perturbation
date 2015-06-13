import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

/**
 * @author Christopher Kao
 * 6/3/15
 */
public class Graph {

	private SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph; // graph that gets perturbed
	private SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> originalGraph;
	
	/**
	 * Constructor that takes an input .txt file and builds the graph using JGraphT's SimpleDirectedGraph class.
	 * @param fileName
	 */
	public Graph(String fileName) {
		graph = new SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge>(CustomWeightedEdge.class);
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
		originalGraph = copyGraph(graph);
	}
	
	/** 
	 * Alternate constructor for the Graph class, which creates a random clique instead of reading in a .txt file
	 * @param size the number of nodes that we want in the graph/clique
	 */
	public Graph(int size) {
		graph = new SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge>(CustomWeightedEdge.class);
		createRandomCliquePerfectTriangles(size);
		originalGraph = copyGraph(graph);
		System.out.println("original graph");
		System.out.println(originalGraph);
	}
	
	public void createRandomCliquePerfectTriangles(int size) {
		Random r = new Random();
		graph.addVertex(0); 
		// generate random edge weights from node 0 to every other node
		for (int i = 1; i < size; i++) {
			graph.addVertex(i);
			double randomEdgeWeight = -1.0 + 2.0 * r.nextDouble(); // random double between -1.0 and 1.0 inclusive
			addEdge(0, i, randomEdgeWeight);
		}
		// generate random edge weights between all neighbors of node 0, with the property that the triangle
		// must add up to 0.0
		for (int j = 1; j < size - 1; j++) {
			for (int k = j + 1; k < size; k++) {
				// edge (0, 1) or (0, j)
				// edge (0, 2) or (0, k)
				CustomWeightedEdge edge1 = graph.getEdge(0, j);
				CustomWeightedEdge edge2 = graph.getEdge(0, k);
				double edge1Weight = graph.getEdgeWeight(edge1);
				double edge2Weight = graph.getEdgeWeight(edge2);
				// edge3 will go from j to k
				double edge3Weight = edge2Weight - edge1Weight;
				addEdge(j, k, edge3Weight);
			}
		}
		
		// want to see how long it takes to find cycles in graphs of size, say 100 or 500.
		
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
		System.out.println("Finding cycles...");
		List<List> cycles = slCycles.findSimpleCycles();
		List<List> output = new ArrayList<List>();
		System.out.println("Szwarcfiter Lauer cycles:");
		// ignore all the cycles of length 2 (these trivially sum up to 0)
		for (List cycle : cycles) {
			if (cycle.size() > 2) {
				output.add(cycle);
			}
		}
		//System.out.println(output);
		System.out.println("There are " + output.size() + " cycles of length 3 or greater.");
		return output;
	}
	
	public void perturbEdges() {
		// before each iteration, need to reset the totalEdgePerturbationMap
		// edgeNumCyclesMap can stay the same since the topology of the graph doesn't change.
		System.out.println("Starting to find cycles...");
		List<List> cycles = findSzwarcfiterLauerCycles();
		Map<Integer, Map<Integer, Integer>> edgeNumCyclesMap = createEdgeNumCyclesMap(cycles);
		double totalCycleInconsistency = 1.0; // any number greater than the threshold works
		int iteration = 0;
		while (totalCycleInconsistency > 0.001) {
			iteration++;
			System.out.println("---------------------------------------------");
			System.out.println("ITERATION: " + iteration);
			totalCycleInconsistency = perturbIteration(cycles, edgeNumCyclesMap);
			//print(graph);
			System.out.println();
			System.out.println("total cycle inconsistency: " + totalCycleInconsistency);
		}
		printFinalStatistics(cycles, iteration);
		getPercentChange();
	}
	
	private void printFinalStatistics(List<List> cycles, int iteration) {
		printEdges();
		int cycleNumber = 0;
		for (List<Integer> cycle : cycles) {
			cycleNumber++;
			int cycleLength = cycle.size();
			double cycleSum = 0.0;
			
			CustomWeightedEdge edge;
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
			System.out.println("Cycle " + cycleNumber + ": " + cycle + " - " + "cycle sum: " + cycleSum);
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
			CustomWeightedEdge edge;
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
		for (CustomWeightedEdge edge : graph.edgeSet()) {
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
			CustomWeightedEdge edge;
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
	
	/**
	 * Helper method which not only adds the edge literally from startNode to endNode, but also adds a negative
	 * edge weight from endNode to startNode.
	 */
	public void addEdge(int startNode, int endNode, double weight) {
		CustomWeightedEdge forwardEdge = graph.addEdge(startNode, endNode);
		graph.setEdgeWeight(forwardEdge, weight);
		CustomWeightedEdge backwardEdge = graph.addEdge(endNode, startNode);
		graph.setEdgeWeight(backwardEdge, -1.0 * weight);
	}
	
	public double getPercentChange() {
		// assumed that originalGraph and perturbedGraph still have the same nodes and edges.
		// The only thing that has changed is the weight on each edge.
		double totalEdgeDifference = 0.0; // magnitude of the total edge perturbations
		double originalEdgeSum = 0.0; // magnitude of the total edge sum
		for (CustomWeightedEdge originalEdge : originalGraph.edgeSet()) {
			// get that same edge in the graph
			double originalEdgeWeight = originalGraph.getEdgeWeight(originalEdge);
			originalEdgeSum += Math.abs(originalEdgeWeight);
			Integer sourceVertex = originalGraph.getEdgeSource(originalEdge);
			Integer targetVertex = originalGraph.getEdgeTarget(originalEdge);
			CustomWeightedEdge perturbedEdge = graph.getEdge(sourceVertex, targetVertex);
			double perturbedEdgeWeight = graph.getEdgeWeight(perturbedEdge);
			totalEdgeDifference += Math.abs(originalEdgeWeight - perturbedEdgeWeight);
		}
		System.out.println("total edge difference: " + totalEdgeDifference);
		System.out.println("original edge sum: " + originalEdgeSum);
		double percentChange = totalEdgeDifference / originalEdgeSum;
		System.out.println("percent change/perturbation: " + percentChange);
		return percentChange;
	}
	
	public SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> copyGraph(
			SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> originalGraph) {
		SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> copiedGraph = new SimpleDirectedWeightedGraph<Integer, 
				CustomWeightedEdge>(CustomWeightedEdge.class);
		// copy all the originalGraph's vertices
		for (Integer vertex : originalGraph.vertexSet()) {
			copiedGraph.addVertex(vertex);
		}
		// copy all the originalGraph's edges
		for (CustomWeightedEdge edge : originalGraph.edgeSet()) {
			// don't have it pass/copy edge by reference, pass by value instead!
			Integer sourceVertex = originalGraph.getEdgeSource(edge);
			Integer targetVertex = originalGraph.getEdgeTarget(edge);
			double edgeWeight = originalGraph.getEdgeWeight(edge);
			CustomWeightedEdge copiedEdge = copiedGraph.addEdge(sourceVertex, targetVertex);
			copiedGraph.setEdgeWeight(copiedEdge, edgeWeight);
		}
		return copiedGraph;
	}
	
	public boolean isConnected() {
		ConnectivityInspector conn = new ConnectivityInspector(graph);
		return conn.isGraphConnected();
	}
	
	public List<Set> getConnectedComponents() {
		ConnectivityInspector conn = new ConnectivityInspector(graph);
		List<Set> connectedComponents = conn.connectedSets();
		for (int i = 0; i < connectedComponents.size(); i++) {
			System.out.println("Connected Component " + (i + 1) + ": " + connectedComponents.get(i).size() + " nodes");
			System.out.println(connectedComponents.get(i));
			System.out.println();
		}
		System.out.println("There are " + connectedComponents.size() + " connected components.");
		return connectedComponents;
	}
	
	public void printEdges() {
		Set<CustomWeightedEdge> edges = graph.edgeSet();
		System.out.println(" Edges:          Original Weights:      Perturbed Weights:");
		for (CustomWeightedEdge edge : edges) {
			double weight = graph.getEdgeWeight(edge);
			Integer sourceVertex = graph.getEdgeSource(edge);
			Integer targetVertex = graph.getEdgeTarget(edge);
			CustomWeightedEdge originalEdge = originalGraph.getEdge(sourceVertex, targetVertex);
			double originalWeight = originalGraph.getEdgeWeight(originalEdge);
			System.out.println(edge + "           " + originalWeight + "              " + weight);
		}
		System.out.println();
	}
	
	public void printVertexDegrees() {
		for (Integer vertex : graph.vertexSet()) {
			List<Integer> neighbors = new ArrayList<Integer>();
			for (CustomWeightedEdge edge : graph.edgesOf(vertex)) {
				if (graph.getEdgeSource(edge).equals(vertex)) {
					neighbors.add(graph.getEdgeTarget(edge));
				}
			}
			System.out.println("Node: " + vertex + "    Degree: " + graph.outDegreeOf(vertex) + "   " + neighbors);
		}
	}
	
	public void getOneCycle() {
		Cycles cycles = new Cycles(graph);
		cycles.getOneCycle();
	}
	
	public List<BitSet> getCycles(int minCycleCount) {
		Cycles c = new Cycles(graph);
		List<BitSet> cycles = c.getCycles(minCycleCount);
		return cycles;
	}
	
	public boolean verifyCycles(List<BitSet> cycles) {
		Cycles c = new Cycles(graph);
		return c.verifyCycles(cycles);
	}
	
	public List<CustomWeightedEdge> findBridges() {
		List<CustomWeightedEdge> bridges = new ArrayList<CustomWeightedEdge>();
		// make a copy of the graph
		SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graphCopy = copyGraph(graph);
		Set<CustomWeightedEdge> edges = graph.edgeSet();
		for (CustomWeightedEdge edge : edges) {
			Integer sourceVertex = graphCopy.getEdgeSource(edge);
			Integer targetVertex = graphCopy.getEdgeTarget(edge);
			// remove the edges of the graphCopy one by one
			graphCopy.removeEdge(sourceVertex, targetVertex);
			graphCopy.removeEdge(targetVertex, sourceVertex);
			// get connected components
			ConnectivityInspector conn = new ConnectivityInspector(graphCopy);
			List<Set> connectedComponents = conn.connectedSets();
			if (connectedComponents.size() == 2) { // if the removal of the edge created 2 connected components
				bridges.add(edge);
			}
			// add the removed edge back
			graphCopy.addEdge(sourceVertex, targetVertex, edge);
			graphCopy.addEdge(targetVertex, sourceVertex, edge);
		}
		System.out.println("Bridges: " + bridges);
		return bridges;
	}
	
	
}
