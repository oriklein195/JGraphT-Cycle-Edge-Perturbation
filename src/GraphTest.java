import static org.junit.Assert.*;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;


public class GraphTest {

	@Test
	public void testConstructor() {
		Graph graph = new Graph("simple-square.txt");
		graph.print();
	}
	
	@Test
	public void testSimpleSquareJohnsonCycle() {
		Graph graph = new Graph("simple-square.txt");
		graph.findJohnsonCycles();
	}
	
	@Test
	public void testSquareCliqueJohnsonCycle() {
		Graph graph = new Graph("square-clique.txt");
		graph.print();
		graph.perturbEdges();
	}

}
