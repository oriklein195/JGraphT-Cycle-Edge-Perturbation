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

}
