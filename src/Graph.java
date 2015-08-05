import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

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
	private SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> perturbedGraph;
	
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
				if (!graph.containsEdge(startNode, endNode)) {

					addEdge(startNode, endNode, weight);
				}
			}
		} catch (IOException e) {
			System.out.println("IO Exception.");
		}
		originalGraph = copyGraph(graph);
	}
	
	/** 
	 * Alternate constructor for the Graph class, which creates a random graph instead of reading in a .txt file.
	 * The created random graph has N nodes and 2N - 1 edges. (4N - 2 directed edges)
	 * @param size the number of nodes that we want in the graph/clique
	 */
	public Graph(int size) {
		//originalGraph = new SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge>(CustomWeightedEdge.class);
		//perturbedGraph = new SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge>(CustomWeightedEdge.class);
		createRandomPerfectGraph(size);
		System.out.println("Original Graph:");
		System.out.println(graph.vertexSet().size() + " Vertices: " + graph.vertexSet());
		System.out.println(graph.edgeSet().size() + " Edges: ");
		for (CustomWeightedEdge edge : graph.edgeSet()) {
			System.out.println(edge + "\t" + graph.getEdgeWeight(edge));
		}
		originalGraph = copyGraph(graph);
		
	}
	
	public void createRandomPerfectGraph(int size) {
		graph = new SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge>(CustomWeightedEdge.class);
		Map<Integer, Double> vertexToFreeBindingEnergyMap = new HashMap<Integer, Double>();
		List<Integer> inTree = new ArrayList<Integer>();
		List<Integer> notInTree = new ArrayList<Integer>();
		// add vertices. Edges are not yet added
		for (int i = 0; i < size; i++) {
			graph.addVertex(i);
			// assign a random value to the vertex
			Random r = new Random();
			double randomFreeBindingEnergy = -5.0 + 10.0 * r.nextDouble(); // random double between -5.0 and 5.0 inclusive
			System.out.println(randomFreeBindingEnergy);
			vertexToFreeBindingEnergyMap.put(i, randomFreeBindingEnergy);
			notInTree.add(i); // initially, none of the edges are in the tree
		}
		inTree.add(0); // arbitrarily let node 0 be the root of the tree
		notInTree.remove(0);
		// Pick a node in the tree. Pick a node that's not in a tree. Create 2 directed edges between these nodes
		while (notInTree.size() > 0) { // while there exists a node that's not in the tree
			// randomly pick a node in the tree
			Integer nodeInTree = pickRandomIntegerFromList(inTree);
			Integer nodeNotInTree = pickRandomIntegerFromList(notInTree);
			double forwardEdgeWeight = vertexToFreeBindingEnergyMap.get(nodeNotInTree) - vertexToFreeBindingEnergyMap.get(nodeInTree);
			addEdge(nodeInTree, nodeNotInTree, forwardEdgeWeight);
			notInTree.remove(nodeNotInTree);
			inTree.add(nodeNotInTree); // the not-in-tree node is now in the tree
		}
		// At this point, we've created a spanning tree. Now, we want to add more edges to the graph to create cycles.
		int numEdgesAdded = 0;
		while (numEdgesAdded < size) { // adding an additional N edges, for a total of 2N - 1 edges and N nodes
			// choose an unconnected pair of nodes uniformly at random and connect them. These two nodes also 
			// can't be the same node; they must be different
			Integer node1 = pickRandomIntegerFromList(inTree);
			Integer node2 = pickRandomIntegerFromList(inTree);
			if (!graph.containsEdge(node1, node2) && node1 != node2) { // add an edge, move to the next iteration
				double forwardEdgeWeight = vertexToFreeBindingEnergyMap.get(node2) - vertexToFreeBindingEnergyMap.get(node1);
				System.out.println("forward edge weight: " + forwardEdgeWeight);
				addEdge(node1, node2, forwardEdgeWeight);
				numEdgesAdded++;
			} 
		}
	}
	
	public void perturbEdges() {
		Random r = new Random();
		int count = -1;
		for (CustomWeightedEdge edge : graph.edgeSet()) { // generate new random number every other edge
			count++;
			if (count % 2 != 0) {
				continue;
			}
			double edgePerturbation = -1.0 + 2.0 * r.nextDouble();
			//System.out.println("edge perturbation: " + edgePerturbation);
			graph.setEdgeWeight(edge, graph.getEdgeWeight(edge) + edgePerturbation);
			CustomWeightedEdge backwardEdge = graph.getEdge(graph.getEdgeTarget(edge), 
					graph.getEdgeSource(edge));
			graph.setEdgeWeight(backwardEdge, graph.getEdgeWeight(backwardEdge) - edgePerturbation);
		}
		perturbedGraph = copyGraph(graph);
	}
	
	public Integer pickRandomIntegerFromList(List<Integer> list) {
		Random r = new Random();
		// pick a random integer between 0 and list.size() - 1
		int randomIndex = r.nextInt(list.size());
		return list.get(randomIndex);
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
		System.out.println(output.size() + " cycles of length 3 or greater:");
		System.out.println(output);
		return output;
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
	
	public void getPercentChangeOriginalToPerturbed() {
		getPercentChange(originalGraph, perturbedGraph);
	}
	
	public void getPercentChangedPerturbedToCorrected() {
		getPercentChange(perturbedGraph, graph);
	}
	
	public void getPercentChangedCorrectedToOriginal() {
		getPercentChange(graph, originalGraph);
	}
	
	public double getPercentChange(SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph1, 
			SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> graph2) {
		// assumed that originalGraph and perturbedGraph still have the same nodes and edges.
		// The only thing that has changed is the weight on each edge.
		double totalEdgeDifference = 0.0; // magnitude of the total edge perturbations
		double originalEdgeSum = 0.0; // magnitude of the total edge sum
		for (CustomWeightedEdge originalEdge : graph1.edgeSet()) {
			// get that same edge in the graph
			double originalEdgeWeight = graph1.getEdgeWeight(originalEdge);
			originalEdgeSum += Math.abs(originalEdgeWeight);
			Integer sourceVertex = graph1.getEdgeSource(originalEdge);
			Integer targetVertex = graph1.getEdgeTarget(originalEdge);
			CustomWeightedEdge perturbedEdge = graph2.getEdge(sourceVertex, targetVertex);
			double perturbedEdgeWeight = graph2.getEdgeWeight(perturbedEdge);
			totalEdgeDifference += Math.abs(originalEdgeWeight - perturbedEdgeWeight);
			//System.out.println(totalEdgeDifference);
		}
		System.out.println();
		System.out.println("total edge difference: " + totalEdgeDifference);
		System.out.println("original edge sum: " + originalEdgeSum);
		double percentChange = totalEdgeDifference / originalEdgeSum;
		System.out.println("percent change/perturbation: " + percentChange);
		return percentChange;
	}
	
	public static SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> copyGraph(
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
	
	public void printEdgeWeights() {
		Set<CustomWeightedEdge> edges = graph.edgeSet();
		System.out.println("Edge Weights (For Matlab):");
		for (CustomWeightedEdge edge : edges) {
			double weight = graph.getEdgeWeight(edge);
			Integer sourceVertex = graph.getEdgeSource(edge);
			Integer targetVertex = graph.getEdgeTarget(edge);
			CustomWeightedEdge originalEdge = originalGraph.getEdge(sourceVertex, targetVertex);
			double originalWeight = originalGraph.getEdgeWeight(originalEdge);
			System.out.println(weight);
		}
		System.out.println();
	}
	
	public void printEdges() {
		Set<CustomWeightedEdge> edges = graph.edgeSet();
		System.out.println("Edges:");
		for (CustomWeightedEdge edge : edges) {
			double weight = graph.getEdgeWeight(edge);
			Integer sourceVertex = graph.getEdgeSource(edge);
			Integer targetVertex = graph.getEdgeTarget(edge);
			CustomWeightedEdge originalEdge = originalGraph.getEdge(sourceVertex, targetVertex);
			double originalWeight = originalGraph.getEdgeWeight(originalEdge);
			System.out.println(sourceVertex + " " + targetVertex + " " + weight);
		}
		System.out.println();
	}
	
	public void saveGraphAsText() {
		try {
			File file = new File("/Users/christopher/Desktop/Cycles_Matrix/graph.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (CustomWeightedEdge edge : graph.edgeSet()) {
				Integer sourceVertex = graph.getEdgeSource(edge);
				Integer targetVertex = graph.getEdgeTarget(edge);
				double edgeWeight = graph.getEdgeWeight(edge);
				bw.write(sourceVertex + " " + targetVertex + " " + edgeWeight);
				bw.newLine();
			}
			bw.close();
			System.out.println("Finished writing file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public void printVertexDegreesHistogram() {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>(); // mapping from degree to # vertices with that degree
		for (Integer vertex : graph.vertexSet()) {
			int vertexDegree = graph.inDegreeOf(vertex);
			if (map.containsKey(vertexDegree)) {
				map.put(vertexDegree, map.get(vertexDegree) + 1);
			} else {
				map.put(vertexDegree, 1);
			}
		}
		
		System.out.println("Degree:" + "\t" + "Number of Vertices with Degree:");
		for (Integer degree : map.keySet()) {
			System.out.println(degree + "\t" + map.get(degree));
		}
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
	
	public SimpleDirectedWeightedGraph<Integer, CustomWeightedEdge> getGraph() {
		return graph;
	}
	
	public void printCycleLengthHistogram(List<List> slCycles) {
		Map<Integer, Integer> histogram = new TreeMap<Integer, Integer>();
		for (List slCycle : slCycles) {
			int cycleLength = slCycle.size();
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
	
	public void printIntegerToEdgeMap(Map<Integer, CustomWeightedEdge> integerToEdgeMap) {
		for (Integer key : integerToEdgeMap.keySet()) {
			CustomWeightedEdge edge = integerToEdgeMap.get(key);
			System.out.println(key + " - " + edge + " - " + graph.getEdgeWeight(edge));
		}
	}
	
}
