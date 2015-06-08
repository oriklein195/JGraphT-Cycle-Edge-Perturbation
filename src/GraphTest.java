import static org.junit.Assert.*;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;


public class GraphTest {

	@Test
	public void testConstructor() {
		Graph graph = new Graph("simple-square.txt");
		graph.printPerturbedGraph();
	}
	
	@Test
	public void testSimpleSquareJohnsonCycle() {
		Graph graph = new Graph("simple-square.txt");
		graph.findJohnsonCycles();
	}
	
	@Test
	public void testSzwarcfiterLauerSimpleCycles() {
		Graph graph = new Graph("simple-square.txt");
		graph.findSzwarcfiterLauerCycles();
	}
	
	@Test
	public void testSquareClique() {
		Graph graph = new Graph("square-clique.txt");
		graph.printOriginalGraph();
		graph.perturbEdges();
	}
	
	@Test
	public void testRandomPerfectCliqueConstructorSize4() {
		Graph graph = new Graph(4);
		graph.perturbEdges();
	}
	
	@Test
	public void testRandomPerfectCliqueConstructorSize5() {
		Graph graph = new Graph(5);
		graph.perturbEdges();
	}
	
	// 174548332364311563 cycles in a 20-clique (cycles with lengths 3 or greater)
	// 190 edges
	@Test
	public void testFindCyclesOfClique20() {
		Graph graph = new Graph(20);
		System.out.println("created graph");
		graph.perturbEdges();
	}
	
	// 556014 cycles in a 10-clique
	// 45 edges
	@Test
	public void testFindCyclesOfClique10() {
		Graph graph = new Graph(10);
		graph.perturbEdges();
	}
	
	@Test
	public void test7Nodes9Edges() {
		Graph graph = new Graph("7-nodes-9-edges.txt");
		graph.printOriginalGraph();
		graph.perturbEdges();
	}
	
	@Test
	public void testCreateGraph() {
		Graph graph = new Graph("s1.txt");
		graph.printOriginalGraph();
		graph.perturbEdges();
	}
	
<<<<<<< Updated upstream
	@Test
	public void testSzwarcfiterLauerCyclesOfData() {
		Graph graph = new Graph("s1.txt");
		graph.findSzwarcfiterLauerCycles();
=======
	// graph is not connected. Now we want to know how many connected components there are?
	@Test
	public void testIsConnected() {
		Graph graph = new Graph("s1.txt");
		System.out.println(graph.isConnected());
	}
	
	// There are 6 connected components.
	@Test
	public void testGetConnectedComponents() {
		Graph graph = new Graph("s1.txt");
		graph.getConnectedComponents();
	}
	
	@Test
	public void testVisualizeGraph() {
		Graph graph = new Graph("7-nodes-9-edges.txt");
		graph.visualizeGraph();
>>>>>>> Stashed changes
	}
	

}
