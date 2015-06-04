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
	public void testSquareCliqueJohnsonCycle() {
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

}
