import java.util.List;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

/**
 * @author Christopher Kao
 * 6/2/15
 */
public class Search {
	
	private SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph;
	//private EdgeFactory<Integer, Integer> ef = new EdgeFactory<Integer, Integer>();
	
	public Search() {
		// The parameter for the constructor is a class that extends the edge class.
		graph = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	}
	
	public void populateGraph() {
		graph.addVertex(0);
		graph.addVertex(1);
		graph.addVertex(2);
		graph.addVertex(3);
		
		DefaultWeightedEdge e1 = graph.addEdge(0, 1);
		graph.setEdgeWeight(e1, 1.0);
		
		DefaultWeightedEdge e2 = graph.addEdge(1, 2);
		graph.setEdgeWeight(e2, 1.0);
		
		DefaultWeightedEdge e3 = graph.addEdge(2, 3);
		graph.setEdgeWeight(e3, 1.0);
		
		DefaultWeightedEdge e4 = graph.addEdge(3, 0);
		graph.setEdgeWeight(e4, 1.0);
	}
	
	public void createSquare() {
		graph.addVertex(0);
		graph.addVertex(1);
		graph.addVertex(2);
		graph.addVertex(3);
		
		DefaultWeightedEdge e1 = graph.addEdge(0, 1);
		graph.setEdgeWeight(e1, 1.0);
		
		DefaultWeightedEdge e1r = graph.addEdge(1, 0); // edge 1 reversed
		graph.setEdgeWeight(e1r, -1.0);
		
		DefaultWeightedEdge e2 = graph.addEdge(1, 2);
		graph.setEdgeWeight(e2, 1.0);
		
		DefaultWeightedEdge e2r = graph.addEdge(2, 1);
		graph.setEdgeWeight(e2r, -1.0);
		
		DefaultWeightedEdge e3 = graph.addEdge(2, 3);
		graph.setEdgeWeight(e3, 1.0);
		
		DefaultWeightedEdge e3r = graph.addEdge(3, 2);
		graph.setEdgeWeight(e3r, -1.0);
		
		DefaultWeightedEdge e4 = graph.addEdge(3, 0);
		graph.setEdgeWeight(e4, 1.0);
		
		DefaultWeightedEdge e4r = graph.addEdge(0, 3);
		graph.setEdgeWeight(e4r, -1.0);
	}
	
	public void findJohnsonCycles() {
		JohnsonSimpleCycles johnsonCycles = new JohnsonSimpleCycles(graph);
		List cycles = johnsonCycles.findSimpleCycles();
		System.out.println("Johnson cycles:");
		System.out.println(cycles);
		System.out.println();
	}
	
	public void findSzwarcfiterLauerCycles() {
		SzwarcfiterLauerSimpleCycles slCycles = new SzwarcfiterLauerSimpleCycles(graph);
		List cycles = slCycles.findSimpleCycles();
		System.out.println("Szwarcfiter Lauer cycles:");
		System.out.println(cycles);
	}
	
	public void print() {
		System.out.println(graph.toString());
		System.out.println();
	}
}
