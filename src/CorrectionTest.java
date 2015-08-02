import static org.junit.Assert.*;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;


public class CorrectionTest {

	@Test
	public void testCorrectionSCC2() {
		Graph graph = new Graph("scc2.txt");
		Cycles c = new Cycles(graph);
		c.printIntegerToEdgeMap();
		List<BitSet> cycles = c.getCycles(4);
		Map<Integer, CustomWeightedEdge> integerToEdgeMap = c.getIntegerToEdgeMap();
		Correction.correctEdges(graph.getGraph(), cycles, integerToEdgeMap);
	}
	
	@Test
	public void testCorrectionSCC3() {
		Graph graph = new Graph("scc3.txt");
		Cycles c = new Cycles(graph);
		List<BitSet> cycles = c.getCycles(100);
		Map<Integer, CustomWeightedEdge> integerToEdgeMap = c.getIntegerToEdgeMap();
		Correction.correctEdges(graph.getGraph(), cycles, integerToEdgeMap);
	}
	
	@Test
	public void testRandomPerfectGraph8Nodes() {
		Graph graph = new Graph(8);
		graph.saveGraphAsText();
		graph.perturbEdges();
		Cycles c = new Cycles(graph);
		List<BitSet> cycles = c.getCycles(25);
		c.printIntegerToEdgeMap();
		c.saveCyclesMatrixAsText(cycles);
	}
	
	// recreating test case from above
	@Test
	public void testImportedPerfectGraph8Nodes() {
		Graph graph = new Graph("RandomPerfect8Nodes.txt");
		System.out.println("perfect edges");
		graph.printEdges();
		graph.perturbEdges();
		System.out.println("perturbed edges");
		graph.printEdges();
		Cycles c = new Cycles(graph);
		List<BitSet> cycles = c.getCycles(30);
		Map<Integer, CustomWeightedEdge> integerToEdgeMap = c.getIntegerToEdgeMap();
		Correction.correctEdges(graph.getGraph(), cycles, integerToEdgeMap);
		graph.getPercentChangeOriginalToPerturbed(); // percent change between original graph and perturbed graph (how much error was induced)
		graph.getPercentChangedPerturbedToCorrected(); // percent change between perturbed graph and corrected graph
		graph.getPercentChangedCorrectedToOriginal();
		graph.printEdges();
	}
	
	@Test
	public void testRandomPerfectGraph() {
		// graph needs to print out a .txt file such as the scc3.txt file, prints out all the edges
		// need to print out the perturbed graph's integerToEdgeMap
		// need to print out the perturbed graph's cycles matrix
		Graph graph = new Graph(100); // only works for sizes greater than 4
		graph.saveGraphAsText();
		System.out.println("perfect edges");
		graph.printEdges();
		graph.perturbEdges();
		System.out.println("perturbed edges");
		graph.printEdges();
		Cycles c = new Cycles(graph);
		List<BitSet> cycles = c.getCycles(200);
		c.saveCyclesMatrixAsText(cycles);
		/*Map<Integer, CustomWeightedEdge> integerToEdgeMap = c.getIntegerToEdgeMap();
		Correction.correctEdges(graph.getGraph(), cycles, integerToEdgeMap);
		graph.getPercentChangeOriginalToPerturbed(); // percent change between original graph and perturbed graph (how much error was induced)
		graph.getPercentChangedPerturbedToCorrected(); // percent change between perturbed graph and corrected graph
		graph.getPercentChangedCorrectedToOriginal(); // percent change between original graph and corrected graph */
	}
	
	// recreating test case from above
	@Test
	public void testImportedPerfectGraph100Nodes() {
		Graph graph = new Graph("RandomPerfect100Nodes.txt");
		/*System.out.println("perfect edges");
		graph.printEdges();*/
		graph.perturbEdges();
		System.out.println("perturbed edges");
		//graph.printEdges();
		graph.saveGraphAsText();
		/*Cycles c = new Cycles(graph);
		List<BitSet> cycles = c.getCycles(1000);
		c.saveCyclesMatrixAsText(cycles);
		Map<Integer, CustomWeightedEdge> integerToEdgeMap = c.getIntegerToEdgeMap();
		Correction.correctEdges(graph.getGraph(), cycles, integerToEdgeMap);
		graph.getPercentChangeOriginalToPerturbed(); // percent change between original graph and perturbed graph (how much error was induced)
		graph.getPercentChangedPerturbedToCorrected(); // percent change between perturbed graph and corrected graph
		graph.getPercentChangedCorrectedToOriginal();
		graph.printEdges();*/
	}
	
	@Test
	public void testSimulatedAnnealing() {
		//Graph graph = new Graph("RandomPerfect100Nodes.txt");
		//graph.perturbEdges();
		Graph graph = new Graph("Random100NodesPerturbed.txt");
		Cycles c = new Cycles(graph);
		List<BitSet> cycles = c.getCycles(100);
		Map<Integer, CustomWeightedEdge> integerToEdgeMap = c.getIntegerToEdgeMap();
		Map<CustomWeightedEdge, Integer> edgeToIntegerMap = c.getEdgeToIntegerMap();
		Correction.simulatedAnnealing(graph.getGraph(), cycles, integerToEdgeMap, edgeToIntegerMap);
		//graph.getPercentChangeOriginalToPerturbed(); // percent change between original graph and perturbed graph (how much error was induced)
		//graph.getPercentChangedPerturbedToCorrected(); // percent change between perturbed graph and corrected graph
		//graph.getPercentChangedCorrectedToOriginal();
		
		//graph.printIntegerToEdgeMap(integerToEdgeMap);
	}
	
	@Test
	public void testGetTemperature() {
		// getTemperature(int iteration, double initialTemp, double lambda)
		for (int i = 0; i < 10; i++) {
			// getProbability(double temperature, double delta)
			boolean probability = Correction.getProbability(i, 1.0);
		}
	}
	

}
