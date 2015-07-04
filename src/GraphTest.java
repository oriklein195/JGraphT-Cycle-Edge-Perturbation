import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;


public class GraphTest {

	@Test
	public void testConstructor() {
		Graph graph = new Graph("simple-square.txt");
	}

	// graph is not connected. Now we want to know how many connected components there are?
	@Test
	public void testIsConnected() {
		Graph graph = new Graph("s1.txt");
		System.out.println(graph.isConnected());
	}
	
	// There are 6 connected components.
	@Test
	public void testGetConnectedComponents() {
		Graph graph = new Graph("scc6.txt");
		graph.getConnectedComponents();
	}
	
	@Test
	public void testCCInformation() {
		Graph graph = new Graph("s1.txt");
		graph.findSzwarcfiterLauerCycles();
	}
	
	@Test
	public void testPrintVertexDegreesSCC4() {
		Graph graph = new Graph("scc4.txt");
		graph.printVertexDegrees();
	}
	
	@Test
	public void testPrintVertexDegreesSCC5() {
		Graph graph = new Graph("scc5.txt");
		graph.printVertexDegrees();
	}
	
	@Test
	public void testPrintVertexDegreesSCC6() {
		Graph graph = new Graph("scc6.txt");
		graph.printVertexDegrees();
	}
	
	@Test
	public void testPrintVertexDegreesHistogramSCC3() {
		Graph graph = new Graph("scc3.txt");
		graph.printVertexDegreesHistogram();
	}
	
	@Test
	public void testPrintVertexDegreesHistogramSCC4() {
		Graph graph = new Graph("scc4.txt");
		graph.printVertexDegreesHistogram();
	}
	
	@Test
	public void testPrintVertexDegreesHistogramSCC5() {
		Graph graph = new Graph("scc5.txt");
		graph.printVertexDegreesHistogram();
	}
	
	@Test
	public void testPrintVertexDegreesHistogramSCC6() {
		Graph graph = new Graph("scc6.txt");
		graph.printVertexDegreesHistogram();
	}
	
	@Test
	public void testFindBridgesSCC1() {
		Graph graph = new Graph("scc1.txt");
		graph.findBridges();
	}
	
	@Test
	public void testFindBridgesSCC2() {
		Graph graph = new Graph("scc2.txt");
		graph.findBridges();
	}
	
	@Test
	public void testFindBridgesSCC3() {
		Graph graph = new Graph("scc3.txt");
		graph.findBridges();
	}
	
	@Test
	public void testFindBridgesSCC4() {
		Graph graph = new Graph("scc4.txt");
		graph.findBridges();
	}
	
	@Test
	public void testFindBridgesSCC5() {
		Graph graph = new Graph("scc5.txt");
		graph.findBridges();
	}
	
	@Test
	public void testFindBridgesSCC6() {
		Graph graph = new Graph("scc6.txt");
		graph.findBridges();
	}
	
	@Test
	public void testRandomPerfectGraph() {
		Graph graph = new Graph(5); // only works for sizes greater than 4
	}
	
	@Test
	public void testPerturbEdges() {
		Graph graph = new Graph(5);
		graph.perturbEdges();
	}

}
